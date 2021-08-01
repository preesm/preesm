package org.preesm.model.pisdf.header.parser;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.FunctionPrototype;

/**
 * This class provides helper to check the templated functions and generate the code to call them.
 * 
 * @author ahonorat
 */
public class AutoFillHeaderTemplate {

  private AutoFillHeaderTemplate() {
    // forbid instantiation
  }

  /**
   * Associates templated parameter names with their related Parameter or DataPort (if fifo info).
   * 
   * @param refinement
   *          The refinement to consider.
   * @param proto
   *          The refinement prototype to consider (init or loop).
   * @param lookForFifInfos
   *          Whether or not the special parameters for fifo infos are checked.
   * @return A list of pair with parameter name as key and related element as value ({@code null} if not found).
   */
  public static List<Pair<String, Object>> getCorrespondingParamObject(final CHeaderRefinement refinement,
      final FunctionPrototype proto, final boolean lookForFifInfos) {
    final List<Pair<String, Object>> result = new ArrayList<>();
    // split the template parameters
    final String rawFunctionName = proto.getName();
    final int indexStartTemplate = rawFunctionName.indexOf("<");
    final int indexEndTemplate = rawFunctionName.lastIndexOf(">");
    if (indexStartTemplate < 0 || indexEndTemplate < 0 || indexEndTemplate < indexStartTemplate) {
      // not templated
      return result;
    }
    final String onlyTemplatePart = rawFunctionName.substring(indexStartTemplate + 1, indexEndTemplate);
    final String[] rawTemplateSubparts = onlyTemplatePart.split(",");
    for (final String rawTemplateSubpart : rawTemplateSubparts) {
      // we split again in case of a default value
      final String[] equalSubparts = rawTemplateSubpart.split("\\s*=\\s*");
      final String paramName = equalSubparts[0];
      // TODO parse all params to look for the corresponding one
      // TODO parse all function arguments to look for corresponding template name if FIFO
      if (equalSubparts.length > 2) {
        final String defaultValue = equalSubparts[1];
        result.add(new Pair<>(paramName, defaultValue));
      }
    }

    return result;
  }

}
