/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.Comparator;

import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Compares two SDF vertices using their scheduling orders
 * 
 * @author mpelcat
 */
public class SchedulingOrderComparator implements
Comparator<DAGVertex> {

	/**
	 * @Override compare
	 */
	@Override
	public int compare(DAGVertex v1, DAGVertex v2) {

		int difference = 0;

		difference = (Integer) v1.getPropertyBean().getValue(
				"schedulingOrder");

		difference -= (Integer) v2.getPropertyBean().getValue(
				"schedulingOrder");

		return difference;
	}

}
