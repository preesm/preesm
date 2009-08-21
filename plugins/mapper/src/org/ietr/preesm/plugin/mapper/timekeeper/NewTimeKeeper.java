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
	 * In order to minimize recalculation, component final times are stored
	 */
	private Map<ArchitectureComponent, Long> componentFinalTimes;

	/**
	 * In order to minimize recalculation, a set of modified vertices is kept
	 */
	private Set<DAGVertex> dirtyVertices;

	/**
	 * Constructor
	 */
	public NewTimeKeeper(MapperDAG implementation) {

		this.implementation = implementation;
		neighborindex = null;
		componentFinalTimes = new HashMap<ArchitectureComponent, Long>();
		dirtyVertices = new HashSet<DAGVertex>();
	}

	/**
	 * Observer update notifying that a vertex status has changed and its
	 * timings need recalculation
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null && arg1 instanceof MapperDAGVertex) {
			MapperDAGVertex v = (MapperDAGVertex) arg1;
			dirtyVertices.add(v);
		}
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

	private void calculateTLevel() {

		DirectedGraph<DAGVertex, DAGEdge> castAlgo = implementation;
		neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(castAlgo);

		// allDirtyVertices contains the vertices dirty because of
		// implementation modification or
		// neighbors modification

		Iterator<DAGVertex> vIt = dirtyVertices.iterator();
		while (vIt.hasNext()) {
			if(!implementation.vertexSet().contains(vIt.next())){
				vIt.remove();
			}
		}
		
		Set<DAGVertex> allDirtyVertices = new HashSet<DAGVertex>(dirtyVertices);
		
		// We iterate the original dirty vertices to calculate the inferred ones
		/*addSuccessorsDirtyVertices(dirtyVertices, allDirtyVertices);

		for (DAGVertex v : dirtyVertices) {
			addPrecedingTransfersDirtyVertices(neighborindex.predecessorsOf(v),
					allDirtyVertices);
		}*/

		//dirtyVertices.addAll(allDirtyVertices);

		for (DAGVertex v : allDirtyVertices) {
			if (dirtyVertices.contains(v))
				calculateTLevel((MapperDAGVertex) v);
		}
	}

	/**
	 * All the successors of a dirty vertex are dirty
	 */
	/*public void addSuccessorsDirtyVertices(Set<DAGVertex> vertices,
			Set<DAGVertex> allDirtyVertices) {
		// We iterate the dirty vertices successors to set them as dirty
		if (vertices != null) {
			for (DAGVertex dagV : vertices) {
				allDirtyVertices.add(dagV);

				Set<DAGVertex> succSet = neighborindex.successorsOf(dagV);
				addSuccessorsDirtyVertices(succSet, allDirtyVertices);
			}
		}
	}*/

	/**
	 * All the preceding transfers of a dirty vertex are dirty
	 */
	/*public void addPrecedingTransfersDirtyVertices(Set<DAGVertex> vertices,
			Set<DAGVertex> allDirtyVertices) {
		// We iterate the preceding transfers to set them as dirty
		if (vertices != null) {
			for (DAGVertex dagV : vertices) {
				if (dagV instanceof TransferVertex
						|| dagV instanceof OverheadVertex) {
					allDirtyVertices.add(dagV);
					
					if(dagV instanceof TransferVertex && ((TransferVertex)dagV).getInvolvementVertex() != null){
						allDirtyVertices.add(((TransferVertex)dagV).getInvolvementVertex());
					}

					Set<DAGVertex> predSet = neighborindex.predecessorsOf(dagV);
					addPrecedingTransfersDirtyVertices(predSet,
							allDirtyVertices);
				}
			}
		}
	}*/

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

			// Updating the operator final time
			ArchitectureComponent c = modifiedvertex
					.getImplementationVertexProperty().getEffectiveComponent();
			ArchitectureComponent finalTimeRefCmp = c;
			long currentCmpFinalTime = TimingVertexProperty.UNAVAILABLE;

			// Looking for the architecture component corresponding to c
			for (ArchitectureComponent o : componentFinalTimes.keySet()) {
				if (o.equals(c)) {
					currentCmpFinalTime = componentFinalTimes.get(o);
					finalTimeRefCmp = o;
				}
			}

			long newFinalTime = getFinalTime(modifiedvertex);

			if (newFinalTime > currentCmpFinalTime) {
				componentFinalTimes.put(finalTimeRefCmp, newFinalTime);
			}
		} else {
			// If the current vertex has no effective component
			PreesmLogger.getLogger().log(
					Level.FINEST,
					"tLevel unavailable for vertex " + modifiedvertex
							+ ". No effective component.");
			currenttimingproperty
					.setNewtLevel(TimingVertexProperty.UNAVAILABLE);
		}

		dirtyVertices.remove(modifiedvertex);
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
			if (dirtyVertices.contains(vertex)) {
				if (vertex.getImplementationVertexProperty()
						.hasEffectiveComponent()) {
					calculateTLevel(vertex);
				}
			}

			// If we could not calculate the T level of the predecessor,
			// calculation fails
			if (!vertexTProperty.hasCost() || dirtyVertices.contains(vertex)) {
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
		long newPathLength = predTProperty.getTlevel()
				+ predTProperty.getCost() + edgeCost;

		return newPathLength;
	}

	/**
	 * Gives the final time of the given vertex in the current implementation.
	 * If current implementation information is not enough to calculate this
	 * timing, returns UNAVAILABLE
	 */
	public long getFinalTime(MapperDAGVertex vertex) {

		long vertexfinaltime = TimingVertexProperty.UNAVAILABLE;
		TimingVertexProperty timingproperty = vertex.getTimingVertexProperty();
		if (vertex.getTimingVertexProperty().hasCost()) {
			if (!dirtyVertices.contains(vertex)) {
				// Returns, if possible, TLevel + vertex timing
				vertexfinaltime = vertex.getTimingVertexProperty().getCost()
						+ timingproperty.getTlevel();
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

		for (ArchitectureComponent o : componentFinalTimes.keySet()) {
			long nextFinalTime = componentFinalTimes.get(o);
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

		ArchitectureComponent finalTimeRefCmp = null;
		for (ArchitectureComponent o : componentFinalTimes.keySet()) {
			if (o.equals(component)) {
				finalTimeRefCmp = o;
			}
		}

		if (finalTimeRefCmp != null) {
			return componentFinalTimes.get(finalTimeRefCmp);
		}

		return TimingVertexProperty.UNAVAILABLE;
	}

	private void calculateBLevel() {

	}

	public void updateTLevels() {
/*
		componentFinalTimes.clear();
		calculateTLevel();

		compareResults();
		dirtyVertices.clear();*/
	}

	public void updateTandBLevels() {
		/*
		 * componentFinalTimes.clear(); calculateTLevel(); calculateBLevel();
		 * 
		 * // compareResults(); dirtyVertices.clear();
		 */
	}

	private void compareResults() {

		Iterator<DAGVertex> it = implementation.vertexSet().iterator();

		while (it.hasNext()) {
			MapperDAGVertex v = ((MapperDAGVertex) it.next());

			TimingVertexProperty tProp = v.getTimingVertexProperty();

			/*
			 * if (tProp.getBlevel() != tProp.getNewbLevel()) {
			 * PreesmLogger.getLogger().log(Level.SEVERE, "false bL " +
			 * v.getName()); }
			 */

			if (tProp.getTlevel() != tProp.getNewtLevel()) {
				/*
				 * PreesmLogger.getLogger().log(Level.SEVERE, "false tL " +
				 * v.getName());
				 */
				int i = 0;
				i++;
			} else if (tProp.getTlevel() != -1) {
				int i = 1;
				i++;
			}
		}
	}
}
