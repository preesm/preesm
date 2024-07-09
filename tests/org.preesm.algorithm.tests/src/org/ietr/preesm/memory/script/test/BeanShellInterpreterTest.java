/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2023) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.memory.script.test;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.memory.script.Buffer;
import org.preesm.algorithm.memory.script.Match;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * Non regression tests for the BeanShell2 third party dependency.
 *
 * The evaluation of import statement before calling the scripts seem to be useless (ahonorat).
 */
public class BeanShellInterpreterTest {

  private static final String PLUGIN_PATH          = "../../plugins/";
  private static final String MEMORY_SCRIPT_PLUGIN = "org.preesm.algorithm";

  // Fetch memory scripts from the org.preesm.algorithm package
  private static final String BROADCAST_SCRIPT   = PLUGIN_PATH + MEMORY_SCRIPT_PLUGIN
      + "/resources/scripts/broadcast.bsh";
  private static final String FORK_SCRIPT        = PLUGIN_PATH + MEMORY_SCRIPT_PLUGIN + "/resources/scripts/fork.bsh";
  private static final String JOIN_SCRIPT        = PLUGIN_PATH + MEMORY_SCRIPT_PLUGIN + "/resources/scripts/join.bsh";
  private static final String ROUNDBUFFER_SCRIPT = PLUGIN_PATH + MEMORY_SCRIPT_PLUGIN
      + "/resources/scripts/roundbuffer.bsh";

  private static final String IMPORT = "import ";

  private static final String HEIGHT  = "Height";
  private static final String WIDTH   = "Width";
  private static final String NBSLICE = "NbSlice";
  private static final String OVERLAP = "Overlap";

  private static final String I_INPUT  = "i_input";
  private static final String O_OUTPUT = "o_output";

  private static final String MATCH       = "match = ";
  private static final String RESLIST_SC  = "resList;";
  private static final String RESLIST_ADD = "resList.add(match);\n";

  private static final String INPUT_BUFFER  = "inputBuffer";
  private static final String OUTPUT_BUFFER = "outputBuffer";
  private static final String INPUTS        = "inputs";
  private static final String OUTPUTS       = "outputs";
  private static final String RESLIST       = "resList";

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
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");

    Object result = interpreter.eval("list = new ArrayList(10)");
    Assert.assertNotNull(result);
    Assert.assertTrue(result instanceof ArrayList);
    result = interpreter.eval("list");
    Assert.assertNotNull(result);

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final PrintStream ps = new PrintStream(baos);

