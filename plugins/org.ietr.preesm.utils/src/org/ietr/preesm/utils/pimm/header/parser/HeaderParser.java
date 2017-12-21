/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015 - 2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015 - 2016)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2016)
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
package org.ietr.preesm.utils.pimm.header.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.Port;

// TODO: Auto-generated Javadoc
/**
 * Utility class containing method to extract prototypes from a C header file.
 *
 * @author kdesnos
 * @author cuy
 *
 */
public class HeaderParser {

  /**
   * This Regular expression is used to identify and separate the different elements composing a function prototype parameter. It should be noted that before
   * being processed by this regular expression, all consecutive whitespace were reduced to a single one. <br>
   * <br>
   * Explanation:
   * <ul>
   * <li><code>(IN|OUT)?</code>: <b>Group 1 - Direction</b> Check if either character string <code>IN</code> xor <code>OUT</code> are present, or not, at the
   * beginning of the parameter declaration.</li>
   * <li><code>\\s?</code>: Match 0 or 1 whitespace.</li>
   * <li><code>([^\\*]+)</code>: <b>Group 2 - Type</b> Match as many characters different from '*' as possible (1 to infinite). For the pattern to match, the
   * remaining of the regular expression must still be matchable. Which means that the "as many as possible" clause holds as long as the remaining patterns are
   * also matched.</li>
   * <li><code>\\s?</code>: Match 0 or 1 whitespace.</li>
   * <li><code>(\\*+(?:\\s?const)?)?</code>: <b>Group 3 - Pointers</b> Match, if possible, a string '*' of length 1 to infinite. Also match optional
   * <code> const</code> option for pointer.</li>
   * <li><code>\\s</code>: Match exactly 1 whitespace.</li>
   * <li><code>([\\S&&[^\\[\\]]]+)</code>: <b>Group 4 - Name</b></li> Match a string of non-whitespace characters (except '[' or ']') of length 1 to infinite.
   * <li><code>(\\[(\\d|\\]\\[)*\\])?</code>: <b>Group 5 - Array?</b></li> Match, if possible, a string of opening and closing square brackets '[]', possibly
   * containing digits.
   * </ul>
   */
  private static final String PARAM_BREAK_DOWN_REGEX = "(IN|OUT)?\\s?([^\\*]+)\\s?(\\*+(?:\\s?const)?)?\\s([\\S&&[^\\[\\]]]+)(\\[(\\d|\\]\\[)*\\])?\\s?";

