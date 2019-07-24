package org.preesm.codegen.format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ToolFactory;
import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.cdt.core.formatter.Messages;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.TextEdit;
import org.preesm.commons.files.URLHelper;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Deeply inspired from {@link org.eclipse.cdt.core.formatter.CodeFormatterApplication}.
 *
 * @author anmorvan
 *
 */
public class PreesmCFormatter {

  /**
   *
   */
  public static final void format(final IFile file) {
    final Map<String, String> options = CCorePlugin.getOptions();
    options.put("org.eclipse.cdt.core.formatter.tabulation.char", "space");
    options.put("org.eclipse.cdt.core.formatter.tabulation.size", "2");
    options.put("org.eclipse.cdt.core.formatter.lineSplit", "120");
    options.put("org.eclipse.cdt.core.encoding", "UTF-8");
    final CodeFormatter formatter = ToolFactory.createDefaultCodeFormatter(options);
    PreesmCFormatter.formatFile(file.getRawLocation().toFile(), formatter);
  }

  private static void formatFile(final File file, final CodeFormatter codeFormatter) {
    final IDocument doc = new Document();
    try {
      // read the file
      final String contents = URLHelper.read(file.toURI().toURL());
      // format the file (the meat and potatoes)
      doc.set(contents);
      final TextEdit edit = codeFormatter.format(CodeFormatter.K_TRANSLATION_UNIT, contents, 0, contents.length(), 0,
          null);
      if (edit != null) {
        edit.apply(doc);
      } else {
        final String errorMessage = NLS.bind(Messages.FormatProblem, file.getAbsolutePath());
        PreesmLogger.getLogger().log(Level.WARNING, errorMessage);
        return;
      }

      // write the file
      try (final BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
        out.write(doc.get());
        out.flush();
      }
    } catch (final IOException e) {
      final String errorMessage = NLS.bind(Messages.CaughtException, "IOException", e.getLocalizedMessage());
      PreesmLogger.getLogger().log(Level.WARNING, errorMessage);
    } catch (final BadLocationException e) {
      final String errorMessage = NLS.bind(Messages.CaughtException, "BadLocationException", e.getLocalizedMessage());
      PreesmLogger.getLogger().log(Level.WARNING, errorMessage);
    }
  }
}
