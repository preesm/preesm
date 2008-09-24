/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.ietr.preesm.core.task.ITask;

/**
 * This class provides a transformation workflow node.
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 * 
 */
public class TaskNode implements IWorkflowNode {

	/**
	 * The ITransformation created using transformationId.
	 */
	private ITask task;

	/**
	 * The transformation identifier of this transformation node.
	 */
	private String taskId;

	/**
	 * Variables used to parameterize the transformation.
	 */
	private Map<String,String> variables;

	/**
	 * Creates a new transformation node with the given transformation
	 * identifier.
	 * 
	 * @param taskId
	 *            The transformation identifier.
	 */
	public TaskNode(String taskId) {
		this.taskId = taskId;
		
		variables = new HashMap<String, String>();
	}

	/**
	 * Creates a new variable retrieved from the xml in 
	 * {@link WorkflowParser}.
	 * 
	 * @param key 
	 * 				the name of the variable.
	 * @param value 
	 * 				the value of the variable.
	 */
	public void addVariable(String key, String value) {
		variables.put(key, value);
	}

	/**
	 * Returns the transformation associated with this
	 * {@link TaskNode}. Note that it is only valid if
	 * {@link #isTransformationPossible()} returns true.
	 * 
	 * @return The transformation associated with this transformation node, or
	 *         <code>null</code> if the transformation is not valid.
	 */
	public ITask getTask() {
		return task;
	}

	/**
	 * Returns the variables map.
	 * 
	 * @return the variables map.
	 */
	public Map<String, String> getVariables() {
		return variables;
	}

	/**
	 * Returns the value of the variable with name key.
	 * 
	 * @param key 
	 * 				the name of the variable.
	 * @return the value of the variable.
	 */
	public String getVariableValue(String key) {
		return variables.get(key);
	}

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
		return false;
	}

	@Override
	public boolean isTaskNode() {
		return true;
	}

	/**
	 * Checks if this task exists .
	 * 
	 * @return True if this task exists, false otherwise.
	 */
	public boolean isTaskPossible() {
		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			boolean found = false;
			IConfigurationElement[] elements = registry
					.getConfigurationElementsFor("org.ietr.preesm.core.tasks");
			for (int i = 0; i < elements.length && !found; i++) {
				IConfigurationElement element = elements[i];
				if (element.getAttribute("id").equals(taskId)) {
					// Tries to create the transformation
					Object obj = element.createExecutableExtension("type");

					// and checks it actually is an ITransformation.
					if (obj instanceof ITask) {
						task = (ITask) obj;
						found = true;
					}
				}
			}

			return found;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}

}
