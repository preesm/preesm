package org.ietr.preesm.cli.test;

import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.cli.CommandLineUtil;
import org.junit.Assert;
import org.junit.Test;

public class CommandLineUtilTest {

	@Test
	public void testDisableuatobuild() {
		try {
			CommandLineUtil.disableAutoBuild(null);
			Assert.fail();
		} catch (CoreException e) {
			Assert.fail();
		} catch (NullPointerException e) {
			//success
			return;
		}
		Assert.fail();
	}
}
