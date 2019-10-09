package org.preesm.tests.model.scenario.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.preesm.model.scenario.util.DefaultTypeSizes;

/**
 *
 * @author anmorvan
 *
 */
public class DefaultTypeSizesTest {

  @Test
  public void testVoidTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getDefaultTypeSize("void");
    assertEquals(1L, defaultTypeSize);
  }

  @Test
  public void testCharTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getDefaultTypeSize("char");
    assertEquals(1L, defaultTypeSize);
  }

  @Test
  public void testINT32TTypeSize() {
    final long defaultTypeSize = DefaultTypeSizes.getInstance().getDefaultTypeSize("int32_t");
    assertEquals(4L, defaultTypeSize);
  }

}
