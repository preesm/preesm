/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddOverheadVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.AddTransferVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * @author mpelcat
 * 
 */
public class MediumRouterImplementer extends CommunicationRouterImplementer {

	public MediumRouterImplementer(MapperDAG implementation,
			IEdgeSched edgeScheduler, SchedOrderManager orderManager) {
		super(implementation, edgeScheduler, orderManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Transaction addVertices(AbstractRouteStep routeStep,
			MapperDAGEdge edge, TransactionManager transactions, int type,
			int routeStepIndex, Transaction lastTransaction) {

		if (type == CommunicationRouter.transferType) {
			long transferCost = 1000;
			MediumRouteStep mediumRouteStep = (MediumRouteStep) routeStep;

			Transaction transaction = new AddTransferVertexTransaction(
					lastTransaction, edgeScheduler, edge, implementation,
					orderManager, routeStepIndex, mediumRouteStep,
					transferCost, true);

			transactions.add(transaction);

			return transaction;
		} else if (type == CommunicationRouter.overheadType) {
			MapperDAGEdge firstTransferIncomingEdge = (MapperDAGEdge)getTransfer(
					(MapperDAGVertex) edge.getSource(), (MapperDAGVertex) edge
							.getTarget(), routeStepIndex).incomingEdges().toArray()[0];

			if (firstTransferIncomingEdge != null) {
				transactions.add(new AddOverheadVertexTransaction(
						firstTransferIncomingEdge, implementation, routeStep,
						orderManager));
			} else {
				PreesmLogger.getLogger().log(
						Level.SEVERE,
						"The transfer following vertex" + edge.getSource()
								+ "was not found. We could not add overhead.");
			}
		}
		return null;
	}

	private TransferVertex getTransfer(MapperDAGVertex source,
			MapperDAGVertex target, int routeStepIndex) {

		for (DAGEdge transferEdge : source.outgoingEdges()) {
			if (transferEdge.getTarget() instanceof TransferVertex) {
				TransferVertex v = (TransferVertex) transferEdge.getTarget();
				if (v.getTarget().equals(target)) {
					if (v.getRouteStepIndex() == routeStepIndex) {
						return v;
					} else {
						return getTransfer(v, target, routeStepIndex);
					}
				}
			}
		}
		return null;
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {

	}

}
