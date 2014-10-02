package org.ietr.preesm.experiment.memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.bounds.AbstractMemoryBoundsEstimator;
import org.ietr.preesm.memory.bounds.MemoryBoundsEstimatorEngine;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class MultiMemoryBoundsEstimator extends AbstractMemoryBoundsEstimator {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		// Check Workflow element parameters
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		String valueSolver = parameters.get(PARAM_SOLVER);

		@SuppressWarnings("unchecked")
		Map<DirectedAcyclicGraph, MemoryExclusionGraph> dagsAndMemExs = (Map<DirectedAcyclicGraph, MemoryExclusionGraph>) inputs
				.get(KEY_DAG_AND_MEM_EX_MAP);

		Set<Integer> minBounds = new HashSet<Integer>();
		Set<Integer> maxBounds = new HashSet<Integer>();
		
		for (MemoryExclusionGraph memEx : dagsAndMemExs.values()) {
			MemoryBoundsEstimatorEngine engine = new MemoryBoundsEstimatorEngine(
					memEx, valueVerbose);
			engine.mergeBroadcasts();
			engine.selectSolver(valueSolver);
			engine.solve();

			int minBound = engine.getMinBound();

			int maxBound = engine.getMaxBound();

			logger.log(Level.INFO, "Bound_Max = " + maxBound + " Bound_Min = "
					+ minBound);
			System.out.println(minBound + ";");

			engine.unmerge();
			
			minBounds.add(minBound);
			maxBounds.add(maxBound);
		}
		// Generate output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(KEY_BOUND_MAX_SET, maxBounds);
		output.put(KEY_BOUND_MIN_SET, minBounds);
		output.put(KEY_DAG_AND_MEM_EX_MAP, dagsAndMemExs);
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
