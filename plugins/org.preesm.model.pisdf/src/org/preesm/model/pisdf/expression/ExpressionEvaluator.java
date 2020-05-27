/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019 - 2020)
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
package org.preesm.model.pisdf.expression;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.commons.math.JEPWrapper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.ExpressionProxy;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.LongExpression;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.StringExpression;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 *
 * @author anmorvan
 *
 */
public class ExpressionEvaluator {

  private ExpressionEvaluator() {
    // forbid instantiation
  }

  /**
   *
   * @throws ExpressionEvaluationException
   *           If the expression cannot be evaluated.
   */
  public static final long evaluate(final Parameter param, final Map<Parameter, String> overridenValues) {
    return evaluate(param, param.getValueExpression().getExpressionAsString(), overridenValues);
  }

  /**
   *
   * @throws ExpressionEvaluationException
   *           If the expression cannot be evaluated.
   */
  public static final long evaluate(final Parameterizable p, final String value,
      final Map<Parameter, String> overridenValues) {
    final Map<String, Double> lookupParameterValues = lookupParameterValues(p, overridenValues);
    return JEPWrapper.evaluate(value, lookupParameterValues);
  }

  /**
   *
   * @throws ExpressionEvaluationException
   *           If the expression cannot be evaluated.
   */
  public static final long evaluate(final Expression expression) {
    return evaluate(expression, Collections.emptyMap());
  }

  /**
   * Will lookup parameter values if needed (see
   * {@link InternalExpressionEvaluationVisitor#caseStringExpression(StringExpression)}).
   * 
   * @throws ExpressionEvaluationException
   *           If the expression cannot be evaluated.
   */
  public static final long evaluate(final Expression expression,
      final Map<Parameter, String> overridenParameterValues) {
    return new InternalExpressionEvaluationVisitor(Collections.emptyMap(), overridenParameterValues)
        .doSwitch(expression);
  }

  /**
   *
   */
  public static Parameter lookupFirstNonInterfaceParent(final ConfigInputInterface cii) {
    Parameter parent = cii;
    while (parent instanceof ConfigInputInterface) {
      final ConfigInputPort graphPort = ((ConfigInputInterface) parent).getGraphPort();
      final Dependency incomingDependency = graphPort.getIncomingDependency();
      if (incomingDependency != null) {
        final ISetter setter = incomingDependency.getSetter();
        if (setter instanceof ConfigOutputPort) {
          throw new ExpressionEvaluationException("Cannot evaluate expression of a dynamic Setter");
        } else {
          parent = (Parameter) setter;
          if (!(parent instanceof ConfigInputInterface)) {
            return parent;
          }
        }
      } else {
        return null;
      }
    }
    return null;
  }

  /**
   *
   */
  public static Map<String, Number> lookupParameterValues(final Expression expression,
      final Map<Parameter, String> overridenValues) {
    final Map<String, Number> result = new LinkedHashMap<>();
    final Parameterizable holder = expression.getHolder();
    if (holder != null) {
      result.putAll(lookupParameterValues(holder, overridenValues));
    }
    return result;
  }

  /**
   *
   */
  public static Map<String, Double> lookupParameterValues(final Parameterizable parameterizable,
      final Map<Parameter, String> overridenValues) {
    final Map<String, Double> res = new LinkedHashMap<>();
    if (parameterizable != null) {
      final List<Parameter> inputParameters = parameterizable.getInputParameters();
      for (final Parameter param : inputParameters) {
        final String paramExpressionValue = getParamExpression(overridenValues, param);
        final double evaluate = evaluateExpression(overridenValues, param, paramExpressionValue);

        if ((parameterizable instanceof Parameter) || (parameterizable instanceof Delay)
            || (parameterizable instanceof InterfaceActor) || (parameterizable instanceof PiGraph)) {
          res.put(param.getName(), evaluate);
        } else if (parameterizable instanceof DataPort || parameterizable instanceof AbstractActor) {
          lookupActorAndDataPort(parameterizable, res, param, evaluate);
        } else {
          throw new ExpressionEvaluationException("Could not compute proper parameter name");
        }
      }
    }
    return res;
  }

