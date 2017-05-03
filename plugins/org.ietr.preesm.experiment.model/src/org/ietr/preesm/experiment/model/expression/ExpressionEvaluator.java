package org.ietr.preesm.experiment.model.expression;

import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 *
 * @author anmorvan
 *
 */
public class ExpressionEvaluator {

  /**
   * Take an {@link Expression}, use its eContainers to lookup parameters and finally evaluates the expression to a long value representing the result
   *
   */
  public static final long evaluate(final Expression expression) {
    final Parameterizable parameterizableObj = ExpressionEvaluator.lookUpParameters(expression);
    final Map<String, Number> addInputParameterValues = ExpressionEvaluator.addInputParameterValues(parameterizableObj);
    return ExpressionEvaluator.evaluate(expression.getString(), addInputParameterValues);
  }

  public static final long evaluate(final String expression) {
    return ExpressionEvaluator.evaluate(expression, null);
  }

  /**
   */
  public static final long evaluate(final String expression, final Map<String, Number> addInputParameterValues) {
    final JEP jep = ExpressionEvaluator.initJep(addInputParameterValues);
    long result;
    try {
      result = ExpressionEvaluator.parse(expression, jep);
      return result;
    } catch (final ParseException e) {
      throw new RuntimeException("Parsing Error, check expression syntax" + " : " + expression, e);
    }
  }

  private static JEP initJep(final Map<String, Number> addInputParameterValues) {
    final JEP jep = new JEP();

    if (addInputParameterValues != null) {
      addInputParameterValues.forEach((name, value) -> {
        jep.addVariable(name, value);
      });
    }
    // jep.addStandardConstants();
    // jep.addStandardFunctions();
    return jep;
  }

  private static long parse(final String allExpression, final JEP jep) throws ParseException {
    final Node parse = jep.parse(allExpression);
    final Object result = jep.evaluate(parse);
    if (!(result instanceof Double)) {
      throw new UnsupportedOperationException("Unsupported result type " + result.getClass().getSimpleName());
    }
    final Double dResult = (Double) result;
    final long round = Math.round(dResult);
    return round;
  }

  private static Parameterizable lookUpParameters(final Expression expression) {
    Parameterizable parameterizableObj;
    if (expression.eContainer() instanceof Parameterizable) {
      parameterizableObj = (Parameterizable) expression.eContainer();
    } else if (expression.eContainer().eContainer() instanceof Parameterizable) {
      parameterizableObj = (Parameterizable) expression.eContainer().eContainer();
    } else {
      throw new RuntimeException("Neither a child of Parameterizable nor a child of a child of Parameterizable");
    }
    return parameterizableObj;
  }

  private static Map<String, Number> addInputParameterValues(final Parameterizable parameterizableObj) {
    final Map<String, Number> result = new LinkedHashMap<>();
    for (final ConfigInputPort port : parameterizableObj.getConfigInputPorts()) {
      if ((port.getIncomingDependency() != null) && (port.getIncomingDependency().getSetter() instanceof Parameter)) {
        final Parameter p = (Parameter) port.getIncomingDependency().getSetter();

        String parameterName;
        if ((parameterizableObj instanceof Parameter) || (parameterizableObj instanceof Delay) || (parameterizableObj instanceof InterfaceActor)) {
          parameterName = p.getName();
        } else {
          parameterName = port.getName();
        }

        final String evaluatedParam = p.getExpression().evaluate();
        final double parseDouble = Double.parseDouble(evaluatedParam);
        result.put(parameterName, parseDouble);
      }
    }
    return result;
  }
}
