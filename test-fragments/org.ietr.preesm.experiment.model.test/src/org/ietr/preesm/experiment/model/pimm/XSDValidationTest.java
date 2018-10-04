package org.ietr.preesm.experiment.model.pimm;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.ietr.preesm.experiment.model.pimm.util.URLResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

/**
 *
 * @author anmorvan
 *
 */
@RunWith(Parameterized.class)
public class XSDValidationTest {

  private static final URL SCHEMA_URL = URLResolver.resolveURLFromClasspath("/PiSDF.xsd", XSDValidationTest.class);

  private final URL pisdURL;

  public XSDValidationTest(final String pisdfName) {
    this.pisdURL = URLResolver.findFirstInPluginList("resources/pisdf/" + pisdfName,
        "org.ietr.preesm.experiment.model.test");
  }

  @Parameters
  public static Collection<Object[]> data() {
    Object[][] data = new Object[][] { { "actor_mlp.pi" } };
    return Arrays.asList(data);
  }

  /**
   * @throws IOException
   *
   */
  private static final boolean validate(final URL schemaURL, final URL pisdfURL) throws IOException {
    // webapp example xsd:
    // URL schemaFile = new URL("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd");
    // local file example:
    // File schemaFile = new File("/location/to/localfile.xsd"); // etc.
    final Source xmlFile = new StreamSource(pisdfURL.openStream());
    final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      final Schema schema = schemaFactory.newSchema(schemaURL);
      final Validator validator = schema.newValidator();
      validator.validate(xmlFile);
      return true;
    } catch (final SAXException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Test
  public void sampleTest() throws IOException {
    final boolean validate = XSDValidationTest.validate(XSDValidationTest.SCHEMA_URL, this.pisdURL);
    Assert.assertTrue(validate);
  }
}
