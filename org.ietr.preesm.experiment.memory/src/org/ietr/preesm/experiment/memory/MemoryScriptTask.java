package org.ietr.preesm.experiment.memory;

import java.util.HashMap;
import java.util.Map;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.scenario.PreesmScenario;

public class MemoryScriptTask extends AbstractTaskImplementation {
	
	public static final String PARAM_CHECK = "Check";
	public static final String VALUE_CHECK_NONE = "None";
	public static final String VALUE_CHECK_FAST = "Fast";
	public static final String VALUE_CHECK_THOROUGH = "Thorough";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {		
		
		// Retrieve the input graph
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

		ScriptRunner sr = new ScriptRunner();
		
		// Retrieve all the scripts
		sr.findScripts(dag);
		
		// Get the data types from the scenario
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
		sr.setDataTypes(scenario.getSimulationManager().getDataTypes());
		
		// Get check policy
		String checkString = parameters.get(PARAM_CHECK);
		switch(checkString){
		case VALUE_CHECK_NONE:
			sr.setCheckPolicy(CheckPolicy.NONE);
			break;
		case VALUE_CHECK_FAST:
			sr.setCheckPolicy(CheckPolicy.FAST);
			break;
		case VALUE_CHECK_THOROUGH:
			sr.setCheckPolicy(CheckPolicy.THOROUGH);
			break;
		default:
			sr.setCheckPolicy(CheckPolicy.FAST);
			break;
		}

		// Execute all the scripts
		sr.run();
		//sr.runTest();

		// Outputs
		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put("MemEx", inputs.get("MemEx"));
		return outputs;
	}

	

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String,String> param = new HashMap<String,String>();
		param.put(PARAM_CHECK, "? C {"+VALUE_CHECK_NONE+", "+VALUE_CHECK_FAST+", "+VALUE_CHECK_THOROUGH+"}");
		return param;
	}

	@Override
	public String monitorMessage() {
		return "Running Memory Optimization Scripts.";
	}

}
