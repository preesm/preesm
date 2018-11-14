/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
/**
 *
 */
package org.ietr.dftools.architecture.slam.process;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.eclipse.emf.common.util.EList;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.SlamFactory;
import org.ietr.dftools.architecture.slam.attributes.AttributesFactory;
import org.ietr.dftools.architecture.slam.attributes.Parameter;
import org.ietr.dftools.architecture.slam.component.ComInterface;
import org.ietr.dftools.architecture.slam.component.Component;
import org.ietr.dftools.architecture.slam.component.HierarchyPort;
import org.ietr.dftools.architecture.slam.link.ControlLink;
import org.ietr.dftools.architecture.slam.link.DataLink;
import org.ietr.dftools.architecture.slam.link.Link;
import org.ietr.dftools.architecture.slam.link.LinkFactory;
import org.ietr.dftools.architecture.utils.SlamException;

/**
 * Methods to flatten the hierarchy of a System-Level Architecture Model. If multiple refinements are available for a
 * component, the first is selected.
 *
 * @author mpelcat
 */
public class SlamFlattener {

  /**
   * Flattens all levels of a hierarchical architecture.
   *
   * @param design
   *          the design
   */
  public void flattenAllLevels(final Design design) {

    while (hasHierarchy(design)) {
      flattenUpperLevel(design);
    }
  }

  /**
   * Flattens n levels of a hierarchical architecture.
   *
   * @param design
   *          the design
   * @param n
   *          the n
   */
  public void flatten(final Design design, final int n) {
    int i = 0;
    while (i < n) {
      flattenUpperLevel(design);
      i++;
    }
  }

  /**
   * Flattens the upper level of a hierarchical architecture.
   *
   * @param design
   *          the design
   */
  public void flattenUpperLevel(final Design design) {

    // Set of removed subdesigns
    final Set<Design> removedSubdesigns = new LinkedHashSet<>();

    // Replace each instance by its content
    final List<ComponentInstance> componentInstances = new ArrayList<>(design.getComponentInstances());

    for (final ComponentInstance instance : componentInstances) {
      if (!instance.getComponent().getRefinements().isEmpty()) {
        removedSubdesigns.add(instance.getComponent().getRefinements().get(0));
        replaceInstanceByContent(design, instance);
      }
    }

    // Removing all references to components no more instanciated
    cleanComponentHolder(design);
  }

  /**
   * Gets the all instances.
   *
   * @param design
   *          the design
   * @param globalInstances
   *          the global instances
   * @return the all instances
   */
  private void getAllInstances(final Design design, final Set<ComponentInstance> globalInstances) {

    for (final ComponentInstance instance : design.getComponentInstances()) {
      globalInstances.add(instance);
      for (final Design subDesign : instance.getComponent().getRefinements()) {
        getAllInstances(subDesign, globalInstances);
      }
    }
  }

  /**
   * Removing all references to components no more instanciated.
   *
   * @param design
   *          reference design
   * @param removedSubdesigns
   *          subdesigns containing instances to eliminate
   */
  private void cleanComponentHolder(final Design design) {

    // Getting all instances and their components from the design and its
    // subdesigns
    final Set<ComponentInstance> globalInstances = new LinkedHashSet<>();
    final Set<Component> globalComponents = new LinkedHashSet<>();

    getAllInstances(design, globalInstances);
    for (final ComponentInstance instance : globalInstances) {
      globalComponents.add(instance.getComponent());
    }

    final Set<Component> holderComponents = new LinkedHashSet<>(design.getComponentHolder().getComponents());
    for (final Component component : holderComponents) {
      // Remove all references to instances of the removed hierarchy level
      if (!globalComponents.contains(component)) {
        design.getComponentHolder().getComponents().remove(component);
      }
    }
  }

  /**
   * Replaces a component instance in a design by its content (components and links).
   *
   * @param design
   *          the design
   * @param instance
   *          the instance
   */
  private void replaceInstanceByContent(final Design design, final ComponentInstance instance) {
    // Associates a reference instance in the refinement to each cloned
    // instance in the design.
    final Map<ComponentInstance, ComponentInstance> refMap = new LinkedHashMap<>();
    final Component component = instance.getComponent();
    final Design subDesign = component.getRefinements().get(0);

    insertComponentInstancesClones(subDesign.getComponentInstances(), design, instance, refMap);
    insertInternalLinksClones(subDesign.getLinks(), design, refMap);

    // Before removing the instance, hierarchical connections are managed if
    // possible (moved from the instance to its content)
    manageHierarchicalLinks(instance, design, subDesign, refMap);

    // Remove the instance and, if needed, the component itself

    // We remove the replaced instance from the top level
    design.getComponentInstances().remove(instance);

    // We remove the replaced instance link from its component
    final Component refComponent = instance.getComponent();
    refComponent.getInstances().remove(instance);

    // If the component has no more instance, it is also removed
    if (refComponent.getInstances().isEmpty()) {
      design.getComponentHolder().getComponents().remove(refComponent);
    }
  }

  /**
   * Links the newly created instances appropriately to respect hierarchy.
   *
   * @param instance
   *          the instance
   * @param design
   *          the design
   * @param subDesign
   *          the sub design
   * @param refMap
   *          the ref map
   */
  private void manageHierarchicalLinks(final ComponentInstance instance, final Design design, final Design subDesign,
      final Map<ComponentInstance, ComponentInstance> refMap) {

    // Iterating the upper graph links
    final Set<Link> links = new LinkedHashSet<>(design.getLinks());
    for (final Link link : links) {
      if (link.getSourceComponentInstance().equals(instance)) {
        manageSourceHierarchicalLink(link, design, subDesign, refMap);
      } else if (link.getDestinationComponentInstance().equals(instance)) {
        manageDestinationHierarchicalLink(link, design, subDesign, refMap);
      }
    }
  }

