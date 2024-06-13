/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.pisdf.autodelays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.mapper.ui.stats.EditorRunnable;
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.algorithm.mapper.ui.stats.StatEditorInput;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorSynthesis;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays.CycleInfos;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.evaluation.latency.SimpleLatencyEvaluation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PreesmSchedulingException;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.SimulationInfo;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class computes best locations for delays.
 * <p>
 * For more details, see conference paper "A Fast Heuristic to Pipeline SDF Graphs", published at SAMOS 2020 (DOI
 * 10.1007/978-3-030-60939-9_10).
 *
 * @author ahonorat
 *
 */
@PreesmTask(id = "pisdf-delays.setter", name = "Automatic Placement of Delays",
    shortDescription = "Puts delays in a flat PiMM, in order to speed up the execution.",

    description = "Puts delays in a flat PiMM, in order to speed up the execution. "
        + "Works only on homogeneous architectures. The heuristic will perform a search of all simple cycles,"
        + " so the task may take a long time to run if many cycles are present.",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class) },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class) },

    parameters = {
        @Parameter(name = AutoDelaysTask.SELEC_PARAM_NAME,
            description = "Number of graph cuts to consider, " + "higher or equal to the maximum number of cuts.",
            values = { @Value(name = AutoDelaysTask.SELEC_PARAM_VALUE,
                effect = "Split the graph in zones of equivalent work load.") }),
        @Parameter(name = AutoDelaysTask.MAXII_PARAM_NAME,
            description = "Maximum number of graph cuts induced by the added delays. "
                + "Each graph cut adds one pipeline stage. If delays are already present, the values are summed.",
            values = { @Value(name = AutoDelaysTask.MAXII_PARAM_VALUE, effect = "") }),
        @Parameter(name = AutoDelaysTask.CHOCO_PARAM_NAME,
            description = "Computes all topological graph cuts with a CP solver. "
                + "All topological cuts are evaluated with a list scheduler to select the best.",
            values = { @Value(name = AutoDelaysTask.CHOCO_PARAM_VALUE, effect = "False disables this comparison.") }),
        @Parameter(name = AutoDelaysTask.SCHED_PARAM_NAME,
            description = "Whether or not a schedule must be generated at the end.",
            values = { @Value(name = AutoDelaysTask.SCHED_PARAM_VALUE, effect = "False disables this feature.") }),
        @Parameter(name = AutoDelaysTask.CYCLES_PARAM_NAME,
            description = "Whether or not the cycles must be broken with extra delays.",
            values = { @Value(name = AutoDelaysTask.CYCLES_PARAM_VALUE, effect = "False disables this feature.") }) })

public class AutoDelaysTask extends AbstractTaskImplementation {

  public static final String SELEC_PARAM_NAME   = "Selection cuts";
  public static final String SELEC_PARAM_VALUE  = "4";
  public static final String MAXII_PARAM_NAME   = "Maximum cuts";
  public static final String MAXII_PARAM_VALUE  = "1";
  public static final String CYCLES_PARAM_NAME  = "Fill cycles ?";
  public static final String CYCLES_PARAM_VALUE = "false";
  public static final String SCHED_PARAM_NAME   = "Test scheduling ?";
  public static final String SCHED_PARAM_VALUE  = "false";
  public static final String CHOCO_PARAM_NAME   = "Test best choco ?";
  public static final String CHOCO_PARAM_VALUE  = "false";

  private static final String GENERIC_MAXII_ERROR = "Maximum number of graph cuts must be a positive number, "
      + "instead of: ";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    final int nbCore = architecture.getProcessingElements().get(0).getInstances().size();
    if (!graph.getChildrenGraphs().isEmpty()) {
      throw new PreesmRuntimeException("This task must be called with a flatten PiMM graph, abandon.");
    }

