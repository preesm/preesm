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
 * here because then we would not check the other faulty elements, but prefer an hand-made reduction ensuring a complete
 * evaluation. Similarly, DO NOT USE lazy boolean evaluation as {@code &&} but prefer force boolean evaluation with
 * {@code &=}.
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
  protected Map<EObject, List<String>> recoverableErrors;
  /**
   * Warnings, harmful for codegen only.
   */
  protected Map<EObject, List<String>> onlyWarnings;

  protected CheckerErrorLevel throwExceptionLevel;
  protected CheckerErrorLevel loggerLevel;

  public AbstractPiSDFObjectChecker() {
    this(CheckerErrorLevel.NONE, CheckerErrorLevel.NONE);
  }

  public AbstractPiSDFObjectChecker(final CheckerErrorLevel throwExceptionLevel, final CheckerErrorLevel loggerLevel) {
    this.throwExceptionLevel = throwExceptionLevel;
    this.loggerLevel = loggerLevel;
    fatalErrors = new LinkedHashMap<>();
    recoverableErrors = new LinkedHashMap<>();
    onlyWarnings = new LinkedHashMap<>();
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
    Map<EObject, List<String>> mapError = null;
    switch (level) {
      case FATAL:
        mapError = fatalErrors;
        break;
      case RECOVERABLE:
        mapError = recoverableErrors;
        break;
      case WARNING:
        mapError = onlyWarnings;
        break;
      default:
        break;
    }
    return mapError;
  }

  @Override
  public Boolean defaultCase(final EObject object) {
    return Boolean.TRUE;
  }

  protected void mergeMessages(final AbstractPiSDFObjectChecker checkerToCopy) {
    // big big generic to then iterate over the three maps to merge
    final List<Pair<Map<EObject, List<String>>, Map<EObject, List<String>>>> mapPairs = new ArrayList<>();
    mapPairs.add(new Pair<>(this.fatalErrors, checkerToCopy.fatalErrors));
    mapPairs.add(new Pair<>(this.recoverableErrors, checkerToCopy.recoverableErrors));
    mapPairs.add(new Pair<>(this.onlyWarnings, checkerToCopy.onlyWarnings));

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
    if (!recoverableErrors.isEmpty()) {
      sb.append("\nRecoverable errors:\n");
      for (final List<String> messages : recoverableErrors.values()) {
        sb.append(messages.stream().collect(Collectors.joining("\n", "", "\n")));
      }
    }
    if (!onlyWarnings.isEmpty()) {
      sb.append("\nWarnings:\n");
      for (final List<String> messages : onlyWarnings.values()) {
        sb.append(messages.stream().collect(Collectors.joining("\n", "", "\n")));
      }
    }
    return sb.toString();
  }

}
