package org.preesm.algorithm.pisdf.autodelays;

import java.util.ArrayList;
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
import java.util.stream.Collectors;
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
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.evaluation.latency.SimpleLatencyEvaluation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
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
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.SimulationInfo;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class computes best locations for delays.
 *
 * @author ahonorat
 *
 */
@PreesmTask(id = "pisdf-delays.setter", name = "Automatic Placement of Delays",
    shortDescription = "Puts delays in a flat PiMM, in order to speed up the execution.",

    description = "Puts delays in a flat PiMM, in order to speed up the execution. "
        + "The heuristic will perform a search of all simple cycles, so the task may take time to run.",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = {
        @Parameter(name = AutoDelaysTask.SELEC_PARAM_NAME,
            values = { @Value(name = AutoDelaysTask.SELEC_PARAM_VALUE, effect = "Number of graph cuts to consider.") }),
        @Parameter(name = AutoDelaysTask.MAXII_PARAM_NAME,
            values = { @Value(name = AutoDelaysTask.MAXII_PARAM_VALUE,
                effect = "Maximum number of graph cuts induced by the added delays.") }),
        @Parameter(name = AutoDelaysTask.CHOCO_PARAM_NAME,
            values = { @Value(name = AutoDelaysTask.CHOCO_PARAM_VALUE,
                effect = "Whether or not the cuts should be selected among the best Choco solutions "
                    + "(disable the heuristic).") }),
        @Parameter(name = AutoDelaysTask.SCHED_PARAM_NAME,
            values = { @Value(name = AutoDelaysTask.SCHED_PARAM_VALUE,
                effect = "Whether or not a scheduling is attempted at the end.") }),
        @Parameter(name = AutoDelaysTask.CYCLES_PARAM_NAME, values = { @Value(name = AutoDelaysTask.CYCLES_PARAM_VALUE,
            effect = "Whether or not the task must also break the cycles with delays.") }) }

)
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

    if (!graph.getChildrenGraphs().isEmpty()) {
      throw new PreesmRuntimeException("This task must be called with a flatten PiMM graph, abandon.");
    }

    if (architecture.getOperatorComponents().size() != 1) {
      throw new PreesmRuntimeException("This task must be called with a homogeneous architecture, abandon.");
    }

    int nbCore = architecture.getOperatorComponents().get(0).getInstances().size();
    PreesmLogger.getLogger().log(Level.INFO, "Found " + nbCore + " cores.");

    final String selecStr = parameters.get(SELEC_PARAM_NAME);
    int selec = nbCore;
    try {
      int parse = Integer.parseInt(selecStr);
      if (parse < 1) {
        throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + selecStr);
      }
      selec = parse;
    } catch (NumberFormatException e) {
      throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + selecStr, e);
    }

    final String maxiiStr = parameters.get(MAXII_PARAM_NAME);
    int maxii = nbCore - 1;
    try {
      int parse = Integer.parseInt(maxiiStr);
      if (parse < 0) {
        throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + maxiiStr);
      }
      maxii = Math.min(parse, maxii);
    } catch (NumberFormatException e) {
      throw new PreesmRuntimeException(GENERIC_MAXII_ERROR + maxiiStr, e);
    }

    final String cyclesStr = parameters.get(CYCLES_PARAM_NAME);
    final boolean cycles = Boolean.parseBoolean(cyclesStr);

    final Map<String, Object> output = new LinkedHashMap<>();
    if (maxii <= 0 && !cycles) {
      PreesmLogger.getLogger().log(Level.INFO, "nothing to do.");
      output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
      return output;
    }

    final String schedStr = parameters.get(SCHED_PARAM_NAME);
    final boolean sched = Boolean.parseBoolean(schedStr);

    final long time = System.nanoTime();

    // BRV and timings

    PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    Map<AbstractVertex, Long> brv = PiBRV.compute(graphCopy, BRVMethod.LCM);

    Map<AbstractVertex, Long> wcets = new HashMap<>();
    for (final Entry<AbstractVertex, Long> en : brv.entrySet()) {
      final AbstractVertex a = en.getKey();
      AbstractVertex actor = PreesmCopyTracker.getOriginalSource(a);
      long wcetMin = Long.MAX_VALUE;
      if (actor instanceof AbstractActor) {
        for (final Component operatorDefinitionID : architecture.getOperatorComponents()) {
          final long timing = scenario.getTimings().evaluateTimingOrDefault((AbstractActor) actor,
              operatorDefinitionID);
          if (timing < wcetMin) {
            wcetMin = timing;
          }
        }
      } else {
        wcetMin = ScenarioConstants.DEFAULT_TIMING_TASK.getValue();
      }
      wcets.put(a, wcetMin);
    }
    DescriptiveStatistics ds = new DescriptiveStatistics();
    for (final Entry<AbstractVertex, Long> en : wcets.entrySet()) {
      AbstractVertex av = en.getKey();
      Long wcet = en.getValue();
      Long rv = brv.get(av);
      for (long i = 0; i < rv; i++) {
        ds.addValue(wcet);
      }
    }
    printWCETstatistics(ds);

    final HeuristicLoopBreakingDelays hlbd = new HeuristicLoopBreakingDelays();
    hlbd.performAnalysis(graphCopy, brv);

    if (cycles) {
      PreesmLogger.getLogger().log(Level.WARNING, "Experimental breaking of cycles.");
      fillCycles(hlbd, brv);
      // we redo the analysis since adding delays will modify the topo ranks
      hlbd.performAnalysis(graphCopy, brv);
    }

    long duration = System.nanoTime() - time;
    PreesmLogger.getLogger().info("Analysis time " + Math.round(duration / 1e6) + " ms.");

    // intermediate data : forbidden fifos (in cycles), ranks, wcet(rank)

    final Set<FifoAbstraction> forbiddenFifos = hlbd.getForbiddenFifos();

    final Set<AbstractActor> sourceActors = new LinkedHashSet<>(hlbd.additionalSourceActors);
    final Set<AbstractActor> sinkActors = new LinkedHashSet<>(hlbd.additionalSinkActors);
    for (final AbstractActor absActor : graphCopy.getActors()) {
      if (absActor instanceof ExecutableActor) {
        if (absActor.getDataOutputPorts().isEmpty()) {
          sinkActors.add(absActor);
        }
        if (absActor.getDataInputPorts().isEmpty()) {
          sourceActors.add(absActor);
        }
      }
    }
    final Map<AbstractActor,
        TopoVisit> topoRanks = TopologicalRanking.topologicalASAPranking(sourceActors, hlbd.actorsNbVisitsTopoRank);
    // build intermediate list of actors per rank
    final SortedMap<Integer, Set<AbstractActor>> irRankActors = mapRankActors(topoRanks, false, 0);
    // offset of one to ease next computation
    final int maxRank = irRankActors.lastKey() + 1;
    final Map<AbstractActor,
        TopoVisit> topoRanksT = TopologicalRanking.topologicalASAPrankingT(sinkActors, hlbd.actorsNbVisitsTopoRankT);
    final SortedMap<Integer, Set<AbstractActor>> irRankActorsT = mapRankActors(topoRanksT, true, maxRank);

    final SortedMap<Integer, Long> rankWCETs = new TreeMap<>();
    for (Entry<AbstractActor, TopoVisit> e : topoRanks.entrySet()) {
      AbstractActor aa = e.getKey();
      TopoVisit tv = e.getValue();
      long tWCET = (long) Math.ceil((double) hlbd.minCycleBrv.get(aa) / nbCore) * wcets.get(aa);
      long prev = rankWCETs.getOrDefault(tv.rank, 0L);
      rankWCETs.put(tv.rank, tWCET + prev);
    }
    // reorder topoRanksT with same order as topoRanks
    for (Entry<AbstractActor, TopoVisit> e : topoRanksT.entrySet()) {
      AbstractActor aa = e.getKey();
      TopoVisit tv = e.getValue();
      long tWCET = (long) Math.ceil((double) hlbd.minCycleBrv.get(aa) / nbCore) * wcets.get(aa);
      int rank = maxRank - tv.rank;
      long prev = rankWCETs.getOrDefault(rank, 0L);
      rankWCETs.put(rank, tWCET + prev);
    }
    // as loads are counted 2 times if one same rank, divide by 2
    // scale by the number of actors per rank (in order to expose parallelism)
    long totC = 0L;
    for (Entry<Integer, Long> e : rankWCETs.entrySet()) {
      int rank = e.getKey();
      long load = e.getValue() / 2;
      int size = (irRankActors.get(rank).size() + irRankActorsT.get(rank).size()) / 2;
      load = (long) Math.ceil((double) load / size);
      totC += load;
      rankWCETs.put(rank, load);
    }

    // compute possible cuts
    SortedMap<Integer, Set<CutInformation>> possibleCuts = computePossibleCuts(irRankActors, irRankActorsT, topoRanks,
        topoRanksT, forbiddenFifos, maxRank, hlbd);

    List<CutInformation> bestCuts = selectBestCuts(possibleCuts, selec, rankWCETs, totC, scenario);

    duration = System.nanoTime() - time;
    PreesmLogger.getLogger().info("Total heuristic time " + Math.round(duration / 1e6) + " ms.");

    // set the cuts
    final int nbCuts = Math.min(bestCuts.size(), maxii);
    for (int index = 0; index < nbCuts; index++) {
      CutInformation ci = bestCuts.get(index);
      PreesmLogger.getLogger().log(Level.INFO,
          "Setting cut from rank " + ci.getRankStr() + " using " + ci.memSize + " Bytes.");
      // set the graph
      Map<FifoAbstraction, Integer> cutMap = new HashMap<>();
      for (FifoAbstraction fa : ci.edgeCut) {
        cutMap.put(fa, 1);
      }
      setCut(cutMap, false);
    }

    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graphCopy);

    final String chocoStr = parameters.get(CHOCO_PARAM_NAME);
    final boolean choco = Boolean.parseBoolean(chocoStr);
    if (choco && maxii > 0) {
      // compute latency of heuristic
      long heuristicLatency = computeLatency(graphCopy, scenario, architecture, false);
      // reset heuristic delays
      for (int index = 0; index < nbCuts; index++) {
        CutInformation ci = bestCuts.get(index);
        Map<FifoAbstraction, Integer> cutMap = new HashMap<>();
        for (FifoAbstraction fa : ci.edgeCut) {
          cutMap.put(fa, 1);
        }
        setCut(cutMap, true);
      }
      // call choco
      DescriptiveStatistics dsc = executeChocoModel(graphCopy, architecture, scenario, maxii, hlbd);
      printChocoStatistics(heuristicLatency, dsc);
    }

    if (sched) {
      computeLatency(graphCopy, scenario, architecture, true);
    }

    return output;
  }

  private void printChocoStatistics(final long heuristicLatency, final DescriptiveStatistics dsc) {

    PreesmLogger.getLogger().info("Worst latency found by choco cut: " + dsc.getMax());
    PreesmLogger.getLogger().info("Best latency found by choco cut: " + dsc.getMin());
    PreesmLogger.getLogger().info("Mean latency found by choco cut: " + dsc.getMean());

    int counterAboveLat = 0;
    final double[] chocoLatencies = dsc.getValues();
    for (final double cl : chocoLatencies) {
      if (heuristicLatency <= cl) {
        counterAboveLat++;
      }
    }
    final int percent = (counterAboveLat * 100) / chocoLatencies.length;
    PreesmLogger.getLogger()
        .info("Heuristic latency is better (or equal) than " + counterAboveLat + " cuts, i.e. " + percent + "%");

  }

  private static void printWCETstatistics(DescriptiveStatistics ds) {
    final long maxWCET = Math.round(ds.getMax());
    final long percentMax = Math.round((ds.getMax() / ds.getSum()) * 100);
    PreesmLogger.getLogger().log(Level.FINE,
        "Max WCET is " + maxWCET + " and represents " + percentMax + "% of the total.");
    final long meanWCET = Math.round(ds.getMean());
    final long percentMean = Math.round((ds.getMean() / ds.getSum()) * 100);
    PreesmLogger.getLogger().log(Level.FINE,
        "Mean WCET is " + meanWCET + " and represents " + percentMean + "% of the total.");
    final long standardDeviation = Math.round(ds.getStandardDeviation());
    final long percentSD = Math.round((ds.getStandardDeviation() / ds.getSum()) * 100);
    PreesmLogger.getLogger().log(Level.FINE,
        "Standard deviation of WCET is " + standardDeviation + " and represents " + percentSD + "% of the total.");
  }

  private static DescriptiveStatistics executeChocoModel(final PiGraph graphCopy, final Design architecture,
      final Scenario scenario, final int maxii, final HeuristicLoopBreakingDelays hlbd) {
    // final long time1 = System.nanoTime();
    final ChocoCutModel ccm = new ChocoCutModel(hlbd, maxii);
    final DescriptiveStatistics dsc = new DescriptiveStatistics();
    // final List<Map<FifoAbstraction, Integer>> chocoCuts = ccm.findAllCuts();
    // long duration = System.nanoTime() - time1;
    // PreesmLogger.getLogger().info("Time of choco model " + Math.round(duration / 1e6) + " ms.");
    final long time2 = System.nanoTime();
    Map<FifoAbstraction, Integer> bestDelays = null;
    long bestLatency = Long.MAX_VALUE;
    long bestMemory = Long.MAX_VALUE;
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
        long memCost = getCutMemoryCost(delays.keySet(), scenario);
        if (memCost < bestMemory) {
          bestLatency = latency;
          bestDelays = delays;
        }
      }
      setCut(delays, true);
      if (nbCutsTested % 1000 == 0) {
        System.err.println("1000 cuts tested.");
      }
    }
    long duration = System.nanoTime() - time2;
    PreesmLogger.getLogger().setLevel(backupLevel);
    PreesmLogger.getLogger().info("Number of cuts tested: " + nbCutsTested);
    PreesmLogger.getLogger().info("Time of choco tests " + Math.round(duration / 1e6) + " ms.");
    if (bestDelays != null) {
      setCut(bestDelays, false);
      final StringBuilder sb = new StringBuilder("\nAdded delays by choco:\n");
      for (Entry<FifoAbstraction, Integer> e : bestDelays.entrySet()) {
        final FifoAbstraction fa = e.getKey();
        final AbstractActor src = hlbd.absGraph.getEdgeSource(fa);
        final AbstractActor tgt = hlbd.absGraph.getEdgeTarget(fa);
        final int stages = e.getValue();
        sb.append(stages + " stages from " + src.getName() + " to " + tgt.getName() + "\n");
      }

      PreesmLogger.getLogger().info("Best Choco cut: " + sb.toString());
    }
    return dsc;
  }

  private static void setCut(final Map<FifoAbstraction, Integer> delays, final boolean reset) {

    for (Entry<FifoAbstraction, Integer> e : delays.entrySet()) {
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
          f.setDelay(delay);
          delay.setName(delay.getId());
          delay.getActor().setName(delay.getId());
          final PiGraph graphFifo = f.getContainingPiGraph();
          graphFifo.addDelay(delay);
          PreesmLogger.getLogger().log(Level.INFO, "Set fifo delay size and type of: " + f.getId());
        } else {
          if (!reset) {
            pipeSize += delay.getExpression().evaluate();
            PreesmLogger.getLogger().log(Level.WARNING, "Reset fifo delay size and type of: " + f.getId());
          } else {
            pipeSize = delay.getExpression().evaluate() - pipeSize;
          }
        }
        delay.setLevel(PersistenceLevel.PERMANENT);
        delay.setExpression(pipeSize);
      }
    }

  }

  private static long computeLatency(PiGraph graph, Scenario scenario, Design architecture, boolean printGantt) {
    final PiGraph dag = PiSDFToSingleRate.compute(graph, BRVMethod.LCM);
    final IScheduler scheduler = new PeriodicScheduler();
    final SynthesisResult scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
    // use implementation evaluation of PeriodicScheduler instead?
    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
    final LatencyCost evaluate = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    PreesmLogger.getLogger().log(Level.INFO, "Latency of graph with added delays: " + evaluate.getValue());

    if (printGantt) {
      final IStatGenerator statGen = new StatGeneratorSynthesis(architecture, scenario, scheduleAndMap.mapping, null,
          evaluate);
      final IEditorInput input = new StatEditorInput(statGen);

      // Check if the workflow is running in command line mode
      try {
        // Run statistic editor
        PlatformUI.getWorkbench().getDisplay().asyncExec(new EditorRunnable(input));
      } catch (final IllegalStateException e) {
        PreesmLogger.getLogger().log(Level.INFO, "Gantt display is impossible in this context."
            + " Ignore this log entry if you are running the command line version of Preesm.");
      }
    }
    return evaluate.getValue();
  }

  private static void fillCycles(final HeuristicLoopBreakingDelays hlbd, final Map<AbstractVertex, Long> brv) {
    for (FifoAbstraction fa : hlbd.breakingFifosAbs) {
      final List<Long> pipelineValues = fa.pipelineValues;
      final List<Fifo> fifos = fa.fifos;
      final int size = fifos.size();
      for (int i = 0; i < size; i++) {
        final Fifo f = fifos.get(i);
        long pipeSize = pipelineValues.get(i);
        final AbstractActor tgt = hlbd.absGraph.getEdgeTarget(fa);
        final long brvTgt = brv.get(tgt);
        final long brvTgtCycle = hlbd.minCycleBrv.get(tgt);
        if (brvTgt > brvTgtCycle) {
          pipeSize /= brvTgt;
          pipeSize *= brvTgtCycle;
        }

        Delay delay = f.getDelay();
        if (delay == null) {
          delay = PiMMUserFactory.instance.createDelay();
          f.setDelay(delay);
          delay.setName(delay.getId());
          delay.getActor().setName(delay.getId());
          final PiGraph graphFifo = f.getContainingPiGraph();
          graphFifo.addDelay(delay);
          PreesmLogger.getLogger().log(Level.INFO, "[in Cycle] Set fifo delay size and type of: " + f.getId());
        } else {
          final long prevSize = delay.getExpression().evaluate();
          pipeSize = Math.max(pipeSize, prevSize);
          if (pipeSize != prevSize) {
            PreesmLogger.getLogger().log(Level.WARNING, "[in Cycle] Reset fifo delay size and type of: " + f.getId());
          } else {
            PreesmLogger.getLogger().log(Level.INFO,
                "[in Cycle] Fiifo delay size of: " + f.getId() + " is already correct.");
          }
        }
        delay.setLevel(PersistenceLevel.PERMANENT);
        delay.setExpression(pipeSize);
      }
    }
  }

  private static SortedMap<Integer, Set<AbstractActor>> mapRankActors(final Map<AbstractActor, TopoVisit> topoRanks,
      boolean reverse, int maxRank) {
    final SortedMap<Integer, Set<AbstractActor>> irRankActors = new TreeMap<>();
    for (Entry<AbstractActor, TopoVisit> e : topoRanks.entrySet()) {
      final AbstractActor aa = e.getKey();
      final TopoVisit tv = e.getValue();
      int rank = tv.rank;
      if (reverse) {
        rank = maxRank - tv.rank;
      }
      Set<AbstractActor> aas = irRankActors.get(rank);
      if (aas == null) {
        aas = new HashSet<>();
        irRankActors.put(rank, aas);
      }
      aas.add(aa);
    }
    return irRankActors;
  }

  private static SortedMap<Integer, Set<CutInformation>> computePossibleCuts(
      final SortedMap<Integer, Set<AbstractActor>> irRankActors,
      final SortedMap<Integer, Set<AbstractActor>> irRankActorsT, final Map<AbstractActor, TopoVisit> topoRanks,
      final Map<AbstractActor, TopoVisit> topoRanksT, final Set<FifoAbstraction> forbiddenFifos, final int maxRank,
      final HeuristicLoopBreakingDelays hlbd) {
    final SortedMap<Integer, Set<CutInformation>> result = new TreeMap<>();

    final SortedMap<Integer,
        Set<FifoAbstraction>> crossingFifos = computeCrossingFifos(false, topoRanks, hlbd, maxRank, forbiddenFifos);
    for (Entry<Integer, Set<AbstractActor>> e : irRankActors.entrySet()) {
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

    for (Entry<Integer, Set<AbstractActor>> e : irRankActorsT.entrySet()) {
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
      for (Entry<Integer, Set<CutInformation>> e : result.entrySet()) {
        final int rank = e.getKey();
        PreesmLogger.getLogger().log(Level.FINE, "=====> Rank " + rank);
        for (CutInformation ci : e.getValue()) {
          Set<FifoAbstraction> fas = ci.edgeCut;
          final StringBuilder sb = new StringBuilder("Cut: \n");
          for (FifoAbstraction fa : fas) {
            AbstractActor src = hlbd.absGraph.getEdgeSource(fa);
            AbstractActor tgt = hlbd.absGraph.getEdgeTarget(fa);
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
    for (FifoAbstraction fa : graph.edgeSet()) {
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
        boolean isForbidden = forbiddenFifos.contains(fa);
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
    for (AbstractActor aa : aas) {
      for (FifoAbstraction fa : hlbd.absGraph.incomingEdgesOf(aa)) {
        AbstractActor src = hlbd.absGraph.getEdgeSource(fa);
        boolean selfLoop = src == aa;
        boolean forbiddenFifo = forbiddenFifos.contains(fa);
        if (!forbiddenFifo && !selfLoop) {
          fas.add(fa);
        } else if (hlbd.breakingFifosAbs.contains(fa) || selfLoop) {
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
    for (Set<CutInformation> cis : cuts.values()) {
      for (CutInformation ci : cis) {
        int nbActorsInCut = ci.edgeCut.size();
        if (nbActorsInCut > maxParallelActors) {
          maxParallelActors = nbActorsInCut;
        }
      }
    }
    PreesmLogger.getLogger().log(Level.FINE, "Max Actors in Parallel: " + maxParallelActors);

    // we divide by the number of maxii
    final long avgCutLoad = totC / (nbSelec + 1);
    final long maxLoad = totC - avgCutLoad;
    PreesmLogger.getLogger().log(Level.FINE, "Average cut load: " + avgCutLoad);
    int lastLoadIndex = 1;
    long currentLoad = 0;
    for (Entry<Integer, Long> e : rankWCETs.entrySet()) {
      if (currentLoad >= avgCutLoad * lastLoadIndex && (currentLoad <= maxLoad || preSelectedRanks.isEmpty())) {
        int rank = e.getKey();
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
    for (Entry<Integer, Long> e : rankWCETsT.entrySet()) {
      currentLoad += e.getValue();
      if (currentLoad >= avgCutLoad * lastLoadIndex && (currentLoad <= maxLoad || preSelectedRanks.size() < 2)) {
        int rank = e.getKey();
        if (!cuts.getOrDefault(rank, new HashSet<>()).isEmpty()) {
          preSelectedRanks.add(rank);
          lastLoadIndex += 1;
        }
      }
    }

    String strPreSelected = String.join(", ",
        preSelectedRanks.stream().map(i -> Integer.toString(i)).collect(Collectors.toList()));
    PreesmLogger.getLogger().log(Level.FINE, "Preselected cut ranks: " + strPreSelected);
    // select remaining cuts sorted by memory size
    final List<CutInformation> bestCuts = new ArrayList<>();
    for (int i : preSelectedRanks) {
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
      // selects cut which is closer to the midle
      CutInformation bestCut = null;
      long bestCutValue = 0;
      for (CutInformation ci : bestCuts) {
        long wcetDiff = Math.abs(avgCutLoad - getIntermediateWeightedWCET(1, ci.rank, rankWCETs));
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
      CutSizeComparator csc = new CutSizeComparator();
      Set<CutInformation> cisToRemove = new HashSet<>();
      final int bcSize = bestCuts.size();
      for (int i = bcSize - 1; i >= 0; i--) {
        CutInformation ci1 = bestCuts.get(i);
        if (bcSize - cisToRemove.size() <= nbSelec) {
          break;
        }
        // traversal in reverse order since we want to get
        // rid of large cut first
        for (int j = i - 1; j >= 0; j--) {
          if (bcSize - cisToRemove.size() <= nbSelec) {
            break;
          }
          CutInformation ci2 = bestCuts.get(j);
          long wcetDiff = getIntermediateWeightedWCET(ci1.rank, ci2.rank, rankWCETs);
          if (wcetDiff < avgCutLoad) {
            int memComparison = csc.compare(ci1, ci2);
            if (memComparison < 0) {
              cisToRemove.add(ci2);
            } else if (memComparison > 0) {
              cisToRemove.add(ci1);
            } else {
              // breaks tie with lower rank
              // (otherwise it is not fully transitive, and we might remove all ci ...)
              int rankComparison = Integer.compare(ci1.rank, ci2.rank);
              if (rankComparison < 0) {
                cisToRemove.add(ci2);
              } else {
                cisToRemove.add(ci1);
              }
            }
          }
        }
      }
      String strRemovedClose = String.join(", ",
          cisToRemove.stream().map(x -> x.getRankStr()).collect(Collectors.toList()));
      PreesmLogger.getLogger().log(Level.FINE, "Removed too close cut ranks: " + strRemovedClose);

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
    SimulationInfo si = scenar.getSimulationInfo();
    for (FifoAbstraction fa : fas) {
      final List<Long> pipelineValues = fa.pipelineValues;
      final List<Fifo> fifos = fa.fifos;
      final int size = fifos.size();
      for (int i = 0; i < size; i++) {
        final Fifo f = fifos.get(i);
        final long dataSize = si.getDataTypeSizeOrDefault(f.getType());
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
      String reverseInfo = wasReversed ? "T" : "";
      return rank + reverseInfo;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof CutInformation) {
        CutInformation ci = (CutInformation) o;
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
      long size0 = arg0.memSize;
      long size1 = arg1.memSize;
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
