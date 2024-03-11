/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018)
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

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.preesm.commons.math.ExpressionEvaluationException;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 *
 */
@RunWith(Parameterized.class)
public class JEPCustomFunctionTest {

  private final String input;
  private final long   expected;

  public JEPCustomFunctionTest(String input, long expected) {
    this.input = input;
    this.expected = expected;
  }

  @Parameters(name = "Expression: {0}, expected = {1}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { "floor(0)", 0 }, { "floor(0)", 0 }, { "floor(-1)", -1 },
        { "floor(1.2)", 1 }, { "floor(1.9)", 1 }, { "floor(-1.9)", -2 }, { "floor(-1.1)", -2 }, { "floor(-2.1)", -3 },
        { "floor(5/2)", 2 }, { "floor(5/-2)", -3 }, { "ceil(5/2)", 3 }, { "min(min(1,-3),-2)", -3 },
        { "max(10, max(1,-3))", 10 }, { "1000*geo_sum(3,1/2,4)", 5625 }, { "geo_sum(3,2,4)", 45 },
        { "geo_sum(512000,0.25,7)", 682625 }, { "pow_div_max(2, 72)", 3 }, { "pow_div_max(2, 73)", 0 } });
  }

  @Test
  public void testEvaluation() {
    long evaluate = 0;
    final Expression createExpression = PiMMUserFactory.instance.createExpression(input);
    try {
      evaluate = createExpression.evaluate();
    } catch (final ExpressionEvaluationException e) {
      Assert.fail();
    }
    Assert.assertEquals(expected, evaluate);
  }

}
