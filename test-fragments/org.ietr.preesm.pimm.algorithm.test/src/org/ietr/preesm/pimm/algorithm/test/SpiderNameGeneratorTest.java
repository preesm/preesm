package org.ietr.preesm.pimm.algorithm.test;

import org.ietr.preesm.pimm.algorithm.spider.codegen.utils.SpiderNameGenerator;
import org.junit.Assert;
import org.junit.Test;

public class SpiderNameGeneratorTest {

	@Test
	public void testGetCoreName() {
		final String inputString = "toto";
		final String expectedOutput = "CORE_TOTO";
		final String coreName = SpiderNameGenerator.getCoreName(inputString);
		Assert.assertNotNull(coreName);
		Assert.assertTrue(expectedOutput.equals(coreName));
	}
}
