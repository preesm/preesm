/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.mapper.timekeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.EdgeTiming;
import org.ietr.preesm.mapper.model.property.VertexTiming;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;

/**
 * Visitor computing the TLevel of each actor firing
 * 
 * @author mpelcat
 */
public class TLevelVisitor implements
		IGraphVisitor<MapperDAG, MapperDAGVertex, MapperDAGEdge> {

	/**
	 * Vertices which TLevel needs to be recomputed
	 */
	private Set<MapperDAGVertex> dirtyVertices;

	public TLevelVisitor(Set<MapperDAGVertex> dirtyVertices) {
		super();
		this.dirtyVertices = dirtyVertices;
	}

	/**
	 * Visiting a graph in topological order to assign t-levels
	 */
	@Override
	public void visit(MapperDAG dag) {
		// Visiting a DAG consists in computing T Levels for all its vertices,
		// starting from vertices without predecessors
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(dag);

		// Recomputing all TLevels
		if (dirtyVertices.isEmpty()) {
			while (iterator.hasNext()) {
				DAGVertex next = iterator.next();
				try {
					next.accept(this);
				} catch (SDF4JException e) {
					e.printStackTrace();
				}
			}
		} else {
			boolean dirty = false;
			while (iterator.hasNext()) {
				DAGVertex next = iterator.next();
				if (!dirty) {
					dirty |= dirtyVertices.contains(next);
				}
				if (dirty) {
					try {
						next.accept(this);
					} catch (SDF4JException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Visiting a vertex to assign t-levels. Prececessors are considered already
	 * visited. Successors are accepted
	 */
	@Override
	public void visit(MapperDAGVertex dagVertex) throws SDF4JException {
		long maxTLevel = -1;
		VertexTiming timing = dagVertex.getTiming();

		// Synchronized vertices are taken into account to compute t-level
		List<MapperDAGVertex> synchroVertices = timing
				.getVertices((MapperDAG) dagVertex.getBase());

		if (dagVertex.incomingEdges().isEmpty()) {
			timing.setTLevel(0l);
		} else {
			Map<MapperDAGVertex, MapperDAGEdge> predecessors = new HashMap<MapperDAGVertex, MapperDAGEdge>();

			for (MapperDAGVertex v : synchroVertices) {
				Map<MapperDAGVertex, MapperDAGEdge> preds = v
						.getPredecessors(false);
				predecessors.putAll(preds);
			}

			// From predecessors, computing the earliest time that the
			// vertex can start
			for (MapperDAGVertex pred : predecessors.keySet()) {
				VertexTiming predTiming = pred.getTiming();
				EdgeTiming edgeTiming = predecessors.get(pred).getTiming();
				if (predTiming.hasTLevel() && predTiming.hasCost()
						&& edgeTiming.hasCost()) {
					long currentTLevel = predTiming.getTLevel()
							+ predTiming.getCost() + edgeTiming.getCost();
					if (currentTLevel > maxTLevel) {
						maxTLevel = currentTLevel;
					}
				} else {
					timing.resetTLevel();
				}
			}

			if (maxTLevel >= 0) {
				timing.setTLevel(maxTLevel);
			}
		}
	}

	@Override
	public void visit(MapperDAGEdge dagEdge) {

	}

}