/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Morvan Antoine <antoine.morvan@insa-rennes.fr> (2019)
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
package org.preesm.commons.doc.generators;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.commons.PreesmPlugin;
import org.preesm.commons.doc.annotations.DocumentedError;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;

/**
 *
 * @author anmorvan
 *
 */
public class MarkdownPrinter {

  /**
   * Append documentation to File associated to given file path
   */
  public static final void prettyPrintTo(final String filePath) {
    final String prettyPrint = prettyPrint();
    final File f = new File(filePath);
    try {
      Files.append(prettyPrint, f, StandardCharsets.UTF_8);
    } catch (IOException e) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Could not output MarkDown task reference to " + filePath, e);
    }
  }

  /**
   *
   */
  public static final String prettyPrint() {
    final Map<String,
        Set<Class<?>>> outputsPerCategory = new HashMap<>(PreesmPlugin.getInstance().getTasksByCategory());

    final StringBuilder sb = new StringBuilder();
    // get default category, listed in the end of doc
    final Set<Class<?>> otherCategoryContent = outputsPerCategory.remove("Other");
    for (final Entry<String, Set<Class<?>>> category : outputsPerCategory.entrySet()) {
      final String categoryName = category.getKey();
      final Set<Class<?>> categoryContent = category.getValue();
      sb.append("## " + categoryName + "\n");
      for (final Class<?> content : categoryContent) {
        sb.append(MarkdownPrinter.prettyPrint(content) + "\n");
      }
    }
    if (otherCategoryContent != null) {
      sb.append("## Other\n");
      for (final Class<?> content : otherCategoryContent) {
        sb.append(MarkdownPrinter.prettyPrint(content) + "\n");
      }
    }
    return sb.toString();
  }

  /**
   *
   */
  public static final String prettyPrint(final Class<?> preesmTask) {
    final StringBuilder sb = new StringBuilder();
    final PreesmTask annotation = preesmTask.getAnnotation(PreesmTask.class);

    sb.append(generateHeader(preesmTask));
    sb.append(generatePorts("Inputs", annotation.inputs()));
    sb.append(generatePorts("Outputs", annotation.outputs()));
    sb.append(generateDescription(annotation));
    sb.append(generateParameters(annotation.parameters()));
    sb.append(generateDocumentedErrors(annotation.documentedErrors()));
    sb.append(generateSeeAlso(annotation.seeAlso()));

    return sb.toString();
  }

  private static final String generateHeader(final Class<?> preesmTask) {
    final PreesmTask annotation = preesmTask.getAnnotation(PreesmTask.class);

    final String name;
    if (preesmTask.isAnnotationPresent(Deprecated.class)) {
      name = annotation.name() + " - _Deprecated_";
    } else {
      name = annotation.name();
    }
    final String id = annotation.id();
    final String shortDescription = annotation.shortDescription();
    final StringBuilder sb = new StringBuilder();

    sb.append("\n### " + name + "\n\n");
    sb.append("  * **Identifier**: `" + id + "`\n");
    sb.append("  * **Implementing Class**: `" + preesmTask.getCanonicalName() + "`\n");
    sb.append("  * **Short description**: " + shortDescription + "\n");

    return sb.toString();
  }

  private static Object generateDescription(final PreesmTask annotation) {
    StringBuilder sb = new StringBuilder("\n#### Description\n");
    if (annotation.description().isEmpty()) {
      sb.append(annotation.shortDescription() + "\n");
    } else {
      sb.append(annotation.description() + "\n");
    }
    return sb.toString();
  }

  private static String generateSeeAlso(final String[] seeAlso) {
    if (seeAlso.length > 0) {
      final StringBuilder sb = new StringBuilder("\n#### See Also\n\n");
      for (final String s : seeAlso) {
        sb.append("  * " + s + "\n");
      }
      return sb.toString();
    } else {
      return "";
    }
  }

  private static final String generateParameters(final Parameter[] parameters) {
    final StringBuilder sb = new StringBuilder("\n#### Parameters\n");
    if (parameters.length > 0) {
      for (final Parameter param : parameters) {
        sb.append("\n##### " + param.name() + "\n");
        sb.append(param.description() + "\n");
        final Value[] values = param.values();
        if (values.length > 0) {
          sb.append("\n| Value | Effect |\n| --- | --- |\n");
          for (final Value val : values) {
            sb.append("| _" + val.name() + "_ | " + val.effect() + " |\n");
          }
        }
      }
    } else {
      sb.append("None.\n");
    }
    return sb.toString();
  }

  private static final String generateDocumentedErrors(final DocumentedError[] documentedErrors) {
    if (documentedErrors.length > 0) {
      final StringBuilder sb = new StringBuilder("\n#### Documented Errors\n");
      sb.append("\n| Message | Explanation |\n| --- | --- |\n");
      for (final DocumentedError error : documentedErrors) {
        sb.append("| **" + error.message() + "** | " + error.explanation() + " |\n");
      }
      return sb.toString();
    } else {
      return "";
    }
  }

  private static final String generatePorts(final String direction, final Port[] ports) {
    final StringBuilder sb = new StringBuilder("\n#### " + direction + "\n");
    if (ports.length > 0) {
      for (Port p : ports) {
        sb.append("  * **" + p.name() + "** (of _" + p.type().getSimpleName() + "_)");
        if (!p.description().isEmpty()) {
          sb.append(" : " + p.description());
        }
        sb.append("\n");
      }
    } else {
      sb.append("None.\n");
    }
    return sb.toString();
  }

}
