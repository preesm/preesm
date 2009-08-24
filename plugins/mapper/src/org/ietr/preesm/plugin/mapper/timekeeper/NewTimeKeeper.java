/**
 * 
 */
package org.ietr.preesm.plugin.mapper.timekeeper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.order.Schedule;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;
import org.ietr.preesm.plugin.mapper.model.impl.InvolvementVertex;
import org.ietr.preesm.plugin.mapper.model.impl.OverheadVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * New version of the time keeper. Trying to minimize the mapping time by
 * reducing the evaluation time of timings.
 * 
 * @author mpelcat
 */
public class NewTimeKeeper implements Observer {

	/**
	 * Current implementation: the same as in the ABC
	 */
	protected MapperDAG implementation;

	/**
	 * Helper to scan the neighbors of a vertex
	 */
	private DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex;

	/**
	 * In order to minimize recalculation, a set of modified vertices is kept
	 */
	private Set<DAGVertex> dirtyTLevelVertices;

	/**
	 * Manager of the vertices ordering
	 */
	private SchedOrderManager orderManager;

	/**
	 * Constructor
	 */
	public NewTimeKeeper(MapperDAG implementation,
			SchedOrderManager orderManager) {

		this.implementation = implementation;
		neighborindex = null;
		dirtyTLevelVertices = new HashSet<DAGVertex>();
		this.orderManager = orderManager;
		this.orderManager.addObserver(this);
	}

	/**
	 * Resets the time keeper timings of the whole DAG
	 */
	public void resetTimings() {
		Iterator<DAGVertex> it = implementation.vertexSet().iterator();

		while (it.hasNext()) {
			((MapperDAGVertex) it.next()).getTimingVertexProperty().reset();
		}
	}

	// // T Level calculation

	/**
	 * Observer update notifying that a vertex status has changed and its
	 * timings need recalculation
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null) {
			if (arg1 instanceof Set) {
				dirtyTLevelVertices.addAll((Set<DAGVertex>) arg1);
			} else if (arg1 instanceof MapperDAGVertex) {
				dirtyTLevelVertices.add((MapperDAGVertex) arg1);
			}
		}
	}

	private void calculateTLevel() {

		DirectedGraph<DAGVertex, DAGEdge> castAlgo = implementation;
		neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(castAlgo);

		/*
		 * allDirtyVertices contains the vertices dirty because of
		 * implementation modification or neighbors modification
		 */

		Iterator<DAGVertex> vIt = dirtyTLevelVertices.iterator();
		while (vIt.hasNext()) {
			DAGVertex v = vIt.next();
			if (!implementation.vertexSet().contains(v)) {
				vIt.remove();
			}
		}

		Set<DAGVertex> allDirtyTLevelVertices = new HashSet<DAGVertex>(
				dirtyTLevelVertices);

