/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2022 - 2024)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2022)
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
package org.preesm.model.pisdf.check;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;

/**
 * Class to check different properties of the Refinements of the Actors of a PiGraph. Entry point is the
 * checkRefinements method. Actors with invalid refinements are kept in several sets
 *
 * @author cguy
 *
 */
public class RefinementChecker extends AbstractPiSDFObjectChecker {

  /** All accepted header file extensions. */
  private static final String[] acceptedHeaderExtensions = { "h", "hpp", "hxx", "h++", "hh", "H" };

  /** In templates, the FIFO_TYPE can be replaced automatically only if starting with this prefix. */
  public static final String FIFO_TYPE_TEMPLATED_PREFIX = "FIFO_TYPE_";

  /** In templates, the FIFO_DEPTH can be replaced automatically only if starting with this prefix. */
  public static final String FIFO_DEPTH_TEMPLATED_PREFIX = "FIFO_DEPTH_";

  /** If the user wants a FIFO type being a pointer, then the default replacement type is the following. */
  public static final String DEFAULT_PTR_TYPE = "void*";

  private static final String INT8_T   = "int8_t";
  private static final String UINT8_T  = "uint8_t";
  private static final String INT16_T  = "int16_t";
  private static final String UINT16_T = "uint16_t";
  private static final String INT32_T  = "int32_t";
  private static final String UINT32_T = "uint32_t";
  private static final String INT64_T  = "int64_t";
  private static final String UINT64_T = "uint64_t";

  /*
   * Map holding synonyms of identical types to avoid displaying a warning e.g. when one side of the fifo uses unsigned
   * char while the other uses uint8_t.
   */
  private static final Map<String,
      String> typeSynonymsMap = Map.ofEntries(new AbstractMap.SimpleEntry<>(INT8_T, INT8_T),
          new AbstractMap.SimpleEntry<>("char", INT8_T), new AbstractMap.SimpleEntry<>(UINT8_T, UINT8_T),
          new AbstractMap.SimpleEntry<>("unsigned char", UINT8_T), new AbstractMap.SimpleEntry<>(INT16_T, INT16_T),
          new AbstractMap.SimpleEntry<>("short", INT16_T), new AbstractMap.SimpleEntry<>(UINT16_T, UINT16_T),
          new AbstractMap.SimpleEntry<>("unsigned short", UINT16_T), new AbstractMap.SimpleEntry<>(INT32_T, INT32_T),
          new AbstractMap.SimpleEntry<>("int", INT32_T), new AbstractMap.SimpleEntry<>(UINT32_T, UINT32_T),
          new AbstractMap.SimpleEntry<>("unsigned int", UINT32_T), new AbstractMap.SimpleEntry<>(INT64_T, INT64_T),
          new AbstractMap.SimpleEntry<>("long", INT64_T), new AbstractMap.SimpleEntry<>("long long", INT64_T),
          new AbstractMap.SimpleEntry<>(UINT64_T, UINT64_T), new AbstractMap.SimpleEntry<>("unsigned long", UINT64_T),
          new AbstractMap.SimpleEntry<>("unsigned long long", UINT64_T));

  /**
   * Check if the given C/C++ header file extension is supported.
   *
   * @param extension
   *          The extension to check (as "hxx" for "MyHeader.hxx").
   * @return Whether or not the file extension is supported.
   */
  public static boolean isAsupportedHeaderFileExtension(final String extension) {
    return Arrays.asList(acceptedHeaderExtensions).stream().anyMatch(x -> x.equals(extension));
  }

  public static List<String> getAcceptedHeaderExtensions() {
    return Collections.unmodifiableList(Arrays.asList(acceptedHeaderExtensions));
  }

