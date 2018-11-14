/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.preesm.experiment.model.pimm.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.preesm.commons.files.URLResolver;
import org.xml.sax.SAXException;

/**
 *
 * @author anmorvan
 *
 */
public class PiSDFXSDValidator {

  final URL schemaURL;

  private PiSDFXSDValidator() {
    this.schemaURL = new URLResolver(this.getClass().getClassLoader()).resolve("PiSDF.xsd", Collections.emptyList());
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

  /**
   *
   */
  public static final void validate(final URL pisdfURL) throws IOException {
    // get the content of the URL, then parse
    final String pisdfContent = IOUtils.toString(pisdfURL);
    PiSDFXSDValidator.validate(pisdfContent);
  }

  /**
   *
   */
  public static final void validate(final String pisdfcontent) throws IOException {
    final InputStream targetStream = new ByteArrayInputStream(pisdfcontent.getBytes());
    new PiSDFXSDValidator().validateLocal(targetStream);
  }

  /**
   * @throws IOException
   *
   */
  private final void validateLocal(final InputStream pisdfStreamed) throws IOException {
    if (this.schemaURL == null) {
      throw new NullPointerException("PiSDF XSD was not initialized properly");
    }
    pisdfStreamed.mark(Integer.MAX_VALUE);
    final CloseShieldInputStream protectedStream = new CloseShieldInputStream(pisdfStreamed);
    final Source xmlFile = new StreamSource(protectedStream);
    final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      final Schema schema = schemaFactory.newSchema(this.schemaURL);
      final Validator validator = schema.newValidator();
      validator.validate(xmlFile);
    } catch (final SAXException e) {
      throw new PiSDFXSDValidationException("Could not validate PiSDF", e);
    }
    pisdfStreamed.reset();
  }
}
