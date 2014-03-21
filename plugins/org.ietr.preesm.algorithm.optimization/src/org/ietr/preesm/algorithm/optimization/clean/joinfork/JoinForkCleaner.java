/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 *
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.algorithm.optimization.clean.joinfork;

import java.util.HashSet;
import java.util.Set;

import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;

/**
 * Class cleaning the useless join-fork pairs of vertices which may have been
 * introduced by hierarchy flattening and single rate transformation
 * 
 * @author cguy
 * 
 */
public class JoinForkCleaner {

	// Set of edges to remove from graph
	Set<SDFEdge> edgesToRemove;
	// Set of vertices to remove from graph
	Set<SDFAbstractVertex> verticesToRemove;

	/**
	 * Top method to call in order to remove all the join-fork pairs which can
	 * be removed safely from an SDFGraph
	 * 
	 * @param graph
	 *            the SDFGraph we want to clean
	 * @throws InvalidExpressionException
	 */
	public void cleanJoinForkPairsFrom(SDFGraph graph)
			throws InvalidExpressionException {
		edgesToRemove = new HashSet<SDFEdge>();
		verticesToRemove = new HashSet<SDFAbstractVertex>();
		// For every edge e of the graph
		for (SDFEdge e : graph.edgeSet()) {
			// Check whether it stands between an SDFJoinVertex and an
			// SDFForkVertex and whether it's safe to remove it
			if (canBeRemovedSafely(e)) {
				// If it is the case, add e, its source and its target to the
				// elements to remove from graph
				edgesToRemove.add(e);
				verticesToRemove.add(e.getSource());
				verticesToRemove.add(e.getTarget());				
			}
		}
		
		// Then, add the edges to replace e
		for(SDFEdge e : edgesToRemove) {			
			addEdgesToReplace(e, graph);
		}

		// Finally, remove all useless elements from graph
		graph.removeAllEdges(edgesToRemove);
		graph.removeAllVertices(verticesToRemove);
	}

	/**
	 * Call different methods to add the needed edges depending whether we need
	 * to replace the pair of join-fork vertices by join vertices, fork vertices
	 * or no vertex at all.
	 * 
	 * @param edge
	 *            the SDFEdge to replace (i.e., the one which stands between the
	 *            pair of join-fork vertices we want to remove)
	 * @param graph
	 *            the SDFGraph containing edge and in which we will ad new edges
	 * @throws InvalidExpressionException
	 */
	private void addEdgesToReplace(SDFEdge edge, SDFGraph graph)
			throws InvalidExpressionException {
		SDFJoinVertex source = (SDFJoinVertex) edge.getSource();
		SDFForkVertex target = (SDFForkVertex) edge.getTarget();
		int nbForkEdges = target.getOutgoingConnections().size();
		int nbJoinEdges = source.getIncomingConnections().size();
		// Check whether we need to add join/fork vertices
		// If there is more edges entering the source of edge than edges leaving
		// its target, we will need to add some join vertices
		if (nbForkEdges < nbJoinEdges) {
			addDirectEdgesAndJoinVertices(edge, graph);
		}
		// If there is more edges leaving the target of edge than edges entering
		// its source, we will need to add some fork vertices
		else if (nbForkEdges > nbJoinEdges) {
			addDirectEdgesAndForkVertices(edge, graph);
		}
		// If there as much entering than leaving edges, we do not need to add
		// any vertices
		else {
			addOnlyDirectEdges(edge, graph);
		}
	}