  /**
   * Check if the templated type of a FunctionArgument is an hls stream.
   *
   * @param argType
   *          The templated argument type to check.
   * @return The parameter name of the stream type and the parameter name of the stream depth (optional). {@code null}
   *         if not a valid hls stream type.
   */
  public static Pair<String, String> isHlsStreamTemplated(final String argType) {
    final String argTypeWithoutWhiteSpaces = argType.trim();
    final String regex1 = "^hls::stream<((" + FIFO_TYPE_TEMPLATED_PREFIX + ")?([\\w\\s])+(<[\\w,]+>)?)>$";
    final Pattern pattern1 = Pattern.compile(regex1);
    final Matcher matcher1 = pattern1.matcher(argTypeWithoutWhiteSpaces);
    if (matcher1.find()) {
      final String match = matcher1.group(1);
      return new Pair<>(match, null);
    }

    final String regex2 = "^hls::stream<((" + FIFO_TYPE_TEMPLATED_PREFIX + ")?([\\w\\s])+(<[\\w,]+>)?),\\s?(("
        + FIFO_TYPE_TEMPLATED_PREFIX + ")?\\w+)>$";
    final Pattern pattern2 = Pattern.compile(regex2);
    final Matcher matcher2 = pattern2.matcher(argTypeWithoutWhiteSpaces);
    if (matcher2.find()) {
      // first group is the full type param name and second is only its optional prefix
      final String matchType = matcher2.group(1);
      // fifth group is the full depth param name and fourth is only its optional prefix
      final String matchDepth = matcher2.group(5);
      return new Pair<>(matchType, matchDepth);
    }
    return null;
  }

  /**
   * Instantiates a new refinement checker.
   */
  public RefinementChecker() {
    super();
  }

  /**
   * Instantiates a new refinement checker.
   *
   * @param throwExceptionLevel
   *          The maximum level of error throwing exceptions.
   * @param loggerLevel
   *          The maximum level of error generating logs.
   */
  public RefinementChecker(final CheckerErrorLevel throwExceptionLevel, final CheckerErrorLevel loggerLevel) {
    super(throwExceptionLevel, loggerLevel);
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    boolean ok = true;
    for (final AbstractActor aa : graph.getActors()) {
      if (aa instanceof Actor) {
        ok &= doSwitch(aa);
      } else if (aa instanceof final PiGraph piGraph) {
        // PiGraph as direct children means that a reconnection already occurred
        // but we don't want a recursive pattern and it needs a different check
        // Indeed, the SubgraphReconnector already checked the ports so we only need to check the fifo types.
        ok &= checkReconnectedSubGraphFifoTypes(piGraph);
      }
    }
    return ok;
  }

  @Override
  public Boolean caseActor(final Actor a) {
    final Refinement refinement = a.getRefinement();
    boolean validity = false;
    if ((refinement != null) && (refinement.getFilePath() != null) && !refinement.getFilePath().isEmpty()) {
      validity = checkRefinementExtension(a) && checkRefinementValidity(a);
      if (validity) {
        final List<Pair<Port, Port>> correspondingPorts = getPiGraphRefinementCorrespondingPorts(a);
        final Pair<List<Pair<Port, FunctionArgument>>,
            List<Pair<Port, FunctionArgument>>> correspondingArguments = getCHeaderRefinementCorrespondingArguments(a);

        validity &= checkRefinementPorts(a, correspondingPorts, correspondingArguments);
        validity &= checkRefinementFifoTypes(a, correspondingPorts, correspondingArguments);
        validity &= checkRefinementTemplateParams(a, correspondingArguments);
        // continue recursive visit if hierarchical?
      }
    } else {
      reportError(CheckerErrorLevel.FATAL_CODEGEN, a, "Actor [%s] has no refinement set.", a.getVertexPath());
    }

    return validity;
  }

  /**
   * Check the file extension of the Refinement of an Actor
   *
   * <p>
   * Precondition: a has a non-null refinement with a non-null and non-empty filePath.
   * </p>
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @return true if the file extension of the refinement of a is a valid one, false otherwise
   */
  private boolean checkRefinementExtension(final Actor a) {
    final IPath path = new Path(a.getRefinement().getFilePath());
    final String fileExtension = path.getFileExtension();
    if (!fileExtension.equals("idl") && !fileExtension.equals("pi")
        && !isAsupportedHeaderFileExtension(fileExtension)) {
      // File pointed by the refinement of a does not have a valid extension
      reportError(CheckerErrorLevel.FATAL_ANALYSIS, a, "Actor [%s] has an unrecognized refinement file extension.",
          a.getVertexPath());
      return false;
    }
    return true;
  }

