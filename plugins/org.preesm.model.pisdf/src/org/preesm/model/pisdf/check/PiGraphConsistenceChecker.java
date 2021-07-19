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
import java.util.List;
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
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
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
  public Boolean caseInterfaceActor(final InterfaceActor actor) {
    final DataPort dataPort = actor.getDataPort();
    final String portName = dataPort.getName();
    final String name = actor.getName();
    if (!name.equals(portName)) {
      final String message = "The interface data port <%s> should have the same name as its containing actor '%s'.";
      reportError(CheckerErrorLevel.FATAL, actor, message, portName, actor.getVertexPath());
    }
    return super.caseInterfaceActor(actor);
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    this.graphStack.push(graph);
    // visit children & references
    boolean graphValid = graph.getUrl() != null;
    if (!graphValid) {
      // for an unknown reason, this cannot be a fatal error, otherwise it is triggered at each saving operation
      reportError(CheckerErrorLevel.RECOVERABLE, graph, "Graph [%s] has null URL.", graph.getVertexPath());
    }

    graphValid &= graph.getDependencies().stream().map(x -> doSwitch(x)).reduce(true, (x, y) -> x && y);

    final DependencyCycleDetector dcd = new DependencyCycleDetector();
    dcd.doSwitch(graph);
    graphValid &= !dcd.cyclesDetected();
    if (dcd.cyclesDetected()) {
      reportError(CheckerErrorLevel.RECOVERABLE, graph, "Graph [%s] has cyclic dependencies between its parameters.",
          graph);
    }

    graphValid &= graph.getActors().stream().map(x -> doSwitch(x)).reduce(true, (x, y) -> x && y);

    final RefinementChecker refinementChecker = new RefinementChecker(throwExceptionLevel, loggerLevel);
    graphValid &= refinementChecker.doSwitch(graph);
    mergeMessages(refinementChecker);

    graphValid &= graph.getFifos().stream().map(x -> doSwitch(x)).reduce(true, (x, y) -> x && y);
    graphValid &= graph.getChildrenGraphs().stream().map(x -> doSwitch(x)).reduce(true, (x, y) -> x && y);
    this.graphStack.pop();
    return graphValid;
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
      reportError(CheckerErrorLevel.FATAL, fifo, "Fifo [%s] is not valid.", fifo);
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
      reportError(CheckerErrorLevel.FATAL, actor, "DelayActor [%s] has no proper linked delay.", actor);
    }
    if (!delayProperlyContained) {
      reportError(CheckerErrorLevel.FATAL, actor, "DelayActor [%s] has a delay not contained in the graph.", actor);
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
      reportError(CheckerErrorLevel.FATAL, delay, "Delay [%s] is no proper linked actor.", delay);
    }
    if (!delayActorProperlyContained) {
      reportError(CheckerErrorLevel.FATAL, delay, "Delay [%s] has an actor not contained in the graph.", delay);
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
      reportError(CheckerErrorLevel.FATAL, dependency, "Dependency [%s] getter [%s] is not contained.", dependency,
          getter);
    } else {

      if (!(containingConfigurable instanceof PiGraph)) {
        final PiGraph peek = this.graphStack.peek();
        final PiGraph containingPiGraph = containingConfigurable.getContainingPiGraph();
        properTarget = containingPiGraph == peek;
        if (!properTarget) {
          reportError(CheckerErrorLevel.FATAL, dependency,
              "Dependency [%s] getter [%s] is contained in an actor that is not part of the graph.", dependency,
              getter);
        }
      }
    }
    return setterok && getterContained && properTarget;
  }

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    // check actor
    boolean actorValid = true;

    final List<Port> allPorts = actor.getAllPorts();
    final int nbPorts = allPorts.size();
    for (int i = 0; i < (nbPorts - 1); i++) {
      for (int j = i + 1; j < nbPorts; j++) {
        final Port port1 = allPorts.get(i);
        final Port port2 = allPorts.get(j);
        final String name = port1.getName();
        final String name2 = port2.getName();
        final boolean redundantPorts = (Strings.nullToEmpty(name).equals(name2));
        actorValid = actorValid && !redundantPorts;
        if (redundantPorts) {
          reportError(CheckerErrorLevel.FATAL, actor, "Actor [%s] has several ports with same name [%s].", actor, name);
        }
      }
    }

    if (!(actor instanceof DelayActor)) {
      // visit children & references
      actorValid &= actor.getAllDataPorts().stream().map(x -> doSwitch(x)).reduce(true, (x, y) -> x && y);
      actorValid &= actor.getConfigInputPorts().stream().map(x -> doSwitch(x)).reduce(true, (x, y) -> x && y);
      actorValid &= actor.getConfigOutputPorts().stream().map(x -> doSwitch(x)).reduce(true, (x, y) -> x && y);
    }

    return actorValid;
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort cfgInPort) {
    final PiGraph peek = this.graphStack.peek();
    final boolean containedInProperGraph = cfgInPort.getConfigurable().getContainingPiGraph() == peek;
    if (!containedInProperGraph) {
      reportError(CheckerErrorLevel.FATAL, cfgInPort, "Config input port [%s] is not contained in graph.", cfgInPort);
    }
    final Dependency incomingDependency = cfgInPort.getIncomingDependency();
    final boolean portConnected = incomingDependency != null;
    boolean depInGraph = true;
    if (!portConnected) {
      reportError(CheckerErrorLevel.FATAL, cfgInPort, "Config input port [%s] is not connected to a dependency.",
          cfgInPort);
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
      reportError(CheckerErrorLevel.FATAL, port, "Port [%s] has not containing actor.", portName);
    } else {
      final PiGraph containingPiGraph = containingActor.getContainingPiGraph();
      wellContained = (containingPiGraph == peek);
      if (!wellContained) {
        reportError(CheckerErrorLevel.FATAL, port,
            "Port [<%s>:%s] containing actor graph [%s] differs from peek graph [%s].", actorName, portName,
            containingPiGraph, peek);
      }
    }
    final Fifo fifo = port.getFifo();
    final boolean hasFifo = fifo != null;
    boolean fifoWellContained = true;
    if (!hasFifo) {
      reportError(CheckerErrorLevel.RECOVERABLE, port, "Port [<%s>:%s] is not connected to a fifo.", actorName,
          portName);
    } else {
      final PiGraph containingPiGraph2 = fifo.getContainingPiGraph();
      fifoWellContained = (containingPiGraph2 == peek);
      if (!fifoWellContained) {
        reportError(CheckerErrorLevel.FATAL, port, "Port [<%s>:%s] fifo graph [%s] differs from peek graph [%s].",
            actorName, portName, containingPiGraph2, peek);
      }
    }
    return wellContained && fifoWellContained;
  }

}
