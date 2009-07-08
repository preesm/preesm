/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.mapper.timekeeper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * The time keeper tags the vertices with mapping timing information
 * 
 * @author mpelcat
 */
public class GraphTimeKeeper {

	/**
	 * Flag true if the timings are dirty and need to be reprocessed before
	 * sending them.
	 */
	protected boolean dirtyTimings;

	/**
	 * Current implementation: the same as in the ABC
	 */
	protected MapperDAG implementation;

	/**
	 * Helper to scan the neighbors of a vertex
	 */
	private DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex;
	
	private Map<ArchitectureComponent,Long> componentFinalTimes;

	/**
	 * Constructor
	 */
	public GraphTimeKeeper(MapperDAG implementation) {

		this.dirtyTimings = false;
		this.implementation = implementation;

		neighborindex = null;
		componentFinalTimes = new HashMap<ArchitectureComponent,Long>();
	}

	/**
	 * Specifying that vertex has no more the right timings in its cost
	 * attribute
	 */
	public void setAsDirty(MapperDAGVertex vertex) {
		dirtyTimings = true;
	}

	/**
	 * Specifying that all timings are clean
	 */
	public void setAsClean() {

		dirtyTimings = false;
	}

	/**
	 * calculating top times of each vertex in dirty vertices set. The
	 * parallelism is limited by the edges
	 */
	public void calculateTLevel() {

		MapperDAGVertex currentvertex;
		DirectedGraph<DAGVertex, DAGEdge> castAlgo = implementation;
		neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(castAlgo);

		// We iterate the dirty vertices to reset their t-levels
		for (DAGVertex dagV : implementation.vertexSet()) {
			currentvertex = (MapperDAGVertex) dagV;
			currentvertex.getTimingVertexProperty().resetTlevel();
		}

		// We iterate the dirty vertices to set their t-levels
		for (DAGVertex dagV : implementation.vertexSet()) {
			currentvertex = (MapperDAGVertex) dagV;
			calculateTLevel(currentvertex);
		}

		// We handle synchronized vertices
		for (DAGVertex dagV : implementation.vertexSet()) {
			currentvertex = (MapperDAGVertex) dagV;
			handleSynchros(currentvertex);
		}

	}

	/**
	 * Handling synchronized vertices.
	 */
	private void handleSynchros(MapperDAGVertex modifiedvertex) {
		// Handling synchronized vertices: Setting all the t-levels of synchronized vertices at the same value.
		if (modifiedvertex.getTimingVertexProperty().getSynchronizedVertices() != null) {
			List<MapperDAGVertex> synchronizedVertices = modifiedvertex
					.getTimingVertexProperty().getSynchronizedVertices();
			if (synchronizedVertices.size() > 1) {
				long maxTLevel = -1;
				for (MapperDAGVertex v : synchronizedVertices) {
					long tLevel = v.getTimingVertexProperty().getTlevel();
					if (tLevel > maxTLevel)
						maxTLevel = tLevel;
				}

				if(maxTLevel >= 0)
					updateTLevel((MapperDAGVertex) modifiedvertex, maxTLevel);
			}
		}
	}

	public void updateTLevel(MapperDAGVertex modifiedvertex, long newTLevel) {
		if (newTLevel > modifiedvertex.getTimingVertexProperty().getTlevel()
				/*&& modifiedvertex.getImplementationVertexProperty()
						.hasEffectiveComponent()*/) {
			modifiedvertex.getTimingVertexProperty().setTlevel(newTLevel);
			Set<DAGVertex> sucSet = neighborindex.successorsOf(modifiedvertex);
			for (DAGVertex v : sucSet) {
				updateTLevel((MapperDAGVertex) v,
						getVertexTLevelFromPredecessorNoEdge(modifiedvertex,
								(MapperDAGVertex) v));
			}
		}
	}