  /**
   * Check the existence of the file of the Refinement of an Actor
   *
   * <p>
   * Precondition: a has a non-null refinement with a non-null and non-empty filePath.
   * </p>
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @return true if the file exists, false otherwise
   */
  private boolean checkRefinementValidity(final Actor a) {
    if (a.getRefinement().isGenerated()) {
      return true;
    }
    final IPath path = new Path(a.getRefinement().getFilePath());
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    if (!file.exists()) {
      // File pointed by the refinement does not exist
      reportError(CheckerErrorLevel.FATAL_ANALYSIS, a,
          "Actor [%s] has a refinement file missing in the file system: '%s'.", a.getVertexPath(),
          a.getRefinement().getFilePath());
      return false;
    }
    return true;
  }

  /**
   * Check if all the ports of the actor are present in the refinement, and vice versa.
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @param correspondingPorts
   *          The corresponding ports if PiGraph refinement (null otherwise).
   * @param correspondingArguments
   *          The corresponding arguments if PiCHeader refinement (null otherwise).
   * @return true if all ports are corresponding, false otherwise
   */
  private boolean checkRefinementPorts(final Actor a, final List<Pair<Port, Port>> correspondingPorts,
      final Pair<List<Pair<Port, FunctionArgument>>, List<Pair<Port, FunctionArgument>>> correspondingArguments) {
    boolean validity = true;

    if (a.isHierarchical()) {
      for (final Pair<Port, Port> correspondingPort : correspondingPorts) {
        final Port topPort = correspondingPort.getKey();
        final Port subPort = correspondingPort.getValue();

        if (topPort != null && subPort == null) {
          validity = false;
          reportError(CheckerErrorLevel.FATAL_CODEGEN, a, "Port [%s:%s] is not present in refinement.", a.getName(),
              topPort.getName());
        } else if (topPort == null && subPort != null) {
          validity = false;
          reportError(CheckerErrorLevel.FATAL_CODEGEN, a, "Port [%s:%s] is only present in refinement.", a.getName(),
              subPort.getName());
        }

      }
    } else if (a.getRefinement() instanceof CHeaderRefinement) {
      final List<Pair<Port, FunctionArgument>> initMapArgs = correspondingArguments.getKey();
      if (initMapArgs != null) {
        validity &= checkCHeaderRefinementPrototypePorts(a, initMapArgs, true);
      }
      final List<Pair<Port, FunctionArgument>> loopMapArgs = correspondingArguments.getValue();
      if (loopMapArgs != null) {
        validity &= checkCHeaderRefinementPrototypePorts(a, loopMapArgs, false);
      }
    }

    return validity;
  }

  private boolean checkCHeaderRefinementPrototypePorts(final Actor a,
      List<Pair<Port, FunctionArgument>> correspondingArguments, final boolean initProto) {
    boolean validity = true;
    for (final Pair<Port, FunctionArgument> correspondingArgument : correspondingArguments) {
      final Port topPort = correspondingArgument.getKey();
      final FunctionArgument fa = correspondingArgument.getValue();

      // ConfigInputPort are not required to be in the C/C++ refinement
      // if init prototype, any kind of port is not required
      if (!(topPort instanceof ConfigInputPort) && !initProto && topPort != null && fa == null) {
        validity = false;
        reportError(CheckerErrorLevel.FATAL_CODEGEN, a, "Port [%s:%s] is not present in refinement.", a.getName(),
            topPort.getName());
      } else if (topPort == null && fa != null) {
        validity = false;
        reportError(CheckerErrorLevel.FATAL_CODEGEN, a, "Port [%s:%s] is only present in refinement.", a.getName(),
            fa.getName());
      }
    }

    return validity;
  }

