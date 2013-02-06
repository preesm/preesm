package org.ietr.preesm.experiment.model.transformation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

public class TaskExpression extends AbstractTaskImplementation{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		// TODO Auto-generated method stub
		
		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String monitorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
