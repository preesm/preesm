/**
 * 
 */
package org.ietr.preesm.workflow.elements;

import java.util.Map;
import java.util.Set;

import org.ietr.preesm.workflow.WorkflowException;

/**
 * Abstract scenario that can be implemented by a plugin wanting workflow
 * execution capabilities.
 * 
 * @author mpelcat
 */
public abstract class AbstractScenario {

	/**
	 * The workflow scenario element must accept the names of the output ports
	 * in the graph. Otherwise, the graph is considered not valid.
	 */
	public abstract boolean accept(Set<String> outputs);

	/**
	 * Displays the preferred prototype
	 */
	public abstract String displayPrototype();

	/**
	 * The workflow scenario element must have an initialize method that is called
	 * by the workflow to generate the scenario outputs (for example, an
	 * algorithm and an architecture).
	 */
	public abstract Map<String, Object> extractData(String path)
			throws WorkflowException;
}
