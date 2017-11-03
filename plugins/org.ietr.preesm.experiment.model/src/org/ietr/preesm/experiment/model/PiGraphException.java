package org.ietr.preesm.experiment.model;

/**
 * Generic exception for PiGraph issues
 *
 * @author anmorvan
 *
 */
public class PiGraphException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public PiGraphException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PiGraphException(final String message) {
    this(message, null);
  }
}
