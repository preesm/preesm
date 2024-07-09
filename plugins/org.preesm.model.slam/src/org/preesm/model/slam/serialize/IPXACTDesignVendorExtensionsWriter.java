/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2022)
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

import java.util.Map.Entry;
import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.Dma;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.Link;
import org.preesm.model.slam.Mem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class managing information stored in design vendor extensions.
 *
 * @author mpelcat
 */
public class IPXACTDesignVendorExtensionsWriter {

  /** The design. */
  private final Design design;

  /**
   * Instantiates a new IPXACT design vendor extensions writer.
   *
   * @param design
   *          the design
   */
  public IPXACTDesignVendorExtensionsWriter(final Design design) {
    this.design = design;
  }

  /**
   * Writes the vendor extension inside a dom element.
   *
   * @param parent
   *          the parent
   * @param document
   *          the document
   */
  public void write(final Element parent, final Document document) {
    final Element vendorExtensionsElt = document.createElement("spirit:vendorExtensions");
    parent.appendChild(vendorExtensionsElt);

    final Element componentDescriptionsElt = document.createElement("slam:componentDescriptions");
    vendorExtensionsElt.appendChild(componentDescriptionsElt);

    this.design.getComponentHolder().getComponents();
    for (final Component component : this.design.getComponentHolder().getComponents()) {
      writeComponentDescription(componentDescriptionsElt, component, document);
    }

    final Element linkDescriptionsElt = document.createElement("slam:linkDescriptions");
    vendorExtensionsElt.appendChild(linkDescriptionsElt);

    for (final Link link : this.design.getLinks()) {
      writeLinkDescription(linkDescriptionsElt, link, document);
    }

    final Element designDescriptionElt = document.createElement("slam:designDescription");
    vendorExtensionsElt.appendChild(designDescriptionElt);

    final Element parametersElt = document.createElement("slam:parameters");
    designDescriptionElt.appendChild(parametersElt);

    for (final Entry<String, String> p : this.design.getParameters()) {
      writeDesignParameter(parametersElt, p.getKey(), p.getValue(), document);
    }
  }

  /**
   * Writes a parameter of the design.
   *
   * @param parent
   *          the parent
   * @param key
   *          the key
   * @param value
   *          the value
   * @param document
   *          the document
   */
  public void writeDesignParameter(final Element parent, final String key, final String value,
      final Document document) {
    final Element parameterElt = document.createElement("slam:parameter");
    parent.appendChild(parameterElt);

    parameterElt.setAttribute("slam:key", key);
    parameterElt.setAttribute("slam:value", value);
  }

  /**
   * Writes a component description inside a dom element.
   *
   * @param parent
   *          the parent
   * @param component
   *          the component
   * @param document
   *          the document
   */
  public void writeComponentDescription(final Element parent, final Component component, final Document document) {

    // Adding as component type the name of the component ecore EClass.
    final String componentRef = component.getVlnv().getName();
    String componentType = component.eClass().getName();

    // Communication node type is concatenated if necessary
    if (componentType.equals("ComNode")) {
      if (((ComNode) component).isParallel()) {
        componentType = "parallel" + componentType;
      } else {
        componentType = "contention" + componentType;
      }

    }

    final Element componentElt = document.createElement("slam:componentDescription");
    parent.appendChild(componentElt);

    componentElt.setAttribute("slam:componentRef", componentRef);
    componentElt.setAttribute("slam:componentType", componentType);

    final RefinementList list = new RefinementList();
    for (final Design subDesign : component.getRefinements()) {
      list.addName(subDesign.getUrl());
    }
    final String refinementPath = list.toString();

    componentElt.setAttribute("slam:refinement", refinementPath);

    // Managing specific component properties
    if (component instanceof final ComNode comNode) {
      componentElt.setAttribute("slam:speed", Float.toString(comNode.getSpeed()));
    } else if (component instanceof final Mem mem) {
      componentElt.setAttribute("slam:size", Long.toString(mem.getSize()));
    } else if (component instanceof final Dma dma) {
      componentElt.setAttribute("slam:setupTime", Integer.toString(dma.getSetupTime()));
    } else if (component instanceof final FPGA fpga) {
      componentElt.setAttribute("slam:frequency", Integer.toString(fpga.getFrequency()));
      componentElt.setAttribute("slam:part", fpga.getPart());
      componentElt.setAttribute("slam:board", fpga.getBoard());
    }
  }

  /**
   * Writes a link description inside a dom element.
   *
   * @param parent
   *          the parent
   * @param link
   *          the link
   * @param document
   *          the document
   */
  public void writeLinkDescription(final Element parent, final Link link, final Document document) {
    final Element linkElt = document.createElement("slam:linkDescription");
    parent.appendChild(linkElt);

    linkElt.setAttribute("slam:referenceId", link.getUuid());
    final String directed = link.isDirected() ? "directed" : "undirected";
    linkElt.setAttribute("slam:directedLink", directed);
    linkElt.setAttribute("slam:linkType", link.eClass().getName());
  }
}
