/**
 * 
 */
package org.ietr.preesm.plugin.abc.accuratelytimed;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.CommunicationRouter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.OverheadVertexAdder;
import org.ietr.preesm.plugin.mapper.model.implementation.TransferVertexAdder;

/**
 * The accurately timed ABC schedules edges and set-up times
 * 
 * @author mpelcat
 */
public class AccuratelyTimedAbc extends
		AbstractAbc {

	/**
	 * simulator of the transfers
	 */
	protected CommunicationRouter router;

	/**
	 * Transfer vertex adder for edge scheduling
	 */
	protected TransferVertexAdder tvertexAdder;

	/**
	 * Overhead vertex adder for edge scheduling
	 */
	protected OverheadVertexAdder overtexAdder;

	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public AccuratelyTimedAbc(MapperDAG dag,
			IArchitecture archi) {
		super(dag, archi);

		// The media simulator calculates the edges costs
		router = new CommunicationRouter(archi);

		tvertexAdder = new TransferVertexAdder(router, orderManager, false);

		overtexAdder = new OverheadVertexAdder(orderManager);
	}

	/**
	 * Called when a new vertex operator is set
	 */
	@Override
	protected void fireNewMappedVertex(MapperDAGVertex vertex) {

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		if (effectiveOp == Operator.NO_COMPONENT) {
			PreesmLogger.getLogger().severe(
					"implementation of " + vertex.getName() + " failed");
		} else {
			int vertextime = vertex.getInitialVertexProperty().getTime(
					effectiveOp);

			// Set costs
			vertex.getTimingVertexProperty().setCost(vertextime);

			setEdgesCosts(vertex.incomingEdges());
			setEdgesCosts(vertex.outgoingEdges());

			precedenceEdgeAdder.deletePrecedenceEdges(implementation);
			transactionManager.undoTransactionList();
			tvertexAdder.addTransferVertices(implementation,transactionManager);
			overtexAdder.addOverheadVertices(implementation,transactionManager);
			precedenceEdgeAdder.addPrecedenceEdges(implementation);

		}
	}

	@Override
	protected void fireNewUnmappedVertex(MapperDAGVertex vertex) {

		Operator effectiveOp = vertex.getImplementationVertexProperty()
				.getEffectiveOperator();

		// unimplanting a vertex resets the cost of the current vertex
		// and its edges
		// It also removes incoming and outgoing schedule edges
		if (effectiveOp == Operator.NO_COMPONENT) {
			vertex.getTimingVertexProperty().resetCost();

			resetCost(vertex.incomingEdges());
			resetCost(vertex.outgoingEdges());

		} else {
			PreesmLogger.getLogger().severe(
					"unimplementation of " + vertex.getName() + " failed");
		}
	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 * Depending on the king of timings we want, calls the necessary updates.
	 */
	@Override
	protected final void updateTimings() {

		if (dirtyTimings) {

			timekeeper.updateTLevels(this.implementation);
			dirtyVertices.clear();

			dirtyTimings = false;
		}
	}

	/**
	 * Edge scheduling vertices are added. Thus useless edge costs are removed
	 */
	protected final void setEdgeCost(MapperDAGEdge edge) {

		edge.getTimingEdgeProperty().setCost(0);

	}
}
