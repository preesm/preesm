package org.preesm.model.pisdf.switches;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 *
 */
public class PiSDFPredecessorSwitch extends PiMMSwitch<Boolean> {

  private final List<AbstractActor> visitedElements = new ArrayList<>();

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    if (!visitedElements.contains(actor)) {
      visitedElements.add(actor);
      actor.getDataInputPorts().forEach(this::doSwitch);
      final EList<AbstractActor> delayActors = actor.getContainingPiGraph().getDelayActors();
      delayActors.forEach(this::doSwitch);
    }
    return true;
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    final DataOutputPort sourcePort = fifo.getSourcePort();
    doSwitch(sourcePort);
    return true;
  }

  @Override
  public Boolean caseDataOutputPort(DataOutputPort dop) {
    final AbstractActor containingActor = dop.getContainingActor();
    doSwitch(containingActor);
    return true;
  }

  @Override
  public Boolean caseDataInputPort(DataInputPort dip) {
    final Fifo fifo = dip.getFifo();
    if (fifo != null) {
      doSwitch(fifo);
    }
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(DataInputInterface inputInterface) {
    caseAbstractActor(inputInterface);
    final Port graphPort = inputInterface.getGraphPort();
    doSwitch(graphPort);
    return true;
  }

  @Override
  public Boolean casePiGraph(PiGraph graph) {
    // only visit children if did not start here
    if (!visitedElements.isEmpty()) {
      graph.getActors().forEach(this::doSwitch);
    }
    caseAbstractActor(graph);
    return true;
  }

  @Override
  public Boolean caseDelayActor(DelayActor delayActor) {
    caseAbstractActor(delayActor);
    final PiGraph graph = delayActor.getContainingPiGraph();
    graph.getDataInputInterfaces().forEach(this::doSwitch);
    return true;
  }

  /**
   * returns true if potentialPred is actually a predecessor of target
   */
  public static final boolean isPredecessor(final AbstractActor potentialPred, final AbstractActor target) {
    try {
      new IsPredecessorSwitch(target).doSwitch(potentialPred);
      return false;
    } catch (final PredecessorFoundException e) {
      return true;
    }
  }

  /**
   *
   * @author anmorvan
   *
   */
  private static class PredecessorFoundException extends RuntimeException {
    private static final long serialVersionUID = -566281860304717658L;
  }

  /**
   *
   * @author anmorvan
   *
   */
  private static class IsPredecessorSwitch extends PiSDFPredecessorSwitch {
    private AbstractActor potentialPred;

    public IsPredecessorSwitch(final AbstractActor potentialPred) {
      this.potentialPred = potentialPred;
    }

    @Override
    public Boolean caseAbstractActor(final AbstractActor actor) {
      if (actor.equals(potentialPred)) {
        throw new PredecessorFoundException();
      }
      return super.caseAbstractActor(actor);
    }

  }

}
