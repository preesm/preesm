package org.preesm.model.pisdf.util;

import com.google.common.base.Strings;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;

/**
 *
 */
public class PiGraphConsistenceChecker extends PiMMSwitch<Boolean> {

  /**
   *
   */
  public static final void check(final PiGraph graph) {
    check(graph, true);
  }

  /**
   *
   */
  public static final Boolean check(final PiGraph graph, final boolean throwExceptions) {
    final PiGraphConsistenceChecker piGraphConsistenceChecker = new PiGraphConsistenceChecker(throwExceptions);
    final boolean graphIsConsistent = piGraphConsistenceChecker.doSwitch(graph);
    final boolean hierarchyIsConsistent = piGraphConsistenceChecker.graphStack.isEmpty();
    return graphIsConsistent && hierarchyIsConsistent;
  }

  private final Deque<PiGraph> graphStack;
  private final boolean        throwExceptions;
  private final List<String>   messages;

  private PiGraphConsistenceChecker(final boolean throwExceptions) {
    this.throwExceptions = throwExceptions;
    this.graphStack = new ArrayDeque<>();
    this.messages = new ArrayList<>();
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    graphStack.push(graph);
    // visit children & references
    boolean graphValid = graph.getActors().stream().allMatch(this::doSwitch);
    graphValid = graphValid && graph.getFifos().stream().allMatch(this::doSwitch);
    graphValid = graphValid && graph.getDependencies().stream().allMatch(this::doSwitch);
    graphValid = graphValid && graph.getChildrenGraphs().stream().allMatch(this::doSwitch);
    graphStack.pop();
    return graphValid;
  }

  private void error(String messageFormat, Object... args) {
    final String msg = String.format(messageFormat, args);
    if (throwExceptions) {
      throw new PreesmException(msg);
    } else {
      messages.add(msg);
    }
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    // check fifo
    final boolean sourcePortNotNull = fifo.getSourcePort() != null;
    final boolean targetPortNotNull = fifo.getTargetPort() != null;
    final boolean containedByGraph = this.graphStack.peek().getFifos().contains(fifo);
    final boolean fifoValid = sourcePortNotNull && targetPortNotNull && containedByGraph;

    // Instantiate check result
    if (!fifoValid) {
      error("Fifo [%s] is not valid", fifo);
    }

    final Boolean doSwitch = doSwitch(fifo.getSourcePort());
    final Boolean doSwitch2 = doSwitch(fifo.getTargetPort());
    // visit children & references
    // TODO : delays
    return fifoValid && doSwitch && doSwitch2;
  }

  @Override
  public Boolean caseParameter(Parameter param) {
    boolean containOK = param.getContainingPiGraph() == graphStack.peek();
    boolean depsOk = param.getOutgoingDependencies().stream()
        .allMatch(d -> d.getContainingGraph() == graphStack.peek());
    return containOK && depsOk;
  }

  @Override
  public Boolean defaultCase(EObject object) {
    error("Object [%s] has not been assessed as valid.", object);
    return false;
  }

  @Override
  public Boolean caseDependency(final Dependency dependency) {
    final ISetter setter = dependency.getSetter();
    final boolean setterok = doSwitch(setter);

    final ConfigInputPort getter = dependency.getGetter();
    final Configurable containingConfigurable = getter.getConfigurable();

    final boolean getterContained = containingConfigurable != null;
    if (!getterContained) {
      error("Dependency [%s] getter [%s] is not contained.", dependency, getter);
    }

    boolean properTarget = true;
    if (!(containingConfigurable instanceof PiGraph)) {
      properTarget = containingConfigurable.getContainingPiGraph() == graphStack.peek();
    }

    return setterok && properTarget;
  }

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    // check actor
    boolean actorValid = true;

    final List<Port> allPorts = actor.getAllPorts();
    final int nbPorts = allPorts.size();
    for (int i = 0; i < nbPorts - 1; i++) {
      for (int j = i + 1; j < nbPorts; j++) {
        final Port port1 = allPorts.get(i);
        final Port port2 = allPorts.get(j);
        final String name = port1.getName();
        final String name2 = port2.getName();
        actorValid = actorValid && !(Strings.nullToEmpty(name).equals(name2));

      }
    }

    // Instantiate check result
    if (!actorValid) {
      final String message = "Actor [" + actor + "] is not valid.";
      error(message);
    }

    if (!(actor instanceof DelayActor)) {
      // visit children & references
      actorValid = actorValid && actor.getAllDataPorts().stream().allMatch(this::doSwitch);
      actorValid = actorValid && actor.getConfigInputPorts().stream().allMatch(this::doSwitch);
      actorValid = actorValid && actor.getConfigOutputPorts().stream().allMatch(this::doSwitch);
    }
    return actorValid;
  }

  @Override
  public Boolean caseConfigInputPort(ConfigInputPort cfgInPort) {
    final boolean containedInProperGraph = cfgInPort.getConfigurable().getContainingPiGraph() == graphStack.peek();
    final boolean depInGraph = cfgInPort.getIncomingDependency().getContainingGraph() == graphStack.peek();
    return depInGraph && containedInProperGraph;
  }

  @Override
  public Boolean caseConfigOutputPort(ConfigOutputPort object) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public Boolean caseDataPort(final DataPort port) {
    final AbstractActor containingActor = port.getContainingActor();
    boolean isContained = containingActor != null;
    if (!isContained) {
      error("port [%s] has not containing actor", port);
    }
    final PiGraph containingPiGraph = containingActor.getContainingPiGraph();
    final PiGraph peek = graphStack.peek();
    boolean wellContained = isContained && containingPiGraph == peek;
    if (!wellContained) {
      error("port [%s] containing actor graph [%s] differs from peek graph [%s]", port, containingPiGraph, peek);
    }
    final Fifo fifo = port.getFifo();
    boolean hasFifo = fifo != null;
    if (!hasFifo) {
      error("port [%s] is not connected to a fifo", port);
    }
    final PiGraph containingPiGraph2 = fifo.getContainingPiGraph();
    boolean fifoWellContained = hasFifo && containingPiGraph2 == peek;
    if (!fifoWellContained) {
      error("port [%s] fifo graph [%s] differs from peek graph [%s]", port, containingPiGraph2, peek);
    }

    boolean portValid = wellContained && fifoWellContained;

    if (!portValid) {
      final String message = "Port [" + port + "] is not valid.";
      error(message);
    }

    return portValid;
  }

}
