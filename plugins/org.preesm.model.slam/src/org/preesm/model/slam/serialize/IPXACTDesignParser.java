/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021 - 2022)
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.preesm.commons.DomUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.URLResolver;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComInterface;
import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentHolder;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.Dma;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.HierarchyPort;
import org.preesm.model.slam.Link;
import org.preesm.model.slam.Mem;
import org.preesm.model.slam.ParameterizedElement;
import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.SlamPackage;
import org.preesm.model.slam.VLNV;
import org.preesm.model.slam.serialize.IPXACTDesignVendorExtensionsParser.LinkDescription;
import org.preesm.model.slam.utils.SlamUserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Parser of a System-Level Architecture model from the IP-XACT format.
 *
 * @author mpelcat
 */
public class IPXACTDesignParser extends IPXACTParser {

  /** Information needed in the vendor extensions of the design. */
  private final IPXACTDesignVendorExtensionsParser vendorExtensions;

  /**
   * IPXact parser constructor.
   *
   */
  public IPXACTDesignParser() {
    this.vendorExtensions = new IPXACTDesignVendorExtensionsParser();
  }

  /**
   * Parsing a design from and IP XACT design file.
   *
   * @param inputStream
   *          the stream obtained from the IP-XACT file
   * @param componentHolder
   *          a component holder if inherited from a design upper in the hierarchy. null otherwise.
   * @param refinedComponent
   *          component refined by the current design
   * @return the parsed design
   */
  public Design parse(final InputStream inputStream, ComponentHolder componentHolder, Component refinedComponent) {
    // The topmost component is initialized to enable storing
    // the hierarchical external interfaces

    if (refinedComponent == null) {
      refinedComponent = SlamFactory.eINSTANCE.createComponent();
    }

    final Design design = SlamFactory.eINSTANCE.createDesign();
    refinedComponent.getRefinements().add(design);

    // Creates a component holder in case of a top design.
    // It is inherited in the case of a subdesign.
    if (componentHolder == null) {
      componentHolder = SlamFactory.eINSTANCE.createComponentHolder();
    }
    design.setComponentHolder(componentHolder);

    final Document document = DomUtil.parseDocument(inputStream);
    final Element root = document.getDocumentElement();

    // Parsing vendor extensions that will parameterize the model
    this.vendorExtensions.parse(root);

    // Retrieving custom design parameters from vendor extensions
    setDesignParameters(design);

    // Parsing the file content to fill the design
    parseDesign(root, design);

    // Managing the hierarchy: the refinement of the components are set.
    manageRefinements(design);

    try {
      inputStream.close();
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Could not parse IPXACT");
    }

    return design;
  }

  /**
   * Sets the design parameters.
   *
   * @param design
   *          the new design parameters
   */
  private void setDesignParameters(final Design design) {
    final Map<String, String> designParameters = this.vendorExtensions.getDesignParameters();
    design.getParameters().putAll(designParameters);
  }

