/**
 * 
 */
package org.ietr.preesm.plugin.abc.taskscheduling;

import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Scheduling the tasks when an algorithm maps a vertex
 * 
 * @author mpelcat
 */
public abstract class AbstractTaskSched {

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	protected SchedOrderManager orderManager = null;

	public AbstractTaskSched(SchedOrderManager orderManager) {
		super();
		this.orderManager = orderManager;
	}
	
	public abstract void insertVertex(MapperDAGVertex vertex);
	
	public static AbstractTaskSched getInstance(TaskSchedType type, SchedOrderManager orderManager){
		if(type.equals(TaskSchedType.Simple)){
			return new SimpleTaskSched(orderManager);
		}
		else if(type.equals(TaskSchedType.Switcher)){
			return new TaskSwitcher(orderManager);
		}
		else if(type.equals(TaskSchedType.Topological)){
			return new TopologicalTaskSched(orderManager);
		}
		
		return null;
	}
}
