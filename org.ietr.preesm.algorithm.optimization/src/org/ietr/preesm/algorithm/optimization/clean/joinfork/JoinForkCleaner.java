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

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;

/**
 * Class cleaning the useless join-fork pairs of vertices which may have been
 * introduced by hierarchy flattening and single rate transformation
 * 
 * @author cguy
 * 
 */
public class JoinForkCleaner {
	/**
	 * Top method to call in order to remove all the join-fork pairs which can
	 * be removed safely from an SDFGraph
	 * 
	 * @param graph
	 *            the SDFGraph we want to clean
	 */
	public void cleanJoinForkPairsFrom(SDFGraph graph) {
		// Set of edges to remove from graph
		Set<SDFEdge> edgesToRemove = new HashSet<SDFEdge>();
		// Set of vertices to remove from graph
		Set<SDFAbstractVertex> verticesToRemove = new HashSet<SDFAbstractVertex>();
		// For every edge e of the graph
		for (SDFEdge e : graph.edgeSet()) {
			// Check whether it stands between an SDFJoinVertex and an
			// SDFForkVertex and whether it's safe o remove it
			if (isBetweenJoinAndFork(e) && canBeRemovedSafely(e)) {
				// If it is the case, add e, its source and its target to the
				// elements to remove from graph
				edgesToRemove.add(e);
				verticesToRemove.add(e.getSource());
				verticesToRemove.add(e.getTarget());
				// Then, add the edges to replace e
				addDirectEdgesToReplace(e, graph);
			}
		}
		
		//Remove all useless elements from graph
		graph.removeAllEdges(edgesToRemove);
		graph.removeAllVertices(verticesToRemove);
	}

	private boolean isBetweenJoinAndFork(SDFEdge edge) {
		return (edge.getSource() instanceof SDFJoinVertex)
				&& (edge.getTarget() instanceof SDFForkVertex);
	}

	private boolean canBeRemovedSafely(SDFEdge edge) {
		boolean result = false;
		// TODO: Add the different conditions under which we can remove an edge
		// which stands between an SDFJoinVertex and an SDFForkVertex
		return result;
	}

	private void addDirectEdgesToReplace(SDFEdge edge, SDFGraph graph) {
		// TODO Auto-generated method stub

	}
}
