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
