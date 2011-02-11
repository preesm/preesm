/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.workflow;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
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
	private Map<String, String> variables;

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
	 * Creates a new variable retrieved from the xml in {@link WorkflowParser}.
	 * 
	 * @param key
	 *            the name of the variable.
	 * @param value
	 *            the value of the variable.
	 */
	public void addVariable(String key, String value) {
		variables.put(key, value);
	}

	/**
	 * Returns the transformation associated with this {@link TaskNode}. Note
	 * that it is only valid if {@link #isTransformationPossible()} returns
	 * true.
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
	 *            the name of the variable.
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
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"Failed to find plugins from workflow");
			return false;
		}
	}

	public String getTaskId() {
		return taskId;
	}

}
