package org.preesm.algorithm.schedule.fpga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
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
 * This class provides helper to generate the code to call FPGA templated C++ functions.
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
   * @return A pair of suffixes, for init call as key, and for loop call as value.
   */
  public static Pair<String, String> getFilledTemplateFunctionPart(final Actor a) {
    final Pair<List<Pair<Port, FunctionArgument>>,
        List<Pair<Port, FunctionArgument>>> correspondingArguments = RefinementChecker
            .getCHeaderRefinementCorrespondingArguments(a);
    if (correspondingArguments == null) {
      // not a CHeader
      return null;
    }
    final CHeaderRefinement cref = (CHeaderRefinement) a.getRefinement();
    String initTemplate = null;
    String loopTemplate = null;
    if (cref.getInitPrototype() != null) {
      initTemplate = getFilledTemplatePrototypePart(cref, cref.getInitPrototype(), correspondingArguments.getKey());
    }
    if (cref.getLoopPrototype() != null) {
      loopTemplate = getFilledTemplatePrototypePart(cref, cref.getLoopPrototype(), correspondingArguments.getValue());
    }

    return new Pair<>(initTemplate, loopTemplate);
  }

  protected static String getFilledTemplatePrototypePart(final CHeaderRefinement refinement,
      final FunctionPrototype proto, final List<Pair<Port, FunctionArgument>> correspondingArguments) {
    final RefinementChecker refChecker = new RefinementChecker();
    final Map<String, Pair<CorrespondingTemplateParameterType, Object>> relatedObjects = refChecker
        .getCHeaderCorrespondingTemplateParamObject(refinement, proto, correspondingArguments);
    final List<String> evaluatedParams = new ArrayList<>();

    for (final Pair<CorrespondingTemplateParameterType, Object> p : relatedObjects.values()) {
      final Object o = p.getValue();
      final CorrespondingTemplateParameterType c = p.getKey();
      if (c == CorrespondingTemplateParameterType.NONE || c == CorrespondingTemplateParameterType.MULTIPLE) {
        templateParametersException(refinement, proto);
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
          evaluatedParams.add(FpgaCodeGenerator.getFifoDataSizeName(f));
        } else {
          templateParametersException(refinement, proto);
        }
      } else {
        // could not evaluate the related object
        templateParametersException(refinement, proto);
      }
    }

    if (evaluatedParams.isEmpty()) {
      return "";
    }

    final String values = evaluatedParams.stream().collect(Collectors.joining(","));
    return "<" + values + ">";
  }

  private static void templateParametersException(final CHeaderRefinement refinement, final FunctionPrototype proto) {
    throw new PreesmRuntimeException("FPGA codegen couldn't deduce template parameters values for function "
        + proto.getName() + " used by actor " + ((AbstractActor) refinement.getRefinementContainer()).getVertexPath());
  }

}
