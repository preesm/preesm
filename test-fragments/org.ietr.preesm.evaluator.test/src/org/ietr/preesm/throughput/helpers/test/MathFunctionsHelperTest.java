package org.ietr.preesm.throughput.helpers.test;

import org.ietr.preesm.throughput.helpers.MathFunctionsHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * unit test of MathFunctionsHelper class
 * 
 * @author hderoui
 *
 */
public class MathFunctionsHelperTest {

  @Test
  public void testGCDShouldBeComputed() {
    Assert.assertEquals(1, MathFunctionsHelper.gcd(5, 7), 0);
    Assert.assertEquals(35, MathFunctionsHelper.lcm(5, 7), 0);

    double[] a = { 2, 6, 12 };
    Assert.assertEquals(2, MathFunctionsHelper.gcd(a), 0);
    Assert.assertEquals(12, MathFunctionsHelper.lcm(a), 0);
  }
}