  /**
   * This method parse a C header file and extract a set of function prototypes from it.
   *
   * @param file
   *          the {@link IFile} corresponding to the C Header file to parse.
   * @return The {@link Set} of {@link FunctionPrototype} found in the parsed C header file. Returns <code>null</code> if no valid function prototype could be
   *         found.
   */
  public static List<FunctionPrototype> parseHeader(final IFile file) {
    // Read the file
    List<FunctionPrototype> result = null;
    if (file != null) {
      try {
        // Read the file content
        String fileContent = HeaderParser.readFileContent(file);

        // Filter unwanted content
        fileContent = HeaderParser.filterHeaderFileContent(fileContent);

        // Identify and isolate prototypes in the remaining code
        final List<String> prototypes = HeaderParser.extractPrototypeStrings(fileContent);

        // Create the FunctionPrototypes
        result = HeaderParser.createFunctionPrototypes(prototypes);
      } catch (CoreException | IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * Given a {@link List} of C function prototypes represented as {@link String}, this function create the {@link List} of corresponding
   * {@link FunctionPrototype}.
   *
   * @param prototypes
   *          {@link List} of C function prototypes, as produced by the {@link #extractPrototypeStrings(String)} method.
   * @return a {@link List} of {@link FunctionPrototype}, or <code>null</code> if something went wrong during the parsing.
   */
  protected static List<FunctionPrototype> createFunctionPrototypes(final List<String> prototypes) {
    List<FunctionPrototype> result;
    result = new ArrayList<>();

    // Unique RegEx to separate the return type, the function name
    // and the list of parameters
    final Pattern pattern = Pattern.compile("(.+?)\\s(\\S+?)\\s?\\((.*?)\\)");
    for (final String prototypeString : prototypes) {
      final FunctionPrototype funcProto = PiMMUserFactory.instance.createFunctionPrototype();

      // Get the name
      Matcher matcher = pattern.matcher(prototypeString);
      if (matcher.matches()) { // apply the matcher
        funcProto.setName(matcher.group(2));
      }

      // Get the parameters (if any)
      // A new array list must be created because the list
      // returned by Arrays.asList cannot be modified (in
      // particular, no element can be removed from it).
      final List<String> parameters = new ArrayList<>(Arrays.asList(matcher.group(3).split("\\s?,\\s?")));
      // Remove empty match (is the function has no parameter)
      parameters.remove("");
      parameters.remove(" ");

      final Pattern paramPattern = Pattern.compile(HeaderParser.PARAM_BREAK_DOWN_REGEX);
      // Procces parameters one by one
      for (final String param : parameters) {
        final FunctionParameter fp = PiMMUserFactory.instance.createFunctionParameter();
        matcher = paramPattern.matcher(param);
        final boolean matched = matcher.matches();
        if (matched) { // Apply the matcher (if possible)
          // Get the parameter name
          fp.setName(matcher.group(4));
          // Get the parameter type
          fp.setType(matcher.group(2));
          // Check the direction (if any)
          if (matcher.group(1) != null) {
            if (matcher.group(1).equals("IN")) {
              fp.setDirection(Direction.IN);

            }
            if (matcher.group(1).equals("OUT")) {
              fp.setDirection(Direction.OUT);
            }
          }
          if ((matcher.group(3) == null) && (matcher.group(5) == null)) {
            fp.setIsConfigurationParameter(true);
          }
          final EList<FunctionParameter> protoParameters = funcProto.getParameters();
          protoParameters.add(fp);
        }
      }
      result.add(funcProto);
    }
    return result;
  }

  /**
   * Separate the {@link String} corresponding to the function prototypes from the filtered file content.
   *
   * @param fileContent
   *          the filtered file content provided by {@link #filterHeaderFileContent(String)}.
   * @return the {@link List} of {@link String} corresponding to the function prototypes.
   */
  protected static List<String> extractPrototypeStrings(final String fileContent) {
    // The remaining code is a single line containing only C code
    // (enum, struct, prototypes, inline functions, ..)
    final Pattern pattern = Pattern.compile("[^;}]([^;}{]*?\\(.*?\\))\\s?[;]");
    final Matcher matcher = pattern.matcher(fileContent);
    final List<String> prototypes = new ArrayList<>();
    boolean containsPrototype;
    do {
      containsPrototype = matcher.find();
      if (containsPrototype) {
        prototypes.add(matcher.group(1));
      }
    } while (containsPrototype);
    return prototypes;
  }

  /**
   * Filter the content of an header file as follows :
   * <ul>
   * <li>Filter comments between <code>/* * /</code></li>
   * <li>Filter comments after //</li>
   * <li>Filter all pre-processing commands</li>
   * <li>Replace new lines and multiple spaces with a single space</li>
   * <li>Make sure there always is a space before and after each group of * this will ease type identification during prototype identification.</li>
   * </ul>
   *
   * @param fileContent
   *          the content to filter as a {@link String}.
   * @return the filtered content as a {@link String}
   */
  protected static String filterHeaderFileContent(String fileContent) {
    // Order of the filter is important !
    // Comments must be removed before pre-processing commands and
    // end of lines.

    // Filter comments between /* */
    Pattern pattern = Pattern.compile("(/\\*)(.*?)(\\*/)", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(fileContent);
    fileContent = matcher.replaceAll("");

    // Filter comments after //
    pattern = Pattern.compile("(//)(.*?\\n)", Pattern.DOTALL);
    matcher = pattern.matcher(fileContent);
    fileContent = matcher.replaceAll("");

    // Filter all pre-processing (
    pattern = Pattern.compile("^\\s*#\\s*(([^\\\\]+?)((\\\\$[^\\\\]+?)*?$))", Pattern.MULTILINE | Pattern.DOTALL);
    matcher = pattern.matcher(fileContent);
    fileContent = matcher.replaceAll("");

    // Replace new lines and multiple spaces with a single space
    pattern = Pattern.compile("\\s+", Pattern.MULTILINE);
    matcher = pattern.matcher(fileContent);
    fileContent = matcher.replaceAll(" ");

    // Make sure there always is a space before and after each
    // group of * this will ease type identification during
    // prototype identification.
    // 1. remove all spaces around "*"
    pattern = Pattern.compile("\\s?\\*\\s?");
    matcher = pattern.matcher(fileContent);
    fileContent = matcher.replaceAll("*");
    // 2. add space around each groupe of *
    pattern = Pattern.compile(":?\\*+");
    matcher = pattern.matcher(fileContent);
    fileContent = matcher.replaceAll(" $0 ");
    return fileContent;
  }

  /**
   * Read the content of an {@link IFile} and returns it as a {@link String}.
   *
   * @param file
   *          the {@link IFile} to read.
   * @return the content of the {@link IFile} as a {@link String}.
   *
   * @throws CoreException
   *           {@link IFile#getContents()}
   * @throws IOException
   *           {@link InputStream#read()}
   */
  protected static String readFileContent(final IFile file) throws CoreException, IOException {
    String fileString = null;
    try (Scanner scanner = new Scanner(file.getContents())) {
      fileString = scanner.useDelimiter("\\A").next();
    }
    return fileString;
  }

  /**
   * Filters the prototypes obtained from the parsed file to keep only the ones corresponding to the actor possible initialization.
   *
   * @param actor
   *          the AbstractActor which ports we use to filter prototypes
   * @param prototypes
   *          the prototypes
   * @return the set of FunctionPrototypes corresponding to actor initialization
   */
  public static List<FunctionPrototype> filterInitPrototypesFor(final AbstractActor actor, final List<FunctionPrototype> prototypes) {
    final List<FunctionPrototype> result = new ArrayList<>();

    // For each function prototype proto
    for (final FunctionPrototype proto : prototypes) {
      // proto matches the initialization of actor if:
      // -it does not have more parameters than the actors configuration
      // input ports
      final List<FunctionParameter> params = new ArrayList<>(proto.getParameters());
      boolean matches = params.size() <= actor.getConfigInputPorts().size();
      // -all function parameters of proto match a configuration input
      // port of the actor (initialization function cannot read or write
      // in fifo nor write on configuration output ports)
      if (matches) {
        for (final FunctionParameter param : params) {
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
   * Filters the prototypes obtained from the parsed file to keep only the ones corresponding to the actor signature (ports).
   *
   * @param actor
   *          the AbstractActor which ports we use to filter prototypes
   * @param prototypes
   *          the prototypes
   * @return the set of FunctionPrototypes corresponding to actor
   */
  public static List<FunctionPrototype> filterLoopPrototypesFor(final AbstractActor actor, final List<FunctionPrototype> prototypes) {
    final List<FunctionPrototype> result = new ArrayList<>();

    // For each function prototype proto
    for (final FunctionPrototype proto : prototypes) {
      // proto matches the signature of actor if:
      // -it does not have more parameters than the actors ports
      final ArrayList<FunctionParameter> params = new ArrayList<>(proto.getParameters());
      boolean matches = params.size() <= (actor.getDataInputPorts().size() + actor.getDataOutputPorts().size() + actor.getConfigInputPorts().size()
          + actor.getConfigOutputPorts().size());

      // Check that all proto parameters can be matched with a port
      final List<Port> allPorts = new ArrayList<>();
      allPorts.addAll(actor.getDataInputPorts());
      allPorts.addAll(actor.getDataOutputPorts());
      allPorts.addAll(actor.getConfigInputPorts());
      allPorts.addAll(actor.getConfigOutputPorts());
      for (final FunctionParameter param : proto.getParameters()) {
        matches &= HeaderParser.hasCorrespondingPort(param, allPorts);
      }

      // -each of the data input and output ports of the actor matches one
      // of the parameters of proto
      if (matches) {
        for (final Port p : actor.getDataInputPorts()) {
          final FunctionParameter param = HeaderParser.getCorrespondingFunctionParameter(p, params);
          if (param != null) {
            param.setDirection(Direction.IN);
            param.setIsConfigurationParameter(false);
            params.remove(param);
          } else {
            matches = false;
            break;
          }
        }
      }
      if (matches) {
        for (final Port p : actor.getDataOutputPorts()) {
          final FunctionParameter param = HeaderParser.getCorrespondingFunctionParameter(p, params);
          if (param != null) {
            param.setDirection(Direction.OUT);
            param.setIsConfigurationParameter(false);
            params.remove(param);
          } else {
            matches = false;
            break;
          }
        }
      }
      // -each of the configuration output ports of the actor matches one
      // of the parameters of proto
      if (matches) {
        for (final Port p : actor.getConfigOutputPorts()) {
          final FunctionParameter param = HeaderParser.getCorrespondingFunctionParameter(p, params);
          if (param != null) {
            param.setDirection(Direction.OUT);
            param.setIsConfigurationParameter(true);
            params.remove(param);
          } else {
            matches = false;
            break;
          }
        }
      }
      // -all other function parameters of proto match a configuration
      // input port of the actor
      if (matches) {
        for (final FunctionParameter param : params) {
          if (HeaderParser.hasCorrespondingPort(param, actor.getConfigInputPorts())) {
            param.setDirection(Direction.IN);
            param.setIsConfigurationParameter(true);
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
   * Filters the prototypes obtained from the parsed file to keep only the ones corresponding to possible initializations.
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
      final List<FunctionParameter> params = new ArrayList<>(proto.getParameters());
      boolean allParams = true;
      for (final FunctionParameter param : params) {
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
   * Gets the corresponding function parameter.
   *
   * @param p
   *          the p
   * @param params
   *          the params
   * @return the corresponding function parameter
   */
  private static FunctionParameter getCorrespondingFunctionParameter(final Port p, final List<FunctionParameter> params) {
    for (final FunctionParameter param : params) {
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
  private static boolean hasCorrespondingPort(final FunctionParameter f, final List<? extends Port> ports) {
    for (final Port p : ports) {
      if (p.getName().equals(f.getName())) {
        return true;
      }
    }
    return false;
  }

}
