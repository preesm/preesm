/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;

/**
 * Routes a communication and creates the necessary communication vertices
 * 
 * @author mpelcat
 */
public abstract class CommunicationRouterImplementer {
	
	private AbstractCommunicationRouter user = null;
	
	public CommunicationRouterImplementer(AbstractCommunicationRouter user) {
		super();
		this.user = user;
	}
	
	public MapperDAG getImplementation() {
		return user.getImplementation();
	}
	
	public IEdgeSched getEdgeScheduler() {
		return user.getEdgeScheduler();
	}
	
	public SchedOrderManager getOrderManager() {
		return user.getOrderManager();
	}

	public abstract Transaction addVertices(AbstractRouteStep routeStep, MapperDAGEdge edge, TransactionManager transactions, int type, int routeStepIndex, Transaction lastTransaction);
	public abstract void removeVertices(MapperDAGEdge edge, TransactionManager transactions);
	protected abstract long evaluateSingleTransfer(MapperDAGEdge edge, AbstractRouteStep step);
}
