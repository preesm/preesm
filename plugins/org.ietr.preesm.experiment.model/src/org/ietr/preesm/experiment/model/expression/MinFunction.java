package org.ietr.preesm.experiment.model.expression;

import java.util.Stack;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Min function (for two double numbers)
 * 
 * @author ahonorat
 */
public class MinFunction extends PostfixMathCommand {

  /**
   * Instantiates a new ceil function.
   */
  public MinFunction() {
    super();
    numberOfParameters = 2;
  }

  /**
   * Evaluate the min expression for two parameters.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void run(Stack s) throws ParseException {
    Object aObj = s.pop();
    Object bObj = s.pop();
    if ((!(aObj instanceof Number)) || (!(bObj instanceof Number))) {
      throw new ParseException(
          "Min: both arguments must be doubles. They are " + bObj + "(" + bObj.getClass().getName() + ") and " + aObj + "(" + bObj.getClass().getName() + ")");
    }
    double aDouble = ((Number) aObj).doubleValue();
    double bDouble = ((Number) bObj).doubleValue();
    s.push(new Double(Math.min(aDouble, bDouble)));
  }

}
