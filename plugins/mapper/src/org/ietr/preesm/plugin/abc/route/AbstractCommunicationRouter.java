/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Routes a communication and creates the necessary communication vertices
 * 
 * @author mpelcat
 */
public abstract class AbstractCommunicationRouter {

	private Map<String, CommunicationRouterImplementer> implementers;

	protected MapperDAG implementation = null;
	protected IEdgeSched edgeScheduler = null;
	protected SchedOrderManager orderManager = null;

	public AbstractCommunicationRouter(MapperDAG implementation,
			IEdgeSched edgeScheduler, SchedOrderManager orderManager) {
		super();
		this.implementers = new HashMap<String, CommunicationRouterImplementer>();
		setManagers(implementation, edgeScheduler, orderManager);
	}

	protected void addImplementer(String name,
			CommunicationRouterImplementer implementer) {
		implementers.put(name, implementer);
	}

	protected CommunicationRouterImplementer getImplementer(String name) {
		return implementers.get(name);
	}

	public MapperDAG getImplementation() {
		return implementation;
	}

	public IEdgeSched getEdgeScheduler() {
		return edgeScheduler;
	}

	public SchedOrderManager getOrderManager() {
		return orderManager;
	}

	public void setManagers(MapperDAG implementation, IEdgeSched edgeScheduler,
			SchedOrderManager orderManager) {
		this.implementation = implementation;
		this.edgeScheduler = edgeScheduler;
		this.orderManager = orderManager;
	}

	public abstract void routeAll(MapperDAG implementation, Integer type);

	public abstract void routeNewVertex(MapperDAGVertex newVertex,
			List<Integer> types);

	public abstract long evaluateTransfer(MapperDAGEdge edge);
}
