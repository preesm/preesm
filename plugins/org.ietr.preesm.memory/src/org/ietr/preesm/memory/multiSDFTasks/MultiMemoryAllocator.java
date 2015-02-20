package org.ietr.preesm.memory.multiSDFTasks;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.allocation.MemoryAllocator;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

public class MultiMemoryAllocator extends AbstractMemoryAllocatorTask {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		this.init(parameters);

		// Retrieve the input of the task
		@SuppressWarnings("unchecked")
		Map<DirectedAcyclicGraph, MemoryExclusionGraph> dagsAndMemExs = (Map<DirectedAcyclicGraph, MemoryExclusionGraph>) inputs
				.get(KEY_DAG_AND_MEM_EX_MAP);
		
		for (DirectedAcyclicGraph dag : dagsAndMemExs.keySet()) {
			MemoryExclusionGraph memEx = dagsAndMemExs.get(dag);
			
			// Prepare the MEG with the alignment
			MemoryAllocator.alignSubBuffers(memEx, alignment);
	
			createAllocators(memEx);
	
			// Heat up the neighborsBackup
			if (verbose) {
				logger.log(Level.INFO, "Heat up MemEx");
			}
			for (MemoryExclusionVertex vertex : memEx.vertexSet()) {
				memEx.getAdjacentVertexOf(vertex);
			}
	
			StringBuilder csv = new StringBuilder();
	
			for (MemoryAllocator allocator : allocators) {
				this.allocateWith(allocator, csv);
			}
			
			System.out.println(csv);
		}
		
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(KEY_DAG_AND_MEM_EX_MAP, dagsAndMemExs);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_TRUE_FALSE_DEFAULT);
		parameters.put(PARAM_ALLOCATORS, VALUE_ALLOCATORS_DEFAULT);
		parameters.put(PARAM_XFIT_ORDER, VALUE_XFIT_ORDER_DEFAULT);
		parameters.put(PARAM_NB_SHUFFLE, VALUE_NB_SHUFFLE_DEFAULT);
		parameters.put(PARAM_ALIGNMENT, VALUE_ALIGNEMENT_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Allocating MemExs";
	}

}
