/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

package org.preesm.codegen.format;

import org.eclipse.core.resources.IFile;
import org.preesm.commons.logger.PreesmLogger;

/**
 * This class helps to print formatted code.
 *
 * @author ahonorat
 */
public class CodeFormatterAndPrinter {

  private CodeFormatterAndPrinter() {
    // forbid instantiation
  }

  /**
   * Format the code in the file if language is recognized (based on file extension).
   *
   * @param iFile
   *          Existing file to format.
   */
  public static void format(final IFile iFile) {
    final String ext = iFile.getFileExtension();
    switch (ext) {
      case "c", "h", "cpp" -> PreesmCFormatter.format(iFile);
      case "xml" -> PreesmXMLFormatter.format(iFile);
      default -> PreesmLogger.getLogger()
          .fine(() -> "One file with extension '" + ext + "' has been generated but not formatted.");
    }
  }

}
