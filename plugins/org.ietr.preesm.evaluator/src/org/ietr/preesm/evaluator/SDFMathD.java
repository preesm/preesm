package org.ietr.preesm.evaluator;

//TODO To remove and put the functions inside SDFMath once finished
public class SDFMathD {
	
	/**
	 * Computes the Greatest common divisor of two double
	 */
	public static double gcd(double a, double b) {
		if (a < b)
			return (gcd(b, a));
		else if (b == 0)
			return (a);
		else
			return (gcd(b, a % b));
	}
	
	/**
	 * Computes the Lowest Common Multiple of two double
	 */
	public static double lcm(double a, double b) {
		return (a/gcd(a,b)) * b;
	}
}
