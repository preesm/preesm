package org.ietr.preesm.core.algorithm.visitors;

import java.util.HashSet;
import java.util.Iterator;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.visitors.IGraphVisitor;
import net.sf.dftools.algorithm.model.visitors.SDF4JException;
import net.sf.dftools.architecture.slam.ComponentInstance;

/**
 * Visitor to identify the inter-core communications of a mapped DAG. This
 * visitor is inspired by CommunicationRouter.routeAll() implementation.
 * 
 * @author kdesnos
 * 
 */
public class CommunicationIdentifierVisitor implements
		IGraphVisitor<DirectedAcyclicGraph, DAGVertex, DAGEdge> {

	protected HashSet<DAGEdge> interCoreComm;

	/**
	 * Constructor of the CommunicationIdentifier.
	 */
	public CommunicationIdentifierVisitor() {
		interCoreComm = new HashSet<DAGEdge>();
	}

	/**
	 * Return the result of the visitor algorithm.
	 * 
	 * @return list containing edges that are inter-core communications
	 */
	public HashSet<DAGEdge> getResult() {
		return interCoreComm;
	}

	@Override
	public void visit(DAGEdge currentEdge) {
		// First, we check that both source and target vertices are tasks
		// i.e. we skip existing send/receive nodes
		if (currentEdge.getSource().getPropertyBean().getValue("vertexType")
				.toString().equals("task")
				&& currentEdge.getTarget().getPropertyBean()
						.getValue("vertexType").toString().equals("task")) {

			ComponentInstance sourceComponent = (ComponentInstance) (currentEdge
					.getSource()).getPropertyBean().getValue("Operator");
			ComponentInstance targetComponent = (ComponentInstance) (currentEdge
					.getTarget()).getPropertyBean().getValue("Operator");

			if (sourceComponent != null && targetComponent != null) {
				if (!sourceComponent.equals(targetComponent)) {
					// This code is reached only if the current edge is an
					// inter-core communication
					interCoreComm.add(currentEdge);
				}
			}
		}
	}

	@Override
	public void visit(DirectedAcyclicGraph dag) throws SDF4JException {
		// We iterate the edges
		Iterator<DAGEdge> iterator = dag.edgeSet().iterator();

		while (iterator.hasNext()) {
			DAGEdge currentEdge = (DAGEdge) iterator.next();
			currentEdge.accept(this);
		}
	}

	@Override
	public void visit(DAGVertex dagVertex) throws SDF4JException {
		// Nothing to do here for this visitor

	}

}
