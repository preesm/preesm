package org.ietr.preesm.evaluator.test;

import org.ietr.preesm.evaluator.SDFMathD;
import org.junit.Assert;
import org.junit.Test;

public class SdfMathDTest {

	@Test
	public void testGCD() {
		final double i1 = 5, i2 = 7, expected = 1.0d;
		final double gcd = SDFMathD.gcd(i1,i2);
		Assert.assertEquals(expected, gcd, 0);
	}

}
