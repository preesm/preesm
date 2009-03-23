package org.ietr.preesm.plugin.abc.route;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.NodeRouteStep;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

public class MessageComRouterImplementer extends CommunicationRouterImplementer {

	public MessageComRouterImplementer(MapperDAG implementation,
			AbstractEdgeSched edgeScheduler, SchedOrderManager orderManager) {
		super(implementation, edgeScheduler, orderManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addVertices(AbstractRouteStep routeStep, MapperDAGEdge edge,
			TransactionManager transactions, String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {
		// TODO Auto-generated method stub
		
	}

}
