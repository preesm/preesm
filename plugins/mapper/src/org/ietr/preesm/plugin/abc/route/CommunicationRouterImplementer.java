/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Routes a communication and creates the necessary communication vertices
 * 
 * @author mpelcat
 */
public abstract class CommunicationRouterImplementer {

	protected MapperDAG implementation = null;
	protected IEdgeSched edgeScheduler = null;
	protected SchedOrderManager orderManager = null;
	
	public CommunicationRouterImplementer(MapperDAG implementation, IEdgeSched edgeScheduler, SchedOrderManager orderManager) {
		super();
		this.implementation = implementation;
		this.edgeScheduler = edgeScheduler;
		this.orderManager = orderManager;
	}
	
	public abstract Transaction addVertices(AbstractRouteStep routeStep, MapperDAGEdge edge, TransactionManager transactions, int type, int routeStepIndex, Transaction lastTransaction);
	public abstract void removeVertices(MapperDAGEdge edge, TransactionManager transactions);
	protected abstract long evaluateSingleTransfer(MapperDAGEdge edge, AbstractRouteStep step);
}
