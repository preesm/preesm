package org.ietr.preesm.experiment.model.expression.functions;

import java.util.Stack;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 *
 */
public abstract class AbstractPreesmMathFunction extends PostfixMathCommand {

  public AbstractPreesmMathFunction() {
    super();
    this.numberOfParameters = getArgCount();
  }

  public final void integrateWithin(final JEP jep) {
    jep.addFunction(this.getName(), this);
  }

  /**
   * Calculates the result of applying the floor function to the top of the stack and pushes it back on the stack.
   *
   * @param stack
   *          the stack
   * @throws ParseException
   *           the parse exception
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

  protected abstract String getName();

  protected abstract int getArgCount();

  protected abstract double compute(double... args);

}
