/**
 * 
 */
package org.ietr.preesm.workflow.elements;

import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * Workflow graph
 * 
 * @author mpelcat
 */
public class Workflow {

	DirectedGraph<IWorkflowNode, WorkflowEdge> graph = null;
	
	public Workflow() {
		this.graph = new SimpleDirectedGraph<IWorkflowNode, WorkflowEdge>(
				WorkflowEdge.class);
	}

	public void addNode(IWorkflowNode node){
		graph.addVertex(node);
	}
	
	public WorkflowEdge addEdge(IWorkflowNode source, IWorkflowNode target){
		return graph.addEdge(source, target);
	}
	
	public Set<WorkflowEdge> edgesOf(IWorkflowNode node){
		return graph.edgesOf(node);
	}
	
	public Set<IWorkflowNode> vertexSet(){
		return graph.vertexSet();
	}
}
