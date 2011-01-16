package org.ietr.preesm.workflow.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.workflow.elements.AbstractTask;
import org.ietr.preesm.workflow.tools.WorkflowLogger;

public class TestWorkflowTask1 extends AbstractTask {

	@Override
	public String displayPrototype() {
		return "in: algo, archi; out: superData";
	}

	@Override
	public boolean accept(Set<String> inputs, Set<String> outputs) {
		if (inputs.size() == 2 && inputs.contains("algo")
				&& inputs.contains("archi") && outputs.size() == 1
				&& outputs.contains("superData")) {
			return true;
		}

		return false;
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters) {
		Map<String, Object> outputs = new HashMap<String, Object>();
		WorkflowLogger.getLogger().log(Level.INFO,"Executing TestWorkflowTask1");
		outputs.put("superData", "superData1");
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("size", "25");
		parameters.put("duration", "short");
		return parameters;
	}

}
