/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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

import java.io.Serializable;
import java.math.BigInteger;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;

/**
 * This is a copy of the Apache {@link Fraction} with long numerator and denominator instead of Long. It also adds two
 * new methods: {@link #getCeiledRounding(int)} and {@link #getFlooredRounding(int)}.
 *
 * @author anmorvan
 *
 */
public class LongFraction extends Number implements FieldElement<LongFraction>, Comparable<LongFraction>, Serializable {
  /*
   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
   * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this
   * file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
   * with the License. You may obtain a copy of the License at
   *
   * http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
   * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
   * specific language governing permissions and limitations under the License.
   */

  /** A fraction representing "2 / 1". */
  public static final LongFraction TWO = new LongFraction(2, 1);

  /** A fraction representing "1". */
  public static final LongFraction ONE = new LongFraction(1, 1);

  /** A fraction representing "0". */
  public static final LongFraction ZERO = new LongFraction(0, 1);

  /** A fraction representing "4/5". */
  public static final LongFraction FOUR_FIFTHS = new LongFraction(4, 5);

  /** A fraction representing "1/5". */
  public static final LongFraction ONE_FIFTH = new LongFraction(1, 5);

  /** A fraction representing "1/2". */
  public static final LongFraction ONE_HALF = new LongFraction(1, 2);

  /** A fraction representing "1/4". */
  public static final LongFraction ONE_QUARTER = new LongFraction(1, 4);

  /** A fraction representing "1/3". */
  public static final LongFraction ONE_THIRD = new LongFraction(1, 3);

  /** A fraction representing "3/5". */
  public static final LongFraction THREE_FIFTHS = new LongFraction(3, 5);

  /** A fraction representing "3/4". */
  public static final LongFraction THREE_QUARTERS = new LongFraction(3, 4);

  /** A fraction representing "2/5". */
  public static final LongFraction TWO_FIFTHS = new LongFraction(2, 5);

  /** A fraction representing "2/4". */
  public static final LongFraction TWO_QUARTERS = new LongFraction(2, 4);

  /** A fraction representing "2/3". */
  public static final LongFraction TWO_THIRDS = new LongFraction(2, 3);

  /** A fraction representing "-1 / 1". */
  public static final LongFraction MINUS_ONE = new LongFraction(-1, 1);

  /** Serializable version identifier */
  private static final long serialVersionUID = 3698073679419233275L;

  /** The denominator. */
  private final long denominator;

  /** The numerator. */
  private final long numerator;

  /**
   * Create a fraction from a long. The fraction is num / 1.
   *
   * @param num
   *          the numerator.
   */
  public LongFraction(long num) {
    this(num, 1L);
  }

  /**
   * Create a fraction from another LongFraction.
   *
   */
  public LongFraction(LongFraction f) {
    this(f.getNumerator(), f.getDenominator());
  }

