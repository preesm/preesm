/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2023) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
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
