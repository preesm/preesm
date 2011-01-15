package org.ietr.preesm.workflow.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.workflow.elements.Task;

public class TestWorkflowTask1 extends Task {

	@Override
	public String displayPrototype() {
		return "outputs: toto";
	}

	@Override
	public boolean accept(Set<String> inputs, Set<String> outputs) {
		if (inputs.size() == 0 && outputs.size() == 1
				&& outputs.contains("toto")) {
			return true;
		}

		return false;
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("size", "25");
		parameters.put("duration", "short");
		return parameters;
	}

}
