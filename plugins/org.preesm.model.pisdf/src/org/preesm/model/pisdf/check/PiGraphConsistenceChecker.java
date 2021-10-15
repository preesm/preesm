/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.model.pisdf.check;

import com.google.common.base.Strings;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import org.preesm.commons.graph.Graph;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.pisdf.util.DependencyCycleDetector;

/**
 * Important note to devs (by ahonorat): this class is called in different contexts. Sometimes we want to check the
 * whole graph even if we have already detected some errors. So DO NOT USE {@link Stream#allMatch} here because then we
 * would not check the other faulty elements, but prefer an hand-made reduction ensuring a complete evaluation.
 * Similarly, DO NOT USE lazy boolean evaluation as {@code &&} but prefer force boolean evaluation with {@code &=}.
 * 
 */
public class PiGraphConsistenceChecker extends AbstractPiSDFObjectChecker {

  private final Deque<PiGraph> graphStack;

  /**
   * Builds the checker without logging messages nor stopping on errors. Then the user has to call the
   * {@link #check(PiGraph)} method.
   * 
   */
  public PiGraphConsistenceChecker() {
    this(CheckerErrorLevel.NONE, CheckerErrorLevel.NONE);
  }

  /**
   * Builds the checker, then the user has to call the {@link #check(PiGraph)} method.
   * 
   * @param throwExceptionLevel
   *          The maximum level of error throwing exceptions.
   * @param loggerLevel
   *          The maximum level of error generating logs.
   */
  public PiGraphConsistenceChecker(final CheckerErrorLevel throwExceptionLevel, final CheckerErrorLevel loggerLevel) {
    super(throwExceptionLevel, loggerLevel);
    this.graphStack = new ArrayDeque<>();
  }

  /**
   * Check the whole graph, throwing exception for every warning but not logging them.
   * 
   * @param graph
   *          The PiSDF graph to check.
   * @return Whether or not the PiSDF graph is consistent.
   */
  public final Boolean check(final PiGraph graph) {
    final boolean graphIsConsistent = doSwitch(graph);
    final boolean hierarchyIsConsistent = graphStack.isEmpty();
    graphStack.clear();
    return graphIsConsistent && hierarchyIsConsistent;
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    this.graphStack.push(graph);
    // visit children & references
    boolean graphValid = graph.getUrl() != null;
    if (!graphValid) {
      // for an unknown reason, this cannot be a fatal error, otherwise it is triggered at each saving operation
      reportError(CheckerErrorLevel.FATAL_ANALYSIS, graph, "Graph [%s] has null URL.", graph.getVertexPath());
    }

    graphValid &= caseAbstractActor(graph);

    final BinaryOperator<Boolean> reductor = (x, y) -> (x && y);
    graphValid &= graph.getDependencies().stream().map(this::doSwitch).reduce(true, reductor);

    final DependencyCycleDetector dcd = new DependencyCycleDetector();
    dcd.doSwitch(graph);
    graphValid &= !dcd.cyclesDetected();
    if (dcd.cyclesDetected()) {
      reportError(CheckerErrorLevel.FATAL_ANALYSIS, graph, "Graph [%s] has cyclic dependencies between its parameters.",
          graph.getVertexPath());
    }

    graphValid &= graph.getActors().stream().map(this::doSwitch).reduce(true, reductor);

    final RefinementChecker refinementChecker = new RefinementChecker(throwExceptionLevel, loggerLevel);
    graphValid &= refinementChecker.doSwitch(graph);
    mergeMessages(refinementChecker);

    graphValid &= graph.getFifos().stream().map(this::doSwitch).reduce(true, reductor);
    graphValid &= graph.getChildrenGraphs().stream().map(this::doSwitch).reduce(true, reductor);
    this.graphStack.pop();
    return graphValid;
  }

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    // check unicity of port names
    int nullPortCount = 0;
    final Map<String, Integer> allPortNameOccurrences = new HashMap<>();
    for (final Port p : actor.getAllPorts()) {
      final String key = Strings.nullToEmpty(p.getName());
      if (!key.isEmpty()) {
        final int count = allPortNameOccurrences.getOrDefault(key, 0);
        allPortNameOccurrences.put(key, count + 1);
      } else {
        nullPortCount++;
      }
    }

    final boolean redundantPorts = (actor.getAllPorts().size() + nullPortCount) != allPortNameOccurrences.size();
    boolean actorValid = !redundantPorts;
    if (redundantPorts) {
      for (final Entry<String, Integer> e : allPortNameOccurrences.entrySet()) {
        if (e.getValue() > 1) {
          reportError(CheckerErrorLevel.FATAL_ALL, actor, "Actor [%s] has several ports with same name [%s].",
              actor.getVertexPath(), e.getKey());
        }
      }
    }

