/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddTransferVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * @author mpelcat
 * 
 */
public class MediumRouterImplementer extends CommunicationRouterImplementer {

	public MediumRouterImplementer(MapperDAG implementation,
			AbstractEdgeSched edgeScheduler, SchedOrderManager orderManager) {
		super(implementation, edgeScheduler, orderManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addVertices(AbstractRouteStep routeStep, MapperDAGEdge edge,
			TransactionManager transactions, String type) {

		if (type.equals("transfer")) {
			long transferCost = 1000;
			MediumRouteStep mediumRouteStep = (MediumRouteStep) routeStep;

			Transaction precedingTransaction = transactions.getLast();
			Transaction transaction = new AddTransferVertexTransaction(
					precedingTransaction, edgeScheduler, edge, implementation,
					orderManager, 0, mediumRouteStep, transferCost, true);

			transactions.add(transaction);
		}
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {

	}

}
