package org.ietr.preesm.core.expression;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class CeilFunction extends PostfixMathCommand {

	public CeilFunction() {
		numberOfParameters = -1;
	}

	public Double ceil(Number d1) {
		return Math.ceil(d1.doubleValue());
	}

	/**
	 * Adds two numbers together. The parameters can be of type Number, Complex,
	 * or String. If a certain combination of types is not supported, a
	 * ParseException is thrown.
	 * 
	 * @param param1
	 *            The first parameter to be added.
	 * @param param2
	 *            The second parameter to be added.
	 * @return The sum of param1 and param2, or concatenation of the two if they
	 *         are Strings.
	 * @throws ParseException
	 */
	public Object ceil(Object param1) throws ParseException {
		if (param1 instanceof Number) {
			return ceil((Number) param1);
		} else if ((param1 instanceof String)) {
			return "%" + (String) param1;
		}

		throw new ParseException("Invalid parameter type");
	}

	/**
	 * Calculates the result of applying the "+" operator to the arguments from
	 * the stack and pushes it back on the stack.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run(Stack stack) throws ParseException {
		checkStack(stack);// check the stack

		Object ceil;
		Object param1;
		// get the parameter from the stack
		param1 = stack.pop();

		// add it to the sum (order is important for String arguments)
		ceil = ceil(param1);

		stack.push(ceil);

		return;
	}

}
