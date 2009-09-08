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

package org.ietr.preesm.plugin.abc.impl.latency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;

import org.eclipse.swt.widgets.Composite;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.impl.ImplementationCleaner;
import org.ietr.preesm.plugin.abc.order.Schedule;
import org.ietr.preesm.plugin.abc.route.AbstractCommunicationRouter;
import org.ietr.preesm.plugin.abc.route.CommunicationRouter;
import org.ietr.preesm.plugin.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.ietr.preesm.plugin.mapper.plot.IImplementationPlotter;
import org.ietr.preesm.plugin.mapper.timekeeper.NewTimeKeeper;
import org.ietr.preesm.plugin.mapper.tools.SchedulingOrderIterator;
import org.ietr.preesm.plugin.mapper.tools.TLevelIterator;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Abc that simulates a dynamic scheduler
 * 
 * @author mpelcat
 */
public class DynamicQueuingAbc extends AbstractAbc {

	/**
	 * Current abc parameters
	 */
	protected AbcParameters params;

	private long currentSchedulerTime = 0l;

	private Map<String, Long> currentScheduleEnds = new HashMap<String, Long>();

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public DynamicQueuingAbc(AbcParameters params, MapperDAG dag,
			MultiCoreArchitecture archi, AbcType abcType, IScenario scenario) {
		super(dag, archi, abcType, scenario);

		this.params = params;
		currentSchedulerTime = 0l;

		for (ArchitectureComponent c : archi
				.getComponents(ArchitectureComponentType.operator)) {
			currentScheduleEnds.put(c.getName(), 0l);
		}
	}

	/**
	 * Sets the DAG as current DAG and retrieves all implementation to calculate
	 * timings
	 */
	@Override
	public void setDAG(MapperDAG dag) {

		currentSchedulerTime = 0l;

		for (ArchitectureComponent c : archi
				.getComponents(ArchitectureComponentType.operator)) {
			currentScheduleEnds.put(c.getName(), 0l);
		}

		this.dag = dag;
		this.implementation = dag.clone();

		orderManager.reconstructTotalOrderFromDAG(implementation);

		// Forces the unmapping process before the new mapping process
		HashMap<MapperDAGVertex, Operator> operators = new HashMap<MapperDAGVertex, Operator>();

		for (DAGVertex v : dag.vertexSet()) {
			MapperDAGVertex mdv = (MapperDAGVertex) v;
			operators.put(mdv, mdv.getImplementationVertexProperty()
					.getEffectiveOperator());
			mdv.getImplementationVertexProperty().setEffectiveComponent(
					Operator.NO_COMPONENT);
			implementation.getMapperDAGVertex(mdv.getName())
					.getImplementationVertexProperty().setEffectiveComponent(
							Operator.NO_COMPONENT);
			;
		}

		SchedulingOrderIterator iterator = new SchedulingOrderIterator(
				this.dag, this, true);

		while (iterator.hasNext()) {
			MapperDAGVertex vertex = iterator.next();
			Operator operator = operators.get(vertex);

			implant(vertex, operator, false);
		}
	}

	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex,
			boolean updateRank) {

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		if (effectiveOp == Operator.NO_COMPONENT) {
			PreesmLogger.getLogger().severe(
					"implementation of " + vertex.getName() + " failed");
		} else {

			long vertextime = vertex.getInitialVertexProperty().getTime(
					effectiveOp);

			// Set costs
			vertex.getTimingVertexProperty().setCost(vertextime);

			/*
			 * Here is the dynamic scheduler part, where timings are computed
			 */
			long currentScheduleEnd = currentScheduleEnds.get(effectiveOp
					.getName());

			vertex.getTimingVertexProperty().setNewtLevel(currentScheduleEnd);

			currentScheduleEnds.put(effectiveOp.getName(), currentScheduleEnd
					+ vertex.getTimingVertexProperty().getCost());

			setEdgesCosts(vertex.incomingEdges());
			setEdgesCosts(vertex.outgoingEdges());

			if (updateRank) {
				// No task scheduler: tasks are always scheduled at the end
				orderManager.addLast(vertex);
			} else {
				orderManager.insertVertexInTotalOrder(vertex);
			}

		}
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		// unimplanting a vertex resets the cost of the current vertex
		// and its edges

		ImplementationCleaner cleaner = new ImplementationCleaner(orderManager,
				implementation);
		cleaner.unscheduleVertex(vertex);

		// Keeps the total order
		orderManager.remove(vertex, false);

		vertex.getTimingVertexProperty().resetCost();
		resetCost(vertex.incomingEdges());
		resetCost(vertex.outgoingEdges());

	}

	/**
	 * Setting edge costs
	 */
	@Override
	protected void setEdgeCost(MapperDAGEdge edge) {
		edge.getTimingEdgeProperty().setCost(0l);
	}

	/**
	 * *********Timing accesses**********
	 */

	/**
	 * The cost of a vertex is the end time of its execution (latency
	 * minimization)
	 */
	@Override
	public final long getFinalCost(MapperDAGVertex vertex) {
		return 0l;

	}

	/**
	 * The cost of a component is the end time of its last vertex (latency
	 * minimization)
	 */
	@Override
	public final long getFinalCost(ArchitectureComponent component) {
		return 0l;
	}

	/**
	 * The cost of an implementation is calculated from its latency and loads
	 */
	@Override
	public final long getFinalCost() {
		return 0l;
	}

	/**
	 * Plots the current implementation. If delegatedisplay=false, the gantt is
	 * displayed in a shell. Otherwise, it is displayed in Eclipse.
	 */
	@Override
	public final void plotImplementation(Composite delegateDisplay) {

		GanttPlotter.plotDeployment(implementation, this.getArchitecture(),
				delegateDisplay);
	}

	@Override
	public void reschedule(List<String> totalOrder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reschedule() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rescheduleTransfers(List<MapperDAGVertex> orderlist) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFinalCosts() {
		// TODO Auto-generated method stub

	}

	@Override
	public EdgeSchedType getEdgeSchedType() {
		// TODO Auto-generated method stub
		return null;
	}
}
