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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.visitors.SingleRateChecker;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;

/**
 * Class cleaning the useless join-fork pairs of vertices which may have been
 * introduced by hierarchy flattening and single rate transformation
 * 
 * @author cguy
 * @author kdesnos
 * 
 */
public class JoinForkCleaner {

	/**
	 * Top method to call in order to remove all the join-fork pairs which can
	 * be removed safely from an SDFGraph.<br>
	 * <br>
	 * 
	 * <b>This algorithm should be called only on single-rate graphs.
	 * Verification is performed.</b>
	 * 
	 * @param graph
	 *            the SDFGraph we want to clean
	 * @throws InvalidExpressionException
	 * 
	 * @return true if some join-fork pairs has be removed
	 * @throws SDF4JException
	 */
	static public boolean cleanJoinForkPairsFrom(SDFGraph graph) throws InvalidExpressionException, SDF4JException {
		boolean result = false;

		// Check that the graph is single rate.
		SingleRateChecker srChecker = new SingleRateChecker();
		graph.accept(srChecker);
		if (!srChecker.isSingleRate) {
			throw new SDF4JException("Cannot clean fork/join pairs in a non-single-rate graph.");
		}

		// Set of edges to remove from graph
		SDFEdge edgeToRemove;

		// Set of vertices to remove from graph
		Set<SDFAbstractVertex> verticesToRemove;

		boolean changeDone;
		do {
			// reset
			changeDone = false;
			edgeToRemove = null;
			verticesToRemove = new HashSet<SDFAbstractVertex>();

			// Scan every edge e of the graph
			// Stop as soon as there is an edge between a join and a fork
			for (SDFEdge e : graph.edgeSet()) {
				// Check whether it stands between an SDFJoinVertex and an
				// SDFForkVertex and whether it's safe to remove it
				if (isBetweenJoinAndFork(e)) {
					// If it is the case, add e, its source and its target to
					// the
					// elements to remove from graph
					edgeToRemove = e;
					verticesToRemove.add(e.getSource());
					verticesToRemove.add(e.getTarget());
					break;
				}
			}

			result |= changeDone = edgeToRemove != null;

			// if any, replace edgeToRemove
			if (edgeToRemove != null) {
				// Use the single-rate transformation to replace the edge.
				replaceEdge(edgeToRemove, graph);

				// Finally, remove all useless elements from graph
				graph.removeEdge(edgeToRemove);
				graph.removeAllVertices(verticesToRemove);
			}
		} while (changeDone);

		return result;
	}

