package org.ietr.preesm.utils.test;

import org.ietr.preesm.utils.files.ContainersManager;
import org.junit.Assert;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class ContainersManagerTest.
 */
public class ContainersManagerTest {

  /**
   * Test project exists.
   */
  @Test
  public void testProjectExists() {
    try {
      final boolean projectExists = ContainersManager.projectExists("toto");
      Assert.assertFalse(projectExists);
      System.out.println("done");
    } catch (final IllegalStateException e) {
      System.out.println("workspace not found");
      return;
    }

  }

}
