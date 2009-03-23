/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
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
	protected AbstractEdgeSched edgeScheduler = null;
	protected SchedOrderManager orderManager = null;
	
	public CommunicationRouterImplementer(MapperDAG implementation, AbstractEdgeSched edgeScheduler, SchedOrderManager orderManager) {
		super();
		this.implementation = implementation;
		this.edgeScheduler = edgeScheduler;
		this.orderManager = orderManager;
	}
	
	public abstract void addVertices(AbstractRouteStep routeStep, MapperDAGEdge edge, TransactionManager transactions, String type);
	public abstract void removeVertices(MapperDAGEdge edge, TransactionManager transactions);
}
