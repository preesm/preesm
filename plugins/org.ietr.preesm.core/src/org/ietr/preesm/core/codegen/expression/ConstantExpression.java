package org.ietr.preesm.core.codegen.expression;

import org.ietr.preesm.core.codegen.types.DataType;

public class ConstantExpression extends VariableExpression {

	private Integer value;

	public ConstantExpression(String name, DataType type, int value) {
		super(name, type);
		this.value = value;
	}

	public ConstantExpression(int value) {
		super("", new DataType("long"));
		this.value = value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return Integer.toString(value);
	}
}
