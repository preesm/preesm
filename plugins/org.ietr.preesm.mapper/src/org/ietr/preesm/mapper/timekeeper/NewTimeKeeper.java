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
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.mapper.abc.order.IScheduleElement;
import org.ietr.preesm.mapper.abc.order.SchedOrderManager;
import org.ietr.preesm.mapper.abc.order.SynchronizedVertices;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.TimingVertexProperty;
import org.ietr.preesm.mapper.model.impl.TransferVertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;


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
	private Set<IScheduleElement> dirtyTLevelElts;

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
		dirtyTLevelElts = new HashSet<IScheduleElement>();
		this.orderManager = orderManager;
		this.orderManager.addObserver(this);
	}

	/**
	 * Returns the element corresponding to the vertex in DirtyVertices, if any
	 */
	public IScheduleElement getDirtyScheduleElt(MapperDAGVertex vertex) {
		if (dirtyTLevelElts.contains(vertex)) {
			return vertex;
		}

		for (IScheduleElement elt : dirtyTLevelElts) {
			if (elt instanceof SynchronizedVertices) {
				if (((SynchronizedVertices) elt).vertices().contains(vertex)) {
					return elt;
				}
			}
		}

		return null;
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
	@SuppressWarnings(value = "unchecked")
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null) {
			if (arg1 instanceof Set) {
				dirtyTLevelElts.addAll((Set<IScheduleElement>) arg1);
			} else if (arg1 instanceof IScheduleElement) {
				dirtyTLevelElts.add((IScheduleElement) arg1);
			}
		}
	}

	private void calculateTLevel() {

		// dirtyTLevelElts.addAll(orderManager.getTotalOrder().getList());
		DirectedGraph<DAGVertex, DAGEdge> castAlgo = implementation;
		neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(castAlgo);

		/*
		 * We remove the not usable dirty vertices -> the duplicated ones
		 * including in synchro vertices and the ones not in implementation
		 */
		// Getting all SynchronizedVertices
		Set<IScheduleElement> synchros = new HashSet<IScheduleElement>();
		for (IScheduleElement elt : dirtyTLevelElts) {
			if (elt instanceof SynchronizedVertices) {
				synchros.addAll(((SynchronizedVertices) elt).vertices());
			}
		}

		Iterator<IScheduleElement> eltIt = dirtyTLevelElts.iterator();
		while (eltIt.hasNext()) {
			IScheduleElement elt = eltIt.next();
			if (elt instanceof MapperDAGVertex
					&& (!implementation.vertexSet().contains(elt) || synchros
							.contains(elt))) {
				eltIt.remove();
			} else if (elt instanceof SynchronizedVertices) {
				if (((SynchronizedVertices) elt).isEmpty()) {
					eltIt.remove();
				}
				for (MapperDAGVertex v : ((SynchronizedVertices) elt)
						.vertices()) {
					if (!implementation.vertexSet().contains(v)) {
						eltIt.remove();
						break;
					}
				}
			}
		}

		Set<IScheduleElement> allDirtyTLevelVertices = new HashSet<IScheduleElement>(
				dirtyTLevelElts);

		for (IScheduleElement v : allDirtyTLevelVertices) {
			if (dirtyTLevelElts.contains(v)) {
				// PreesmLogger.getLogger().log(Level.SEVERE,"t-level of " +
				// v.getName());
				calculateTLevel(v);
			}
		}
	}

	/**
	 * calculating top time (or tLevel) of modified vertex and all its
	 * successors.
	 */
	private void calculateTLevel(IScheduleElement modifiedElt) {

		TimingVertexProperty currenttimingproperty = modifiedElt
				.getTimingVertexProperty();

		// If the current vertex has an effective component
		if (modifiedElt instanceof SynchronizedVertices
				|| modifiedElt.getImplementationVertexProperty()
						.hasEffectiveComponent()) {

			Set<MapperDAGVertex> predset = new HashSet<MapperDAGVertex>();
			Set<MapperDAGVertex> inducedPredset = new HashSet<MapperDAGVertex>();

			for (DAGEdge edge : modifiedElt.incomingEdges()) {
				MapperDAGVertex pred = (MapperDAGVertex) implementation
						.getEdgeSource(edge);
				if (pred != null) {
					predset.add(pred);

					if (pred instanceof TransferVertex) {
						inducedPredset.add(((TransferVertex) pred).getSource());
					}
				}
			}

			predset.removeAll(inducedPredset);
			inducedPredset.clear();

			for (IScheduleElement dirtyElt : dirtyTLevelElts) {
				for (MapperDAGVertex predV : predset) {
					if (dirtyElt instanceof SynchronizedVertices) {
						SynchronizedVertices synch = (SynchronizedVertices) dirtyElt;
						if (synch.contains(predV)) {
							inducedPredset.addAll(synch.vertices());
						}
					}
				}
			}

			predset.addAll(inducedPredset);

			long time;

			if (predset.isEmpty()) {
				// If the vertex has no predecessor, ALAP=ASAP=0;
				// t-level = ASAP
				time = 0;
			} else {
				// The T level is the time of the longest preceding path
				time = getLongestPrecedingPath(predset, modifiedElt);
			}

			currenttimingproperty.setNewtLevel(time);

		} else {
			// If the current vertex has no effective component
			currenttimingproperty
					.setNewtLevel(TimingVertexProperty.UNAVAILABLE);
		}

		// The synchro vertices object may have been recreated and thus may be
		// different. Tests are needed to remove it
		dirtyTLevelElts.remove(modifiedElt);
	}

	/**
	 * given the set of preceding vertices, returns the finishing time of the
	 * longest path reaching the vertex testedvertex
	 * 
	 * @return last finishing time
	 */
	private long getLongestPrecedingPath(Set<MapperDAGVertex> graphset,
			IScheduleElement inputElt) {

		long timing = TimingVertexProperty.UNAVAILABLE;

		if (!inputElt.getImplementationVertexProperty().hasEffectiveComponent()) {
			WorkflowLogger.getLogger().log(
					Level.INFO,
					"tLevel unavailable for vertex " + inputElt
							+ ". No effective component.");
			return TimingVertexProperty.UNAVAILABLE;
		}

		// We iterate a set of preceding vertices of inputvertex
		for (MapperDAGVertex vertex : graphset) {
			TimingVertexProperty vertexTProperty = vertex
					.getTimingVertexProperty();

			// If we lack information on predecessors, path calculation fails
			// No recalculation of predecessor T Level if already calculated
			IScheduleElement dirtyElt = getDirtyScheduleElt(vertex);
			if (dirtyElt != null) {
				if (vertex.getImplementationVertexProperty()
						.hasEffectiveComponent()) {
					calculateTLevel(dirtyElt);
				}
			}

			// If we could not calculate the T level of the predecessor,
			// calculation fails
			if (!vertexTProperty.hasCost() && dirtyElt != null) {

				calculateTLevel(dirtyElt);
				WorkflowLogger.getLogger().log(
						Level.SEVERE,
						"tLevel unavailable for vertex " + inputElt
								+ ". Lacking information on predecessor "
								+ vertex + ".");
				return TimingVertexProperty.UNAVAILABLE;
			}

			long newPathLength = getVertexTLevelFromPredecessor(vertex,
					inputElt);

			// Keeping the longest preceding path
			if (timing < newPathLength) {
				timing = newPathLength;
			}
		}

		return timing;
	}

	private long getVertexTLevelFromPredecessor(MapperDAGVertex pred,
			IScheduleElement current) {
		MapperDAGEdge edge = null;

		for (DAGEdge e : current.incomingEdges()) {
			DAGVertex v = implementation.getEdgeSource(e);
			if (v != null && v.equals(pred)) {
				edge = (MapperDAGEdge) e;
			}
		}
		TimingVertexProperty predTProperty = pred.getTimingVertexProperty();
		long edgeCost = (edge == null) ? 0 : edge.getTimingEdgeProperty()
				.getCost();
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
			if (currentvertex.getSuccessorSet(false).isEmpty())
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
				.hasEffectiveComponent() && succset.isEmpty()) {

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

			WorkflowLogger
					.getLogger()
					.log(Level.SEVERE,
							"Trying to start b_level calculation from a vertex with successors or without mapping.");
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
			if (!dirtyTLevelElts.contains(vertex)) {
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

		for (ComponentInstance o : orderManager.getArchitectureComponents()) {
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
	 * considers a partially mapped graph and ignores the non mapped vertices
	 */
	public long getFinalTime(ComponentInstance component) {

		long finaltime = TimingVertexProperty.UNAVAILABLE;

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

		// Mapping groups make dirty vertices mechanism obsolete. TODO: improve
		/*
		 * dirtyTLevelElts.clear(); for(DAGVertex v :
		 * implementation.vertexSet()) dirtyTLevelElts.add((MapperDAGVertex) v);
		 */

		calculateTLevel();
		dirtyTLevelElts.clear();

		// compareResults();
	}

	public void updateTandBLevels() {

		// Mapping groups make dirty vertices mechanism obsolete. TODO: improve
		/*
		 * dirtyTLevelElts.clear(); for(DAGVertex v :
		 * implementation.vertexSet()) dirtyTLevelElts.add((MapperDAGVertex) v);
		 */

		calculateTLevel();
		dirtyTLevelElts.clear();
		calculateBLevel();

		// compareResults();
	}
}
