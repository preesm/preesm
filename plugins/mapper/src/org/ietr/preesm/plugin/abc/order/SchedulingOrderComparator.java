/**
 * 
 */
package org.ietr.preesm.plugin.abc.order;

import java.util.Comparator;

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Compares two SDF vertices using their scheduling orders
 * 
 * @author mpelcat
 */
public class SchedulingOrderComparator implements
Comparator<MapperDAGVertex> {

	/**
	 * @Override compare
	 */
	@Override
	public int compare(MapperDAGVertex v1, MapperDAGVertex v2) {

		int difference = 0;

		difference = (Integer) v1.getImplementationVertexProperty().getSchedulingTotalOrder();

		difference -= (Integer) v2.getImplementationVertexProperty().getSchedulingTotalOrder();

		return difference;
	}

}
