/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
package org.preesm.commons.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.TokenMgrError;
import org.nfunk.jep.Variable;
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
   * Return the list of symbols used in the expression. The result list does not include standard constants, standard
   * functions and user declared functions (see {@link #initJep(Map)}). Unknown functions are treated as symbols.
   * 
   * @throws ExpressionEvaluationException
   *           If the expression cannot be evaluated.
   */
  public static final List<String> involvement(final String expression) {
    final JEP jep = initJep(Collections.emptyMap());
    // allow undeclared to be able to list symbols without values
    jep.setAllowUndeclared(true);
    // allow implicit multiplication to be able to list undeclared functions as symbols
    jep.setImplicitMul(true);
    final List<String> res = new ArrayList<>();
    try {
      jep.parse(expression);
      final SymbolTable symbolTable = jep.getSymbolTable();
      @SuppressWarnings("unchecked")
      final Set<Entry<String, Variable>> symbols = symbolTable.entrySet();
      for (final Entry<String, Variable> sym : symbols) {
        if (sym.getValue().getValue() == null) {
          res.add(sym.getKey());
        }
      }
    } catch (ParseException | TokenMgrError e) {
      throw new ExpressionEvaluationException("Could not analyse '" + expression + "'.", e);
    }
    return res;
  }

  /**
   *
   * @throws ExpressionEvaluationException
   *           If the expression cannot be evaluated.
   */
  public static final long evaluate(final String expression,
      final Map<String, ? extends Number> addInputParameterValues) {
    final JEP jep = initJep(addInputParameterValues);
    long result;
    try {
      result = parse(expression, jep);
    } catch (final ParseException | TokenMgrError e) {
      final String msg = "Could not evaluate '" + expression + "': " + e.getMessage().trim();
      throw new ExpressionEvaluationException(msg, e);
    }
    return result;
  }

  private static JEP initJep(final Map<String, ? extends Number> addInputParameterValues) {
    final JEP jep = new JEP();

    jep.setAllowAssignment(false);
    jep.setAllowUndeclared(false);
    jep.setImplicitMul(false);

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

  /**
   * 
   * @throws ExpressionEvaluationException
   *           If the expression cannot be evaluated.
   */
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
      throw new ExpressionEvaluationException("Unsupported result type " + result.getClass().getSimpleName());
    }
  }
}
