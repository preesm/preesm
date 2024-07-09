/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
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
  FATAL_ALL(0, Level.SEVERE),
  /**
   * Harmful for any analysis.
   */
  FATAL_ANALYSIS(1, Level.WARNING),
  /**
   * Harmful for code generation only.
   */
  FATAL_CODEGEN(2, Level.INFO),
  /**
   * Warnings only.
   */
  WARNING(3, Level.INFO);

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
