/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
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
package org.preesm.model.pisdf.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

// Run a set of tests from traces generated from an old Preesm run using an instrumented version
// Test are generated from the syntax
// <expression>:[<param1 (string)>=<value1 (double)>,<param2 (string)>=<value2 (double)>]
public class JavaExpressionParserTest {

  private static BufferedReader bufferedReader;

  @BeforeAll
  public static void beforeTest() {

    final InputStream inputStream = JavaExpressionParserTest.class.getClassLoader()
        .getResourceAsStream("resources/expression.traces");

    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
  }

  @TestFactory
  Stream<DynamicTest> jepTestFactory() {

    return bufferedReader.lines()
        .map(testLine -> DynamicTest.dynamicTest("Running JEP test for input: " + testLine, () -> {

          final String localTestLine = testLine.replace(" ", "");
          final String expr = localTestLine.substring(0, localTestLine.indexOf(":"));
          final String[] variables = localTestLine.substring(localTestLine.indexOf("[") + 1, localTestLine.indexOf("]"))
              .split(",");

          final JEP jep = new JEP();

          for (final String variable : variables) {

            if (variable.isEmpty()) {
              break;
            }

            final String[] tuple = variable.split("=");
            jep.addVariable(tuple[0], Double.parseDouble(tuple[1]));
          }

          final Node parse = jep.parse(expr);
          final Object evaluate = jep.evaluate(parse);
          Assertions.assertTrue(evaluate instanceof Double);
        }));
  }

  @Test
  void testFail() {
    final String expr = "INVALID";
    final JEP jep = new JEP();

    final Exception exception = Assertions.assertThrows(ParseException.class, () -> jep.parse(expr));

    final String expectedMessage = "Unrecognized symbol";
    final String actualMessage = exception.getMessage();

    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

}
