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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.preesm.model.slam.ComponentHolder;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.component.Component;
import org.preesm.model.slam.component.Operator;
import org.preesm.model.slam.component.impl.ComNodeImpl;

/**
 * Provides specific getters and setters for S-LAM architecture.
 *
 * @author mpelcat
 */
public class DesignTools {

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
    return Collections.unmodifiableList(operatorInstanceIds);
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

    return Collections.unmodifiableList(operatorInstanceIds);
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
    return Collections.unmodifiableList(operatorInstanceIds);
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
    return Collections.unmodifiableList(operatorInstances);
  }

  /**
   * Gets the ordered operator ids.
   */
  public static List<String> getOrderedOperatorIds(final Design design) {
    final List<String> operatorComponentIds = new ArrayList<>(getOperatorInstanceIds(design));
    Collections.sort(operatorComponentIds, (o1, o2) -> o1.compareTo(o2));
    return Collections.unmodifiableList(operatorComponentIds);
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
    return Collections.unmodifiableList(operatorIds);
  }

  /**
   * Gets the ordered operator.
   *
   * @return the ordered operator
   */
  public static List<ComponentInstance> getOrderedOperators(final Design design) {
    final List<ComponentInstance> opIdList = new ArrayList<>(getOperatorInstances(design));
    Collections.sort(opIdList, (o1, o2) -> o1.getInstanceName().compareTo(o2.getInstanceName()));
    return Collections.unmodifiableList(opIdList);
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
    return Collections.unmodifiableList(operators);
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
    return Collections.unmodifiableList(instances);
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

}
