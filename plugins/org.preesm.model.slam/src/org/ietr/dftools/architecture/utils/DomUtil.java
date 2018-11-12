/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Ghislain Roquier <ghislain.roquier@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.architecture.utils;

import java.io.InputStream;
import java.io.OutputStream;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

/**
 * This class defines utility methods to create DOM documents and print them to an output stream using DOM 3 Load Save
 * objects.
 *
 * @author Matthieu Wipliez
 *
 */
public class DomUtil {

  private DomUtil() {
    // prevent instantiation
  }

  /** The impl. */
  private static DOMImplementation impl;

  /** The registry. */
  private static DOMImplementationRegistry registry;

  /**
   * Creates a new DOM document.
   *
   * @param docElt
   *          name of the document element
   * @return a new DOM document if something goes wrong
   */
  public static Document createDocument(final String docElt) {
    DomUtil.getImplementation();
    return DomUtil.impl.createDocument("", docElt, null);
  }

  /**
   * Creates the document.
   *
   * @param namespaceURI
   *          the namespace URI
   * @param docElt
   *          the doc elt
   * @return the document
   */
  public static Document createDocument(final String namespaceURI, final String docElt) {
    DomUtil.getImplementation();
    return DomUtil.impl.createDocument(namespaceURI, docElt, null);
  }

  /**
   * Creates a new instance of the DOM registry and get an implementation of DOM 3 with Load Save objects.
   *
   * @return the implementation
   */
  private static void getImplementation() {
    if (DomUtil.registry == null) {
      try {
        DomUtil.registry = DOMImplementationRegistry.newInstance();
      } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        throw new SlamException("Could not instantiate DOM", e);
      }
    }

    if (DomUtil.impl == null) {
      DomUtil.impl = DomUtil.registry.getDOMImplementation("Core 3.0 XML 3.0 LS");
    }
  }

  /**
   * Parses the given input stream as XML and returns the corresponding DOM document.
   *
   * @param is
   *          an input stream
   * @return a DOM document if something goes wrong
   */
  public static Document parseDocument(final InputStream is) {
    DomUtil.getImplementation();
    final DOMImplementationLS implLS = (DOMImplementationLS) DomUtil.impl;

    // serialize to XML
    final LSInput input = implLS.createLSInput();
    input.setByteStream(is);

    // parse without comments and whitespace
    final LSParser builder = implLS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
    final DOMConfiguration config = builder.getDomConfig();
    config.setParameter("comments", false);
    config.setParameter("element-content-whitespace", false);

    return builder.parse(input);
  }

  /**
   * Writes the given document to the given output stream.
   *
   * @param os
   *          an output stream
   * @param document
   *          a DOM document created by {@link #writeDocument(OutputStream, Document)} if something goes wrong
   */
  public static void writeDocument(final OutputStream os, final Document document) {
    DomUtil.getImplementation();
    final DOMImplementationLS implLS = (DOMImplementationLS) DomUtil.impl;

    // serialize to XML
    final LSOutput output = implLS.createLSOutput();
    output.setByteStream(os);

    // serialize the document, close the stream
    final LSSerializer serializer = implLS.createLSSerializer();
    serializer.getDomConfig().setParameter("format-pretty-print", true);
    serializer.write(document, output);
  }

}
