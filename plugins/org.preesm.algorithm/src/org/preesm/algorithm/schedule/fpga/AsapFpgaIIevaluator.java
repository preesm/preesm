package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays.CycleInfos;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
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
    protected long                avgII;

    protected ActorNormalizedInfos(final AbstractActor aa, final AbstractActor ori, final long oriET, final long oriII,
        final long brv) {
      this.aa = aa;
      this.ori = ori;
      this.oriET = oriET;
      this.oriII = oriII;
      this.brv = brv;
      this.normGraphII = brv * oriII;
    }
  }

  protected static class ActorScheduleInfos {
    protected long nbFirings    = 0; // number of firings in this interval
    protected long startTime    = 0; // real start time, refined afterwards
    protected long minStartTime = 0; // minimum possible ensuring all dependencies
    protected long minDuration  = 0; // minimum duration of all firings
    protected long finishTime   = 0; // real finish time > startTime + min duration
  }

  final PiGraph                   flatGraph;
  final Scenario                  scenario;
  final Map<AbstractVertex, Long> brv;

  /**
   * Builds an evaluator of the ASAP schedule on FPGA.
   * 
   * @param flatGraph
   *          Graph to analyse.
   * @param scenario
   *          Scenario to get the timings and mapping constraints.
   * @param brv
   *          Repetition vector of actors in cc.
   */
  public AsapFpgaIIevaluator(final PiGraph flatGraph, final Scenario scenario, final Map<AbstractVertex, Long> brv) {
    this.flatGraph = flatGraph;
    this.scenario = scenario;
    this.brv = brv;
  }

  /**
   * Analyze the graph, schedule it, and compute buffer sizes.
   */
  public void performAnalysis() {
    // Get all sub graph (connected components) composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = PiMMHelper.getAllConnectedComponentsWOInterfaces(flatGraph);

    Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos = new LinkedHashMap<>();
    // check and set the II for each subgraph
    for (List<AbstractActor> cc : subgraphsWOInterfaces) {
      mapActorNormalizedInfos.putAll(checkAndSetActorInfos(cc, scenario, brv));
    }

    // check the cycles
    final HeuristicLoopBreakingDelays hlbd = new HeuristicLoopBreakingDelays();
    hlbd.performAnalysis(flatGraph, brv);

    final AbstractFifoEvaluator fifoEval = new FifoEvaluatorAsArray(mapActorNormalizedInfos, hlbd);
    // TODO set min durations of all AsapFpgaIIevaluator.ActorScheduleInfos, with cycle latency if in a cycle
    for (Entry<List<AbstractActor>, CycleInfos> e : hlbd.cyclesInfos.entrySet()) {
      fifoEval.computeCycleMinII(e.getKey(), e.getValue());
    }

    final Map<AbstractActor, TopoVisit> topoRanks = TopologicalRanking.topologicalASAPranking(hlbd);
    // build intermediate list of actors per rank to perform scheduling analysis
    final SortedMap<Integer, Set<AbstractActor>> irRankActors = TopologicalRanking.mapRankActors(topoRanks, false, 0);

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
  public static Map<AbstractActor, ActorNormalizedInfos> checkAndSetActorInfos(final List<AbstractActor> cc,
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
    for (final ActorNormalizedInfos ani : listInfos) {
      ani.avgII = slowestGraphII / ani.brv;
    }
    if (listInfos.size() > 1) {
      final ActorNormalizedInfos fastestActorInfos = listInfos.get(listInfos.size() - 1);
      PreesmLogger.getLogger()
          .info("Throughput of your application is limited by the actor " + slowestActorInfos.ori.getVertexPath()
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