	/**
	 * Method called in the case where we don't need to add join or fork
	 * vertices. Add only edges to the graph. The added edges are direct between
	 * the predecessors of the join vertex and the successors of the fork one.
	 * 
	 * @param edge
	 *            the SDFEdge to replace (i.e., the one which stands between the
	 *            pair of join-fork vertices we want to remove)
	 * @param graph
	 *            the SDFGraph containing edge and in which we will ad new edges
	 * @throws InvalidExpressionException
	 */
	private void addOnlyDirectEdges(SDFEdge edge, SDFGraph graph)
			throws InvalidExpressionException {
		SDFJoinVertex source = (SDFJoinVertex) edge.getSource();
		SDFForkVertex target = (SDFForkVertex) edge.getTarget();
		int nbEdges = target.getOutgoingConnections().size();
		// Rate for production and consumption of the created edges
		AbstractEdgePropertyType<?> newProdCons = source
				.getIncomingConnections().get(0).getCons();
		// Take pairs consisting in one predecessor of the join vertex and one
		// successor of the fork vertex
		for (int i = 0; i < nbEdges; i++) {
			SDFEdge oldIncoming = source.getIncomingConnections().get(i);
			SDFEdge oldOutgoing = target.getOutgoingConnections().get(i);
			// Get these vertices, between which we will create an edge
			SDFAbstractVertex newSource = oldIncoming.getSource();
			SDFAbstractVertex newTarget = oldOutgoing.getTarget();
			/*
			 * Delay for the new edge is the sum of the delays on the three
			 * existing edges it will replace: -edge between the SDFJoinVertex
			 * source and the SDFForkVertex target; -oldIncoming between
			 * newSource and source; -oldOutgoing between target and newTarget.
			 */
			int newDelay = oldIncoming.getDelay().intValue()
					+ oldOutgoing.getDelay().intValue()
					+ edge.getDelay().intValue();
			// Create a new edge between the vertices
			SDFEdge newEdge = graph.addEdge(newSource, newTarget, newProdCons, newProdCons,
					new SDFIntEdgePropertyType(newDelay));
			newEdge.setSourceInterface(oldIncoming.getSourceInterface());
			newEdge.setTargetInterface(oldOutgoing.getTargetInterface());
		}
	}

	/**
	 * Method called in the case where we need to add fork vertices. Add edges
	 * and fork vertices. The added edges are added between the predecessors of
	 * the original join vertex and the new fork vertices and between the new
	 * fork vertices and the successors of the original fork vertex.
	 * 
	 * @param edge
	 *            the SDFEdge to replace (i.e., the one which stands between the
	 *            pair of join-fork vertices we want to remove)
	 * @param graph
	 *            the SDFGraph containing edge and in which we will ad new edges
	 * @throws InvalidExpressionException
	 */
	private void addDirectEdgesAndForkVertices(SDFEdge edge, SDFGraph graph)
			throws InvalidExpressionException {
		SDFJoinVertex source = (SDFJoinVertex) edge.getSource();
		SDFForkVertex target = (SDFForkVertex) edge.getTarget();
		int nbForkEdges = target.getOutgoingConnections().size();
		int nbJoinEdges = source.getIncomingConnections().size();
		// Number of SDFForkVertex to create
		int nbNewFork = nbForkEdges / nbJoinEdges;
		for (int i = 0; i < nbNewFork; i++) {
			// Create a new SDFJoinVertex
			SDFForkVertex newFork = new SDFForkVertex();
			// Reconnect outgoing edges from the original fork vertex to the
			// new one
			for (int j = 0; j < nbJoinEdges; j++) {
				SDFEdge edgeToReconnect = target.getOutgoingConnections().get(
						i * nbJoinEdges + j);
				SDFAbstractVertex newTarget = edgeToReconnect.getTarget();
				SDFEdge newEdge = graph.addEdge(newFork, newTarget, edgeToReconnect.getProd(),
						edgeToReconnect.getCons(), edgeToReconnect.getDelay());
				// Set ports for the newEdge
				newEdge.setTargetInterface(edgeToReconnect.getTargetInterface());
				SDFSourceInterfaceVertex srcPort = new SDFSourceInterfaceVertex();
				srcPort.setName("out_" + newFork.getSources().size());
				newFork.getSources().add(srcPort);
				newEdge.setSourceInterface(srcPort);
				
				edgesToRemove.add(edgeToReconnect);
			}

			// Then connect the new fork vertex to its predecessor (one of the
			// predecessors of the original join)
			SDFEdge oldIncoming = source.getIncomingConnections().get(i);
			SDFAbstractVertex newSource = oldIncoming.getSource();
			// Rate for production and consumption of the created edges
			AbstractEdgePropertyType<?> newProdCons = oldIncoming.getCons();
			/*
			 * Delay for the new edge is the sum of the delays on the two
			 * existing edges it will replace: -edge between the SDFJoinVertex
			 * source and the SDFForkVertex target; -oldIncoming between
			 * newSource and source.
			 */
			int newDelay = oldIncoming.getDelay().intValue()
					+ edge.getDelay().intValue();
			// Create a new edge between the vertices
			SDFEdge newEdge = graph.addEdge(newSource, newFork, newProdCons, newProdCons,
					new SDFIntEdgePropertyType(newDelay));
			newEdge.setSourceInterface(oldIncoming.getSourceInterface());
			// Set ports for the newEdge
			SDFSinkInterfaceVertex tgtPort = new SDFSinkInterfaceVertex();
			tgtPort.setName("in_" + newFork.getSinks().size());
			newFork.getSinks().add(tgtPort);
			newEdge.setTargetInterface(tgtPort);
		}
	}

