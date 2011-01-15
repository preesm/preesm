/**
 * 
 */
package org.ietr.preesm.workflow.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

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
	
	public Set<WorkflowEdge> incomingEdgesOf(IWorkflowNode node){
		return graph.incomingEdgesOf(node);
	}
	
	public Set<WorkflowEdge> outgoingEdgesOf(IWorkflowNode node){
		return graph.outgoingEdgesOf(node);
	}
	
	public Set<IWorkflowNode> vertexSet(){
		return graph.vertexSet();
	}
	
	public List<IWorkflowNode> vertexTopologicalList(){
		List<IWorkflowNode> nodeList = new ArrayList<IWorkflowNode>();
		TopologicalOrderIterator<IWorkflowNode, WorkflowEdge> it = new TopologicalOrderIterator<IWorkflowNode, WorkflowEdge>(
				graph);
		
		while (it.hasNext()) {
			IWorkflowNode node = it.next();
			nodeList.add(node);
		}
		
		return nodeList;
	}
}