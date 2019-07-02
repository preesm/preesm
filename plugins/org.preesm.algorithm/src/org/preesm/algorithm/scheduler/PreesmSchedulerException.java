package org.preesm.algorithm.scheduler;

import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 *
 * @author anmorvan
 *
 */
public class PreesmSchedulerException extends PreesmRuntimeException {

  private static final long serialVersionUID = 8953229077928699904L;

  public PreesmSchedulerException() {
    this((String) null);
  }

  public PreesmSchedulerException(String message) {
    this(message, null);
  }

  public PreesmSchedulerException(String message, Throwable cause) {
    super(true, message, cause);
  }

  public PreesmSchedulerException(Throwable cause) {
    this(null, cause);

  }
}
