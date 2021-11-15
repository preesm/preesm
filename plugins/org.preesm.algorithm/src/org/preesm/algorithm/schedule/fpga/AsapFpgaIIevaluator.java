package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorPrecomputed;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays.CycleInfos;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
import org.preesm.commons.IntegerName;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.TimingType;

/**
 * Class to determine the normalized throughput of each actor.
 * 
 * @author ahonorat
 */
public class AsapFpgaIIevaluator {

  protected static class ActorNormalizedInfos {
    protected final AbstractActor aa;
    protected final AbstractActor ori;
    protected final long          brv;
    protected final long          oriET;
    protected final long          oriII;
    protected final long          normGraphII;
    protected long                cycledII;

    protected ActorNormalizedInfos(final AbstractActor aa, final AbstractActor ori, final long oriET, final long oriII,
        final long brv) {
      this.aa = aa;
      this.ori = ori;
      this.oriET = oriET;
      this.oriII = oriII;
      this.brv = brv;
      this.normGraphII = brv * oriII;
      this.cycledII = 0L;
    }
  }

  protected static class ActorScheduleInfos {
    // number of firings in this interval
    protected long nbFirings = 0;
    // minimum duration of all firings
    protected long minDuration = 0;
    // real start time, refined afterwards
    protected long startTime = 0;
    // minimum possible according to each incoming dependency
    protected List<Long> minInStartTimes = new ArrayList<>();
    // real finish time > startTime + min duration
    protected long finishTime = 0;
    // minimum possible according to each incoming dependency
    protected List<Long> minInFinishTimes = new ArrayList<>();
  }

  private AsapFpgaIIevaluator() {
    // forbid instantiation
  }

