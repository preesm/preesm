package org.ietr.preesm.plugin.abc.looselytimed;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.CommunicationRouter;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.PrecedenceEdgeAdder;

/**
 * A loosely timed architecture simulator associates a simple cost to
 * each communication. This cost is the transfer size multiplied by the
 * medium speed. The communications are parallel with computation and
 * all parallel with each other.
 *         
 * @author mpelcat   
 */
public class LooselyTimedAbc extends
		AbstractAbc {

	/**
	 * simulator of the transfers
	 */
	protected CommunicationRouter router;

	/**
	 * Current precedence edge adder: called exclusively by simulator to schedule
	 * vertices on the different operators
	 */
	protected PrecedenceEdgeAdder precedenceEdgeAdder;
	
	/**
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public LooselyTimedAbc(MapperDAG dag, IArchitecture archi) {
		super(dag, archi);

		// The media simulator calculates the edges costs
		router = new CommunicationRouter(archi);
		precedenceEdgeAdder = new PrecedenceEdgeAdder(orderManager);
	}

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

			// precedenceEdgeAdder.deleteScheduleIncomingEdges(implementation,
			// vertex);
			transactionManager.undoTransactionList();

			precedenceEdgeAdder.addPrecedenceEdges(implementation,transactionManager);
			// precedenceEdgeAdder.addScheduleIncomingEdge(implementation, vertex,
			// this);

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

			// Only T level necessary. No update of B Level
			timekeeper.updateTLevels(this.implementation);
			dirtyVertices.clear();

			dirtyTimings = false;
		}
	}

	/**
	 * In the loosely timed ABC, the edges receive the communication times.
	 */
	protected final void setEdgeCost(MapperDAGEdge edge) {

		ImplementationVertexProperty sourceimp = ((MapperDAGVertex)edge.getSource())
				.getImplementationVertexProperty();
		ImplementationVertexProperty destimp = ((MapperDAGVertex)edge.getTarget())
				.getImplementationVertexProperty();

		Operator sourceOp = sourceimp.getEffectiveOperator();
		Operator destOp = destimp.getEffectiveOperator();

		if (sourceOp != Operator.NO_COMPONENT
				&& destOp != Operator.NO_COMPONENT) {
			if (sourceOp.equals(destOp)) {
				edge.getTimingEdgeProperty().setCost(0);
			} else {

				// The transfer evaluation takes into account the route

				edge.getTimingEdgeProperty().setCost(
						router.evaluateTransfer(edge, sourceOp, destOp));
			}
		}

	}

}
