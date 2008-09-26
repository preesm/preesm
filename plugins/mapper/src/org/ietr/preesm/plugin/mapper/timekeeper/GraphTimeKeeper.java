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
import org.ietr.preesm.plugin.abc.accuratelytimed.AccuratelyTimedAbc;
import org.ietr.preesm.plugin.mapper.graphtransfo.DAGCreator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;
import org.ietr.preesm.plugin.mapper.tools.BLevelIterator;
import org.ietr.preesm.plugin.mapper.tools.SubsetFinder;
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
		MultiCoreArchitecture archi = Examples.get2FaradayArchi();
		archi = archi.clone();

		logger.log(Level.FINEST, "Creating DAG");
		MapperDAG dag = new DAGCreator().dagexample2(archi);

		IAbc simulator = new AccuratelyTimedAbc(
				dag, archi);

		logger.log(Level.FINEST, "Evaluating DAG");
		// simulator.implantAllVerticesOnOperator(archi.getMainOperator());
		simulator.implant(dag.getMapperDAGVertex("n1"), archi.getOperator("c64x_1"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n3"), archi.getOperator("c64x_1"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n2"), archi.getOperator("c64x_1"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n7"), archi.getOperator("c64x_1"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n6"), archi.getOperator("c64x_2"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n5"), archi.getOperator("c64x_4"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n4"), archi.getOperator("c64x_3"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n8"), archi.getOperator("c64x_4"),
				true);

		time = simulator.getFinalTime(archi.getOperator("c64x_1"));
		logger.log(Level.FINEST, "final time c64x_1: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_2"));
		logger.log(Level.FINEST, "final time c64x_2: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_3"));
		logger.log(Level.FINEST, "final time c64x_3: " + time);
		time = simulator.getFinalTime(archi.getOperator("c64x_4"));
		logger.log(Level.FINEST, "final time c64x_4: " + time + "\n");

		simulator.implant(dag.getMapperDAGVertex("n9"), archi.getOperator("c64x_4"),
				true);

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
	 * calculating top and bottom times of each vertex not considering the
	 * number of processors. The parallelism is limited by the edges
	 */
	public void calculateLevels(MapperDAG algorithm, boolean blevel) {

		MapperDAGVertex currentvertex;
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(algorithm);

		/*
		 * We iterate the dag tree in topological order to calculate t-level and
		 * b-level
		 */

		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex) iterator.next();
			calculateLevels(algorithm, currentvertex, blevel);

		}
	}

	/**
	 * calculating top and bottom times of modified vertex not considering the
	 * number of processors. The parallelism is limited by the edges. If
	 * possible, calculates the B level of all predecessors
	 */
	public void calculateLevels(MapperDAG algorithm,
			MapperDAGVertex modifiedvertex, boolean blevel) {

		DirectedGraph<DAGVertex, DAGEdge> castAlgo = algorithm;
		
		TimingVertexProperty currenttimingproperty = modifiedvertex
				.getTimingVertexProperty();
		DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex = new DirectedNeighborIndex<DAGVertex, DAGEdge>(
				castAlgo);

		Set<DAGVertex> predset;
		Set<DAGVertex> succset;

		predset = neighborindex.predecessorsOf(modifiedvertex);

		if (modifiedvertex.getImplementationVertexProperty()
				.hasEffectiveComponent()) {
			int currentvertextiming;

			currentvertextiming = modifiedvertex.getTimingVertexProperty()
					.getCost();
			currenttimingproperty = modifiedvertex.getTimingVertexProperty();

			predset = neighborindex
					.predecessorsOf((MapperDAGVertex) modifiedvertex);

			// If the vertex has no predecessor, ALAP=ASAP=0;
			// t-level = ASAP
			if (predset.isEmpty()) {
				currenttimingproperty.setTlevel(0);
			} else {
				currenttimingproperty.setTlevel(getLongestPrecedingPath(
						predset, modifiedvertex, algorithm));
			}

			if (blevel) {
				// If the vertex has no successor, we can go back in the tree to
				// set the b-level
				succset = neighborindex
						.successorsOf((MapperDAGVertex) modifiedvertex);

				if (succset.isEmpty()) {

					if (currenttimingproperty.hasTlevel()
							&& modifiedvertex.getTimingVertexProperty()
									.hasCost()) {
						currenttimingproperty.setBlevel(currentvertextiming);
						currenttimingproperty.setBlevelValidity(true);
					} else
						currenttimingproperty
								.setBlevel(TimingVertexProperty.UNAVAILABLE);

					if (!predset.isEmpty())
						setPrecedingBlevel(modifiedvertex, predset,
								neighborindex, algorithm);
				}
			}
		} else {

			currenttimingproperty.setTlevel(TimingVertexProperty.UNAVAILABLE);
			currenttimingproperty.setBlevel(TimingVertexProperty.UNAVAILABLE);

			if (blevel) {
				// If the vertex has no successor, we can go back in the tree to
				// set the b-level
				succset = neighborindex
						.successorsOf((MapperDAGVertex) modifiedvertex);

				if (succset.isEmpty()) {

					if (!predset.isEmpty())
						setPrecedingBlevel(modifiedvertex, predset,
								neighborindex, algorithm);
				}
			}
		}
	}

	/**
	 * Gives the total implementation time if possible. If current implementation
	 * information is not enough to calculate this timing, returns UNAVAILABLE
	 */
	public int getFinalTime(MapperDAG implementation) {

		int finaltime = TimingVertexProperty.UNAVAILABLE;

		Iterator<DAGVertex> iterator = implementation.vertexSet()
				.iterator();

		while (iterator.hasNext()) {
			MapperDAGVertex next = (MapperDAGVertex)iterator.next();

			if (getFinalTime(next) == TimingVertexProperty.UNAVAILABLE) {
				return TimingVertexProperty.UNAVAILABLE;
			} else
				finaltime = Math.max(finaltime, getFinalTime(next));
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
		int finaltime = TimingVertexProperty.UNAVAILABLE;

		// Finder of the vertices subset that is totally implanted on the given
		// operator
		Set<DAGVertex> currentset = implementation.vertexSet();
		SubsetFinder<DAGVertex, ArchitectureComponent> subsetfinder = new SubsetFinder<DAGVertex, ArchitectureComponent>(
				currentset, component) {

			@Override
			protected boolean subsetCondition(DAGVertex tested,
					ArchitectureComponent component) {

				boolean test = false;

				if (((MapperDAGVertex)tested).getImplementationVertexProperty()
						.hasEffectiveComponent())
					test = ((MapperDAGVertex)tested).getImplementationVertexProperty()
							.getEffectiveComponent().equals(component);

				return test;
			}

		};

		Set<DAGVertex> subset = subsetfinder.subset();

		// If no assigned vertex, operator is ready at time 0
		if (subset.isEmpty()) {
			finaltime = 0;
		} else {
			Iterator<DAGVertex> iterator = subset.iterator();

			while (iterator.hasNext()) {
				MapperDAGVertex next = (MapperDAGVertex)iterator.next();

				if (getFinalTime(next) == TimingVertexProperty.UNAVAILABLE) {
					return TimingVertexProperty.UNAVAILABLE;
				} else
					finaltime = Math.max(finaltime, getFinalTime(next));
			}
		}

		// PreesmLogger.getLogger().log(Level.INFO, "getFinalTime(" +
		// operator.getName() + "): " + finaltime);

		return finaltime;
	}

	/**
	 * Gives the final time of the given vertex in the current implementation. If
	 * current implementation information is not enough to calculate this timing,
	 * returns UNAVAILABLE
	 */
	public int getFinalTime(MapperDAGVertex vertex) {

		int vertexfinaltime = TimingVertexProperty.UNAVAILABLE;
		TimingVertexProperty timingproperty = vertex.getTimingVertexProperty();
		if (vertex.getTimingVertexProperty().hasCost()) {
			if (timingproperty.hasTlevel()) {
				vertexfinaltime = vertex.getTimingVertexProperty().getCost()
						+ timingproperty.getTlevel();
			}
		}

		return vertexfinaltime;
	}

	/**
	 * given the set of preceding vertices, returns the finishing time of the
	 * longest path reaching the vertex testedvertex
	 * 
	 * @return last finishing time
	 */
	private int getLongestPrecedingPath(Set<DAGVertex> graphset,
			MapperDAGVertex testedvertex, MapperDAG algorithm) {

		int timing = TimingVertexProperty.UNAVAILABLE;

		if (!testedvertex.getImplementationVertexProperty()
				.hasEffectiveComponent())
			return TimingVertexProperty.UNAVAILABLE;

		Iterator<DAGVertex> iterator = graphset.iterator();
		MapperDAGVertex currentvertex;
		TimingVertexProperty currenttimingproperty;
		int edgeweight;

		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex) iterator.next();
			currenttimingproperty = currentvertex.getTimingVertexProperty();
			edgeweight = ((MapperDAGEdge)algorithm.getEdge(currentvertex, testedvertex))
					.getTimingEdgeProperty().getCost();

			// If we lack information on predecessors, path calculation fails
			if (!currentvertex.getTimingVertexProperty().hasCost()) {

				if (currentvertex.getImplementationVertexProperty()
						.hasEffectiveComponent()) {
					calculateLevels(algorithm, currentvertex, false);
				}
			}

			if (!currentvertex.getTimingVertexProperty().hasCost()
					|| !currenttimingproperty.hasTlevel())
				return TimingVertexProperty.UNAVAILABLE;

			if (timing < currenttimingproperty.getTlevel()
					+ currentvertex.getTimingVertexProperty().getCost()
					+ edgeweight) {
				timing = currenttimingproperty.getTlevel()
						+ currentvertex.getTimingVertexProperty().getCost()
						+ edgeweight;

			}
		}

		return timing;
	}

	/**
	 * Resets the time keeper timings of the whole DAG
	 */
	public void resetTimings(MapperDAG implementation) {
		Iterator<DAGVertex> it = implementation.vertexSet().iterator();

		while (it.hasNext()) {
			((MapperDAGVertex)it.next()).getTimingVertexProperty().reset();
		}
	}

	/**
	 * recursive method setting the b-level of the preceding tasks given the
	 * b-level of a start task
	 */
	private void setPrecedingBlevel(
			MapperDAGVertex startvertex,
			Set<DAGVertex> predset,
			DirectedNeighborIndex<DAGVertex, DAGEdge> neighborindex,
			MapperDAG algorithm) {

		Iterator<DAGVertex> iterator = predset.iterator();
		MapperDAGVertex currentvertex;
		int currentBLevel = 0;
		TimingVertexProperty currenttimingproperty;
		TimingVertexProperty starttimingproperty = startvertex
				.getTimingVertexProperty();
		int edgeweight;

		// Sets the b-levels of each predecessor not considering the scheduling
		// edges
		while (iterator.hasNext()) {

			currentvertex = (MapperDAGVertex) iterator.next();

			currenttimingproperty = currentvertex.getTimingVertexProperty();
			edgeweight = ((MapperDAGEdge)algorithm.getEdge(currentvertex, startvertex))
					.getTimingEdgeProperty().getCost();

			// If we lack information on successor, b-level calculation fails
			if (!starttimingproperty.hasBlevel()
					|| !currentvertex.getTimingVertexProperty().hasCost()
					|| (edgeweight < 0)) {

				currentBLevel = TimingVertexProperty.UNAVAILABLE;
			} else {

				currentBLevel = starttimingproperty.getValidBlevel()
						+ currentvertex.getTimingVertexProperty().getCost()
						+ edgeweight;
			}

			currenttimingproperty.setBlevel(Math.max(currenttimingproperty
					.getBlevel(), currentBLevel));

			Iterator<DAGVertex> succIt = neighborindex.successorsOf(
					currentvertex).iterator();
			boolean allSuccessorsBLevel = true;

			while (succIt.hasNext()) {
				MapperDAGVertex succ = (MapperDAGVertex)succIt.next();
				allSuccessorsBLevel = allSuccessorsBLevel
						&& succ.getTimingVertexProperty().hasBlevel();
				allSuccessorsBLevel = allSuccessorsBLevel
						&& ((MapperDAGEdge)algorithm.getEdge(currentvertex, succ))
								.getTimingEdgeProperty().hasCost();
			}

			currenttimingproperty.setBlevelValidity(allSuccessorsBLevel);

			Set<DAGVertex> newPredSet = neighborindex
					.predecessorsOf(currentvertex);

			if (!newPredSet.isEmpty())
				setPrecedingBlevel(currentvertex, newPredSet, neighborindex,
						algorithm);
		}
	}

	@Override
	public void updateTLevels(MapperDAG implementation) {
		calculateLevels(implementation, false);

	}

	@Override
	public void updateTandBLevels(MapperDAG implementation) {
		calculateLevels(implementation, true);

	}

	/**
	 * Updates the timing info of the implementation relative to the given vertex
	 * and its successors
	 */
	@Override
	public void updateTandBLevels(MapperDAG implementation, MapperDAGVertex vertex) {
		calculateLevels(implementation, vertex, true);
	}

}
