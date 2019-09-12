/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
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

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamDMARouteStep;
import org.preesm.model.slam.SlamMemoryRouteStep;
import org.preesm.model.slam.SlamMessageRouteStep;
import org.preesm.model.slam.SlamPackage;
import org.preesm.model.slam.SlamRoute;
import org.preesm.model.slam.SlamRouteStep;
import org.preesm.model.slam.VLNV;
import org.preesm.model.slam.impl.SlamFactoryImpl;

/**
 *
 * @author anmorvan
 *
 */
public class SlamUserFactory extends SlamFactoryImpl {

  public static final SlamUserFactory eINSTANCE = new SlamUserFactory();

  private SlamUserFactory() {
    // Not meant to be instantiated: use static methods.
  }

  /**
   *
   */
  public Component createComponent(final VLNV name, final String componentType) {
    final EClass eClass = (EClass) SlamPackage.eINSTANCE.getEClassifier(componentType);
    final Component component = (Component) super.create(eClass);
    component.setVlnv(name);
    return component;
  }

  /**
   */
  public SlamRoute createSlamRoute(final SlamRouteStep step) {
    final SlamRoute res = super.createSlamRoute();
    res.getRouteSteps().add(step);
    return res;
  }

  /**
   * Instantiates a new route.
   *
   * @param r1
   *          the r 1
   * @param r2
   *          the r 2
   */
  public SlamRoute createSlamRoute(final SlamRoute r1, final SlamRoute r2) {
    final SlamRoute res = super.createSlamRoute();
    res.getRouteSteps().addAll(r1.getRouteSteps());
    res.getRouteSteps().addAll(r2.getRouteSteps());
    return res;
  }

  /**
   *
   */
  public final SlamRouteStep createRouteStep(final Design archi, final ComponentInstance source,
      final List<ComponentInstance> nodes, final ComponentInstance target) {
    SlamRouteStep step = null;

    final ComponentInstance dma = SlamCommunicationFinder.getDma(archi, nodes, source);
    final ComponentInstance mem = SlamCommunicationFinder.getRam(archi, nodes, source);
    if (dma != null) {
      step = createSlamDMARouteStep(source, nodes, target, dma);
    } else if (mem != null) {
      step = createSlamMemoryRouteStep(source, nodes, target, mem,
          SlamCommunicationFinder.getRamNodeIndex(archi, nodes));
    } else {
      step = createSlamMessageRouteStep(source, nodes, target);
    }

    return step;
  }

  /**
   *
   */
  public SlamDMARouteStep createSlamDMARouteStep(final ComponentInstance sender, final List<ComponentInstance> nodes,
      final ComponentInstance receiver, final ComponentInstance dma) {
    final SlamDMARouteStep res = super.createSlamDMARouteStep();
    res.setReceiver(receiver);
    res.setSender(sender);
    res.getNodes().addAll(nodes);

    res.setDma(dma);
    return res;
  }

  /**
   *
   */
  public SlamMemoryRouteStep createSlamMemoryRouteStep(final ComponentInstance sender,
      final List<ComponentInstance> nodes, final ComponentInstance receiver, final ComponentInstance mem,
      final int ramNodeIndex) {
    final SlamMemoryRouteStep res = super.createSlamMemoryRouteStep();
    res.setReceiver(receiver);
    res.setSender(sender);
    res.getNodes().addAll(nodes);

    res.setMemory(mem);
    res.setRamNodeIndex(ramNodeIndex);
    return res;
  }

  /**
   *
   */
  public SlamMessageRouteStep createSlamMessageRouteStep(final ComponentInstance sender,
      final List<ComponentInstance> nodes, final ComponentInstance receiver) {
    final SlamMessageRouteStep res = super.createSlamMessageRouteStep();
    res.setReceiver(receiver);
    res.setSender(sender);
    res.getNodes().addAll(nodes);
    return res;
  }
}
