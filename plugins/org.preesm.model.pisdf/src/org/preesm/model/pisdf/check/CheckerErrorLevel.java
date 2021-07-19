package org.preesm.model.pisdf.check;

import java.util.logging.Level;

/**
 * Error level categorizes the impact on the PiSDF model consistency.
 * 
 * @author ahonorat
 */
public enum CheckerErrorLevel {
  /**
   * Not an error, only for thresholding.
   */
  NONE(-1, null),
  /**
   * Harmful for load/store.
   */
  FATAL(0, Level.SEVERE),
  /**
   * Harmful for any analysis.
   */
  RECOVERABLE(1, Level.WARNING),
  /**
   * Harmful for code generation only.
   */
  WARNING(2, Level.INFO);

  private int   index;
  private Level loggerCorrespondingLevel;

  private CheckerErrorLevel(final int index, final Level loggerLevel) {
    this.index = index;
    this.loggerCorrespondingLevel = loggerLevel;
  }

  public int getIndex() {
    return index;
  }

  public Level getCorrespondingLoggingLevel() {
    return loggerCorrespondingLevel;
  }
}
