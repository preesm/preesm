package org.ietr.preesm.codegen.model.expression;

public class StringExpression implements IExpression {
	private String expr;

	public StringExpression(String expr) {
		this.expr = expr;
	}

	public String toString() {
		return "(" + expr + ")";
	}
}
