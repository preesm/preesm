package org.preesm.model.pisdf.check;

import java.util.SortedSet;
import java.util.TreeSet;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.MalleableParameter;

/**
 * Provide syntax checker and utils for malleable parameters.
 * 
 * @author ahonorat
 */
public class MalleableParameterExprChecker {

  /** Regular expression to validate the syntax, then split with the regex ";" */
  public static final String REDUCED_SYNTAX_GRP_REGEX = "([-]?[0-9]+)(;[-]?[0-9]+)*";

  public static boolean isOnlyNumbers(String userExpression) {
    return userExpression.matches(REDUCED_SYNTAX_GRP_REGEX);
  }

  /**
   * Chcek if the expression of a given MalleableParameter is valid.
   * 
   * @param mp
   *          MalleableParameter to check.
   * @return {@code null} if no problem, the first exception encountered if there is a problem.
   */
  public static ExpressionEvaluationException isValid(MalleableParameter mp) {
    if (isOnlyNumbers(mp.getUserExpression())) {
      return null;
    }
    return checkEachParameter(mp);
  }

  private static ExpressionEvaluationException checkEachParameter(MalleableParameter mp) {
    String[] strValues = mp.getUserExpression().split(";");
    String backupFirst = mp.getExpression().getExpressionAsString();
    ExpressionEvaluationException res = null;
    for (String str : strValues) {
      mp.setExpression(str);
      try {
        mp.getExpression().evaluate();
      } catch (ExpressionEvaluationException e) {
        res = e;
        break;
      }
    }
    mp.setExpression(backupFirst);
    return res;
  }

  /**
   * Compute and returns list of long values, being possible values of a malleable parameter.
   * 
   * @param userExpression
   *          The malleable parameter userExpression.
   * @return The values, without repetition, in ascending order.
   */
  public static final SortedSet<Long> getUniqueValues(String userExpression) {
    SortedSet<Long> res = new TreeSet<>();
    if (!isOnlyNumbers(userExpression)) {
      return res;
    }
    String[] strValues = userExpression.split(";");

    try {
      for (String strValue : strValues) {
        long value = Long.parseLong(strValue);
        res.add(value);
      }
    } catch (NumberFormatException e) {
      throw new PreesmRuntimeException("A number in a malleable parameter expression cannot be converted to long.", e);
    }
    return res;
  }

  /**
   * Returns the default expression of a malleable parameter.
   * 
   * @param userExpression
   *          The malleable parameter userExpression.
   * @return The first expression.
   */
  public static final String getFirstExpr(String userExpression) {
    String[] strValues = userExpression.split(";");
    return strValues[0];
  }

}
