/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos

[mpelcat,jnezan,kdesnos]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.memory.bounds;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.memory.allocation.MemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemExBroadcastMerger;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;

/**
 * Workflow element that takes a MemoryExclusionGraph as input and computes its
 * memory bounds. It outputs the unmodified MemEx as well as the input and
 * output bounds found.
 * 
 * @author kdesnos
 * 
 */
public class MemoryBoundsEstimator extends AbstractTaskImplementation {

	static final public String PARAM_SOLVER = "Solver";
	static final public String VALUE_SOLVER_DEFAULT = "? C {Heuristic, Ostergard, Yamaguchi}";
	static final public String VALUE_SOLVER_OSTERGARD = "Ostergard";
	static final public String VALUE_SOLVER_YAMAGUCHI = "Yamaguchi";
	static final public String VALUE_SOLVER_HEURISTIC = "Heuristic";

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";

	static final public String OUTPUT_KEY_BOUND_MIN = "BoundMin";
	static final public String OUTPUT_KEY_BOUND_MAX = "BoundMax";
	static final public String OUTPUT_KEY_MEM_EX = "MemEx";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		// Check Workflow element parameters
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		boolean verbose;
		verbose = valueVerbose.equals(VALUE_VERBOSE_TRUE);

		String valueSolver = parameters.get(PARAM_SOLVER);
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

		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");
		
		// Check if the broadcast were merged in the past
		String merged = memEx.getPropertyStringValue(MemoryAllocatorTask.BROADCAST_MERGED_PROPERTY);
		MemExBroadcastMerger merger = null;
		if(merged!=null && merged.equalsIgnoreCase("true")){
			int nbBefore = memEx.vertexSet().size();
			if(verbose){
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

		// Derive bounds
		AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver = null;

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

		if (verbose) {
			logger.log(Level.INFO,
					"Maximum-Weight Clique Problem : start solving");
		}

		solver.solve();
		int minBound = solver.sumWeight(solver.getHeaviestClique());
		int maxBound = solver.sumWeight(memEx.vertexSet());

		logger.log(Level.INFO, "Bound_Max = " + maxBound + " Bound_Min = "
				+ minBound);
		System.out.println(minBound+";");
		
		// unmerge
		if(merger != null){
			merger.unmerge();
		}

		// Generate output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(OUTPUT_KEY_BOUND_MAX, maxBound);
		output.put(OUTPUT_KEY_BOUND_MIN, minBound);
		output.put(OUTPUT_KEY_MEM_EX, memEx);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_SOLVER, VALUE_SOLVER_DEFAULT);
		parameters.put(PARAM_VERBOSE, VALUE_VERBOSE_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Estimating Memory Bounds";
	}

}
