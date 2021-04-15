package org.preesm.algorithm.schedule.fpga;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays.CycleInfos;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;

/**
 * This class evalutes fifo dependencies and size as in the SDF model: all data are produced at the end while they are
 * consumed at the beginning of a firing.
 * 
 * @author ahonorat
 */
public class FifoEvaluatorAsArray extends AbstractFifoEvaluator {

  public FifoEvaluatorAsArray(final Map<AbstractVertex, Long> brv) {
    super(brv);
  }

  public long computeActorMinLatencyInCycle(final List<AbstractActor> cycleActors, final CycleInfos cycleInfos,
      final AbstractActor src) {
    final int indexSrc = cycleActors.indexOf(src);

    long previousNBF = 1L;
    long minLatency = Long.MAX_VALUE;
    // we iterate over the cycleFifos list, in reverse order from the index of src
    for (int i = indexSrc + cycleActors.size() - 1; i >= indexSrc; i--) {
      final int iFA = i % indexSrc; // cycleActors.size() == cycleFifos.size()
      final FifoAbstraction currentFifo = cycleInfos.fifosPerEdge.get(iFA);
      previousNBF = AbstractFifoEvaluator.nbfOpposite(currentFifo, previousNBF, true);
      final long delay = currentFifo.delays.stream().min(Long::compare).orElse(0L);

    }

    return 0L;
  }

}
