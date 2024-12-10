/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.io;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class provides methods to transform an XML file or a DOM element to:
 * <ul>
 * <li>a string</li>
 * <li>a new document</li>
 * <li>a child of an existing element</li>
 * </ul>
 * .
 *
 * @author Matthieu Wipliez
 */
public class XsltTransformer {

  /** The transformer. */
  private Transformer transformer;

  /**
   * Creates a new {@link XsltTransformer} with an XSLT stylesheet contained in the file whose name is
   * <code>fileName</code>.
   *
   * @param contributorId
   *          the identifier of the contributor of the XSLT transformation
   * @param fileName
   *          The XSLT stylesheet file name.
   * @throws TransformerConfigurationException
   *           Thrown if there are errors when parsing the Source or it is not possible to create a {@link Transformer}
   *           instance.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws URISyntaxException
   *           the URI syntax exception
   */
  public XsltTransformer(final String contributorId, final String fileName)
      throws TransformerConfigurationException, IOException, URISyntaxException {
    final IPath path = new Path(fileName);
    final Bundle bundle = Platform.getBundle(contributorId);
    final IPath folder = path.removeLastSegments(1);

    final TransformerFactory factory = TransformerFactory
        .newInstance(net.sf.saxon.TransformerFactoryImpl.class.getCanonicalName(), null);

    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);

    factory.setURIResolver((href, base) -> {
      try {
        // What we are doing here is solving the "href" URI and get
        // an InputStream from it.
        IPath path1 = new Path(href);
        InputStream is;

        if (path1.isAbsolute()) {
          // absolute path, just opens it
          is = new FileInputStream(path1.toOSString());
        } else {
          // relative path, a file that is relative to the
          // "folder" path in this bundle.
          path1 = folder.append(path1);
          is = FileLocator.openStream(bundle, path1, false);
        }

        return new StreamSource(is);
      } catch (final IOException e) {
        throw new TransformerException(e);
      }
    });

    final InputStream is = FileLocator.openStream(bundle, path, false);
    final StreamSource xsltSource = new StreamSource(is);

    this.transformer = factory.newTransformer(xsltSource);
    this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
  }

  /**
   * Calls {@link Transformer#setParameter(String, Object)} on the underlying {@link #transformer}.
   *
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public void setParameter(final String name, final Object value) {
    this.transformer.setParameter(name, value);
  }

  /**
   * Transforms the given DOM element (and its children) and returns the result. The result element is in a different
   * document than the source's owner document.
   *
   * @param source
   *          The source element to transform.
   * @return The document element (and its children) resulting from the transformation.
   * @throws TransformerException
   *           If an unrecoverable error occurs during the course of the transformation.
   */
  public Element transformDomToDom(final Element source) throws TransformerException {
    // create document
    final Document document = DomHelper.createDocument("", "dummy");
    document.removeChild(document.getDocumentElement());

    final Source xmlSource = new DOMSource(source);
    final Result outputTarget = new DOMResult(document);
    this.transformer.transform(xmlSource, outputTarget);

    return document.getDocumentElement();
  }

  /**
   * Transforms the given DOM element (and its children) and returns the result as a string. The string may contain text
   * or XML.
   *
   * @param element
   *          The source element to transform.
   * @return The string resulting from the transformation.
   * @throws TransformerException
   *           If an unrecoverable error occurs during the course of the transformation.
   */
  public String transformDomToString(final Element element) throws TransformerException {
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final DOMSource source = new DOMSource(element);
    final StreamResult result = new StreamResult(os);
    this.transformer.transform(source, result);
    try {
      os.close();
    } catch (final IOException e) {
      // never happens on a byte array output stream
    }

    return os.toString();
  }

}
