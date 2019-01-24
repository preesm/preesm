package org.preesm.workflow.eow;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 */
public class ErrorOnWarningLogHandler extends Handler {

  // keep track of handled records to avoid throwing another error when reporting the warning as error.
  private final Set<LogRecord> recordedLogs = new HashSet<>();

  @Override
  public void publish(final LogRecord record) {
    if (record.getLevel().intValue() >= Level.WARNING.intValue() && !recordedLogs.contains(record)) {
      record.setLevel(Level.SEVERE);
      recordedLogs.add(record);
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
