package org.ietr.preesm.workflow.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.workflow.WorkflowException;
import org.ietr.preesm.workflow.elements.Scenario;
import org.ietr.preesm.workflow.elements.Task;

public class TestWorkflowScenario extends Scenario {

	@Override
	public String displayPrototype() {
		return "outputs: algo, archi";
	}

	@Override
	public boolean accept(Set<String> outputs) {
		if (outputs.size() == 2 && outputs.contains("algo") && outputs.contains("archi")) {
			return true;
		}

		return false;
	}

	@Override
	public Map<String, Object> extractData(String path)
			throws WorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

}
