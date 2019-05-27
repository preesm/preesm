package org.preesm.commons.test;

import java.io.IOException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.commons.exceptions.PreesmResourceException;
import org.preesm.commons.files.PreesmResourcesHelper;

/**
 *
 * @author anmorvan
 *
 */
public class PreesmResourcesTest {

  final String expectedContent1 = "some content\n" + "WLD49MCZp0VOb9687vtd\n" + "s1q4Tv9aKGneVwRp80CG\n"
      + "xD4gcmtVdItagi8xlyOE\n" + "MyFSqix4LBrOnImmK1tb\n";

  final String expectedContentSubFolder = "subfolder content\n" + "WLD49MCZp0VOb9687vtd\n" + "s1q4Tv9aKGneVwRp80CG\n"
      + "xD4gcmtVdItagi8xlyOE\n" + "MyFSqix4LBrOnImmK1tb\n";

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundles() throws IOException {
    final URL url = PreesmResourcesHelper.getInstance().resolve("test_resource.txt", "org.preesm.tests.framework",
        this.getClass());
    final String content = PreesmResourcesHelper.getInstance().read(url);
    Assert.assertEquals(expectedContent1, content);
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesSubFolder() throws IOException {
    final URL url = PreesmResourcesHelper.getInstance().resolve("subfolder/test_resource.txt",
        "org.preesm.tests.framework", this.getClass());
    final String content = PreesmResourcesHelper.getInstance().read(url);
    Assert.assertEquals(expectedContentSubFolder, content);
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesFake() throws IOException {
    try {
      final URL url = PreesmResourcesHelper.getInstance().resolve("test_resource_fake.txt",
          "org.preesm.tests.framework", this.getClass());
      PreesmResourcesHelper.getInstance().read(url);
      Assert.fail("Expecting preesm resource exception");
    } catch (final PreesmResourceException e) {
      // success
    }
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesSubOnly() throws IOException {
    try {
      final URL url = PreesmResourcesHelper.getInstance().resolve("test_resource_subonly.txt",
          "org.preesm.tests.framework", this.getClass());
      PreesmResourcesHelper.getInstance().read(url);
      Assert.fail("Expecting preesm resource exception");
    } catch (final PreesmResourceException e) {
      // success
    }
  }
}
