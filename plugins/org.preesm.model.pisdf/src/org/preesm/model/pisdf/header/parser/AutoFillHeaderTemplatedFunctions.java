package org.preesm.model.pisdf.header.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.check.RefinementChecker;
import org.preesm.model.pisdf.check.RefinementChecker.CorrespondingTemplateParameterType;

/**
 * This class provides helper to generate the code to call templated C++ functions.
 * 
 * @author ahonorat
 */
public class AutoFillHeaderTemplatedFunctions {

  private AutoFillHeaderTemplatedFunctions() {
    // forbid instantiation
  }

  /**
   * Compute the templated suffix of templated C++ function calls of an actor.
   * 
   * @param a
   *          The actor to consider.
   * @param allFifoSizes
   *          The fifo sizes, used only if related template parameters are present.
   * @return A pair of suffixes, for init call as key, and for loop call as value.
   */
  public static Pair<String, String> getFilledTemplateFunctionPart(final Actor a, Map<Fifo, Long> allFifoSizes) {
    final List<Pair<Port, FunctionArgument>> correspondingArguments = RefinementChecker
        .getCHeaderRefinementCorrespondingArguments(a);
    if (correspondingArguments == null) {
      // not a CHeader
      return null;
    }
    final CHeaderRefinement cref = (CHeaderRefinement) a.getRefinement();
    String initTemplate = null;
    String loopTemplate = null;
    if (cref.getInitPrototype() != null) {
      initTemplate = getFilledTemplatePrototypePart(cref, cref.getInitPrototype(), correspondingArguments,
          allFifoSizes);
    }
    if (cref.getLoopPrototype() != null) {
      loopTemplate = getFilledTemplatePrototypePart(cref, cref.getLoopPrototype(), correspondingArguments,
          allFifoSizes);
    }

    return new Pair<>(initTemplate, loopTemplate);
  }

  private static String getFilledTemplatePrototypePart(final CHeaderRefinement refinement,
      final FunctionPrototype proto, final List<Pair<Port, FunctionArgument>> correspondingArguments,
      Map<Fifo, Long> allFifoSizes) {
    final Map<String, Pair<CorrespondingTemplateParameterType, Object>> relatedObjects = RefinementChecker
        .getCHeaderCorrespondingTemplateParamObject(refinement, proto, correspondingArguments);
    final List<String> evaluatedParams = new ArrayList<>();

    for (final Pair<CorrespondingTemplateParameterType, Object> p : relatedObjects.values()) {
      final Object o = p.getValue();
      final CorrespondingTemplateParameterType c = p.getKey();
      if (c == CorrespondingTemplateParameterType.NONE || c == CorrespondingTemplateParameterType.MULTIPLE) {
        return null;
      }
      if (o instanceof Parameter) {
        final Long value = ((Parameter) o).getExpression().evaluate();
        evaluatedParams.add(value.toString());
      } else if (o instanceof String) {
        evaluatedParams.add((String) o);
      } else if (o instanceof Fifo) {
        final Fifo f = ((Fifo) o);
        if (c == CorrespondingTemplateParameterType.FIFO_TYPE) {
          evaluatedParams.add(f.getType());
        } else if (c == CorrespondingTemplateParameterType.FIFO_DEPTH) {
          final Long depth = allFifoSizes.getOrDefault(f, null);
          if (depth == null) {
            return null;
          }
          evaluatedParams.add(depth.toString());
        } else {
          return null;
        }
      } else {
        // could not evaluate the related object
        return null;
      }
    }

    if (evaluatedParams.isEmpty()) {
      return "";
    }

    final String values = evaluatedParams.stream().collect(Collectors.joining(","));
    return "<" + values + ">";
  }

}
