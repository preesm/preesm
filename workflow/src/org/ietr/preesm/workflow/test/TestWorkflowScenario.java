package org.ietr.preesm.workflow.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.workflow.WorkflowException;
import org.ietr.preesm.workflow.elements.AbstractScenarioImplementation;
import org.ietr.preesm.workflow.tools.WorkflowLogger;

public class TestWorkflowScenario extends AbstractScenarioImplementation {

	@Override
	public String displayPrototype() {
		return "out: algo, archi";
	}

	@Override
	public boolean accept(Set<String> outputs) {
		if (outputs.size() == 2 && outputs.contains("algo")
				&& outputs.contains("archi")) {
			return true;
		}

		return false;
	}

	@Override
	public Map<String, Object> extractData(String path)
			throws WorkflowException {
		Map<String, Object> outputs = new HashMap<String, Object>();
		WorkflowLogger.getLogger().log(Level.INFO,
				"Retrieving data from scenario");

		outputs.put("algo", "algo1");
		outputs.put("archi", "archi1");
		return outputs;
	}

	@Override
	public String monitorMessage() {
		return "Retrieving data from scenario";
	}

}
