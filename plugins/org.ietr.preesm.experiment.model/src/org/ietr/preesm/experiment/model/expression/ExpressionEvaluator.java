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
    final JEP jep = new JEP();

    Parameterizable parameterizableObj;
    if (expression.eContainer() instanceof Parameterizable) {
      parameterizableObj = (Parameterizable) expression.eContainer();
    } else if (expression.eContainer().eContainer() instanceof Parameterizable) {
      parameterizableObj = (Parameterizable) expression.eContainer().eContainer();
    } else {
      return "Neither a child of Parameterizable nor a child of a child of Parameterizable";
    }

    try {
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

      final Node parse = jep.parse(allExpression);

      final Object result = jep.evaluate(parse);
      String evaluation;

      /*
       * Display an Integer as it: 1 instead of 1.0 (Useful to parse them then)
       */
      if ((result instanceof Double) && (((Double) result % 1) == 0)) {
        evaluation = Integer.toString((int) (double) result);
      } else {
        evaluation = result.toString();
      }
      return evaluation;

    } catch (final ParseException e) {
      return "Parsing Error, check expression syntax" + " : " + allExpression;
    } catch (final NumberFormatException e) {
      return "Evaluation Error, check parameter dependencies" + " : " + allExpression;
    }
  }
}
