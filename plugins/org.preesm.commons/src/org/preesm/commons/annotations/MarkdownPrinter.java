package org.preesm.commons.annotations;

/**
 *
 * @author anmorvan
 *
 */
public class MarkdownPrinter {

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