	/**
	 * Replace an {@link SDFEdge} between a {@link SDFJoinVertex} and an
	 * {@link SDFForkVertex} of a single-rate {@link SDFGraph} with equivalent
	 * connections so that the two {@link SDFForkVertex} and
	 * {@link SDFJoinVertex} vertices can be removed from the graph.
	 * 
	 * <b>The code of this method was strongly inspired by the HSDF
	 * transformation, if bugs are found here, it is likely they exist also
	 * there</b> (sorry for the poor code design).
	 * 
	 * @param replacedEdge
	 *            the {@link SDFEdge} to replace.
	 * @param graph
	 *            the processed single-rate {@link SDFGraph}.
	 * @throws InvalidExpressionException
	 *             if some expressions associated to data ports or delays are
	 *             invalid.
	 */
	static private void replaceEdge(SDFEdge replacedEdge, SDFGraph graph) throws InvalidExpressionException {

		// Retrieve the sources and targets actor to connect, as well as all
		// edges replaced b this algorithm.
		Vector<SDFAbstractVertex> sourceCopies = new Vector<SDFAbstractVertex>();
		Vector<SDFEdge> sourceEdges = new Vector<SDFEdge>();
		// retrieve connection in the right order
		for (SDFEdge inEdge : ((SDFJoinVertex) replacedEdge.getSource()).getIncomingConnections()) {
			sourceCopies.add(inEdge.getSource());
			sourceEdges.add(inEdge);
		}

		Vector<SDFAbstractVertex> targetCopies = new Vector<SDFAbstractVertex>();
		Vector<SDFEdge> targetEdges = new Vector<SDFEdge>();
		// retrieve connection in the right order
		for (SDFEdge outEdge : ((SDFForkVertex) replacedEdge.getTarget()).getOutgoingConnections()) {
			targetCopies.add(outEdge.getTarget());
			targetEdges.add(outEdge);
		}

		Vector<SDFAbstractVertex> originalSourceCopies = new Vector<>(sourceCopies);
		Vector<SDFAbstractVertex> originalTargetCopies = new Vector<>(targetCopies);

		// Delays of the edge between the fork and the join
		int nbDelays = replacedEdge.getDelay().intValue();

		// Total number of token exchanged (produced and consumed) for this edge
		int totalNbTokens = replacedEdge.getCons().intValue();

		// Absolute target is the targeted consumed token among the total
		// number of consumed/produced tokens
		int absoluteTarget = nbDelays;
		int absoluteSource = 0;

		// totProd is updated to store the number of token consumed by the
		// targets that are "satisfied" by the added edges.
		int totProd = 0;

		List<SDFEdge> newEdges = new ArrayList<SDFEdge>();
		// Add edges until all consumed token are "satisfied"
		while (totProd < totalNbTokens) {

			// sourceProd and targetCons are the number of token already
			// produced/consumed by the currently indexed source/target
			int sourceProd = 0;
			int targetCons = 0;
			// Index of the currently processed sourceVertex among the
			// duplicates of the current edge source.
			int sourceIndex = 0;
			{
				int producedTokens = 0;
				while (producedTokens < absoluteSource) {
					producedTokens += sourceEdges.get(sourceIndex).getProd().intValue();
					sourceIndex++; // no need of modulo, producers are scanned
									// once.
				}
				if (producedTokens > absoluteSource) {
					sourceIndex--; // no need of modulo, producers are scanned
									// once.
					producedTokens -= sourceEdges.get(sourceIndex).getProd().intValue();
					sourceProd = absoluteSource - producedTokens;
				}
			}

			// targetIndex is used to know which duplicates of the target
			// will
			// be targeted by the currently indexed copy of the source.
			int targetIndex = 0;
			{
				int consumedTokens = 0;
				while (consumedTokens < absoluteTarget) {
					consumedTokens += targetEdges.get(targetIndex).getCons().intValue();
					targetIndex = (targetIndex + 1) % targetEdges.size();
				}
				if (consumedTokens > absoluteTarget) {
					targetIndex = (targetIndex - 1 + targetEdges.size()) % targetEdges.size(); // modulo
																								// because
																								// of
																								// delays
					consumedTokens -= targetEdges.get(targetIndex).getCons().intValue();
					targetCons = absoluteTarget - consumedTokens;
				}
			}

			// rest is both the production and consumption rate on the
			// created edge.
			int rest = Math.min(sourceEdges.get(sourceIndex).getProd().intValue() - sourceProd,
					targetEdges.get(targetIndex).getCons().intValue() - targetCons);

			// This int represent the number of iteration separating the
			// currently indexed source and target (between which an edge is
			// added)
			// If this int is > to 0, this means that the added edge must
			// have
			// delays (with delay=prod=cons of the added edge)
			// Warning, this integer division is not factorable
			int iterationDiff = absoluteTarget / totalNbTokens - absoluteSource / totalNbTokens;

			// Testing zone beginning
			// for inserting explode and implode vertices
			// boolean set to true if an explode should be added
			boolean explode = rest < sourceEdges.get(sourceIndex).getProd().intValue();
			boolean implode = rest < targetEdges.get(targetIndex).getCons().intValue();
			if (explode && !(sourceCopies.get(sourceIndex) instanceof SDFForkVertex)
					&& !(sourceCopies.get(sourceIndex) instanceof SDFBroadcastVertex)) {

				// If an explode must be added
				SDFAbstractVertex explodeVertex = new SDFForkVertex();
				graph.addVertex(explodeVertex);
				SDFAbstractVertex originVertex = sourceCopies.get(sourceIndex);
				explodeVertex.setName("explode_" + originVertex.getName() + "_"
						+ sourceEdges.get(sourceIndex).getSourceInterface().getName());

				// Replace the source vertex by the explode in the
				// sourceCopies list
				sourceCopies.set(sourceIndex, explodeVertex);

				// Add an edge between the source and the explode
				SDFEdge newEdge = graph.addEdge(originVertex, explodeVertex);
				newEdge.setDelay(new SDFIntEdgePropertyType(0));
				newEdge.setProd(new SDFIntEdgePropertyType(sourceEdges.get(sourceIndex).getProd().intValue()));
				newEdge.setCons(new SDFIntEdgePropertyType(sourceEdges.get(sourceIndex).getProd().intValue()));
				newEdge.setDataType(sourceEdges.get(sourceIndex).getDataType());
				newEdge.setSourceInterface(sourceEdges.get(sourceIndex).getSourceInterface());
				newEdge.setTargetInterface(sourceEdges.get(sourceIndex).getTargetInterface());
				newEdge.setSourcePortModifier(sourceEdges.get(sourceIndex).getSourcePortModifier());

				// Add a target port modifier to the edge
				newEdge.setTargetPortModifier(new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
			}
			if (implode && !(targetCopies.get(targetIndex) instanceof SDFJoinVertex)
					&& !(targetCopies.get(targetIndex) instanceof SDFRoundBufferVertex)) {
				// If an implode must be added
				SDFAbstractVertex implodeVertex = new SDFJoinVertex();
				graph.addVertex(implodeVertex);
				SDFAbstractVertex originVertex = targetCopies.get(targetIndex);
				implodeVertex.setName("implode_" + originVertex.getName() + "_"
						+ targetEdges.get(targetIndex).getTargetInterface().getName());

				// Replace the target vertex by the implode one in the
				// targetCopies List
				targetCopies.set(targetIndex, implodeVertex);

				// Add an edge between the implode and the target
				SDFEdge newEdge = graph.addEdge(implodeVertex, originVertex);
				newEdge.setDelay(new SDFIntEdgePropertyType(0));
				newEdge.setProd(new SDFIntEdgePropertyType(targetEdges.get(targetIndex).getCons().intValue()));
				newEdge.setCons(new SDFIntEdgePropertyType(targetEdges.get(targetIndex).getCons().intValue()));
				newEdge.setDataType(targetEdges.get(targetIndex).getDataType());
				newEdge.setSourceInterface(targetEdges.get(targetIndex).getSourceInterface());
				newEdge.setTargetInterface(targetEdges.get(targetIndex).getTargetInterface());
				newEdge.setTargetPortModifier(targetEdges.get(targetIndex).getTargetPortModifier());

				// Add a source port modifier to the edge
				newEdge.setSourcePortModifier(new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
			}
			// end of testing zone

			// Create the new Edge for the output graph
			SDFEdge newEdge = graph.addEdge(sourceCopies.get(sourceIndex), targetCopies.get(targetIndex));
			newEdges.add(newEdge);

			// Set the source interface of the new edge
			// If the source is a newly added fork/broadcast (or extra
			// output added to existing fork/broadcast) we rename the
			// new output ports. Contrary to ports of join/roundbuffer, no
			// special processing is needed to order the edges.
			if (sourceCopies.get(sourceIndex) == originalSourceCopies.get(sourceIndex)
					&& (!explode || !((originalSourceCopies.get(sourceIndex) instanceof SDFBroadcastVertex)
							|| (originalSourceCopies.get(sourceIndex) instanceof SDFForkVertex)))) {
				// If the source does not need new ports
				if (sourceCopies.get(sourceIndex)
						.getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName()) != null) {
					// if the source already has the appropriate interface
					newEdge.setSourceInterface(sourceCopies.get(sourceIndex)
							.getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName()));
				} else {
					// if the source does not have the interface.
					newEdge.setSourceInterface(sourceEdges.get(sourceIndex).getSourceInterface().clone());
				}
				// Copy the source port modifier of the original source
				newEdge.setSourcePortModifier(sourceEdges.get(sourceIndex).getSourcePortModifier());
			} else {
				// If the source is a fork (new or not)
				// or a broadcast with a new port
				SDFInterfaceVertex sourceInterface = sourceEdges.get(sourceIndex).getSourceInterface().clone();

				String newInterfaceName = sourceInterface.getName() + "_" + sourceProd;

				// Get the current index of the port (if any)
				// and update it
				if (sourceInterface.getName().matches(SpecialActorPortsIndexer.indexRegex)) {
					Pattern pattern = Pattern.compile(SpecialActorPortsIndexer.indexRegex);
					Matcher matcher = pattern.matcher(sourceInterface.getName());
					matcher.find();
					int existingIdx = Integer.decode(matcher.group(SpecialActorPortsIndexer.groupXX));
					int newIdx = existingIdx + sourceProd;
					newInterfaceName = sourceInterface.getName().substring(0,
							matcher.start(SpecialActorPortsIndexer.groupXX)) + newIdx;
				}

				sourceInterface.setName(newInterfaceName);
				newEdge.setSourceInterface(sourceInterface);
				// Add a source port modifier
				newEdge.setSourcePortModifier(new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
			}

			// Set the target interface of the new edge
			// If the target is a newly added join/roundbuffer
			// we need to take extra care to make sure the incoming edges
			// are in the right order (which might be a little bit complex
			// when playing with delays)

			// If the target is not an actor with new ports (because of an
			// explosion)
			if (targetCopies.get(targetIndex) == originalTargetCopies.get(targetIndex)
					&& (!implode || !((originalTargetCopies.get(targetIndex) instanceof SDFRoundBufferVertex)
							|| (originalTargetCopies.get(targetIndex) instanceof SDFJoinVertex)))) {

				// if the target already has the appropriate interface
				if (targetCopies.get(targetIndex)
						.getSource(targetEdges.get(targetIndex).getTargetInterface().getName()) != null) {

					newEdge.setTargetInterface(targetCopies.get(targetIndex)
							.getSource(targetEdges.get(targetIndex).getTargetInterface().getName()));
				}
				// if the target does not have the interface.
				else {
					newEdge.setTargetInterface(targetEdges.get(targetIndex).getTargetInterface().clone());
				}
				// Copy the target port modifier of the original source
				// Except for roundbuffers
				if (!(newEdge.getTarget() instanceof SDFRoundBufferVertex)) {
					newEdge.setTargetPortModifier(targetEdges.get(targetIndex).getTargetPortModifier());
				} else {
					// The processing of roundBuffer portModifiers is done
					// after the while loop
				}
			}
			// If the target is join (new or not) /roundbuffer with new
			// ports
			else {
				SDFInterfaceVertex targetInterface = targetEdges.get(targetIndex).getTargetInterface().clone();

				String newInterfaceName = targetInterface.getName() + "_" + targetCons;
				// Get the current index of the port (if any)
				// and update it
				if (targetInterface.getName().matches(SpecialActorPortsIndexer.indexRegex)) {
					Pattern pattern = Pattern.compile(SpecialActorPortsIndexer.indexRegex);
					Matcher matcher = pattern.matcher(targetInterface.getName());
					matcher.find();
					int existingIdx = Integer.decode(matcher.group(SpecialActorPortsIndexer.groupXX));
					int newIdx = existingIdx + targetCons;
					newInterfaceName = targetInterface.getName().substring(0,
							matcher.start(SpecialActorPortsIndexer.groupXX)) + newIdx;
				}

				targetInterface.setName(newInterfaceName);
				newEdge.setTargetInterface(targetInterface);
				// Add a target port modifier
				newEdge.setTargetPortModifier(new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));

			}

			// Associate the interfaces to the new edge
			if (targetCopies.get(targetIndex) instanceof SDFVertex) {
				if (((SDFVertex) targetCopies.get(targetIndex))
						.getSource(targetEdges.get(targetIndex).getTargetInterface().getName()) != null) {
					SDFInterfaceVertex inputVertex = ((SDFVertex) targetCopies.get(targetIndex))
							.getSource(targetEdges.get(targetIndex).getTargetInterface().getName());
					((SDFVertex) targetCopies.get(targetIndex)).setInterfaceVertexExternalLink(newEdge, inputVertex);
				}
			}
			if (sourceCopies.get(sourceIndex) instanceof SDFVertex) {
				if (((SDFVertex) sourceCopies.get(sourceIndex))
						.getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName()) != null) {
					SDFInterfaceVertex outputVertex = ((SDFVertex) sourceCopies.get(sourceIndex))
							.getSink(sourceEdges.get(sourceIndex).getSourceInterface().getName());
					((SDFVertex) sourceCopies.get(sourceIndex)).setInterfaceVertexExternalLink(newEdge, outputVertex);
				}
			}

			// Set the properties of the new edge
			// newEdge.copyProperties(edge);
			newEdge.setProd(new SDFIntEdgePropertyType(rest));
			newEdge.setCons(new SDFIntEdgePropertyType(rest));
			newEdge.setDataType(replacedEdge.getDataType());

			// If the replacedEdge has a delay and that delay still exist in the
			// SRSDF (i.e. if the source & target do not belong to the same
			// "iteration")
			if (iterationDiff > 0) {
				int addedDelays = (iterationDiff * newEdge.getCons().intValue());
				// Check that there are enough delays available
				if (nbDelays < addedDelays) {
					// kdesnos: I added this check, but it will most
					// probably never happen
					throw new RuntimeException("Insufficient delays on edge " + replacedEdge.getSource().getName() + "."
							+ replacedEdge.getSourceInterface().getName() + "=>" + replacedEdge.getTarget().getName()
							+ "." + replacedEdge.getTargetInterface().getName() + ". At least " + addedDelays
							+ " delays missing.");
				}
				newEdge.setDelay(new SDFIntEdgePropertyType(addedDelays));
				nbDelays = nbDelays - addedDelays;
			} else {
				newEdge.setDelay(new SDFIntEdgePropertyType(0));
			}

			// Preserve delays of sourceEdge and targetEdge.
			if (sourceEdges.get(sourceIndex).getDelay().intValue() > 0
					|| targetEdges.get(targetIndex).getDelay().intValue() > 0) {
				// Number of delays is a multiplier of production/consumption
				// rate (since the graph is single-rate).
				int multSource = sourceEdges.get(sourceIndex).getDelay().intValue()
						/ sourceEdges.get(sourceIndex).getProd().intValue();
				int multTarget = targetEdges.get(sourceIndex).getDelay().intValue()
						/ targetEdges.get(sourceIndex).getCons().intValue();

				// Compute the new number of delays
				int nbPreservedDelays = newEdge.getDelay().intValue() + multSource * newEdge.getProd().intValue()
						+ multTarget * newEdge.getProd().intValue();

				// Add the delays to the newEdge
				newEdge.setDelay(new SDFIntEdgePropertyType(nbPreservedDelays));
			}

			// Update the number of token produced/consumed by the currently
			// indexed source/target
			absoluteTarget += rest;
			absoluteSource += rest;

			// Update the totProd for the current edge (totProd is used in
			// the condition of the While loop)
			totProd += rest;
		}

		// Make sure all ports are in order
		if (!SpecialActorPortsIndexer.checkIndexes(graph)) {
			throw new RuntimeException(
					"There are still special actors with non-indexed ports. Contact Preesm developers.");
		}

		SpecialActorPortsIndexer.sortIndexedPorts(graph);
	}

	static private boolean isBetweenJoinAndFork(SDFEdge edge) {
		return (edge.getSource() instanceof SDFJoinVertex) && (edge.getTarget() instanceof SDFForkVertex);
	}
}
