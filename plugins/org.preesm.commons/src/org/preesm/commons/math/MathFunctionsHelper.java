/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
package org.preesm.commons.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.preesm.commons.exceptions.PreesmException;

/**
 *
 * @author hderoui
 *
 *         Math functions helper class contains math functions that do not exists in the java.lang.Math library
 */
public interface MathFunctionsHelper {

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
   * computes the Greatest Common Divisor (GCD) of two long
   *
   * @param a
   *          long number
   * @param b
   *          long number
   * @return the gcd of a and b
   */
  public static long gcd(long a, long b) {
    while (b > 0) {
      final long temp = b;
      b = a % b;
      a = temp;
    }
    return a;
  }

  /**
   * Computes the gcd (greatest common divider) of a list of integer.
   *
   * @param valList
   *          The list of integer to compute
   * @return The gcd (greatest common divider) of the list
   */
  public static long gcd(final List<Long> valList) {
    long gcd = 0;
    for (final Long val : valList) {
      if (gcd == 0) {
        gcd = val;
      } else {
        gcd = ArithmeticUtils.gcd(gcd, val);
      }
    }
    return gcd;
  }

  public static long gcd(final LongFraction a, final LongFraction b) {
    return ArithmeticUtils.lcm(a.getDenominator(), b.getDenominator());
  }

  public static long gcd(final long a, final LongFraction b) {
    return ArithmeticUtils.lcm(a, b.getDenominator());
  }

  /**
   *
   */
  public static long gcd(final Collection<LongFraction> fracs) {
    long gcd = 1;
    for (final LongFraction f : fracs) {
      gcd = gcd(gcd, f.abs());
    }
    return gcd;
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
   */
  public static List<Long> toNatural(final Collection<LongFraction> fracs) {
    final long gcd = gcd(fracs);
    final List<Long> result = new ArrayList<>();
    for (final LongFraction f : fracs) {
      final LongFraction absRat = f.abs();
      final long longNum = absRat.getNumerator();
      final long longRes = (longNum * gcd) / absRat.getDenominator();
      result.add(longRes);
    }
    return result;
  }

  /**
   * Compute rationnal null space.
   *
   * @param matrix
   *          the matrix
   * @return the vector
   */
  public static List<LongFraction> computeRationnalNullSpace(final double[][] matrix) {
    final List<LongFraction> vrb = new ArrayList<>();
    final int numberOfRows = matrix.length;
    int numberOfColumns = 1;

    if (numberOfRows != 0) {
      numberOfColumns = matrix[0].length;
    }

    if ((numberOfRows == 0) || (numberOfColumns == 1)) {
      for (int i = 0; i < numberOfColumns; i++) {
        vrb.add(new LongFraction(1));
      }
      return vrb;
    }

    final LongFraction[][] rationnalTopology = new LongFraction[numberOfRows][numberOfColumns];

    for (int i = 0; i < numberOfRows; i++) {
      for (int j = 0; j < numberOfColumns; j++) {
        rationnalTopology[i][j] = new LongFraction(((Double) matrix[i][j]).longValue());
      }
    }
    int switchIndices = 1;
    while (rationnalTopology[0][0].isZero()) {
      final LongFraction[] buffer = rationnalTopology[0];
      rationnalTopology[0] = rationnalTopology[switchIndices];
      rationnalTopology[switchIndices] = buffer;
      switchIndices++;
    }
    int pivot = 0;
    for (int i = 0; i < numberOfColumns; i++) {
      double pivotMax = 0;
      int maxIndex = i;
      for (int t = i; t < numberOfRows; t++) {
        if (Math.abs(rationnalTopology[t][i].doubleValue()) > pivotMax) {
          maxIndex = t;
          pivotMax = Math.abs(rationnalTopology[t][i].doubleValue());
        }
      }
      if ((pivotMax != 0) && (maxIndex != i)) {
        final LongFraction[] buffer = rationnalTopology[i];
        rationnalTopology[i] = rationnalTopology[maxIndex];
        rationnalTopology[maxIndex] = buffer;
        pivot = i;
      } else if ((maxIndex == i) && (pivotMax != 0)) {
        pivot = i;
      } else {
        break;
      }
      final LongFraction odlPivot = new LongFraction(rationnalTopology[i][i]);
      for (int t = i; t < numberOfColumns; t++) {
        rationnalTopology[i][t] = rationnalTopology[i][t].divide(odlPivot);
      }
      for (int j = i + 1; j < numberOfRows; j++) {
        if (!rationnalTopology[j][i].isZero()) {
          final LongFraction oldji = new LongFraction(rationnalTopology[j][i]);
          for (int k = 0; k < numberOfColumns; k++) {
            rationnalTopology[j][k] = rationnalTopology[j][k]
                .subtract(rationnalTopology[i][k].multiply(oldji.divide(rationnalTopology[pivot][pivot])));
          }
        }
      }
    }
    for (int i = 0; i < numberOfColumns; i++) {
      vrb.add(new LongFraction(1));
    }
    int i = numberOfRows - 1;
    while (i >= 0) {
      LongFraction val = new LongFraction(0);
      for (int k = i + 1; k < numberOfColumns; k++) {
        val = val.add(rationnalTopology[i][k].multiply(vrb.get(k)));
      }
      if (!val.isZero()) {
        if (rationnalTopology[i][i].isZero()) {
          throw new PreesmException("Should have zero elements in the diagonal");
        }
        vrb.set(i, val.abs().divide(rationnalTopology[i][i]));
      }
      i--;
    }
    return vrb;
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
   * computes the Least Common Multiple (LCM) of two long
   *
   * @param a
   *          long number
   * @param b
   *          long number
   * @return lcm of a and b
   */
  public static long lcm(final long a, final long b) {
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

  /**
   * computes the Least Common Multiple (LCM) of a vector of long
   *
   * @param input
   *          an array of long
   * @return the lcm
   */
  public static long lcm(long[] input) {
    long result = input[0];
    for (int i = 1; i < input.length; i++) {
      result = lcm(result, input[i]);
    }
    return result;
  }

}
