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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.impl.ImplementationCleaner;
import org.ietr.preesm.plugin.abc.order.Schedule;
import org.ietr.preesm.plugin.abc.route.AbstractCommunicationRouter;
import org.ietr.preesm.plugin.abc.route.CommunicationRouter;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.OverheadVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.ietr.preesm.plugin.mapper.plot.IImplementationPlotter;
import org.ietr.preesm.plugin.mapper.timekeeper.GraphTimeKeeper;
import org.ietr.preesm.plugin.mapper.tools.SchedulingOrderIterator;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Abc that minimizes latency
 * 
 * @author mpelcat
 */
public abstract class LatencyAbc extends AbstractAbc {

	/**
	 * Current precedence edge adder: called exclusively by simulator to
	 * schedule vertices on the different operators
	 */
	protected PrecedenceEdgeAdder precedenceEdgeAdder;

	/**
	 * Current time keeper: called exclusively by simulator to update the useful
	 * time tags in DAG
	 */
	protected GraphTimeKeeper timeKeeper;

	protected AbstractCommunicationRouter comRouter = null;

	/**
	 * Scheduling the transfer vertices on the media
	 */
	protected IEdgeSched edgeScheduler;

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public LatencyAbc(EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi, AbcType abcType, IScenario scenario) {
		super(dag, archi, abcType, scenario);
		precedenceEdgeAdder = new PrecedenceEdgeAdder(orderManager);

		this.timeKeeper = new GraphTimeKeeper(implementation);
		timeKeeper.resetTimings();

		// The media simulator calculates the edges costs
		edgeScheduler = AbstractEdgeSched.getInstance(edgeSchedType,
				orderManager);
		comRouter = new CommunicationRouter(archi, scenario, implementation,
				edgeScheduler, orderManager);
	}

	/**
	 * Sets the DAG as current DAG and retrieves all implementation to calculate
	 * timings
	 */
	@Override
	public void setDAG(MapperDAG dag) {

		this.dag = dag;
		this.implementation = dag.clone();

		orderManager.reconstructTotalOrderFromDAG(implementation);

		this.timeKeeper = new GraphTimeKeeper(implementation);
		timeKeeper.resetTimings();

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

		edgeScheduler = AbstractEdgeSched.getInstance(edgeScheduler
				.getEdgeSchedType(), orderManager);
		comRouter.setManagers(implementation, edgeScheduler, orderManager);

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

			if (updateRank) {
				taskScheduler.insertVertex(vertex);
			} else {
				orderManager.insertVertexInTotalOrder(vertex);
			}

			long vertextime = vertex.getInitialVertexProperty().getTime(
					effectiveOp);

			// Set costs
			vertex.getTimingVertexProperty().setCost(vertextime);

			setEdgesCosts(vertex.incomingEdges());
			setEdgesCosts(vertex.outgoingEdges());
		}
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		// unimplanting a vertex resets the cost of the current vertex
		// and its edges

		vertex.getTimingVertexProperty().resetCost();

		resetCost(vertex.incomingEdges());
		resetCost(vertex.outgoingEdges());

