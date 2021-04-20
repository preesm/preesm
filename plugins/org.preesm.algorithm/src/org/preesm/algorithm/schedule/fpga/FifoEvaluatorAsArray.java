package org.preesm.algorithm.schedule.fpga;

import java.util.Map;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.schedule.fpga.AsapFpgaIIevaluator.ActorNormalizedInfos;
import org.preesm.model.pisdf.AbstractActor;

/**
 * This class evalutes fifo dependencies and size as in the SDF model: all data are produced at the end while they are
 * consumed at the beginning of a firing.
 * 
 * @author ahonorat
 */
public class FifoEvaluatorAsArray extends AbstractFifoEvaluator {

  public FifoEvaluatorAsArray(final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos,
      final HeuristicLoopBreakingDelays hlbd) {
    super(mapActorNormalizedInfos, hlbd);
  }

}
