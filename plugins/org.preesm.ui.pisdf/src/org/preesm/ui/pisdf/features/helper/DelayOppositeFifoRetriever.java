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
   * @return
   */
  default Connection getTargetConnection(AbstractFeature af, Delay delay, AnchorContainer delayFeature,
      Connection connec) {
    final ChopboxAnchor cba = (ChopboxAnchor) delayFeature.getAnchors().get(0);
    final List<Connection> outgoingConnections = cba.getOutgoingConnections();
    Connection targetConnection = null;
    for (final Connection connection : outgoingConnections) {
      // copy of AbstractFeatureImpl code since getBusinessObjectForPictogramElement is protected
      Object obj = af.getFeatureProvider().getBusinessObjectForPictogramElement(connection);
      // With setter delay, there can be multiple FIFOs
      // We have to choose the correct one
      if (obj instanceof Fifo && (((Fifo) obj).getDelay() == delay) && connection != connec) {
        targetConnection = connection;
        break;
      }
    }
    return targetConnection;
  }

  /**
   * Retrieve the target connection when fifo contains a delay
   * 
   * @param delay
   *          Delay object.
   * @param delayFeature
   *          Parent delay of the Fifo.
   * @param connec
   *          Half of the Fifo, source part
   * @return
   */
  default Connection getSourceConnection(AbstractFeature af, Delay delay, AnchorContainer delayFeature,
      Connection connec) {
    final ChopboxAnchor cba = (ChopboxAnchor) delayFeature.getAnchors().get(0);
    final List<Connection> incomingConnections = cba.getIncomingConnections();
    Connection sourceConnection = null;
    for (final Connection connection : incomingConnections) {
      // copy of AbstractFeatureImpl code since getBusinessObjectForPictogramElement is protected
      Object obj = af.getFeatureProvider().getBusinessObjectForPictogramElement(connection);
      // With setter delay, there can be multiple FIFOs
      // We have to choose the correct one
      if (obj instanceof Fifo && (((Fifo) obj).getDelay() == delay) && connection != connec) {
        sourceConnection = connection;
        break;
      }
    }
    return sourceConnection;
  }

}