  /**
   * Check if all the fifo connected to the actor and its refinement have the same type.
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @param correspondingPorts
   *          The corresponding ports if PiGraph refinement (null otherwise).
   * @param correspondingArguments
   *          The corresponding arguments if CHeader refinement (null otherwise).
   * @return true if all (present) fifo types are corresponding, false otherwise
   */
  private boolean checkRefinementFifoTypes(final Actor a, final List<Pair<Port, Port>> correspondingPorts,
      final Pair<List<Pair<Port, FunctionArgument>>, List<Pair<Port, FunctionArgument>>> correspondingArguments) {

    if (a.isHierarchical()) {
      return checkRefinementFifoTypesHierarchical(a, correspondingPorts);
    }
    if (a.getRefinement() instanceof CHeaderRefinement) {
      return checkRefinementFifoTypesCRefinement(a, correspondingArguments);
    }

    return true;
  }

  private boolean checkRefinementFifoTypesHierarchical(final Actor a, final List<Pair<Port, Port>> correspondingPorts) {
    boolean validity = true;

    for (final Pair<Port, Port> correspondingPort : correspondingPorts) {
      final Port topPort = correspondingPort.getKey();
      final Port subPort = correspondingPort.getValue();

      if (!(topPort instanceof DataPort) || !(subPort instanceof DataPort)) {
        continue;
      }

      final Fifo topFifo = ((DataPort) topPort).getFifo();
      final Fifo subFifo = ((DataPort) subPort).getFifo();

      if (topFifo == null || subFifo == null) {
        continue;
      }

      if (!areTypesIdentical(topFifo.getType(), subFifo.getType())) {
        validity = false;
        reportError(CheckerErrorLevel.FATAL_CODEGEN, a,
            "Port [%s:%s] has a different fifo type than in its inner self [%s]: '%s' vs '%s'.", a.getName(),
            topPort.getName(), a.getSubGraph().getName(), topFifo.getType(), subFifo.getType());
      }
    }

    return validity;
  }

  private boolean checkRefinementFifoTypesCRefinement(final Actor a,
      final Pair<List<Pair<Port, FunctionArgument>>, List<Pair<Port, FunctionArgument>>> correspondingArguments) {

    boolean validity = true;

    // we check only the loop prototype since the init one should not have any fifo connected to it
    final List<Pair<Port, FunctionArgument>> loopMapArgs = correspondingArguments.getValue();
    if (loopMapArgs == null) {
      return true;
    }

    for (final Pair<Port, FunctionArgument> correspondingArgument : loopMapArgs) {
      final Port topPort = correspondingArgument.getKey();
      final FunctionArgument fa = correspondingArgument.getValue();

      if (!(topPort instanceof final DataPort topDataPort) || topDataPort.getFifo() == null || fa == null) {
        continue;
      }

      final String topFifoType = topDataPort.getFifo().getType();
      final String funcArgType = fa.getType();

      if (!areTypesIdentical(topFifoType, funcArgType) && isHlsStreamTemplated(funcArgType) == null) {
        validity = false;
        reportError(CheckerErrorLevel.FATAL_CODEGEN, a,
            "Port [%s:%s] has a different fifo type than in its C/C++ refinement: '%s' vs '%s'.", a.getName(),
            topPort.getName(), topFifoType, funcArgType);
        // check here the container type? If hls::stream for FPGA,
        // the templated FIFO type is either a parameter inferred by PREESM or direct value check along with the
        // actor
      }
      if (DEFAULT_PTR_TYPE.equals(funcArgType)) {
        reportError(CheckerErrorLevel.WARNING, a,
            "Port [%s:%s] is a %s pointer in its C/C++ refinement, "
                + "this is discouraged since outside of the memory allocated by PREESM.",
            a.getName(), topPort.getName(), DEFAULT_PTR_TYPE);
      }
    }

    return validity;
  }

  private boolean areTypesIdentical(final String type1, final String type2) {

    final String str1 = typeSynonymsMap.getOrDefault(type1, type1);
    final String str2 = typeSynonymsMap.getOrDefault(type2, type2);

    return str1.equals(str2);
  }