    if (!(actor instanceof PiGraph)) {
      // visit children ports except for PiGraph since their ports will not be linked to anything if subgraph is
      // not already reconnected

      final BinaryOperator<Boolean> reductor = (x, y) -> (x && y);
      actorValid &= actor.getConfigInputPorts().stream().map(this::doSwitch).reduce(true, reductor);
      // note that fifo will also trigger a visit of their data ports, so we visit only the unconnected ones
      actorValid &= actor.getAllConnectedDataOutputPorts().stream().map(this::doSwitch).reduce(true, reductor);
      actorValid &= actor.getDataInputPorts().stream().filter(x -> x.getFifo() != null).map(this::doSwitch).reduce(true,
          reductor);
    }

    return actorValid;
  }

  @Override
  public Boolean caseUserSpecialActor(final UserSpecialActor actor) {
    boolean actorValid = caseAbstractActor(actor);

    final Set<String> typeSet = new LinkedHashSet<>();
    // for special actor we also have to check that all fifos connected to it have the same type
    actor.getAllConnectedDataOutputPorts().forEach(x -> typeSet.add(x.getFifo().getType()));
    actor.getDataInputPorts().stream().filter(x -> x.getFifo() != null)
        .forEach(x -> typeSet.add(x.getFifo().getType()));
    // the getAllConnectedDataOutputPorts method already filters the non null fifo
    if (typeSet.size() > 1) {
      actorValid = false;
      reportError(CheckerErrorLevel.FATAL_CODEGEN, actor, "Special actor [%s] is connected to fifos of multiple types.",
          actor.getVertexPath());
    }

    return actorValid;
  }

  @Override
  public Boolean caseInterfaceActor(final InterfaceActor actor) {
    final DataPort dataPort = actor.getDataPort();
    final String portName = dataPort.getName();
    final String name = actor.getName();
    final boolean validity = name.equals(portName);
    if (!validity) {
      final String message = "The interface data port [%s] should have the same name as its containing actor '%s'.";
      reportError(CheckerErrorLevel.FATAL_ALL, actor, message, portName, actor.getVertexPath());
    }
    return validity;
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort cfgInPort) {
    final PiGraph peek = this.graphStack.peek();
    boolean containedInProperGraph = true;
    final Configurable cfg = cfgInPort.getConfigurable();
    if (!(cfg instanceof PiGraph)) {
      // if a PiGraph then we would be at top level or in a non reconnected PiGraph
      containedInProperGraph = cfg.getContainingPiGraph() == peek;
      if (!containedInProperGraph) {
        reportError(CheckerErrorLevel.FATAL_ALL, cfgInPort, "Config input port [%s] is not contained in graph.",
            cfgInPort.getName());
      }
    }
    final Dependency incomingDependency = cfgInPort.getIncomingDependency();
    final boolean portConnected = incomingDependency != null;
    boolean depInGraph = true;
    if (!portConnected) {
      reportError(CheckerErrorLevel.FATAL_ANALYSIS, cfgInPort,
          "Config input port [%s] is not connected to a dependency.", cfgInPort.getName());
    } else {
      final Graph containingGraph = incomingDependency.getContainingGraph();
      depInGraph = containingGraph == peek;
    }
    return depInGraph && portConnected && containedInProperGraph;
  }

  @Override
  public Boolean caseConfigOutputPort(final ConfigOutputPort object) {
    // no check
    return true;
  }

  @Override
  public Boolean caseDataPort(final DataPort port) {
    final PiGraph peek = this.graphStack.peek();
    final AbstractActor containingActor = port.getContainingActor();
    final boolean isContained = containingActor != null;
    final String portName = port.getName();
    final String actorName = isContained ? containingActor.getName() : "unknown";
    boolean wellContained = true;
    if (!isContained) {
      reportError(CheckerErrorLevel.FATAL_ALL, port, "Port [%s] has not containing actor.", portName);
    } else if (!(containingActor instanceof PiGraph)) {
      // if a PiGraph then we would be at top level or in a non reconnected PiGraph
      final PiGraph containingPiGraph = containingActor.getContainingPiGraph();
      wellContained = (containingPiGraph == peek);
      if (!wellContained) {
        reportError(CheckerErrorLevel.FATAL_ALL, port,
            "Port [<%s>:%s] containing actor graph [%s] differs from peek graph [%s].", actorName, portName,
            containingPiGraph, peek);
      }
    }
    final Fifo fifo = port.getFifo();
    final boolean hasFifo = fifo != null;
    boolean fifoWellContained = true;
    if (!hasFifo) {
      reportError(CheckerErrorLevel.FATAL_ANALYSIS, port, "Port [<%s>:%s] is not connected to a fifo.", actorName,
          portName);
    } else if (!(containingActor instanceof PiGraph)) {
      // if a PiGraph then we would be at top level or in a non reconnected PiGraph
      final PiGraph containingPiGraph2 = fifo.getContainingPiGraph();
      fifoWellContained = (containingPiGraph2 == peek);
      if (!fifoWellContained) {
        reportError(CheckerErrorLevel.FATAL_ALL, port, "Port [<%s>:%s] fifo graph [%s] differs from peek graph [%s].",
            actorName, portName, containingPiGraph2, peek);
      }
    }
    return wellContained && fifoWellContained;
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    // check fifo
    final boolean sourcePortNotNull = fifo.getSourcePort() != null;
    final boolean targetPortNotNull = fifo.getTargetPort() != null;
    final boolean containedByGraph = this.graphStack.peek().getFifos().contains(fifo);
    boolean fifoValid = sourcePortNotNull && targetPortNotNull && containedByGraph;

    // Instantiate check result
    if (!fifoValid) {
      reportError(CheckerErrorLevel.FATAL_ALL, fifo, "Fifo [%s] is not valid.", fifo.getId());
    } else {
      final FifoChecker fifoChecker = new FifoChecker(throwExceptionLevel, loggerLevel);
      fifoValid = fifoChecker.doSwitch(fifo);
      mergeMessages(fifoChecker);
      fifoValid &= doSwitch(fifo.getSourcePort());
      fifoValid &= doSwitch(fifo.getTargetPort());

      if (fifo.getDelay() != null) {
        fifoValid &= doSwitch(fifo.getDelay());
      }
    }

    return fifoValid;
  }

  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
    final Delay linkedDelay = actor.getLinkedDelay();
    final boolean hasLinkedDelay = linkedDelay != null && linkedDelay.getActor() == actor;
    final boolean delayProperlyContained = actor.getContainingPiGraph().getVertices().contains(linkedDelay);

    final boolean delayActorValid = hasLinkedDelay && delayProperlyContained;
    if (!hasLinkedDelay) {
      reportError(CheckerErrorLevel.FATAL_ALL, actor, "DelayActor [%s] has no proper linked delay.",
          actor.getVertexPath());
    }
    if (!delayProperlyContained) {
      reportError(CheckerErrorLevel.FATAL_ALL, actor, "DelayActor [%s] has a delay not contained in the graph.",
          actor.getVertexPath());
    }
    return delayActorValid;
  }

  @Override
  public Boolean caseDelay(final Delay delay) {
    final DelayActor actor = delay.getActor();
    final boolean actorLinkedProperly = actor != null && actor.getLinkedDelay() == delay;
    final boolean delayActorProperlyContained = delay.getContainingPiGraph().getVertices().contains(actor);
    final boolean delayValid = actorLinkedProperly && delayActorProperlyContained;
    if (!actorLinkedProperly) {
      reportError(CheckerErrorLevel.FATAL_ALL, delay, "Delay [%s] is no proper linked actor.", delay.getName());
    }
    if (!delayActorProperlyContained) {
      reportError(CheckerErrorLevel.FATAL_ALL, delay, "Delay [%s] has an actor not contained in the graph.",
          delay.getName());
    }
    return delayValid;
  }

  @Override
  public Boolean caseDependency(final Dependency dependency) {
    final ISetter setter = dependency.getSetter();
    final boolean setterok = doSwitch(setter);

    final ConfigInputPort getter = dependency.getGetter();
    final Configurable containingConfigurable = getter.getConfigurable();

    final boolean getterContained = containingConfigurable != null;
    boolean properTarget = true;
    if (!getterContained) {
      reportError(CheckerErrorLevel.FATAL_ALL, dependency, "Dependency [%s] getter [%s] is not contained.", dependency,
          getter.getName());
    } else if (!(containingConfigurable instanceof PiGraph)) {
      // if a PiGraph then we would be at top level or in a non reconnected PiGraph
      final PiGraph peek = this.graphStack.peek();
      final PiGraph containingPiGraph = containingConfigurable.getContainingPiGraph();
      properTarget = containingPiGraph == peek;
      if (!properTarget) {
        reportError(CheckerErrorLevel.FATAL_ALL, dependency,
            "Dependency [%s] getter [%s] is contained in an actor that is not part of the graph.", dependency,
            getter.getName());
      }
    }
    return setterok && getterContained && properTarget;
  }

  @Override
  public Boolean caseParameter(final Parameter param) {
    final boolean containOK = param.getContainingPiGraph() == this.graphStack.peek();
    final boolean depsOk = param.getOutgoingDependencies().stream()
        .allMatch(d -> d.getContainingGraph() == this.graphStack.peek());
    final boolean parameterOK = containOK && depsOk;
    if (!parameterOK) {
      reportError(CheckerErrorLevel.FATAL_ALL, param, "Parameter [%s] is not contained by graph [%s].", param.getName(),
          graphStack.peek().getVertexPath());
    }
    return containOK && depsOk;
  }

}
