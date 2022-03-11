package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.TimingType;

/**
 * Generic class for Fpga Fifo Evaluators.
 * 
 * @author ahonorat
 */
public abstract class AbstractGenericFpgaFifoEvaluator {

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

  /**
   * Analyze the graph, schedule it with ASAP, and compute buffer sizes.
   * 
   * @param flatGraph
   *          Graph to analyze.
   * @param scenario
   *          Scenario to get the timings and mapping constraints.
   * @param brv
   *          Repetition vector of actors in cc.
   * @return StatGenerator for Gantt Data and map of all fifo sizes in bit.
   */
  public abstract Pair<IStatGenerator, Map<Fifo, Long>> performAnalysis(final PiGraph flatGraph,
      final Scenario scenario, final Map<AbstractVertex, Long> brv);

  /**
   * Builds the corresponding evaluator object.
   * 
   * @param fifoEvaluatorName
   *          String representing the evaluator to be used (for scheduling and fifo sizing).
   * @return Instance of the correct evaluator.
   */
  public static AbstractGenericFpgaFifoEvaluator getEvaluatorInstance(final String fifoEvaluatorName) {
    if (AsapFpgaFifoEvaluator.FIFO_EVALUATOR_SDF.equalsIgnoreCase(fifoEvaluatorName)
        || AsapFpgaFifoEvaluator.FIFO_EVALUATOR_AVG.equalsIgnoreCase(fifoEvaluatorName)) {
      return new AsapFpgaFifoEvaluator(fifoEvaluatorName);
    } else if (AdfgFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG.equalsIgnoreCase(fifoEvaluatorName)) {
      return new AdfgFpgaFifoEvaluator();
    }
    throw new PreesmRuntimeException("Could not recognize fifo evaluator name: " + fifoEvaluatorName);
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
  public static Map<AbstractActor, ActorNormalizedInfos> checkAndSetActorNormalizedInfos(final List<AbstractActor> cc,
      final Scenario scenario, final Map<AbstractVertex, Long> brv) {
    final ComponentInstance fpga = scenario.getDesign().getComponentInstances().get(0);
    final Map<AbstractActor, ActorNormalizedInfos> mapInfos = new LinkedHashMap<>();
    // check and set standard infos
    for (final AbstractActor aa : cc) {
      AbstractActor ori = PreesmCopyTracker.getOriginalSource(aa);
      // check mapping
      if (!scenario.getPossibleMappings(ori).contains(fpga)) {
        throw new PreesmRuntimeException("Actor " + ori.getVertexPath() + " is not mapped to the only fpga.");
      }

      final long maxRate = getActorMaximumRate(aa);
      long ii;
      long et;

      if (aa instanceof UserSpecialActor) {
        // set timings
        ii = maxRate;
        et = ii;
      } else {
        // check timings
        ii = scenario.getTimings().evaluateTimingOrDefault(ori, fpga.getComponent(), TimingType.INITIATION_INTERVAL);
        et = scenario.getTimings().evaluateTimingOrDefault(ori, fpga.getComponent(), TimingType.EXECUTION_TIME);
        if (et < ii) {
          throw new PreesmRuntimeException(
              String.format("Actor %s has its execution time (%d) strictly lower than its initiation interval (%d).",
                  ori.getVertexPath(), et, ii));
        }
      }

      if (maxRate > ii) {
        throw new PreesmRuntimeException(String.format(
            "Actor %s has its maximal production/consumption (%d) strictly greater than its initiation interval (%d).",
            ori.getVertexPath(), maxRate, ii));

      }
      // store infos
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

  private static long getActorMaximumRate(final AbstractActor aa) {
    long maxRate = 0L;
    for (final DataPort dp : aa.getAllDataPorts()) {
      final long rate = dp.getExpression().evaluate();
      if (rate > maxRate) {
        maxRate = rate;
      }
    }
    return maxRate;
  }

  public static class DecreasingGraphIIComparator implements Comparator<ActorNormalizedInfos> {

    @Override
    public int compare(ActorNormalizedInfos arg0, ActorNormalizedInfos arg1) {
      return Long.compare(arg1.normGraphII, arg0.normGraphII);
    }

  }

}