  /**
   * Check if the template parameters of an actor with C/C++ refinement can be found in its containing actor or graph..
   *
   * @param actor
   *          The actor to check.
   * @param correspondingArguments
   *          List of ports corresponding to function arguments.
   * @return Whether or not all function template parameters are correct.
   */
  private boolean checkRefinementTemplateParams(final Actor actor,
      final Pair<List<Pair<Port, FunctionArgument>>, List<Pair<Port, FunctionArgument>>> correspondingArguments) {
    final Refinement ref = actor.getRefinement();
    if (ref instanceof final CHeaderRefinement cref) {
      boolean validity = true;
      if (cref.getInitPrototype() != null) {
        validity &= checkRefinementTemplateParamsAux(actor, correspondingArguments.getKey(), cref,
            cref.getInitPrototype());
      }
      if (cref.getLoopPrototype() != null) {
        validity &= checkRefinementTemplateParamsAux(actor, correspondingArguments.getValue(), cref,
            cref.getLoopPrototype());
      }
      return validity;
    }
    return true;
  }

  private boolean checkRefinementTemplateParamsAux(final Actor actor,
      final List<Pair<Port, FunctionArgument>> correspondingArguments, final CHeaderRefinement cref,
      final FunctionPrototype proto) {
    boolean validity = true;
    if (proto != null) {
      final Map<String,
          Pair<CorrespondingTemplateParameterType,
              Object>> correspondingObjects = getCHeaderCorrespondingTemplateParamObject(cref, proto,
                  correspondingArguments);
      // check if the stream params are used multiple times
      final List<String> badlyUsedStreamParams = new ArrayList<>();
      correspondingObjects.forEach((x, y) -> {
        if (y.getKey() == CorrespondingTemplateParameterType.MULTIPLE) {
          badlyUsedStreamParams.add(x);
        }
      });
      if (!badlyUsedStreamParams.isEmpty()) {
        reportError(CheckerErrorLevel.FATAL_CODEGEN, actor,
            "Templated refinement of actor [%s] has parameters '%s' found multiple times for different usages.",
            actor.getVertexPath(), badlyUsedStreamParams.stream().collect(Collectors.joining(",")));
        validity = false;
      }
      // check if the function template params are all related to an object
      final List<String> lonelyParams = new ArrayList<>();
      correspondingObjects.forEach((x, y) -> {
        if (y.getKey() == CorrespondingTemplateParameterType.NONE) {
          lonelyParams.add(x);
        }
      });

      if (!lonelyParams.isEmpty()) {
        reportError(CheckerErrorLevel.FATAL_CODEGEN, actor,
            "Templated refinement of actor [%s] has unknown parameters '%s'.", actor.getVertexPath(),
            lonelyParams.stream().collect(Collectors.joining(",")));
        validity = false;
      }
    }
    return validity;
  }

  /**
   * While checking a template parameter, we can categorize it depending on the object it refers to.
   *
   * @author ahonorat
   */
  public enum CorrespondingTemplateParameterType {
    ACTOR_PARAM, GRAPH_PARAM, DEFAULT_PARAM, FIFO_TYPE, FIFO_DEPTH, MULTIPLE, NONE;
  }

