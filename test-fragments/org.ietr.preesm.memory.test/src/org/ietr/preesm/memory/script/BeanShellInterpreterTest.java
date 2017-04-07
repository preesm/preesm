package org.ietr.preesm.memory.script;

import org.junit.Assert;
import org.junit.Test;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellInterpreterTest {

	@Test
	public void testBasicEval() throws EvalError {
		final Interpreter interpreter = new Interpreter();
		Object eval = interpreter.eval("a = 2;");
		Assert.assertNotNull(eval);
		Assert.assertTrue(eval instanceof Integer);
		Integer value = (Integer) eval;
		int intValue = value.intValue();
		Assert.assertEquals(2, intValue);
	}

}
