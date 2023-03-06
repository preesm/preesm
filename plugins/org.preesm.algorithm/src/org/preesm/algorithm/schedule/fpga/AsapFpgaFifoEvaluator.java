package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.algorithm.mapper.gantt.TaskColorSelector;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorPrecomputed;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays.CycleInfos;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
import org.preesm.commons.IntegerName;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * Class to evaluate buffer sizes thanks to an ASAP strategy.
 * 
 * @author ahonorat
 */
public class AsapFpgaFifoEvaluator extends AbstractGenericFpgaFifoEvaluator {

  public static final String FIFO_EVALUATOR_SDF = "sdfFifoEval";
  public static final String FIFO_EVALUATOR_AVG = "avgFifoEval";

  protected static class ActorScheduleInfos {
    // number of firings in this interval
    protected long nbFirings = 0L;
    // minimum duration of all firings
    protected long minDuration = 0L;
    // real start time, refined afterwards
    protected long startTime = 0L;
    // minimum possible according to each incoming dependency
    protected List<Long> minInStartTimes = new ArrayList<>();
    // real finish time > startTime + min duration
    protected long finishTime = 0L;
    // minimum possible according to each incoming dependency
    protected List<Long> minInFinishTimes = new ArrayList<>();
  }

  protected final String fifoEvaluatorName;

  protected AsapFpgaFifoEvaluator() {
    this(FIFO_EVALUATOR_AVG);
  }

  protected AsapFpgaFifoEvaluator(final String fifoEvaluatorName) {
    super();
    this.fifoEvaluatorName = fifoEvaluatorName;
  }

