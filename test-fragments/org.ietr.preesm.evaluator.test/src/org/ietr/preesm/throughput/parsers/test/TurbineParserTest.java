package org.ietr.preesm.throughput.parsers.test;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.tools.parsers.TurbineParser;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test of GraphStrucutureHelper class
 * 
 * @author hderoui
 *
 */
public class TurbineParserTest {

  @Ignore
  @Test
  public void testIBSDFGraphShouldBeImported() {

    // ------------------------- Import an SDF Graph ------------------
    String dirPath = "resources/";
    String sdf_file = "SDF_test.tur";

    // TODO add SDF import function
    SDFGraph inputGraph = TurbineParser.importIBSDFGraph(dirPath + sdf_file, null);

    // check the results
    // Assert.assertEquals(36200, latency, 0);
  }

}
