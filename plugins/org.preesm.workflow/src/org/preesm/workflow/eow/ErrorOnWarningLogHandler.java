package org.preesm.workflow.eow;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 */
public class ErrorOnWarningLogHandler extends Handler {

  @Override
  public void publish(LogRecord record) {
    if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
      throw new ErrorOnWarningError(record);
    }
  }

  @Override
  public void flush() {
    // nothing
  }

  @Override
  public void close() {
    // nothing
  }

}
