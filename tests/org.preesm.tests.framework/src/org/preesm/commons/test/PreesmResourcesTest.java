package org.preesm.commons.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
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
    final URI uri = PreesmResourcesHelper.getInstance().resolve("test_resource.txt",
        Arrays.asList("org.preesm.tests.framework"), this);
    final String content = PreesmResourcesHelper.getInstance().read(uri);
    Assert.assertEquals(expectedContent1, content);
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesSubFolder() throws IOException {
    final URI uri = PreesmResourcesHelper.getInstance().resolve("subfolder/test_resource.txt",
        Arrays.asList("org.preesm.tests.framework"), this);
    final String content = PreesmResourcesHelper.getInstance().read(uri);
    Assert.assertEquals(expectedContentSubFolder, content);
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesFake() throws IOException {
    try {
      final URI uri = PreesmResourcesHelper.getInstance().resolve("test_resource_fake.txt",
          Arrays.asList("org.preesm.tests.framework"), this);
      PreesmResourcesHelper.getInstance().read(uri);
      Assert.fail("Expecting file not found exception");
    } catch (final FileNotFoundException e) {
      // success
    }
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesSubOnly() throws IOException {
    try {
      final URI uri = PreesmResourcesHelper.getInstance().resolve("test_resource_subonly.txt",
          Arrays.asList("org.preesm.tests.framework"), this);
      PreesmResourcesHelper.getInstance().read(uri);
      Assert.fail("Expecting file not found exception");
    } catch (final FileNotFoundException e) {
      // success
    }
  }
}