  /**
   * Parses the design.
   *
   * @param parent
   *          the parent
   * @param design
   *          the design
   */
  private void parseDesign(final Element parent, final Design design) {

    final VLNV vlnv = parseVLNV(parent);
    design.setVlnv(vlnv);

    Node node = parent.getFirstChild();

    while (node != null) {
      // this test allows us to skip #text nodes
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        final Element element = (Element) node;
        final String nodeName = node.getNodeName();
        if (nodeName.equals("spirit:componentInstances")) {
          parseComponentInstances(element, design);
        } else if (nodeName.equals("spirit:interconnections")) {
          parseLinks(element, design);
        } else if (nodeName.equals("spirit:hierConnections")) {
          parseHierarchicalPorts(element, design);
        } else {
          // ignore for the moment
        }
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Parses the component instances.
   *
   * @param parent
   *          the parent
   * @param design
   *          the design
   */
  private void parseComponentInstances(final Element parent, final Design design) {
    Node node = parent.getFirstChild();

    while (node != null) {
      if (node instanceof final Element element) {
        final String type = element.getTagName();
        if (type.equals("spirit:componentInstance")) {
          parseComponentInstance(element, design);
        }
      }
      node = node.getNextSibling();
    }

    // set default hardware ID
    final EList<ComponentInstance> componentInstances = design.getComponentInstances();
    final Set<Integer> usedIDs = componentInstances.stream().map(ComponentInstance::getHardwareId)
        .collect(Collectors.toSet());
    // this looks completely useless because the parseHardwareID method throws exceptions
    // if one hardwareId is unset so it is dead code
    final List<ComponentInstance> unsetHardwareIDComponentInstances = componentInstances.stream()
        .filter(c -> c.getHardwareId() == -1).collect(Collectors.toList());
    if (!unsetHardwareIDComponentInstances.isEmpty()) {
      PreesmLogger.getLogger().log(Level.WARNING,
          "Some component instances have an unset hardware ID. Using counter to set a default value.");
      final AtomicInteger counter = new AtomicInteger(0);
      for (final ComponentInstance cmp : unsetHardwareIDComponentInstances) {
        while (usedIDs.contains(counter.get())) {
          counter.incrementAndGet();
        }
        cmp.setHardwareId(counter.get());
        usedIDs.add(counter.get());
      }

    }

  }

  /**
   * Parses the component instance.
   *
   * @param parent
   *          the parent
   * @param design
   *          the design
   */
  private void parseComponentInstance(final Element parent, final Design design) {

    final ComponentInstance instance = SlamFactory.eINSTANCE.createComponentInstance();

    design.getComponentInstances().add(instance);

    VLNV vlnv = null;
    final String instanceName = parseID(parent);
    instance.setInstanceName(instanceName);

    final int id = parseHardwareID(parent, instanceName);
    instance.setHardwareId(id);

    Node node = parent.getFirstChild();

    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if ("spirit:configurableElementValues".equals(type)) {
          parseParameters(elt, instance);
        } else if ("spirit:componentRef".equals(type)) {
          vlnv = parseCompactVLNV(elt);
        }
      }
      node = node.getNextSibling();
    }
    if (vlnv == null) {
      throw new PreesmRuntimeException("Could not parse VLNV");
    }

    // Component type is retrieved from vendor extensions if there are any.
    // Otherwise, a generic component is created
    final IPXACTDesignVendorExtensionsParser.ComponentDescription description = this.vendorExtensions
        .getComponentDescription(vlnv.getName());
    String componentType = "Component";
    if (description != null) {
      componentType = description.getComponentType();
    }

    // Creates the component if necessary
    // eClass is retrieved from the component type
    if (design.containsComponent(vlnv)) {
      instance.setComponent(design.getComponent(vlnv));
    } else {

      final Component component = SlamUserFactory.eINSTANCE.createComponent(vlnv, componentType);
      design.getComponentHolder().getComponents().add(component);

      instance.setComponent(component);
      if (description == null) {
        throw new PreesmRuntimeException("Could not parse description");
      }
      try {
        // Special component cases
        if (component instanceof final ComNode comNode) {
          comNode.setSpeed(Float.valueOf(description.getSpecificParameter("slam:speed")));
          final boolean equals = "contention".equals(description.getSpecificParameter("ComNodeType"));
          comNode.setParallel(!equals);
        } else if (component instanceof final Mem mem) {
          mem.setSize(Integer.valueOf(description.getSpecificParameter("slam:size")));
        } else if (component instanceof final Dma dma) {
          dma.setSetupTime(Integer.valueOf(description.getSpecificParameter("slam:setupTime")));
        } else if (component instanceof final FPGA fpga) {
          fpga.setFrequency(Integer.valueOf(description.getSpecificParameter("slam:frequency")));
          fpga.setPart(description.getSpecificParameter("slam:part"));
          fpga.setBoard(description.getSpecificParameter("slam:board"));
        }
      } catch (final NumberFormatException e) {
        throw new PreesmRuntimeException("Could not parse a numeric property of component instance <"
            + instance.getInstanceName() + ">, please check it in the slam.", e);
      }

    }

  }

