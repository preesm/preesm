package org.ietr.preesm.core.test;

import org.ietr.preesm.core.expression.CeilFunction;
import org.junit.Assert;
import org.junit.Test;

public class CeilFunctionTest {

	@Test
	public void testCeil() {
		final double input = 15.2d;
		final double expectedOutput = 16d;
		final double ceil = new CeilFunction().ceil(input).doubleValue();
		Assert.assertEquals(expectedOutput, ceil, 0);

	}

}