	/**
	 * Method called in the case where we need to add join vertices. Add edges
	 * and join vertices. The added edges are added between the predecessors of
	 * the original join vertex and the new join vertices and between the new
	 * join vertices and the successors of the original fork vertex.
	 * 
	 * @param edge
	 *            the SDFEdge to replace (i.e., the one which stands between the
	 *            pair of join-fork vertices we want to remove)
	 * @param graph
	 *            the SDFGraph containing edge and in which we will ad new edges
	 * @throws InvalidExpressionException
	 */
	private void addDirectEdgesAndJoinVertices(SDFEdge edge, SDFGraph graph)
			throws InvalidExpressionException {
		SDFJoinVertex source = (SDFJoinVertex) edge.getSource();
		SDFForkVertex target = (SDFForkVertex) edge.getTarget();
		int nbForkEdges = target.getOutgoingConnections().size();
		int nbJoinEdges = source.getIncomingConnections().size();
		// Number of SDFJoinVertex to create
		int nbNewJoin = nbJoinEdges / nbForkEdges;
		for (int i = 0; i < nbNewJoin; i++) {
			// Create a new SDFJoinVertex
			SDFJoinVertex newJoin = new SDFJoinVertex();
			// Reconnect incoming edges to the original join vertex to the
			// new one
			for (int j = 0; j < nbForkEdges; j++) {
				SDFEdge edgeToReconnect = source.getIncomingConnections().get(
						i * nbForkEdges + j);
				SDFAbstractVertex newSource = edgeToReconnect.getSource();
				SDFEdge newEdge = graph.addEdge(newSource, newJoin, edgeToReconnect.getProd(),
						edgeToReconnect.getCons(), edgeToReconnect.getDelay());
				// Set ports for the newEdge
				newEdge.setSourceInterface(edgeToReconnect.getSourceInterface());
				SDFSinkInterfaceVertex tgtPort = new SDFSinkInterfaceVertex();
				tgtPort.setName("in_" + newJoin.getSinks().size());
				newJoin.getSinks().add(tgtPort);
				newEdge.setTargetInterface(tgtPort);
				
				edgesToRemove.add(edgeToReconnect);
			}

			// Then connect the new join vertex to its successor (one of the
			// successors of the original fork)
			SDFEdge oldOutgoing = target.getOutgoingConnections().get(i);
			SDFAbstractVertex newTarget = oldOutgoing.getTarget();
			// Rate for production and consumption of the created edges
			AbstractEdgePropertyType<?> newProdCons = oldOutgoing.getCons();
			/*
			 * Delay for the new edge is the sum of the delays on the two
			 * existing edges it will replace: -edge between the SDFJoinVertex
			 * source and the SDFForkVertex target; -oldOutgoing between target
			 * and newTarget.
			 */
			int newDelay = oldOutgoing.getDelay().intValue()
					+ edge.getDelay().intValue();
			// Create a new edge between the vertices
			SDFEdge newEdge = graph.addEdge(newJoin, newTarget, newProdCons, newProdCons,
					new SDFIntEdgePropertyType(newDelay));
			newEdge.setTargetInterface(oldOutgoing.getTargetInterface());
			// Set ports for the newEdge
			SDFSourceInterfaceVertex srcPort = new SDFSourceInterfaceVertex();
			srcPort.setName("out_" + newJoin.getSources().size());
			newJoin.getSources().add(srcPort);
			newEdge.setSourceInterface(srcPort);
		}

	}

