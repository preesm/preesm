/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.dftools.workflow.tools;

import java.text.DateFormat;
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
class DefaultPreesmFormatter extends Formatter {

  /*
   * (non-Javadoc)
   *
   * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
   */
  @Override
  public String format(final LogRecord record) {

    final StringBuilder output = new StringBuilder();

    if (!hasRawFlag(record)) {
      final Date date = new Date(record.getMillis());
      final DateFormat df = DateFormat.getTimeInstance();

      output.append(df.format(date));
      // Default printing for warn & severe
      if (record.getLevel().intValue() > Level.INFO.intValue()) {
        output.append(" ").append(record.getLevel());
      } else if (record.getLevel().intValue() == Level.INFO.intValue()) {
        output.append(" NOTICE");
      } else if (record.getLevel().intValue() == Level.FINER.intValue()) {
        output.append(" DEBUG");
      }
      output.append(": ");
    }

    output.append(record.getMessage());

    final Throwable thrown = record.getThrown();
    if (thrown != null) {
      output.append("\n" + ExceptionUtils.getStackTrace(thrown));
    }

    return output.toString();
  }

  /**
   * Checks for raw flag.
   *
   * @param record
   *          the record
   * @return true, if successful
   */
  private boolean hasRawFlag(final LogRecord record) {
    final Object[] params = record.getParameters();
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
