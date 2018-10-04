package org.ietr.preesm.experiment.model.pimm.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 *
 * @author anmorvan
 *
 */
public class PiSDFXSDValidator {

  final URL schemaURL;

  /**
   *
   */
  public PiSDFXSDValidator() {
    schemaURL = new URLResolver(this.getClass().getClassLoader()).resolve("PiSDF.xsd", Collections.emptyList());
  }

  /**
   *
   */
  public static class PiSDFXSDValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PiSDFXSDValidationException(final String message) {
      super(message);
    }

    public PiSDFXSDValidationException(final String message, final Throwable cause) {
      super(message, cause);
    }

  }

  public static final void validate(final URL pisdfURL) throws IOException {
    new PiSDFXSDValidator().validateLocal(pisdfURL);
  }

  /**
   * @throws IOException
   *
   */
  private final void validateLocal(final URL pisdfURL) throws IOException {
    if (this.schemaURL == null) {
      System.out.println("could not find schema");
      throw new NullPointerException();
    }

    final Source xmlFile = new StreamSource(pisdfURL.openStream());
    final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      final Schema schema = schemaFactory.newSchema(schemaURL);
      final Validator validator = schema.newValidator();
      validator.validate(xmlFile);
    } catch (final SAXException e) {
      throw new PiSDFXSDValidationException("Could not validate " + pisdfURL, e);
    }
  }
}