  private static void lookupActorAndDataPort(final Parameterizable parameterizable, final Map<String, Double> res,
      final Parameter param, final double evaluate) {
    final AbstractActor actor;
    if (parameterizable instanceof AbstractActor) {
      actor = (AbstractActor) parameterizable;
    } else {
      actor = ((DataPort) parameterizable).getContainingActor();
    }
    if (actor instanceof InterfaceActor || actor instanceof DelayActor) {
      res.put(param.getName(), evaluate);
    } else {
      final List<ConfigInputPort> inputPorts = actor.lookupConfigInputPortsConnectedWithParameter(param);
      for (ConfigInputPort cip : inputPorts) {
        final String name = cip.getName();
        res.put(name, evaluate);
      }
    }
  }

  private static double evaluateExpression(final Map<Parameter, String> overridenValues, final Parameter param,
      final String paramExpressionValue) {
    final double evaluate;
    if (param instanceof ConfigInputInterface) {
      final ConfigInputInterface configInputInterface = (ConfigInputInterface) param;
      evaluate = evaluateConfigInputInterface(overridenValues, configInputInterface);
    } else {
      evaluate = evaluate(param, paramExpressionValue, overridenValues);
    }
    return evaluate;
  }

  private static String getParamExpression(final Map<Parameter, String> overridenValues, final Parameter param) {
    final String paramExpressionValue;
    if (overridenValues.containsKey(param)) {
      paramExpressionValue = overridenValues.get(param);
    } else {
      paramExpressionValue = param.getExpression().getExpressionAsString();
    }
    return paramExpressionValue;
  }

  private static double evaluateConfigInputInterface(final Map<Parameter, String> overridenValues,
      final ConfigInputInterface configInputInterface) {
    final double evaluate;
    final Parameter connectedParam = lookupFirstNonInterfaceParent(configInputInterface);
    if (connectedParam != null) {
      final String connectedeParamExpressionValue;
      if (overridenValues.containsKey(connectedParam)) {
        connectedeParamExpressionValue = overridenValues.get(connectedParam);
      } else {
        connectedeParamExpressionValue = connectedParam.getExpression().getExpressionAsString();
      }
      evaluate = evaluate(connectedParam, connectedeParamExpressionValue, overridenValues);
    } else {
      evaluate = evaluate(configInputInterface.getExpression());
    }
    return evaluate;
  }

  /**
   *
   */
  public static final boolean canEvaluate(final Parameterizable p, final String value) {
    if (value != null && !value.isEmpty()) {
      final List<String> involvement = JEPWrapper.involvement(value);
      final Map<String, Double> lookupParameterValues = lookupParameterValues(p, Collections.emptyMap());
      return lookupParameterValues.keySet().containsAll(involvement);
    }
    return false;
  }

  /**
   *
   * @author anmorvan
   *
   */
  private static class InternalExpressionEvaluationVisitor extends PiMMSwitch<Long> {
    private final Map<String, ? extends Number> parameterValues;
    private final Map<Parameter, String>        overridenValues;

    /**
     * Initialize the Expression evaluator with pre-computed parameter values. This can speedup evaluation as there will
     * be no parameter value lookup.
     */
    private InternalExpressionEvaluationVisitor(final Map<String, ? extends Number> parameterValues,
        final Map<Parameter, String> overridenValues) {
      this.parameterValues = parameterValues;
      this.overridenValues = overridenValues;
    }

    @Override
    public Long caseLongExpression(final LongExpression longExpr) {
      return longExpr.getValue();
    }

    @Override
    public Long caseExpressionProxy(final ExpressionProxy proxyExpr) {
      return doSwitch(proxyExpr.getProxy().getExpression());
    }

    @Override
    public Long caseStringExpression(final StringExpression stringExpr) {
      final String expressionString = stringExpr.getExpressionString();
      try {
        // try to parse a long value stored as String
        // NumberFormatException is thrown if the expression String does not represent a long value
        return Long.parseLong(expressionString);
      } catch (final NumberFormatException e) {
        try {
          // try to evaluate the expression without collecting variables, but only the one given in the parameterValue
          // Map. ExpressionEvaluationException is thrown if the evaluation encounter unknown parameters.
          // This can speedup even with empty Map in case expression only involves constant values.
          return JEPWrapper.evaluate(expressionString, this.parameterValues);
        } catch (final ExpressionEvaluationException ex) {
          // gather Expression parameters and evaluate the expression.
          // ExpressionEvaluationException will still be thrown if something goes wrong.
          final Map<String,
              Number> addInputParameterValues = ExpressionEvaluator.lookupParameterValues(stringExpr, overridenValues);
          return JEPWrapper.evaluate(expressionString, addInputParameterValues);
        }
      }
    }
  }
}
