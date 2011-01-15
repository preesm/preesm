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

package org.ietr.preesm.workflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.workflow.elements.IWorkflowNode;
import org.ietr.preesm.workflow.elements.ScenarioNode;
import org.ietr.preesm.workflow.elements.TaskNode;
import org.ietr.preesm.workflow.elements.Workflow;
import org.ietr.preesm.workflow.elements.WorkflowEdge;
import org.ietr.preesm.workflow.task.Task;
import org.ietr.preesm.workflow.tools.PreesmLogger;

/**
 * This class provides methods to check and execute a workflow. A workflow
 * consists of several transformation plug-ins tasks applied to a scenario.
 * 
 * @author mpelcat
 */
public class WorkflowManager {

	public WorkflowManager() {
		super();
	}

	/**
	 * Checks the existence of all task classes
	 */
	public boolean check(String workflowPath, IProgressMonitor monitor) {

		monitor.subTask("Checking workflow...");
		Workflow workflow = new WorkflowParser().parse(workflowPath);

		boolean workflowOk = true;
		for (IWorkflowNode node : workflow.vertexSet()) {
			if (node.isTaskNode()) {
				// Testing only connected nodes
				if (!workflow.edgesOf(node).isEmpty()) {
					workflowOk = ((TaskNode) node).isTaskPossible();
				}

				if (!workflowOk) {
					PreesmLogger.getLogger().log(
							Level.SEVERE,
							"Failed to find plugin "
									+ ((TaskNode) node).getTaskId()
									+ " from workflow.");
				}
			}
		}

		// Check the workflow
		monitor.worked(1);

		return workflowOk;
	}

	/**
	 * Checks that the workflow task node edges fit the task prototype
	 */
	public void checkPrototype(TaskNode taskNode, Workflow workflow) {
		Task task = taskNode.getTask();
		Set<String> inputs = new HashSet<String>();
		Set<String> outputs = new HashSet<String>();

		for (WorkflowEdge edge : workflow.incomingEdgesOf(taskNode)) {
			inputs.add(edge.getDataType());
		}
		
		for (WorkflowEdge edge : workflow.outgoingEdgesOf(taskNode)) {
			outputs.add(edge.getDataType());
		}
		
		if (!task.accept(inputs, outputs)) {
			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"The edges of task " + taskNode.getTaskId()
							+ " are not correct. Needed inputs: "
							+ inputs.toString() + ". Needed outputs: "
							+ outputs.toString() + ".");
		}
	}

	/**
	 * Executes the workflow
	 */
	public boolean execute(String workflowPath, String scenarioPath) {

		Workflow workflow = new WorkflowParser().parse(workflowPath);

		for (IWorkflowNode node : workflow.vertexTopologicalList()) {
			if (node.isScenarioNode()) {

				// The scenario node is special because it gets a path from the
				// outside
				// IScenario scenario =

			} else if (node.isTaskNode()) {
				TaskNode taskNode = (TaskNode) node;
				Task task = taskNode.getTask();

				// Checks that the workflow task node edges fit the task prototype
				checkPrototype(taskNode, workflow);
				// Preparing the input and output maps of the execute method
				Map<String, Object> inputs = new HashMap<String, Object>();
				Map<String, Object> outputs = null;

				// Retrieving the data from input edges
				for (WorkflowEdge edge : workflow.incomingEdgesOf(taskNode)) {
					inputs.put(edge.getDataType(), edge.getData());
				}

				// Executing the workflow task
				try {
					outputs = task.execute(inputs);
				} catch (WorkflowException e) {
					PreesmLogger.getLogger().log(Level.SEVERE, e.getMessage());
				}

				// Putting the data in output edges
				for (WorkflowEdge edge : workflow.outgoingEdgesOf(taskNode)) {
					String type = edge.getDataType();
					if (outputs.containsKey(type)) {
						edge.setData(outputs.get(type));
					} else {
						edge.setData(null);
						PreesmLogger
								.getLogger()
								.log(Level.SEVERE,
										"The workflow task "
												+ taskNode.getTaskId()
												+ " has not produced the necessary data "
												+ type
												+ ". Consider revising the workflow.");
					}
				}
			}
		}

		return true;
	}
}
