/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021 - 2023)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2024)
 * Mickael Dardaillon [mickael.dardaillon@insa-rennes.fr] (2022 - 2024)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
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
   * Wraps the results in a single object. /!\ No mechanism ensures the correct filling of this data structure, should
   * be checked by the developper for now.
   *
   * @author ahonorat
   */
  public static class AnalysisResultFPGA {
    // given flattened graph
    public final PiGraph flatGraph;
    // given repetition vector of the flat graph
    public final Map<AbstractVertex, Long> flatBrv;
    // given interface rates (repetition factor + rate)
    public final Map<InterfaceActor, Pair<Long, Long>> interfaceRates;
    // computed graphII, i.e. slowest actor normalized II
    public Long graphII = null;
    // optionally computed irRanks Asap
    public SortedMap<Integer, Set<AbstractActor>> irRankActors = null;
    // computed fifo sizes
    public Map<Fifo, Long> flatFifoSizes = null;
    // computed stats for UI or other
    public IStatGenerator statGenerator = null;

    public AnalysisResultFPGA(final PiGraph flatGraph, final Map<AbstractVertex, Long> flatBrv,
        final Map<InterfaceActor, Pair<Long, Long>> interfaceRates) {
      this.flatGraph = flatGraph;
      this.flatBrv = flatBrv;
      this.interfaceRates = interfaceRates;
    }
  }

  /**
   * Analyze the graph, schedule it with ASAP, and compute buffer sizes.
   *
   * @param scenario
   *          Scenario to get the timings and mapping constraints.
   * @param analysisResult
   *          Container storing the flat graph and its repetition vector, to be updated with all available results (at
   *          least fifo sizes in bit).
   */
  public abstract void performAnalysis(final Scenario scenario, final AnalysisResultFPGA analysisResult);

  /**
   * Builds the corresponding evaluator object.
   *
   * @param fifoEvaluatorName
   *          String representing the evaluator to be used (for scheduling and fifo sizing).
   * @return Instance of the correct evaluator.
   */
  public static AbstractGenericFpgaFifoEvaluator getEvaluatorInstance(final String fifoEvaluatorName) {

    return switch (fifoEvaluatorName) {
      case AsapFpgaFifoEvaluator.FIFO_EVALUATOR_SDF, AsapFpgaFifoEvaluator.FIFO_EVALUATOR_AVG ->
        new AsapFpgaFifoEvaluator(fifoEvaluatorName);
      case AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_EXACT -> new AdfgOjalgoFpgaFifoEvaluator(true);
      case AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_LINEAR -> new AdfgOjalgoFpgaFifoEvaluator(false);
      default -> throw new PreesmRuntimeException("Could not recognize fifo evaluator name: " + fifoEvaluatorName);
    };
  }

  /**
   * Computes the normalized infos about actors II.
   *
   * @param scenario
   *          Scenario to get the timings and mapping constraints.
   * @param analysisResult
   *          Container of the graph to analyze and its brv. Its graphII attribute will be updated.
   * @return Map of actor infos.
   */
  public static Map<AbstractActor, ActorNormalizedInfos> logCheckAndSetActorNormalizedInfos(final Scenario scenario,
      final AnalysisResultFPGA analysisResult) {

    // Get all sub graph (connected components) composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = PiMMHelper
        .getAllConnectedComponentsWOInterfaces(analysisResult.flatGraph);

    final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos = new LinkedHashMap<>();
    // check and set the II for each subgraph
    for (final List<AbstractActor> cc : subgraphsWOInterfaces) {
      mapActorNormalizedInfos.putAll(checkAndSetActorNormalizedInfosInCC(cc, scenario, analysisResult.flatBrv));
    }

    final List<ActorNormalizedInfos> listInfos = new ArrayList<>(mapActorNormalizedInfos.values());
    Collections.sort(listInfos, new DecreasingGraphIIComparator());
    // set graph II
    final ActorNormalizedInfos slowestActorInfos = listInfos.get(0);
    final long slowestGraphII = slowestActorInfos.normGraphII;
    analysisResult.graphII = Long.valueOf(slowestGraphII);
    // log graph II
    if (listInfos.size() > 1) {
      final ActorNormalizedInfos fastestActorInfos = listInfos.get(listInfos.size() - 1);
      PreesmLogger.getLogger()
          .info(() -> "Throughput of your application is limited by the actor " + slowestActorInfos.ori.getVertexPath()
              + " with graph II=" + slowestGraphII + " whereas fastest actor " + fastestActorInfos.ori.getVertexPath()
              + " has its graph II=" + fastestActorInfos.normGraphII);

    }
    return mapActorNormalizedInfos;
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
   * @return Map of actor infos.
   */
  protected static Map<AbstractActor, ActorNormalizedInfos> checkAndSetActorNormalizedInfosInCC(
      final List<AbstractActor> cc, final Scenario scenario, final Map<AbstractVertex, Long> brv) {

    final ComponentInstance fpga = scenario.getDesign().getComponentInstances().get(0);
    final Map<AbstractActor, ActorNormalizedInfos> mapInfos = new LinkedHashMap<>();
    // check and set standard infos
    for (final AbstractActor aa : cc) {
      final AbstractActor ori = PreesmCopyTracker.getOriginalSource(aa);
      // check mapping
      if (!scenario.getPossibleMappings(ori).contains(fpga)) {
        throw new PreesmRuntimeException("Actor " + ori.getVertexPath() + " is not mapped to the only fpga.");
      }

      final long maxRate = getActorMaximumRate(aa);
      long ii;
      long et;

      // TODO checker si l'acteur à un temps dans le scénario
      // sinon tester si specialActor
      // sinon exception
      // if ((scenario.getTimings().evaluateTiming(ori, fpga.getComponent(),
      // TimingType.INITIATION_INTERVAL) == ScenarioConstants.DEFAULT_MISSING_TIMING.getValue()
      // || scenario.getTimings().evaluateTiming(ori, fpga.getComponent(),
      // TimingType.EXECUTION_TIME) == ScenarioConstants.DEFAULT_MISSING_TIMING.getValue())
      // && !(aa instanceof UserSpecialActor)) {
      // throw new PreesmRuntimeException("wtf");
      // }

      // If the actor ISN'T a special actor
      if (!(aa instanceof UserSpecialActor)) {
        if (scenario.getTimings().evaluateTiming(ori, fpga.getComponent(),
            TimingType.INITIATION_INTERVAL) == ScenarioConstants.DEFAULT_MISSING_TIMING.getValue()) {
          throw new PreesmRuntimeException(String
              .format("Actor %s does not have a valid Initiation Interval in the Scenario.", ori.getVertexPath()));
        }
        if (scenario.getTimings().evaluateTiming(ori, fpga.getComponent(),
            TimingType.EXECUTION_TIME) == ScenarioConstants.DEFAULT_MISSING_TIMING.getValue()) {
          throw new PreesmRuntimeException(
              String.format("Actor %s does not have a valid Execution Time in the Scenario.", ori.getVertexPath()));
        }
      }

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

    return mapInfos;
  }

  private static long getActorMaximumRate(final AbstractActor aa) {
    long maxRate = 0L;
    for (final DataPort dp : aa.getAllDataPorts()) {
      final long rate = dp.getExpression().evaluateAsLong();
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
