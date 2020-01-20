package org.preesm.model.pisdf.check;

import java.util.SortedSet;
import java.util.TreeSet;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * Provide syntax checker and utils for malleable parameters.
 * 
 * @author ahonorat
 */
public class MalleableParameterExprChecker {

  /** Regular expression to validate the syntax, then split with the regex ";" */
  public static final String SYNTAX_GRP_REGEX = "([-]?[0-9]+)(;[-]?[0-9]+)*";

  public static boolean isValid(String userExpression) {
    return userExpression.matches(SYNTAX_GRP_REGEX);
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
    if (!isValid(userExpression)) {
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
   * Compute and returns list of long values, being possible values of a malleable parameter.
   * 
   * @param userExpression
   *          The malleable parameter userExpression.
   * @return The values, without repetition.
   */
  public static final Long getFirstValue(String userExpression) {
    Long res = null;
    if (!isValid(userExpression)) {
      return res;
    }
    String[] strValues = userExpression.split(";");

    try {
      res = Long.parseLong(strValues[0]);
    } catch (NumberFormatException e) {
      throw new PreesmRuntimeException("A number in a malleable parameter expression cannot be converted to long.", e);
    }
    return res;
  }

}