		for (DAGVertex v : allDirtyTLevelVertices) {
			if (dirtyTLevelVertices.contains(v))
				calculateTLevel((MapperDAGVertex) v);
		}
	}

	/**
	 * calculating top time (or tLevel) of modified vertex and all its
	 * successors.
	 */
	private void calculateTLevel(MapperDAGVertex modifiedvertex) {

		TimingVertexProperty currenttimingproperty = modifiedvertex
				.getTimingVertexProperty();

		// If the current vertex has an effective component
		if (modifiedvertex.getImplementationVertexProperty()
				.hasEffectiveComponent()) {

			Set<DAGVertex> predset = neighborindex
					.predecessorsOf(modifiedvertex);

			// If the vertex has no predecessor, ALAP=ASAP=0;
			// t-level = ASAP
			if (predset.isEmpty()) {
				currenttimingproperty.setNewtLevel(0);
			} else {
				// The T level is the time of the longest preceding path
				long l = getLongestPrecedingPath(predset, modifiedvertex);
				currenttimingproperty.setNewtLevel(l);
			}

		} else {
			// If the current vertex has no effective component
			currenttimingproperty
					.setNewtLevel(TimingVertexProperty.UNAVAILABLE);
		}

		dirtyTLevelVertices.remove(modifiedvertex);
	}

	/**
	 * given the set of preceding vertices, returns the finishing time of the
	 * longest path reaching the vertex testedvertex
	 * 
	 * @return last finishing time
	 */
	private long getLongestPrecedingPath(Set<DAGVertex> graphset,
			MapperDAGVertex inputvertex) {

		long timing = TimingVertexProperty.UNAVAILABLE;

		if (!inputvertex.getImplementationVertexProperty()
				.hasEffectiveComponent()) {
			PreesmLogger.getLogger().log(
					Level.INFO,
					"tLevel unavailable for vertex " + inputvertex
							+ ". No effective component.");
			return TimingVertexProperty.UNAVAILABLE;
		}

		// We iterate a set of preceding vertices of inputvertex
		for (DAGVertex dagV : graphset) {
			MapperDAGVertex vertex = (MapperDAGVertex) dagV;
			TimingVertexProperty vertexTProperty = vertex
					.getTimingVertexProperty();

			// If we lack information on predecessors, path calculation fails
			// No recalculation of predecessor T Level if already calculated
			if (dirtyTLevelVertices.contains(vertex)) {
				if (vertex.getImplementationVertexProperty()
						.hasEffectiveComponent()) {
					calculateTLevel(vertex);
				}
			}

			// If we could not calculate the T level of the predecessor,
			// calculation fails
			if (!vertexTProperty.hasCost()
					|| dirtyTLevelVertices.contains(vertex)) {
				PreesmLogger.getLogger().log(
						Level.INFO,
						"tLevel unavailable for vertex " + inputvertex
								+ ". Lacking information on predecessor "
								+ vertex + ".");
				return TimingVertexProperty.UNAVAILABLE;
			}

			long newPathLength = getVertexTLevelFromPredecessor(vertex,
					inputvertex);

			// Keeping the longest preceding path
			if (timing < newPathLength) {
				timing = newPathLength;
			}
		}

		return timing;
	}

	private long getVertexTLevelFromPredecessor(MapperDAGVertex pred,
			MapperDAGVertex current) {

		MapperDAGEdge edge = (MapperDAGEdge) implementation.getEdge(pred,
				current);
		TimingVertexProperty predTProperty = pred.getTimingVertexProperty();
		long edgeCost = edge.getTimingEdgeProperty().getCost();
		long newPathLength = predTProperty.getNewtLevel()
				+ predTProperty.getCost() + edgeCost;

		return newPathLength;
	}

	// // B Level Section

	/**
	 * calculating bottom times of each vertex. A b-level is the difference
	 * between the start time of the task and the end time of the longest branch
	 * containing the vertex.
	 */
	public void calculateBLevel() {

		MapperDAGVertex currentvertex;

		Iterator<DAGVertex> iterator = implementation.vertexSet().iterator();

		// We iterate the dag tree in topological order to calculate b-level

		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex) iterator.next();

			// Starting from end vertices, sets the b-levels of the preceding
			// tasks
			if (currentvertex.outgoingEdges().isEmpty())
				calculateBLevel(currentvertex);

		}
	}

	/**
	 * calculating bottom time of a vertex without successors.
	 */
	public void calculateBLevel(MapperDAGVertex modifiedvertex) {

		TimingVertexProperty currenttimingproperty = modifiedvertex
				.getTimingVertexProperty();
		DirectedGraph<DAGVertex, DAGEdge> castAlgo = implementation;
		neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(castAlgo);

		Set<DAGVertex> predset = neighborindex
				.predecessorsOf((MapperDAGVertex) modifiedvertex);
		Set<DAGVertex> succset = neighborindex
				.successorsOf((MapperDAGVertex) modifiedvertex);

		// If the current vertex has an effective component and is an ending
		// vertex
		if (modifiedvertex.getImplementationVertexProperty()
				.hasEffectiveComponent()
				&& succset.isEmpty()) {

			if (currenttimingproperty.hasNewtLevel()
					&& currenttimingproperty.hasCost()) {
				currenttimingproperty.setNewbLevel(currenttimingproperty
						.getCost());

				if (!predset.isEmpty()) {
					// Sets recursively the BLevel of its predecessors
					setPrecedingBlevel(modifiedvertex, predset);
				}
			} else {
				currenttimingproperty
						.setNewbLevel(TimingVertexProperty.UNAVAILABLE);
			}

		} else {

			PreesmLogger
					.getLogger()
					.log(
							Level.SEVERE,
							"Trying to start b_level calculation from a vertex with successors or without implantation.");
			currenttimingproperty
					.setNewbLevel(TimingVertexProperty.UNAVAILABLE);
		}
	}

	/**
	 * recursive method setting the b-level of the preceding tasks given the
	 * b-level of a start task
	 */
	private void setPrecedingBlevel(MapperDAGVertex startvertex,
			Set<DAGVertex> predset) {

		long currentBLevel = 0;
		TimingVertexProperty starttimingproperty = startvertex
				.getTimingVertexProperty();
		boolean hasStartVertexBLevel = starttimingproperty.hasNewblevel();

		Iterator<DAGVertex> iterator = predset.iterator();

		// Sets the b-levels of each predecessor
		while (iterator.hasNext()) {

			MapperDAGVertex currentvertex = (MapperDAGVertex) iterator.next();

			TimingVertexProperty currenttimingproperty = currentvertex
					.getTimingVertexProperty();

			long edgeweight = ((MapperDAGEdge) implementation.getEdge(
					currentvertex, startvertex)).getTimingEdgeProperty()
					.getCost();

			if (hasStartVertexBLevel && currenttimingproperty.hasCost()
					&& edgeweight >= 0) {
				currentBLevel = starttimingproperty.getNewbLevel()
						+ currenttimingproperty.getCost() + edgeweight;

				currenttimingproperty.setNewbLevel(Math.max(
						currenttimingproperty.getNewbLevel(), currentBLevel));

				Set<DAGVertex> newPredSet = neighborindex
						.predecessorsOf(currentvertex);

				if (!newPredSet.isEmpty())
					// Recursively sets the preceding b levels
					setPrecedingBlevel(currentvertex, newPredSet);
			} else {
				currenttimingproperty
						.setNewbLevel(TimingVertexProperty.UNAVAILABLE);
			}

		}
	}

	// // Final Time Section

	/**
	 * Gives the final time of the given vertex in the current implementation.
	 * If current implementation information is not enough to calculate this
	 * timing, returns UNAVAILABLE
	 */
	public long getFinalTime(MapperDAGVertex vertex) {

		long vertexfinaltime = TimingVertexProperty.UNAVAILABLE;
		TimingVertexProperty timingproperty = vertex.getTimingVertexProperty();
		if (vertex.getTimingVertexProperty().hasCost()) {
			if (!dirtyTLevelVertices.contains(vertex)) {
				// Returns, if possible, TLevel + vertex timing
				vertexfinaltime = vertex.getTimingVertexProperty().getCost()
						+ timingproperty.getNewtLevel();
			}
		}

		return vertexfinaltime;
	}

	/**
	 * Gives the total implementation time if possible. If current
	 * implementation information is not enough to calculate this timing,
	 * returns UNAVAILABLE
	 */
	public long getFinalTime() {

		long finaltime = TimingVertexProperty.UNAVAILABLE;

		for (ArchitectureComponent o : orderManager.getArchitectureComponents()) {
			long nextFinalTime = getFinalTime(o);
			// Returns TimingVertexProperty.UNAVAILABLE if at least one
			// vertex has no final time. Otherwise returns the highest final
			// time
			if (nextFinalTime == TimingVertexProperty.UNAVAILABLE) {
				return TimingVertexProperty.UNAVAILABLE;
			} else
				finaltime = Math.max(finaltime, nextFinalTime);
		}

		return finaltime;
	}

	/**
	 * Gives the implementation time on the given operator if possible. It
	 * considers a partially implanted graph and ignores the non implanted
	 * vertices
	 */
	public long getFinalTime(ArchitectureComponent component) {

		long finaltime = TimingVertexProperty.UNAVAILABLE;

		ArchitectureComponent finalTimeRefCmp = null;
		for (ArchitectureComponent o : orderManager.getArchitectureComponents()) {
			if (o.equals(component)) {
				finalTimeRefCmp = o;
			}
		}

		if (finalTimeRefCmp != null) {
			Schedule sched = orderManager.getSchedule(finalTimeRefCmp);

			if (sched != null && !sched.isEmpty()) {
				finaltime = getFinalTime(sched.getLast());
			} else {
				finaltime = 0;
			}
		}

		return finaltime;
	}

	public void updateTLevels() {
		calculateTLevel();
		dirtyTLevelVertices.clear();

		// compareResults();
	}

	public void updateTandBLevels() {
		calculateTLevel();
		dirtyTLevelVertices.clear();
		calculateBLevel();

		// compareResults();
	}

	private void compareResults() {

		Iterator<DAGVertex> it = implementation.vertexSet().iterator();

		while (it.hasNext()) {
			MapperDAGVertex v = ((MapperDAGVertex) it.next());

			TimingVertexProperty tProp = v.getTimingVertexProperty();

			if (tProp.getNewbLevel() != tProp.getNewbLevel()) {
				PreesmLogger.getLogger().log(Level.SEVERE,
						"false bL " + v.getName());
				int i = 0;
				i++;
			} else if (tProp.getNewbLevel() != -1) {
				int i = 1;
				i++;
			}

			if (tProp.getNewtLevel() != tProp.getNewtLevel()) {

				PreesmLogger.getLogger().log(Level.SEVERE,
						"false tL " + v.getName());

				int i = 0;
				i++;
			} else if (tProp.getNewtLevel() != -1) {
				int i = 1;
				i++;
			}
		}
	}
}
