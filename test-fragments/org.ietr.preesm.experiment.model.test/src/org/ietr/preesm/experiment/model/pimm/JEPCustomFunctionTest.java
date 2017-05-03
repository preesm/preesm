package org.ietr.preesm.experiment.model.pimm;

import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class JEPCustomFunctionTest {

  private void testEvaluation(String input, long expected) {
    final long evaluate = ExpressionEvaluator.evaluate(input);
    Assert.assertEquals(expected, evaluate);
  }

  @Test
  public void jepFloorTest1() {
    testEvaluation("floor(0)", 0);
  }

  @Test
  public void jepFloorTest2() {
    testEvaluation("floor(0)", 0);
  }

  @Test
  public void jepFloorTest3() {
    testEvaluation("floor(-1)", -1);
  }

  @Test
  public void jepFloorTest4() {
    testEvaluation("floor(1.2)", 1);
  }

  @Test
  public void jepFloorTest5() {
    testEvaluation("floor(1.9)", 1);
  }

  @Test
  public void jepFloorTest6() {
    testEvaluation("floor(-1.9)", -2);
  }

  @Test
  public void jepFloorTest7() {
    testEvaluation("floor(-1.1)", -2);
  }

  @Test
  public void jepFloorTest8() {
    testEvaluation("floor(-2.1)", -3);
  }

  @Test
  public void jepFloorTest9() {
    testEvaluation("floor(5/2)", 2);
  }

  @Test
  public void jepFloorTest10() {
    testEvaluation("floor(5/-2)", -3);
  }

  @Test
  public void jepCeilTest() {

  }

}
