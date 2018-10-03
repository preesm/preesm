package org.ietr.preesm.evaluator;

/**
 *
 * @author anmorvan
 *
 */
public class EvaluationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EvaluationException(String message) {
    super(message);
  }

  public EvaluationException(String message, Throwable cause) {
    super(message, cause);
  }

}
