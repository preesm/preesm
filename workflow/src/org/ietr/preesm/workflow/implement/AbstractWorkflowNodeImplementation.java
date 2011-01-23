/**
 * 
 */
package org.ietr.preesm.workflow.implement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.workflow.tools.WorkflowLogger;

/**
 * Node implementation is the superclass of both scenario and task
 * implementation. Their outputs are handled the same way.
 * 
 * @author mpelcat
 */
public abstract class AbstractWorkflowNodeImplementation {

	/**
	 * Id and fully qualified names of node output retrieved from the extension.
	 */
	private Map<String, String> outputPrototype;

	public AbstractWorkflowNodeImplementation() {
		outputPrototype = new HashMap<String, String>();
	}

	/**
	 * Adds an input to the task prototype.
	 */
	final public void addOutput(String id, String type) {
		outputPrototype.put(id, type);
	}

	/**
	 * Gets the fully qualified name of the class associated to the given output
	 * port.
	 */
	final public String getOutputType(String id) {
		return outputPrototype.get(id);
	}

	/**
	 * Compares the prototype with the output edges. Not all outputs need to be
	 * used
	 */
	final public boolean acceptOutputs(Set<String> outputPortNames) {

		for (String outputPortName : outputPortNames) {
			if (!outputPrototype.keySet().contains(outputPortName)) {
				WorkflowLogger.getLogger().logFromProperty(Level.SEVERE,
						"Workflow.FalseOutputEdge", outputPortName);
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the preferred prototype for the node in a workflow. Useful to
	 * give user information in the workflow
	 */
	public String displayPrototype() {
		return " outputs=" + outputPrototype.toString();
	}

	/**
	 * Returns a message to display in the monitor.
	 */
	public abstract String monitorMessage();
}
