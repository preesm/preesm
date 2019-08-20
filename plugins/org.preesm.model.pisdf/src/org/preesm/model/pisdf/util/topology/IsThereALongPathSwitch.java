package org.preesm.model.pisdf.util.topology;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 *         A long path is defined as a path that encounters more than one Fifo
 *
 */
public class IsThereALongPathSwitch extends PiSDFSuccessorSwitch {

  private final AbstractActor potentialSucc;
  private long                fifoCount;

  /**
   * @author dgageot
   */
  static class ThereIsALongPath extends RuntimeException {
    private static final long serialVersionUID = 8123950588521527792L;
  }

  public IsThereALongPathSwitch(final AbstractActor potentialSucc) {
    this.potentialSucc = potentialSucc;
    this.fifoCount = 0;
  }

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    // If we reached the target
    if (actor.equals(this.potentialSucc)) {
      if (this.fifoCount > 1) {
        throw new ThereIsALongPath();
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
