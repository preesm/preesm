package org.preesm.algorithm.pisdf.autodelays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;

/**
 * Computes one stage pipeline in a graph
 * 
 * @author ahonorat
 */
public class ChocoCutModel {

  private final DefaultDirectedGraph<AbstractActor, FifoAbstraction> dag;
  private final Set<FifoAbstraction>                                 fixedFifos;
  private final Map<AbstractActor, Integer>                          mapVertices;
  private final Map<FifoAbstraction, Integer>                        mapEdges;

  private final Model    model;
  private final IntVar[] vertexDelays;
  private final IntVar[] edgeDelays;
  private final Solver   solver;

  public static final long TIMEOUT      = 3600000L; // 1h in ms
  public static final long MAXSOLUTIONS = 1000000L;

  /**
   * Initializes the Choco model.
   * 
   * @param hlbd
   *          Abstract graph, breaking fifos and cycles informations.
   */
  public ChocoCutModel(final HeuristicLoopBreakingDelays hlbd, final int nbStages) {
    if (nbStages < 1) {
      throw new PreesmRuntimeException("Number of stages must be a positive integer in the Choco model.");
    }

    // prepare graph structure
    dag = AbstractGraph.copyGraph(hlbd.absGraph);
    dag.removeAllEdges(hlbd.breakingFifosAbs);
    // remove self-loops
    for (AbstractActor aa : dag.vertexSet()) {
      dag.removeEdge(aa, aa);
    }
    fixedFifos = hlbd.getForbiddenFifos();
    // create model
    final int nbActors = dag.vertexSet().size();
    final int nbFifos = dag.edgeSet().size();

    mapVertices = new HashMap<>();
    mapEdges = new HashMap<>();

    model = new Model(nbStages + "-stages pipeline cuts of a graph.");
    vertexDelays = new IntVar[nbActors];
    edgeDelays = new IntVar[nbFifos];

    // initialize actor delays
    for (AbstractActor aa : dag.vertexSet()) {
      final int index = mapVertices.size();
      mapVertices.put(aa, index);
      if (dag.inDegreeOf(aa) == 0) {
        vertexDelays[index] = model.intVar(0);
      } else {
        vertexDelays[index] = model.intVar(0, nbStages);
      }
    }
    // initialize fifo delays
    for (FifoAbstraction fa : dag.edgeSet()) {
      final int index = mapEdges.size();
      mapEdges.put(fa, index);
      if (fixedFifos.contains(fa)) {
        edgeDelays[index] = model.intVar(0);
      } else {
        edgeDelays[index] = model.intVar(0, nbStages);
      }
    }
    // main equation per incoming fifo
    for (AbstractActor aa : dag.vertexSet()) {
      final int inDegree = dag.inDegreeOf(aa);
      if (inDegree > 0) {
        final IntVar[] actorInConstraints = new IntVar[1 + inDegree];
        actorInConstraints[0] = vertexDelays[mapVertices.get(aa)];
        int i = 1;
        for (FifoAbstraction fa : dag.incomingEdgesOf(aa)) {
          final IntVar dst = model.intVar(0, nbStages);
          actorInConstraints[i++] = dst;
          final IntVar src = vertexDelays[mapVertices.get(dag.getEdgeSource(fa))];
          final IntVar edge = edgeDelays[mapEdges.get(fa)];
          model.arithm(src, "+", edge, "=", dst).post();
        }
        model.allEqual(actorInConstraints).post();
      }
    }
    // force at least one output to be nbStages
    final IntVar maxActorDelay = model.intVar(0, nbStages);
    model.max(maxActorDelay, vertexDelays).post();
    model.arithm(maxActorDelay, "=", nbStages).post();

    // set solver
    solver = model.getSolver();
    solver.limitTime(TIMEOUT);
    solver.limitSolution(MAXSOLUTIONS);

  }

  /**
   * Computes all solutions
   */
  public List<Map<FifoAbstraction, Integer>> findAllCuts() {
    final List<Solution> solutions = solver.findAllSolutions();

    if (solver.isStopCriterionMet()) {
      if (solutions.size() == MAXSOLUTIONS) {
        PreesmLogger.getLogger().warning("Reached MAX SOLUIONS: " + MAXSOLUTIONS);
      } else {
        PreesmLogger.getLogger().warning("Reached TIME OUT: " + TIMEOUT + " ms");
      }
    }
    PreesmLogger.getLogger().info("Number of cuts found by Choco: " + solutions.size());

    final List<Map<FifoAbstraction, Integer>> result = new ArrayList<>();

    for (Solution sol : solutions) {
      Map<FifoAbstraction, Integer> delays = new HashMap<>();
      for (FifoAbstraction fa : dag.edgeSet()) {
        final int delay = sol.getIntVal(edgeDelays[mapEdges.get(fa)]);
        if (delay > 0) {
          delays.put(fa, delay);
        }
      }
      result.add(delays);
    }
    return result;
  }

  /**
   * Computes next solution
   */
  public Map<FifoAbstraction, Integer> findNextCut() {

    final boolean found = solver.solve();

    if (solver.isStopCriterionMet()) {
      PreesmLogger.getLogger().warning("Reached TIME OUT or MAX_SOLUTIONS");
      return null;
    }

    Solution sol = null;
    if (found) {
      sol = new Solution(model).record();
    } else {
      return null; // all solutions have been found
    }

    Map<FifoAbstraction, Integer> cut = new HashMap<>();
    for (FifoAbstraction fa : dag.edgeSet()) {
      final int delay = sol.getIntVal(edgeDelays[mapEdges.get(fa)]);
      if (delay > 0) {
        cut.put(fa, delay);
      }
    }
    return cut;
  }

}