  /**
   * Create a fraction given the numerator and denominator. The fraction is reduced to lowest terms.
   *
   * @param num
   *          the numerator.
   * @param den
   *          the denominator.
   * @throws MathArithmeticException
   *           if the denominator is {@code zero}
   */
  public LongFraction(long num, long den) {
    if (den == 0) {
      throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, num, den);
    }
    // move sign to numerator.
    if (den < 0) {
      if (num == Long.MIN_VALUE || den == Long.MIN_VALUE) {
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, num, den);
      }
      num = -num;
      den = -den;
    }
    // reduce numerator and denominator by greatest common denominator.
    final long d = ArithmeticUtils.gcd(num, den);
    if (d > 1) {
      num /= d;
      den /= d;
    }

    this.numerator = num;
    this.denominator = den;
  }

  /**
   * Returns the absolute value of this fraction.
   *
   * @return the absolute value.
   */
  public LongFraction abs() {
    LongFraction ret;
    if (numerator >= 0) {
      ret = this;
    } else {
      ret = negate();
    }
    return ret;
  }

  /**
   * Compares this object to another based on size.
   *
   * @param object
   *          the object to compare to
   * @return -1 if this is less than {@code object}, +1 if this is greater than {@code object}, 0 if they are equal.
   */
  public int compareTo(LongFraction object) {
    long nOd = numerator * object.denominator;
    long dOn = denominator * object.numerator;
    final int tmpResult = (nOd > dOn) ? +1 : 0;
    return (nOd < dOn) ? -1 : tmpResult;
  }

  /**
   * Gets the fraction as a {@code double}. This calculates the fraction as the numerator divided by denominator.
   *
   * @return the fraction as a {@code double}
   */
  @Override
  public double doubleValue() {
    return (double) numerator / (double) denominator;
  }

  /**
   * Test for the equality of two fractions. If the lowest term numerator and denominators are the same for both
   * fractions, the two fractions are considered to be equal.
   *
   * @param other
   *          fraction to test for equality to this fraction
   * @return true if two fractions are equal, false if object is {@code null}, not an instance of {@link LongFraction},
   *         or not equal to this fraction instance.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof LongFraction) {
      // since fractions are always in lowest terms, numerators and
      // denominators can be compared directly for equality.
      LongFraction rhs = (LongFraction) other;
      return (numerator == rhs.numerator) && (denominator == rhs.denominator);
    }
    return false;
  }

  /**
   * Gets the fraction as a {@code float}. This calculates the fraction as the numerator divided by denominator.
   *
   * @return the fraction as a {@code float}
   */
  @Override
  public float floatValue() {
    return (float) doubleValue();
  }

  /**
   * Access the denominator.
   *
   * @return the denominator.
   */
  public long getDenominator() {
    return denominator;
  }

  /**
   * Access the numerator.
   *
   * @return the numerator.
   */
  public long getNumerator() {
    return numerator;
  }

  /**
   * Gets a hashCode for the fraction.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return 37 * (37 * 17 + Long.hashCode(numerator)) + Long.hashCode(denominator);
  }

  /**
   * Gets the fraction as an {@code int}. This returns the whole number part of the fraction.
   *
   * @return the whole number fraction part
   */
  @Override
  public int intValue() {
    return (int) doubleValue();
  }

  /**
   * Gets the fraction as a {@code long}. This returns the whole number part of the fraction.
   *
   * @return the whole number fraction part
   */
  @Override
  public long longValue() {
    return (long) doubleValue();
  }

  /**
   * Return the additive inverse of this fraction.
   *
   * @return the negation of this fraction.
   */
  public LongFraction negate() {
    if (numerator == Long.MIN_VALUE) {
      throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, numerator, denominator);
    }
    return new LongFraction(-numerator, denominator);
  }

  /**
   * Return the multiplicative inverse of this fraction.
   *
   * @return the reciprocal fraction
   */
  public LongFraction reciprocal() {
    return new LongFraction(denominator, numerator);
  }

  /**
   * <p>
   * Adds the value of this fraction to another, returning the result in reduced form. The algorithm follows Knuth,
   * 4.5.1.
   * </p>
   *
   * @param fraction
   *          the fraction to add, must not be {@code null}
   * @return a {@code Fraction} instance with the resulting values
   * @throws NullArgumentException
   *           if the fraction is {@code null}
   * @throws MathArithmeticException
   *           if the resulting numerator or denominator exceeds {@code Long.MAX_VALUE}
   */
  public LongFraction add(LongFraction fraction) {
    return addSub(fraction, true /* add */);
  }

  /**
   * Add a long to the fraction.
   *
   * @param i
   *          the {@code long} to add.
   * @return this + i
   */
  public LongFraction add(final long i) {
    return new LongFraction(numerator + i * denominator, denominator);
  }

  /**
   * <p>
   * Subtracts the value of another fraction from the value of this one, returning the result in reduced form.
   * </p>
   *
   * @param fraction
   *          the fraction to subtract, must not be {@code null}
   * @return a {@code Fraction} instance with the resulting values
   * @throws NullArgumentException
   *           if the fraction is {@code null}
   * @throws MathArithmeticException
   *           if the resulting numerator or denominator cannot be represented in an {@code long}.
   */
  public LongFraction subtract(LongFraction fraction) {
    return addSub(fraction, false /* subtract */);
  }

  /**
   * Subtract a long from the fraction.
   *
   * @param i
   *          the {@code long} to subtract.
   * @return this - i
   */
  public LongFraction subtract(final long i) {
    return new LongFraction(numerator - i * denominator, denominator);
  }

  /**
   * Implement add and subtract using algorithm described in Knuth 4.5.1.
   *
   * @param fraction
   *          the fraction to subtract, must not be {@code null}
   * @param isAdd
   *          true to add, false to subtract
   * @return a {@code Fraction} instance with the resulting values
   * @throws NullArgumentException
   *           if the fraction is {@code null}
   * @throws MathArithmeticException
   *           if the resulting numerator or denominator cannot be represented in an {@code long}.
   */
  private LongFraction addSub(LongFraction fraction, boolean isAdd) {
    if (fraction == null) {
      throw new NullArgumentException(LocalizedFormats.FRACTION);
    }
    // zero is identity for addition.
    if (numerator == 0) {
      return isAdd ? fraction : fraction.negate();
    }
    if (fraction.numerator == 0) {
      return this;
    }
    // if denominators are randomly distributed, d1 will be 1 about 61%
    // of the time.
    long d1 = ArithmeticUtils.gcd(denominator, fraction.denominator);
    if (d1 == 1) {
      // result is ( (u*v' +/- u'v) / u'v')
      long uvp = ArithmeticUtils.mulAndCheck(numerator, fraction.denominator);
      long upv = ArithmeticUtils.mulAndCheck(fraction.numerator, denominator);
      return new LongFraction(isAdd ? ArithmeticUtils.addAndCheck(uvp, upv) : ArithmeticUtils.subAndCheck(uvp, upv),
          ArithmeticUtils.mulAndCheck(denominator, fraction.denominator));
    }
    // the quantity 't' requires 65 bits of precision; see knuth 4.5.1
    // exercise 7. we're going to use a BigInteger.
    // t = u(v'/d1) +/- v(u'/d1)
    BigInteger uvp = BigInteger.valueOf(numerator).multiply(BigInteger.valueOf(fraction.denominator / d1));
    BigInteger upv = BigInteger.valueOf(fraction.numerator).multiply(BigInteger.valueOf(denominator / d1));
    BigInteger t = isAdd ? uvp.add(upv) : uvp.subtract(upv);
    // but d2 doesn't need extra precision because
    // d2 = gcd(t,d1) = gcd(t mod d1, d1)
    long tmodd1 = t.mod(BigInteger.valueOf(d1)).longValue();
    long d2 = (tmodd1 == 0) ? d1 : ArithmeticUtils.gcd(tmodd1, d1);

    // result is (t/d2) / (u'/d1)(v'/d2)
    BigInteger w = t.divide(BigInteger.valueOf(d2));
    if (w.bitLength() > 63) {
      throw new MathArithmeticException(LocalizedFormats.NUMERATOR_OVERFLOW_AFTER_MULTIPLY, w);
    }
    return new LongFraction(w.longValue(), ArithmeticUtils.mulAndCheck(denominator / d1, fraction.denominator / d2));
  }

  /**
   * <p>
   * Multiplies the value of this fraction by another, returning the result in reduced form.
   * </p>
   *
   * @param fraction
   *          the fraction to multiply by, must not be {@code null}
   * @return a {@code Fraction} instance with the resulting values
   * @throws NullArgumentException
   *           if the fraction is {@code null}
   * @throws MathArithmeticException
   *           if the resulting numerator or denominator exceeds {@code Long.MAX_VALUE}
   */
  public LongFraction multiply(LongFraction fraction) {
    if (fraction == null) {
      throw new NullArgumentException(LocalizedFormats.FRACTION);
    }
    if (numerator == 0 || fraction.numerator == 0) {
      return ZERO;
    }
    // knuth 4.5.1
    // make sure we don't overflow unless the result *must* overflow.
    long d1 = ArithmeticUtils.gcd(numerator, fraction.denominator);
    long d2 = ArithmeticUtils.gcd(fraction.numerator, denominator);
    return getReducedFraction(ArithmeticUtils.mulAndCheck(numerator / d1, fraction.numerator / d2),
        ArithmeticUtils.mulAndCheck(denominator / d2, fraction.denominator / d1));
  }

  /**
   * Multiply the fraction by a long.
   *
   * @param i
   *          the {@code long} to multiply by.
   * @return this * i
   */
  public LongFraction multiply(final long i) {
    return multiply(new LongFraction(i));
  }

  @Override
  public LongFraction multiply(int n) {
    return multiply(new LongFraction(n));
  }

  /**
   * <p>
   * Divide the value of this fraction by another.
   * </p>
   *
   * @param fraction
   *          the fraction to divide by, must not be {@code null}
   * @return a {@code Fraction} instance with the resulting values
   * @throws IllegalArgumentException
   *           if the fraction is {@code null}
   * @throws MathArithmeticException
   *           if the fraction to divide by is zero
   * @throws MathArithmeticException
   *           if the resulting numerator or denominator exceeds {@code Long.MAX_VALUE}
   */
  public LongFraction divide(LongFraction fraction) {
    if (fraction == null) {
      throw new NullArgumentException(LocalizedFormats.FRACTION);
    }
    if (fraction.numerator == 0) {
      throw new MathArithmeticException(LocalizedFormats.ZERO_FRACTION_TO_DIVIDE_BY, fraction.numerator,
          fraction.denominator);
    }
    return multiply(fraction.reciprocal());
  }

  /**
   * Divide the fraction by a long.
   *
   * @param i
   *          the {@code long} to divide by.
   * @return this * i
   */
  public LongFraction divide(final long i) {
    return divide(new LongFraction(i));
  }

  /**
   * <p>
   * Gets the fraction percentage as a {@code double}. This calculates the fraction as the numerator divided by
   * denominator multiplied by 100.
   * </p>
   *
   * @return the fraction percentage as a {@code double}.
   */
  public double percentageValue() {
    return 100 * doubleValue();
  }

  /**
   * <p>
   * Creates a {@code Fraction} instance with the 2 parts of a fraction Y/Z.
   * </p>
   *
   * <p>
   * Any negative signs are resolved to be on the numerator.
   * </p>
   *
   * @param numerator
   *          the numerator, for example the three in 'three sevenths'
   * @param denominator
   *          the denominator, for example the seven in 'three sevenths'
   * @return a new fraction instance, with the numerator and denominator reduced
   * @throws MathArithmeticException
   *           if the denominator is {@code zero}
   */
  public static LongFraction getReducedFraction(long numerator, long denominator) {
    if (denominator == 0) {
      throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, numerator, denominator);
    }
    if (numerator == 0) {
      return ZERO; // normalize zero.
    }
    // allow 2^k/-2^31 as a valid fraction (where k>0)
    if (denominator == Long.MIN_VALUE && (numerator & 1) == 0) {
      numerator /= 2;
      denominator /= 2;
    }
    if (denominator < 0) {
      if (numerator == Long.MIN_VALUE || denominator == Long.MIN_VALUE) {
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, numerator, denominator);
      }
      numerator = -numerator;
      denominator = -denominator;
    }
    // simplify fraction.
    long gcd = ArithmeticUtils.gcd(numerator, denominator);
    numerator /= gcd;
    denominator /= gcd;
    return new LongFraction(numerator, denominator);
  }

  /**
   * Returns an approximated ceiled version of the Fraction.
   * 
   * @param maxBinaryPrecision
   *          Maximal number of bits.
   * @return A ceiled fraction of the original with no more bits than the given precision on both the numerator and the
   *         denominator. Or a copy of the original fraction if precision was already lower.
   */
  public LongFraction getCeiledRounding(final int maxBinaryPrecision) {
    if (maxBinaryPrecision < 2) {
      throw new IllegalArgumentException("The binary precision cannot be lower than 2 bits (unsigned).");
    }
    final int nbBitsNum = BigInteger.valueOf(numerator).bitLength();
    final int nbBitsDenom = BigInteger.valueOf(denominator).bitLength();
    if (nbBitsNum < maxBinaryPrecision && nbBitsDenom < maxBinaryPrecision) {
      // not enough precision, so we keep the same
      return new LongFraction(this);
    }
    final int maxBitsToRemove = Math.min(nbBitsNum, nbBitsDenom) - 1;
    final int minBitsToRemove = Math.max(nbBitsNum, nbBitsDenom) - maxBinaryPrecision;
    final int nbBitsToRemove = Math.min(maxBitsToRemove, minBitsToRemove);
    final long dividor = 1L << nbBitsToRemove;
    // for ceil Fraction we ceil numerator while flooring denominator
    final long newNum = (numerator + dividor - 1L) / dividor;
    final long newDenom = denominator / dividor;
    return new LongFraction(newNum, newDenom);
  }

  /**
   * Returns an approximated floored version of the Fraction.
   * 
   * @param maxBinaryPrecision
   *          Maximal number of bits.
   * @return A floored fraction of the original with no more bits than the given precision on both the numerator and the
   *         denominator. Or a copy of the original fraction if precision was already lower.
   */
  public LongFraction getFlooredRounding(final int maxBinaryPrecision) {
    if (maxBinaryPrecision < 2) {
      throw new IllegalArgumentException("The binary precision cannot be lower than 2 bits (unsigned).");
    }
    final int nbBitsNum = BigInteger.valueOf(numerator).bitLength();
    final int nbBitsDenom = BigInteger.valueOf(denominator).bitLength();
    if (nbBitsNum < maxBinaryPrecision && nbBitsDenom < maxBinaryPrecision) {
      // not enough precision, so we keep the same
      return new LongFraction(this);
    }
    final int maxBitsToRemove = Math.min(nbBitsNum, nbBitsDenom) - 1;
    final int minBitsToRemove = Math.max(nbBitsNum, nbBitsDenom) - maxBinaryPrecision;
    final int nbBitsToRemove = Math.min(maxBitsToRemove, minBitsToRemove);
    final long dividor = 1L << nbBitsToRemove;
    // for ceil Fraction we ceil numerator while flooring denominator
    final long newNum = numerator / dividor;
    final long newDenom = (denominator + dividor - 1L) / dividor;
    return new LongFraction(newNum, newDenom);
  }

  /**
   * <p>
   * Returns the {@code String} representing this fraction, ie "num / dem" or just "num" if the denominator is one.
   * </p>
   *
   * @return a string representation of the fraction.
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String str = null;
    if (denominator == 1) {
      str = Long.toString(numerator);
    } else if (numerator == 0) {
      str = "0";
    } else {
      str = numerator + " / " + denominator;
    }
    return str;
  }

  /** {@inheritDoc} */
  public LongFractionField getField() {
    return LongFractionField.getInstance();
  }

  public boolean isZero() {
    return this.equals(ZERO);
  }

}
