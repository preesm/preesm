package org.ietr.preesm.core.test;

import org.ietr.preesm.core.scenario.papi.PapiConfigParser;
import org.ietr.preesm.core.scenario.papi.PapiEventInfo;
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
    final PapiEventInfo parse = parser.parse("resources/papi/PAPI_info.xml");

    System.out.println(parse);
  }
}
