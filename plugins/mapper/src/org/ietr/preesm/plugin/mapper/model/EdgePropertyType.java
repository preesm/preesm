package org.ietr.preesm.plugin.mapper.model;

import org.sdf4j.model.AbstractEdgePropertyType;

/**
 * Extending propertyType
 * 
 * @author mpelcat
 */
public class EdgePropertyType extends AbstractEdgePropertyType<Integer> {

	int time;

	public EdgePropertyType(int time) {
		super();
		this.time = time;

	}

	@Override
	public AbstractEdgePropertyType<Integer> clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return time;
	}

	@Override
	public String toString() {
		return String.format("%d",time) ;
	}

}
