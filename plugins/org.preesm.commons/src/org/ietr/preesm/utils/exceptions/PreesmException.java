package org.ietr.preesm.utils.exceptions;

/**
 *
 * Topmost exception thrown by Preesm
 *
 * @author anmorvan
 *
 */
public class PreesmException extends RuntimeException {

  private static final long serialVersionUID = -8790240310563615206L;

  public PreesmException() {
    super();
  }

  public PreesmException(final String message) {
    super(message);
  }

  public PreesmException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PreesmException(final Throwable cause) {
    super(cause);
  }

}
