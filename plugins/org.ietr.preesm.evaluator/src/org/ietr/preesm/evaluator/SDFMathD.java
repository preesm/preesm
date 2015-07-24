package org.ietr.preesm.evaluator;

//TODO To put inside SDFMath once finished
public class SDFMathD {
	
	/**
	 * Computes the GCD of two doubles
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
	 * Computes the lcm of two doubles
	 */
	public static double lcm(double a, double b) {
		return (a/gcd(a,b)) * b;
	}
}