  /**
   * Associates templated parameter names with their related Parameter or DataPort (if fifo info).
   *
   * @param refinement
   *          The refinement to consider.
   * @param proto
   *          The refinement prototype to consider (init or loop).
   * @param correspondingArguments
   *          List of ports corresponding to function arguments.
   * @return A map of function template parameter names to pair with parameter category as key and related element as
   *         value ({@code null} if not found).
   */
  public Map<String, Pair<CorrespondingTemplateParameterType, Object>> getCHeaderCorrespondingTemplateParamObject(
      final CHeaderRefinement refinement, final FunctionPrototype proto,
      final List<Pair<Port, FunctionArgument>> correspondingArguments) {
    final Map<String, Pair<CorrespondingTemplateParameterType, Object>> result = new LinkedHashMap<>();
    // split the template parameters
    final String rawFunctionName = proto.getName();
    final int indexStartTemplate = rawFunctionName.indexOf('<');
    final int indexEndTemplate = rawFunctionName.lastIndexOf('>');
    if (indexStartTemplate < 0 || indexEndTemplate < 0 || indexEndTemplate < indexStartTemplate) {
      // not templated
      return result;
    }
    // this weird way of passing the refinement instead of the actor is needed to handle
    // both Actor and DelayActor whose closest common ancestor is RefinementContainer
    // (which can hold a PiGraph or a CHeaderRefinement)
    // refinement container always is an actor for now
    final AbstractActor containerActor = (AbstractActor) refinement.getRefinementContainer();
    final PiGraph containerGraph = containerActor.getContainingPiGraph();
    final String prefix = getActorNamePrefix(containerActor);

    // get the hls templated fifo (not always needed, but factorized for memoization)
    final Map<String, Pair<CorrespondingTemplateParameterType,
        List<FunctionArgument>>> hlsStreamParamsToFA = getAllHlsStreamTemplateParamNames(proto);

    // get the template names
    final String onlyTemplatePart = rawFunctionName.substring(indexStartTemplate + 1, indexEndTemplate);
    final String[] rawTemplateSubparts = onlyTemplatePart.split(",");
    for (final String rawTemplateSubpart : rawTemplateSubparts) {
      // we split again in case of a default value
      final String[] equalSubparts = rawTemplateSubpart.split("=");
      final String paramName = equalSubparts[0].trim();

      Object relatedObject = null;
      CorrespondingTemplateParameterType relatedObjectCat = CorrespondingTemplateParameterType.NONE;
      // look for a parameter of the actor first
      if (containerActor instanceof final DelayActor delayActor) {
        for (final Parameter inputParam : delayActor.getInputParameters()) {
          if (inputParam.getName().equals(prefix + paramName)) {
            relatedObject = inputParam;
            relatedObjectCat = CorrespondingTemplateParameterType.ACTOR_PARAM;
            break;
          }
        }
      } else {
        for (final ConfigInputPort cip : containerActor.getConfigInputPorts()) {
          if (cip.getName().equals(paramName)) {
            final ISetter setter = cip.getIncomingDependency().getSetter();
            if (setter instanceof Parameter) {
              relatedObject = setter;
              relatedObjectCat = CorrespondingTemplateParameterType.ACTOR_PARAM;
              break;
            }
          }
        }
      }
      // look for graph parameters if not found
      if (relatedObject == null) {
        for (final Parameter paramG : containerGraph.getParameters()) {
          if (paramG.getName().equals(prefix + paramName)) {
            relatedObject = paramG;
            relatedObjectCat = CorrespondingTemplateParameterType.GRAPH_PARAM;
            reportError(CheckerErrorLevel.WARNING, refinement,
                "In templated refinement of actor [%s], "
                    + "template parameter '%s' has been found in the original graph but not in the actor.",
                containerActor.getVertexPath(), paramName);
            break;
          }
        }
      }
      // look for FIFO infos if not found in parameters
      if (relatedObject == null) {
        final Pair<CorrespondingTemplateParameterType,
            List<FunctionArgument>> correspondingFAs = hlsStreamParamsToFA.get(paramName);
        if (correspondingFAs != null) {
          if (containerActor instanceof final DelayActor delayActor) {
            // in this case, there is only one fifo and one corresponding function argument
            relatedObject = delayActor.getLinkedDelay().getContainingFifo();
            relatedObjectCat = correspondingFAs.getKey();
          } else {
            // then we got at least one matching fifo, but we need to retrieve it
            for (final FunctionArgument fa : correspondingFAs.getValue()) {
              for (final Pair<Port, FunctionArgument> portToFA : correspondingArguments) {
                if (fa.equals(portToFA.getValue())) {
                  final Port relatedPort = portToFA.getKey();
                  if (relatedPort instanceof final DataPort dataPort) {
                    final Fifo relatedFifo = dataPort.getFifo();
                    if (relatedFifo != null) {
                      relatedObject = relatedFifo;
                      relatedObjectCat = correspondingFAs.getKey();
                    }
                  }
                  break;
                }
              }
            }
          }
        }
      }
      // if param not found but default value is provided, we take it
      if (relatedObject == null && equalSubparts.length > 1) {
        relatedObject = equalSubparts[1].trim();
        relatedObjectCat = CorrespondingTemplateParameterType.DEFAULT_PARAM;
        reportError(CheckerErrorLevel.WARNING, refinement,
            "In templated refinement of actor [%s], "
                + "template parameter '%s' has not been found but default value (%s) was provided.",
            containerActor.getVertexPath(), paramName, equalSubparts[1]);
      }

      result.put(paramName, new Pair<>(relatedObjectCat, relatedObject));
    }

    // as we know here the template type of the prototype arguments,
    // should we also check that the stream fifo types are correct?
    // however, even if the stream type is not equal to the fifo type, it might be a macro ... and thus equal in the end

    return result;
  }

