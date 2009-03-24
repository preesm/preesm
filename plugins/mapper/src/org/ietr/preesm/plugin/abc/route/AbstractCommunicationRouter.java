/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Routes a communication and creates the necessary communication vertices
 * 
 * @author mpelcat
 */
public abstract class AbstractCommunicationRouter {

	private Map<String,CommunicationRouterImplementer> implementers;

	public AbstractCommunicationRouter() {
		super();
		this.implementers = new HashMap<String, CommunicationRouterImplementer>();
	}
	
	protected void addImplementer(String name,CommunicationRouterImplementer implementer){
		implementers.put(name,implementer);
	}
	
	protected CommunicationRouterImplementer getImplementer(String name){
		return implementers.get(name);
	}
	
	public abstract void routeNewVertex(MapperDAGVertex newVertex);
	public abstract long evaluateTransfer(MapperDAGEdge edge);
}