    final Object setListEval = interpreter.eval("for (i = 0; i < 10; i++) { list.add(\"#\"+i); }");
    Assert.assertNull(setListEval);
    interpreter.setOut(ps);
    final Object readListEval = interpreter.eval("for (string : list) { print(string); }");
    Assert.assertNull(readListEval);
    final String genContent = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    Assert.assertFalse(genContent.isEmpty());
    final String expectedContent = "#0\n#1\n#2\n#3\n#4\n#5\n#6\n#7\n#8\n#9\n".replace("\n", System.lineSeparator());
    Assert.assertEquals(expectedContent, genContent);
  }

  @Test
  public void testParameterSet() throws EvalError {
    final Interpreter interpreter = new Interpreter();
    final Map<String, Double> hashMap = new LinkedHashMap<>();
    hashMap.put("item1", Double.valueOf(5.125));
    interpreter.set("myParam", hashMap);

    final Object result = interpreter.eval("myParam");
    Assert.assertNotNull(result);
    Assert.assertTrue(result instanceof LinkedHashMap);
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
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");

    final Object result = interpreter.eval("list = new ArrayList(10)");
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
  public void testDesinterleave() throws EvalError, IOException {
    final Interpreter interpreter = new Interpreter();
    final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/desinterleave_standalonetest.bsh";

    interpreter.eval(IMPORT + Buffer.class.getName() + ";");
    interpreter.eval(IMPORT + Match.class.getName() + ";");
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");

    final Map<String, Integer> arguments = new LinkedHashMap<>();
    final int N = 4;
    arguments.put("N", 4);
    arguments.put("clusterSize", 16);
    arguments.put("interClusterSize", 8);

    arguments.forEach((k, v) -> {
      try {
        interpreter.set(k, v.intValue());
      } catch (final EvalError e) {
        throw new PreesmRuntimeException(e);
      }
    });
    final Buffer i = new Buffer(null, "v1", "i", 1024, 4, true);
    final Buffer o = new Buffer(null, "v1", "o", 1024, 4, true);
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
  public void testShuffleSplit() throws EvalError, IOException {
    final Interpreter interpreter = new Interpreter();
    final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/ShuffleSplit_standalonetest.bsh";

    interpreter.eval(IMPORT + Buffer.class.getName() + ";");
    interpreter.eval(IMPORT + Match.class.getName() + ";");
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");

    final Map<String, Integer> arguments = new LinkedHashMap<>();
    final int NbSlice = 8;
    arguments.put(HEIGHT, 1080);
    arguments.put(WIDTH, 1920);
    arguments.put(NBSLICE, NbSlice);
    arguments.put(OVERLAP, 1);

    arguments.forEach((k, v) -> {
      try {
        interpreter.set(k, v.intValue());
      } catch (final EvalError e) {
        throw new PreesmRuntimeException(e);
      }
    });
    final Buffer i = new Buffer(null, "v1", "i", 1024 * 1024 * 1024, 1, true);
    final Buffer o = new Buffer(null, "v1", "o", 1024 * 1024 * 1024, 1, true);
    interpreter.set(I_INPUT, i);
    interpreter.set(O_OUTPUT, o);
    final Object eval = interpreter.source(bshFileUnderTest);
    Assert.assertNotNull(eval);
    Assert.assertTrue(eval instanceof ArrayList);
    @SuppressWarnings("unchecked")
    final List<Match> matchList = (List<Match>) eval;
    final int size = matchList.size();
    Assert.assertEquals(NbSlice, size);

  }

  @Test
  public void testSplit() throws EvalError, IOException {
    final Interpreter interpreter = new Interpreter();
    final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/split_standalonetest.bsh";

    interpreter.eval(IMPORT + Buffer.class.getName() + ";");
    interpreter.eval(IMPORT + Match.class.getName() + ";");
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");

    final Map<String, Integer> arguments = new LinkedHashMap<>();
    final int NbSlice = 80;
    arguments.put(HEIGHT, 1080);
    arguments.put(WIDTH, 1920);
    arguments.put(NBSLICE, NbSlice);
    arguments.put(OVERLAP, 1);

    arguments.forEach((k, v) -> {
      try {
        interpreter.set(k, v.intValue());
      } catch (final EvalError e) {
        throw new PreesmRuntimeException(e);
      }
    });
    final Buffer i = new Buffer(null, "v1", "i", 1024 * 1024 * 1024, 1, true);
    final Buffer o = new Buffer(null, "v1", "o", 1024 * 1024 * 1024, 1, true);
    interpreter.set(I_INPUT, i);
    interpreter.set(O_OUTPUT, o);
    final Object eval = interpreter.source(bshFileUnderTest);
    Assert.assertNotNull(eval);
    Assert.assertTrue(eval instanceof ArrayList);
    @SuppressWarnings("unchecked")
    final List<Match> matchList = (List<Match>) eval;
    final int size = matchList.size();
    Assert.assertEquals(NbSlice, size);

  }

  @Test
  public void testSplitFail() throws EvalError, IOException {
    final Interpreter interpreter = new Interpreter();
    final String bshFileUnderTest = ScriptRunnerTest.SCRIPT_FOLDER_PATH + "/split_standalonetest.bsh";

    final Map<String, Integer> arguments = new LinkedHashMap<>();
    final int NbSlice = 800;
    arguments.put(HEIGHT, 1080);
    arguments.put(WIDTH, 1920);
    arguments.put(NBSLICE, NbSlice);
    arguments.put(OVERLAP, 10);

    arguments.forEach((k, v) -> {
      try {
        interpreter.set(k, v.intValue());
      } catch (final EvalError e) {
        throw new PreesmRuntimeException(e);
      }
    });
    final Buffer i = new Buffer(null, "v1", "i", 10, 1, true);
    final Buffer o = new Buffer(null, "v1", "o", 10, 1, true);
    interpreter.set(I_INPUT, i);
    interpreter.set(O_OUTPUT, o);

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
   */
  @Test
  public void testFork() throws URISyntaxException, IOException, EvalError {

    final StringBuilder content = new StringBuilder();
    final File scriptFile = new File(FORK_SCRIPT);

    try (final BufferedReader in = new BufferedReader(new FileReader(scriptFile));) {
      String inputLine;
      // instrument code to return the list of matches
      while ((inputLine = in.readLine()) != null) {
        final boolean contains = inputLine.contains("matchWith");
        if (contains) {
          content.append(MATCH);
        }
        content.append(inputLine + "\n");
        if (contains) {
          content.append(RESLIST_ADD);
        }
      }
      content.append(RESLIST_SC);
    }
    Assert.assertTrue(content.toString().contains("inputs.get(0).matchWith(inIdx,output,0,outSize);"));

    final int bufferToSplitSize = 1024 * 1024 * 8; // 8MB
    final int numberOfForks = 8;

    final List<Buffer> inputs = new ArrayList<>(1);
    inputs.add(new Buffer(null, "v1", INPUT_BUFFER, bufferToSplitSize, 1, true));
    final List<Buffer> outputs = new ArrayList<>(numberOfForks);
    for (int i = 0; i < numberOfForks; i++) {
      outputs.add(new Buffer(null, "v1", OUTPUT_BUFFER + i, bufferToSplitSize / numberOfForks, 1, true));
    }
    final List<Match> resList = new ArrayList<>();

    final Interpreter interpreter = new Interpreter();
    interpreter.eval(IMPORT + Buffer.class.getName() + ";");
    interpreter.eval(IMPORT + Match.class.getName() + ";");
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");
    interpreter.eval(IMPORT + Arrays.class.getName() + ".*;");
    interpreter.set(INPUTS, inputs);
    interpreter.set(OUTPUTS, outputs);
    interpreter.set(RESLIST, resList);
    final Object eval = interpreter.eval(content.toString());
    Assert.assertEquals(resList, eval);

    final int size = resList.size();
    Assert.assertEquals(numberOfForks, size);
  }

  /**
   * Requires Plugin testing
   *
   */
  @Test
  public void testJoin() throws URISyntaxException, IOException, EvalError {

    final StringBuilder content = new StringBuilder();
    final File scriptFile = new File(JOIN_SCRIPT);

    try (final BufferedReader in = new BufferedReader(new FileReader(scriptFile));) {
      String inputLine;
      // instrument code to return the list of matches
      while ((inputLine = in.readLine()) != null) {
        final boolean contains = inputLine.contains("matchWith");
        if (contains) {
          content.append(MATCH);
        }
        content.append(inputLine + "\n");
        if (contains) {
          content.append(RESLIST_ADD);
        }
      }
      content.append(RESLIST_SC);
    }
    Assert.assertTrue(content.toString().contains("outputs.get(0).matchWith(outIdx,input,0,inSize);"));

    final int bufferToSplitSize = 1024 * 1024 * 8; // 8MB
    final int numberOfForks = 8;

    final List<Buffer> inputs = new ArrayList<>(1);
    for (int i = 0; i < numberOfForks; i++) {
      inputs.add(new Buffer(null, "v1", INPUT_BUFFER + i, bufferToSplitSize / numberOfForks, 1, true));
    }
    final List<Buffer> outputs = new ArrayList<>(1);
    outputs.add(new Buffer(null, "v1", OUTPUT_BUFFER, bufferToSplitSize, 1, true));
    final List<Match> resList = new ArrayList<>();

    final Interpreter interpreter = new Interpreter();
    interpreter.eval(IMPORT + Buffer.class.getName() + ";");
    interpreter.eval(IMPORT + Match.class.getName() + ";");
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");
    interpreter.eval(IMPORT + Arrays.class.getName() + ".*;");
    interpreter.set(INPUTS, inputs);
    interpreter.set(OUTPUTS, outputs);
    interpreter.set(RESLIST, resList);
    final Object eval = interpreter.eval(content.toString());
    Assert.assertEquals(resList, eval);

    final int size = resList.size();
    Assert.assertEquals(numberOfForks, size);
  }

  /**
   * Requires Plugin testing
   *
   */
  @Test
  public void testRoundBuffer() throws URISyntaxException, IOException, EvalError {

    final StringBuilder content = new StringBuilder();
    final File scriptFile = new File(ROUNDBUFFER_SCRIPT);

    try (final BufferedReader in = new BufferedReader(new FileReader(scriptFile));) {
      String inputLine;
      // instrument code to return the list of matches
      while ((inputLine = in.readLine()) != null) {
        final boolean contains = inputLine.contains("input.matchWith(");
        if (contains) {
          content.append(MATCH);
        }
        content.append(inputLine + "\n");
        if (contains) {
          content.append(RESLIST_ADD);
        }
      }
    }
    content.append(RESLIST_SC);

    Assert.assertTrue(content.toString().contains("RuntimeException"));

    final int bufferToBroadcastSize = 1024 * 1024 * 8; // 8MB

    final List<Buffer> inputs = new ArrayList<>(1);
    inputs.add(new Buffer(null, "v1", INPUT_BUFFER, bufferToBroadcastSize, 1, true));
    final List<Buffer> outputs = new ArrayList<>(1);
    outputs.add(new Buffer(null, "v1", OUTPUT_BUFFER, bufferToBroadcastSize, 1, true));

    final List<Match> resList = new ArrayList<>();

    final Interpreter interpreter = new Interpreter();
    interpreter.eval(IMPORT + Buffer.class.getName() + ";");
    interpreter.eval(IMPORT + Match.class.getName() + ";");
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");
    interpreter.eval(IMPORT + Arrays.class.getName() + ".*;");
    interpreter.eval(IMPORT + Collections.class.getName() + ";");
    interpreter.eval(IMPORT + Math.class.getName() + ";");
    interpreter.set(INPUTS, inputs);
    interpreter.set(OUTPUTS, outputs);
    interpreter.set(RESLIST, resList);
    final Object eval = interpreter.eval(content.toString());
    Assert.assertNotNull(eval);
    Assert.assertTrue(eval instanceof List);
    Assert.assertEquals(resList, eval);
    final int size = resList.size();
    Assert.assertEquals(1, size);

  }

  /**
   * Requires Plugin testing
   *
   */
  @Test
  public void testBroadCast() throws URISyntaxException, IOException, EvalError {

    final StringBuilder content = new StringBuilder();
    final File scriptFile = new File(BROADCAST_SCRIPT);

    try (final BufferedReader in = new BufferedReader(new FileReader(scriptFile));) {
      String inputLine;
      // instrument code to return the list of matches
      while ((inputLine = in.readLine()) != null) {
        final boolean contains = inputLine.contains("inputs.get(0).matchWith(inIdx,output,outIdx,matchSize);");
        if (contains) {
          content.append(MATCH);
        }
        content.append(inputLine + "\n");
        if (contains) {
          content.append(RESLIST_ADD);
        }
      }
    }
    content.append(RESLIST_SC);

    Assert.assertTrue(content.toString().contains("inputs.get(0).matchWith(inIdx,output,outIdx,matchSize);"));

    final long nbOutputBuffers = 2L;
    final long bufferToBroadcastSize = 1024L * 1024L * 8L; // 8MB
    final long ratio = 4L;
    final long inputBuffersSize = bufferToBroadcastSize / ratio;

    final List<Buffer> inputs = new ArrayList<>(1);
    inputs.add(new Buffer(null, "v1", INPUT_BUFFER, inputBuffersSize, 1, true));
    final List<Buffer> outputs = new ArrayList<>((int) nbOutputBuffers);
    for (int i = 0; i < nbOutputBuffers; i++) {
      outputs.add(new Buffer(null, "v1", OUTPUT_BUFFER + i, bufferToBroadcastSize, 1, true));
    }
    final List<Match> resList = new ArrayList<>();

    final Interpreter interpreter = new Interpreter();
    interpreter.eval(IMPORT + Buffer.class.getName() + ";");
    interpreter.eval(IMPORT + Match.class.getName() + ";");
    interpreter.eval(IMPORT + List.class.getName() + ";");
    interpreter.eval(IMPORT + ArrayList.class.getName() + ";");
    interpreter.eval(IMPORT + Arrays.class.getName() + ".*;");
    interpreter.set(INPUTS, inputs);
    interpreter.set(OUTPUTS, outputs);
    interpreter.set(RESLIST, resList);
    final Object eval = interpreter.eval(content.toString());
    Assert.assertNotNull(eval);
    Assert.assertTrue(eval instanceof List);
    Assert.assertEquals(resList, eval);
    final int size = resList.size();
    Assert.assertEquals(nbOutputBuffers * ratio, size);
  }
}
