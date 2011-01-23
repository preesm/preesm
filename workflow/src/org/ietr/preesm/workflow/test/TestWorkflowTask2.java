package org.ietr.preesm.workflow.test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.workflow.tools.WorkflowLogger;

public class TestWorkflowTask2 extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) {
		Map<String, Object> outputs = new HashMap<String, Object>();
		WorkflowLogger.getLogger().log(Level.INFO,
				"Executing TestWorkflowTask2; node: " + nodeName);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("size", "10");
		parameters.put("duration", "long");
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Executing TestWorkflowTask2";
	}

}
