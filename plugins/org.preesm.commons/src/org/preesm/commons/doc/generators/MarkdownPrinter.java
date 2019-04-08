package org.preesm.commons.doc.generators;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
   *
   */
  public static final void prettyPrintTo(final String filePath) {
    final String prettyPrint = prettyPrint();
    final File f = new File(filePath);
    try {
      Files.append(prettyPrint, f, Charset.forName("UTF8"));
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
    for (final Class<?> t : lookupChildClassesOf) {
      final PreesmTask annotation = t.getAnnotation(PreesmTask.class);
      final String category = annotation.category();
      final Set<String> categoryContent = outputsPerCategory.getOrDefault(category, new HashSet<>());

      final String output = MarkdownPrinter.prettyPrint(annotation);
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
  public static final String prettyPrint(final PreesmTask annotation) {
    final StringBuilder sb = new StringBuilder();

    final String name = annotation.name();
    final String id = annotation.id();

    sb.append("\n### " + name + "\n\n");

    sb.append("  * **Identifier**: `" + id + "`\n");
    sb.append("  * **Short description**: " + annotation.shortDescription() + "\n");

    sb.append("\n#### Inputs\n" + generatePorts(annotation.inputs()));

    sb.append("\n#### Outputs\n" + generatePorts(annotation.outputs()));

    sb.append("\n#### Description\n");
    sb.append(annotation.description() + "\n");

    sb.append("\n#### Parameters\n" + generateParameters(annotation.parameters()));

    sb.append("\n#### Documented Errors\n" + generateDocumentedErrors(annotation.documentedErrors()));

    sb.append("\n#### See Also\n" + generateSeeAlso(annotation.seeAlso()));

    return sb.toString();
  }

  private static String generateSeeAlso(final String[] seeAlso) {
    final StringBuilder sb = new StringBuilder("\n");
    for (final String s : seeAlso) {
      sb.append("  * " + s + "\n");
    }
    return sb.toString();
  }

  private static final String generateParameters(final Parameter[] parameters) {
    final StringBuilder sb = new StringBuilder("");
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
    return sb.toString();
  }

  private static final String generateDocumentedErrors(final DocumentedError[] documentedErrors) {
    final StringBuilder sb = new StringBuilder("\n| Message | Explanation |\n| --- | --- |\n");
    for (final DocumentedError error : documentedErrors) {
      sb.append("| **" + error.message() + "** | " + error.explanation() + " |\n");
    }
    return sb.toString();
  }

  private static final String generatePorts(final Port[] ports) {
    final StringBuilder sb = new StringBuilder("");
    for (Port p : ports) {
      sb.append("  * **" + p.name() + "**:" + p.type().getSimpleName() + " : _" + p.description() + "_\n");
    }
    return sb.toString();
  }

}