		ImplementationCleaner cleaner = new ImplementationCleaner(orderManager,
				implementation);
		cleaner.removeAllOverheads(vertex);
		cleaner.removeAllTransfers(vertex);
		cleaner.unscheduleVertex(vertex);
	}

	@Override
	public void implant(MapperDAGVertex dagvertex, Operator operator,
			boolean updateRank) {
		super.implant(dagvertex, operator, updateRank);
		timeKeeper.setAsDirty(dagvertex);
	}

	@Override
	public void unimplant(MapperDAGVertex dagvertex) {
		super.unimplant(dagvertex);
		timeKeeper.setAsDirty(dagvertex);
	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 * Depending on the king of timings we want, calls the necessary updates.
	 */
	protected void updateTimings() {

		timeKeeper.updateTLevels();
	}

	/**
	 * Setting edge costs for special types
	 */
	@Override
	protected void setEdgeCost(MapperDAGEdge edge) {

		// Special vertices create edges with dissuasive costs so that they
		// are mapped correctly: fork after the sender and join before the
		// receiver
		if ((edge.getTarget() != null && SpecialVertexManager.isFork(edge
				.getTarget()))
		/*
		 * || (edge.getSource() != null && SpecialVertexManager
		 * .isJoin(edge.getSource()))
		 */) {
			ImplementationVertexProperty sourceimp = ((MapperDAGVertex) edge
					.getSource()).getImplementationVertexProperty();
			ImplementationVertexProperty destimp = ((MapperDAGVertex) edge
					.getTarget()).getImplementationVertexProperty();

			Operator sourceOp = sourceimp.getEffectiveOperator();
			Operator destOp = destimp.getEffectiveOperator();

			if (sourceOp != Operator.NO_COMPONENT
					&& destOp != Operator.NO_COMPONENT) {
				if (sourceOp.equals(destOp)) {
					edge.getTimingEdgeProperty().setCost(0);
				} else {
					edge.getTimingEdgeProperty().setCost(
							SpecialVertexManager.dissuasiveCost);
				}
			}
		}
	}

	public abstract EdgeSchedType getEdgeSchedType();

	/**
	 * *********Timing accesses**********
	 */

	@Override
	public final long getFinalCost() {

		updateTimings();

		// visualize results
		// monitor.render(new SimpleTextRenderer());

		long finalTime = timeKeeper.getFinalTime();

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative implementation final time");
		}

		return finalTime;
	}

	@Override
	public final long getFinalCost(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		updateTimings();

		long finalTime = timeKeeper.getFinalTime(vertex);

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative vertex final time");
		}

		return finalTime;

	}

	@Override
	public final long getFinalCost(ArchitectureComponent component) {

		updateTimings();

		long finalTime = timeKeeper.getFinalTime(component);

		if (finalTime < 0) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"negative component final time");
		}

		return finalTime;
	}

	public final long getTLevel(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		updateTimings();
		return vertex.getTimingVertexProperty().getTlevel();
	}

	public final long getBLevel(MapperDAGVertex vertex) {
		vertex = translateInImplementationVertex(vertex);

		updateTimings();
		return vertex.getTimingVertexProperty().getBlevel();
	}

	/**
	 * Plots the current implementation. If delegatedisplay=false, the gantt is
	 * displayed in a shell. Otherwise, it is displayed in Eclipse.
	 */
	public final IImplementationPlotter plotImplementation(
			boolean delegateDisplay) {

		if (!delegateDisplay) {
			updateTimings();
			GanttPlotter.plot(implementation, this);
			return null;
		} else {
			updateTimings();
			return new GanttPlotter("Solution gantt", implementation, this);
		}
	}

	public AbstractCommunicationRouter getComRouter() {
		return comRouter;
	}

	private void addVertexAfterSourceLastTransfer(MapperDAGVertex v,
			List<String> orderedNames) {
		DAGVertex source = ((MapperDAGEdge) v.incomingEdges().toArray()[0])
				.getSource();
		int predIdx = orderedNames.indexOf(source.getName()) + 1;
		try {
			while (orderedNames.get(predIdx).indexOf("__transfer") == 0
					|| orderedNames.get(predIdx).indexOf("__overhead") == 0) {
				predIdx++;
			}
			orderedNames.add(predIdx, v.getName());
		} catch (IndexOutOfBoundsException e) {
			orderedNames.add(v.getName());
		}
	}

	private void addVertexBeforeTargetFirstTransfer(MapperDAGVertex v,
			List<String> orderedNames) {
		DAGVertex target = ((MapperDAGEdge) v.outgoingEdges().toArray()[0])
				.getTarget();
		int predIdx = orderedNames.indexOf(target.getName()) - 1;
		try {
			while (orderedNames.get(predIdx).indexOf("__transfer") == 0) {
				predIdx--;
			}
			orderedNames.add(predIdx + 1, v.getName());
		} catch (IndexOutOfBoundsException e) {
			orderedNames.add(0, v.getName());
		}
	}

	private void addVertexBeforeTarget(MapperDAGVertex v,
			List<String> orderedNames) {
		DAGVertex target = ((MapperDAGEdge) v.outgoingEdges().toArray()[0])
				.getTarget();
		int predIdx = orderedNames.indexOf(target.getName()) - 1;
		try {
			orderedNames.add(predIdx + 1, v.getName());
		} catch (IndexOutOfBoundsException e) {
			orderedNames.add(0, v.getName());
		}
	}

	private void addVertexAfterSourceLastOverhead(MapperDAGVertex v,
			List<String> orderedNames) {
		DAGVertex source = ((MapperDAGEdge) v.incomingEdges().toArray()[0])
				.getSource();
		int predIdx = orderedNames.indexOf(source.getName()) + 1;
		try {
			while (orderedNames.get(predIdx).indexOf("__overhead") == 0) {
				predIdx++;
			}
			orderedNames.add(predIdx, v.getName());
		} catch (IndexOutOfBoundsException e) {
			orderedNames.add(v.getName());
		}
	}

	/**
	 * Reschedule all the transfers generated during mapping
	 */
	public void rescheduleTransfers(List<MapperDAGVertex> cpnDominantList) {
		Schedule totalOrder = this.getTotalOrder();
		List<String> orderedNames = new ArrayList<String>();

		for (MapperDAGVertex v : totalOrder) {
			if (v instanceof TransferVertex) {
				// addVertexAfterSourceLastTransfer(v, orderedNames);
			} else if (v instanceof OverheadVertex) {
				addVertexAfterSourceLastOverhead(v, orderedNames);
			} else {
				orderedNames.add(v.getName());
			}
		}

		for(int index = cpnDominantList.size()-1;index >= 0 ; index--){
			MapperDAGVertex v = cpnDominantList.get(index);
			for (DAGVertex t : ImplementationCleaner.getFollowingTransfers(this.translateInImplementationVertex(v))) {
				if (!orderedNames.contains(t.getName())) {
					addVertexAfterSourceLastTransfer((MapperDAGVertex)t, orderedNames);
				}
			}
		}
		/*
		for (MapperDAGVertex v : cpnDominantList) {
			for (DAGVertex t : ImplementationCleaner.getPrecedingTransfers(this.translateInImplementationVertex(v))) {
				if (!orderedNames.contains(t.getName())) {
					addVertexBeforeTarget((MapperDAGVertex)t, orderedNames);
				}
			}
		}*/

		/*
		 * MapperDAGVertex v = totalOrder.getLast();
		 * 
		 * while(v!=null){ if (v instanceof TransferVertex) {
		 * addVertexBeforeTargetFirstTransfer(v, orderedNames); } else if (v
		 * instanceof OverheadVertex) { //addVertexAfterSourceLastOverhead(v,
		 * orderedNames); } else { //orderedNames.add(v.getName()); } v =
		 * totalOrder.getPreviousVertex(v); }
		 */
		//reorder(orderedNames);
	}
}
