/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * This class provides common data structures for PiSDF object checkers. This class inherits from {@link PiMMSwitch}
 * only to have it available in the inheritance tree.
 * <p>
 * Important note to devs (by ahonorat): classes derived from this one are called in different contexts. Sometimes we
 * want to check the whole graph even if we have already detected some errors. So DO NOT USE {@link Stream#allMatch}
 * here because then we would not check the other faulty elements (lazy evaluation), but prefer an hand-made reduction
 * ensuring a complete evaluation. Similarly, DO NOT USE lazy boolean evaluation as {@code &&} but prefer bit-wise
 * boolean evaluation with {@code &} and bit-wise assignment with {@code &=}.
 *
 * @author ahonorat
 */
public abstract class AbstractPiSDFObjectChecker extends PiMMSwitch<Boolean> {

  /**
   * Errors which avoid to save or load a PiSDF graph.
   */
  protected Map<EObject, List<String>> fatalErrors;
  /**
   * Errors which avoid to analyze a PiSDF graph.
   */
  protected Map<EObject, List<String>> fatalAnalysisErrors;
  /**
   * Errors which avoid to execute the codegen only.
   */
  protected Map<EObject, List<String>> fatalCodegenErrors;
  /**
   * Warnings only.
   */
  protected Map<EObject, List<String>> warnings;

  protected CheckerErrorLevel throwExceptionLevel;
  protected CheckerErrorLevel loggerLevel;

  protected AbstractPiSDFObjectChecker() {
    this(CheckerErrorLevel.NONE, CheckerErrorLevel.NONE);
  }

  protected AbstractPiSDFObjectChecker(final CheckerErrorLevel throwExceptionLevel,
      final CheckerErrorLevel loggerLevel) {
    this.throwExceptionLevel = throwExceptionLevel;
    this.loggerLevel = loggerLevel;
    fatalErrors = new LinkedHashMap<>();
    fatalAnalysisErrors = new LinkedHashMap<>();
    fatalCodegenErrors = new LinkedHashMap<>();
    warnings = new LinkedHashMap<>();
  }

  /**
   * Report an error.
   *
   * @param level
   *          Level of the error (if {@link CheckerErrorLevel.NONE}, returns immediately).
   * @param obj
   *          PiSDF object related to the error.
   * @param messageFormat
   *          Message of the error.
   * @param args
   *          Arguments of the formatted message.
   */
  public void reportError(final CheckerErrorLevel level, final EObject obj, final String messageFormat,
      final Object... args) {

    final Map<EObject, List<String>> mapError = getErrorMap(level);
    if (mapError == null) {
      // then it means that the level is NONE
      return;
    }

    final String message = String.format(messageFormat, args);

    mapError.computeIfAbsent(obj, x -> new ArrayList<>()).add(message);

    if (loggerLevel.getIndex() >= level.getIndex()) {
      PreesmLogger.getLogger().log(level.getCorrespondingLoggingLevel(), message);
    }
    if (throwExceptionLevel.getIndex() >= level.getIndex()) {
      throw new PreesmRuntimeException(message);
    }

  }

  /**
   * Get a map (indexed per object) of all errors of a given level.
   *
   * @param level
   *          Only level to consider.
   * @return The corresponding map or {@code null} if none.
   */
  public Map<EObject, List<String>> getErrorMap(final CheckerErrorLevel level) {
    return switch (level) {
      case FATAL_ALL -> fatalErrors;
      case FATAL_ANALYSIS -> fatalAnalysisErrors;
      case FATAL_CODEGEN -> fatalCodegenErrors;
      case WARNING -> warnings;
      default -> null;
    };
  }

  @Override
  public Boolean defaultCase(final EObject object) {
    return Boolean.TRUE;
  }

  protected void mergeMessages(final AbstractPiSDFObjectChecker checkerToCopy) {
    // big big generic to then iterate over the three maps to merge
    final List<Pair<Map<EObject, List<String>>, Map<EObject, List<String>>>> mapPairs = new ArrayList<>();
    mapPairs.add(new Pair<>(this.fatalErrors, checkerToCopy.fatalErrors));
    mapPairs.add(new Pair<>(this.fatalAnalysisErrors, checkerToCopy.fatalAnalysisErrors));
    mapPairs.add(new Pair<>(this.fatalCodegenErrors, checkerToCopy.fatalCodegenErrors));
    mapPairs.add(new Pair<>(this.warnings, checkerToCopy.warnings));

    // we cannot use Map#putAll since we want to merge the List values of the key possibly
    // existing in both maps
    for (final Pair<Map<EObject, List<String>>, Map<EObject, List<String>>> p : mapPairs) {
      final Map<EObject, List<String>> mapToExtend = p.getKey();
      final Map<EObject, List<String>> mapToCopy = p.getValue();
      for (final Entry<EObject, List<String>> e : mapToCopy.entrySet()) {
        final EObject piObject = e.getKey();
        final List<String> messagesToCopy = e.getValue();
        final List<String> messagesToExtend = mapToExtend.computeIfAbsent(piObject, x -> new ArrayList<>());
        messagesToExtend.addAll(messagesToCopy);
      }
    }

  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    if (!fatalErrors.isEmpty()) {
      sb.append("\nFatal errors:\n");
      for (final List<String> messages : fatalErrors.values()) {
        sb.append(messages.stream().collect(Collectors.joining("\n", "", "\n")));
      }
    }
    if (!fatalAnalysisErrors.isEmpty()) {
      sb.append("\nAnalysis errors:\n");
      for (final List<String> messages : fatalAnalysisErrors.values()) {
        sb.append(messages.stream().collect(Collectors.joining("\n", "", "\n")));
      }
    }
    if (!fatalCodegenErrors.isEmpty()) {
      sb.append("\nCodegen errors:\n");
      for (final List<String> messages : fatalCodegenErrors.values()) {
        sb.append(messages.stream().collect(Collectors.joining("\n", "", "\n")));
      }
    }
    if (!warnings.isEmpty()) {
      sb.append("\nWarnings:\n");
      for (final List<String> messages : warnings.values()) {
        sb.append(messages.stream().collect(Collectors.joining("\n", "", "\n")));
      }
    }
    return sb.toString();
  }

}
