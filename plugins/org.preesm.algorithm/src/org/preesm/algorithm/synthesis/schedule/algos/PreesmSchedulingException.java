package org.preesm.algorithm.synthesis.schedule.algos;

import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * This class is dedicated to exceptions occuring during scheduling process.
 * 
 * @author ahonorat
 *
 */
public class PreesmSchedulingException extends PreesmRuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -3372846062816482972L;

  public PreesmSchedulingException() {
    this("Scheduling exception (no more information).");
  }

  public PreesmSchedulingException(final String message) {
    this(message, null);
  }

  public PreesmSchedulingException(final String message, final Throwable cause) {
    super(true, message, cause);
  }

  public PreesmSchedulingException(final Throwable cause) {
    this(null, cause);

  }

}
