/**
 * 
 */
package org.ietr.preesm.core.workflow;

/**
 * This class provides a scenario workflow node.
 * 
 * @author mpelcat
 */
public class ScenarioNode implements IWorkflowNode {

	@Override
	public boolean isAlgorithmNode() {
		return false;
	}

	@Override
	public boolean isArchitectureNode() {
		return false;
	}

	@Override
	public boolean isScenarioNode() {
		return true;
	}

	@Override
	public boolean isTaskNode() {
		return false;
	}
}
