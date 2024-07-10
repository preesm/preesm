/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.model.pisdf.check;

import java.util.SortedSet;
import java.util.TreeSet;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.MoldableParameter;

/**
 * Provide syntax checker and utils for moldable parameters.
 *
 * @author ahonorat
 */
public class MoldableParameterExprChecker {

  private MoldableParameterExprChecker() {
    // Forbids instantiation
  }

  /** Regular expression to validate the syntax, then split with the regex ";" */
  public static final String REDUCED_SYNTAX_GRP_REGEX = "(-?\\d+)(;-?\\d+)*";

  public static boolean isOnlyNumbers(String userExpression) {
    return userExpression.matches(REDUCED_SYNTAX_GRP_REGEX);
  }

  /**
   * Chcek if the expression of a given MoldableParameter is valid. If valid, the default expression is set to the first
   * one, otherwise, it is set to the first invalid expression.
   *
   * @param mp
   *          MoldableParameter to check.
   * @return {@code null} if no problem, the first exception encountered if there is a problem.
   */
  public static Exception isValid(MoldableParameter mp) {
    return checkEachParameter(mp);
  }

  private static Exception checkEachParameter(MoldableParameter mp) {
    final String[] strValues = mp.getUserExpression().split(";");
    for (final String str : strValues) {
      mp.setExpression(str);
      try {
        mp.getExpression().evaluate();
      } catch (final ExpressionEvaluationException e) {
        return e;
      }
    }
    if (strValues.length > 0) {
      mp.setExpression(strValues[0]);
    } else {
      mp.setExpression("");
    }
    return null;
  }

  /**
   * Compute and returns list of long values, being possible values of a moldable parameter.
   *
   * @param userExpression
   *          The moldable parameter userExpression.
   * @return The values, without repetition, in ascending order.
   */
  public static final SortedSet<Long> getUniqueValues(String userExpression) {
    final SortedSet<Long> res = new TreeSet<>();
    if (!isOnlyNumbers(userExpression)) {
      return res;
    }
    final String[] strValues = userExpression.split(";");

    try {
      for (final String strValue : strValues) {
        final long value = Long.parseLong(strValue);
        res.add(value);
      }
    } catch (final NumberFormatException e) {
      throw new PreesmRuntimeException("A number in a moldable parameter expression cannot be converted to long.", e);
    }
    return res;
  }

  /**
   * Returns the default expression of a moldable parameter.
   *
   * @param userExpression
   *          The moldable parameter userExpression.
   * @return The first expression.
   */
  public static final String getFirstExpr(String userExpression) {
    final String[] strValues = userExpression.split(";");
    return strValues[0];
  }

}