  @Override
  public void performAnalysis(final Scenario scenario, final AnalysisResultFPGA analysisResult) {

    final Map<AbstractActor,
        ActorNormalizedInfos> mapActorNormalizedInfos = logCheckAndSetActorNormalizedInfos(scenario, analysisResult);

    // check the cycles
    final HeuristicLoopBreakingDelays hlbd = new HeuristicLoopBreakingDelays();
    hlbd.performAnalysis(analysisResult.flatGraph, analysisResult.flatBrv);

    AbstractAsapFpgaFifoEvaluator fifoEval;
    if (FIFO_EVALUATOR_SDF.equalsIgnoreCase(fifoEvaluatorName)) {
      fifoEval = new FifoEvaluatorAsArray(scenario, hlbd, mapActorNormalizedInfos);
    } else if (FIFO_EVALUATOR_AVG.equalsIgnoreCase(fifoEvaluatorName)) {
      fifoEval = new FifoEvaluatorAsAverage(scenario, hlbd, mapActorNormalizedInfos);
    } else {
      throw new PreesmRuntimeException("Could not recognize fifo evaluator name: " + fifoEvaluatorName);
    }

    // set min durations of all AsapFpgaIIevaluator.ActorScheduleInfos, with cycle latency if in a cycle
    for (Entry<List<AbstractActor>, CycleInfos> e : hlbd.cyclesInfos.entrySet()) {
      final long cycleLatency = fifoEval.computeCycleMinII(e.getKey(), e.getValue());
      PreesmLogger.getLogger()
          .info(() -> "Cycle starting from " + e.getKey().get(0).getVertexPath() + " has its II >= " + cycleLatency);
      for (final AbstractActor aa : e.getKey()) {
        final ActorNormalizedInfos ani = mapActorNormalizedInfos.get(aa);
        ani.cycledII = Math.max(ani.cycledII, (cycleLatency * e.getValue().repetition) / ani.brv);
      }
    }

    final Map<AbstractActor, TopoVisit> topoRanks = TopologicalRanking.topologicalASAPranking(hlbd);
    final Map<AbstractActor, TopoVisit> topoRanksT = TopologicalRanking.topologicalASAPrankingT(hlbd);
    // build intermediate list of actors per rank to perform scheduling analysis
    final SortedMap<Integer, Set<AbstractActor>> irRankActors = TopologicalRanking.mapRankActors(topoRanks, false, 0);
    final SortedMap<Integer, Set<AbstractActor>> irRankActorsT = TopologicalRanking.mapRankActors(topoRanksT, false, 0);
    // build maps of sched infos
    final Pair<Map<AbstractActor, ActorScheduleInfos>, Map<AbstractActor,
        ActorScheduleInfos>> mapsOfActorSchedInfos = computeMapsOfActorSchedInfos(mapActorNormalizedInfos);
    final Map<AbstractActor, ActorScheduleInfos> mapActorSchedInfos = mapsOfActorSchedInfos.getKey();
    final Map<AbstractActor, ActorScheduleInfos> mapActorSchedInfosT = mapsOfActorSchedInfos.getValue();

    // ASAP in reverse order (sort of ALAP)
    final int minRank = irRankActors.firstKey();
    final int maxRank = irRankActors.lastKey();
    for (int i = minRank + 1; i <= maxRank; i++) {
      for (final AbstractActor aa : irRankActorsT.get(i)) {
        final ActorScheduleInfos reversedProd = mapActorSchedInfosT.get(aa);
        for (final FifoAbstraction fa : hlbd.getAbsGraph().outgoingEdgesOf(aa)) {
          if (!hlbd.breakingFifosAbs.contains(fa)) {
            final AbstractActor opposite = hlbd.getAbsGraph().getEdgeTarget(fa);
            final ActorScheduleInfos reversedCons = mapActorSchedInfosT.get(opposite);
            fifoEval.computeMinStartFinishTimeCons(reversedCons, fa, reversedProd, true);
          }
        }
        final ActorScheduleInfos asiT = mapActorSchedInfosT.get(aa);
        PreesmLogger.getLogger().fine(
            () -> "ALAP start/finish time of " + aa.getVertexPath() + ": " + asiT.startTime + "/" + asiT.finishTime);
      }
    }
    // get the latest finish time
    final long maxFinishTime = mapActorSchedInfosT.values().stream().map(x -> x.finishTime).max(Long::compare)
        .orElse(0L);
    // reuse the finish time of sources as their start time and vice versa
    for (final AbstractActor src : hlbd.allSourceActors) {
      final ActorScheduleInfos asi = mapActorSchedInfos.get(src);
      final ActorScheduleInfos asiT = mapActorSchedInfosT.get(src);
      asi.startTime = maxFinishTime - asiT.finishTime;
      // TODO other strategy for the finish time?
      asi.finishTime = maxFinishTime - asiT.startTime; // asi.startTime + asi.minDuration;
      PreesmLogger.getLogger().fine(() -> "ALAP reset start/finish time of " + src.getVertexPath() + " to: "
          + asi.startTime + "/" + asi.finishTime);
    }
    // reset the temporary in start/finish times
    resetAllInTimes(mapActorSchedInfos.values());

    // ASAP after which we can have FIFO sizes
    long sumFifoSizes = 0L;
    final Map<Fifo, Long> allFifoSizes = new LinkedHashMap<>();
    final StringBuilder fifoSizesPrint = new StringBuilder("Sizes of fifos:\n");

    for (int i = minRank + 1; i <= maxRank; i++) {
      for (final AbstractActor aa : irRankActors.get(i)) {
        final ActorScheduleInfos cons = mapActorSchedInfos.get(aa);
        for (final FifoAbstraction fa : hlbd.getAbsGraph().incomingEdgesOf(aa)) {
          if (!hlbd.breakingFifosAbs.contains(fa)) {
            final AbstractActor opposite = hlbd.getAbsGraph().getEdgeSource(fa);
            final ActorScheduleInfos prod = mapActorSchedInfos.get(opposite);
            fifoEval.computeMinStartFinishTimeCons(prod, fa, cons, false);
          }
        }
        PreesmLogger.getLogger().fine(
            () -> "Actor " + aa.getVertexPath() + " starts/finishes at " + cons.startTime + "/" + cons.finishTime);
        // then compute the sizes
        for (final FifoAbstraction fa : hlbd.getAbsGraph().incomingEdgesOf(aa)) {
          if (!hlbd.breakingFifosAbs.contains(fa)) {
            final AbstractActor opposite = hlbd.getAbsGraph().getEdgeSource(fa);
            final ActorScheduleInfos prod = mapActorSchedInfos.get(opposite);
            sumFifoSizes += computeAndPrintSingleAbstractFifoSizes(fifoEval, prod, fa, cons, allFifoSizes,
                fifoSizesPrint);
          }
        }
      }
    }
    // at the very end, we can evaluate the size of breaking fifo
    for (final FifoAbstraction fa : hlbd.breakingFifosAbs) {
      final AbstractActor srcA = hlbd.getAbsGraph().getEdgeSource(fa);
      final ActorScheduleInfos prod = mapActorSchedInfos.get(srcA);
      final AbstractActor tgtA = hlbd.getAbsGraph().getEdgeTarget(fa);
      final ActorScheduleInfos cons = mapActorSchedInfos.get(tgtA);
      sumFifoSizes += computeAndPrintSingleAbstractFifoSizes(fifoEval, prod, fa, cons, allFifoSizes, fifoSizesPrint);
    }

    PreesmLogger.getLogger().info(fifoSizesPrint::toString);
    // store the results before returning
    analysisResult.irRankActors = irRankActors;
    analysisResult.flatFifoSizes = allFifoSizes;
    analysisResult.statGenerator = buildStatGenerator(scenario, irRankActors, sumFifoSizes, mapActorSchedInfos,
        mapActorNormalizedInfos);
  }

  private static void resetAllInTimes(final Collection<ActorScheduleInfos> asis) {
    asis.forEach(x -> {
      x.minInStartTimes.clear();
      x.minInFinishTimes.clear();
    });
  }

