/**
 * 
 */
package org.ietr.preesm.plugin.abc.impl;

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
	 * Constructor of the simulator from a "blank" implementation where every
	 * vertex has not been implanted yet.
	 */
	public LatencyAbc(EdgeSchedType edgeSchedType, MapperDAG dag,
			MultiCoreArchitecture archi, AbcType abcType) {
		super(dag, archi, abcType);

		// The media simulator calculates the edges costs
		router = new CommunicationRouter(archi);
		precedenceEdgeAdder = new PrecedenceEdgeAdder(orderManager);
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

		transactionManager.undoTransactions(vertex);
	}

	/**
	 * Asks the time keeper to update timings. Crucial and costly operation.
	 * Depending on the king of timings we want, calls the necessary updates.
	 */
	@Override
	protected final void updateTimings() {

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

}
