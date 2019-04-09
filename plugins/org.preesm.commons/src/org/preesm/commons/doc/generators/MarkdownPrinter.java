package org.preesm.commons.doc.generators;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.commons.ReflectionUtil;
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
    final Collection<Class<?>> lookupChildClassesOf = ReflectionUtil.lookupAnnotatedClasses("org.preesm.workflow.tasks",
        PreesmTask.class);
    final Map<String, Set<String>> outputsPerCategory = new HashMap<>();
    for (final Class<?> preesmTask : lookupChildClassesOf) {
      final PreesmTask annotation = preesmTask.getAnnotation(PreesmTask.class);
      final String category = annotation.category();
      final Set<String> categoryContent = outputsPerCategory.getOrDefault(category, new HashSet<>());

      final String output = MarkdownPrinter.prettyPrint(preesmTask);
      categoryContent.add(output);

      outputsPerCategory.put(category, categoryContent);
    }

    final StringBuilder sb = new StringBuilder();
    // get default category, listed in the end of doc
    final Set<String> otherCategoryContent = outputsPerCategory.remove("Other");
    for (final Entry<String, Set<String>> category : outputsPerCategory.entrySet()) {
      final String categoryName = category.getKey();
      final Set<String> categoryContent = category.getValue();
      sb.append("## " + categoryName + "\n");
      for (final String content : categoryContent) {
        sb.append(content + "\n");
      }
    }
    if (otherCategoryContent != null) {
      sb.append("## Other\n");
      for (final String content : otherCategoryContent) {
        sb.append(content + "\n");
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
        sb.append("  * **" + p.name() + "**:" + p.type().getSimpleName());
        if (!p.description().isEmpty()) {
          sb.append(" : _" + p.description() + "_");
        }
        sb.append("\n");
      }
    } else {
      sb.append("None.\n");
    }
    return sb.toString();
  }

}
