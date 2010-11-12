package org.ietr.preesm.core.codegen.expression;

public class BinaryExpression implements IExpression {

	private String operator;
	public IExpression op1;
	public IExpression op2;

	public BinaryExpression(String operator, IExpression op1, IExpression op2) {
		this.op1 = op1;
		this.op2 = op2;
		this.operator = operator;
	}

	public String toString() {
		return "(" + op1.toString() + operator + op2.toString() + ")";
	}
}
