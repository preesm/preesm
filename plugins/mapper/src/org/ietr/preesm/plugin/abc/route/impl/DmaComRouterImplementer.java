package org.ietr.preesm.plugin.abc.route.impl;

import java.util.List;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.simplemodel.AbstractNode;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNode;
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
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;

/**
 * Class responsible to generate the suited vertices while simulating a dma
 * communication
 * 
 * @author mpelcat
 */
public class DmaComRouterImplementer extends CommunicationRouterImplementer {

	public DmaComRouterImplementer(AbstractCommunicationRouter user) {
		super(user);
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {

	}

	/**
	 * Adds the simulation vertices
	 */
	@Override
	public Transaction addVertices(AbstractRouteStep routeStep,
			MapperDAGEdge edge, TransactionManager transactions, int type,
			int routeStepIndex, Transaction lastTransaction, List<Object> alreadyCreatedVertices) {

		if (routeStep instanceof DmaRouteStep) {
			if (type == CommunicationRouter.transferType) {
				DmaRouteStep dmaStep = ((DmaRouteStep)routeStep);
				
				Transaction transaction = lastTransaction;

				for (AbstractNode node : dmaStep.getNodes()) {
					if (node instanceof ContentionNode) {
						long transferTime = 100;
						transaction = new AddTransferVertexTransaction(
								lastTransaction, getEdgeScheduler(), edge,
								getImplementation(), getOrderManager(),
								routeStepIndex, routeStep, transferTime, node
								, true);

						transactions.add(transaction);
					}
				}

				return transaction;
			} else if (type == CommunicationRouter.overheadType) {

			} else if (type == CommunicationRouter.sendReceive) {

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

}
