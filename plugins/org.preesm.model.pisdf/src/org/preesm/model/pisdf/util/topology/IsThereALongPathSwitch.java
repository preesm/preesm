package org.preesm.model.pisdf.util.topology;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 *         Package visible helper switch that throws a {@link ThereIsALongPathException} when an actor equals to the
 *         local attribute 'potentialSucc' is encountered while visiting the successors of the subject of the switch and
 *         the path is strictly longer than 1. See
 *         {@link PiSDFTopologyHelper#isThereIsALongPath(AbstractActor, AbstractActor)} for usage.
 *
 *         A long path is defined as a path that encounters more than one Fifo.
 *
 */
class IsThereALongPathSwitch extends PiSDFSuccessorSwitch {

  private final AbstractActor potentialSucc;
  private long                fifoCount;

  /**
   * @author dgageot
   */
  static class ThereIsALongPathException extends RuntimeException {
    private static final long serialVersionUID = 8123950588521527792L;
  }

  IsThereALongPathSwitch(final AbstractActor potentialSucc) {
    this.potentialSucc = potentialSucc;
    this.fifoCount = 0;
  }

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    // If we reached the target
    if (actor.equals(this.potentialSucc)) {
      if (this.fifoCount > 1) {
        throw new ThereIsALongPathException();
      }
      return false;
    }

    if (!this.visitedElements.contains(actor)) {
      this.visitedElements.add(actor);
      long tempFifoCount = this.fifoCount;
      for (DataOutputPort dop : actor.getDataOutputPorts()) {
        this.fifoCount = tempFifoCount;
        doSwitch(dop);
      }
    }
    return true;
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    // If there is a delay on fifo, stop here
    if (fifo.getDelay() != null) {
      return true;
    }
    this.fifoCount = this.fifoCount + 1;
    return super.caseFifo(fifo);
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    return caseAbstractActor(graph);
  }
}
