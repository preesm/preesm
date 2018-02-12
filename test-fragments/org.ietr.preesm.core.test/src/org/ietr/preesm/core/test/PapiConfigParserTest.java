package org.ietr.preesm.core.test;

import org.ietr.preesm.core.scenario.papi.PapiConfigParser;
import org.ietr.preesm.core.scenario.papi.PapiEventInfo;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author anmorvan
 *
 */
public class PapiConfigParserTest {

  @Test
  public void testParser() {
    final PapiConfigParser parser = new PapiConfigParser();
    final PapiEventInfo eventInfos = parser.parse("resources/papi/PAPI_info.xml");
    Assert.assertNotNull(eventInfos);
    final int size = eventInfos.getComponents().size();
    Assert.assertEquals(3, size);
  }
}
