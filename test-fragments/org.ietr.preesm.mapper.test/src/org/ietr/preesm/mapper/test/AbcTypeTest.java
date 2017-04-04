package org.ietr.preesm.mapper.test;

import org.ietr.preesm.mapper.abc.AbcType;
import org.junit.Assert;
import org.junit.Test;

public class AbcTypeTest {

	@Test
	public void testAbcTypeFromString() {
		final AbcType fromString = AbcType.fromString("toto");
		Assert.assertNull(fromString);
	}
}
