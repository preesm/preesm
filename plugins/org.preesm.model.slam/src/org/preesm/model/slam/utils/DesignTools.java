/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2014)
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
package org.preesm.model.slam.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.preesm.model.slam.ComponentHolder;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.attributes.Parameter;
import org.preesm.model.slam.component.Component;
import org.preesm.model.slam.component.Operator;
import org.preesm.model.slam.component.impl.ComNodeImpl;
import org.preesm.model.slam.link.Link;

/**
 * Provides specific getters and setters for S-LAM architecture.
 *
 * @author mpelcat
 */
public class DesignTools {

  /** Value used to state a non-existing component. */
  public static final ComponentInstance NO_COMPONENT_INSTANCE = null;

  /** Key of instance parameter used to store a property used in Preesm. */
  public static final String OPERATOR_BASE_ADDRESS = "BaseAddress";

  /**
   * Comparing two components using their names.
   */
  public static class ComponentInstanceComparator implements Comparator<ComponentInstance> {

    /*
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ComponentInstance cb1, ComponentInstance cb2) {
      final String o1 = cb1.getInstanceName();
      final String o2 = cb2.getInstanceName();

      final String o1StringPart = o1.replaceAll("\\d", "");
      final String o2StringPart = o2.replaceAll("\\d", "");

      if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
        return extractInt(o1) - extractInt(o2);
      } else {
        return o1.compareTo(o2);
      }
    }

    int extractInt(String s) {
      String num = s.replaceAll("\\D", "");
      // return 0 if no digits found
      return num.isEmpty() ? 0 : Integer.parseInt(num);
    }

  }

  /**
   * Getting all operator instance ids in architecture.
   *
   * @param design
   *          the design
   * @return the operator instance ids
   */
  public static List<String> getOperatorInstanceIds(final Design design) {
    final List<String> operatorInstanceIds = new ArrayList<>();
    if (design != null) {
      for (final ComponentInstance cmpInstance : getOperatorInstances(design)) {
        operatorInstanceIds.add(cmpInstance.getInstanceName());
      }
    }
    return operatorInstanceIds;
  }

  /**
   * Getting all communication node instance ids in architecture.
   *
   * @param design
   *          the design
   * @return the com node instance ids
   */
  public static List<String> getComNodeInstanceIds(final Design design) {
    final List<String> operatorInstanceIds = new ArrayList<>();
    if (design != null) {
      for (final ComponentInstance cmpInstance : getComNodeInstances(design)) {
        operatorInstanceIds.add(cmpInstance.getInstanceName());
      }
    }

    return operatorInstanceIds;
  }

  /**
   * Getting all communication node instance ids in architecture.
   *
   * @param design
   *          the design
   * @return the com node instance ids
   */
  public static List<ComponentInstance> getComNodeInstances(final Design design) {
    final List<ComponentInstance> operatorInstanceIds = new ArrayList<>();
    if (design != null) {
      for (final ComponentInstance cmpInstance : design.getComponentInstances()) {
        if (cmpInstance.getComponent() instanceof ComNodeImpl) {
          operatorInstanceIds.add(cmpInstance);
        }
      }
    }
    return operatorInstanceIds;
  }

  /**
   * Getting all operator instances in architecture.
   *
   * @param design
   *          the design
   * @return the operator instances
   */
  public static List<ComponentInstance> getOperatorInstances(final Design design) {
    final List<ComponentInstance> operatorInstances = new ArrayList<>();
    for (final ComponentInstance cmpInstance : design.getComponentInstances()) {
      if (cmpInstance.getComponent() instanceof Operator) {
        operatorInstances.add(cmpInstance);
      }
    }
    return operatorInstances;
  }

  /**
   * Getting all operator ids in architecture.
   *
   * @param design
   *          the design
   * @return the operator component ids
   */
  public static List<String> getOperatorComponentIds(final Design design) {
    final List<String> operatorIds = new ArrayList<>();
    for (final org.preesm.model.slam.component.Component component : getOperatorComponents(design)) {
      operatorIds.add(component.getVlnv().getName());
    }
    return operatorIds;
  }

  /**
   * Getting all operator instances in architecture.
   *
   * @param design
   *          the design
   * @return the operator components
   */
  public static List<Component> getOperatorComponents(final Design design) {
    final List<Component> operators = new ArrayList<>();
    if (design != null) {
      final ComponentHolder componentHolder = design.getComponentHolder();
      if (componentHolder != null) {
        for (final org.preesm.model.slam.component.Component component : componentHolder.getComponents()) {
          if (component instanceof Operator) {
            operators.add(component);
          }
        }
      }
    }
    return operators;
  }

  /**
   * Getting all component instances in architecture.
   *
   * @param design
   *          the design
   * @return the component instances
   */
  public static List<ComponentInstance> getComponentInstances(final Design design) {
    final List<ComponentInstance> instances = new ArrayList<>();
    for (final ComponentInstance cmpInstance : design.getComponentInstances()) {
      instances.add(cmpInstance);
    }
    return instances;
  }

