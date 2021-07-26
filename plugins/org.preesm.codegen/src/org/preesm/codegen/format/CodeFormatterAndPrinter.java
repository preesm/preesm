package org.preesm.codegen.format;

import java.util.logging.Level;
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
      case "c":
      case "h":
      case "cpp":
        PreesmCFormatter.format(iFile);
        break;
      case "xml":
        PreesmXMLFormatter.format(iFile);
        break;
      default:
        final String msg = "One file with extension '" + ext + "' has been generated but not formatted.";
        PreesmLogger.getLogger().log(Level.FINE, msg);
    }

  }

}
