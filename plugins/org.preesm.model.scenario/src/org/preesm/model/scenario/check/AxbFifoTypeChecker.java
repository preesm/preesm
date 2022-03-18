package org.preesm.model.scenario.check;

import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * This class checks if all fifo types of the algorithm correspond to valid AxB types.
 *
 * @author hmiomand
 */
public class AxbFifoTypeChecker {

  private AxbFifoTypeChecker() {
    // forbid instantiation
  }

  private static final String REGEX_AXB_PREFIX = "^AXB_";
  private static final String REGEX_FXP_INFIX  = "FXP<";
  private static final String REGEX_CFP_INFIX  = "CFP<";
  private static final String REGEX_AXB_SUFIX  = ">$";

  private static final String REGEX_OPT_SIGN = "([[1][-][\\+]]\\.)?";
  private static final String REGEX_NUM      = "-?[0-9]+";
  private static final String REGEX_POS_NUM  = "[0-9]+";
  private static final String REGEX_SEP      = ",";

  private static final String SIGN_UNDEF = "SIGN_UNDEF";
  private static final String SIGN_POS   = "SIGN_POS";
  private static final String SIGN_NEG   = "SIGN_NEG";

  private static final String SIGNBIT_ERROR  = "' has a signbit issue.";
  private static final String FXP_NAME_ERROR = "' is not a valid name for an AXB with FXP format.";
  private static final String CFP_NAME_ERROR = "' is not a valid name for an AXB with CFP format.";

  public static boolean isAxbType(String typeName) {

    typeName = typeName.replaceAll("\\s", "");
    // Only checking for FXP and CFP, not for UQ yet.
    // Also checks if the format is correct (<S.I.F> or <I.F> for FXP, <S.E.M.B> for CFP

    return isAxbFxp(typeName) || isAxbCfp(typeName);
  }

  public static boolean isAxbFxp(String typeName) {
    // return typeName.matches("^AXB_FXP<([[1][\\-][\\+]]\\.)?-?[0-9]+\\.-?[0-9]+>$");
    return typeName.matches(
        REGEX_AXB_PREFIX + REGEX_FXP_INFIX + REGEX_OPT_SIGN + REGEX_NUM + REGEX_SEP + REGEX_NUM + REGEX_AXB_SUFIX);
  }

  public static boolean isAxbCfp(String typeName) {
    // return typeName.matches("^AXB_CFP<([[1][\\-][\\+]]\\.)?[0-9]+\\.[0-9]+\\.-?[0-9]+>$");
    return typeName.matches(REGEX_AXB_PREFIX + REGEX_CFP_INFIX + REGEX_OPT_SIGN + REGEX_POS_NUM + REGEX_SEP
        + REGEX_POS_NUM + REGEX_SEP + REGEX_POS_NUM + REGEX_AXB_SUFIX);
  }

  public static long getAxbTokenSize(String typeName) {

    typeName = typeName.replaceAll("\\s", "");

    if (isAxbFxp(typeName)) {
      return getAxbFxpTokenSize(typeName);
    } else if (isAxbCfp(typeName)) {
      return getAxbCfpTokenSize(typeName);
    }

    throw new PreesmRuntimeException("'" + typeName + "' is not a valid name for an AXB.");
  }

  private static long getAxbFxpTokenSize(String typeName) {

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

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

    if (typeSize <= 0) {
      throw new PreesmRuntimeException("'" + typeName + FXP_NAME_ERROR);
    }

    return typeSize;
  }

  public static String getAxbSignBit(String typeName) {

    typeName = typeName.replaceAll("\\s", "");

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
    final String[] values = format.split(REGEX_SEP);

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
        throw new PreesmRuntimeException("'" + typeName + SIGNBIT_ERROR);
      }
    } else {
      throw new PreesmRuntimeException("'" + typeName + SIGNBIT_ERROR);
    }
  }

  public static long getAxbFxpIntegerSize(String typeName) {

    typeName = typeName.replaceAll("\\s", "");

    if (!isAxbFxp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + FXP_NAME_ERROR);
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

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

    typeName = typeName.replaceAll("\\s", "");

    if (!isAxbFxp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + FXP_NAME_ERROR);
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

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

  private static long getAxbCfpTokenSize(String typeName) {

    typeName = typeName.replaceAll("\\s", "");

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

    // Format has to be <S.E.M.B>
    long typeSize = (long) Integer.parseInt(values[1]) + Integer.parseInt(values[2]);

    if (values[0].equals("1")) {
      typeSize++;
    }

    return typeSize;
  }

  private static String getAxbCfpSignBit(String typeName) {

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

    if (values[0].equals("+")) {
      return SIGN_POS;
    } else if (values[0].equals("-")) {
      return SIGN_NEG;
    } else if (values[0].equals("1")) {
      return SIGN_UNDEF;
    } else {
      throw new PreesmRuntimeException("'" + typeName + SIGNBIT_ERROR);
    }
  }

  public static long getAxbCfpExponentSize(String typeName) {

    if (!isAxbCfp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + CFP_NAME_ERROR);
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

    return Integer.parseInt(values[1]);
  }

  public static long getAxbCfpMantissaSize(String typeName) {

    if (!isAxbCfp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + CFP_NAME_ERROR);
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

    return Integer.parseInt(values[2]);
  }

  public static long getAxbCfpBias(String typeName) {

    typeName = typeName.replaceAll("\\s", "");

    if (!isAxbCfp(typeName)) {
      throw new PreesmRuntimeException("'" + typeName + CFP_NAME_ERROR);
    }

    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);

    return Integer.parseInt(values[3]);
  }
}
