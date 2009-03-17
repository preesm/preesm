/**
 * 
 */
package org.ietr.preesm.plugin.abc.impl.latency;

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.route.CommunicationRouter;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.ietr.preesm.plugin.mapper.plot.IImplementationPlotter;
import org.ietr.preesm.plugin.mapper.timekeeper.GraphTimeKeeper;

/**
 * Abc that minimizes latency
 * @author mpelcat
 */
public abstract class LatencyAbc extends AbstractAbc {

	/**
	 * simulator of the transfers
	 */
	protected CommunicationRouter router;

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

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public LatencyAbc(EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi, AbcType abcType) {
		super(dag, archi, abcType);

		// The media simulator calculates the edges costs
		router = new CommunicationRouter(archi);
		precedenceEdgeAdder = new PrecedenceEdgeAdder(orderManager);

		this.timeKeeper = new GraphTimeKeeper(implementation);
		timeKeeper.resetTimings();
	}
	@Override
	public void setDAG(MapperDAG dag) {
		this.timeKeeper = new GraphTimeKeeper(implementation);
		timeKeeper.resetTimings();
		super.setDAG(dag);
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

		timeKeeper.setAsDirty(vertex);
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		// unimplanting a vertex resets the cost of the current vertex
		// and its edges

		vertex.getTimingVertexProperty().resetCost();

		resetCost(vertex.incomingEdges());
		resetCost(vertex.outgoingEdges());

		transactionManager.undoTransactions(vertex);

		timeKeeper.setAsDirty(vertex);
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
				/*|| (edge.getSource() != null && SpecialVertexManager
						.isJoin(edge.getSource()))*/) {
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
					edge.getTimingEdgeProperty().setCost(SpecialVertexManager.dissuasiveCost);
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
	public final IImplementationPlotter plotImplementation(boolean delegateDisplay) {

		if (!delegateDisplay) {
			updateTimings();
			GanttPlotter.plot(implementation, this);
			return null;
		} else {
			updateTimings();
			return new GanttPlotter("Solution gantt", implementation, this);
		}
	}
}
