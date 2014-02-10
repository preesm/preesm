package org.ietr.preesm.experiment.memory;

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
import org.ietr.preesm.core.scenario.PreesmScenario;

public class MemoryScriptTask extends AbstractTaskImplementation {

	public static final String PARAM_VERBOSE = "Verbose";
	public static final String VALUE_TRUE = "True";
	public static final String VALUE_FALSE = "False";

	public static final String PARAM_CHECK = "Check";
	public static final String VALUE_CHECK_NONE = "None";
	public static final String VALUE_CHECK_FAST = "Fast";
	public static final String VALUE_CHECK_THOROUGH = "Thorough";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		// Get verbose parameter
		boolean verbose = false;
		verbose = parameters.get(PARAM_VERBOSE).equals(VALUE_TRUE);

		// Get the logger
		Logger logger = WorkflowLogger.getLogger();

		// Retrieve the input graph
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

		ScriptRunner sr = new ScriptRunner();

		// Retrieve all the scripts
		int nbScripts = sr.findScripts(dag);

		// Get the data types from the scenario
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
		sr.setDataTypes(scenario.getSimulationManager().getDataTypes());

		// Execute all the scripts
		if (verbose) {
			logger.log(Level.INFO, "Running " + nbScripts + " memory scripts.");
		}
		sr.run();

		// Check the result
		// Get check policy
		String checkString = parameters.get(PARAM_CHECK);
		switch (checkString) {
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
			checkString = VALUE_CHECK_FAST;
			sr.setCheckPolicy(CheckPolicy.FAST);
			break;
		}
		if (verbose) {
			logger.log(Level.INFO,
					"Checking results of memory scripts with checking policy: "
							+ checkString + ".");
		}
		sr.check();

		// Pre-process the script result
		if (verbose) {
			logger.log(Level.INFO, "Processing memory script results.");
		}
		sr.process();
		

		// sr.runTest();

		// Outputs
		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put("MemEx", inputs.get("MemEx"));
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> param = new HashMap<String, String>();
		param.put(PARAM_VERBOSE, "? C {" + VALUE_TRUE + ", " + VALUE_FALSE
				+ "}");
		param.put(PARAM_CHECK, "? C {" + VALUE_CHECK_NONE + ", "
				+ VALUE_CHECK_FAST + ", " + VALUE_CHECK_THOROUGH + "}");
		return param;
	}

	@Override
	public String monitorMessage() {
		return "Running Memory Optimization Scripts.";
	}

}
