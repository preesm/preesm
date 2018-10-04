package org.ietr.preesm.experiment.model.pimm;

import java.io.IOException;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.ietr.preesm.experiment.model.pimm.util.URLResolver;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author anmorvan
 *
 */
public class XSDValidationTest {

  private static final URL SCHEMA_URL = URLResolver.resolveURLFromClasspath("/PiSDF.xsd", XSDValidationTest.class);

  /**
   * @throws IOException
   *
   */
  public boolean validate(final URL schemaURL, final URL pisdfURL) throws IOException {
    // webapp example xsd:
    // URL schemaFile = new URL("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd");
    // local file example:
    // File schemaFile = new File("/location/to/localfile.xsd"); // etc.
    Source xmlFile = new StreamSource(pisdfURL.openStream());
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      Schema schema = schemaFactory.newSchema(schemaURL);
      Validator validator = schema.newValidator();
      validator.validate(xmlFile);
      return true;
    } catch (SAXException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Test
  public void sampleTest() throws IOException {
    final URL findFirstInPluginList = URLResolver.findFirstInPluginList("resources/pisdf/actor_mlp.pi",
        "org.ietr.preesm.experiment.model.test");
    final boolean validate = validate(SCHEMA_URL, findFirstInPluginList);
    Assert.assertTrue(validate);
  }
}
