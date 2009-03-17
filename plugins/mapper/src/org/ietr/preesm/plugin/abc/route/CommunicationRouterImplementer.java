/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Routes a communication and creates the necessary communication vertices
 * 
 * @author mpelcat
 */
public abstract class CommunicationRouterImplementer {

	public abstract void route(MapperDAGVertex source, MapperDAGVertex target);
	public abstract void unroute(MapperDAGVertex source, MapperDAGVertex target);
}
