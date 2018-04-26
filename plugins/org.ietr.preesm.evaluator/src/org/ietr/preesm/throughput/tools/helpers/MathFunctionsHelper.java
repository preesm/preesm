/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.ietr.preesm.throughput.tools.helpers;

/**
 *
 * @author hderoui
 *
 *         Math functions helper class contains math functions that do not exists in the java.lang.Math library
 */
public abstract class MathFunctionsHelper {

  /**
   * computes the Greatest Common Divisor (GCD) of two doubles
   *
   * @param a
   *          double number
   * @param b
   *          double number
   * @return the gcd of a and b
   */
  public static double gcd(double a, double b) {
    while (b > 0) {
      final double temp = b;
      b = a % b; // % is remainder
      a = temp;
    }
    return a;
  }

  /**
   * computes the Greatest Common Divisor (GCD) of a vector of doubles
   *
   * @param input
   *          an array of doubles
   * @return the gcd
   */
  public static double gcd(final double[] input) {
    double result = input[0];
    for (int i = 1; i < input.length; i++) {
      result = MathFunctionsHelper.gcd(result, input[i]);
    }
    return result;
  }

  /**
   * computes the Least Common Multiple (LCM) of two doubles
   *
   * @param a
   *          double number
   * @param b
   *          double number
   * @return lcm of a and b
   */
  public static double lcm(final double a, final double b) {
    return a * (b / MathFunctionsHelper.gcd(a, b));
  }

  /**
   * computes the Least Common Multiple (LCM) of a vector of doubles
   *
   * @param input
   *          an array of doubles
   * @return the lcm
   */
  public static double lcm(final double[] input) {
    double result = input[0];
    for (int i = 1; i < input.length; i++) {
      result = MathFunctionsHelper.lcm(result, input[i]);
    }

    return result;
  }

  /*
   * public static long gcd(long a, long b) { while (b > 0) { long temp = b; b = a % b; // % is remainder a = temp; } return a; }
   *
   * public static long gcd(long[] input) { long result = input[0]; for (int i = 1; i < input.length; i++) result = gcd(result, input[i]); return result; }
   *
   * // lcm public static long lcm(long a, long b) { return a * (b / gcd(a, b)); }
   *
   * public static long lcm(long[] input) { long result = input[0]; for (int i = 1; i < input.length; i++) result = lcm(result, input[i]); return result; }
   *
   */
}
