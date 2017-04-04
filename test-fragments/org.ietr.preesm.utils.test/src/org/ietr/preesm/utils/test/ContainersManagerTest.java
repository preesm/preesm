package org.ietr.preesm.utils.test;

import org.ietr.preesm.utils.files.ContainersManager;
import org.junit.Assert;
import org.junit.Test;

public class ContainersManagerTest {

	@Test
	public void testProjectExists() {
		try {
			final boolean projectExists = ContainersManager.projectExists("toto");
			Assert.assertFalse(projectExists);
			System.out.println("done");
		} catch (IllegalStateException e) {
			System.out.println("workspace not found");
			return;
		}

	}

}