  private static Pair<Map<AbstractActor, ActorScheduleInfos>, Map<AbstractActor, ActorScheduleInfos>>
      computeMapsOfActorSchedInfos(final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {
    final Map<AbstractActor, ActorScheduleInfos> mapActorSchedInfos = new LinkedHashMap<>();
    final Map<AbstractActor, ActorScheduleInfos> mapActorSchedInfosT = new LinkedHashMap<>();
    for (final Entry<AbstractActor, ActorNormalizedInfos> e : mapActorNormalizedInfos.entrySet()) {
      final AbstractActor aa = e.getKey();
      final ActorNormalizedInfos ani = e.getValue();
      final ActorScheduleInfos asi = new ActorScheduleInfos();
      final ActorScheduleInfos asiT = new ActorScheduleInfos();
      final long minDuration = (ani.brv - 1) * Math.max(ani.oriII, ani.cycledII) + ani.oriET;
      asi.minDuration = minDuration;
      asiT.minDuration = minDuration;
      asi.finishTime = minDuration;
      asiT.finishTime = minDuration;
      asi.nbFirings = ani.brv;
      asiT.nbFirings = ani.brv;
      mapActorSchedInfos.put(aa, asi);
      mapActorSchedInfosT.put(aa, asiT);
    }
    return new Pair<>(mapActorSchedInfos, mapActorSchedInfosT);
  }

  private static long computeAndPrintSingleAbstractFifoSizes(final AbstractAsapFpgaFifoEvaluator fifoEval,
      final ActorScheduleInfos prod, final FifoAbstraction fa, final ActorScheduleInfos cons,
      final Map<Fifo, Long> allFifoSizes, final StringBuilder fifoSizesPrint) {
    final List<Long> fifoSizes = fifoEval.computeFifoSizes(prod, fa, cons);
    long sumFifoSizes = 0L;
    for (int j = 0; j < fifoSizes.size(); j++) {
      final long fifoSize = fifoSizes.get(j);
      sumFifoSizes += fifoSize;
      final Fifo fifo = fa.fifos.get(j);
      allFifoSizes.put(fifo, fifoSize);
      fifoSizesPrint.append(fifo.getId() + " of size " + fifoSize + " bits.\n");
    }
    return sumFifoSizes;
  }

  private static StatGeneratorPrecomputed buildStatGenerator(final Scenario scenario,
      final SortedMap<Integer, Set<AbstractActor>> irRankActors, final long totSize,
      final Map<AbstractActor, ActorScheduleInfos> mapActorSchedInfos,
      final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {

    final TaskColorSelector tcs = new TaskColorSelector();
    // get the only FPGA component
    final ComponentInstance fpga = scenario.getDesign().getComponentInstances().get(0);
    final Map<ComponentInstance, Long> mems = new HashMap<>();
    mems.put(fpga, totSize);
    final Map<ComponentInstance, Long> loads = new HashMap<>();

    // fill GanttData in topological order
    long sumMinDuration = 0L;
    double sumActorPipelinedRatio = 0L;
    long maxRealGraphII = 0L;
    long maxOriGraphII = 0L;
    long topoIndex = 0L;
    long maxFinishTime = 0L;
    final IntegerName intFormatter = new IntegerName(mapActorSchedInfos.size());
    final GanttData gd = new GanttData();
    for (final Set<AbstractActor> aas : irRankActors.values()) {
      for (final AbstractActor aa : aas) {
        topoIndex++;
        // here we cheat a bit: we create one component per actor
        final String componentName = fpga.getInstanceName() + "_" + intFormatter.toString(topoIndex);
        final ActorScheduleInfos asi = mapActorSchedInfos.get(aa);
        sumMinDuration += asi.minDuration;
        final long time = asi.finishTime - asi.startTime;
        sumActorPipelinedRatio += (asi.minDuration * (double) asi.minDuration) / time;
        final ActorNormalizedInfos ani = mapActorNormalizedInfos.get(aa);
        // the real max graphII is the longest actor time minus its (ET - II)
        maxRealGraphII = Math.max(maxRealGraphII, time - ani.oriET + ani.oriII);
        // the original one is the normalized one
        maxOriGraphII = Math.max(maxOriGraphII, ani.normGraphII);
        final String suffixName = getPipelinedCategory(time, asi.minDuration, ani.brv * ani.oriET);
        final String actorName = suffixName + " " + ani.brv + "x " + aa.getVertexPath();
        gd.insertTask(actorName, componentName, asi.startTime, time, tcs.doSwitch(aa));

        if (asi.finishTime > maxFinishTime) {
          maxFinishTime = asi.finishTime;
        }

      }
    }

    loads.put(fpga, maxRealGraphII * (long) (sumActorPipelinedRatio / sumMinDuration));
    // here we cheat again: we tell the statGenerator that our number of used components is the latency
    final long latency = (maxFinishTime + maxRealGraphII - 1) / maxRealGraphII;

    return new StatGeneratorPrecomputed(scenario.getDesign(), scenario, maxOriGraphII, sumMinDuration, maxRealGraphII,
        (int) latency, loads, mems, gd);
  }

  private static String getPipelinedCategory(final long actorTime, final long actorPipelinedTime,
      final long actorSequentialTime) {
    if (actorTime > actorSequentialTime) {
      // not pipelined
      return "[- - -]";
    } else if (actorTime > actorPipelinedTime) {
      // not fully pipelined
      return "[-|-|-]";
    } else {
      // fully pipelined
      return "[-----]";
    }
  }

}
