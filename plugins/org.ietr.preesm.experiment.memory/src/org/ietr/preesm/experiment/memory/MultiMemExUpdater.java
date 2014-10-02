package org.ietr.preesm.experiment.memory;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.preesm.memory.exclusiongraph.AbstractMemExUpdater;
import org.ietr.preesm.memory.exclusiongraph.MemExUpdaterEngine;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class MultiMemExUpdater extends AbstractMemExUpdater {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Check Workflow element parameters
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		boolean verbose = valueVerbose.equals(VALUE_TRUE);

		String valueLifetime = parameters.get(PARAM_LIFETIME);
		boolean lifetime = valueLifetime.equals(VALUE_TRUE);

		String valueSupprForkJoin = parameters.get(PARAM_SUPPR_FORK_JOIN);
		boolean forkJoin = valueSupprForkJoin.equals(VALUE_TRUE);

		// Retrieve the input of the task
		@SuppressWarnings("unchecked")
		Map<DirectedAcyclicGraph, MemoryExclusionGraph> dagsAndMemExsInput = (Map<DirectedAcyclicGraph, MemoryExclusionGraph>) inputs
				.get(KEY_DAG_AND_MEM_EX_MAP);
		
		for (DirectedAcyclicGraph dag : dagsAndMemExsInput.keySet()) {
			MemoryExclusionGraph memEx = dagsAndMemExsInput.get(dag);
			MemExUpdaterEngine engine = new MemExUpdaterEngine(dag, memEx,
					verbose);
			engine.createLocalDag(forkJoin);
			engine.update(lifetime);
		}
		// Generate output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(KEY_DAG_AND_MEM_EX_MAP, dagsAndMemExsInput);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_TRUE_FALSE_DEFAULT);
		parameters.put(PARAM_LIFETIME, VALUE_TRUE_FALSE_DEFAULT);
		parameters.put(PARAM_SUPPR_FORK_JOIN, VALUE_TRUE_FALSE_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Updating MemEx";
	}

}
