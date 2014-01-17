package org.ietr.preesm.experiment.memory;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

public class MemoryScriptTask extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		
		ScriptRunner sr = new ScriptRunner();
		sr.run();
		
		// Outputs
		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put("MemEx", inputs.get("MemEx"));
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String monitorMessage() {
		return "Running Memory Optimization Scripts.";
	}

}
