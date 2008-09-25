package org.ietr.preesm.plugin.abc.approximatelytimed;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.CommunicationRouter;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.TransferVertexAdder;

/**
 * An approximately timed architecture simulator associates a complex
 * cost to each inter-core communication. This cost is composed of an
 * overhead on the sender, a transfer time on the medium and a reception
 * time on the receiver. Scheduling transfer vertices are added and
 * mapped to the media architecture components
 *         
 * @author mpelcat   
 */
public class ApproximatelyTimedAbc extends
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
	 * Constructor of the simulator from a "blank" implantation where every
	 * vertex has not been implanted yet.
	 */
	public ApproximatelyTimedAbc(MapperDAG dag,
			IArchitecture archi) {
		super(dag, archi);

		// The media simulator calculates the edges costs
		router = new CommunicationRouter(archi);

		tvertexAdder = new TransferVertexAdder(router, orderManager, false);
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
