package org.ietr.preesm.ui;

/**
 *
 * @author anmorvan
 *
 */
public class PreesmUIException extends RuntimeException {

  private static final long serialVersionUID = -2444889221654370756L;

  public PreesmUIException(final String message) {
    super(message);
  }

  public PreesmUIException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
