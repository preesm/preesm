package org.ietr.preesm.core.expression;

import org.nfunk.jep.Variable;

public class Parameter extends Variable {

	public int sdxIndex;

	public Parameter(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public Parameter(String name, Object value) {
		super(name, value);
		// TODO Auto-generated constructor stub
	}

	public Parameter clone() {
		Parameter newParam = new Parameter(this.name);
		newParam.setValue(this.getValue());
		newParam.setSdxIndex(this.getSdxIndex());
		return newParam;
	}

	public int getSdxIndex() {
		return sdxIndex;
	}

	public Object getValue() {
		return super.getValue();
	}

	public void setSdxIndex(int index) {
		sdxIndex = index;
	}

	public boolean setValue(Object value) {
		return super.setValue(value);
	}

}
