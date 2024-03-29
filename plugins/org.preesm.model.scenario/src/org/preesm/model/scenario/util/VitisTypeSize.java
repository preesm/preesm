package org.preesm.model.scenario.util;

import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * This class checks if all fifo types of the algorithm correspond to valid AxB types.
 *
 * @author hmiomand
 */
public class VitisTypeSize {

  private VitisTypeSize() {
    // forbid instantiation
  }

  private static final String REGEX_VITIS_PREFIX      = "^ap_";
  private static final String REGEX_VITIS_INT_INFIX   = "u?int<";
  private static final String REGEX_VITIS_FIXED_INFIX = "u?fixed<";
  private static final String REGEX_VITIS_SUFIX       = ">$";

  private static final String REGEX_NUM     = "-?\\d+";
  private static final String REGEX_POS_NUM = "\\d+";
  private static final String REGEX_SEP     = ",";

  private static final String REGEX_VITIS_QTZ = "(AP_RND|AP_RND_ZERO|AP_RND_MIN_INF|AP_RND_INF|AP_RND_CONV"
      + "|AP_TRN|AP_TRN_ZERO)";
  private static final String REGEX_VITIS_OVF = "(AP_SAT|AP_SAT_ZERO|AP_SAT_SYM|AP_WRAP|AP_WRAP_SM)";

  public static boolean isVitisType(String typeName) {
    typeName = typeName.replaceAll("\\s", "");

    return isVitisApInt(typeName) || isVitisApFixed(typeName);
  }

  private static boolean isVitisApInt(String typeName) {
    return typeName.matches(REGEX_VITIS_PREFIX + REGEX_VITIS_INT_INFIX + REGEX_POS_NUM + REGEX_VITIS_SUFIX);
  }

  private static boolean isVitisApFixed(String typeName) {
    return typeName.matches(REGEX_VITIS_PREFIX + REGEX_VITIS_FIXED_INFIX + REGEX_POS_NUM + REGEX_SEP + REGEX_NUM + "((,"
        + REGEX_VITIS_QTZ + ")(," + REGEX_VITIS_OVF + ")?)?" + REGEX_VITIS_SUFIX);
  }

  public static long getVitisTokenSize(String typeName) {
    typeName = typeName.replaceAll("\\s", "");

    if (isVitisApInt(typeName)) {
      return getVitisApIntTokenSize(typeName);
    }
    if (isVitisApFixed(typeName)) {
      return getVitisApFixedTokenSize(typeName);
    }

    throw new PreesmRuntimeException("'" + typeName + "' is not a valid name.");
  }

  private static long getVitisApIntTokenSize(String typeName) {
    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final long typeSize = Integer.parseInt(format);

    if (typeSize <= 0) {
      return DefaultTypeSizes.UNKNOWN_TYPE;
    }

    return typeSize;
  }

  private static long getVitisApFixedTokenSize(String typeName) {
    final String format = typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">", typeName.indexOf("<")));
    final String[] values = format.split(REGEX_SEP);
    final long typeSize = Integer.parseInt(values[0]);

    if (typeSize <= 0) {
      return DefaultTypeSizes.UNKNOWN_TYPE;
    }

    return typeSize;
  }

}
