/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model.implementation;

import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * A transfer vertex represents a route step
 * 
 * @author mpelcat
 */
public class TransferVertex extends MapperDAGVertex {

	private RouteStep step;

	public TransferVertex(String id, MapperDAG base) {
		super(id, base);
		// TODO Auto-generated constructor stub
	}

	public RouteStep getRouteStep() {
		return step;
	}

	public void setRouteStep(RouteStep step) {
		this.step = step;
	}

}
