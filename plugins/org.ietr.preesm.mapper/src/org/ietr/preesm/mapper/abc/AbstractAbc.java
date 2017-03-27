/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/

package org.ietr.preesm.mapper.abc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFEndVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.impl.latency.AccuratelyTimedAbc;
import org.ietr.preesm.mapper.abc.impl.latency.ApproximatelyTimedAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.impl.latency.LooselyTimedAbc;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.abc.taskscheduling.TaskSwitcher;
import org.ietr.preesm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.VertexMapping;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.tools.CustomTopologicalIterator;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;

/**
 * An architecture simulator calculates costs for a given partial or total
 * implementation
 * 
 * @author mpelcat
 */
public abstract class AbstractAbc implements IAbc {

	/**
	 * Architecture related to the current simulator
	 */
	protected Design archi;

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	protected OrderManager orderManager = null;

	/**
	 * Current directed acyclic graph. It is the external dag graph
	 */
	protected MapperDAG dag;

	/**
	 * Current implementation: the internal model that will be used to add
	 * edges/vertices and calculate times
	 */
	protected MapperDAG implementation;

	/**
	 * Current Abc type
	 */
	protected AbcType abcType = null;

	/**
	 * Task scheduler
	 */
	protected AbstractTaskSched taskScheduler = null;

	/**
	 * Scenario with information common to algorithm and architecture
	 */
	protected PreesmScenario scenario;

	/**
	 * Gets internal implementation graph. Use only for debug!
	 */
	@Override
	public MapperDAG getImplementation() {
		return implementation;
	}
	/**
	 * Activating traces. Put to true only for debug!
	 */
	private static final boolean debugTraces = false;
	/**
	 * Gets a new architecture simulator from a simulator type
	 */
	public static IAbc getInstance(AbcParameters params, MapperDAG dag,
			Design archi, PreesmScenario scenario) throws WorkflowException {

		AbstractAbc abc = null;
		AbcType simulatorType = params.getSimulatorType();

		if (simulatorType == AbcType.InfiniteHomogeneous) {
			abc = new InfiniteHomogeneousAbc(params, dag, archi, scenario);
		} else if (simulatorType == AbcType.LooselyTimed) {
			abc = new LooselyTimedAbc(params, dag, archi, simulatorType,
					scenario);
		} else if (simulatorType == AbcType.ApproximatelyTimed) {
			abc = new ApproximatelyTimedAbc(params, dag, archi, simulatorType,
					scenario);
		} else if (simulatorType == AbcType.AccuratelyTimed) {
			abc = new AccuratelyTimedAbc(params, dag, archi, simulatorType,
					scenario);
		}

		return abc;
	}

	/**
	 * ABC constructor
	 */
	protected AbstractAbc(MapperDAG dag, Design archi, AbcType abcType,
			PreesmScenario scenario) {

		this.abcType = abcType;
		orderManager = new OrderManager(archi);

		this.dag = dag;

		// implementation is a duplicate from dag
		this.implementation = dag.clone();

		// Initializes relative constraints
		initRelativeConstraints();

		this.archi = archi;
		this.scenario = scenario;

		// Schedules the tasks in topological and alphabetical order. Some
		// better order should be looked for
		setTaskScheduler(new TaskSwitcher());
	}

	/**
	 * Setting common constraints to all non-special vertices and their related
	 * init and end vertices
	 */
	private void initRelativeConstraints() {
		for (DAGVertex v : implementation.vertexSet()) {
			populateRelativeConstraint((MapperDAGVertex) v);
		}
	}

	/**
	 * Setting common constraints to a non-special vertex and its related init
	 * and end vertices
	 */
	private void populateRelativeConstraint(MapperDAGVertex vertex) {

		Set<MapperDAGVertex> verticesToAssociate = new HashSet<MapperDAGVertex>();
		verticesToAssociate.add(vertex);

		if (SpecialVertexManager.isInit(vertex)) {
			SDFEndVertex sdfEndVertex = (SDFEndVertex) ((SDFInitVertex) vertex
					.getCorrespondingSDFVertex()).getEndReference();
			MapperDAGVertex end = (MapperDAGVertex) implementation
					.getVertex(sdfEndVertex.getName());
			verticesToAssociate.add(end);
		}

	}

