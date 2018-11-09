/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.dftools.workflow.converter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.ietr.dftools.workflow.WorkflowException;

/**
 * This class provides methods to transform an XML file via XSLT
 *
 * @author Matthieu Wipliez
 * @author mpelcat
 *
 */
public class XsltTransformer {

  private Transformer transformer;

  /**
   * Creates a new {@link XsltTransform}
   */
  public XsltTransformer() {
    super();
  }

  /**
   * Sets an XSLT stylesheet contained in the file whose name is <code>fileName</code>.
   *
   * @param xslFileLoc
   *          The XSLT stylesheet file name.
   * @throws TransformerConfigurationException
   *           Thrown if there are errors when parsing the Source or it is not possible to create a {@link Transformer}
   *           instance.
   */
  public boolean setXSLFile(final String xslFileLoc) throws TransformerConfigurationException {

    final TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    final StreamSource source = new StreamSource(xslFileLoc);

    try {
      this.transformer = factory.newTransformer(source);
    } catch (final Exception e) {
      throw new WorkflowException("Could not transform Xslt", e);
    }

    if (this.transformer == null) {
      throw new WorkflowException("XSL sheet not found or not valid: " + xslFileLoc);
    }
    this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    return true;
  }

  /**
   * Transforms the given input file and generates the output file
   */
  public void transformFileToFile(final String sourceFileLoc, final String destFileLoc) {

    if (this.transformer != null) {

      try {
        this.transformer.transform(new StreamSource(sourceFileLoc),
            new StreamResult(new FileOutputStream(destFileLoc)));
      } catch (final FileNotFoundException ex) {
        final String message = "Problem finding files for XSL transfo (" + sourceFileLoc + "," + destFileLoc + ")";
        throw new WorkflowException(message, ex);
      } catch (final TransformerException ex) {
        throw new WorkflowException("Could not transform file", ex);
      }
    }

  }

}
