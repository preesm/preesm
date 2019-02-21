package org.preesm.commons;

import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * This class provides normalized integer Strings : it appends it with leading 0;
 * 
 * @author ahonorat
 *
 */
public class IntegerName {

  /**
   * Conversion format with leading zero.
   */
  private final String format;

  /**
   * Builds IntegerName.
   * 
   * @param maxValue
   *          Maximum expected value, used to compute the maximum number of digits. It has to be positive (if 0,
   *          processed as 1), otherwise {@link PreesmRuntimeException} is thrown.
   */
  public IntegerName(long maxValue) {
    if (maxValue == 0) {
      maxValue = 1;
    } else if (maxValue < 0) {
      throw new PreesmRuntimeException("Attempting to convert a negative number into a printable index.");
    }
    int nbDigits = 1 + (int) Math.log10(maxValue); // performs floor automatically
    format = "%0" + nbDigits + "d";
  }

  /**
   * Format a number with the correct number of leading zero (depends on this instance maximum value).
   * 
   * @param value
   *          The number to be formatted.
   * @return The number as a String with leading zeros according to the maximum value.
   */
  public String toString(long value) {
    return String.format(format, value);
  }

}
