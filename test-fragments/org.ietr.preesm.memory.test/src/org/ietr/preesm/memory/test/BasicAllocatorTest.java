package org.ietr.preesm.memory.test;

import org.ietr.preesm.memory.allocation.BasicAllocator;
import org.junit.Assert;
import org.junit.Test;


public class BasicAllocatorTest {

	@Test
	public void testBasicAllocator() {
		try {
			new BasicAllocator(null);
			Assert.fail();
		} catch (NullPointerException e) {
			//success
			return;
		}
		Assert.fail();
	}

}
