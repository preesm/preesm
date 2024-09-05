/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Mickael Dardaillon [mickael.dardaillon@insa-rennes.fr] (2021 - 2024)
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

package org.preesm.codegen.fpga;

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
      if (o instanceof final Parameter param) {
        final Long value = param.getExpression().evaluateAsLong();
        evaluatedParams.add(value.toString());
      } else if (o instanceof final String s) {
        evaluatedParams.add(s);
      } else if (o instanceof final Fifo f) {
        if (c == CorrespondingTemplateParameterType.FIFO_TYPE) {
          evaluatedParams.add(f.getType());
        } else if (c == CorrespondingTemplateParameterType.FIFO_DEPTH) {
          evaluatedParams.add(FpgaCodeGenerator.getFifoStreamSizeNameMacro(f));
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
