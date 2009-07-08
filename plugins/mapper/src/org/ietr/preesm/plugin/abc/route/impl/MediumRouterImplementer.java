/**
 * 
 */
package org.ietr.preesm.plugin.abc.route.impl;

import java.util.List;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.route.AbstractCommunicationRouter;
import org.ietr.preesm.plugin.abc.route.CommunicationRouter;
import org.ietr.preesm.plugin.abc.route.CommunicationRouterImplementer;
import org.ietr.preesm.plugin.abc.transaction.AddOverheadVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.AddSendReceiveTransaction;
import org.ietr.preesm.plugin.abc.transaction.AddTransferVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;

/**
 * Class responsible to generate the suited vertices while simulating a medium
 * communication
 * 
 * @author mpelcat
 */
public class MediumRouterImplementer extends CommunicationRouterImplementer {

	public MediumRouterImplementer(AbstractCommunicationRouter user) {
		super(user);
	}

	/**
	 * Adds the simulation vertices
	 */
	@Override
	public Transaction addVertices(AbstractRouteStep routeStep,
			MapperDAGEdge edge, TransactionManager transactions, int type,
			int routeStepIndex, Transaction lastTransaction,
			List<Object> alreadyCreatedVertices) {

		if (routeStep instanceof MediumRouteStep) {
			MediumRouteStep mediumRouteStep = ((MediumRouteStep) routeStep);
			Medium medium = mediumRouteStep.getMedium();
			MediumDefinition mediumDef = ((MediumDefinition) medium
					.getDefinition());
			if (type == CommunicationRouter.transferType) {

				long transferTime = mediumDef.getTransferTime(edge
						.getInitialEdgeProperty().getDataSize());

				if (transferTime > 0) {
					int nodeIndex = 0;// a medium route step has only one node with index 0
					Transaction transaction = new AddTransferVertexTransaction(
							lastTransaction, getEdgeScheduler(), edge,
							getImplementation(), getOrderManager(),
							routeStepIndex, nodeIndex, routeStep, transferTime, medium,
							true);
					transactions.add(transaction);
					return transaction;
				} else {
					PreesmLogger.getLogger().log(
							Level.INFO,
							"A transfer vertex must have a strictly positive size: "
									+ edge);
					return null;
				}
			} else if (type == CommunicationRouter.overheadType) {
				MapperDAGEdge incomingEdge = null;

				for (Object o : alreadyCreatedVertices) {
					if (o instanceof TransferVertex) {
						TransferVertex v = (TransferVertex) o;
						if (v.getSource().equals(edge.getSource())
								&& v.getTarget().equals(edge.getTarget())
								&& v.getRouteStep() == routeStep)
							incomingEdge = (MapperDAGEdge) v.incomingEdges()
									.toArray()[0];

					}
				}

				long overheadTime = mediumDef.getOverheadTime();
				if (incomingEdge != null) {
					transactions.add(new AddOverheadVertexTransaction(
							incomingEdge, getImplementation(), routeStep,
							overheadTime, getOrderManager()));
				} else {
					PreesmLogger
							.getLogger()
							.log(
									Level.FINE,
									"The transfer following vertex"
											+ edge.getSource()
											+ "was not found. We could not add overhead.");
				}
			} else if (type == CommunicationRouter.sendReceiveType) {

				Transaction transaction = new AddSendReceiveTransaction(
						lastTransaction, edge, getImplementation(),
						getOrderManager(), routeStepIndex, routeStep,
						TransferVertex.SEND_RECEIVE_COST);

				transactions.add(transaction);
				return transaction;
			}
		}
		return null;
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {

	}

}
