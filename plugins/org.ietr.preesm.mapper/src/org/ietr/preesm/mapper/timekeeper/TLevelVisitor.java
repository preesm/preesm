/**
 * 
 */
package org.ietr.preesm.mapper.timekeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.EdgeTiming;
import org.ietr.preesm.mapper.model.property.VertexTiming;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;
import org.jgrapht.alg.CycleDetector;

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
	 * Method to detect bugs
	 */
	private void detectCycle(MapperDAG dag) {

		// TODO: delete test code
		CycleDetector<DAGVertex, DAGEdge> cd = new CycleDetector<DAGVertex, DAGEdge>(dag);
		if (cd.detectCycles()) {
			System.out.println("cycle detected");
			System.out.println("cycle " + cd.findCycles());
		}
	}

	/**
	 * Visiting a graph in topological order to assign t-levels
	 */
	@Override
	public void visit(MapperDAG dag) {
		// Visiting a DAG consists in computing T Levels for all its vertices,
		// starting from vertices without predecessors
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(dag);

		detectCycle(dag);

		/*
		 * //TODO: delete test code List<MapperDAGVertex> orderedVList = new
		 * ArrayList<MapperDAGVertex>(); // On the whole group otherwise
		 * CustomTopologicalIterator iterator2 = new
		 * CustomTopologicalIterator(dag,true); while(iterator2.hasNext()){
		 * MapperDAGVertex v = iterator2.next(); orderedVList.add(v); }
		 * System.out.println("list" + orderedVList);
		 */

		try {
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
					// TODO: Remove test
					// System.out.println(next.getName());
					dag.getPredecessorVerticesOf(next);
					if (!dirty) {
						dirty |= dirtyVertices.contains(next);
					}
					if (dirty) {
						next.accept(this);
					}
				}
			}
		} catch (SDF4JException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
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