  /**
   * Links the newly created instances appropriately to respect hierarchy.
   *
   * @param link
   *          the link
   * @param design
   *          the design
   * @param subDesign
   *          the sub design
   * @param refMap
   *          the ref map
   */
  private void manageSourceHierarchicalLink(final Link link, final Design design, final Design subDesign,
      final Map<ComponentInstance, ComponentInstance> refMap) {
    HierarchyPort foundPort = null;

    // Looking for the hierarchy port corresponding to the current upper
    // level link
    for (final HierarchyPort port : subDesign.getHierarchyPorts()) {
      if (port.getExternalInterface().equals(link.getSourceInterface())) {
        foundPort = port;
      }
    }

    // In case we found the internal hierarchy port corresponding to the
    // port in the upper graph
    if (foundPort != null) {
      final ComponentInstance instanceToConnect = refMap.get(foundPort.getInternalComponentInstance());
      final ComInterface itf = foundPort.getInternalInterface();
      link.setSourceComponentInstance(instanceToConnect);
      link.setSourceInterface(itf);
    } else {
      throw new SlamException("Could not find port");
    }
  }

  /**
   * Links the newly created instances appropriately to respect hierarchy.
   *
   * @param link
   *          the link
   * @param design
   *          the design
   * @param subDesign
   *          the sub design
   * @param refMap
   *          the ref map
   */
  private void manageDestinationHierarchicalLink(final Link link, final Design design, final Design subDesign,
      final Map<ComponentInstance, ComponentInstance> refMap) {
    HierarchyPort foundPort = null;

    // Looking for the hierarchy port corresponding to the current upper
    // level link
    for (final HierarchyPort port : subDesign.getHierarchyPorts()) {
      if (port.getExternalInterface().equals(link.getDestinationInterface())) {
        foundPort = port;
      }
    }

    // In case we found the internal hierarchy port corresponding to the
    // port in the upper graph
    if (foundPort != null) {
      final ComponentInstance instanceToConnect = refMap.get(foundPort.getInternalComponentInstance());
      final ComInterface itf = foundPort.getInternalInterface();
      link.setDestinationComponentInstance(instanceToConnect);
      link.setDestinationInterface(itf);
    } else {
      throw new SlamException("Could not find port");
    }
  }

  /**
   * Inserts clones of the given instances in a given design.
   *
   * @param instances
   *          the instances
   * @param design
   *          the design
   * @param processedInstance
   *          the processed instance
   * @param refMap
   *          the ref map
   */
  private void insertComponentInstancesClones(final EList<ComponentInstance> instances, final Design design,
      final ComponentInstance processedInstance, final Map<ComponentInstance, ComponentInstance> refMap) {
    for (final ComponentInstance originalInstance : instances) {
      final String originalName = originalInstance.getInstanceName();
      final ComponentInstance newInstance = SlamFactory.eINSTANCE.createComponentInstance();
      final String newName = getUniqueInstanceName(originalName, design, processedInstance.getInstanceName());
      design.getComponentInstance(newName);
      newInstance.setInstanceName(newName);
      newInstance.setComponent(originalInstance.getComponent());
      design.getComponentInstances().add(newInstance);
      refMap.put(originalInstance, newInstance);

      // Duplicates instance parameters
      for (final Parameter param : originalInstance.getParameters()) {
        final Parameter newParam = AttributesFactory.eINSTANCE.createParameter();
        newParam.setKey(param.getKey());
        newParam.setValue(param.getValue());
        newInstance.getParameters().add(newParam);
      }
    }
  }

  /**
   * Inserts clones of the given links in a given design.
   *
   * @param links
   *          the links
   * @param design
   *          the design
   * @param refMap
   *          the ref map
   */
  private void insertInternalLinksClones(final EList<Link> links, final Design design,
      final Map<ComponentInstance, ComponentInstance> refMap) {

    for (final Link originalLink : links) {
      Link newLink = null;

      if (originalLink instanceof DataLink) {
        newLink = LinkFactory.eINSTANCE.createDataLink();
      } else if (originalLink instanceof ControlLink) {
        newLink = LinkFactory.eINSTANCE.createControlLink();
      } else {
        throw new SlamException("Unsupported link type");
      }

      newLink.setDirected(originalLink.isDirected());
      // Choosing a new unique Uuid
      newLink.setUuid(UUID.randomUUID().toString());
      final ComponentInstance source = originalLink.getSourceComponentInstance();
      final ComponentInstance destination = originalLink.getDestinationComponentInstance();
      newLink.setSourceComponentInstance(refMap.get(source));
      newLink.setSourceInterface(originalLink.getSourceInterface());
      newLink.setDestinationComponentInstance(refMap.get(destination));
      newLink.setDestinationInterface(originalLink.getDestinationInterface());
      design.getLinks().add(newLink);
    }
  }

  /**
   * Creates a unique name by prefixing the name by the upper design name.
   *
   * @param originalName
   *          the name in the original design
   * @param design
   *          the upper desing in which the component is instanciated
   * @param path
   *          the path to append to the name
   * @return the unique instance name
   */
  private String getUniqueInstanceName(final String originalName, final Design design, final String path) {
    String name = path + "/" + originalName;
    int i = 2;

    while (design.getComponentInstance(name) != null) {
      name = originalName + "_" + i;
      i++;
    }

    return name;
  }

  /**
   * Checks for hierarchy.
   *
   * @param design
   *          the design
   * @return true, if successful
   */
  private boolean hasHierarchy(final Design design) {

    for (final Component component : design.getComponentHolder().getComponents()) {
      if (!component.getRefinements().isEmpty()) {
        return true;
      }
    }

    return false;
  }
}
