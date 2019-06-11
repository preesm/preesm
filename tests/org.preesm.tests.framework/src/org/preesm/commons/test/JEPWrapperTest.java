package org.preesm.commons.test;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.commons.math.JEPWrapper;

/**
 *
 * @author anmorvan
 *
 */
public class JEPWrapperTest {

  /**
   *
   */
  @Test
  public void testInvolvement_0() {
    final String expression = "a*b";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(2, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertTrue(involvement.contains("b"));
  }

  /**
   *
   */
  @Test
  public void testInvolvement_1() {
    final String expression = "2*a+ceil(b)-(a*b)/3-a-a-a-a*b+b+b+b";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(2, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertTrue(involvement.contains("b"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_2() {
    final String expression = "a*pi";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(1, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertFalse(involvement.contains("pi"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_3() {
    final String expression = "floor(a)";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(1, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertFalse(involvement.contains("floor"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_4() {
    final String expression = "stupid_function(a)";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(2, involvement.size());
    Assert.assertTrue(involvement.contains("a"));
    Assert.assertTrue(involvement.contains("stupid_function"));
  }

  /**
  *
  */
  @Test
  public void testInvolvement_5() {
    final String expression = "floor(15)";
    final List<String> involvement = JEPWrapper.involvement(expression);
    Assert.assertNotNull(involvement);
    Assert.assertEquals(0, involvement.size());
    Assert.assertFalse(involvement.contains("floor"));
  }
}
