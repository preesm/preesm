package org.preesm.algorithm.pisdf.helper;

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.ExpressionProxy;
import org.preesm.model.pisdf.LongExpression;
import org.preesm.model.pisdf.StringExpression;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 *
 * @author anmorvan
 *
 */
final class JEPFastExpressionResolver extends PiMMSwitch<Long> {

  private final JEP jep;

  JEPFastExpressionResolver(final JEP jep) {
    this.jep = jep;
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
    final long value;
    try {
      final Node parse = jep.parse(object.getExpressionString());
      final Object result = jep.evaluate(parse);
      if (result instanceof Long) {
        value = (Long) result;
      } else if (result instanceof Double) {
        value = Math.round((Double) result);
      } else {
        throw new PreesmException("Unsupported result type " + result.getClass().getSimpleName());
      }
    } catch (final ParseException e) {
      throw new PreesmException("Could not parse " + object.getExpressionString(), e);
    }
    return value;
  }

}