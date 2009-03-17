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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.impl.CommContenAbc;
import org.ietr.preesm.plugin.abc.impl.SendReceiveAbc;
import org.ietr.preesm.plugin.abc.impl.latency.AccuratelyTimedAbc;
import org.ietr.preesm.plugin.abc.impl.latency.ApproximatelyTimedAbc;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.impl.latency.LooselyTimedAbc;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.order.Schedule;
import org.ietr.preesm.plugin.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.plugin.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.plugin.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.ietr.preesm.plugin.mapper.plot.IImplementationPlotter;
import org.ietr.preesm.plugin.mapper.timekeeper.GraphTimeKeeper;
import org.ietr.preesm.plugin.mapper.tools.SchedulingOrderIterator;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Architecture simulators common features An architecture simulator calculates
 * costs for a given partial or total implementation
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
	 * Transactions are used to add/remove vertices in the implementation
	 */
	protected TransactionManager transactionManager = new TransactionManager();

	/**
	 * Current Abc type
	 */
	protected AbcType abcType = null;

	/**
	 * Task scheduler
	 */
	protected AbstractTaskSched taskScheduler = null;

	/**
	 * Gets the architecture simulator from a simulator type
	 */
	public static IAbc getInstance(AbcType simulatorType,
			EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi) {

		AbstractAbc abc = null;

		if (simulatorType == AbcType.InfiniteHomogeneous) {
			abc = new InfiniteHomogeneousAbc(edgeSchedType, dag, archi);
		} else if (simulatorType == AbcType.LooselyTimed) {
			abc = new LooselyTimedAbc(edgeSchedType, dag, archi, simulatorType);
		} else if (simulatorType == AbcType.ApproximatelyTimed) {
			abc = new ApproximatelyTimedAbc(edgeSchedType, dag, archi,
					simulatorType);
		} else if (simulatorType == AbcType.AccuratelyTimed) {
			abc = new AccuratelyTimedAbc(edgeSchedType, dag, archi,
					simulatorType);
		} else if (simulatorType == AbcType.CommConten) {
			abc = new CommContenAbc(edgeSchedType, dag, archi, simulatorType);
		} else if (simulatorType == AbcType.SendReceive) {
			abc = new SendReceiveAbc(edgeSchedType, dag, archi, simulatorType);
		}

		return abc;
	}

	/**
	 * Architecture simulator constructor
	 */
	protected AbstractAbc(MapperDAG dag, MultiCoreArchitecture archi,
			AbcType abcType) {

		this.abcType = abcType;
		orderManager = new SchedOrderManager();

		this.dag = dag;
		// implementation is a duplicate from dag
		this.implementation = dag.clone();

		this.archi = archi;
		
		resetTaskScheduler(TaskSchedType.Topological);
	}

	public MapperDAG getDAG() {
		return dag;
	}

	public MapperDAG getImpl() {
		return implementation;
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
	public MultiCoreArchitecture getArchitecture() {
		return this.archi;
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

	@Override
	public final long getLoad(ArchitectureComponent component) {

		long load = 0;

		if (implementation != null) {

			for (DAGVertex v : implementation.vertexSet()) {
				MapperDAGVertex mv = (MapperDAGVertex) v;
				if (mv.getImplementationVertexProperty()
						.getEffectiveComponent().equals(component)) {
					load += getCost(mv);
				}
			}
		}

		return load;
	}

	/**
	 * *********Implantation accesses**********
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
	public Schedule getTotalOrder() {
		return orderManager.getTotalOrder();
	}

	/**
	 * Reorders the implementation using the given total order
	 */
	public void reorder(Map<String, Integer> totalOrder) {
		/*
		 * int test = totalOrder.get("IDFT_7"); int test2 =
		 * totalOrder.get("FFT_a1_3");
		 * 
		 * // Just for test. Checks for doublets Set<String> sSet = new
		 * HashSet<String>(); for(DAGVertex v : implementation.vertexSet()){
		 * MapperDAGVertex implVertex = (MapperDAGVertex)v;
		 * if(sSet.contains(implVertex.getName())){
		 * PreesmLogger.getLogger().log(Level.SEVERE,"duplicated vertex: " +
		 * implVertex.getName()); } sSet.add(implVertex.getName()); }
		 */

		if (implementation != null && dag != null) {

			for (String vName : totalOrder.keySet()) {
				MapperDAGVertex ImplVertex = (MapperDAGVertex) implementation
						.getVertex(vName);
				if (ImplVertex != null)
					ImplVertex.getImplementationVertexProperty()
							.setSchedTotalOrder(totalOrder.get(vName));

				MapperDAGVertex dagVertex = (MapperDAGVertex) dag
						.getVertex(vName);
				if (dagVertex != null)
					dagVertex.getImplementationVertexProperty()
							.setSchedTotalOrder(totalOrder.get(vName));

			}

			orderManager.reconstructTotalOrderFromDAG(implementation);

			TransactionManager localTransactionManager = new TransactionManager();
			PrecedenceEdgeAdder precEdgeAdder = new PrecedenceEdgeAdder(
					orderManager);
			precEdgeAdder.removePrecedenceEdges(implementation,
					localTransactionManager);
			precEdgeAdder.addPrecedenceEdges(implementation,
					localTransactionManager);

		}
	}

	/**
	 * Implants the vertex on the operator. If updaterank is true, finds a new
	 * place for the vertex in the schedule. Otherwise, use the vertex rank to
	 * know where to schedule it.
	 */
	@Override
	public void implant(MapperDAGVertex dagvertex, Operator operator,
			boolean updateRank) {
		MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

		if (operator != Operator.NO_COMPONENT) {
			ImplementationVertexProperty dagprop = dagvertex
					.getImplementationVertexProperty();

			ImplementationVertexProperty impprop = impvertex
					.getImplementationVertexProperty();

			if (impprop.getEffectiveOperator() != Operator.NO_COMPONENT) {
				// Unimplanting if necessary before implanting
				unimplant(dagvertex);
			}

			if (isImplantable(impvertex, operator)
					|| impvertex instanceof TransferVertex) {

				// Implantation property is set in both DAG and implementation
				dagprop.setEffectiveOperator(operator);
				impprop.setEffectiveOperator(operator);

				fireNewMappedVertex(impvertex, updateRank);

			} else {
				PreesmLogger.getLogger().log(
						Level.SEVERE,
						impvertex.toString() + " can not be implanted on "
								+ operator.toString());
			}
		} else {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"Operator asked may not exist");
		}
	}

	/**
	 * Sets the total orders in the dag
	 */
	public void retrieveTotalOrder() {

		orderManager.tagDAG(dag);
	}

	/**
	 * implants all the vertices on the given operator if possible. If a vertex
	 * can not be executed on the given operator, looks for another operator
	 * with same type. If again none is found, looks for any other operator able
	 * to execute the vertex.
	 */
	public boolean implantAllVerticesOnOperator(Operator operator) {

		boolean possible = true;
		MapperDAGVertex currentvertex;
		TopologicalDAGIterator iterator = new TopologicalDAGIterator(
				implementation);

		/*
		 * The listener is implanted in each vertex
		 */
		while (iterator.hasNext()) {
			currentvertex = (MapperDAGVertex) iterator.next();

			// Looks for an operator able to execute currentvertex (preferably
			// the given operator)
			Operator adequateOp = findOperator(currentvertex, operator);

			if (adequateOp != null) {
				implant(currentvertex, adequateOp, true);
			} else {
				PreesmLogger
						.getLogger()
						.severe(
								"The current mapping algorithm necessitates that all vertices can be mapped on an operator");
				PreesmLogger.getLogger().severe(
						"Problem with: " + currentvertex.getName() + ". Consider changing the scenario.");

				possible = false;
			}
		}

		return possible;
	}

	/**
	 * Looks for an operator able to execute currentvertex (preferably the given
	 * operator)
	 */
	public Operator findOperator(MapperDAGVertex currentvertex,
			Operator preferedOperator) {

		Operator adequateOp = null;

		if (isImplantable(currentvertex, preferedOperator)) {
			adequateOp = preferedOperator;
		} else {

			// Search among the operators with same type than the prefered one
			for (Operator op : currentvertex.getInitialVertexProperty()
					.getOperatorSet()) {
				if (op.getDefinition().equals(preferedOperator.getDefinition())) {
					if (isImplantable(currentvertex, op)) {
						adequateOp = op;

						PreesmLogger
								.getLogger()
								.info(
										"The vertex: "
												+ currentvertex.getName()
												+ " could not be mapped on main operator "
												+ preferedOperator.getName()
												+ ". An alternative with same definition was found.");
					}
				}
			}

			// Search among the operators with other type than the prefered one
			if (adequateOp == null) {

				for (Operator op : currentvertex.getInitialVertexProperty()
						.getOperatorSet()) {
					if (isImplantable(currentvertex, op)) {
						adequateOp = op;

						PreesmLogger
								.getLogger()
								.info(
										"The vertex: "
												+ currentvertex.getName()
												+ " could not be mapped on main operator "
												+ preferedOperator.getName()
												+ ". An alternative with another definition was found.");
					}
				}
			}
		}

		return adequateOp;
	}

	/**
	 * Checks in the vertex implementation properties if it can be implanted on
	 * the given operator
	 */
	@Override
	public boolean isImplantable(MapperDAGVertex vertex, Operator operator) {

		vertex = translateInImplementationVertex(vertex);
		return vertex.getInitialVertexProperty().isImplantable(operator);
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
			PreesmLogger.getLogger().log(Level.SEVERE,
					"No simulator internal vertex with id " + vertex.getName());
		}
		return internalVertex;
	}

	/**
	 * resets the costs of a set of edges
	 */
	private final MapperDAGEdge translateInImplantationEdge(MapperDAGEdge edge) {

		MapperDAGVertex sourceVertex = translateInImplementationVertex((MapperDAGVertex) edge
				.getSource());
		MapperDAGVertex destVertex = translateInImplementationVertex((MapperDAGVertex) edge
				.getTarget());

		if (destVertex == null || sourceVertex == null) {
			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"Implantation vertex with id " + edge.getSource() + " or "
							+ edge.getTarget() + " not found");
		} else {
			MapperDAGEdge internalEdge = (MapperDAGEdge) implementation
					.getEdge(sourceVertex, destVertex);
			return internalEdge;
		}

		return null;
	}

	/**
	 * Unimplants all vertices
	 * 
	 * Resets the time keeper only at the end
	 */
	public void resetImplementation() {

		Iterator<DAGVertex> iterator = implementation.vertexSet().iterator();

		while (iterator.hasNext()) {
			unimplant((MapperDAGVertex) iterator.next());
		}
	}

	/**
	 * Unimplants all vertices in both implementation and DAG
	 * 
	 * Resets the time keeper only at the end
	 */
	public void resetDAG() {

		Iterator<DAGVertex> iterator = dag.vertexSet().iterator();

		while (iterator.hasNext()) {
			unimplant((MapperDAGVertex) iterator.next());
		}

		orderManager.resetTotalOrder();
	}

	/**
	 * Removes the vertex implementation In silent mode, does not update
	 * implementation timings
	 */
	public void unimplant(MapperDAGVertex dagvertex) {

		MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

		fireNewUnmappedVertex(impvertex);

		dagvertex.getImplementationVertexProperty().setEffectiveOperator(
				(Operator) Operator.NO_COMPONENT);

		impvertex.getImplementationVertexProperty().setEffectiveOperator(
				(Operator) Operator.NO_COMPONENT);

		// Keeps the total order
		orderManager.remove(impvertex, false);

		// PreesmLogger.getLogger().log(Level.SEVERE,"unimplanting " +
		// impvertex.getName() + "\n");
	}

	/**
	 * Gets the time of the given vertex
	 */
	@Override
	public final long getCost(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);
		return vertex.getTimingVertexProperty().getCost();
	}

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	@Override
	public long getCost(MapperDAGEdge edge) {
		edge = translateInImplantationEdge(edge);
		return edge.getTimingEdgeProperty().getCost();

	}

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

	public AbcType getType() {
		return abcType;
	}

	public void resetTaskScheduler(TaskSchedType taskSchedType){

		taskScheduler = AbstractTaskSched.getInstance(taskSchedType, orderManager);
		if(taskScheduler instanceof TopologicalTaskSched){
			((TopologicalTaskSched)taskScheduler).createTopology(implementation);
		}
	}

}
