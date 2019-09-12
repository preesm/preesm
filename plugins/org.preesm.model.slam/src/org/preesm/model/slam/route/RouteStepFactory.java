/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2012)
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
package org.preesm.model.slam.route;

import java.util.List;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.ControlLink;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.Link;
import org.preesm.model.slam.impl.DmaImpl;
import org.preesm.model.slam.impl.MemImpl;

/**
 * Depending on the architecture nodes separating two operators, generates a suited route step. The route steps
 * represents one type of connection between two connected operators
 *
 * @author mpelcat
 */
public class RouteStepFactory {

  /**
   * Gets the dma corresponding to the step if any exists. The Dma must have a setup link with the source.
   *
   * @param nodes
   *          the nodes
   * @param dmaSetup
   *          the dma setup
   * @return the dma
   */
  public static final ComponentInstance getDma(final Design archi, final List<ComponentInstance> nodes,
      final ComponentInstance dmaSetup) {
    ComponentInstance dmaInst = null;
    for (final ComponentInstance node : nodes) {
      for (final Link i : archi.getLinks()) {
        if (i.getSourceComponentInstance() == node || i.getDestinationComponentInstance() == node) {
          if (i.getSourceComponentInstance().getComponent() instanceof DmaImpl) {
            dmaInst = i.getSourceComponentInstance();
          }
          if (i.getDestinationComponentInstance().getComponent() instanceof DmaImpl) {
            dmaInst = i.getDestinationComponentInstance();
          }

          if (dmaInst != null && existSetup(archi, dmaInst, dmaSetup)) {
            return dmaInst;
          }
        }
      }
    }
    return null;
  }

  /**
   * Gets the ram corresponding to the step if any exists. The ram must have a setup link with the source.
   *
   * @param nodes
   *          the nodes
   * @param ramSetup
   *          the ram setup
   * @return the ram
   */
  public static final ComponentInstance getRam(final Design archi, final List<ComponentInstance> nodes,
      final ComponentInstance ramSetup) {
    ComponentInstance ramInst = null;
    for (final ComponentInstance node : nodes) {
      for (final Link i : archi.getLinks()) {
        if (i.getSourceComponentInstance() == node || i.getDestinationComponentInstance() == node) {
          if (i.getSourceComponentInstance().getComponent() instanceof MemImpl) {
            ramInst = i.getSourceComponentInstance();
          }
          if (i.getDestinationComponentInstance().getComponent() instanceof MemImpl) {
            ramInst = i.getDestinationComponentInstance();
          }

          if (ramInst != null && existSetup(archi, ramInst, ramSetup)) {
            return ramInst;
          }
        }
      }
    }
    return null;

  }

  /**
   * Gets the ram corresponding to the step if any exists. The ram must have a setup link with the source.
   *
   * @param nodes
   *          the nodes
   * @return the ram node index
   */
  public static final int getRamNodeIndex(final Design archi, final List<ComponentInstance> nodes) {
    ComponentInstance ramInst = null;
    for (final ComponentInstance node : nodes) {
      for (final Link i : archi.getLinks()) {
        if (i.getSourceComponentInstance() == node || i.getDestinationComponentInstance() == node) {
          if (i.getSourceComponentInstance().getComponent() instanceof MemImpl) {
            ramInst = i.getSourceComponentInstance();
          }
          if (i.getDestinationComponentInstance().getComponent() instanceof MemImpl) {
            ramInst = i.getDestinationComponentInstance();
          }

          if (ramInst != null) {
            return nodes.indexOf(node);
          }
        }
      }
    }
    return -1;
  }

  /**
   * Checks if a setup link exists between cmp and operator.
   *
   * @param target
   *          the cmp
   * @param source
   *          the op
   * @return true, if successful
   */
  private static final boolean existSetup(final Design archi, final ComponentInstance target,
      final ComponentInstance source) {

    for (final Link i : archi.getLinks()) {
      if (i.getSourceComponentInstance() == source && i.getDestinationComponentInstance() == target
          && (i instanceof ControlLink)) {
        return true;
      }
    }

    return false;
  }
}
