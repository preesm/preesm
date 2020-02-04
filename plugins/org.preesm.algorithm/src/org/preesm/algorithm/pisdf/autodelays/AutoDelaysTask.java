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
import org.eclipse.core.runtime.IProgressMonitor;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
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

    final HeuristicLoopBreakingDelays hlbd = new HeuristicLoopBreakingDelays();
    hlbd.performAnalysis(graphCopy, brv);

    if (cycles) {
      PreesmLogger.getLogger().log(Level.WARNING, "Experimental breaking of cycles.");
      fillCycles(hlbd, brv);
      // we redo the analysis since adding delays will modify the topo ranks
      hlbd.performAnalysis(graphCopy, brv);
    }

    if (maxii <= 0) {
      PreesmLogger.getLogger().log(Level.INFO, "0 cuts asked, nothing to do.");
      output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
      return output;
    }

    // intermediate data : forbidden fifos (in cycles), ranks, wcet(rank)

    final Set<FifoAbstraction> forbiddenFifos = getForbiddenFifos(hlbd);

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
      long tWCET = (long) Math.ceil((double) brv.get(aa) / nbCore) * wcets.get(aa);
      long prev = rankWCETs.getOrDefault(tv.rank, 0L);
      rankWCETs.put(tv.rank, tWCET + prev);
    }
    // reorder topoRanksT with same order as topoRanks
    for (Entry<AbstractActor, TopoVisit> e : topoRanksT.entrySet()) {
      AbstractActor aa = e.getKey();
      TopoVisit tv = e.getValue();
      long tWCET = (long) Math.ceil((double) brv.get(aa) / nbCore) * wcets.get(aa);
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
    SortedMap<Integer, Set<Set<FifoAbstraction>>> possibleCuts = computePossibleCuts(irRankActors, irRankActorsT,
        topoRanks, topoRanksT, forbiddenFifos, maxRank, hlbd);

    List<Set<FifoAbstraction>> bestCuts = selectBestCuts(possibleCuts, selec, rankWCETs, totC, scenario);

    System.err.println("Minimum mem: " + getCutMemoryCost(bestCuts.get(0), scenario));
    // set the cuts
    final int nbCuts = Math.min(bestCuts.size(), maxii);
    for (int index = 0; index < nbCuts; index++) {
      // set the graph
      for (FifoAbstraction fa : bestCuts.get(index)) {
        final List<Long> pipelineValues = fa.pipelineValues;
        final List<Fifo> fifos = fa.fifos;
        final int size = fifos.size();
        for (int i = 0; i < size; i++) {
          final Fifo f = fifos.get(i);
          long pipeSize = pipelineValues.get(i);
          Delay delay = f.getDelay();
          if (delay == null) {
            delay = PiMMUserFactory.instance.createDelay();
            f.setDelay(delay);
            delay.setName(delay.getId());
            delay.getActor().setName(delay.getId());
            final PiGraph graphFifo = f.getContainingPiGraph();
            graphFifo.addDelay(delay);
            PreesmLogger.getLogger().log(Level.INFO, "Set fifo delay size and type of: " + f.getId());
          } else {
            pipeSize += delay.getExpression().evaluate();
            PreesmLogger.getLogger().log(Level.WARNING, "Reset fifo delay size and type of: " + f.getId());
          }
          delay.setLevel(PersistenceLevel.PERMANENT);
          delay.setExpression(pipeSize);
        }
      }

    }

    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graphCopy);
    return output;
  }

  private static void fillCycles(final HeuristicLoopBreakingDelays hlbd, final Map<AbstractVertex, Long> brv) {
    for (FifoAbstraction fa : hlbd.breakingFifosAbs) {
      final List<Long> pipelineValues = fa.pipelineValues;
      final List<Fifo> fifos = fa.fifos;
      final int size = fifos.size();
      for (int i = 0; i < size; i++) {
        final Fifo f = fifos.get(i);
        long pipeSize = pipelineValues.get(i);
        AbstractActor tgt = hlbd.absGraph.getEdgeTarget(fa);
        long brvTgt = brv.get(tgt);
        long brvTgtCycle = hlbd.minCycleBrv.get(tgt);
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
          pipeSize = Math.max(pipeSize, delay.getExpression().evaluate());
          PreesmLogger.getLogger().log(Level.WARNING, "[in Cycle] Reset fifo delay size and type of: " + f.getId());
        }
        delay.setLevel(PersistenceLevel.PERMANENT);
        delay.setExpression(pipeSize);
      }
    }
  }

  private static Set<FifoAbstraction> getForbiddenFifos(final HeuristicLoopBreakingDelays hlbd) {
    final Set<FifoAbstraction> forbiddenFifos = new HashSet<>();
    for (List<AbstractActor> cycle : hlbd.cycles) {
      if (cycle.size() < 2) {
        continue;
      }
      AbstractActor lastA = cycle.get(cycle.size() - 1);
      for (AbstractActor aa : cycle) {
        final FifoAbstraction fa = hlbd.absGraph.getEdge(lastA, aa);
        forbiddenFifos.add(fa);
        lastA = aa;
      }
    }
    return forbiddenFifos;
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

  private static SortedMap<Integer, Set<Set<FifoAbstraction>>> computePossibleCuts(
      final SortedMap<Integer, Set<AbstractActor>> irRankActors,
      final SortedMap<Integer, Set<AbstractActor>> irRankActorsT, final Map<AbstractActor, TopoVisit> topoRanks,
      final Map<AbstractActor, TopoVisit> topoRanksT, final Set<FifoAbstraction> forbiddenFifos, final int maxRank,
      final HeuristicLoopBreakingDelays hlbd) {
    final SortedMap<Integer, Set<Set<FifoAbstraction>>> result = new TreeMap<>();

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
          Set<Set<FifoAbstraction>> fass = result.get(rank);
          if (fass == null) {
            fass = new HashSet<>();
            result.put(rank, fass);
          }
          fass.add(fas);
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
          Set<Set<FifoAbstraction>> fass = result.get(rank);
          if (fass == null) {
            fass = new HashSet<>();
            result.put(rank, fass);
          }
          fass.add(fas);
        }
      }
    }

    if (PreesmLogger.getLogger().isLoggable(Level.FINE)) {
      for (Entry<Integer, Set<Set<FifoAbstraction>>> e : result.entrySet()) {
        final int rank = e.getKey();
        PreesmLogger.getLogger().log(Level.FINE, "=====> Rank " + rank);
        for (Set<FifoAbstraction> fas : e.getValue()) {
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

  private static List<Set<FifoAbstraction>> selectBestCuts(SortedMap<Integer, Set<Set<FifoAbstraction>>> cuts,
      final int nbSelec, final SortedMap<Integer, Long> rankWCETs, final long totC, final Scenario scenar) {
    final Set<Integer> preSelectedRanks = new LinkedHashSet<>();
    // we divide by the number of maxii
    final long avgCutLoad = totC / (nbSelec + 1);
    int lastLoadIndex = 1;
    long currentLoad = 0;
    for (Entry<Integer, Long> e : rankWCETs.entrySet()) {
      currentLoad += e.getValue();
      if (currentLoad > avgCutLoad * lastLoadIndex) {
        boolean bothEmpty = true;
        int rank = e.getKey();
        if (!cuts.getOrDefault(rank, new HashSet<>()).isEmpty()) {
          bothEmpty = false;
          preSelectedRanks.add(rank);
        }
        if (rank < cuts.lastKey()) {
          rank += 1;
          if (!cuts.getOrDefault(rank, new HashSet<>()).isEmpty()) {
            bothEmpty = false;
            preSelectedRanks.add(rank);
          }
        }

        if (!bothEmpty) {
          lastLoadIndex += 1;
        }
      }
    }

    // same in the reverse order
    final SortedMap<Integer, Long> rankWCETsT = new TreeMap<>(Collections.reverseOrder());
    rankWCETsT.putAll(rankWCETs);
    lastLoadIndex = 1;
    currentLoad = 0;
    for (Entry<Integer, Long> e : rankWCETs.entrySet()) {
      currentLoad += e.getValue();
      if (currentLoad > avgCutLoad * lastLoadIndex) {
        boolean bothEmpty = true;
        int rank = e.getKey();
        if (!cuts.getOrDefault(rank, new HashSet<>()).isEmpty()) {
          bothEmpty = false;
          preSelectedRanks.add(rank);
        }
        if (rank > cuts.firstKey()) {
          rank -= 1;
          if (!cuts.getOrDefault(rank, new HashSet<>()).isEmpty()) {
            bothEmpty = false;
            preSelectedRanks.add(rank);
          }
        }

        if (!bothEmpty) {
          lastLoadIndex += 1;
        }
      }
    }

    String strPreSelected = String.join(", ",
        preSelectedRanks.stream().map(i -> Integer.toString(i)).collect(Collectors.toList()));
    PreesmLogger.getLogger().log(Level.FINE, "Preselected cut ranks: " + strPreSelected);
    // select remaining cuts sorted by memory size
    List<Set<FifoAbstraction>> bestCuts = new ArrayList<>();
    for (int i : preSelectedRanks) {
      List<Set<FifoAbstraction>> twoPossibilities = new ArrayList<>(cuts.get(i));
      if (twoPossibilities.size() == 1) {
        bestCuts.add(twoPossibilities.get(0));
      } else if (twoPossibilities.size() == 2) {
        if (getCutMemoryCost(twoPossibilities.get(0), scenar) < getCutMemoryCost(twoPossibilities.get(1), scenar)) {
          bestCuts.add(twoPossibilities.get(0));
        } else {
          bestCuts.add(twoPossibilities.get(1));
        }
      }
    }

    bestCuts.sort(new CutSizeComparator(scenar));

    return bestCuts;
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
   * Comparator of graph cuts, in ascending order of memory size
   * 
   * @author ahonorat
   */
  public static class CutSizeComparator implements Comparator<Set<FifoAbstraction>> {

    private final Scenario scenar;

    private CutSizeComparator(Scenario scenar) {
      this.scenar = scenar;
    }

    @Override
    public int compare(Set<FifoAbstraction> arg0, Set<FifoAbstraction> arg1) {
      long size0 = getCutMemoryCost(arg0, scenar);
      long size1 = getCutMemoryCost(arg1, scenar);
      return Long.compare(size0, size1);
    }

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MAXII_PARAM_NAME, MAXII_PARAM_VALUE);
    parameters.put(CYCLES_PARAM_NAME, CYCLES_PARAM_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computes delays to speed up the graph execution.";
  }

}
