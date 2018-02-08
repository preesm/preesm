package org.ietr.preesm.experiment.model.expression.functions;

import java.util.Stack;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Abstract class to wrap simple postfix math functions for JEP.
 */
public abstract class AbstractPreesmMathFunction extends PostfixMathCommand {

  /**
   * Properly initialize number of argument with abstract method
   */
  public AbstractPreesmMathFunction() {
    super();
    this.numberOfParameters = getArgCount();
  }

  public final void integrateWithin(final JEP jep) {
    jep.addFunction(this.getName(), this);
  }

  /**
   * Gets the {@link #getArgCount()} peek elements of the stack and put them in order in an array of double, then call the {@link #compute(double...)} method to
   * compute the result.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void run(Stack stack) throws ParseException {

    final double[] args = new double[getArgCount()];
    for (int i = 0; i < getArgCount(); i++) {
      Object arg = stack.pop();
      if (!(arg instanceof Double)) {
        throw new ParseException("Argument must be a number, whereas it is " + arg + "(" + arg.getClass().getName() + ")");
      }
      args[i] = (double) arg;
    }
    final double result = compute(args);
    stack.push(result);
  }

  /**
   * returns the name of the function that will be used in the math expressions
   */
  protected abstract String getName();

  protected abstract int getArgCount();

  protected abstract double compute(double... args);

}
