package org.ietr.preesm.plugin.abc.route;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;

public class MessageComRouterImplementer extends CommunicationRouterImplementer {

	public MessageComRouterImplementer(AbstractCommunicationRouter user) {
		super(user);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Transaction addVertices(AbstractRouteStep routeStep,
			MapperDAGEdge edge, TransactionManager transactions, int type,
			int routeStepIndex, Transaction lastTransaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long evaluateSingleTransfer(MapperDAGEdge edge,
			AbstractRouteStep step) {
		// TODO Auto-generated method stub
		return 0;
	}

}