  /**
   * After a flattening transformation {@link org.preesm.model.pisdf.statictools.PiSDFFlattener}, inner element names
   * have been prefixed with the top graph names, so this function retrieves the prefix. This is risky if other
   * transformations are coded but not having the same prefix convention.
   *
   * @param aa
   *          Current actor (after transformation).
   * @return Prefix of current actor name obtained by comparison with the original actor name.
   */
  public static String getActorNamePrefix(final AbstractActor aa) {
    final AbstractActor aaOri = PreesmCopyTracker.getOriginalSource(aa);
    final int offsetPrefix = aa.getName().lastIndexOf(aaOri.getName());
    String prefix = "";
    if (offsetPrefix > 0) {
      prefix = aa.getName().substring(0, offsetPrefix);
    }
    return prefix;
  }

  private static Map<String, Pair<CorrespondingTemplateParameterType, List<FunctionArgument>>>
      getAllHlsStreamTemplateParamNames(final FunctionPrototype proto) {
    final Map<String, Pair<CorrespondingTemplateParameterType, List<FunctionArgument>>> result = new LinkedHashMap<>();
    for (final FunctionArgument fa : proto.getArguments()) {
      final Pair<String, String> hlsStream = isHlsStreamTemplated(fa.getType());

      if (hlsStream == null) {
        continue;
      }

      final String key = hlsStream.getKey();
      final String value = hlsStream.getValue();

      if (key != null) {
        final Pair<CorrespondingTemplateParameterType, List<FunctionArgument>> p = result.computeIfAbsent(key,
            k -> new Pair<>(CorrespondingTemplateParameterType.FIFO_TYPE, new ArrayList<>()));
        p.getValue().add(fa);
        if (p.getKey() != CorrespondingTemplateParameterType.FIFO_TYPE) {
          // a single parameter is used for different use cases
          result.replace(key, new Pair<>(CorrespondingTemplateParameterType.MULTIPLE, p.getValue()));
        }
      }

      if (value != null) {
        final Pair<CorrespondingTemplateParameterType, List<FunctionArgument>> p = result.computeIfAbsent(value,
            k -> new Pair<>(CorrespondingTemplateParameterType.FIFO_DEPTH, new ArrayList<>()));
        p.getValue().add(fa);
        if (p.getKey() != CorrespondingTemplateParameterType.FIFO_DEPTH) {
          // a single parameter is used for different use cases
          result.replace(value, new Pair<>(CorrespondingTemplateParameterType.MULTIPLE, p.getValue()));
        }
      }

    }
    return result;
  }

  private boolean checkReconnectedSubGraphFifoTypes(final PiGraph graph) {
    boolean validity = true;
    for (final AbstractActor aa : graph.getActors()) {
      if (aa instanceof final InterfaceActor ia) {
        final DataPort iaPort = ia.getDataPort();
        final DataPort graphPort = ia.getGraphPort();
        if (iaPort == null || graphPort == null) {
          // already reported elsewhere
          continue;
        }
        final Fifo iaFifo = iaPort.getFifo(); // fifo sub graph
        final Fifo graphFifo = graphPort.getFifo(); // fifo top graph
        if (iaFifo != null && graphFifo != null && !iaFifo.getType().equals(graphFifo.getType())) {
          validity = false;
          reportError(CheckerErrorLevel.FATAL_CODEGEN, graph,
              "Port [%s:%s] has a different fifo type than in its inner Interface: '%s' vs '%s'.", graph.getName(),
              graphPort.getName(), graphFifo.getType(), iaFifo.getType());
        }
      }
    }

    return validity;
  }

