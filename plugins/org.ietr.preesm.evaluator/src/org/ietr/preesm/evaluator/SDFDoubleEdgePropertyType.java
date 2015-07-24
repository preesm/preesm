//TODO put in : package org.ietr.dftools.algorithm.model.sdf.types;

package org.ietr.preesm.evaluator;

import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;

public class SDFDoubleEdgePropertyType extends AbstractEdgePropertyType<Double>  {

	/**
	 * Creates a new SDFDefaultEdgePropertyType with the given double value
	 * 
	 * @param val
	 *            The Long value of this SDFDefaultEdgePropertyType
	 */
	public SDFDoubleEdgePropertyType(double val) {
		super(val);
	}

	/**
	 * Creates a new SDFDefaultEdgePropertyType with the given String value
	 * 
	 * @param val
	 *            The String value of this SDFDefaultEdgePropertyType
	 */
	public SDFDoubleEdgePropertyType(String val) {
		super(new Double(val));
	}

	@Override
	public AbstractEdgePropertyType<Double> clone() {
		return new SDFDoubleEdgePropertyType(value);
	}

	@Override
	public int intValue(){
		return value.intValue();
	}

	@Override
	public String toString() {
		return value.toString();
	}
	
	@Override
	public Double getValue() {
		return value;
	}
	
}
