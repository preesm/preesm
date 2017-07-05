package org.ietr.preesm.experiment.model.expression;

import org.nfunk.jep.ParseException;

/**
 *
 * @author anmorvan
 *
 */
public class ExpressionEvaluationException extends RuntimeException {

  public ExpressionEvaluationException(final String msg) {
    super(msg);
  }

  public ExpressionEvaluationException(final String msg, final ParseException cause) {
    super(msg, cause);
  }

  /**
   *
   */
  private static final long serialVersionUID = 6317019195219546436L;

}