	/**
	 * Check whether an SDFEdge and its source and target can be removed safely
	 * from their SDFGraph and replaced by direct edges or not
	 * 
	 * @param edge
	 *            the SDFEdge we want to remove
	 * @return true if we can remove edge, false otherwise
	 * @throws InvalidExpressionException
	 */
	// TODO cguy: Check we do not allow unsafe edge removal
	private boolean canBeRemovedSafely(SDFEdge edge)
			throws InvalidExpressionException {
		boolean result = false;
		// We can only remove safely edges between join and fork vertices during
		// this pass
		if (isBetweenJoinAndFork(edge)) {
			SDFJoinVertex source = (SDFJoinVertex) edge.getSource();
			SDFForkVertex target = (SDFForkVertex) edge.getTarget();
			// XXX cguy: Should we consider other cases?
			// We consider only join/fork vertices with homogenous cons/prod
			// respectively
			if (hasHomogenousCons(source) && hasHomogenousProd(target)) {
				int nbForkEdges = target.getOutgoingConnections().size();
				int nbJoinEdges = source.getIncomingConnections().size();
				// In order to reduce the number of join/fork vertices the
				// #edges entering/leaving the vertices must be multiple one
				// from another
				result = (Math.max(nbForkEdges, nbJoinEdges)
						% Math.min(nbForkEdges, nbJoinEdges) == 0);
			}

		}
		return result;
	}

	private boolean isBetweenJoinAndFork(SDFEdge edge) {
		return (edge.getSource() instanceof SDFJoinVertex)
				&& (edge.getTarget() instanceof SDFForkVertex);
	}

	/**
	 * Check whether all the outgoing edges of an SDFForkVertex have the same
	 * production rate or not
	 * 
	 * @param target
	 *            the SDFForkVertex for which we check the production rates
	 * @return true if all the outgoing edges of target have the same production
	 *         rate, false otherwise
	 * @throws InvalidExpressionException
	 */
	private boolean hasHomogenousProd(SDFForkVertex target)
			throws InvalidExpressionException {
		int forkProd = target.getOutgoingConnections().get(0).getProd()
				.intValue();
		for (SDFEdge outEdge : target.getOutgoingConnections()) {
			if (outEdge.getProd().intValue() != forkProd) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether all the incoming edges of an SDFJoinVertex have the same
	 * consumption rate or not
	 * 
	 * @param source
	 *            the SDFJoinVertex for which we check the production rates
	 * @return true if all the incoming edges of target have the same
	 *         consumption rate, false otherwise
	 * @throws InvalidExpressionException
	 */
	private boolean hasHomogenousCons(SDFJoinVertex source)
			throws InvalidExpressionException {
		int joinCons = source.getIncomingConnections().get(0).getCons()
				.intValue();
		for (SDFEdge inEdge : source.getIncomingConnections()) {
			if (inEdge.getCons().intValue() != joinCons) {
				return false;
			}
		}
		return true;
	}
}
