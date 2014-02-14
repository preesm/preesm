/**
 * 
 */
package org.ietr.preesm.mapper.timekeeper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.VertexTiming;

/**
 * New version of the time keeper. Trying to minimize the mapping time by
 * reducing the evaluation time of timings.
 * 
 * @author mpelcat
 */
public class TimeKeeper implements Observer {

	/**
	 * If debug mode is activated, timing actions are traced
	 */
	//private boolean debugMode = true;

	/**
	 * Current implementation: the same as in the ABC
	 */
	protected MapperDAG implementation;

	/**
	 * Manager of the vertices ordering
	 */
	private OrderManager orderManager;

	/**
	 * Vertices which timings need to be recomputed
	 */
	private Set<MapperDAGVertex> dirtyVertices;

	/**
	 * Constructor
	 */
	public TimeKeeper(MapperDAG implementation, OrderManager orderManager) {

		this.implementation = implementation;
		this.orderManager = orderManager;
		this.orderManager.addObserver(this);
		dirtyVertices = new HashSet<MapperDAGVertex>();
	}

	/**
	 * Resets the time keeper timings of the whole DAG
	 */
	public void resetTimings() {
		Iterator<DAGVertex> it = implementation.vertexSet().iterator();

		while (it.hasNext()) {
			((MapperDAGVertex) it.next()).getTiming().reset();
		}
	}

	// // Final Time Section

	/**
	 * Gives the final time of the given vertex in the current implementation.
	 * If current implementation information is not enough to calculate this
	 * timing, returns UNAVAILABLE
	 */
	public long getFinalTime(MapperDAGVertex vertex) {

		long vertexfinaltime = VertexTiming.UNAVAILABLE;
		VertexTiming timingproperty = vertex.getTiming();
		if (vertex.getTiming().hasCost()) {
			// Returns, if possible, TLevel + vertex timing
			vertexfinaltime = vertex.getTiming().getCost()
					+ timingproperty.getTLevel();
		}

		return vertexfinaltime;
	}

	/**
	 * Gives the total implementation time if possible. If current
	 * implementation information is not enough to calculate this timing,
	 * returns UNAVAILABLE
	 */
	public long getFinalTime() {

		long finaltime = VertexTiming.UNAVAILABLE;

		for (ComponentInstance o : orderManager.getArchitectureComponents()) {
			long nextFinalTime = getFinalTime(o);
			// Returns TimingVertexProperty.UNAVAILABLE if at least one
			// vertex has no final time. Otherwise returns the highest final
			// time
			if (nextFinalTime == VertexTiming.UNAVAILABLE) {
				return VertexTiming.UNAVAILABLE;
			} else
				finaltime = Math.max(finaltime, nextFinalTime);
		}

		return finaltime;
	}

	/**
	 * Gives the implementation time on the given operator if possible. It
	 * considers a partially mapped graph and ignores the non mapped vertices
	 */
	public long getFinalTime(ComponentInstance component) {

		long finaltime = VertexTiming.UNAVAILABLE;

		ComponentInstance finalTimeRefCmp = null;
		for (ComponentInstance o : orderManager.getArchitectureComponents()) {
			if (o.getInstanceName().equals(component.getInstanceName())) {
				finalTimeRefCmp = o;
			}
		}

		if (finalTimeRefCmp != null) {
			List<MapperDAGVertex> sched = orderManager
					.getVertexList(finalTimeRefCmp);

			if (sched != null && !sched.isEmpty()) {
				finaltime = getFinalTime(sched.get(sched.size() - 1));
			} else {
				finaltime = 0;
			}
		}

		return finaltime;
	}

	public void updateTLevels() {
		TLevelVisitor tLevelVisitor = new TLevelVisitor(dirtyVertices);
		tLevelVisitor.visit(implementation);
		dirtyVertices.clear();
	}

	public void updateTandBLevels() {
		TLevelVisitor tLevelVisitor = new TLevelVisitor(dirtyVertices);
		BLevelVisitor bLevelVisitor = new BLevelVisitor();
		tLevelVisitor.visit(implementation);
		bLevelVisitor.visit(implementation);
		dirtyVertices.clear();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(Observable arg0, Object arg1) {
		if(arg1 instanceof MapperDAGVertex){
			dirtyVertices.add((MapperDAGVertex)arg1);
		}
		else if(arg1 instanceof Set<?>){
			dirtyVertices.addAll((Set<MapperDAGVertex>)arg1);
		}
	}
}
