package org.ietr.preesm.throughput.helpers;

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
      double temp = b;
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
  public static double gcd(double[] input) {
    double result = input[0];
    for (int i = 1; i < input.length; i++) {
      result = gcd(result, input[i]);
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
  public static double lcm(double a, double b) {
    return a * (b / gcd(a, b));
  }

  /**
   * computes the Least Common Multiple (LCM) of a vector of doubles
   * 
   * @param input
   *          an array of doubles
   * @return the lcm
   */
  public static double lcm(double[] input) {
    double result = input[0];
    for (int i = 1; i < input.length; i++) {
      result = lcm(result, input[i]);
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
