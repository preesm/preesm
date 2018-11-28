/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.StringExpression;

/**
 *
 * @author anmorvan
 *
 */
public class StringExpressionEvaluator {

  private StringExpressionEvaluator() {
    // use static methods only
  }

  /**
   * Take an {@link Expression}, use its eContainers to lookup parameters and finally evaluates the expression to a long
   * value representing the result
   *
   */
  public static final long evaluate(final StringExpression expression) {
    try {
      return Long.parseLong(expression.getExpressionString());
    } catch (final NumberFormatException e) {
      final Map<String, Number> addInputParameterValues = StringExpressionEvaluator.addInputParameterValues(expression);
      return JEPWrapper.evaluate(expression.getExpressionAsString(), addInputParameterValues);
    }
  }

  private static Map<String, Number> addInputParameterValues(final Expression expression) {
    final Map<String, Number> result = new LinkedHashMap<>();
    final ExpressionHolder holder = expression.getHolder();
    if (holder != null) {
      final EList<Parameter> inputParameters = holder.getInputParameters();
      for (final Parameter param : inputParameters) {
        final Expression valueExpression = param.getValueExpression();
        final double value = valueExpression.evaluate();

        if ((holder instanceof Parameter) || (holder instanceof Delay) || (holder instanceof InterfaceActor)
            || (holder instanceof PeriodicElement)) {
          result.put(param.getName(), value);
        } else if (holder instanceof DataPort) {
          final AbstractActor containingActor = ((DataPort) holder).getContainingActor();
          if (containingActor instanceof InterfaceActor || containingActor instanceof DelayActor) {
            result.put(param.getName(), value);
          } else {
            final List<
                ConfigInputPort> inputPorts = containingActor.lookupConfigInputPortsConnectedWithParameter(param);
            for (ConfigInputPort cip : inputPorts) {
              final String name = cip.getName();
              result.put(name, value);
            }
          }

        } else {
          throw new ExpressionEvaluationException("Could not compute proper parameter name");
        }
      }
    }
    return result;
  }

}
