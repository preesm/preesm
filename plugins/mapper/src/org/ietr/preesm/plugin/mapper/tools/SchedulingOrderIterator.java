/**
 * 
 */
package org.ietr.preesm.plugin.mapper.tools;

import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * Iterates an implementation in the rank order
 * 
 * @author mpelcat
 */
public class SchedulingOrderIterator extends ImplantationIterator {

	public SchedulingOrderIterator(MapperDAG implementation,
			IAbc simulator, boolean directOrder) {
		super(implementation, simulator, directOrder);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ietr.preesm.plugin.mapper.tools.ImplantationIterator#compare(org.
	 * ietr.preesm.plugin.mapper.model.MapperDAGVertex,
	 * org.ietr.preesm.plugin.mapper.model.MapperDAGVertex)
	 */
	@Override
	public int compare(MapperDAGVertex arg0, MapperDAGVertex arg1) {
		return (simulator.getSchedulingTotalOrder(arg0) - simulator
				.getSchedulingTotalOrder(arg1));
	}

}
