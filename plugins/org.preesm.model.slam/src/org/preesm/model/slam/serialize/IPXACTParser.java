/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
/**
 *
 */
package org.preesm.model.slam.serialize;

import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.VLNV;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Parser of a System-Level Architecture model from the IP-XACT format. Utilities common to component and design
 * parsers.
 *
 * @author mpelcat
 */
public abstract class IPXACTParser {

  /**
   * Instantiates a new IPXACT parser.
   */
  protected IPXACTParser() {
    super();
  }

  /**
   * Parses the VLNV.
   *
   * @param parent
   *          the parent
   * @return the vlnv
   */
  protected VLNV parseVLNV(final Element parent) {
    Node node = parent.getFirstChild();

    final VLNV vlnv = SlamFactory.eINSTANCE.createVLNV();

    while (node != null) {
      // this test allows us to skip #text nodes
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) node;
        final String nodeName = node.getNodeName();
        if (nodeName.equals("spirit:vendor")) {
          vlnv.setVendor(element.getTextContent());
        } else if (nodeName.equals("spirit:name")) {
          vlnv.setName(element.getTextContent());
        } else if (nodeName.equals("spirit:library")) {
          vlnv.setLibrary(element.getTextContent());
        } else if (nodeName.equals("spirit:version")) {
          vlnv.setVersion(element.getTextContent());
        } else {
          // nothing
        }
      }
      node = node.getNextSibling();
    }

    return vlnv;
  }

  /**
   * Parses the compact VLNV.
   *
   * @param parent
   *          the parent
   * @return the vlnv
   */
  protected VLNV parseCompactVLNV(final Element parent) {
    final VLNV vlnv = SlamFactory.eINSTANCE.createVLNV();

    vlnv.setVendor(parent.getAttribute("spirit:vendor"));
    vlnv.setLibrary(parent.getAttribute("spirit:library"));
    vlnv.setName(parent.getAttribute("spirit:name"));
    vlnv.setVersion(parent.getAttribute("spirit:version"));

    return vlnv;
  }

}
