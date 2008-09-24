/**
 * 
 */
package org.ietr.preesm.core.workflow;

/**
 * This class provides an architecture workflow node.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class ArchitectureNode implements IWorkflowNode {

	@Override
	public boolean isAlgorithmNode() {
		return false;
	}

	@Override
	public boolean isArchitectureNode() {
		return true;
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
