package org.ietr.preesm.experiment.model.pimm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 *
 */
public class ParameterTest {

  @Test
  public void testParameterCreation() {

    final Parameter p = PiMMFactory.eINSTANCE.createParameter();
    final Expression valueExpression = p.getValueExpression();
    assertNotNull("Parameter value expression cannot be null", valueExpression);
    final String string = valueExpression.getExpressionString();
    assertNotNull("Expression value cannot be null", string);
    final String expectedDefaultalue = "0";
    assertTrue("Expression default value should be " + expectedDefaultalue, expectedDefaultalue.equals(string));
  }
}
