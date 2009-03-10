/**
 * 
 */
package org.ietr.preesm.plugin.abc.taskscheduling;

import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Simple task scheduler that schedules the tasks in the order given by the mapping algorithm
 * 
 * @author mpelcat
 */
public class SimpleTaskSched extends AbstractTaskSched {

	public SimpleTaskSched(SchedOrderManager orderManager) {
		super(orderManager);
	}

	@Override
	public void insertVertex(MapperDAGVertex vertex) {
		orderManager.addLast(vertex);
	}

}
