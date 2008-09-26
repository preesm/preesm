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


/**
 * 
 */
package org.ietr.preesm.plugin.mapper.timekeeper;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.looselytimed.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;
import org.ietr.preesm.plugin.mapper.tools.BLevelIterator;
import org.ietr.preesm.plugin.mapper.tools.TLevelIterator;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * The time keeper tags the vertices with mapping timing information
 * 
 * @author mpelcat
 */
public class GraphTimeKeeper implements ITimeKeeper {

	/**
	 * Main for tests
	 */
	public static void main(String[] args) {

		int time;
		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.ALL);

		logger.log(Level.FINEST, "Creating archi");
		MultiCoreArchitecture archi = Examples.get4C64Archi();
		archi = archi.clone();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simulator = new LooselyTimedAbc(dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), archi
				.getOperator("c64x_1"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n3"), archi
				.getOperator("c64x_1"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n2"), archi
				.getOperator("c64x_1"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n7"), archi
				.getOperator("c64x_1"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n6"), archi
				.getOperator("c64x_2"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n5"), archi
				.getOperator("c64x_4"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n4"), archi
				.getOperator("c64x_3"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n8"), archi
				.getOperator("c64x_4"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n9"), archi
				.getOperator("c64x_4"), true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		logger.log(Level.FINEST, "Iterating in t order");

		TLevelIterator titerator = new TLevelIterator(dag, simulator, false);

		while (titerator.hasNext()) {
			MapperDAGVertex currentvertex = (MapperDAGVertex) titerator.next();

			logger.log(Level.FINEST, "vertex " + currentvertex.getName()
					+ ", t-level: " + simulator.getTLevel(currentvertex));
		}

		logger.log(Level.FINEST, "Iterating in b order");

		BLevelIterator biterator = new BLevelIterator(dag, simulator, false);

		while (biterator.hasNext()) {
			MapperDAGVertex currentvertex = (MapperDAGVertex) biterator.next();

			logger.log(Level.FINEST, "vertex " + currentvertex.getName()
					+ ", b-level: " + simulator.getBLevel(currentvertex));
		}

		logger.log(Level.FINEST, "Getting finishing times");

		int test;

		// simulator.setDAG(dag);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n1"));
		logger.log(Level.FINEST, "n1: " + test);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n5"));
		logger.log(Level.FINEST, "n5: " + test);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n8"));
		logger.log(Level.FINEST, "n8: " + test);

		test = simulator.getFinalTime(dag.getMapperDAGVertex("n9"));
		logger.log(Level.FINEST, "n9: " + test);

		test = simulator.getFinalTime();
		logger.log(Level.FINEST, "final: " + test);

		logger.log(Level.FINEST, "Test finished");

		simulator.plotImplementation();
	}

	/**
	 * Constructor
	 */
	public GraphTimeKeeper() {
	}

	/**
	 * calculating top times of each vertex not considering the number of
	 * processors. The parallelism is limited by the edges
	 */
	public void calculateTLevel(MapperDAG algorithm) {

		MapperDAGVertex currentvertex;
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(algorithm);

		// We iterate the dag tree in topological order to calculate t-level

		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex) iterator.next();
			calculateTLevel(algorithm, currentvertex);

		}
	}

	/**
	 * calculating bottom times of each vertex not considering the number of
	 * processors. The parallelism is limited by the edges
	 */
	public void calculateBLevel(MapperDAG algorithm) {

		MapperDAGVertex currentvertex;
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(algorithm);

		// We iterate the dag tree in topological order to calculate b-level

		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex) iterator.next();
			calculateBLevel(algorithm, currentvertex);

		}
	}

	/**
	 * calculating top time of modified vertex.
	 */
	public void calculateTLevel(MapperDAG algorithm,
			MapperDAGVertex modifiedvertex) {

		DirectedGraph<DAGVertex, DAGEdge> castAlgo = algorithm;

		TimingVertexProperty currenttimingproperty = modifiedvertex
				.getTimingVertexProperty();

		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(
				castAlgo);

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
				currenttimingproperty.setTlevel(getLongestPrecedingPath(
						predset, modifiedvertex, algorithm));
			}

		} else {
			// If the current vertex has no effective component
			currenttimingproperty.setTlevel(TimingVertexProperty.UNAVAILABLE);
		}
	}

	/**
	 * calculating bottom time of modified vertex.
	 */
	public void calculateBLevel(MapperDAG algorithm,
			MapperDAGVertex modifiedvertex) {

		DirectedGraph<DAGVertex, DAGEdge> castAlgo = algorithm;

		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(
				castAlgo);

		TimingVertexProperty currenttimingproperty = modifiedvertex
				.getTimingVertexProperty();

		Set<DAGVertex> predset = neighborindex
				.predecessorsOf((MapperDAGVertex) modifiedvertex);
		Set<DAGVertex> succset = neighborindex
				.successorsOf((MapperDAGVertex) modifiedvertex);

		// If the current vertex has an effective component
		if (modifiedvertex.getImplementationVertexProperty()
				.hasEffectiveComponent()) {
			// If the current vertex has no successor
			if (succset.isEmpty()) {

				if (currenttimingproperty.hasTlevel()
						&& currenttimingproperty.hasCost()) {
					currenttimingproperty.setBlevel(currenttimingproperty
							.getCost());
					currenttimingproperty.setBlevelValidity(true);
				} else
					currenttimingproperty
							.setBlevel(TimingVertexProperty.UNAVAILABLE);

				if (!predset.isEmpty())
					// Sets recursively the BLevel of its predecessors
					setPrecedingBlevel(modifiedvertex, predset, neighborindex,
							algorithm);
			}
		} else {

			currenttimingproperty.setBlevel(TimingVertexProperty.UNAVAILABLE);
			// If the vertex has no successor, we can go back in the tree to
			// set the b-level
			succset = neighborindex
					.successorsOf((MapperDAGVertex) modifiedvertex);

			if (succset.isEmpty()) {

				if (!predset.isEmpty())
					setPrecedingBlevel(modifiedvertex, predset, neighborindex,
							algorithm);
			}
		}
	}

	/**
	 * given the set of preceding vertices, returns the finishing time of the
	 * longest path reaching the vertex testedvertex
	 * 
	 * @return last finishing time
	 */
	private int getLongestPrecedingPath(Set<DAGVertex> graphset,
			MapperDAGVertex inputvertex, MapperDAG algorithm) {

		int timing = TimingVertexProperty.UNAVAILABLE;

		if (!inputvertex.getImplementationVertexProperty()
				.hasEffectiveComponent())
			return TimingVertexProperty.UNAVAILABLE;

		Iterator<DAGVertex> iterator = graphset.iterator();

		// We iterate a set of preceding vertices of inputvertex
		while (iterator.hasNext()) {
			MapperDAGVertex vertex = (MapperDAGVertex) iterator.next();
			TimingVertexProperty vertexTProperty = vertex
					.getTimingVertexProperty();
			MapperDAGEdge edge = (MapperDAGEdge) algorithm.getEdge(vertex,
					inputvertex);
			int edgeCost = edge.getTimingEdgeProperty().getCost();

			// If we lack information on predecessors, path calculation fails
			// No recalculation of predecessor T Level if already calculated
			if (!vertexTProperty.hasTlevel()) {
				if (vertex.getImplementationVertexProperty()
						.hasEffectiveComponent()) {
					calculateTLevel(algorithm, vertex);
				}
			}

			// If we could not calculate the T level of the predecessor,
			// calculation fails
			if (!vertexTProperty.hasCost() || !vertexTProperty.hasTlevel())
				return TimingVertexProperty.UNAVAILABLE;

			int newPathLength = vertexTProperty.getTlevel()
					+ vertexTProperty.getCost() + edgeCost;

			if (timing < newPathLength) {
				timing = newPathLength;
			}
		}

		return timing;
	}

	/**
	 * recursive method setting the b-level of the preceding tasks given the
	 * b-level of a start task
	 */
	private void setPrecedingBlevel(MapperDAGVertex startvertex,
			Set<DAGVertex> predset,
			DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex,
			MapperDAG algorithm) {

		int currentBLevel = 0;
		TimingVertexProperty starttimingproperty = startvertex
				.getTimingVertexProperty();

		Iterator<DAGVertex> iterator = predset.iterator();
		
		// Sets the b-levels of each predecessor not considering the precedence
		// edges
		while (iterator.hasNext()) {

			MapperDAGVertex currentvertex = (MapperDAGVertex) iterator.next();

			TimingVertexProperty currenttimingproperty = currentvertex.getTimingVertexProperty();
			int edgeweight = ((MapperDAGEdge) algorithm.getEdge(currentvertex,
					startvertex)).getTimingEdgeProperty().getCost();

			// If we lack information on successor, b-level calculation fails
			if (!starttimingproperty.hasBlevel()
					|| !currentvertex.getTimingVertexProperty().hasCost()
					|| (edgeweight < 0)) {

				currentBLevel = TimingVertexProperty.UNAVAILABLE;
			} else {

				currentBLevel = starttimingproperty.getValidBlevel()
						+ currenttimingproperty.getCost()
						+ edgeweight;
			}

			currenttimingproperty.setBlevel(Math.max(currenttimingproperty
					.getBlevel(), currentBLevel));

			Iterator<DAGVertex> succIt = neighborindex.successorsOf(
					currentvertex).iterator();
			boolean allSuccessorsBLevel = true;

			while (succIt.hasNext()) {
				MapperDAGVertex succ = (MapperDAGVertex) succIt.next();
				allSuccessorsBLevel = allSuccessorsBLevel
						&& succ.getTimingVertexProperty().hasBlevel();
				
				allSuccessorsBLevel = allSuccessorsBLevel
						&& ((MapperDAGEdge) algorithm.getEdge(currentvertex,
								succ)).getTimingEdgeProperty().hasCost();
				
			}

			currenttimingproperty.setBlevelValidity(allSuccessorsBLevel);

			Set<DAGVertex> newPredSet = neighborindex
					.predecessorsOf(currentvertex);

			if (!newPredSet.isEmpty())
				// Recursively sets the preceding b levels
				setPrecedingBlevel(currentvertex, newPredSet, neighborindex,
						algorithm);
		}
	}

	/**
	 * Gives the final time of the given vertex in the current implementation.
	 * If current implementation information is not enough to calculate this
	 * timing, returns UNAVAILABLE
	 */
	public int getFinalTime(MapperDAGVertex vertex) {

		int vertexfinaltime = TimingVertexProperty.UNAVAILABLE;
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
	public int getFinalTime(MapperDAG implementation) {

		int finaltime = TimingVertexProperty.UNAVAILABLE;

		Iterator<DAGVertex> iterator = implementation.vertexSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGVertex next = (MapperDAGVertex) iterator.next();
			int nextFinalTime = getFinalTime(next);

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
	public int getFinalTime(MapperDAG implementation,
			ArchitectureComponent component) {
		int finaltime = 0;

		Iterator<DAGVertex> iterator = implementation.vertexSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGVertex next = (MapperDAGVertex) iterator.next();

			if (component.equals(next.getImplementationVertexProperty()
					.getEffectiveComponent())) {
				int nextFinalTime = getFinalTime(next);

				// Returns TimingVertexProperty.UNAVAILABLE if at least one
				// vertex has no final time. Otherwise returns the highest final
				// time
				if (nextFinalTime == TimingVertexProperty.UNAVAILABLE) {
					return TimingVertexProperty.UNAVAILABLE;
				} else
					finaltime = Math.max(finaltime, nextFinalTime);
			}
		}

		return finaltime;
	}

	@Override
	public void updateTLevels(MapperDAG implementation) {
		calculateTLevel(implementation);

	}

	@Override
	public void updateTandBLevels(MapperDAG implementation) {
		calculateTLevel(implementation);
		calculateBLevel(implementation);

	}

	@Override
	public void updateTandBLevels(MapperDAG implementation, MapperDAGVertex vertex) {
		calculateTLevel(implementation);
		calculateBLevel(implementation);

	}

	/**
	 * Resets the time keeper timings of the whole DAG
	 */
	public void resetTimings(MapperDAG implementation) {
		Iterator<DAGVertex> it = implementation.vertexSet().iterator();

		while (it.hasNext()) {
			((MapperDAGVertex) it.next()).getTimingVertexProperty().reset();
		}
	}

}
