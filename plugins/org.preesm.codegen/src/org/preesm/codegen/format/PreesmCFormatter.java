/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