  /**
   * Manage refinements.
   *
   * @param design
   *          the design
   */
  private void manageRefinements(final Design design) {
    final Set<Component> components = new LinkedHashSet<>(design.getComponentHolder().getComponents());
    for (final Component component : components) {
      final IPXACTDesignVendorExtensionsParser.ComponentDescription description = this.vendorExtensions
          .getComponentDescription(component.getVlnv().getName());

      // Looking for a refinement design in the project
      if ((description != null) && !description.getRefinement().isEmpty()) {
        final RefinementList list = new RefinementList(description.getRefinement());

        for (final String refinementStringPath : list.toStringArray()) {

          final URL findFirst = URLResolver.findFirst(refinementStringPath);

          File file;
          try {
            file = new File(findFirst.toURI());
          } catch (final URISyntaxException e) {
            throw new PreesmRuntimeException("Malformed URL " + findFirst, e);
          }

          // Read from an input stream
          final IPXACTDesignParser subParser = new IPXACTDesignParser();
          InputStream stream = null;

          try {
            stream = new FileInputStream(file.getPath());
          } catch (final FileNotFoundException e) {
            throw new PreesmRuntimeException("Could not locate file " + file.getPath(), e);
          }

          final Design subDesign = subParser.parse(stream, design.getComponentHolder(), component);

          // A design shares its component holder with its
          // subdesigns
          subDesign.setUrl(refinementStringPath);
          component.getRefinements().add(subDesign);
        }
      }
    }
  }

