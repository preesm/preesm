package org.preesm.commons.math;

import java.util.Map;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.preesm.commons.math.functions.CeilFunction;
import org.preesm.commons.math.functions.FloorFunction;
import org.preesm.commons.math.functions.GeometricSum;
import org.preesm.commons.math.functions.MaxFunction;
import org.preesm.commons.math.functions.MaxPowerDivisibility;
import org.preesm.commons.math.functions.MinFunction;

/**
 *
 */
public class JEPWrapper {

  private JEPWrapper() {
    // forbid instantiation
  }

  /**
   *
   */
  public static final long evaluate(final String expression,
      final Map<String, ? extends Number> addInputParameterValues) {
    final JEP jep = initJep(addInputParameterValues);
    long result;
    try {
      result = parse(expression, jep);
    } catch (final ParseException e) {
      final String msg = "Could not evaluate " + expression + ":\n" + e.getMessage();
      throw new ExpressionEvaluationException(msg, e);
    }
    return result;
  }

  private static JEP initJep(final Map<String, ? extends Number> addInputParameterValues) {
    final JEP jep = new JEP();

    if (addInputParameterValues != null) {
      addInputParameterValues.forEach(jep::addVariable);
    }

    jep.addStandardConstants();
    jep.addStandardFunctions();

    new FloorFunction().integrateWithin(jep);
    new CeilFunction().integrateWithin(jep);
    new MinFunction().integrateWithin(jep);
    new MaxFunction().integrateWithin(jep);
    new GeometricSum().integrateWithin(jep);
    new MaxPowerDivisibility().integrateWithin(jep);

    return jep;
  }

  private static long parse(final String allExpression, final JEP jep) throws ParseException {
    final Node parse = jep.parse(allExpression);
    final Object result = jep.evaluate(parse);
    if (result instanceof Long) {
      return (long) result;
    } else if (result instanceof Double) {
      final Double dResult = (Double) result;
      if (Double.isInfinite(dResult)) {
        throw new ExpressionEvaluationException("Expression '" + allExpression + "' evaluated to infinity.");
      }
      return Math.round(dResult);
    } else if (result instanceof Number) {
      return ((Number) result).longValue();
    } else {
      throw new UnsupportedOperationException("Unsupported result type " + result.getClass().getSimpleName());
    }
  }
}