  /**
   * Getting the number of operator instances in architecture.
   *
   * @param design
   *          the design
   * @return the number of operator instances
   */
  public static int getNumberOfOperatorInstances(final Design design) {
    return DesignTools.getOperatorInstances(design).size();
  }

  /**
   * Testing the presence of an instance in a list based on instance names.
   *
   * @param instances
   *          the instances
   * @param instance
   *          the instance
   * @return true, if successful
   */
  public static boolean contains(final List<ComponentInstance> instances, final ComponentInstance instance) {
    for (final ComponentInstance cmpInstance : instances) {
      if ((instance != null) && cmpInstance.getInstanceName().equals(instance.getInstanceName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Intersecting lists based on instance names.
   *
   * @param instances
   *          the instances
   * @param intersectInstances
   *          the intersect instances
   */
  public static void retainAll(final List<ComponentInstance> instances,
      final List<ComponentInstance> intersectInstances) {
    final Iterator<ComponentInstance> iterator = instances.iterator();
    while (iterator.hasNext()) {
      final ComponentInstance current = iterator.next();

      if (!DesignTools.contains(intersectInstances, current)) {
        iterator.remove();
      }
    }
  }

  /**
   * Getting the instance of the given name.
   *
   * @param design
   *          the design
   * @param name
   *          the name
   * @return the component instance
   */
  public static ComponentInstance getComponentInstance(final Design design, final String name) {

    for (final ComponentInstance cmpInstance : design.getComponentInstances()) {
      if (cmpInstance.getInstanceName().equals(name)) {
        return cmpInstance;
      }
    }

    return null;
  }

  /**
   * Getting all instances of a given component.
   *
   * @param design
   *          the design
   * @param component
   *          the component
   * @return the instances of component
   */
  public static List<ComponentInstance> getInstancesOfComponent(final Design design, final Component component) {
    final List<ComponentInstance> instances = new ArrayList<>();
    for (final ComponentInstance cmpInstance : design.getComponentInstances()) {
      if (cmpInstance.getComponent().getVlnv().getName().equals(component.getVlnv().getName())) {
        instances.add(cmpInstance);
      }
    }
    return instances;
  }

  /**
   * Getting a component parameter corresponding to the given key.
   *
   * @param instance
   *          the instance
   * @param key
   *          the key
   * @return the parameter
   */
  public static String getParameter(final ComponentInstance instance, final String key) {
    for (final Parameter p : instance.getParameters()) {
      if (p.getKey().equals(key)) {
        return p.getValue();
      }
    }
    return null;
  }

  /**
   * Getting the other extremity component of a link.
   *
   * @param link
   *          the link
   * @param c
   *          the c
   * @return the other end
   */
  public static ComponentInstance getOtherEnd(final Link link, final ComponentInstance c) {
    if (!link.getDestinationComponentInstance().getInstanceName().equals(c.getInstanceName())) {
      return link.getDestinationComponentInstance();
    } else {
      return link.getSourceComponentInstance();
    }
  }

  /**
   * All undirected links linked to instance c.
   *
   * @param design
   *          the design
   * @param c
   *          the c
   * @return the undirected links
   */
  public static List<Link> getUndirectedLinks(final Design design, final ComponentInstance c) {
    final List<Link> undirectedLinks = new ArrayList<>();
    for (final Link link : design.getLinks()) {
      if (!link.isDirected() && link.getDestinationComponentInstance().getInstanceName().equals(c.getInstanceName())
          || link.getSourceComponentInstance().getInstanceName().equals(c.getInstanceName())) {
        undirectedLinks.add(link);
      }
    }
    return undirectedLinks;
  }

  /**
   * All undirected links linked to instance c.
   *
   * @param design
   *          the design
   * @param c
   *          the c
   * @return the outgoing directed links
   */
  public static List<Link> getOutgoingDirectedLinks(final Design design, final ComponentInstance c) {
    final List<Link> directedLinks = new ArrayList<>();
    for (final Link link : design.getLinks()) {
      if (link.isDirected() && link.getSourceComponentInstance().getInstanceName().equals(c.getInstanceName())) {
        directedLinks.add(link);
      }
    }
    return directedLinks;
  }

  /**
   * All undirected links linked to instance c.
   *
   * @param design
   *          the design
   * @param c
   *          the c
   * @return the incoming directed links
   */
  public static List<Link> getIncomingDirectedLinks(final Design design, final ComponentInstance c) {
    final List<Link> directedLinks = new ArrayList<>();
    for (final Link link : design.getLinks()) {
      if (link.isDirected() && link.getDestinationComponentInstance().getInstanceName().equals(c.getInstanceName())) {
        directedLinks.add(link);
      }
    }
    return directedLinks;
  }
}
