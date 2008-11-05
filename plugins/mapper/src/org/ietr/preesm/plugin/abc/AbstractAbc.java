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

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.accuratelytimed.AccuratelyTimedAbc;
import org.ietr.preesm.plugin.abc.approximatelytimed.ApproximatelyTimedAbc;
import org.ietr.preesm.plugin.abc.commconten.CommContenAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.looselytimed.LooselyTimedAbc;
import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.abc.sendreceive.SendReceiveAbc;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.implementation.TransferVertex;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
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
	 * ID used to reference the element in a property bean in case of a
	 * computation vertex
	 */
	public static final String propertyBeanName = "AbcReferenceType";

	/**
	 * Architecture related to the current simulator
	 */
	protected MultiCoreArchitecture archi;

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	protected SchedulingOrderManager orderManager = null;

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
	 * Current time keeper: called exclusively by simulator to update the useful
	 * time tags in DAG
	 */
	protected GraphTimeKeeper timeKeeper;

	/**
	 * Transactions are used to add/remove vertices in the implementation
	 */
	protected TransactionManager transactionManager = new TransactionManager();

	/**
	 * Gets the architecture simulator from a simulator type
	 */
	public static IAbc getInstance(AbcType simulatorType, MapperDAG dag,
			MultiCoreArchitecture archi) {

		if (simulatorType == AbcType.InfiniteHomogeneous) {
			return new InfiniteHomogeneousAbc(dag, archi);
		} else if (simulatorType == AbcType.LooselyTimed) {
			return new LooselyTimedAbc(dag, archi);
		} else if (simulatorType == AbcType.ApproximatelyTimed) {
			return new ApproximatelyTimedAbc(dag, archi);
		} else if (simulatorType == AbcType.AccuratelyTimed) {
			return new AccuratelyTimedAbc(dag, archi);
		} else if (simulatorType == AbcType.CommConten) {
			return new CommContenAbc(dag, archi);
		} else if (simulatorType == AbcType.SendReceive) {
			return new SendReceiveAbc(dag, archi);
		}

		return null;
	}

	/**
	 * Architecture simulator constructor
	 */
	public AbstractAbc(MapperDAG dag, MultiCoreArchitecture archi) {

		orderManager = new SchedulingOrderManager();

		this.dag = dag;
		// implementation is a duplicate from dag
		this.implementation = dag.clone();

		this.timeKeeper = new GraphTimeKeeper(implementation);
		timeKeeper.resetTimings();

		this.archi = archi;

		// currentRank = 0;
	}

	public MapperDAG getDAG() {
		return dag;
	}

	/**
	 * Sets the DAG as current DAG and retrieves all implementation to calculate
	 * timings
	 */
	public void setDAG(MapperDAG dag) {

		this.dag = dag;
		this.implementation = dag.clone();

		this.timeKeeper = new GraphTimeKeeper(implementation);
		timeKeeper.resetTimings();

		orderManager.reconstructTotalOrderFromDAG(implementation);

		SchedulingOrderIterator iterator = new SchedulingOrderIterator(
				this.dag, this, true);

		while (iterator.hasNext()) {
			MapperDAGVertex vertex = iterator.next();
			implant(vertex, vertex.getImplementationVertexProperty()
					.getEffectiveOperator(), false);
		}
	}

	/**
	 * Called whenever the implementation of a vertex occurs
	 */
	protected abstract void fireNewMappedVertex(MapperDAGVertex vertex);

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
	 * *********Timing accesses**********
	 */

	@Override
	public final int getFinalTime() {

		updateTimings();

		// visualize results
		// monitor.render(new SimpleTextRenderer());

		int finalTime = timeKeeper.getFinalTime();

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative implementation final time");
		}

		return finalTime;
	}

	@Override
	public final int getFinalTime(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		updateTimings();

		int finalTime = timeKeeper.getFinalTime(vertex);

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative vertex final time");
		}

		return finalTime;

	}

	@Override
	public final int getFinalTime(ArchitectureComponent component) {

		updateTimings();

		int finalTime = timeKeeper.getFinalTime(component);

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative component final time");
		}

		return finalTime;
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

		return orderManager.getSchedulingOrder(vertex);
	}

	/**
	 * Gets the total rank of the given vertex. -1 if the vertex has no rank
	 */
	@Override
	public final int getSchedulingTotalOrder(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		return orderManager.getSchedulingTotalOrder(vertex);
	}

	/**
	 * Gets the time keeper
	 */
	@Override
	public final GraphTimeKeeper getTimeKeeper() {
		return this.timeKeeper;
	}

	@Override
	public final int getTLevel(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		updateTimings();
		return vertex.getTimingVertexProperty().getTlevel();
	}

	@Override
	public final int getBLevel(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		updateTimings();
		return vertex.getTimingVertexProperty().getValidBlevel();
	}

	/**
	 * Implants the vertex on the operator
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

				// Vertex schedule order is reset but not total order
				orderManager.remove(impvertex, false);

				// Implantation property is set in both DAG and implementation
				dagprop.setEffectiveOperator((Operator) Operator.NO_COMPONENT);
				impprop.setEffectiveOperator((Operator) Operator.NO_COMPONENT);

				fireNewUnmappedVertex(impvertex);

			}

			if (isImplantable(impvertex, operator)
					|| impvertex instanceof TransferVertex) {

				// Implantation property is set in both DAG and implementation
				dagprop.setEffectiveOperator(operator);
				impprop.setEffectiveOperator(operator);

				if (updateRank) {
					orderManager.addVertex(impvertex);
				} else {
					orderManager.insertVertexInTotalOrder(impvertex);
				}

				fireNewMappedVertex(impvertex);

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

		timeKeeper.setAsDirty(impvertex);
	}

	/**
	 * Sets the total orders in the dag
	 */
	public void retrieveTotalOrder() {

		orderManager.tagDAG(dag);
	}

	/**
	 * implants all the vertices on the given operator
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

			if (isImplantable(currentvertex, operator)) {
				implant(currentvertex, operator, true);
			} else {

				boolean foundAlternative = false;

				for (Operator op : currentvertex.getInitialVertexProperty()
						.getOperatorSet()) {
					if (op.getDefinition().equals(operator.getDefinition())) {
						if (isImplantable(currentvertex, op)) {
							implant(currentvertex, op, true);
							foundAlternative = true;

							PreesmLogger.getLogger().info(
									"The vertex: " + currentvertex.getName()
											+ " could not be mapped on main operator "
											+ operator.getName()
											+ ". An alternative with same definition was found.");
						}
					}
				}

				if (!foundAlternative){

					for (Operator op : currentvertex.getInitialVertexProperty()
							.getOperatorSet()) {
						if (isImplantable(currentvertex, op)) {
								implant(currentvertex, op, true);
								foundAlternative = true;

								PreesmLogger.getLogger().info(
										"The vertex: " + currentvertex.getName()
												+ " could not be mapped on main operator "
												+ operator.getName()
												+ ". An alternative with another definition was found.");
						}
					}
				}

				if (!foundAlternative)
					PreesmLogger
							.getLogger()
							.severe(
									"The current mapping algorithm necessitates that all vertices can be mapped on an operator");
			}
		}

		return possible;
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
	 * Plots the current implementation. If delegatedisplay=false, the
	 * gantt is displayed in a shell. Otherwise, it is displayed
	 * in Eclipse.
	 */
	public GanttPlotter plotImplementation(boolean delegateDisplay) {
		
		if(!delegateDisplay){
			updateTimings();
			GanttPlotter.plot(implementation, this);
			return null;
		}
		else{			
			updateTimings();
			return new GanttPlotter("Solution gantt", implementation, this);
		}
	}

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
	private final MapperDAGVertex translateInImplementationVertex(
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

		orderManager.resetTotalOrder();

		resetImplementation();

		Iterator<DAGVertex> iterator = dag.vertexSet().iterator();

		while (iterator.hasNext()) {
			unimplant((MapperDAGVertex) iterator.next());
		}
	}

	/**
	 * Removes the vertex implementation In silent mode, does not update
	 * implementation timings
	 */
	public void unimplant(MapperDAGVertex dagvertex) {

		MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

		dagvertex.getImplementationVertexProperty().setEffectiveOperator(
				(Operator) Operator.NO_COMPONENT);

		impvertex.getImplementationVertexProperty().setEffectiveOperator(
				(Operator) Operator.NO_COMPONENT);

		orderManager.remove(impvertex, false);

		timeKeeper.setAsDirty(impvertex);
	}

	/**
	 * Gets the time of the given vertex
	 */
	@Override
	public final int getCost(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);
		return vertex.getTimingVertexProperty().getCost();
	}

	/**
	 * Gets the cost of the given vertex in the implementation
	 */
	@Override
	public int getCost(MapperDAGEdge edge) {
		edge = translateInImplantationEdge(edge);
		return edge.getTimingEdgeProperty().getCost();

	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 */
	protected abstract void updateTimings();

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

}
