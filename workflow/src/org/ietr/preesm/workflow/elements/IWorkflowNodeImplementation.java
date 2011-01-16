/**
 * 
 */
package org.ietr.preesm.workflow.elements;

/**
 * 
 * @author mpelcat
 */
public interface IWorkflowNodeImplementation {

	/**
	 * Returns the preferred prototype for the node in a workflow. Useful to
	 * give user information in the workflow
	 */
	public String displayPrototype();

	/**
	 * Returns a message to display in the monitor.
	 */
	public String monitorMessage();
}