  /**
   * Parses the instance name.
   *
   * @param parent
   *          the parent
   * @return the string
   */
  private String parseID(final Element parent) {
    Node node = parent.getFirstChild();
    String name = "";

    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("spirit:instanceName")) {
          name = elt.getTextContent();
        }
      }
      node = node.getNextSibling();
    }

    return name;
  }

  private int parseHardwareID(final Element parent, final String componentName) {
    Node node = parent.getFirstChild();
    int id = -1;

    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("spirit:hardwareId")) {
          final String textContent = elt.getTextContent();
          if (textContent == null || textContent.isEmpty()) {
            throw new PreesmRuntimeException(
                "No component instance ID specified for component '" + componentName + "'");
          }
          try {
            id = Integer.valueOf(textContent);
            if (id < 0) {
              throw new NumberFormatException(
                  "Component instance ID '" + id + "' of component '" + componentName + "' should be positive.");
            }
          } catch (final NumberFormatException e) {
            throw new PreesmRuntimeException(
                "Could not parse component instance ID '" + textContent + "' of component '" + componentName + "'", e);
          }
        }
      }
      node = node.getNextSibling();
    }
    return id;
  }

  /**
   * Parses the parameters.
   *
   * @param parent
   *          the parent
   * @param paramElt
   *          the param elt
   */
  private void parseParameters(final Element parent, final ParameterizedElement paramElt) {
    Node node = parent.getFirstChild();

    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("spirit:configurableElementValue")) {
          final String name = elt.getAttribute("spirit:referenceId");
          final String value = elt.getTextContent();

          paramElt.getParameters().put(name, value);
        }
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Parses the link.
   *
   * @param parent
   *          the parent
   * @param design
   *          the design
   */
  private void parseLink(final Element parent, final Design design) {
    final List<String> comItfs = new ArrayList<>(2);
    final List<String> componentInstanceRefs = new ArrayList<>(2);
    String linkUuid = "";

    Node node = parent.getFirstChild();
    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("spirit:name")) {
          linkUuid = elt.getTextContent();
        } else if (type.equals("spirit:displayName") || type.equals("spirit:description")) {
          // nothing
        } else if (type.equals("spirit:activeInterface")) {
          comItfs.add(elt.getAttribute("spirit:busRef"));
          componentInstanceRefs.add(elt.getAttribute("spirit:componentRef"));
        }
      }
      node = node.getNextSibling();
    }

    // Retrieving parameters from vendor extensions
    final LinkDescription linkDescription = this.vendorExtensions.getLinkDescription(linkUuid);

    if (linkDescription == null) {
      return;
    }

    String linkType = "DataLink";

    if (!linkDescription.getType().isEmpty()) {
      linkType = linkDescription.getType();
    }

    final EPackage eLinkPackage = SlamPackage.eINSTANCE;
    final EClass linkEclass = (EClass) eLinkPackage.getEClassifier(linkType);

    // Creating the link with appropriate type
    final Link link = (Link) SlamFactory.eINSTANCE.create(linkEclass);

    link.setDirected(linkDescription.isDirected());
    link.setUuid(linkUuid);
    final ComponentInstance sourceInstance = design.getComponentInstance(componentInstanceRefs.get(0));
    link.setSourceComponentInstance(sourceInstance);
    ComInterface sourceInterface = sourceInstance.getComponent().getInterface(comItfs.get(0));

    // Creating source interface if necessary
    if (sourceInterface == null) {
      sourceInterface = SlamFactory.eINSTANCE.createComInterface();
      sourceInterface.setName(comItfs.get(0));
      sourceInstance.getComponent().getInterfaces().add(sourceInterface);
    }
    link.setSourceInterface(sourceInterface);

    final ComponentInstance destinationInstance = design.getComponentInstance(componentInstanceRefs.get(1));
    link.setDestinationComponentInstance(destinationInstance);
    ComInterface destinationInterface = destinationInstance.getComponent().getInterface(comItfs.get(1));

    // Creating destination interface if necessary
    if (destinationInterface == null) {
      destinationInterface = SlamFactory.eINSTANCE.createComInterface();
      destinationInterface.setName(comItfs.get(1));
      destinationInstance.getComponent().getInterfaces().add(destinationInterface);
    }
    link.setDestinationInterface(destinationInterface);

    design.getLinks().add(link);

  }

  /**
   * Parses the links.
   *
   * @param parent
   *          the parent
   * @param design
   *          the design
   */
  private void parseLinks(final Element parent, final Design design) {
    Node node = parent.getFirstChild();
    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("spirit:interconnection")) {
          parseLink(elt, design);
        }
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Parses the hierarchical port.
   *
   * @param parent
   *          the parent
   * @param design
   *          the design
   */
  private void parseHierarchicalPort(final Element parent, final Design design) {

    final HierarchyPort port = SlamFactory.eINSTANCE.createHierarchyPort();

    final String externalInterfaceName = parent.getAttribute("spirit:interfaceRef");
    ComInterface externalInterface = design.getRefined().getInterface(externalInterfaceName);
    // Creating the external interface if nonexistent
    if (externalInterface == null) {
      externalInterface = SlamFactory.eINSTANCE.createComInterface();
      externalInterface.setName(externalInterfaceName);
    }
    port.setExternalInterface(externalInterface);

    String internalInterfaceName = null;
    String internalComponentInstanceName = null;

    Node node = parent.getFirstChild();
    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("spirit:name") || type.equals("spirit:displayName") || type.equals("spirit:description")) {
          // nothing
        } else if (type.equals("spirit:activeInterface")) {
          internalInterfaceName = elt.getAttribute("spirit:busRef");
          internalComponentInstanceName = elt.getAttribute("spirit:componentRef");
        }
      }
      node = node.getNextSibling();
    }

    final ComponentInstance internalComponentInstance = design.getComponentInstance(internalComponentInstanceName);
    port.setInternalComponentInstance(internalComponentInstance);
    ComInterface internalInterface = internalComponentInstance.getComponent().getInterface(internalInterfaceName);

    // Creating internal interface if necessary
    if (internalInterface == null) {
      internalInterface = SlamFactory.eINSTANCE.createComInterface();
      internalInterface.setName(internalInterfaceName);
      internalComponentInstance.getComponent().getInterfaces().add(internalInterface);
    }
    port.setInternalInterface(internalInterface);

    design.getHierarchyPorts().add(port);
  }

  /**
   * Parses the hierarchical ports.
   *
   * @param parent
   *          the parent
   * @param design
   *          the design
   */
  private void parseHierarchicalPorts(final Element parent, final Design design) {
    Node node = parent.getFirstChild();
    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("spirit:hierConnection")) {
          parseHierarchicalPort(elt, design);
        }
      }
      node = node.getNextSibling();
    }

  }
}
