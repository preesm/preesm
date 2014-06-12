package org.ietr.preesm.memory.bounds;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.allocation.MemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemExBroadcastMerger;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;

public class MemoryBoundsEstimatorEngine {

	static final public String PARAM_SOLVER = "Solver";
	static final public String VALUE_SOLVER_DEFAULT = "? C {Heuristic, Ostergard, Yamaguchi}";
	static final public String VALUE_SOLVER_OSTERGARD = "Ostergard";
	static final public String VALUE_SOLVER_YAMAGUCHI = "Yamaguchi";
	static final public String VALUE_SOLVER_HEURISTIC = "Heuristic";

	static final public String VALUE_VERBOSE_TRUE = "True";

	// Rem: Logger is used to display messages in the console
	private Logger logger = WorkflowLogger.getLogger();
	private MemoryExclusionGraph memEx;
	private String valueVerbose;
	private boolean verbose;
	private AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver;
	private MemExBroadcastMerger merger;

	public MemoryBoundsEstimatorEngine(MemoryExclusionGraph memEx,
			String valueVerbose) {
		this.memEx = memEx;
		this.valueVerbose = valueVerbose;
		this.verbose = valueVerbose.contains(VALUE_VERBOSE_TRUE);
	}

	public void mergeBroadcasts() {
		// Check if the broadcast were merged in the past
		String merged = memEx
				.getPropertyStringValue(MemoryAllocatorTask.BROADCAST_MERGED_PROPERTY);
		if (merged != null && merged.equalsIgnoreCase("true")
				&& !valueVerbose.contains("NO_MERGE")) {
			int nbBefore = memEx.vertexSet().size();
			if (verbose) {
				logger.log(Level.INFO,
						"Merging broadcast edges (when possible).");
			}
			merger = new MemExBroadcastMerger(memEx);
			int nbBroadcast = merger.merge();

			if (verbose) {
				logger.log(Level.INFO, "Merging broadcast: " + nbBroadcast
						+ " were mergeable for a total of "
						+ (nbBefore - memEx.vertexSet().size())
						+ " memory objects.");
			}
		}

		double density = memEx.edgeSet().size()
				/ (memEx.vertexSet().size() * (memEx.vertexSet().size() - 1) / 2.0);
		if (verbose)
			logger.log(Level.INFO, "Memory exclusion graph with "
					+ memEx.vertexSet().size() + " vertices and density = "
					+ density);

	}

	public void selectSolver(String valueSolver) {
		if (verbose) {
			if (valueSolver.equals(VALUE_SOLVER_DEFAULT)) {
				logger.log(Level.INFO,
						"No solver specified. Heuristic solver used by default.");
			} else {
				if (valueSolver.equals(VALUE_SOLVER_HEURISTIC)
						|| valueSolver.equals(VALUE_SOLVER_OSTERGARD)
						|| valueSolver.equals(VALUE_SOLVER_YAMAGUCHI)) {
					logger.log(Level.INFO, valueSolver + " solver used.");
				} else {
					logger.log(Level.INFO, "Incorrect solver :" + valueSolver
							+ ". Heuristic solver used by default.");
				}
			}
		}
		switch (valueSolver) {
		case VALUE_SOLVER_HEURISTIC:
			solver = new HeuristicSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
			break;
		case VALUE_SOLVER_OSTERGARD:
			solver = new OstergardSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
			break;
		case VALUE_SOLVER_YAMAGUCHI:
			solver = new YamaguchiSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
			break;
		default:
			solver = new HeuristicSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
		}
	}

	public void solve() {
		if (verbose) {
			logger.log(Level.INFO,
					"Maximum-Weight Clique Problem : start solving");
		}

		solver.solve();
	}

	public int getMinBound() {
		return solver.sumWeight(solver.getHeaviestClique());
	}

	public int getMaxBound() {
		return solver.sumWeight(memEx.vertexSet());
	}

	public void unmerge() {
		// unmerge
		if (merger != null) {
			merger.unmerge();
		}
	}

}
