/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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

  private ExpressionEvaluator() {
    // use static methods only
  }

  /**
   * Take an {@link Expression}, use its eContainers to lookup parameters and finally evaluates the expression to a long value representing the result
   *
   */
  public static final long evaluate(final Expression expression) {
    final Parameterizable parameterizableObj = ExpressionEvaluator.lookUpParameters(expression);
    final Map<String, Number> addInputParameterValues = ExpressionEvaluator.addInputParameterValues(parameterizableObj);
    return ExpressionEvaluator.evaluate(expression.getExpressionString(), addInputParameterValues);
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
    } catch (final ParseException e) {
      throw new ExpressionEvaluationException("Could not evaluate " + expression, e);
    }
    return result;
  }

  private static JEP initJep(final Map<String, Number> addInputParameterValues) {
    final JEP jep = new JEP();

    if (addInputParameterValues != null) {
      addInputParameterValues.forEach(jep::addVariable);
    }
    jep.addStandardConstants();
    jep.addStandardFunctions();

    jep.addFunction("floor", new FloorFunction());
    jep.addFunction("ceil", new CeilFunction());

    return jep;

  }

  private static long parse(final String allExpression, final JEP jep) throws ParseException {
    final Node parse = jep.parse(allExpression);
    final Object result = jep.evaluate(parse);
    if (!(result instanceof Double)) {
      throw new UnsupportedOperationException("Unsupported result type " + result.getClass().getSimpleName());
    }
    final Double dResult = (Double) result;
    if (Double.isInfinite(dResult)) {
      throw new ExpressionEvaluationException("Expression " + allExpression + " evaluated to infinity.");
    }
    return Math.round(dResult);
  }

  private static Parameterizable lookUpParameters(final Expression expression) {
    Parameterizable parameterizableObj;
    if (expression.eContainer() instanceof Parameterizable) {
      parameterizableObj = (Parameterizable) expression.eContainer();
    } else if (expression.eContainer().eContainer() instanceof Parameterizable) {
      parameterizableObj = (Parameterizable) expression.eContainer().eContainer();
    } else {
      throw new ExpressionEvaluationException("Neither a child of Parameterizable nor a child of a child of Parameterizable");
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

        final String evaluatedParam = p.getValueExpression().evaluate();
        final double parseDouble = Double.parseDouble(evaluatedParam);
        result.put(parameterName, parseDouble);
      }
    }
    return result;
  }
}
