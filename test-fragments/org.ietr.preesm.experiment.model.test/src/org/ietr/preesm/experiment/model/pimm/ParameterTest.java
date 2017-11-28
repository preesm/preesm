package org.ietr.preesm.experiment.model.pimm;

import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 *
 */
public class ParameterTest {

  @Test
  public void testParameterCreation() {

    final Parameter p = PiMMUserFactory.instance.createParameter();
    final Expression valueExpression = p.getValueExpression();
    Assert.assertNotNull("Parameter value expression cannot be null", valueExpression);
    final String string = valueExpression.getExpressionString();
    Assert.assertNotNull("Expression value cannot be null", string);
    final String expectedDefaultalue = "0";
    Assert.assertTrue("Expression default value should be " + expectedDefaultalue, expectedDefaultalue.equals(string));
  }
}
