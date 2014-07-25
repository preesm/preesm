package org.ietr.preesm.memory.script;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.allocation.MemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

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

		// Retrieve the alignment param
		String valueAlignment = parameters.get(MemoryAllocatorTask.PARAM_ALIGNMENT);
		int alignment;
		switch (valueAlignment.substring(0,
				Math.min(valueAlignment.length(), 7))) {
		case MemoryAllocatorTask.VALUE_ALIGNEMENT_NONE:
			alignment = -1;
			break;
		case MemoryAllocatorTask.VALUE_ALIGNEMENT_DATA:
			alignment = 0;
			break;
		case MemoryAllocatorTask.VALUE_ALIGNEMENT_FIXED:
			String fixedValue = valueAlignment.substring(7);
			alignment = Integer.parseInt(fixedValue);
			break;
		default:
			alignment = -1;
		}
		if (verbose) {
			logger.log(Level.INFO, "Scripts with alignment:=" + alignment
					+ ".");
		}

		// Retrieve the input graph
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

		ScriptRunner sr = new ScriptRunner(alignment);

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

		// Update memex
		if (verbose) {
			logger.log(Level.INFO, "Updating memory exclusion graph.");
		}
		MemoryExclusionGraph meg = (MemoryExclusionGraph) inputs.get("MemEx");
		sr.updateMEG(meg);

		// Outputs
		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put("MemEx", meg);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> param = new HashMap<String, String>();
		param.put(PARAM_VERBOSE, "? C {" + VALUE_TRUE + ", " + VALUE_FALSE
				+ "}");
		param.put(PARAM_CHECK, "? C {" + VALUE_CHECK_NONE + ", "
				+ VALUE_CHECK_FAST + ", " + VALUE_CHECK_THOROUGH + "}");
		param.put(MemoryAllocatorTask.PARAM_ALIGNMENT,
				MemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);

		return param;
	}

	@Override
	public String monitorMessage() {
		return "Running Memory Optimization Scripts.";
	}

}
