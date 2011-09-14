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

package org.ietr.preesm.plugin.abc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.plugin.abc.impl.latency.AccuratelyTimedAbc;
import org.ietr.preesm.plugin.abc.impl.latency.ApproximatelyTimedAbc;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.impl.latency.LooselyTimedAbc;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.order.VertexOrderList;
import org.ietr.preesm.plugin.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.plugin.abc.taskscheduling.TaskSwitcher;
import org.ietr.preesm.plugin.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.RelativeConstraint;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.esdf.SDFEndVertex;
import org.sdf4j.model.sdf.esdf.SDFInitVertex;

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
	protected MultiCoreArchitecture archi;

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	protected SchedOrderManager orderManager = null;

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
	 * Gets a new architecture simulator from a simulator type
	 */
	public static IAbc getInstance(AbcParameters params, MapperDAG dag,
			MultiCoreArchitecture archi, PreesmScenario scenario) {

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
	protected AbstractAbc(MapperDAG dag, MultiCoreArchitecture archi,
			AbcType abcType, PreesmScenario scenario) {

		this.abcType = abcType;
		orderManager = new SchedOrderManager(archi);

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

		RelativeConstraint currentConstraint = new RelativeConstraint();

		for (MapperDAGVertex v : verticesToAssociate) {
			RelativeConstraint newC = v.getImplementationVertexProperty()
					.getRelativeConstraint();
			if (newC == null) {
				currentConstraint.addVertex(v);
			} else {
				currentConstraint.merge(newC);
			}
			v.getImplementationVertexProperty().setRelativeConstraint(
					currentConstraint);
		}
	}

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
	public final MultiCoreArchitecture getArchitecture() {
		return this.archi;
	}

	public final PreesmScenario getScenario() {
		return scenario;
	}

	/**
	 * Gets the effective operator of the vertex. NO_OPERATOR if not set
	 */
	@Override
	public final ArchitectureComponent getEffectiveComponent(
			MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);
		return vertex.getImplementationVertexProperty().getEffectiveComponent();
	}

	/**
	 * *********Costs accesses**********
	 */

	@Override
	public abstract long getFinalCost();

	@Override
	public abstract long getFinalCost(MapperDAGVertex vertex);

	@Override
	public abstract long getFinalCost(ArchitectureComponent component);

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
	 * Maps the vertex on the operator. If updaterank is true, finds a new place
	 * for the vertex in the schedule. Otherwise, use the vertex rank to know
	 * where to schedule it.
	 */
	@Override
	public final void map(MapperDAGVertex dagvertex, Operator operator,
			boolean updateRank) {
		MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

		AbstractWorkflowLogger.getLogger().log(
				Level.FINE,
				"mapping " + dagvertex.toString() + " on "
						+ operator.toString());

		if (operator != Operator.NO_COMPONENT) {
			ImplementationVertexProperty dagprop = dagvertex
					.getImplementationVertexProperty();

			ImplementationVertexProperty impprop = impvertex
					.getImplementationVertexProperty();

			if (impprop.getEffectiveOperator() != Operator.NO_COMPONENT) {
				// Unmapping if necessary before mapping
				unmap(dagvertex);
			}

			if (isMapable(impvertex, operator) || !updateRank
					|| impvertex instanceof TransferVertex) {

				// Implementation property is set in both DAG and implementation
				dagprop.setEffectiveOperator(operator);
				impprop.setEffectiveOperator(operator);

				fireNewMappedVertex(impvertex, updateRank);

			} else {
				AbstractWorkflowLogger.getLogger().log(
						Level.SEVERE,
						impvertex.toString() + " can not be mapped on "
								+ operator.toString());
			}
		} else {
			AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
					"Operator asked may not exist");
		}
	}

	/**
	 * Sets the total orders in the dag
	 */
	public final void retrieveTotalOrder() {

		orderManager.tagDAG(dag);
	}

	/**
	 * maps all the vertices on the given operator if possible. If a vertex can
	 * not be executed on the given operator, looks for another operator with
	 * same type. If again none is found, looks for any other operator able to
	 * execute the vertex.
	 */
	public final boolean mapAllVerticesOnOperator(Operator operator) {

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
			Operator adequateOp = findOperator(currentvertex, operator);

			if (adequateOp != null) {
				map(currentvertex, adequateOp, true);
			} else {
				AbstractWorkflowLogger
						.getLogger()
						.severe("The current mapping algorithm necessitates that all vertices can be mapped on an operator");
				AbstractWorkflowLogger.getLogger().severe(
						"Problem with: " + currentvertex.getName()
								+ ". Consider changing the scenario.");

				possible = false;
			}
		}

		return possible;
	}

	/**
	 * Looks for operators able to execute currentvertex
	 */
	public List<Operator> getCandidateOperators(MapperDAGVertex vertex) {

		vertex = translateInImplementationVertex(vertex);

		List<Operator> initOperators = null;
		RelativeConstraint rc = vertex.getImplementationVertexProperty()
				.getRelativeConstraint();

		if (rc != null) {
			initOperators = rc.getOperatorsIntersection();
		} else {
			initOperators = vertex.getInitialVertexProperty()
					.getInitialOperatorList();
		}

		if (initOperators.isEmpty()) {
			AbstractWorkflowLogger.getLogger().log(
					Level.SEVERE,
					"Empty operator set for a vertex: " + vertex.getName()
							+ ". Consider relaxing constraints in scenario.");
		}

		return initOperators;
	}

	/**
	 * Looks for an operator able to execute currentvertex (preferably the given
	 * operator or an operator with same type)
	 */

	@Override
	public final Operator findOperator(MapperDAGVertex currentvertex,
			Operator preferedOperator) {

		Operator adequateOp = null;
		List<Operator> opList = getCandidateOperators(currentvertex);

		if (Operator.contains(opList, preferedOperator)) {
			adequateOp = preferedOperator;
		} else {

			// Search among the operators with same type than the prefered one
			for (Operator op : opList) {
				if (op.getDefinition().equals(preferedOperator.getDefinition())) {
					adequateOp = op;
				}
			}

			// Search among the operators with other type than the prefered one
			if (adequateOp == null) {
				for (Operator op : opList) {
					adequateOp = op;
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
	public final boolean isMapable(MapperDAGVertex vertex, Operator operator) {

		vertex = translateInImplementationVertex(vertex);

		return Operator.contains(getCandidateOperators(vertex), operator);
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
				edge.getTimingEdgeProperty().resetCost();
		}
	}

	/**
	 * Returns the implementation vertex corresponding to the DAG vertex
	 */
	protected final MapperDAGVertex translateInImplementationVertex(
			MapperDAGVertex vertex) {

		MapperDAGVertex internalVertex = implementation
				.getMapperDAGVertex(vertex.getName());

		if (internalVertex == null) {
			AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
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
			AbstractWorkflowLogger.getLogger().log(
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
	public final void resetDAG() {

		Iterator<DAGVertex> iterator = dag.vertexSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGVertex v = (MapperDAGVertex) iterator.next();
			if (v.getImplementationVertexProperty().hasEffectiveComponent()) {
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

		dagvertex.getImplementationVertexProperty().setEffectiveOperator(
				(Operator) Operator.NO_COMPONENT);

		impvertex.getImplementationVertexProperty().setEffectiveOperator(
				(Operator) Operator.NO_COMPONENT);
	}

	/**
	 * Gets the cost of the given vertex
	 */
	@Override
	public final long getCost(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);
		return vertex.getTimingVertexProperty().getCost();
	}

	/**
	 * Gets the cost of the given edge in the implementation
	 */
	@Override
	public final long getCost(MapperDAGEdge edge) {
		edge = translateInImplementationEdge(edge);
		return edge.getTimingEdgeProperty().getCost();

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

	public final AbcType getType() {
		return abcType;
	}

	/**
	 * Prepares task rescheduling
	 */
	public void setTaskScheduler(AbstractTaskSched taskScheduler) {

		this.taskScheduler = taskScheduler;
		this.taskScheduler.setOrderManager(orderManager);

		if (this.taskScheduler instanceof TopologicalTaskSched) {
			((TopologicalTaskSched) this.taskScheduler)
					.createTopology(implementation);
		}
	}

}
