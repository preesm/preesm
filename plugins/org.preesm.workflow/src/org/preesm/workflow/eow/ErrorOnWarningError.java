package org.preesm.workflow.eow;

import java.util.logging.LogRecord;

/**
 * Extend Error to not be caught by usual try/catch structures and properly stop execution
 */
public class ErrorOnWarningError extends Error {

  private final LogRecord record;

  public ErrorOnWarningError(LogRecord record) {
    this.record = record;
  }

  public LogRecord getRecord() {
    return record;
  }

  private static final long serialVersionUID = 6841709901022748942L;

}
