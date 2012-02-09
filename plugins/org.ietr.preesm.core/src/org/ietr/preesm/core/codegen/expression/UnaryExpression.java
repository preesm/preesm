package org.ietr.preesm.core.codegen.expression;

public class UnaryExpression implements IExpression {
	private String operator;
	public IExpression op1;

	public UnaryExpression(String operator, IExpression op1) {
		this.op1 = op1;
		this.operator = operator;
	}

	public String toString() {
		return "(" + operator + op1.toString() + ")";
	}

}
