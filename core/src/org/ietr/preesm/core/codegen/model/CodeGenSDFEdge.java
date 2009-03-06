package org.ietr.preesm.core.codegen.model;

import org.sdf4j.model.AbstractEdgePropertyType;
import org.sdf4j.model.sdf.SDFEdge;

public class CodeGenSDFEdge extends SDFEdge {

	public CodeGenSDFEdge(AbstractEdgePropertyType<?> prod,
			AbstractEdgePropertyType<?> cons,
			AbstractEdgePropertyType<?> delay,
			AbstractEdgePropertyType<?> dataType) {
		super(prod, cons, delay, dataType);
	}

	public int getSize() {
		if (this.getSource() != this.getTarget()) {
			return Math.max(this.getSource().getNbRepeat()
					* getProd().intValue(), this.getTarget().getNbRepeat()
					* getCons().intValue());
		} else {
			return getProd().intValue();
		}
	}

	public String toString() {
		return getDataType().toString() + " " + getSource().getName() + "_"
				+ getSourceInterface().getName() + " [" + getProd().intValue()
				+ "];\n";
	}
}
