package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays.CycleInfos;
import org.preesm.algorithm.schedule.fpga.AsapFpgaIIevaluator.ActorNormalizedInfos;
import org.preesm.algorithm.schedule.fpga.AsapFpgaIIevaluator.ActorScheduleInfos;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;

/**
 * This class is a base class for the evaluation of FIFO, i.e. computation of start and finish time of a producer
 * considering a consumer, and computation of the fifo size.
 * 
 * @author ahonorat
 */
public abstract class AbstractFifoEvaluator {

  final HeuristicLoopBreakingDelays              hlbd;
  final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos;

  public AbstractFifoEvaluator(final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos,
      final HeuristicLoopBreakingDelays hlbd) {
    this.mapActorNormalizedInfos = mapActorNormalizedInfos;
    this.hlbd = hlbd;
  }

  /**
   * Get the number of firings of the consumer triggered by executing {@code nbfiringsOpposite} last firings of the
   * producer, without considering delays.
   * 
   * @param fa
   *          Fifo abstraction to consider.
   * @param nbfiringsOpposite
   *          Number of firings of the opposite side.
   * @param reverse
   *          If true, reverse the fifo direction.
   * @param withDelay
   *          If true, consider the delays on the fifo.
   * @return Number of firings of the consumer, or producer if {@code reverse} is true.
   */
  public static final long nbfOpposite(final FifoAbstraction fa, final long nbfiringsOpposite, final boolean reverse,
      final boolean withDelay) {
    long delay = 0;
    if (withDelay) {
      delay += fa.delays.stream().min(Long::compare).orElse(0L);
    }
    long nbf;
    if (reverse) { // nbff
      nbf = (nbfiringsOpposite * fa.getConsRate() - delay + fa.getProdRate() - 1) / fa.getProdRate();
    } else { // nblf
      nbf = (nbfiringsOpposite * fa.getProdRate() - delay + fa.getConsRate() - 1) / fa.getConsRate();
    }
    return nbf;
  }

  /**
   * Compute the cycle minimal II for an entry of {@link HeuristicLoopBreakingDelays#cyclesInfos}.
   * 
   * @param cycleActors
   *          List of actors in the cycle.
   * @param cycleInfos
   *          Extra information about the cycle (especially the breaking fifo).
   * @return Minimal II of the cycle (i.e. time duration between two iterations of the cycle).
   */
  public long computeCycleMinII(final List<AbstractActor> cycleActors, final CycleInfos cycleInfos) {

    // 1. we break the cycle into multiple sub lists
    final FifoAbstraction breakingFifo = cycleInfos.breakingFifo;
    final AbstractActor src = hlbd.getAbsGraph().getEdgeTarget(breakingFifo);
    final int indexSrc = cycleActors.indexOf(src);

    // we will break the cycle in subcycles, created by internal delays
    final List<List<Pair<AbstractActor, ActorScheduleInfos>>> subcycles = new ArrayList<>();
    long previousNBF = 1L;
    long minCycleII = 0L;
    // we iterate over the cycleFifos list, in reverse order from the index of src
    for (int i = indexSrc + cycleActors.size() - 1; i >= indexSrc; i--) {
      final int iFA = i % cycleActors.size(); // cycleActors.size() == cycleFifos.size()
      final FifoAbstraction currentFifo = cycleInfos.fifosPerEdge.get(iFA);
      final long tempNBF = AbstractFifoEvaluator.nbfOpposite(currentFifo, previousNBF, true, true);
      if (tempNBF <= 0 || currentFifo == breakingFifo) {
        previousNBF = AbstractFifoEvaluator.nbfOpposite(currentFifo, previousNBF, true, false);
        // we create a new subcycle
        final List<Pair<AbstractActor, ActorScheduleInfos>> currentSubCycle = new LinkedList<>();
        subcycles.add(0, currentSubCycle);
        final ActorScheduleInfos cloneCurrentSourceSched = new ActorScheduleInfos();
        final AbstractActor currentSrc = hlbd.getAbsGraph().getEdgeTarget(currentFifo);
        currentSubCycle.add(new Pair<>(currentSrc, cloneCurrentSourceSched));
        cloneCurrentSourceSched.nbFirings = 1L;
      } else {
        previousNBF = tempNBF;
      }
      final ActorScheduleInfos currentSched = new ActorScheduleInfos();
      final AbstractActor aa = hlbd.getAbsGraph().getEdgeSource(currentFifo);
      PreesmLogger.getLogger()
          .fine("Cycle analysis visiting: " + aa.getVertexPath() + " fired " + previousNBF + " times");
      subcycles.get(0).add(0, new Pair<>(aa, currentSched));
      currentSched.nbFirings = previousNBF;
      // update minII
      final ActorNormalizedInfos ani = mapActorNormalizedInfos.get(aa);
      final long actorIIcycle = ani.oriII * (ani.brv / cycleInfos.repetition);
      minCycleII = Math.max(minCycleII, actorIIcycle);
    }

    final AbstractActor checkSource = subcycles.get(0).get(0).getKey();
    if (checkSource != src) {
      throw new PreesmRuntimeException("Error while analyzing cycle, not coming back on source ("
          + checkSource.getVertexPath() + " instead of " + src.getVertexPath() + ")");
    }

    // 2. we schedule the cycle sub lists
    for (final List<Pair<AbstractActor, ActorScheduleInfos>> subcycle : subcycles) {
      Iterator<Pair<AbstractActor, ActorScheduleInfos>> itActors = subcycle.iterator();
      Pair<AbstractActor, ActorScheduleInfos> previousPair = itActors.next();
      // schedule the actors
      while (itActors.hasNext()) {
        final Pair<AbstractActor, ActorScheduleInfos> p = itActors.next();
        final AbstractActor prodActor = previousPair.getKey();
        final AbstractActor consActor = p.getKey();
        final FifoAbstraction currentFifo = hlbd.getAbsGraph().getEdge(prodActor, consActor);
        // TODO
        // computeNextStartFinishTime(previousPair.getValue(), currentFifo, p.getValue());
        previousPair = p;
      }
      // retrieve the length
      final long subcycleLength = previousPair.getValue().startTime;
      // compute the maximum cycle II (not checking the sentinel)
      itActors = subcycle.iterator();
      // under approximation
      long minRVratio = Long.MAX_VALUE;
      for (int i = 0; i < subcycle.size() - 1; i++) {
        final Pair<AbstractActor, ActorScheduleInfos> p = itActors.next();
        final ActorNormalizedInfos ani = mapActorNormalizedInfos.get(p.getKey());
        final long actorRVratio = (ani.brv / cycleInfos.repetition) / p.getValue().nbFirings;
        minRVratio = Math.min(minRVratio, actorRVratio);
      }
      minCycleII = Math.max(minCycleII, minRVratio * subcycleLength);
    }

    return minCycleII;
  }

}