  /**
   * Fetch the {@link FunctionArgument} related to each port of an actor.
   *
   * @param a
   *          List of Pair with {@link Port} as value and {@link FunctionArgument} as key.
   * @return First key is for init refinement, second one for loop refinement. Each entry of the list contains as value
   *         the related {@link FunctionArgument} if found, or {@code null} for missing and extra elements ({@link Port}
   *         or {@link FunctionArgument}). Returns {@code null} if not a {@link CHeaderRefinement}.
   */
  public static Pair<List<Pair<Port, FunctionArgument>>, List<Pair<Port, FunctionArgument>>>
      getCHeaderRefinementCorrespondingArguments(final Actor a) {
    if (!(a.getRefinement() instanceof final CHeaderRefinement ref)) {
      return new Pair<>(Collections.emptyList(), Collections.emptyList());
    }

    List<Pair<Port, FunctionArgument>> initResult = null;
    final FunctionPrototype fpInit = ref.getInitPrototype();
    if (fpInit != null) {
      initResult = getCHeaderRefinementPrototypeCorrespondingArguments(a, fpInit);
    }

    List<Pair<Port, FunctionArgument>> loopResult = null;
    final FunctionPrototype fpLoop = ref.getLoopPrototype();
    if (fpLoop != null) {
      loopResult = getCHeaderRefinementPrototypeCorrespondingArguments(a, fpLoop);
    }

    return new Pair<>(initResult, loopResult);
  }

  /**
   * Create a list of port linked to function arguments.
   *
   * @param a
   *          The actor to consider.
   * @param proto
   *          The function prototype to consider of the given actor.
   * @return A list of entries with port as key and function argument as value (one or the other might be {@code null}).
   */
  public static List<Pair<Port, FunctionArgument>>
      getCHeaderRefinementPrototypeCorrespondingArguments(final AbstractActor a, final FunctionPrototype proto) {
    final List<FunctionArgument> noRefCorrespondingFoundYet = new ArrayList<>(proto.getArguments());
    final List<Pair<Port, FunctionArgument>> result = new ArrayList<>();

    for (final Port p1 : a.getAllPorts()) {
      FunctionArgument correspondingFA = null;
      for (final FunctionArgument fa : proto.getArguments()) {
        if (p1.getName().equals(fa.getName())) {
          correspondingFA = fa;
          break;
        }
      }
      noRefCorrespondingFoundYet.remove(correspondingFA);
      result.add(new Pair<>(p1, correspondingFA));
    }

    // Function arguments in refinement without top corresponding port
    for (final FunctionArgument fa : noRefCorrespondingFoundYet) {
      result.add(new Pair<>(null, fa));
    }

    return result;
  }

  private static List<Pair<Port, Port>> getPiGraphRefinementCorrespondingPorts(final Actor a) {
    if (!a.isHierarchical()) {
      return Collections.emptyList();
    }

    final PiGraph subGraph = a.getSubGraph();
    // if it is reconnected, there is no point to do this, this case should not happen however
    if (subGraph.getContainingPiGraph() != null) {
      return new ArrayList<>();
    }

    final List<Port> noRefCorrespondingPort = new ArrayList<>();
    // Then we try to perform a sort of reconnection ...
    // but with the subgraph interface actors instead of the subgraph ports (except for Config Input ports).
    for (final AbstractActor aa : subGraph.getActors()) {
      if (aa instanceof final InterfaceActor ia) {
        final Port iaPort = ia.getDataPort();
        if (iaPort != null) {
          noRefCorrespondingPort.add(iaPort);
        }
      }
    }
    subGraph.getConfigInputInterfaces().stream().filter(cii -> cii.getGraphPort() != null)
        .forEach(cii -> noRefCorrespondingPort.add(cii.getGraphPort()));

    final List<Pair<Port, Port>> result = new ArrayList<>();
    // now we can try to match the ports
    for (final Port p1 : a.getAllPorts()) {
      Port correspondingPort = null;
      for (final Port p2 : noRefCorrespondingPort) {
        if (p1.getName().equals(p2.getName())) {
          correspondingPort = p2;
          break;
        }
      }
      noRefCorrespondingPort.remove(correspondingPort);
      result.add(new Pair<>(p1, correspondingPort));
    }

    // Ports in refinement without top corresponding port
    noRefCorrespondingPort.forEach(p2 -> result.add(new Pair<>(null, p2)));

    return result;
  }

}
