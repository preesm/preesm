/**
 * 
 */
package org.ietr.preesm.mapper.timekeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.visitors.IGraphVisitor;
import net.sf.dftools.algorithm.model.visitors.SDF4JException;

import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.EdgeTiming;
import org.ietr.preesm.mapper.model.property.VertexTiming;
import org.ietr.preesm.mapper.tools.CustomTopologicalIterator;

/**
 * Visitor computing the BLevel of each actor firing.
 * T levels are considered to be valid.
 * 
 * @author mpelcat
 */
public class BLevelVisitor implements
		IGraphVisitor<MapperDAG, MapperDAGVertex, MapperDAGEdge> {

	/**
	 * Visiting a graph in topological order to assign t-levels
	 */
	@Override
	public void visit(MapperDAG dag) {
		// Visiting a DAG consists in computing T Levels for all its vertices,
		// starting from vertices without predecessors
		CustomTopologicalIterator iterator = new CustomTopologicalIterator(dag,false);
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
		
		if(dagVertex.outgoingEdges().isEmpty()){
			timing.setBLevel(0l);
			return;
		}
		
		// Synchronized vertices are taken into account to compute t-level
		List<MapperDAGVertex> synchroVertices = timing.getVertices((MapperDAG)dagVertex.getBase());
		Map<MapperDAGVertex, MapperDAGEdge> successors = new HashMap<MapperDAGVertex, MapperDAGEdge>();
		
		for(MapperDAGVertex v : synchroVertices){
			Map<MapperDAGVertex, MapperDAGEdge> succs = v.getSuccessors(false);
			successors.putAll(succs);
		}

		// From successors, computing the b-level
		for (MapperDAGVertex succ : successors.keySet()) {
			VertexTiming succTiming = succ.getTiming();
			EdgeTiming edgeTiming = successors.get(succ).getTiming();
			if (succTiming.hasBLevel() && timing.hasCost()
					&& edgeTiming.hasCost()) {
				long currentBLevel = succTiming.getBLevel()
						+ timing.getCost() + edgeTiming.getCost();
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