	@Override
	public final MapperDAG getDAG() {
		return dag;
	}

	/**
	 * Called whenever the implementation of a vertex occurs
	 */
	protected abstract void fireNewMappedVertex(MapperDAGVertex vertex,
			boolean updateRank);

	/**
	 * Called whenever the unimplementation of a vertex occurs
	 */
	protected abstract void fireNewUnmappedVertex(MapperDAGVertex vertex);

	/**
	 * Gets the architecture
	 */
	@Override
	public final Design getArchitecture() {
		return this.archi;
	}

	@Override
	public final PreesmScenario getScenario() {
		return scenario;
	}

	/**
	 * Gets the effective operator of the vertex. NO_OPERATOR if not set
	 */
	@Override
	public final ComponentInstance getEffectiveComponent(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);
		return vertex.getEffectiveComponent();
	}

	/**
	 * *********Costs accesses**********
	 */

	@Override
	public abstract long getFinalCost();

	@Override
	public abstract long getFinalCost(MapperDAGVertex vertex);

	@Override
	public abstract long getFinalCost(ComponentInstance component);

	/**
	 * *********Implementation accesses**********
	 */

	/**
	 * Gets the rank of the given vertex on its operator. -1 if the vertex has
	 * no rank
	 */
	@Override
	public final int getSchedulingOrder(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		return orderManager.localIndexOf(vertex);
	}

	/**
	 * Gets the total rank of the given vertex. -1 if the vertex has no rank
	 */
	@Override
	public final int getSchedTotalOrder(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		return orderManager.totalIndexOf(vertex);
	}

	/**
	 * Gets the current total schedule of the ABC
	 */
	@Override
	public final VertexOrderList getTotalOrder() {
		return orderManager.getTotalOrder().toOrderList();
	}

	/**
	 * Maps a single vertex vertex on the operator. If updaterank is true, finds
	 * a new place for the vertex in the schedule. Otherwise, use the vertex
	 * rank to know where to schedule it.
	 */
	private final void mapSingleVertex(MapperDAGVertex dagvertex,
			ComponentInstance operator, boolean updateRank)
			throws WorkflowException {
		MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

		if (impvertex.getEffectiveOperator() != DesignTools.NO_COMPONENT_INSTANCE) {
			// Unmapping if necessary before mapping
			unmap(dagvertex);
		}

		// Testing if the vertex or its group can be mapped on the
		// target operator
		if (isMapable(impvertex, operator, false) || !updateRank
				|| impvertex instanceof TransferVertex) {

			// Implementation property is set in both DAG and
			// implementation
			// Modifying effective operator of the vertex and all its
			// mapping set!
			dagvertex.setEffectiveOperator(operator);
			impvertex.setEffectiveOperator(operator);

			fireNewMappedVertex(impvertex, updateRank);

		} else {
			WorkflowLogger.getLogger().log(
					Level.SEVERE,
					impvertex.toString() + " can not be mapped (single) on "
							+ operator.toString());
		}
	}

	/**
	 * Maps a vertex and its non-trivial group. If the boolean remapGroup is
	 * true, the whole group is forced to be unmapped and remapped.
	 */
	private final void mapVertexWithGroup(MapperDAGVertex dagvertex,
			ComponentInstance operator, boolean updateRank, boolean remapGroup)
			throws WorkflowException {

		VertexMapping dagprop = dagvertex.getMapping();

		// Generating a list of vertices to remap in topological order
		List<MapperDAGVertex> vList = dagprop.getVertices((MapperDAG) dagvertex
				.getBase());
		List<MapperDAGVertex> orderedVList = new ArrayList<MapperDAGVertex>();
		// On the whole group otherwise
		CustomTopologicalIterator iterator = new CustomTopologicalIterator(dag,
				true);
		while (iterator.hasNext()) {
			MapperDAGVertex v = iterator.next();
			if (vList.contains(v)) {
				orderedVList.add(v);
			}
		}

		if(debugTraces)
			System.out.println("unmap and remap " + orderedVList + " on " + operator);

		for (MapperDAGVertex dv : orderedVList) {

			MapperDAGVertex dvi = translateInImplementationVertex(dv);
			ComponentInstance previousOperator = dvi.getEffectiveOperator();

			// We unmap systematically the main vertex (impvertex) if it has an
			// effectiveComponent and optionally its group
			boolean isToUnmap = (previousOperator != DesignTools.NO_COMPONENT_INSTANCE)
					&& (dv.equals(dagvertex) || remapGroup);

			// We map transfer vertices, if rank is kept, and if mappable
			boolean isToMap = (dv.equals(dagvertex) || remapGroup)
					&& (isMapable(dvi, operator, false) || !updateRank || dv instanceof TransferVertex);

			if (isToUnmap) {
				// Unmapping if necessary before mapping
				if(debugTraces)
					System.out.println("unmap " + dvi);
				unmap(dvi);

				if(debugTraces)
					System.out.println("unmapped " + dvi);
			}

			if (isToMap) {
				if(debugTraces)
					System.out.println("map " + dvi + " to " + operator);

				dv.setEffectiveOperator(operator);
				dvi.setEffectiveOperator(operator);

				fireNewMappedVertex(dvi, updateRank);

				if(debugTraces)
					System.out.println("mapped " + dvi);
				
			} else if (dv.equals(dagvertex) || remapGroup) {
				WorkflowLogger.getLogger().log(
						Level.SEVERE,
						dagvertex.toString() + " can not be mapped (group) on "
								+ operator.toString());
				dv.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
				dv.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
			}
		}

		if(debugTraces)
			System.out.println("unmapped and remapped " + orderedVList + " on " + operator);
	}

	/**
	 * Maps the vertex aith a group on the operator. If updaterank is true,
	 * finds a new place for the vertex in the schedule. Otherwise, use the
	 * vertex rank to know where to schedule it.
	 */
	@Override
	public final void map(MapperDAGVertex dagvertex,
			ComponentInstance operator, boolean updateRank, boolean remapGroup)
			throws WorkflowException {
		MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

		if (operator != DesignTools.NO_COMPONENT_INSTANCE) {
			// On a single actor if it is alone in the group
			if (impvertex.getMapping().getNumberOfVertices() < 2) {
				mapSingleVertex(dagvertex, operator, updateRank);
			} else {
				// Case of a group with several actors
				mapVertexWithGroup(dagvertex, operator, updateRank, remapGroup);
			}
		} else {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"Operator asked may not exist");
		}
	}

	/**
	 * Sets the total orders in the dag
	 */
	@Override
	public final void retrieveTotalOrder() {

		orderManager.tagDAG(dag);
	}

	/**
	 * maps all the vertices on the given operator if possible. If a vertex can
	 * not be executed on the given operator, looks for another operator with
	 * same type. If again none is found, looks for any other operator able to
	 * execute the vertex.
	 */
	@Override
	public final boolean mapAllVerticesOnOperator(ComponentInstance operator)
			throws WorkflowException {

		boolean possible = true;
		MapperDAGVertex currentvertex;

		TopologicalDAGIterator iterator = new TopologicalDAGIterator(dag);

		/*
		 * The listener is mapped in each vertex
		 */
		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex) iterator.next();

			// Looks for an operator able to execute currentvertex (preferably
			// the given operator)
			ComponentInstance adequateOp = findOperator(currentvertex,
					operator, true);

			if (adequateOp != null) {
				// Mapping in list order without remapping the group
				map(currentvertex, adequateOp, true, false);
			} else {
				WorkflowLogger
						.getLogger()
						.severe("The current mapping algorithm necessitates that all vertices can be mapped on an operator");
				WorkflowLogger.getLogger().severe(
						"Problem with: " + currentvertex.getName()
								+ ". Consider changing the scenario.");

				possible = false;
			}
		}

		return possible;
	}

	/**
	 * Looks for operators able to execute currentvertex. If the boolean
	 * protectGroupMapping is true and at least one vertex is mapped in the
	 * current vertex group, this unique operator is returned. Otherwise, the
	 * intersection of the available operators for the group is returned.
	 * 
	 * @throws WorkflowException
	 */
	@Override
	public List<ComponentInstance> getCandidateOperators(
			MapperDAGVertex vertex, boolean protectGroupMapping)
			throws WorkflowException {

		vertex = translateInImplementationVertex(vertex);

		List<ComponentInstance> initOperators = null;
		VertexMapping vm = vertex.getMapping();

		if (vm != null) {
			// Delegating the list construction to a mapping group
			initOperators = vm.getCandidateComponents(vertex,
					protectGroupMapping);
		} else {
			initOperators = vertex.getInit().getInitialOperatorList();
			WorkflowLogger.getLogger().log(Level.WARNING,
					"Found no mapping group for vertex " + vertex);
		}

		if (initOperators.isEmpty()) {
			String message = "Empty operator set for a vertex: "
					+ vertex.getName()
					+ ". Consider relaxing constraints in scenario.";
			WorkflowLogger.getLogger().log(Level.SEVERE, message);
			throw new WorkflowException(message);
		}

		return initOperators;
	}

	/**
	 * Looks for an operator able to execute currentvertex (preferably the given
	 * operator or an operator with same type) If the boolean
	 * protectGroupMapping is true and at least one vertex is mapped in the
	 * current vertex group, this unique operator is compared to the prefered
	 * one. Otherwise, the prefered operator is checked of belonging to
	 * available operators of the group.
	 * 
	 * @throws WorkflowException
	 */

	@Override
	public final ComponentInstance findOperator(MapperDAGVertex currentvertex,
			ComponentInstance preferedOperator, boolean protectGroupMapping)
			throws WorkflowException {

		ComponentInstance adequateOp = null;
		List<ComponentInstance> opList = getCandidateOperators(currentvertex,
				protectGroupMapping);

		if (DesignTools.contains(opList, preferedOperator)) {
			adequateOp = preferedOperator;
		} else {

			// Search among the operators with same type than the prefered one
			for (ComponentInstance op : opList) {
				if (preferedOperator != null
						&& op.getComponent()
								.getVlnv()
								.getName()
								.equals(preferedOperator.getComponent()
										.getVlnv().getName())) {
					adequateOp = op;
				}
			}

			// Search among the operators with other type than the prefered one
			if (adequateOp == null) {
				for (ComponentInstance op : opList) {
					adequateOp = op;
					return adequateOp;
				}
			}
		}

		return adequateOp;
	}

	/**
	 * Checks in the vertex implementation properties if it can be mapped on the
	 * given operator
	 */

	@Override
	public final boolean isMapable(MapperDAGVertex vertex,
			ComponentInstance operator, boolean protectGroupMapping)
			throws WorkflowException {

		return DesignTools.contains(
				getCandidateOperators(vertex, protectGroupMapping), operator);
	}

	/**
	 * *********Useful tools**********
	 */

	/**
	 * resets the costs of a set of edges
	 */
	protected final void resetCost(Set<DAGEdge> edges) {
		Iterator<DAGEdge> iterator = edges.iterator();

		while (iterator.hasNext()) {

			MapperDAGEdge edge = (MapperDAGEdge) iterator.next();
			if (!(edge instanceof PrecedenceEdge))
				edge.getTiming().resetCost();
		}
	}

	/**
	 * Returns the implementation vertex corresponding to the DAG vertex
	 */
	@Override
	public final MapperDAGVertex translateInImplementationVertex(
			MapperDAGVertex vertex) {

		MapperDAGVertex internalVertex = implementation
				.getMapperDAGVertex(vertex.getName());

		if (internalVertex == null) {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"No simulator internal vertex with id " + vertex.getName());
		}
		return internalVertex;
	}

	/**
	 * resets the costs of a set of edges
	 */
	private final MapperDAGEdge translateInImplementationEdge(MapperDAGEdge edge) {

		MapperDAGVertex sourceVertex = translateInImplementationVertex((MapperDAGVertex) edge
				.getSource());
		MapperDAGVertex destVertex = translateInImplementationVertex((MapperDAGVertex) edge
				.getTarget());

		if (destVertex == null || sourceVertex == null) {
			WorkflowLogger.getLogger().log(
					Level.SEVERE,
					"Implementation vertex with id " + edge.getSource()
							+ " or " + edge.getTarget() + " not found");
		} else {
			MapperDAGEdge internalEdge = (MapperDAGEdge) implementation
					.getEdge(sourceVertex, destVertex);
			return internalEdge;
		}

		return null;
	}

	/**
	 * Unmaps all vertices from implementation
	 */
	@Override
	public final void resetImplementation() {

		Iterator<DAGVertex> iterator = implementation.vertexSet().iterator();

		while (iterator.hasNext()) {
			unmap((MapperDAGVertex) iterator.next());
		}
	}

	/**
	 * Unmaps all vertices in both implementation and DAG Resets the order
	 * manager only at the end
	 */
	@Override
	public final void resetDAG() {

		Iterator<DAGVertex> iterator = dag.vertexSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGVertex v = (MapperDAGVertex) iterator.next();
			if (v.hasEffectiveComponent()) {
				unmap(v);
			}
		}

		orderManager.resetTotalOrder();
	}

	/**
	 * Removes the vertex implementation In silent mode, does not update
	 * implementation timings
	 */
	@Override
	public final void unmap(MapperDAGVertex dagvertex) {

		MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

		fireNewUnmappedVertex(impvertex);

		dagvertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);

		impvertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
	}

	/**
	 * Removes the vertex implementation of a group of vertices that share the
	 * same mapping. In silent mode, does not update implementation timings
	 */
	public final void unmap(List<MapperDAGVertex> dagvertices) {

		MapperDAGVertex cImpVertex = null;
		MapperDAGVertex cDagVertex = null;
		for (MapperDAGVertex dagvertex : dagvertices) {
			MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

			fireNewUnmappedVertex(impvertex);
			cDagVertex = dagvertex;
			cImpVertex = impvertex;
		}

		cDagVertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
		cImpVertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
	}

	/**
	 * Gets the cost of the given vertex
	 */
	@Override
	public final long getCost(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);
		return vertex.getTiming().getCost();
	}

	/**
	 * Gets the cost of the given edge in the implementation
	 */
	@Override
	public final long getCost(MapperDAGEdge edge) {
		edge = translateInImplementationEdge(edge);
		return edge.getTiming().getCost();

	}

	/**
	 * Setting the cost of an edge is delegated to ABC implementations
	 */
	protected abstract void setEdgeCost(MapperDAGEdge edge);

	/**
	 * An edge cost represents its cost taking into account a possible complex
	 * transfer of data
	 */
	protected final void setEdgesCosts(Set<DAGEdge> edgeset) {

		Iterator<DAGEdge> iterator = edgeset.iterator();

		while (iterator.hasNext()) {
			MapperDAGEdge edge = (MapperDAGEdge) iterator.next();

			if (!(edge instanceof PrecedenceEdge))
				setEdgeCost(edge);
		}
	}

	@Override
	public final AbcType getType() {
		return abcType;
	}

	/**
	 * Prepares task rescheduling
	 */
	@Override
	public void setTaskScheduler(AbstractTaskSched taskScheduler) {

		this.taskScheduler = taskScheduler;
		this.taskScheduler.setOrderManager(orderManager);

		if (this.taskScheduler instanceof TopologicalTaskSched) {
			((TopologicalTaskSched) this.taskScheduler)
					.createTopology(implementation);
		}
	}

}
