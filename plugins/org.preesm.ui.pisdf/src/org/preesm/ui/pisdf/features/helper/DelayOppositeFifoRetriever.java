/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019 - 2023)
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
package org.preesm.ui.pisdf.features.helper;

import java.util.List;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;

/**
 * This interface provides a way to retrieve Fifo opposite connection when there is a delay on it.
 *
 * @author ahonorat
 *
 */
public interface DelayOppositeFifoRetriever {

  /**
   * Retrieve the target connection when fifo contains a delay
   *
   * @param delay
   *          Delay object.
   * @param delayFeature
   *          Parent delay of the Fifo.
   * @param connec
   *          Half of the Fifo, source part
   * @return The target connection.
   */
  default Connection getTargetConnection(AbstractFeature af, Delay delay, AnchorContainer delayFeature,
      Connection connec) {
    final ChopboxAnchor cba = (ChopboxAnchor) delayFeature.getAnchors().get(0);
    final List<Connection> outgoingConnections = cba.getOutgoingConnections();
    Connection targetConnection = null;
    for (final Connection connection : outgoingConnections) {
      // copy of AbstractFeatureImpl code since getBusinessObjectForPictogramElement is protected
      final Object obj = af.getFeatureProvider().getBusinessObjectForPictogramElement(connection);
      // With setter delay, there can be multiple FIFOs
      // We have to choose the correct one
      if (obj instanceof final Fifo fifo && (fifo.getDelay() == delay) && connection != connec) {
        targetConnection = connection;
        break;
      }
    }
    return targetConnection;
  }

  /**
   * Retrieve the source connection when fifo contains a delay
   *
   * @param delay
   *          Delay object.
   * @param delayFeature
   *          Parent delay of the Fifo.
   * @param connec
   *          Half of the Fifo, source part
   * @return the source connection.
   */
  default Connection getSourceConnection(AbstractFeature af, Delay delay, AnchorContainer delayFeature,
      Connection connec) {
    final ChopboxAnchor cba = (ChopboxAnchor) delayFeature.getAnchors().get(0);
    final List<Connection> incomingConnections = cba.getIncomingConnections();
    Connection sourceConnection = null;
    for (final Connection connection : incomingConnections) {
      // copy of AbstractFeatureImpl code since getBusinessObjectForPictogramElement is protected
      final Object obj = af.getFeatureProvider().getBusinessObjectForPictogramElement(connection);
      // With setter delay, there can be multiple FIFOs
      // We have to choose the correct one
      if (obj instanceof final Fifo fifo && (fifo.getDelay() == delay) && connection != connec) {
        sourceConnection = connection;
        break;
      }
    }
    return sourceConnection;
  }

}
