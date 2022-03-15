package org.preesm.model.scenario.check;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.DefaultTypeSizes;

/**
 * This class check if all fifos of the algorithm have a known data type size.
 * 
 * @author ahonorat
 */
public class FifoTypeChecker {

  private FifoTypeChecker() {
    // forbid instantiation
  }

  /**
   * Get all the fifo types for which the size is not specified in the scenario.
   * 
   * @param scenario
   *          To be checked.
   * @return A list of missing fifo type sizes. Empty list if no algorithm specified.
   */
  public static Set<String> getMissingFifoTypeSizes(final Scenario scenario) {
    final Set<String> result = new LinkedHashSet<>();
    final PiGraph graph = scenario.getAlgorithm();
    if (graph == null) {
      return result;
    }
    for (final Fifo f : graph.getAllFifos()) {
      final String typeName = f.getType();
      // Search for typeName in the Scenario

      if (scenario.getSimulationInfo().getDataTypes().get(typeName) != null) {
        // If typeName is known in the Scenario
        continue;
      }

      final long typeSize = DefaultTypeSizes.getInstance().getTypeSize(typeName);

      if (typeSize != -1) {
        // If typeName matches a default known type
        scenario.getSimulationInfo().getDataTypes().put(typeName, typeSize);
        PreesmLogger.getLogger().warning(() -> "A default size of " + typeSize + " bits was used for '" + typeName
            + "' for this Workflow execution.");
      } else if (isAxb(typeName)) {
        scenario.getSimulationInfo().getDataTypes().put(typeName, getAxbTokenSize(typeName));
        PreesmLogger.getLogger().warning(() -> "A size of " + getAxbTokenSize(typeName) + " bits was used for '"
            + typeName + "' for this Workflow execution.");
      } else {
        // If typeName is completely unknown
        result.add("'" + typeName + "'");
        PreesmLogger.getLogger().warning(() -> "Unknown type: " + typeName + ", in Fifo: " + f.getId());
      }

    }
    return result;
  }

  /**
   * Check if all the fifo types have a size defined in the scenario.
   * 
   * @param scenario
   *          To be checked.
   * @throws PreesmRuntimeException
   *           If it is not the case.
   */
  public static void checkMissingFifoTypeSizes(final Scenario scenario) {
    final Set<String> missingDataTypeSizes = getMissingFifoTypeSizes(scenario);
    if (!missingDataTypeSizes.isEmpty()) {
      final String formattedMissingDataTypeSizes = missingDataTypeSizes.stream().collect(Collectors.joining(", "));
      throw new PreesmRuntimeException(
          "Cannot find the size of the following fifo types: " + formattedMissingDataTypeSizes + ".");
    }
  }

  public static boolean isAxb(String typeName) {

    // Only checking for FXP and CFP, not for UQ yet.
    // Also checks if the format is correct (<S.I.F> or <I.F> for FXP, <S.E.M.B> for CFP
    // return
    // typeName.matches("^AXB_(FXP<([[1][\\-][\\+]]\\.)?[0-9]+\\.[0-9]+>|CFP<([[1][\\-][\\+]]\\.)?[0-9]+\\.[0-9]+\\.[0-9]+>)$");

    return isAxbFxp(typeName) || isAxbCfp(typeName);
  }

  public static boolean isAxbFxp(String typeName) {
    return typeName.matches("^AXB_FXP<([[1][\\-][\\+]]\\.)?[0-9]+\\.[0-9]+>$");
  }

  public static boolean isAxbCfp(String typeName) {
    return typeName.matches("^AXB_CFP<([[1][\\-][\\+]]\\.)?[0-9]+\\.[0-9]+\\.[0-9]+>$");
  }

