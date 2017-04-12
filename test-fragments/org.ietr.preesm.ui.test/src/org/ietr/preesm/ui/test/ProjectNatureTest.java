package org.ietr.preesm.ui.test;

import org.ietr.preesm.ui.wizards.PreesmProjectNature;
import org.junit.Assert;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class ProjectNatureTest.
 */
public class ProjectNatureTest {

  /**
   * Test ID.
   */
  @Test
  public void testID() {
    final String id = PreesmProjectNature.ID;
    Assert.assertNotNull(id);
    Assert.assertTrue("org.ietr.preesm.core.ui.wizards.nature".equals(id));
  }

}
