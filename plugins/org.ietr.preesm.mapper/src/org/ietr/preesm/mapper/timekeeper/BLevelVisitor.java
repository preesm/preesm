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

import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.EdgeTiming;
import org.ietr.preesm.mapper.model.property.VertexTiming;
import org.ietr.preesm.mapper.tools.CustomTopologicalIterator;

/**
 * Visitor computing the BLevel of each actor firing. T levels are considered to
 * be valid.
 * 
 * @author mpelcat
 */
public class BLevelVisitor implements
		IGraphVisitor<MapperDAG, MapperDAGVertex, MapperDAGEdge> {

	/**
	 * Visiting a graph in topological order to assign b-levels
	 */
	@Override
	public void visit(MapperDAG dag) {
		// Visiting a DAG consists in computing T Levels for all its vertices,
		// starting from vertices without predecessors
		CustomTopologicalIterator iterator = new CustomTopologicalIterator(dag,
				false);
		while (iterator.hasNext()) {
			DAGVertex vertex = iterator.next();
			try {
				vertex.accept(this);
			} catch (SDF4JException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Visiting a vertex to assign b-levels. Successors are considered already
	 * visited
	 */
	@Override
	public void visit(MapperDAGVertex dagVertex) {
		long maxBLevel = -1;
		VertexTiming timing = dagVertex.getTiming();

		if (dagVertex.outgoingEdges().isEmpty()) {
			timing.setBLevel(timing.getCost());
			return;
		}

		// Synchronized vertices are taken into account to compute b-level
		List<MapperDAGVertex> synchroVertices = timing
				.getVertices((MapperDAG) dagVertex.getBase());
		Map<MapperDAGVertex, MapperDAGEdge> successors = new HashMap<MapperDAGVertex, MapperDAGEdge>();

		for (MapperDAGVertex v : synchroVertices) {
			Map<MapperDAGVertex, MapperDAGEdge> succs = v.getSuccessors(false);
			successors.putAll(succs);
		}

		// From successors, computing the b-level
		for (MapperDAGVertex succ : successors.keySet()) {
			VertexTiming succTiming = succ.getTiming();
			EdgeTiming edgeTiming = successors.get(succ).getTiming();
			if (succTiming.hasBLevel() && timing.hasCost()
					&& edgeTiming.hasCost()) {
				long currentBLevel = succTiming.getBLevel() + timing.getCost()
						+ edgeTiming.getCost();
				if (currentBLevel > maxBLevel) {
					maxBLevel = currentBLevel;
				}
			} else {
				timing.resetBLevel();
			}
		}

		if (maxBLevel >= 0) {
			timing.setBLevel(maxBLevel);
		}
	}

	@Override
	public void visit(MapperDAGEdge dagEdge) {

	}

}