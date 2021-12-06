/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015 - 2016)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2016)
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
package org.preesm.model.pisdf.header.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLinkageSpecification;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.index.EmptyCIndex;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.check.NameCheckerC;
import org.preesm.model.pisdf.check.RefinementChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * Utility class containing method to extract prototypes from a C header file.
 *
 * @author kdesnos
 * @author cuy
 *
 */
public class HeaderParser {

  private static final String DISCARD_FUNC = "Discarded function ";

  private static final String TEMPLATE_WARNING = ". While analyzing it, "
      + "template is not in a suitable format for PREESM. "
      + "Only int and long for parameter values and 'typename FIFO_TYPE_...' are supported.";

  private HeaderParser() {
    // forbid instantiation
  }

  /**
   * This method parse a C/C++ header file and extract a set of function prototypes from it.
   *
   * @param file
   *          the {@link IFile} corresponding to the C/C++ Header file to parse.
   * @return The {@link Set} of {@link FunctionPrototype} found in the parsed C/C++ header file. Returns
   *         <code>null</code> if no valid function prototype could be found.
   */
  public static List<FunctionPrototype> parseCXXHeader(final IFile file) {
    final Map<String, String> definedMacros = new HashMap<>();
    definedMacros.put("IN", "");
    definedMacros.put("OUT", "");
    final FileContent fC = FileContent.create(file);
    final String[] includePaths = new String[0];
    final IScannerInfo info = new ScannerInfo(definedMacros, includePaths);
    final IParserLogService log = new DefaultLogService();
    final IIndex index = EmptyCIndex.INSTANCE; // or can be null
    final IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();
    final int options = ILanguage.OPTION_NO_IMAGE_LOCATIONS | ITranslationUnit.AST_SKIP_ALL_HEADERS;

    try {

      // Using: org.eclipse.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser
      final IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fC, info,
          emptyIncludes, index, options, log);
      return parseCXXHeaderRec(translationUnit);

    } catch (CoreException e) {
      throw new PreesmRuntimeException("Error while parsing header file " + file.getRawLocationURI(), e);
    }
  }

  private static List<FunctionPrototype> parseCXXHeaderRec(final IASTNode nodeAST) {
    final LinkedList<ICPPASTNamespaceDefinition> namespaceStack = new LinkedList<>();
    final LinkedList<ICPPASTTemplateDeclaration> templateStack = new LinkedList<>();
    final LinkedList<IASTDeclSpecifier> returnTypeStack = new LinkedList<>();
    final List<FunctionPrototype> resultList = new ArrayList<>();

    parseCXXHeaderRecAux(nodeAST, namespaceStack, templateStack, returnTypeStack, resultList);
    return resultList;
  }

  private static void parseCXXHeaderRecAux(final IASTNode nodeAST,
      LinkedList<ICPPASTNamespaceDefinition> namespaceStack, LinkedList<ICPPASTTemplateDeclaration> templateStack,
      LinkedList<IASTDeclSpecifier> returnTypeStack, List<FunctionPrototype> resultList) {
    if (nodeAST instanceof IASTFunctionDeclarator) {
      // BASE CASE
      // we got a function declaration !
      final IASTFunctionDeclarator funcDeclor = (IASTFunctionDeclarator) nodeAST;
      parseFunctionDeclor(funcDeclor, namespaceStack, templateStack, returnTypeStack, resultList);
    } else if (nodeAST instanceof IASTFunctionDefinition) {
      // DEEPER CASES
      // we got a function definition, let's retrieve the declaration
      final IASTFunctionDefinition funcDef = (IASTFunctionDefinition) nodeAST;
      returnTypeStack.addLast(funcDef.getDeclSpecifier());
      parseCXXHeaderRecAux(funcDef.getDeclarator(), namespaceStack, templateStack, returnTypeStack, resultList);
      returnTypeStack.removeLast();
    } else if (nodeAST instanceof IASTSimpleDeclaration) {
      // we got a simple declaration, which could start a function definition or declaration with its return type
      final IASTSimpleDeclaration simpleDeclon = (IASTSimpleDeclaration) nodeAST;
      returnTypeStack.addLast(simpleDeclon.getDeclSpecifier());
      for (final IASTDeclarator declor : simpleDeclon.getDeclarators()) {
        parseCXXHeaderRecAux(declor, namespaceStack, templateStack, returnTypeStack, resultList);
      }
      returnTypeStack.removeLast();
    } else if (nodeAST instanceof ICPPASTTemplateDeclaration) {
      // we got a template declaration, which could start a function definition or declaration
      final ICPPASTTemplateDeclaration tempDeclon = (ICPPASTTemplateDeclaration) nodeAST;
      templateStack.addLast(tempDeclon);
      parseCXXHeaderRecAux(tempDeclon.getDeclaration(), namespaceStack, templateStack, returnTypeStack, resultList);
      templateStack.removeLast();
    } else if (nodeAST instanceof ICPPASTNamespaceDefinition) {
      // we got a namespace definition, which could contain other namespaces and function definitions or declarations
      final ICPPASTNamespaceDefinition nsDef = (ICPPASTNamespaceDefinition) nodeAST;
      namespaceStack.addLast(nsDef);
      for (final IASTDeclaration declon : nsDef.getDeclarations()) {
        parseCXXHeaderRecAux(declon, namespaceStack, templateStack, returnTypeStack, resultList);
      }
      namespaceStack.removeLast();
    } else if (nodeAST instanceof ICPPASTLinkageSpecification) {
      final ICPPASTLinkageSpecification linkageSpec = (ICPPASTLinkageSpecification) nodeAST;
      // inside an extern "C" block
      for (final IASTDeclaration declon : linkageSpec.getDeclarations()) {
        parseCXXHeaderRecAux(declon, namespaceStack, templateStack, returnTypeStack, resultList);
      }
    } else if (nodeAST instanceof IASTTranslationUnit) {
      // TOP CASE
      // we got the full file, let's visit the declarations
      final IASTTranslationUnit tu = (IASTTranslationUnit) nodeAST;
      for (IASTDeclaration declon : tu.getDeclarations()) {
        parseCXXHeaderRecAux(declon, namespaceStack, templateStack, returnTypeStack, resultList);
      }
    }

  }

  private static void parseFunctionDeclor(final IASTFunctionDeclarator funcDeclor,
      LinkedList<ICPPASTNamespaceDefinition> namespaceStack, LinkedList<ICPPASTTemplateDeclaration> templateStack,
      LinkedList<IASTDeclSpecifier> returnTypeStack, List<FunctionPrototype> resultList) {
    final String rawName = funcDeclor.getName().getRawSignature().trim();

    if (returnTypeStack.size() != 1) {
      PreesmLogger.getLogger()
          .warning(() -> DISCARD_FUNC + rawName + ". While analyzing it, multiple nested return types were found.");
      return;
    }
    // this log is annoying if checked on all functions
    // else if (!returnTypeStack.getFirst().getRawSignature().contains("void")) {
    // PreesmLogger.getLogger()
    // .warning("Return type of function " + rawName + " is not void, and will not be used by PREESM.");
    // return;
    // }
    if (templateStack.size() > 1) {
      PreesmLogger.getLogger()
          .warning(() -> DISCARD_FUNC + rawName + ". While analyzing it, multiple nested templates were found.");
      return;
    }

    // manage template
    String templateSuffix = "";
    final List<String> functionTemplateNames = new ArrayList<>();
    if (templateStack.size() == 1) {
      final StringBuilder sb = new StringBuilder();
      final ICPPASTTemplateDeclaration tempDeclon = templateStack.getFirst();
      for (final ICPPASTTemplateParameter tempParam : tempDeclon.getTemplateParameters()) {
        final IASTNode[] childsParam = tempParam.getChildren();
        if (childsParam.length == 2) {
          // check if it corresponds to <..., int PARAM, ...> or <..., long PARAM = 00, ...>
          // "int/long" must be the first child node while everything else is in the second child node
          if (!(childsParam[0] instanceof ICPPASTSimpleDeclSpecifier)
              || !(childsParam[1] instanceof ICPPASTDeclarator)) {
            PreesmLogger.getLogger().warning(() -> DISCARD_FUNC + rawName + TEMPLATE_WARNING);
            return;
          }
          IASTPointerOperator[] pops = ((IASTDeclarator) childsParam[1]).getPointerOperators();
          // pointers are not allowed her of course
          if (pops.length > 0) {
            PreesmLogger.getLogger().warning(() -> DISCARD_FUNC + rawName + TEMPLATE_WARNING);
            return;
          }
          final String paramType = ((ICPPASTSimpleDeclSpecifier) childsParam[0]).getRawSignature().trim();
          final String paramName = ((ICPPASTDeclarator) childsParam[1]).getRawSignature().trim();
          // we do not support anything else then int and long static template parameters
          if (!paramType.equals("int") && !paramType.equals("long")) {
            PreesmLogger.getLogger().warning(() -> DISCARD_FUNC + rawName + TEMPLATE_WARNING);
            return;
          }
          functionTemplateNames.add(paramName);
          sb.append(paramName + ",");
        } else if (childsParam.length == 1) {
          // check if it corresponds to <..., typename NAME, ...> or <..., typename FIFO_DEPTH_NAME, ...>
          // this is allowed but supported only for fifo type and depth
          final ICPPASTName typename = (childsParam[0] instanceof ICPPASTName) ? (ICPPASTName) childsParam[0] : null;
          if (!tempParam.getRawSignature().startsWith("typename") || typename == null) {
            PreesmLogger.getLogger().warning(() -> DISCARD_FUNC + rawName + TEMPLATE_WARNING);
            return;
          } else if (!typename.getRawSignature().trim().startsWith(RefinementChecker.FIFO_TYPE_TEMPLATED_PREFIX)
              && !typename.getRawSignature().trim().startsWith(RefinementChecker.FIFO_DEPTH_TEMPLATED_PREFIX)) {
            PreesmLogger.getLogger().info(() -> "Function " + rawName + " has template parameter <" + typename
                + "> without the recommended prefix, codegen might not work.\n Allowed prefixes are: "
                + RefinementChecker.FIFO_TYPE_TEMPLATED_PREFIX + ", " + RefinementChecker.FIFO_DEPTH_TEMPLATED_PREFIX);
          }
          final String typeName = typename.getRawSignature().trim();
          functionTemplateNames.add(typeName);
          sb.append(typeName + ",");
        } else {
          PreesmLogger.getLogger().warning(() -> DISCARD_FUNC + rawName + TEMPLATE_WARNING);
          return;
        }
      }
      final String templateCall = sb.toString();
      if (!templateCall.isEmpty()) {
        templateSuffix = "<" + templateCall.substring(0, templateCall.length() - 1) + ">";
      }
    }

    final String namespacePrefix = namespaceStack.isEmpty() ? ""
        : namespaceStack.stream().map(x -> x.getName().getRawSignature().trim()).collect(Collectors.joining("::"))
            + "::";
    final String modifiedName = namespacePrefix + rawName + templateSuffix;

    // signature of CPPASTParameterDeclaration given by funcDeclor.getChildren() do not include IN and OUT anymore
    final FunctionPrototype funcProto = PiMMUserFactory.instance.createFunctionPrototype();
    funcProto.setName(modifiedName);
    if (!modifiedName.equals(rawName)) {
      funcProto.setIsCPPdefinition(true);
    }
    final EList<FunctionArgument> protoParameters = funcProto.getArguments();

    // parse function arguments
    for (final IASTNode child : funcDeclor.getChildren()) {
      if (child instanceof IASTParameterDeclaration) {
        final FunctionArgument fA = PiMMUserFactory.instance.createFunctionArgument();
        protoParameters.add(fA);

        final IASTParameterDeclaration paramDeclon = (IASTParameterDeclaration) child;
        final String rawArgType = paramDeclon.getDeclSpecifier().getRawSignature();
        final String argType = NameCheckerC.removeCVqualifiers(rawArgType);

        fA.setType(argType);
        final boolean isTemplated = argType.contains("<");
        if (isTemplated || argType.contains(":")) {
          // then the type contains a template or a namespace
          fA.setIsCPPdefinition(true);
        }
        // NOTE: we do not check that the template params of the argument types are present in the function template
        // since it is not possible to know if an argument template param is indeed a parameter to be replaced
        // or is its actual value (and in this case we do not replace it)
        final IASTDeclarator paramDeclor = paramDeclon.getDeclarator();

        final String argName = paramDeclor.getName().getRawSignature().trim();
        fA.setName(argName);

        IASTPointerOperator[] pops = paramDeclor.getPointerOperators();
        if (pops.length == 0) {
          fA.setIsConfigurationParameter(true);
        } else {
          if (pops.length > 1) {
            // we automatically converts to void* since we only have to store the first address
            fA.setType(RefinementChecker.DEFAULT_PTR_TYPE);
            PreesmLogger.getLogger()
                .warning(() -> "Argument " + argName + " of function " + rawName
                    + " is a pointer to pointer, thus it is automatically replaced by "
                    + RefinementChecker.DEFAULT_PTR_TYPE + ".");

          }

          if (pops[pops.length - 1] instanceof ICPPASTReferenceOperator) {
            // then data structure is passed by reference, only for data containers as streams or vectors
            fA.setIsPassedByReference(true);
            // and it is possible only for CPP
            fA.setIsCPPdefinition(true);
          }
        }

      }
    }

    resultList.add(funcProto);
  }

  /**
   * Filters the prototypes obtained from the parsed file to keep only the ones corresponding to the actor possible
   * initialization.
   *
   * @param actor
   *          the AbstractActor which ports we use to filter prototypes
   * @param prototypes
   *          the prototypes
   * @return the set of FunctionPrototypes corresponding to actor initialization
   */
  public static List<FunctionPrototype> filterInitPrototypesFor(final AbstractActor actor,
      final List<FunctionPrototype> prototypes) {
    final List<FunctionPrototype> result = new ArrayList<>();

    // For each function prototype proto
    for (final FunctionPrototype proto : prototypes) {
      // proto matches the initialization of actor if:
      // -it does not have more parameters than the actors configuration
      // input ports
      final List<FunctionArgument> params = new ArrayList<>(proto.getArguments());
      boolean matches = params.size() <= actor.getConfigInputPorts().size();
      // -all function parameters of proto match a configuration input
      // port of the actor (initialization function cannot read or write
      // in fifo nor write on configuration output ports)
      if (matches) {
        for (final FunctionArgument param : params) {
          if (HeaderParser.hasCorrespondingPort(param, actor.getConfigInputPorts())) {
            param.setDirection(Direction.IN);
            param.setIsConfigurationParameter(true);
          } else {
            matches = false;
            break;
          }
        }
      }

      if (matches) {
        result.add(proto);
      }
    }

    return result;
  }

  /**
   * Filters the prototypes obtained from the parsed file to keep only the ones corresponding to the actor signature
   * (ports).
   *
   * @param actor
   *          the AbstractActor which ports we use to filter prototypes
   * @param prototypes
   *          the prototypes
   * @return the set of FunctionPrototypes corresponding to actor
   */
  public static List<FunctionPrototype> filterLoopPrototypesFor(final AbstractActor actor,
      final List<FunctionPrototype> prototypes) {
    final List<FunctionPrototype> result = new ArrayList<>();

    // For each function prototype proto
    for (final FunctionPrototype proto : prototypes) {
      // proto matches the signature of actor if:
      // -it does not have more parameters than the actors ports
      final ArrayList<FunctionArgument> params = new ArrayList<>(proto.getArguments());
      boolean matches = params.size() <= (actor.getDataInputPorts().size() + actor.getDataOutputPorts().size()
          + actor.getConfigInputPorts().size() + actor.getConfigOutputPorts().size());

      // Check that all proto parameters can be matched with a port
      final List<Port> allPorts = new ArrayList<>();
      allPorts.addAll(actor.getDataInputPorts());
      allPorts.addAll(actor.getDataOutputPorts());
      allPorts.addAll(actor.getConfigInputPorts());
      allPorts.addAll(actor.getConfigOutputPorts());
      for (final FunctionArgument param : proto.getArguments()) {
        matches &= HeaderParser.hasCorrespondingPort(param, allPorts);
      }

      // -each of the data input and output ports of the actor matches one
      // of the parameters of proto
      if (matches) {
        matches = searchForDataInputPort(actor, params, matches);
      }
      if (matches) {
        matches = searchForDataOutputPort(actor, params, matches);
      }
      // -each of the configuration output ports of the actor matches one
      // of the parameters of proto
      if (matches) {
        matches = searchForConfigOutputPort(actor, params, matches);
      }
      // -all other function parameters of proto match a configuration
      // input port of the actor
      if (matches) {
        searchForConfigInputPort(actor, params);
      }
      if (matches) {
        result.add(proto);
      }
    }

    return result;
  }

  private static void searchForConfigInputPort(final AbstractActor actor, final ArrayList<FunctionArgument> params) {
    for (final FunctionArgument param : params) {
      if (HeaderParser.hasCorrespondingPort(param, actor.getConfigInputPorts())) {
        param.setDirection(Direction.IN);
        param.setIsConfigurationParameter(true);
      }
    }
  }

  private static boolean searchForConfigOutputPort(final AbstractActor actor, final ArrayList<FunctionArgument> params,
      boolean matches) {
    for (final Port p : actor.getConfigOutputPorts()) {
      final FunctionArgument param = HeaderParser.getCorrespondingFunctionParameter(p, params);
      if (param != null) {
        param.setDirection(Direction.OUT);
        param.setIsConfigurationParameter(true);
        params.remove(param);
      } else {
        matches = false;
        break;
      }
    }
    return matches;
  }

  private static boolean searchForDataOutputPort(final AbstractActor actor, final ArrayList<FunctionArgument> params,
      boolean matches) {
    for (final Port p : actor.getDataOutputPorts()) {
      final FunctionArgument param = HeaderParser.getCorrespondingFunctionParameter(p, params);
      if (param != null) {
        param.setDirection(Direction.OUT);
        param.setIsConfigurationParameter(false);
        params.remove(param);
      } else {
        matches = false;
        break;
      }
    }
    return matches;
  }

  private static boolean searchForDataInputPort(final AbstractActor actor, final ArrayList<FunctionArgument> params,
      boolean matches) {
    for (final Port p : actor.getDataInputPorts()) {
      final FunctionArgument param = HeaderParser.getCorrespondingFunctionParameter(p, params);
      if (param != null) {
        param.setDirection(Direction.IN);
        param.setIsConfigurationParameter(false);
        params.remove(param);
      } else {
        matches = false;
        break;
      }
    }
    return matches;
  }

  /**
   * Filters the prototypes obtained from the parsed file to keep only the ones corresponding to possible
   * initializations.
   *
   * @param prototypes
   *          the prototypes
   * @return the set of FunctionPrototypes corresponding to initialization
   */
  public static List<FunctionPrototype> filterInitPrototypes(final List<FunctionPrototype> prototypes) {
    final List<FunctionPrototype> result = new ArrayList<>();

    // For each function prototype proto check that the prototype has no
    // input or output buffers (i.e. parameters with a pointer type)
    for (final FunctionPrototype proto : prototypes) {
      boolean allParams = true;
      for (final FunctionArgument param : proto.getArguments()) {
        if (param.isIsConfigurationParameter()) {
          param.setDirection(Direction.IN);
        } else {
          allParams = false;
          break;
        }
      }

      if (allParams) {
        result.add(proto);
      }
    }

    return result;
  }

  /**
   * Filters the prototypes obtained from the parsed file to keep only the ones corresponding to possible
   * initializations of delays (only one output buffer).
   *
   * @param prototypes
   *          the prototypes
   * @return the set of FunctionPrototypes corresponding to initialization
   */
  public static List<FunctionPrototype> filterInitBufferPrototypes(final List<FunctionPrototype> prototypes) {
    final List<FunctionPrototype> result = new ArrayList<>();

    // For each function prototype proto check that the prototype has no
    // input or output buffers (i.e. parameters with a pointer type)
    for (final FunctionPrototype proto : prototypes) {
      int nbBuffers = 0;
      for (final FunctionArgument param : proto.getArguments()) {
        if (param.isIsConfigurationParameter()) {
          param.setDirection(Direction.IN);
        } else {
          nbBuffers += 1;
          param.setDirection(Direction.OUT);
        }
      }

      if (nbBuffers == 1) {
        result.add(proto);
      }
    }

    return result;
  }

  /**
   * Gets the corresponding function parameter.
   *
   * @param p
   *          the p
   * @param params
   *          the params
   * @return the corresponding function parameter
   */
  private static FunctionArgument getCorrespondingFunctionParameter(final Port p, final List<FunctionArgument> params) {
    for (final FunctionArgument param : params) {
      if (p.getName().equals(param.getName())) {
        return param;
      }
    }
    return null;
  }

  /**
   * Checks for corresponding port.
   *
   * @param f
   *          the f
   * @param ports
   *          the ports
   * @return true, if successful
   */
  private static boolean hasCorrespondingPort(final FunctionArgument f, final List<? extends Port> ports) {
    for (final Port p : ports) {
      if (p.getName().equals(f.getName())) {
        return true;
      }
    }
    return false;
  }

}
