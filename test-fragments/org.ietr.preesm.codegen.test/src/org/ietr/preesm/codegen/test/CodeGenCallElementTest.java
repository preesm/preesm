package org.ietr.preesm.codegen.test;

import org.ietr.preesm.codegen.model.CodeGenCallElement;
import org.junit.Assert;
import org.junit.Test;

public class CodeGenCallElementTest {

	@Test
	public void testConstructor() {
		final String inputString = "toto";
		final CodeGenCallElement codeGenCallElement = new CodeGenCallElement(inputString);
		final String name = codeGenCallElement.getName();
		Assert.assertNotNull(name);
		Assert.assertTrue(inputString.equals(name));
	}

}
