package org.preesm.commons.math.functions;

import org.nfunk.jep.ParseException;

/**
 * This function computes the maximum power of x still being a divisor of y. Example: pow_div_max(2, 72) = 3 since 72 =
 * 2^3 * 9 and 9 is not divisible by two.
 * 
 * @author ahonorat
 */
public class MaxPowerDivisibility extends AbstractPreesmMathFunction {

  @Override
  protected String getName() {
    return "pow_div_max";
  }

  @Override
  protected int getArgCount() {
    return 2;
  }

  @Override
  protected double compute(double... args) throws ParseException {
    // the stack is in the reverse order
    if (args[1] < 2) {
      throw new ParseException("First argument of pow_div_max must be an integer stricly greater than 1.");
    }
    long x = (long) args[1];
    long y = (long) args[0];
    if (y == 0) {
      throw new ParseException("Second argument of pow_div_max must not be 0.");
    }
    long power = 0;
    while (y % x == 0) {
      y /= x;
      power++;
    }
    return power;
  }

}
