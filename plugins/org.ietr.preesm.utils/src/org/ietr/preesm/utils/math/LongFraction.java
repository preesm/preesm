package org.ietr.preesm.utils.math;

import java.io.Serializable;
import java.math.BigInteger;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;

/**
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

  /** The default epsilon used for convergence. */
  private static final double DEFAULT_EPSILON = 1e-5;

  /** The denominator. */
  private final long denominator;

  /** The numerator. */
  private final long numerator;

  /**
   * Create a fraction given the double value.
   *
   * @param value
   *          the double value to convert to a fraction.
   * @throws FractionConversionException
   *           if the continued fraction failed to converge.
   * @deprecated Prefer use of exact cosntructor with long denominator
   */
  @Deprecated
  public LongFraction(double value) {
    this(value, DEFAULT_EPSILON, 100);
  }

  /**
   * Create a fraction given the double value and maximum error allowed.
   * <p>
   * References:
   * <ul>
   * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html"> Continued Fraction</a> equations (11) and
   * (22)-(26)</li>
   * </ul>
   * </p>
   *
   * @param value
   *          the double value to convert to a fraction.
   * @param epsilon
   *          maximum error allowed. The resulting fraction is within {@code epsilon} of {@code value}, in absolute
   *          terms.
   * @param maxIterations
   *          maximum number of convergents
   * @throws FractionConversionException
   *           if the continued fraction failed to converge.
   * @deprecated Prefer use of exact cosntructor with long denominator
   */
  @Deprecated
  public LongFraction(double value, double epsilon, int maxIterations) {
    this(value, epsilon, Long.MAX_VALUE, maxIterations);
  }

  /**
   * Create a fraction given the double value and maximum denominator.
   * <p>
   * References:
   * <ul>
   * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html"> Continued Fraction</a> equations (11) and
   * (22)-(26)</li>
   * </ul>
   * </p>
   *
   * @param value
   *          the double value to convert to a fraction.
   * @param maxDenominator
   *          The maximum allowed value for denominator
   * @throws FractionConversionException
   *           if the continued fraction failed to converge
   * @deprecated Prefer use of exact cosntructor with long denominator
   */
  @Deprecated
  public LongFraction(double value, long maxDenominator) {
    this(value, 0, maxDenominator, 100);
  }

  /**
   * Create a fraction given the double value and either the maximum error allowed or the maximum number of denominator
   * digits.
   * <p>
   *
   * NOTE: This constructor is called with EITHER - a valid epsilon value and the maxDenominator set to
   * Integer.MAX_VALUE (that way the maxDenominator has no effect). OR - a valid maxDenominator value and the epsilon
   * value set to zero (that way epsilon only has effect if there is an exact match before the maxDenominator value is
   * reached).
   * </p>
   * <p>
   *
   * It has been done this way so that the same code can be (re)used for both scenarios. However this could be confusing
   * to users if it were part of the public API and this constructor should therefore remain PRIVATE.
   * </p>
   *
   * See JIRA issue ticket MATH-181 for more details:
   *
   * https://issues.apache.org/jira/browse/MATH-181
   *
   * @param value
   *          the double value to convert to a fraction.
   * @param epsilon
   *          maximum error allowed. The resulting fraction is within {@code epsilon} of {@code value}, in absolute
   *          terms.
   * @param maxDenominator
   *          maximum denominator value allowed.
   * @param maxIterations
   *          maximum number of convergents
   * @throws FractionConversionException
   *           if the continued fraction failed to converge.
   * @deprecated Prefer use of exact cosntructor with long denominator
   */
  @Deprecated
  private LongFraction(double value, double epsilon, long maxDenominator, int maxIterations) {
    long overflow = Integer.MAX_VALUE;
    double r0 = value;
    long a0 = (long) FastMath.floor(r0);
    if (FastMath.abs(a0) > overflow) {
      throw new FractionConversionException(value, a0, 1L);
    }

    // check for (almost) integer arguments, which should not go to iterations.
    if (FastMath.abs(a0 - value) < epsilon) {
      this.numerator = (int) a0;
      this.denominator = 1;
      return;
    }

    long p0 = 1;
    long q0 = 0;
    long p1 = a0;
    long q1 = 1;

    long p2 = 0;
    long q2 = 1;

    int n = 0;
    boolean stop = false;
    do {
      ++n;
      double r1 = 1.0 / (r0 - a0);
      long a1 = (long) FastMath.floor(r1);
      p2 = (a1 * p1) + p0;
      q2 = (a1 * q1) + q0;

      if ((FastMath.abs(p2) > overflow) || (FastMath.abs(q2) > overflow)) {
        // in maxDenominator mode, if the last fraction was very close to the actual value
        // q2 may overflow in the next iteration; in this case return the last one.
        if (epsilon == 0.0 && FastMath.abs(q1) < maxDenominator) {
          break;
        }
        throw new FractionConversionException(value, p2, q2);
      }

      double convergent = (double) p2 / (double) q2;
      if (n < maxIterations && FastMath.abs(convergent - value) > epsilon && q2 < maxDenominator) {
        p0 = p1;
        p1 = p2;
        q0 = q1;
        q1 = q2;
        a0 = a1;
        r0 = r1;
      } else {
        stop = true;
      }
    } while (!stop);

    if (n >= maxIterations) {
      throw new FractionConversionException(value, maxIterations);
    }

    if (q2 < maxDenominator) {
      this.numerator = (int) p2;
      this.denominator = (int) q2;
    } else {
      this.numerator = (int) p1;
      this.denominator = (int) q1;
    }

  }

  /**
   * Create a fraction from an int. The fraction is num / 1.
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
    if (den < 0) {
      if (num == Integer.MIN_VALUE || den == Integer.MIN_VALUE) {
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

    // move sign to numerator.
    if (den < 0) {
      num = -num;
      den = -den;
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
    if (numerator == Integer.MIN_VALUE) {
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
   *           if the resulting numerator or denominator exceeds {@code Integer.MAX_VALUE}
   */
  public LongFraction add(LongFraction fraction) {
    return addSub(fraction, true /* add */);
  }

  /**
   * Add an integer to the fraction.
   *
   * @param i
   *          the {@code integer} to add.
   * @return this + i
   */
  public LongFraction add(final int i) {
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
   *           if the resulting numerator or denominator cannot be represented in an {@code int}.
   */
  public LongFraction subtract(LongFraction fraction) {
    return addSub(fraction, false /* subtract */);
  }

  /**
   * Subtract an integer from the fraction.
   *
   * @param i
   *          the {@code integer} to subtract.
   * @return this - i
   */
  public LongFraction subtract(final int i) {
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
   *           if the resulting numerator or denominator cannot be represented in an {@code int}.
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
    if (w.bitLength() > 31) {
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
   *           if the resulting numerator or denominator exceeds {@code Integer.MAX_VALUE}
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
   * Multiply the fraction by an integer.
   *
   * @param i
   *          the {@code integer} to multiply by.
   * @return this * i
   */
  public LongFraction multiply(final int i) {
    return multiply(new LongFraction(i));
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
   *           if the resulting numerator or denominator exceeds {@code Integer.MAX_VALUE}
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
   * Divide the fraction by an integer.
   *
   * @param i
   *          the {@code integer} to divide by.
   * @return this * i
   */
  public LongFraction divide(final int i) {
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
    if (denominator == Integer.MIN_VALUE && (numerator & 1) == 0) {
      numerator /= 2;
      denominator /= 2;
    }
    if (denominator < 0) {
      if (numerator == Integer.MIN_VALUE || denominator == Integer.MIN_VALUE) {
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
