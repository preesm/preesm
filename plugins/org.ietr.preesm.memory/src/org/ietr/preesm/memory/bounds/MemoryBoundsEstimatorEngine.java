/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.memory.bounds;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
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
	private boolean verbose;
	private AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver;

	public MemoryBoundsEstimatorEngine(MemoryExclusionGraph memEx,
			String valueVerbose) {
		this.memEx = memEx;
		this.verbose = valueVerbose.contains(VALUE_VERBOSE_TRUE);
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
}
