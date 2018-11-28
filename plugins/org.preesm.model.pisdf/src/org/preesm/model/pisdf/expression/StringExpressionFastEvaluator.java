package org.preesm.model.pisdf.expression;

import java.util.Map;
import org.preesm.commons.math.JEPWrapper;
import org.preesm.model.pisdf.ExpressionProxy;
import org.preesm.model.pisdf.LongExpression;
import org.preesm.model.pisdf.StringExpression;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 *
 * @author anmorvan
 *
 */
public final class StringExpressionFastEvaluator extends PiMMSwitch<Long> {

  private final Map<String, Long> paramValues;

  public StringExpressionFastEvaluator(final Map<String, Long> paramValues) {
    this.paramValues = paramValues;
  }

  @Override
  public Long caseLongExpression(final LongExpression object) {
    return object.getValue();
  }

  @Override
  public Long caseExpressionProxy(final ExpressionProxy object) {
    return doSwitch(object.getProxy().getExpression());
  }

  @Override
  public Long caseStringExpression(final StringExpression object) {
    final String expressionString = object.getExpressionString();
    return JEPWrapper.evaluate(expressionString, paramValues);
  }
}
