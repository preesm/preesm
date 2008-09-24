/**
 * 
 */
package org.ietr.preesm.core.workflow;

/**
 * This class provides an algorithm workflow node.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class AlgorithmNode implements IWorkflowNode {

	@Override
	public boolean isAlgorithmNode() {
		return true;
	}

	@Override
	public boolean isArchitectureNode() {
		return false;
	}

	@Override
	public boolean isScenarioNode() {
		return false;
	}

	@Override
	public boolean isTaskNode() {
		return false;
	}

}
