package org.ietr.preesm.experiment.memory.exclusiongraph;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;



/**
 * Workflow element that takes a Scheduled DAG and a MemEx as inputs and 
 * update the MemEx according to the DAG Schedule
 * 
 * @author kdesnos
 * 
 */
public class MemExUpdater extends AbstractTaskImplementation {

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";
	
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
		
		// Retrieve inputs
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");
		
		int before = memEx.edgeSet().size();
		
		if(verbose) {
			logger.log(Level.INFO, "Memory exclusion graph : start updating with schedule");
		}
		
		memEx.updateWithSchedule(dag);
		
		double density = memEx.edgeSet().size()/(memEx.vertexSet().size()*(memEx.vertexSet().size() -1)/2.0 );
		if(verbose) {
			logger.log(Level.INFO, "Memory exclusion graph updated with "+memEx.vertexSet().size()+" vertices and density = "+density);
			logger.log(Level.INFO, "Exclusions removed: "+(before-memEx.edgeSet().size()));
		}

		// Generate output  
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(OUTPUT_KEY_MEM_EX, memEx);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_VERBOSE_DEFAULT);
		return parameters;
	}
	

	@Override
	public String monitorMessage() {
		return "Updating MemEx";
	}

}
