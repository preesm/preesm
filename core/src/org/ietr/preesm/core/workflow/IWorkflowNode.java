/**
 * 
 */
package org.ietr.preesm.core.workflow;

/**
 * This interface provides methods to manipulate workflow nodes.
 * 
 * @author Matthieu Wipliez
 * 
 */
public interface IWorkflowNode {

	/**
	 * 
	 * @return True if this node is an algorithm node, false otherwise.
	 */
	public boolean isAlgorithmNode();

	/**
	 * 
	 * @return True if this node is an architecture node, false otherwise.
	 */
	public boolean isArchitectureNode();

	/**
	 * 
	 * @return True if this node is a scenario node, false otherwise.
	 */
	public boolean isScenarioNode();

	/**
	 * 
	 * @return True if this node is a transformation node, false otherwise.
	 */
	public boolean isTaskNode();

}