  /**
   * Analyze the graph, schedule it with ASAP, and compute buffer sizes.
   * 
   * @param flatGraph
   *          Graph to analyse.
   * @param scenario
   *          Scenario to get the timings and mapping constraints.
   * @param brv
   *          Repetition vector of actors in cc.
   * @return StatGenerator for Gantt Data and map of all fifo sizes.
   */
  public static Pair<IStatGenerator, Map<Fifo, Long>> performAnalysis(final PiGraph flatGraph, final Scenario scenario,
      final Map<AbstractVertex, Long> brv) {

    // Get all sub graph (connected components) composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = PiMMHelper.getAllConnectedComponentsWOInterfaces(flatGraph);

    final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos = new LinkedHashMap<>();
    // check and set the II for each subgraph
    for (List<AbstractActor> cc : subgraphsWOInterfaces) {
      mapActorNormalizedInfos.putAll(checkAndSetActorInfos(cc, scenario, brv));
    }

    // check the cycles
    final HeuristicLoopBreakingDelays hlbd = new HeuristicLoopBreakingDelays();
    hlbd.performAnalysis(flatGraph, brv);

    final AbstractFifoEvaluator fifoEval = new FifoEvaluatorAsArray(scenario, hlbd, mapActorNormalizedInfos);
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
      }
    }
    // reuse the finish time of sources as their start time
    long maxFinishTime = 0;
    for (final AbstractActor src : hlbd.allSourceActors) {
      final ActorScheduleInfos asiT = mapActorSchedInfosT.get(src);
      maxFinishTime = Math.max(maxFinishTime, asiT.finishTime);
    }
    for (final AbstractActor src : hlbd.allSourceActors) {
      final ActorScheduleInfos asi = mapActorSchedInfos.get(src);
      final ActorScheduleInfos asiT = mapActorSchedInfosT.get(src);
      asi.startTime = maxFinishTime - asiT.finishTime;
      asi.finishTime = asi.startTime + asi.minDuration;
      PreesmLogger.getLogger().fine(() -> "ALAP reset start/finish time of " + src.getVertexPath() + " to: "
          + asi.startTime + "/" + asi.finishTime);
    }

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

    final IStatGenerator statGen = buildStatGenerator(scenario, irRankActors, sumFifoSizes, mapActorSchedInfos,
        mapActorNormalizedInfos);
    return new Pair<>(statGen, allFifoSizes);
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
      mapActorSchedInfos.put(aa, asi);
      mapActorSchedInfosT.put(aa, asiT);
    }
    return new Pair<>(mapActorSchedInfos, mapActorSchedInfosT);
  }

  private static long computeAndPrintSingleAbstractFifoSizes(final AbstractFifoEvaluator fifoEval,
      final ActorScheduleInfos prod, final FifoAbstraction fa, final ActorScheduleInfos cons,
      final Map<Fifo, Long> allFifoSizes, final StringBuilder fifoSizesPrint) {
    final List<Long> fifoSizes = fifoEval.computeFifoSizes(prod, fa, cons);
    long sumFifoSizes = 0L;
    for (int j = 0; j < fifoSizes.size(); j++) {
      final long fifoSize = fifoSizes.get(j);
      sumFifoSizes += fifoSize;
      final Fifo fifo = fa.fifos.get(j);
      allFifoSizes.put(fifo, fifoSize);
      fifoSizesPrint.append(fifo.getId() + " of size " + fifoSize + " bytes.\n");
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
        gd.insertTask(actorName, componentName, asi.startTime, asi.finishTime, tcs.doSwitch(aa));

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

  /**
   * Computes the normalized infos about actors II.
   * 
   * @param cc
   *          Actors in the connected component, except interfaces.
   * @param scenario
   *          Scenario to get the timings and mapping constraints.
   * @param brv
   *          Repetition vector of actors in cc.
   * @return List of actor infos, sorted by decreasing normGraphII.
   */
  private static Map<AbstractActor, ActorNormalizedInfos> checkAndSetActorInfos(final List<AbstractActor> cc,
      final Scenario scenario, final Map<AbstractVertex, Long> brv) {
    final ComponentInstance fpga = scenario.getDesign().getComponentInstances().get(0);
    final Map<AbstractActor, ActorNormalizedInfos> mapInfos = new LinkedHashMap<>();
    // check and set standard infos
    for (final AbstractActor aa : cc) {
      AbstractActor ori = PreesmCopyTracker.getOriginalSource(aa);
      if (!scenario.getPossibleMappings(ori).contains(fpga)) {
        throw new PreesmRuntimeException("Actor " + ori.getVertexPath() + " is not mapped to the only fpga.");
      }
      // check mapping
      final long ii = scenario.getTimings().evaluateTimingOrDefault(ori, fpga.getComponent(),
          TimingType.INITIATION_INTERVAL);
      final long et = scenario.getTimings().evaluateTimingOrDefault(ori, fpga.getComponent(),
          TimingType.EXECUTION_TIME);
      final long rv = brv.get(aa);
      final ActorNormalizedInfos ani = new ActorNormalizedInfos(aa, ori, et, ii, rv);
      mapInfos.put(aa, ani);
    }

    final List<ActorNormalizedInfos> listInfos = new ArrayList<>(mapInfos.values());
    Collections.sort(listInfos, new DecreasingGraphIIComparator());
    // set each avg II
    final ActorNormalizedInfos slowestActorInfos = listInfos.get(0);
    final long slowestGraphII = slowestActorInfos.normGraphII;
    if (listInfos.size() > 1) {
      final ActorNormalizedInfos fastestActorInfos = listInfos.get(listInfos.size() - 1);
      PreesmLogger.getLogger()
          .info(() -> "Throughput of your application is limited by the actor " + slowestActorInfos.ori.getVertexPath()
              + " with graph II=" + slowestGraphII + " whereas fastest actor " + fastestActorInfos.ori.getVertexPath()
              + " has its graph II=" + fastestActorInfos.normGraphII);

    }
    return mapInfos;
  }

  public static class DecreasingGraphIIComparator implements Comparator<ActorNormalizedInfos> {

    @Override
    public int compare(ActorNormalizedInfos arg0, ActorNormalizedInfos arg1) {
      return Long.compare(arg1.normGraphII, arg0.normGraphII);
    }

  }

}
