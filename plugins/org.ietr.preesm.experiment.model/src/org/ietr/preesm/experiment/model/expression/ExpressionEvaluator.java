package org.ietr.preesm.experiment.model.expression;

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
   *
   */
  public static final String evaluate(final Expression expression) {
    final String allExpression = expression.getString();

    Parameterizable parameterizableObj = lookUpParameters(expression);

    try {
      final JEP jep = new JEP();
      addInputParameterValues(parameterizableObj, jep);
      final long round = parse(allExpression, jep);

      return Long.toString(round);
    } catch (final ParseException e) {
      throw new RuntimeException("Parsing Error, check expression syntax" + " : " + allExpression, e);
    } catch (final NumberFormatException e) {
      throw new RuntimeException("Evaluation Error, check parameter dependencies" + " : " + allExpression, e);
    }
  }

  private static long parse(final String allExpression, final JEP jep) throws ParseException {
    final Node parse = jep.parse(allExpression);
    final Object result = jep.evaluate(parse);
    if (!(result instanceof Double)) {
      throw new UnsupportedOperationException("Unsupported result type " + result.getClass().getSimpleName());
    }
    Double dResult = (Double) result;
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

  private static void addInputParameterValues(Parameterizable parameterizableObj, final JEP jep) {
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

        jep.addVariable(parameterName, Double.parseDouble(evaluatedParam));
      }
    }
  }
}
