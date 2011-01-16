/**
 * 
 */
package org.ietr.preesm.workflow.elements;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * Workflow graph
 * 
 * @author mpelcat
 */
public class Workflow extends DirectedMultigraph<IWorkflowNode, WorkflowEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -908014142930559238L;

	public Workflow() {
		super(
				WorkflowEdge.class);
	}

	public List<IWorkflowNode> vertexTopologicalList() {
		List<IWorkflowNode> nodeList = new ArrayList<IWorkflowNode>();
		TopologicalOrderIterator<IWorkflowNode, WorkflowEdge> it = new TopologicalOrderIterator<IWorkflowNode, WorkflowEdge>(
				this);

		while (it.hasNext()) {
			IWorkflowNode node = it.next();
			nodeList.add(node);
		}

		return nodeList;
	}

	public boolean hasScenario() {
		int nbScenarios = 0;
		for (IWorkflowNode node : this.vertexSet()) {
			if (node.isScenarioNode()) {
				nbScenarios++;
			}
		}

		if (nbScenarios == 1) {
			return true;
		}

		return false;
	}
}