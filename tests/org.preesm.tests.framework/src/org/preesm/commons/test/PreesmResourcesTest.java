/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.commons.test;

import java.io.IOException;
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
    final String content = PreesmResourcesHelper.getInstance().read("test_resource.txt", this.getClass());
    Assert.assertEquals(expectedContent1, content);
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesSubFolder() throws IOException {
    final String content = PreesmResourcesHelper.getInstance().read("subfolder/test_resource.txt", this.getClass());
    Assert.assertEquals(expectedContentSubFolder, content);
  }

  /**
   *
   */
  @Test
  public void testResourceLoadFromBundlesFake() throws IOException {
    try {
      PreesmResourcesHelper.getInstance().resolve("test_resource_fake.txt", this.getClass());
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
      PreesmResourcesHelper.getInstance().resolve("test_resource_subonly.txt", this.getClass());
      Assert.fail("Expecting preesm resource exception");
    } catch (final PreesmResourceException e) {
      // success
    }
  }
}
