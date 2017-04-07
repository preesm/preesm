package org.ietr.preesm.memory.script;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.junit.Assert;
import org.junit.Test;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Non regression tests for the BeanShell2 third party dependency.
 */
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

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream ps = new PrintStream(baos);

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

		final Object result = interpreter.eval("list = new ArrayList<String>(10)");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof ArrayList);
		try {
			interpreter.eval("list.methodDoesntExist()");
			Assert.fail();
		} catch (final EvalError err) {
			// success
			return;
		}
		Assert.fail();
	}

	@Test
	public void testDesinterleave() throws EvalError, FileNotFoundException, IOException {
		final Interpreter interpreter = new Interpreter();
		final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/desinterleave_standalonetest.bsh";

		interpreter.eval("import " + Buffer.class.getName() + ";");
		interpreter.eval("import " + Match.class.getName() + ";");
		interpreter.eval("import " + List.class.getName() + ";");
		interpreter.eval("import " + ArrayList.class.getName() + ";");

		final Map<String, Integer> arguments = new LinkedHashMap<>();
		final int N = 4;
		arguments.put("N", 4);
		arguments.put("clusterSize", 16);
		arguments.put("interClusterSize", 8);

		arguments.forEach((k, v) -> {
			try {
				interpreter.set(k, v.intValue());
			} catch (final EvalError e) {
				e.printStackTrace();
			}
		});
		final Buffer i = new Buffer(null, new DAGVertex("v1", null, null), "i", 1024, 4, true);
		final Buffer o = new Buffer(null, new DAGVertex("v1", null, null), "o", 1024, 4, true);
		interpreter.set("i_i", i);
		interpreter.set("o_o", o);
		final Object eval = interpreter.source(bshFileUnderTest);
		Assert.assertNotNull(eval);
		Assert.assertTrue(eval instanceof ArrayList);
		@SuppressWarnings("unchecked")
		final List<Match> matchList = (List<Match>) eval;
		final int size = matchList.size();
		Assert.assertEquals(N, size);

	}

	@Test
	public void testShuffleSplit() throws EvalError, FileNotFoundException, IOException {
		final Interpreter interpreter = new Interpreter();
		final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/ShuffleSplit_standalonetest.bsh";

		interpreter.eval("import " + Buffer.class.getName() + ";");
		interpreter.eval("import " + Match.class.getName() + ";");
		interpreter.eval("import " + List.class.getName() + ";");
		interpreter.eval("import " + ArrayList.class.getName() + ";");

		final Map<String, Integer> arguments = new LinkedHashMap<>();
		final int NbSlice = 8;
		arguments.put("Height", 1080);
		arguments.put("Width", 1920);
		arguments.put("NbSlice", NbSlice);
		arguments.put("Overlap", 1);

		arguments.forEach((k, v) -> {
			try {
				interpreter.set(k, v.intValue());
			} catch (final EvalError e) {
				e.printStackTrace();
			}
		});
		final Buffer i = new Buffer(null, new DAGVertex("v1", null, null), "i", 1024 * 1024 * 1024, 1, true);
		final Buffer o = new Buffer(null, new DAGVertex("v1", null, null), "o", 1024 * 1024 * 1024, 1, true);
		interpreter.set("i_input", i);
		interpreter.set("o_output", o);
		final Object eval = interpreter.source(bshFileUnderTest);
		Assert.assertNotNull(eval);
		Assert.assertTrue(eval instanceof ArrayList);
		@SuppressWarnings("unchecked")
		final List<Match> matchList = (List<Match>) eval;
		final int size = matchList.size();
		Assert.assertEquals(NbSlice, size);

	}

	@Test
	public void testSplit() throws EvalError, FileNotFoundException, IOException {
		final Interpreter interpreter = new Interpreter();
		final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/split_standalonetest.bsh";

		interpreter.eval("import " + Buffer.class.getName() + ";");
		interpreter.eval("import " + Match.class.getName() + ";");
		interpreter.eval("import " + List.class.getName() + ";");
		interpreter.eval("import " + ArrayList.class.getName() + ";");

		final Map<String, Integer> arguments = new LinkedHashMap<>();
		final int NbSlice = 80;
		arguments.put("Height", 1080);
		arguments.put("Width", 1920);
		arguments.put("NbSlice", NbSlice);
		arguments.put("Overlap", 1);

		arguments.forEach((k, v) -> {
			try {
				interpreter.set(k, v.intValue());
			} catch (final EvalError e) {
				e.printStackTrace();
			}
		});
		final Buffer i = new Buffer(null, new DAGVertex("v1", null, null), "i", 1024 * 1024 * 1024, 1, true);
		final Buffer o = new Buffer(null, new DAGVertex("v1", null, null), "o", 1024 * 1024 * 1024, 1, true);
		interpreter.set("i_input", i);
		interpreter.set("o_output", o);
		final Object eval = interpreter.source(bshFileUnderTest);
		Assert.assertNotNull(eval);
		Assert.assertTrue(eval instanceof ArrayList);
		@SuppressWarnings("unchecked")
		final List<Match> matchList = (List<Match>) eval;
		final int size = matchList.size();
		Assert.assertEquals(NbSlice, size);

	}

	@Test
	public void testSplitFail() throws EvalError, FileNotFoundException, IOException {
		final Interpreter interpreter = new Interpreter();
		final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/split_standalonetest.bsh";

		final Map<String, Integer> arguments = new LinkedHashMap<>();
		final int NbSlice = 800;
		arguments.put("Height", 1080);
		arguments.put("Width", 1920);
		arguments.put("NbSlice", NbSlice);
		arguments.put("Overlap", 10);

		arguments.forEach((k, v) -> {
			try {
				interpreter.set(k, v.intValue());
			} catch (final EvalError e) {
				e.printStackTrace();
			}
		});
		final Buffer i = new Buffer(null, new DAGVertex("v1", null, null), "i", 10, 1, true);
		final Buffer o = new Buffer(null, new DAGVertex("v1", null, null), "o", 10, 1, true);
		interpreter.set("i_input", i);
		interpreter.set("o_output", o);

		try {
			interpreter.source(bshFileUnderTest);
			Assert.fail();
		} catch (final EvalError e) {
			final Throwable cause = e.getCause();
			Assert.assertTrue(cause instanceof RuntimeException);
			final String message = cause.getMessage();
			Assert.assertNotNull(message);
			Assert.assertTrue(message.startsWith("Cannot match"));
			// success
			return;
		}
		Assert.fail();
	}

	/**
	 * Requires Plugin testing
	 *
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Test
	public void testFork() throws URISyntaxException, IOException {
		final String plugin_name = "org.ietr.preesm.memory";
		final String script_path = "/scripts/fork.bsh";

		// load fork script

//		final Bundle bundle = Platform.getBundle(plugin_name);
//		Assert.assertNotNull(bundle);
//		final URL fileURL = bundle.getEntry(script_path);
//		final File file = new File(FileLocator.resolve(fileURL).toURI());
//		final String forkScriptPath = file.getAbsolutePath();

		StringBuffer content = new StringBuffer();
		URL url = new URL("platform:/plugin/"+plugin_name+"/"+script_path);
		InputStream inputStream = url.openConnection().getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine+"\n");
		}
		in.close();
		Assert.assertTrue(content.toString().contains("inputs.get(0).matchWith(inIdx,output,0,outSize);"));
	}
}