    if (!SlamDesignPEtypeChecker.isHomogeneousCPU(architecture)) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous CPU architecture, abandon.");
    }

    PreesmLogger.getLogger().info(() -> "Found " + nbCore + " cores.");

    final String selecStr = parameters.get(SELEC_PARAM_NAME);
    int selec = nbCore;
    try {
      final int parse = Integer.parseInt(selecStr);
      if (parse < 1) {
        throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + selecStr);
      }
      selec = parse;
    } catch (final NumberFormatException e) {
      throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + selecStr, e);
    }

    final String maxiiStr = parameters.get(MAXII_PARAM_NAME);
    int maxii = nbCore - 1;
    try {
      final int parse = Integer.parseInt(maxiiStr);
      if (parse < 0) {
        throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + maxiiStr);
      }
      maxii = Math.min(parse, maxii);
    } catch (final NumberFormatException e) {
      throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + maxiiStr, e);
    }

    final String cyclesStr = parameters.get(CYCLES_PARAM_NAME);
    final boolean cycles = Boolean.parseBoolean(cyclesStr);

    final Map<String, Object> output = new LinkedHashMap<>();
    if (maxii <= 0 && !cycles) {
      PreesmLogger.getLogger().log(Level.INFO, "Nothing to do.");
      output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
      return output;
    }

    final String schedStr = parameters.get(SCHED_PARAM_NAME);
    final boolean sched = Boolean.parseBoolean(schedStr);

    final String chocoStr = parameters.get(CHOCO_PARAM_NAME);
    final boolean choco = Boolean.parseBoolean(chocoStr);

    final PiGraph graphCopy = addDelays(graph, architecture, scenario, cycles, choco, sched, nbCore, selec, maxii);

    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graphCopy);

    return output;
  }

  /**
   * Add delays on a flat graph, only if architecture is homogeneous.
   *
   * @param graph
   *          Graph to add delays on.
   * @param architecture
   *          Architecture to consider.
   * @param scenario
   *          Scenario tp consider.
   * @param fillCycles
   *          If delays should be added on cycles too.
   * @param choco
   *          If testing against optimal solution computed by Choco.
   * @param sched
   *          If scheduling must be performed at the end.
   * @param nbCore
   *          Number of core in the architecture.
   * @param nbPreCuts
   *          Number of balanced cuts locations.
   * @param nbMaxCuts
   *          Number of selected cuts.
   * @return Graph copy with delays added on it.
   */
  public static PiGraph addDelays(final PiGraph graph, final Design architecture, final Scenario scenario,
      final boolean fillCycles, final boolean choco, final boolean sched, final int nbCore, final int nbPreCuts,
      final int nbMaxCuts) {

    if (!graph.getChildrenGraphs().isEmpty()) {
      throw new PreesmRuntimeException("This task must be called with a flatten PiMM graph, abandon.");
    }

    if (!SlamDesignPEtypeChecker.isHomogeneousCPU(architecture)) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous CPU architecture, abandon.");
    }

    final long time = System.nanoTime();

    // BRV and timings

    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    final Map<AbstractVertex, Long> brv = PiBRV.compute(graphCopy, BRVMethod.LCM);
    PiMMHelper.removeNonExecutedActorsAndFifos(graphCopy, brv);

    final Map<AbstractVertex, Long> wcets = new HashMap<>();
    for (final Entry<AbstractVertex, Long> en : brv.entrySet()) {
      final AbstractVertex a = en.getKey();
      final AbstractVertex actor = PreesmCopyTracker.getOriginalSource(a);
      long wcetMin = Long.MAX_VALUE;
      if (actor instanceof final AbstractActor abstractActor) {
        for (final Component operatorDefinitionID : architecture.getProcessingElements()) {
          final long timing = scenario.getTimings().evaluateExecutionTimeOrDefault(abstractActor, operatorDefinitionID);
          if (timing < wcetMin) {
            wcetMin = timing;
          }
        }
      } else {
        wcetMin = ScenarioConstants.DEFAULT_TIMING_TASK.getValue();
      }
      wcets.put(a, wcetMin);
    }

    final DescriptiveStatistics ds = new DescriptiveStatistics();
    for (final Entry<AbstractVertex, Long> en : wcets.entrySet()) {
      final AbstractVertex av = en.getKey();
      final Long wcet = en.getValue();
      final Long rv = brv.get(av);
      for (long i = 0; i < rv; i++) {
        ds.addValue(wcet);
      }
    }
    printWCETstatistics(ds);

    final HeuristicLoopBreakingDelays hlbd = new HeuristicLoopBreakingDelays();
    hlbd.performAnalysis(graphCopy, brv);

    if (fillCycles) {
      PreesmLogger.getLogger().log(Level.WARNING, "Experimental breaking of cycles.");
      fillCycles(hlbd);
      // we redo the analysis since adding delays will modify the topo ranks
      hlbd.performAnalysis(graphCopy, brv);
    }

    long duration = System.nanoTime() - time;
    PreesmLogger.getLogger().info("Analysis time " + Math.round(duration / 1e6) + " ms.");

    // intermediate data : forbidden fifos (in cycles), ranks, wcet(rank)

    final Set<FifoAbstraction> forbiddenFifos = hlbd.getForbiddenFifos();

    final Map<AbstractActor, TopoVisit> topoRanks = TopologicalRanking.topologicalAsapRanking(hlbd);
    // build intermediate list of actors per rank
    final SortedMap<Integer, Set<AbstractActor>> irRankActors = TopologicalRanking.mapRankActors(topoRanks, false, 0);
    // offset of one to ease some computations
    final int maxRank = irRankActors.lastKey() + 1;
    if (maxRank < 2) {
      // If there is only one rank ... where would you add delays !?
      PreesmLogger.getLogger().log(Level.INFO, "Nothing to do.");
      return graphCopy;
    }
    final int selec = Math.min(nbPreCuts, maxRank - 1);
    final int maxii = Math.min(nbMaxCuts, maxRank - 1);

    final Map<AbstractActor, TopoVisit> topoRanksT = TopologicalRanking.topologicalAsapRankingT(hlbd);
    // what we are interested in is not exactly ALAP = inverse of ASAP_T, it is ALAP with all sources executed at the
    // beginning
    hlbd.allSourceActors.stream().forEach(x -> {
      final TopoVisit tv = topoRanksT.get(x);
      tv.rank = maxRank - 1;
    });
    final SortedMap<Integer,
        Set<AbstractActor>> irRankActorsT = TopologicalRanking.mapRankActors(topoRanksT, true, maxRank);

    final SortedMap<Integer, Long> rankWCETs = new TreeMap<>();
    for (final Entry<AbstractActor, TopoVisit> e : topoRanks.entrySet()) {
      final AbstractActor aa = e.getKey();
      final TopoVisit tv = e.getValue();
      final long tWCET = (long) Math.ceil((double) hlbd.minCycleBrv.get(aa) / nbCore) * wcets.get(aa);
      final long prev = rankWCETs.getOrDefault(tv.rank, 0L);
      rankWCETs.put(tv.rank, tWCET + prev);
    }
    // reorder topoRanksT with same order as topoRanks
    for (final Entry<AbstractActor, TopoVisit> e : topoRanksT.entrySet()) {
      final AbstractActor aa = e.getKey();
      final TopoVisit tv = e.getValue();
      final long tWCET = (long) Math.ceil((double) hlbd.minCycleBrv.get(aa) / nbCore) * wcets.get(aa);
      final int rank = maxRank - tv.rank;
      final long prev = rankWCETs.getOrDefault(rank, 0L);
      rankWCETs.put(rank, tWCET + prev);

    }
    // as loads are counted 2 times if one same rank, divide by 2
    // scale by the number of actors per rank (in order to expose parallelism)
    long totC = 0L;
    for (final Entry<Integer, Long> e : rankWCETs.entrySet()) {
      final int rank = e.getKey();
      long load = e.getValue() / 2;
      final int size = (irRankActors.get(rank).size() + irRankActorsT.get(rank).size()) / 2;
      load = (long) Math.ceil((double) load / size);
      totC += load;
      rankWCETs.put(rank, load);
    }

    // compute possible cuts
    final SortedMap<Integer, Set<CutInformation>> possibleCuts = computePossibleCuts(irRankActors, irRankActorsT,
        topoRanks, topoRanksT, forbiddenFifos, maxRank, hlbd);

    final List<CutInformation> bestCuts = selectBestCuts(possibleCuts, selec, rankWCETs, totC, scenario);

    duration = System.nanoTime() - time;
    PreesmLogger.getLogger().info("Total heuristic time " + Math.round(duration / 1e6) + " ms.");

    // set the cuts
    final int nbCuts = Math.min(bestCuts.size(), maxii);
    for (int index = 0; index < nbCuts; index++) {
      final CutInformation ci = bestCuts.get(index);
      PreesmLogger.getLogger()
          .info(() -> "Setting cut from rank " + ci.getRankStr() + " using " + ci.memSize + " bits.");
      // set the graph
      final Map<FifoAbstraction, Integer> cutMap = new HashMap<>();
      ci.edgeCut.forEach(fa -> cutMap.put(fa, 1));

      setCut(cutMap, false);
    }

    if (choco && maxii > 0) {
      // compute latency of heuristic
      final long heuristicLatency = computeLatency(graphCopy, scenario, architecture, false);
      // reset heuristic delays
      for (int index = 0; index < nbCuts; index++) {
        final CutInformation ci = bestCuts.get(index);
        final Map<FifoAbstraction, Integer> cutMap = new HashMap<>();
        ci.edgeCut.forEach(fa -> cutMap.put(fa, 1));
        setCut(cutMap, true);
      }
      // call choco
      final DescriptiveStatistics dsc = executeChocoModel(graphCopy, architecture, scenario, maxii, hlbd);
      printChocoStatistics(heuristicLatency, dsc);
    }

    if (sched) {
      computeLatency(graphCopy, scenario, architecture, true);
    }

    return graphCopy;
  }

  private static void printChocoStatistics(final long heuristicLatency, final DescriptiveStatistics dsc) {

    PreesmLogger.getLogger().info("Worst latency found by choco cut: " + dsc.getMax());
    PreesmLogger.getLogger().info("Best latency found by choco cut: " + dsc.getMin());
    PreesmLogger.getLogger().info("Mean latency found by choco cut: " + dsc.getMean());

    final double[] chocoLatencies = dsc.getValues();
    final int counterAboveLat = (int) Arrays.stream(chocoLatencies).filter(cl -> heuristicLatency <= cl).count();

    final int percent = (counterAboveLat * 100) / chocoLatencies.length;
    PreesmLogger.getLogger()
        .info(() -> "Heuristic latency is better (or equal) than " + counterAboveLat + " cuts, i.e. " + percent + "%");

  }

  private static void printWCETstatistics(DescriptiveStatistics ds) {
    final long maxWCET = Math.round(ds.getMax());
    final long percentMax = Math.round((ds.getMax() / ds.getSum()) * 100);
    PreesmLogger.getLogger().fine(() -> "Max WCET is " + maxWCET + " and represents " + percentMax + "% of the total.");
    final long meanWCET = Math.round(ds.getMean());
    final long percentMean = Math.round((ds.getMean() / ds.getSum()) * 100);
    PreesmLogger.getLogger()
        .fine(() -> "Mean WCET is " + meanWCET + " and represents " + percentMean + "% of the total.");
    final long standardDeviation = Math.round(ds.getStandardDeviation());
    final long percentSD = Math.round((ds.getStandardDeviation() / ds.getSum()) * 100);
    PreesmLogger.getLogger().fine(() -> "Standard deviation of WCET is " + standardDeviation + " and represents "
        + percentSD + "% of the total.");
  }

  private static DescriptiveStatistics executeChocoModel(final PiGraph graphCopy, final Design architecture,
      final Scenario scenario, final int maxii, final HeuristicLoopBreakingDelays hlbd) {
    // final long time1 = System.nanoTime();
    final ChocoCutModel ccm = new ChocoCutModel(hlbd, maxii);
    final DescriptiveStatistics dsc = new DescriptiveStatistics();
    // /!\ commented code in case you want to find all cuts at once before exploring them
    // final List<Map<FifoAbstraction, Integer>> chocoCuts = ccm.findAllCuts();
    // long duration = System.nanoTime() - time1;
    // PreesmLogger.getLogger().info("Time of choco model " + Math.round(duration / 1e6) + " ms.");
    final long time2 = System.nanoTime();
    Map<FifoAbstraction, Integer> bestDelays = null;
    long bestLatency = Long.MAX_VALUE;
    final long bestMemory = Long.MAX_VALUE;
    final Level backupLevel = PreesmLogger.getLogger().getLevel();
    PreesmLogger.getLogger().setLevel(Level.SEVERE);
    // for (Map<FifoAbstraction, Integer> delays : chocoCuts) {
    long nbCutsTested = 0;
    Map<FifoAbstraction, Integer> delays = null;
    while ((delays = ccm.findNextCut()) != null) {
      nbCutsTested++;
      setCut(delays, false);
      final long latency = computeLatency(graphCopy, scenario, architecture, false);
      dsc.addValue(latency);
      if (latency < bestLatency) {
        bestLatency = latency;
        bestDelays = delays;
      } else if (latency == bestLatency) {
        final long memCost = getCutMemoryCost(delays.keySet(), scenario);
        if (memCost < bestMemory) {
          bestLatency = latency;
          bestDelays = delays;
        }
      }
      setCut(delays, true);
      if (nbCutsTested % 1000 == 0) {
        PreesmLogger.getLogger().fine(() -> "1000 cuts tested.");
      }
    }
    final long duration = System.nanoTime() - time2;
    PreesmLogger.getLogger().setLevel(backupLevel);
    PreesmLogger.getLogger().info("Number of cuts tested: " + nbCutsTested);
    PreesmLogger.getLogger().info(() -> "Time of choco tests " + Math.round(duration / 1e6) + " ms.");
    if (bestDelays != null) {
      setCut(bestDelays, false);
      final StringBuilder sb = new StringBuilder("\nAdded delays by choco:\n");
      for (final Entry<FifoAbstraction, Integer> e : bestDelays.entrySet()) {
        final FifoAbstraction fa = e.getKey();
        final AbstractActor src = hlbd.absGraph.getEdgeSource(fa);
        final AbstractActor tgt = hlbd.absGraph.getEdgeTarget(fa);
        final int stages = e.getValue();
        sb.append(stages + " stages from " + src.getName() + " to " + tgt.getName() + "\n");
      }

      PreesmLogger.getLogger().info(() -> "Best Choco cut: " + sb.toString());
    }
    return dsc;
  }

  private static void setCut(final Map<FifoAbstraction, Integer> delays, final boolean reset) {

    for (final Entry<FifoAbstraction, Integer> e : delays.entrySet()) {
      final FifoAbstraction fa = e.getKey();
      final int delayMultiplier = e.getValue();
      final List<Long> pipelineValues = fa.pipelineValues;
      final List<Fifo> fifos = fa.fifos;
      final int size = fifos.size();
      for (int i = 0; i < size; i++) {
        final Fifo f = fifos.get(i);
        long pipeSize = delayMultiplier * pipelineValues.get(i);
        Delay delay = f.getDelay();
        if (delay == null && !reset) {
          delay = PiMMUserFactory.instance.createDelay();
          f.assignDelay(delay);
          delay.setName(delay.getId());
          delay.getActor().setName(delay.getId());
          final PiGraph graphFifo = f.getContainingPiGraph();
          graphFifo.addDelay(delay);
          PreesmLogger.getLogger().info(() -> "Set fifo delay size and type of: " + f.getId());
        } else if (delay != null) {
          if (!reset) {
            pipeSize += delay.getExpression().evaluate();
            PreesmLogger.getLogger().warning(() -> "Reset fifo delay size and type of: " + f.getId());
          } else {
            pipeSize = delay.getExpression().evaluate() - pipeSize;
          }
        } else {
          // when (delay == null && reset == true)
          continue;
        }
        delay.setLevel(PersistenceLevel.PERMANENT);
        delay.setExpression(pipeSize);
      }
    }

  }

  private static long computeLatency(PiGraph graph, Scenario scenario, Design architecture, boolean printGantt) {
    final PiGraph dag = PiSDFToSingleRate.compute(graph, BRVMethod.LCM);
    final IScheduler scheduler = new PeriodicScheduler();
    SynthesisResult scheduleAndMap = null;
    try {
      scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
    } catch (final PreesmSchedulingException e) {
      PreesmLogger.getLogger().log(Level.WARNING, "Scheudling was impossible.", e);
      return Long.MAX_VALUE;
    }
    // use implementation evaluation of PeriodicScheduler instead?
    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
    final LatencyCost evaluate = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    PreesmLogger.getLogger().info(() -> "Latency of graph with added delays: " + evaluate.getValue());

    if (printGantt) {
      final IStatGenerator statGen = new StatGeneratorSynthesis(architecture, scenario, scheduleAndMap.mapping, null,
          evaluate);
      final IEditorInput input = new StatEditorInput(statGen);

      // Check if the workflow is running in command line mode
      try {
        // Run statistic editor
        PlatformUI.getWorkbench().getDisplay().asyncExec(new EditorRunnable(input));
      } catch (final IllegalStateException e) {
        PreesmLogger.getLogger().info("Gantt display is impossible in this context."
            + " Ignore this log entry if you are running the command line version of Preesm.");
      }
    }
    return evaluate.getValue();
  }

  private static void fillCycles(final HeuristicLoopBreakingDelays hlbd) {
    for (final CycleInfos ci : hlbd.cyclesInfos.values()) {
      final FifoAbstraction fa = ci.breakingFifo;
      final long gcdCycle = ci.repetition;
      final List<Long> pipelineValues = fa.pipelineValues;
      final List<Fifo> fifos = fa.fifos;
      final int size = fifos.size();

      for (int i = 0; i < size; i++) {
        final Fifo f = fifos.get(i);
        long pipeSize = pipelineValues.get(i) / gcdCycle;

        Delay delay = f.getDelay();
        if (delay == null) {
          delay = PiMMUserFactory.instance.createDelay();
          f.assignDelay(delay);
          delay.setName(delay.getId());
          delay.getActor().setName(delay.getId());
          final PiGraph graphFifo = f.getContainingPiGraph();
          graphFifo.addDelay(delay);
          PreesmLogger.getLogger().info(() -> "[in Cycle] Set fifo delay size and type of: " + f.getId());
        } else {
          final long prevSize = delay.getExpression().evaluate();
          pipeSize = Math.max(pipeSize, prevSize);
          if (pipeSize != prevSize) {
            PreesmLogger.getLogger().warning(() -> "[in Cycle] Reset fifo delay size and type of: " + f.getId());
          } else {
            PreesmLogger.getLogger()
                .info(() -> "[in Cycle] Fiifo delay size of: " + f.getId() + " is already correct.");
          }
        }
        delay.setLevel(PersistenceLevel.PERMANENT);
        delay.setExpression(pipeSize);
      }
    }
  }

  private static SortedMap<Integer, Set<CutInformation>> computePossibleCuts(
      final SortedMap<Integer, Set<AbstractActor>> irRankActors,
      final SortedMap<Integer, Set<AbstractActor>> irRankActorsT, final Map<AbstractActor, TopoVisit> topoRanks,
      final Map<AbstractActor, TopoVisit> topoRanksT, final Set<FifoAbstraction> forbiddenFifos, final int maxRank,
      final HeuristicLoopBreakingDelays hlbd) {
    final SortedMap<Integer, Set<CutInformation>> result = new TreeMap<>();

    final SortedMap<Integer,
        Set<FifoAbstraction>> crossingFifos = computeCrossingFifos(false, topoRanks, hlbd, maxRank, forbiddenFifos);
    for (final Entry<Integer, Set<AbstractActor>> e : irRankActors.entrySet()) {
      final int rank = e.getKey();
      final Set<AbstractActor> aas = e.getValue();
      final Set<FifoAbstraction> fas = computeIncomingCut(aas, forbiddenFifos, hlbd);
      if (!fas.isEmpty()) {
        final Set<FifoAbstraction> crossingFas = crossingFifos.get(rank);
        if (crossingFas != null) {
          fas.addAll(crossingFas);
          Set<CutInformation> fass = result.get(rank);
          if (fass == null) {
            fass = new HashSet<>();
            result.put(rank, fass);
          }
          fass.add(new CutInformation(fas, rank, false));
        }
      }
    }

    final SortedMap<Integer,
        Set<FifoAbstraction>> crossingFifosT = computeCrossingFifos(true, topoRanksT, hlbd, maxRank, forbiddenFifos);

    for (final Entry<Integer, Set<AbstractActor>> e : irRankActorsT.entrySet()) {
      final int rank = e.getKey();
      final Set<AbstractActor> aas = e.getValue();
      final Set<FifoAbstraction> fas = computeIncomingCut(aas, forbiddenFifos, hlbd);
      if (!fas.isEmpty()) {
        final Set<FifoAbstraction> crossingFas = crossingFifosT.get(rank);
        if (crossingFas != null) {
          fas.addAll(crossingFas);
          Set<CutInformation> fass = result.get(rank);
          if (fass == null) {
            fass = new HashSet<>();
            result.put(rank, fass);
          }
          fass.add(new CutInformation(fas, rank, true));
        }
      }
    }

    if (PreesmLogger.getLogger().isLoggable(Level.FINE)) {
      for (final Entry<Integer, Set<CutInformation>> e : result.entrySet()) {
        final int rank = e.getKey();
        PreesmLogger.getLogger().fine(() -> "=====> Rank " + rank);
        for (final CutInformation ci : e.getValue()) {
          final Set<FifoAbstraction> fas = ci.edgeCut;
          final StringBuilder sb = new StringBuilder("Cut: \n");
          for (final FifoAbstraction fa : fas) {
            final AbstractActor src = hlbd.absGraph.getEdgeSource(fa);
            final AbstractActor tgt = hlbd.absGraph.getEdgeTarget(fa);
            sb.append(src.getName() + " --> " + tgt.getName() + "\n");
          }
          PreesmLogger.getLogger().log(Level.FINE, sb.toString());
        }
      }
    }

    return result;
  }

  private static SortedMap<Integer, Set<FifoAbstraction>> computeCrossingFifos(final boolean reverse,
      final Map<AbstractActor, TopoVisit> topoRanks, final HeuristicLoopBreakingDelays hlbd, final int maxRank,
      final Set<FifoAbstraction> forbiddenFifos) {
    final SortedMap<Integer, Set<FifoAbstraction>> result = new TreeMap<>();
    for (int i = 1; i < maxRank; i++) {
      result.put(i, new HashSet<>());
    }
    final DefaultDirectedGraph<AbstractActor, FifoAbstraction> graph = hlbd.getAbsGraph();
    for (final FifoAbstraction fa : graph.edgeSet()) {
      final AbstractActor src = graph.getEdgeSource(fa);
      final AbstractActor tgt = graph.getEdgeTarget(fa);
      int rankSrc = topoRanks.get(src).rank;
      int rankTgt = topoRanks.get(tgt).rank;
      if (reverse) {
        rankSrc = maxRank - rankSrc;
        rankTgt = maxRank - rankTgt;
      }
      rankSrc += 1;
      if (rankTgt > rankSrc) {
        final boolean isForbidden = forbiddenFifos.contains(fa);
        for (int i = rankSrc; i <= rankTgt; i++) {
          if (isForbidden) {
            result.put(i, null);
          } else {
            final Set<FifoAbstraction> fas = result.get(i);
            if (fas != null) {
              fas.add(fa);
            }
          }
        }
      }
    }
    return result;
  }

  private static Set<FifoAbstraction> computeIncomingCut(final Set<AbstractActor> aas,
      final Set<FifoAbstraction> forbiddenFifos, final HeuristicLoopBreakingDelays hlbd) {
    final Set<FifoAbstraction> fas = new HashSet<>();
    boolean isOK = true;
    for (final AbstractActor aa : aas) {
      for (final FifoAbstraction fa : hlbd.absGraph.incomingEdgesOf(aa)) {
        final AbstractActor src = hlbd.absGraph.getEdgeSource(fa);
        final boolean selfLoop = src == aa;
        final boolean forbiddenFifo = forbiddenFifos.contains(fa);
        if (!forbiddenFifo && !selfLoop) {
          fas.add(fa);
        } else if (hlbd.breakingFifosAbs.contains(fa)) {
          // breakingFifosAbs already contains self-loops
          // do nothing: we will not add delays on this one
        } else {
          isOK = false;
          break;
        }
      }
    }
    if (!isOK) {
      fas.clear();
    }
    return fas;
  }

  private static List<CutInformation> selectBestCuts(SortedMap<Integer, Set<CutInformation>> cuts, final int nbSelec,
      final SortedMap<Integer, Long> rankWCETs, final long totC, final Scenario scenar) {
    final Set<Integer> preSelectedRanks = new LinkedHashSet<>();
    int maxParallelActors = 0;
    for (final Set<CutInformation> cis : cuts.values()) {
      for (final CutInformation ci : cis) {
        final int nbActorsInCut = ci.edgeCut.size();
        if (nbActorsInCut > maxParallelActors) {
          maxParallelActors = nbActorsInCut;
        }
      }
    }

    PreesmLogger.getLogger().log(Level.FINE, "Max Actors in Parallel: " + maxParallelActors);

    // we divide by the number of maxii
    final long avgCutLoad = totC / (nbSelec + 1);
    final long maxLoad = totC - avgCutLoad;
    PreesmLogger.getLogger().fine(() -> "Average cut load: " + avgCutLoad);
    int lastLoadIndex = 1;
    long currentLoad = 0;
    for (final Entry<Integer, Long> e : rankWCETs.entrySet()) {
      if (currentLoad >= avgCutLoad * lastLoadIndex && (currentLoad <= maxLoad || preSelectedRanks.isEmpty())) {
        final int rank = e.getKey();
        if (!cuts.getOrDefault(rank, new HashSet<>()).isEmpty()) {
          preSelectedRanks.add(rank);
          lastLoadIndex += 1;
        }
      }
      // done at last since cuts start at rank 2
      currentLoad += e.getValue();
    }

    // same in the reverse order
    final SortedMap<Integer, Long> rankWCETsT = new TreeMap<>(Collections.reverseOrder());
    rankWCETsT.putAll(rankWCETs);
    lastLoadIndex = 1;
    currentLoad = 0;
    for (final Entry<Integer, Long> e : rankWCETsT.entrySet()) {
      currentLoad += e.getValue();
      if (currentLoad >= avgCutLoad * lastLoadIndex && (currentLoad <= maxLoad || preSelectedRanks.size() < 2)) {
        final int rank = e.getKey();
        if (!cuts.getOrDefault(rank, new HashSet<>()).isEmpty()) {
          preSelectedRanks.add(rank);
          lastLoadIndex += 1;
        }
      }
    }

    final String strPreSelected = String.join(", ", preSelectedRanks.stream().map(i -> Integer.toString(i)).toList());
    PreesmLogger.getLogger().fine(() -> "Preselected cut ranks: " + strPreSelected);
    // select remaining cuts sorted by memory size
    final List<CutInformation> bestCuts = new ArrayList<>();
    for (final int i : preSelectedRanks) {
      final List<CutInformation> twoPossibilities = new ArrayList<>(cuts.get(i));
      if (twoPossibilities.size() == 1) {
        final CutInformation cut0 = twoPossibilities.get(0);
        cut0.memSize = getCutMemoryCost(cut0.edgeCut, scenar);
        bestCuts.add(cut0);
      } else if (twoPossibilities.size() == 2) {
        final CutInformation cut0 = twoPossibilities.get(0);
        final CutInformation cut1 = twoPossibilities.get(1);
        cut0.memSize = getCutMemoryCost(cut0.edgeCut, scenar);
        cut1.memSize = getCutMemoryCost(cut1.edgeCut, scenar);
        if (cut0.memSize < cut1.memSize) {
          bestCuts.add(cut0);
        } else {
          bestCuts.add(cut1);
        }
      }
    }

    bestCuts.sort(new CutSizeComparator());

    if (bestCuts.size() > 1 && nbSelec == 1) {
      // selects cut which is closer to the middle
      CutInformation bestCut = null;
      long bestCutValue = 0;
      for (final CutInformation ci : bestCuts) {
        final long wcetDiff = Math.abs(avgCutLoad - getIntermediateWeightedWCET(1, ci.rank, rankWCETs));
        if (bestCut == null || wcetDiff < bestCutValue) {
          bestCut = ci;
          bestCutValue = wcetDiff;
        }
      }
      bestCuts.clear();
      bestCuts.add(bestCut);

    } else if (!bestCuts.isEmpty()) {
      // remove cuts that are too close from each other
      // may happen between cuts from topo and cuts from topoT
      final CutSizeComparator csc = new CutSizeComparator();
      final Set<CutInformation> cisToRemove = new HashSet<>();
      final int bcSize = bestCuts.size();
      for (int i = bcSize - 1; i >= 0; i--) {
        final CutInformation ci1 = bestCuts.get(i);
        if (bcSize - cisToRemove.size() <= nbSelec) {
          break;
        }
        // traversal in reverse order since we want to get
        // rid of large cut first
        for (int j = i - 1; j >= 0; j--) {
          if (bcSize - cisToRemove.size() <= nbSelec) {
            break;
          }
          final CutInformation ci2 = bestCuts.get(j);
          final long wcetDiff = getIntermediateWeightedWCET(ci1.rank, ci2.rank, rankWCETs);
          if (wcetDiff < avgCutLoad) {
            final int memComparison = csc.compare(ci1, ci2);
            if (memComparison < 0) {
              cisToRemove.add(ci2);
            } else if (memComparison > 0) {
              cisToRemove.add(ci1);
            } else {
              // breaks tie with lower rank
              // (otherwise it is not fully transitive, and we might remove all ci ...)
              final int rankComparison = Integer.compare(ci1.rank, ci2.rank);
              if (rankComparison < 0) {
                cisToRemove.add(ci2);
              } else {
                cisToRemove.add(ci1);
              }
            }
          }
        }
      }
      final String strRemovedClose = String.join(", ", cisToRemove.stream().map(x -> x.getRankStr()).toList());
      PreesmLogger.getLogger().fine(() -> "Removed too close cut ranks: " + strRemovedClose);

      bestCuts.removeAll(cisToRemove);
    }

    return bestCuts;

  }

  private static long getIntermediateWeightedWCET(final int rank1, final int rank2,
      SortedMap<Integer, Long> rankWCETs) {
    long res = 0L;
    for (int i = Math.min(rank1, rank2); i < Math.max(rank1, rank2); i++) {
      res += rankWCETs.get(i);
    }
    return res;
  }

  private static long getCutMemoryCost(Set<FifoAbstraction> fas, Scenario scenar) {
    long totalSize = 0L;
    final SimulationInfo si = scenar.getSimulationInfo();
    for (final FifoAbstraction fa : fas) {
      final List<Long> pipelineValues = fa.pipelineValues;
      final List<Fifo> fifos = fa.fifos;
      final int size = fifos.size();
      for (int i = 0; i < size; i++) {
        final Fifo f = fifos.get(i);
        final long dataSize = si.getDataTypeSizeInBit(f.getType());
        long pipeSize = pipelineValues.get(i);
        pipeSize *= dataSize;
        totalSize += pipeSize;
      }
    }
    return totalSize;
  }

  /**
   * Stores main information about a cut, the memory size is computed after the call to the constructor.
   *
   * @author ahonorat
   */
  public static class CutInformation {

    private final Set<FifoAbstraction> edgeCut;
    private final int                  rank;
    private final boolean              wasReversed;
    private long                       memSize = 0L;

    private CutInformation(Set<FifoAbstraction> edgeCut, int rank, boolean wasReversed) {
      this.edgeCut = edgeCut;
      this.rank = rank;
      this.wasReversed = wasReversed;
    }

    public String getRankStr() {
      final String reverseInfo = wasReversed ? "T" : "";
      return rank + reverseInfo;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof final CutInformation ci) {
        return edgeCut.equals(ci.edgeCut);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return edgeCut.hashCode();
    }

  }

  /**
   * Comparator of graph cuts, in ascending order of memory size
   *
   * @author ahonorat
   */
  public static class CutSizeComparator implements Comparator<CutInformation> {

    private CutSizeComparator() {
    }

    @Override
    public int compare(CutInformation arg0, CutInformation arg1) {
      final long size0 = arg0.memSize;
      final long size1 = arg1.memSize;
      return Long.compare(size0, size1);
    }

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(SELEC_PARAM_NAME, SELEC_PARAM_VALUE);
    parameters.put(MAXII_PARAM_NAME, MAXII_PARAM_VALUE);
    parameters.put(CYCLES_PARAM_NAME, CYCLES_PARAM_VALUE);
    parameters.put(SCHED_PARAM_NAME, SCHED_PARAM_VALUE);
    parameters.put(CHOCO_PARAM_NAME, CHOCO_PARAM_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computes delays to speed up the graph execution.";
  }

}
