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
package org.preesm.ui.workflow.tools;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.preesm.commons.logger.DefaultPreesmFormatter;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.workflow.WorkflowException;

/**
 * Displaying information or error messages through a console initialized by the initConsole method.
 *
 * @author mwipliez
 * @author mpelcat
 */
public class PreesmWorkflowLogger extends Logger {

  /** The Constant LOGGER_NAME. */
  private static final String LOGGER_NAME = "net.sf.dftools.log.WorkflowLogger";

  /** The console. */
  MessageConsole console = null;

  /**
   * Instantiates a new DF tools workflow logger.
   */
  public PreesmWorkflowLogger() {
    super(PreesmWorkflowLogger.LOGGER_NAME, null);
    LogManager.getLogManager().addLogger(this);

    initConsole();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.logging.Logger#log(java.util.logging.LogRecord)
   */
  @Override
  public void log(final LogRecord record) {
    final Level level = record.getLevel();
    final int levelVal = level.intValue();
    if ((getLevel() == null) || (levelVal >= getLevel().intValue())) {

      if (this.console == null) {
        // Writes a log in standard output
        Logger.getAnonymousLogger().log(record);
      } else {
        // Writes a log in console
        this.console.activate();
        try (final MessageConsoleStream stream = new MessageConsoleStream(this.console, this.console.getCharset())) {
          PreesmUIPlugin.getDefault().getWorkbench().getDisplay().asyncExec(() -> {
            if (levelVal < Level.WARNING.intValue()) {
              stream.setColor(new Color(null, 0, 0, 0));
            } else if (levelVal == Level.WARNING.intValue()) {
              stream.setColor(new Color(null, 255, 150, 0));
            } else if (levelVal > Level.WARNING.intValue()) {
              stream.setColor(new Color(null, 255, 0, 0));
            }
          });
          stream.println(new DefaultPreesmFormatter(false).format(record));
          if (record.getThrown() != null) {
            // always log stack trace in the anonymous logger
            Logger.getAnonymousLogger().log(Level.SEVERE, record.getThrown().getMessage(), record.getThrown());
          }
        } catch (IOException e) {
          throw new WorkflowException("Could not open console stream", e);
        }
      }
    }
  }

  /**
   * Inits the console.
   */
  public void initConsole() {
    setLevel(Level.INFO);
    final IConsoleManager mgr = ConsolePlugin.getDefault().getConsoleManager();

    if (this.console == null) {
      this.console = new MessageConsole("DFTools Workflow console", null);
      mgr.addConsoles(new IConsole[] { this.console });
    }

    this.console.activate();
    this.console.setBackground(new Color(null, 230, 228, 252));

    mgr.refresh(this.console);
  }

}