  public static long getAxbTokenSize(String typeName) {

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    // final String[] values = format.split("([[\\+][\\-]])?\\.");
    final String[] values = format.split("\\.");

    if (typeName.matches("^AXB_FXP.*$")) {

      long typeSize = 0L;

      // If the format is <I.F>
      if (values.length == 2) {
        typeSize = (long) Integer.parseInt(values[0]) + Integer.parseInt(values[1]);
      }

      // If the format is <S.I.F>
      if (values.length == 3) {
        typeSize = (long) Integer.parseInt(values[1]) + Integer.parseInt(values[2]);
        if (values[0].equals("1")) {
          typeSize++;
        }
      }

      return typeSize;

    } else if (typeName.matches("^AXB_CFP.*$")) {

      // Format has to be <S.E.M.B>
      long typeSize = (long) Integer.parseInt(values[1]) + Integer.parseInt(values[2]);

      if (values[0].equals("1")) {
        typeSize++;
      }

      return typeSize;
    }

    throw new PreesmRuntimeException("'" + typeName + "' is not a valid name for an AXB.");
  }

  private static final String SIGN_UNDEF = "SIGN_UNDEF";
  private static final String SIGN_POS   = "SIGN_POS";
  private static final String SIGN_NEG   = "SIGN_NEG";

  public static String getAxbSignBit(String typeName) {

    if (!(isAxbFxp(typeName) || isAxbCfp(typeName))) {
      throw new PreesmRuntimeException("'" + typeName + "' does not have a sign bit.");
    }

    if (isAxbFxp(typeName)) {
      return getAxbFxpSignBit(typeName);
    }

    if (isAxbCfp(typeName)) {
      return getAxbCfpSignBit(typeName);
    }

    return SIGN_UNDEF;
  }

  private static String getAxbFxpSignBit(String typeName) {

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split("\\.");

    if (values.length == 2) {
      // If the format is <I.F>
      return SIGN_UNDEF;
    } else if (values.length == 3) {
      // If the format is <S.I.F>
      if (values[0].equals("+")) {
        return SIGN_POS;
      } else if (values[0].equals("-")) {
        return SIGN_NEG;
      } else if (values[0].equals("1")) {
        return SIGN_UNDEF;
      } else {
        throw new PreesmRuntimeException("'" + typeName + "' has a signbit issue.");
      }
    } else {
      throw new PreesmRuntimeException("'" + typeName + "' has a signbit issue.");
    }
  }

  public static long getAxbFxpIntegerSize(String typeName) {

    if (!isAxbFxp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + "' is not a valid name for an AXB with FXP format.");
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split("\\.");

    // If the format is <I.F>
    if (values.length == 2) {
      return Integer.parseInt(values[0]);
    }

    // If the format is <S.I.F>
    if (values.length == 3) {
      return Integer.parseInt(values[1]);
    }

    throw new PreesmRuntimeException("Could not parse the format of " + typeName);
  }

  public static long getAxbFxpFractionalSize(String typeName) {

    if (!isAxbFxp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + "' is not a valid name for an AXB with FXP format.");
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split("\\.");

    // If the format is <I.F>
    if (values.length == 2) {
      return Integer.parseInt(values[1]);
    }

    // If the format is <S.I.F>
    if (values.length == 3) {
      return Integer.parseInt(values[2]);
    }

    throw new PreesmRuntimeException("Could not parse the format of " + typeName);
  }

  private static String getAxbCfpSignBit(String typeName) {

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split("\\.");

    if (values[0].equals("+")) {
      return SIGN_POS;
    } else if (values[0].equals("-")) {
      return SIGN_NEG;
    } else if (values[0].equals("1")) {
      return SIGN_UNDEF;
    } else {
      throw new PreesmRuntimeException("'" + typeName + "' has a signbit issue.");
    }
  }

  public static long getAxbCfpExponentSize(String typeName) {
    if (!isAxbCfp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + "' is not a valid name for an AXB with CFP format.");
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split("\\.");

    return Integer.parseInt(values[1]);
  }

  public static long getAxbCfpMantissaSize(String typeName) {
    if (!isAxbCfp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + "' is not a valid name for an AXB with CFP format.");
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split("\\.");

    return Integer.parseInt(values[2]);
  }

  public static long getAxbCfpBias(String typeName) {
    if (!isAxbCfp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + "' is not a valid name for an AXB with CFP format.");
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split("\\.");

    return Integer.parseInt(values[3]);
  }

}
