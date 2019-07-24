package org.preesm.codegen.format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.cdt.core.ToolFactory;
import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.cdt.core.formatter.Messages;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.preesm.commons.files.URLHelper;

/**
 *
 * @author anmorvan
 *
 */
public class PreesmCFormatter {

  /**
   *
   */
  public static final void format(final IFile file) {
    final Map<String, ?> options = new LinkedHashMap<>();
    final CodeFormatter formatter = ToolFactory.createCodeFormatter(options);
    formatFile(file.getRawLocation().toFile(), formatter);
  }

  private static void formatFile(final File file, final CodeFormatter codeFormatter) {
    IDocument doc = new Document();
    try {
      // read the file
      String contents = URLHelper.read(file.toURI().toURL());
      // format the file (the meat and potatoes)
      doc.set(contents);
      TextEdit edit = codeFormatter.format(CodeFormatter.K_TRANSLATION_UNIT, contents, 0, contents.length(), 0, null);
      if (edit != null) {
        edit.apply(doc);
      } else {
        System.err.println(Messages.bind(Messages.FormatProblem, file.getAbsolutePath()));
        return;
      }

      // write the file
      final BufferedWriter out = new BufferedWriter(new FileWriter(file));
      try {
        out.write(doc.get());
        out.flush();
      } finally {
        try {
          out.close();
        } catch (IOException e) {
          /* ignore */
        }
      }
    } catch (IOException e) {
      String errorMessage = Messages.bind(Messages.CaughtException, "IOException", e.getLocalizedMessage());
      System.err.println(Messages.bind(Messages.ExceptionSkip, errorMessage));
    } catch (BadLocationException e) {
      String errorMessage = Messages.bind(Messages.CaughtException, "BadLocationException", e.getLocalizedMessage());
      System.err.println(Messages.bind(Messages.ExceptionSkip, errorMessage));
    }
  }
}
