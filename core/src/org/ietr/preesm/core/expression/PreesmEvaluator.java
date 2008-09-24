package org.ietr.preesm.core.expression;

import java.util.Stack;

import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTStart;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

public class PreesmEvaluator implements EvaluatorI {

	@Override
	public Double eval(Node arg0) throws ParseException {
		if (arg0 instanceof ASTVarNode) {
			ASTVarNode varNode = (ASTVarNode) arg0;
			PreesmEvaluator evaluator = new PreesmEvaluator();
			if (varNode.getVar().getValue() instanceof Node) {
				return evaluator.eval((Node) varNode.getVar().getValue());
			} else if (varNode.getVar().getValue() instanceof Parameter) {
				Parameter param = (Parameter) varNode.getVar().getValue();
				while (param.getValue() != null
						&& param.getValue() instanceof Parameter) {
					param = (Parameter) param.getValue();
				}
				if (param == null) {
					throw new ParseException();
				}
				return evaluator.eval((Node) param.getValue());
			} else {
				throw new ParseException();
			}
		} else if (arg0 instanceof ASTFunNode) {
			ASTFunNode funNode = (ASTFunNode) arg0;
			if (funNode.getPFMC().checkNumberOfParameters(
					funNode.jjtGetNumChildren())) {
				Stack<Double> operand = new Stack<Double>();
				for (int i = 0; i < funNode.jjtGetNumChildren(); i++) {
					PreesmEvaluator evaluator = new PreesmEvaluator();
					operand.add(evaluator.eval(funNode.jjtGetChild(i)));
				}
				PostfixMathCommandI command = funNode.getPFMC();
				command.run(operand);
				return operand.firstElement();
			} else {
				throw new ParseException();
			}
		} else if (arg0 instanceof ASTConstant) {
			return (Double) ((ASTConstant) arg0).getValue();
		} else if (arg0 instanceof ASTStart) {
			return null;
		} else {
			return null;
		}
	}

}
