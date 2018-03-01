package org.ietr.preesm.experiment.model.expression.functions;

import org.nfunk.jep.ParseException;

/**
 * Computes a geometric sum. It takes three parameters: the first term a, the ratio r, and the number of iteration (including the first term). Ex:
 * geo_sum(3,1/2,4) = 3 + 3/2 + 3/4 + 3/8
 * <p>
 * It uses an iterative implementation (when |r| &lt 1) instead of the direct formula. The point is to avoid floating point approximations.
 * 
 * @author ahonorat
 */
public class GeometricSum extends AbstractPreesmMathFunction {

  @Override
  protected String getName() {
    return "geo_sum";
  }

  @Override
  protected int getArgCount() {
    return 3;
  }

  @Override
  protected double compute(double... args) throws ParseException {
    // the stack is in the reverse order
    double a = args[2];
    double r = args[1];
    int i = (int) args[0];
    if (i < 1) {
      throw new ParseException("Third argument of geo_sum must be a strictly positive integer.");
    }
    if (r == -1) {
      if (i % 2 == 0) {
        return 0;
      }
      return a;
    } else if (r == 1) {
      return a * i;
    } else if (r == 0) {
      return 0;
    } else if (r > 1 || r < -1) {
      return (a - a * Math.pow(r, i)) / (1 - r);
    }
    double sum = a;
    double reduceda = a * r;
    for (; i > 1; i--, reduceda *= r) {
      sum += reduceda;
    }
    return sum;
  }

}
