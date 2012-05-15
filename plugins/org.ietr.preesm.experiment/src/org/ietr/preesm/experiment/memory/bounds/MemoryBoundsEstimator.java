package org.ietr.preesm.experiment.memory.bounds;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.experiment.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.experiment.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;

/**
 * Workflow element that takes a MemoryExclusionGraph as input and computes its memory bounds.
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
		
		double density = memEx.edgeSet().size()/(memEx.vertexSet().size()*(memEx.vertexSet().size() -1)/2.0 );
		if(verbose)
			logger.log(Level.INFO, "Memory exclusion graph with "+memEx.vertexSet().size()+" vertices and density = "+density);
		
		
		// Derive bounds
		AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver = null;
		if (valueSolver.equals(VALUE_SOLVER_HEURISTIC)) {
			solver = new HeuristicSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
		}
		if (valueSolver.equals(VALUE_SOLVER_OSTERGARD)) {
			solver = new OstergardSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
		}
		if (valueSolver.equals(VALUE_SOLVER_YAMAGUCHI)) {
			solver = new YamaguchiSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
		}
		if (solver == null) {
			solver = new HeuristicSolver<MemoryExclusionVertex, DefaultEdge>(
					memEx);
		}
		
		if(verbose){
			logger.log(Level.INFO, "Maximum-Weight Clique Problem : start solving");
		}
		
		solver.solve();		
		int minBound = solver.sumWeight(solver.getHeaviestClique());
		int maxBound = solver.sumWeight(memEx.vertexSet());
		
		logger.log(Level.INFO, "Bound_Max = "+ maxBound +" Bound_Min = "+ minBound);

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
