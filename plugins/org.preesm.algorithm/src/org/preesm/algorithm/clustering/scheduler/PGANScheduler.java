package org.preesm.algorithm.clustering.scheduler;

import java.util.Map;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * PGAN scheduler used to schedule actors contained in a cluster.
 *
 * @author dgageot
 */
public class PGANScheduler {

  /**
   * Input graph to schedule.
   */
  private final PiGraph inputGraph;
  /**
   * Copy of input graph.
   */
  private final PiGraph copiedGraph;

  /**
   * Repetition vector of actual processed graph.
   */
  private Map<AbstractVertex, Long> repetitionVector;

  /**
   * Builds a PGAN scheduler.
   * 
   * @param inputGraph
   *          Input graph to schedule.
   */
  public PGANScheduler(final PiGraph inputGraph) {
    // Save references
    this.inputGraph = inputGraph;
    // Copy input graph into copiedGraph
    this.copiedGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(this.inputGraph);
    // Check that input graph is actually clusterizable
    isClusterizable();
  }

  /**
   * Create a schedule for the specified cluster.
   * 
   * @return Created schedule.
   */
  public Schedule schedule() {

    // TODO: Compute BRV from a subgraph
    this.repetitionVector = PiBRV.compute(this.inputGraph, BRVMethod.LCM);

    // Clusterize while creating schedule

    return null;
  }

  private final void isClusterizable() {
    // Check for uncompatible delay (with getter/setter)
    for (Fifo fifo : inputGraph.getFifosWithDelay()) {
      Delay delay = fifo.getDelay();

      // If delay has getter/setter, throw an exception
      if (delay.getActor().getDataInputPort().getIncomingFifo() != null
          || delay.getActor().getDataOutputPort().getOutgoingFifo() != null) {
        throw new PreesmRuntimeException(
            "PGANScheduler: getter/setter are not handled on [" + delay.getActor().getName() + "]");
      }
    }
  }

}
