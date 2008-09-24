package org.ietr.preesm.core.expression;

import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;

public class ExprPrinterVisitor {

	public ExprPrinterVisitor() {
	}

	public String toString(Node n) {
		return visit(n);
	}

	public String visit(ASTConstant node) {
		return node.getValue().toString();
	}

	public String visit(ASTFunNode node) {
		String expr = new String();
		if (node.isOperator() && node.jjtGetNumChildren() == 2) {
			expr += "(";
			expr += visit(node.jjtGetChild(0));
			expr += node.getOperator().getName();
			expr += visit(node.jjtGetChild(1));
			expr += ")";
		} else {
			expr += node.getName();
			expr += "(";
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				expr += visit(node.jjtGetChild(i));
				if (i < node.jjtGetNumChildren()) {
					expr += ",";
				}
			}
			expr += ")";
		}
		return expr;
	}

	public String visit(ASTVarNode node) {
		return node.getName();
	}

	public String visit(Node n) {
		if (n instanceof ASTVarNode) {
			return visit((ASTVarNode) n);
		} else if (n instanceof ASTFunNode) {
			return visit((ASTFunNode) n);
		} else if (n instanceof ASTConstant) {
			return visit((ASTConstant) n);
		}
		return null;
	}
}
