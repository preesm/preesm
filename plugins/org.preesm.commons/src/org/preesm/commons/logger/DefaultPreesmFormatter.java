/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.commons.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Define how text must be printed to logger (Eclipse or System console).
 *
 * @author Antoine Lorence
 */
public class DefaultPreesmFormatter extends Formatter {

  private final boolean debugMode;

  public DefaultPreesmFormatter(final boolean debugMode) {
    super();
    this.debugMode = debugMode;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
   */
  @Override
  public String format(final LogRecord logRecord) {

    final StringBuilder output = new StringBuilder();

    if (!hasRawFlag(logRecord)) {
      final Date date = new Date(logRecord.getMillis());

      output.append(PreesmLogger.getFormattedTime(date));
      // Default printing for warn & severe
      if (logRecord.getLevel().intValue() > Level.WARNING.intValue()) {
        output.append(" ").append(logRecord.getLevel());
      } else if (logRecord.getLevel().intValue() > Level.INFO.intValue()) {
        output.append(" ").append(logRecord.getLevel());
      } else if (logRecord.getLevel().intValue() == Level.INFO.intValue()) {
        output.append(" NOTICE");
      } else if (logRecord.getLevel().intValue() < Level.INFO.intValue()) {
        output.append(" DEBUG");
      }
      output.append(": ");
    }

    output.append(logRecord.getMessage());

    final Throwable thrown = logRecord.getThrown();
    if (thrown != null && debugMode) {
      output.append("\n" + ExceptionUtils.getStackTrace(thrown));
    }

    return output.toString();
  }

  /**
   * Checks for raw flag.
   *
   * @param logRecord
   *          the record
   * @return true, if successful
   */
  private boolean hasRawFlag(final LogRecord logRecord) {
    final Object[] params = logRecord.getParameters();
    if (params == null) {
      return false;
    }

    for (final Object param : params) {
      if (CLIWorkflowLogger.RAW_FLAG.equals(param)) {
        return true;
      }
    }
    return false;
  }
}
