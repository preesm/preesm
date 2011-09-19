/**
 * 
 */
package org.ietr.preesm.plugin.codegen.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;

/**
 * Testing methods to optimize DAG memory use
 * 
 * @author kdesnos
 */
public class MemoryOptimizer extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {
		
		// Copy the input dag to the output
		Map<String, Object> outputs = new HashMap<String, Object>();
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
		outputs.put("DAG", dag);
		
		// Make a copy of the Input DAG for treatment
		DirectedAcyclicGraph localDAG = (DirectedAcyclicGraph) dag.clone();

		// Rem: Logger is used to display messages in the console
		Logger logger = AbstractWorkflowLogger.getLogger();
		
		logger.log(Level.INFO, "Memory exclusion graph : start building");
		
		
		MemoryExclusionGraph memex = new MemoryExclusionGraph();
		try {
			memex.BuildGraph(localDAG);
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			throw new WorkflowException(e.getLocalizedMessage());
		}
		logger.log(Level.INFO, "Memory exclusion graph : graph built");
		logger.log(Level.INFO, "There are " + memex.vertexSet().size() + " memory transfers.");
		logger.log(Level.INFO, "There are " + memex.edgeSet().size() + " exclusions.");

		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Optimizing memory.";
	}
}
