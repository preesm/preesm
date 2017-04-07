package org.ietr.preesm.memory.script;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellInterpreterTest {

	@Test
	public void testBasicEval() throws EvalError {
		final Interpreter interpreter = new Interpreter();
		final Object eval = interpreter.eval("a = 2;");
		Assert.assertNotNull(eval);
		Assert.assertTrue(eval instanceof Integer);
		final Integer value = (Integer) eval;
		final int intValue = value.intValue();
		Assert.assertEquals(2, intValue);
	}

	@Test
	public void testListIteration() throws EvalError {
		final Interpreter interpreter = new Interpreter();
		interpreter.eval("import " + List.class.getName() + ";");
		interpreter.eval("import " + ArrayList.class.getName() + ";");

		Object result = interpreter.eval("list = new ArrayList<String>(10)");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ArrayList);
		result = interpreter.eval("list");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		final Object setListEval = interpreter.eval("for (i = 0; i < 10; i++) { list.add(\"#\"+i); }");
		Assert.assertNull(setListEval);
		interpreter.setOut(ps);
		final Object readListEval = interpreter.eval("for (string : list) { print(string); }");
		Assert.assertNull(readListEval);
		final String genContent = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		Assert.assertFalse(genContent.isEmpty());
		final String expectedContent = "#0\n#1\n#2\n#3\n#4\n#5\n#6\n#7\n#8\n#9\n";
		Assert.assertEquals(expectedContent, genContent);
	}

	@Test
	public void testParameterSet() throws EvalError {
		final Interpreter interpreter = new Interpreter();
		final HashMap<String, Double> hashMap = new HashMap<>();
		hashMap.put("item1", new Double(5.125));
		interpreter.set("myParam", hashMap);

		final Object result = interpreter.eval("myParam");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof HashMap);
	}

	@Test
	public void testNotDefined() throws EvalError {
		final Interpreter interpreter = new Interpreter();
		final Object eval = interpreter.eval("a");
		Assert.assertNull(eval);
	}

	@Test
	public void testMethodNotDefined() throws EvalError {
		final Interpreter interpreter = new Interpreter();
		interpreter.eval("import " + List.class.getName() + ";");
		interpreter.eval("import " + ArrayList.class.getName() + ";");

		Object result = interpreter.eval("list = new ArrayList<String>(10)");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ArrayList);
		try {
			interpreter.eval("list.methodDoesntExist()");
			Assert.fail();
		} catch (EvalError err) {
			//success
			return;
		}
		Assert.fail();
	}
}