	private long getVertexTLevelFromPredecessorNoEdge(MapperDAGVertex pred,
			MapperDAGVertex current) {

		MapperDAGEdge edge = (MapperDAGEdge) implementation.getEdge(pred,
				current);
		TimingVertexProperty predTProperty = pred.getTimingVertexProperty();
		long edgeCost = edge.getTimingEdgeProperty().getCost();

		long newPathLength = -1;
		if (predTProperty.getTlevel() >= 0 && predTProperty.getCost() >= 0 && edgeCost >= 0) {
			newPathLength = predTProperty.getTlevel()
					+ predTProperty.getCost();
		}

		return newPathLength;
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
	 * calculating top time of modified vertex and all its successors.
	 */
	public void calculateTLevel(MapperDAGVertex modifiedvertex) {

		TimingVertexProperty currenttimingproperty = modifiedvertex
				.getTimingVertexProperty();

		Set<DAGVertex> predset;

		// If the current vertex has an effective component
		if (modifiedvertex.getImplementationVertexProperty()
				.hasEffectiveComponent()) {

			currenttimingproperty = modifiedvertex.getTimingVertexProperty();

			predset = neighborindex.predecessorsOf(modifiedvertex);

			// If the vertex has no predecessor, ALAP=ASAP=0;
			// t-level = ASAP
			if (predset.isEmpty()) {
				currenttimingproperty.setTlevel(0);
			} else {
				// The T level is the time of the longest preceding path
				long l = getLongestPrecedingPath(predset, modifiedvertex);
				currenttimingproperty.setTlevel(l);
			}
			
			// Updating the operator final time
			ArchitectureComponent c = modifiedvertex.getImplementationVertexProperty().getEffectiveComponent();
			ArchitectureComponent finalTimeRefCmp = c;
			long currentCmpFinalTime = TimingVertexProperty.UNAVAILABLE;
			for(ArchitectureComponent o : componentFinalTimes.keySet()){
				if(o.equals(c)){
					currentCmpFinalTime = componentFinalTimes.get(o);
					finalTimeRefCmp = o;
				}
			}

			long newFinalTime = getFinalTime(modifiedvertex);
			
			if(newFinalTime > currentCmpFinalTime){
				componentFinalTimes.put(finalTimeRefCmp, newFinalTime);
			}

		} else {
			// If the current vertex has no effective component
			PreesmLogger.getLogger().log(
					Level.FINEST,
					"tLevel unavailable for vertex " + modifiedvertex
							+ ". No effective component.");
			currenttimingproperty.setTlevel(TimingVertexProperty.UNAVAILABLE);
		}
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
			if (!vertexTProperty.hasTlevel()) {
				if (vertex.getImplementationVertexProperty()
						.hasEffectiveComponent()) {
					calculateTLevel(vertex);
				}
			}

			// If we could not calculate the T level of the predecessor,
			// calculation fails
			if (!vertexTProperty.hasCost() || !vertexTProperty.hasTlevel()) {
				PreesmLogger.getLogger().log(
						Level.INFO,
						"tLevel unavailable for vertex " + inputvertex
								+ ". Lacking information on predecessor "
								+ vertex + ".");
				return TimingVertexProperty.UNAVAILABLE;
			}

			long newPathLength = getVertexTLevelFromPredecessor(vertex,
					inputvertex);

			if (timing < newPathLength) {
				timing = newPathLength;
			}
		}

		return timing;
	}

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
			PreesmLogger.getLogger().log(Level.FINEST,
					"calculating b-level of " + currentvertex);

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

		// If the current vertex has an effective component
		if (modifiedvertex.getImplementationVertexProperty()
				.hasEffectiveComponent()
				&& succset.isEmpty()) {

			if (currenttimingproperty.hasTlevel()
					&& currenttimingproperty.hasCost()) {
				currenttimingproperty
						.setBlevel(currenttimingproperty.getCost());

				if (!predset.isEmpty()) {
					// Sets recursively the BLevel of its predecessors
					setPrecedingBlevel(modifiedvertex, predset);
				}
			} else {
				currenttimingproperty
						.setBlevel(TimingVertexProperty.UNAVAILABLE);
			}

		} else {

			PreesmLogger
					.getLogger()
					.log(
							Level.SEVERE,
							"Trying to start b_level calculation from a vertex with successors or without implantation.");
			currenttimingproperty.setBlevel(TimingVertexProperty.UNAVAILABLE);
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
		boolean hasStartVertexBLevel = starttimingproperty.hasBlevel();

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
				currentBLevel = starttimingproperty.getBlevel()
						+ currenttimingproperty.getCost() + edgeweight;

				currenttimingproperty.setBlevel(Math.max(currenttimingproperty
						.getBlevel(), currentBLevel));

				Set<DAGVertex> newPredSet = neighborindex
						.predecessorsOf(currentvertex);

				if (!newPredSet.isEmpty())
					// Recursively sets the preceding b levels
					setPrecedingBlevel(currentvertex, newPredSet);
			} else {
				currenttimingproperty
						.setBlevel(TimingVertexProperty.UNAVAILABLE);
			}

		}
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
			if (timingproperty.hasTlevel()) {
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
		
		for(ArchitectureComponent o : componentFinalTimes.keySet()){
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
		for(ArchitectureComponent o : componentFinalTimes.keySet()){
			if(o.equals(component)){
				finalTimeRefCmp = o;
			}
		}
		
		if(finalTimeRefCmp != null){
			return componentFinalTimes.get(finalTimeRefCmp);
		}
		
		return TimingVertexProperty.UNAVAILABLE;
	}

	public void updateTLevels() {

		componentFinalTimes.clear();
		calculateTLevel();
		setAsClean();

	}

	public void updateTandBLevels() {

		componentFinalTimes.clear();
		calculateTLevel();
		calculateBLevel();
		setAsClean();
	}

	/**
	 * Resets the time keeper timings of the whole DAG
	 */
	public void resetTimings() {
		Iterator<DAGVertex> it = implementation.vertexSet().iterator();

		while (it.hasNext()) {
			((MapperDAGVertex) it.next()).getTimingVertexProperty().reset();
		}

		dirtyTimings = true;
	}

}
