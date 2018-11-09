/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Define the singleton managing PREESM loggers. When using this helper class, your debug, info, warning and errors
 * messages will be displayed in the right eclipse console. If no Eclipse GUI plugin is loaded (i.e. executing a job in
 * command line), all the messages will be sent to the system console.
 *
 * @author cguy
 *
 *         Code adapted from ORCC (net.sf.orcc.core, https://github.com/orcc/orcc)
 * @author Antoine Lorence
 *
 */
public class CLIWorkflowLogger extends Logger {

  /** The Constant RAW_FLAG. */
  static final String RAW_FLAG = "raw_record";

  /** The logger. */
  public CLIWorkflowLogger(final boolean debugMode) {
    super("Preesm-CLI", null);
    final Handler handler = new CLIWorkflowLogHandler(debugMode);
    this.addHandler(handler);
    this.setUseParentHandlers(false);

  }

  /**
   * Log.
   *
   * @param level
   *          the level
   * @param msg
   *          the msg
   */
  @Override
  public void log(final Level level, final String msg) {
    final String message = msg + "\n";
    super.log(level, message);
  }

  @Override
  public void setLevel(Level newLevel) {
    super.setLevel(newLevel);
    final Handler[] handlers = getHandlers();
    for (Handler h : handlers) {
      h.setLevel(newLevel);
    }
  }

}
