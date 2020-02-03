package org.preesm.algorithm.pisdf.autodelays;

import java.util.Collections;
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
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
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
        @Parameter(name = AutoDelaysTask.MAXII_PARAM_NAME,
            values = { @Value(name = AutoDelaysTask.MAXII_PARAM_VALUE,
                effect = "Maximum number of graph cuts induced by the added delays.") }),
        @Parameter(name = AutoDelaysTask.CYCLES_PARAM_NAME, values = { @Value(name = AutoDelaysTask.CYCLES_PARAM_VALUE,
            effect = "Whether or not the task must also break the cycles with delays.") }) }

)
public class AutoDelaysTask extends AbstractTaskImplementation {

  public static final String MAXII_PARAM_NAME   = "Maximum cuts";
  public static final String MAXII_PARAM_VALUE  = "4";
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

    final String maxiiStr = parameters.get(MAXII_PARAM_NAME);
    int maxii = nbCore;
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
    boolean cycles = Boolean.parseBoolean(cyclesStr);
    if (cycles) {
      PreesmLogger.getLogger().log(Level.WARNING,
          "Cycles cannot be broken automatically yet, option without any effect.");
      // TODO modify HeuristicLoopBreakingDelays to precompute
      // the possible delay values in FifoAbstraction
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
    final SortedMap<Integer, Long> rankWCETs = new TreeMap<>();
    for (Entry<AbstractActor, TopoVisit> e : topoRanks.entrySet()) {
      AbstractActor aa = e.getKey();
      TopoVisit tv = e.getValue();
      long tWCET = brv.get(aa) * wcets.get(aa);
      long prev = rankWCETs.getOrDefault(tv.rank, 0L);
      rankWCETs.put(tv.rank, tWCET + prev);
    }
    // offset of one to ease next computation
    final int maxRank = rankWCETs.lastKey() + 1;
    final Map<AbstractActor,
        TopoVisit> topoRanksT = TopologicalRanking.topologicalASAPrankingT(sinkActors, hlbd.actorsNbVisitsTopoRankT);
    // reorder topoRanksT with same order as topoRanks
    for (Entry<AbstractActor, TopoVisit> e : topoRanksT.entrySet()) {
      AbstractActor aa = e.getKey();
      TopoVisit tv = e.getValue();
      long tWCET = brv.get(aa) * wcets.get(aa);
      int rank = maxRank - tv.rank;
      long prev = rankWCETs.getOrDefault(rank, 0L);
      rankWCETs.put(rank, tWCET + prev);
    }
    // as loads are counted 2 times if one same rank, divide by 2
    long totC = 0L;
    for (Entry<Integer, Long> e : rankWCETs.entrySet()) {
      int rank = e.getKey();
      long load = e.getValue() / 2;
      totC += load;
      rankWCETs.put(rank, load);
    }

    // compute possible cuts
    SortedMap<Integer, Set<Set<FifoAbstraction>>> possibleCuts = computePossibleCuts(topoRanks, topoRanksT,
        forbiddenFifos, maxRank, hlbd);

    Set<Set<FifoAbstraction>> bestCuts = selectBestCuts(possibleCuts, maxii, rankWCETs, totC);
    System.err.println("nb possible cuts: " + possibleCuts.size());
    System.err.println("max tank: " + maxRank);

    // select the relevant cuts

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graphCopy);
    return output;
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

  private static SortedMap<Integer, Set<Set<FifoAbstraction>>> computePossibleCuts(
      final Map<AbstractActor, TopoVisit> topoRanks, final Map<AbstractActor, TopoVisit> topoRanksT,
      final Set<FifoAbstraction> forbiddenFifos, final int maxRank, final HeuristicLoopBreakingDelays hlbd) {
    final SortedMap<Integer, Set<Set<FifoAbstraction>>> result = new TreeMap<>();
    // build intermediate list of actors per rank
    final SortedMap<Integer, Set<AbstractActor>> irRankActors = new TreeMap<>();
    for (Entry<AbstractActor, TopoVisit> e : topoRanks.entrySet()) {
      final AbstractActor aa = e.getKey();
      final TopoVisit tv = e.getValue();
      final int rank = tv.rank;
      if (rank > 1) {
        Set<AbstractActor> aas = irRankActors.get(rank);
        if (aas == null) {
          aas = new HashSet<>();
          irRankActors.put(rank, aas);
        }
        aas.add(aa);
      }
    }
    final SortedMap<Integer, Set<AbstractActor>> irRankActorsT = new TreeMap<>();
    for (Entry<AbstractActor, TopoVisit> e : topoRanksT.entrySet()) {
      final AbstractActor aa = e.getKey();
      final TopoVisit tv = e.getValue();
      final int rank = maxRank - tv.rank;
      if (rank > 1) {
        Set<AbstractActor> aas = irRankActorsT.get(rank);
        if (aas == null) {
          aas = new HashSet<>();
          irRankActorsT.put(rank, aas);
        }
        aas.add(aa);
      }
    }

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
    for (int i = 2; i < maxRank; i++) {
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
        boolean forbiddenFifo = forbiddenFifos.contains(fa);
        if (!forbiddenFifo) {
          fas.add(fa);
        } else if (hlbd.breakingFifosAbs.contains(fa)) {
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

  private static Set<Set<FifoAbstraction>> selectBestCuts(SortedMap<Integer, Set<Set<FifoAbstraction>>> cuts,
      final int maxii, final SortedMap<Integer, Long> rankWCETs, final long totC) {
    final Set<Set<FifoAbstraction>> result = new HashSet<>();
    final Set<Integer> preSelectedRanks = new HashSet<>();
    // we divide by the number of maxii
    final long avgCutLoad = totC / (maxii + 1);
    System.err.println("avg load cut : " + avgCutLoad);
    int lastLoadIndex = 0;
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
    lastLoadIndex = 0;
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

    for (int i : preSelectedRanks) {
      System.err.println("Best rank: " + i);
    }

    // select remaining cuts sorted by memory size

    return result;
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
