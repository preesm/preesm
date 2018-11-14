/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.preesm.ui.pimm.features;

import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;

/**
 * Delete feature to remove a {@link Delay} from a {@link Fifo}.
 *
 * @author kdesnos
 */
public class DeleteDelayFeature extends DeleteParameterizableFeature {

  /**
   * Default Constructor of the {@link DeleteDelayFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public DeleteDelayFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.ui.pimm.features.DeleteParameterizableFeature#preDelete(org.eclipse.graphiti.features.context.
   * IDeleteContext)
   */
  @Override
  public void preDelete(final IDeleteContext context) {

    // Transform the two connections linked to the delay back into a single
    // one. before deleting the delay.
    final PictogramElement pictogramElement = context.getPictogramElement();
    final Object[] allBusinessObjectsForPictogramElement = getAllBusinessObjectsForPictogramElement(pictogramElement);
    if (allBusinessObjectsForPictogramElement.length > 0) {
      // only disconnect if business delay exists.
      // this delay could have been already deleted by the delete actor feature when selecting multiple elements
      disconnectDelayFromFifo(context);
    }

    // Remove the contained delay actor
    final Delay delay = (Delay) getBusinessObjectForPictogramElement(pictogramElement);
    final DelayActor delayActor = delay.getActor();
    delayActor.getContainingPiGraph().removeActor(delayActor);

    // Super call to delete the dependencies linked to the delay
    // Do it after deleting the connection (if it exists) to avoid looping infinitely
    super.preDelete(context);
  }

  /**
   * Disconnect delay from fifo.
   *
   * @param context
   *          the context
   * @throws RuntimeException
   *           the runtime exception
   */
  public void disconnectDelayFromFifo(final IDeleteContext context) throws RuntimeException {
    // Retrieve the two connections
    final ContainerShape cs = (ContainerShape) context.getPictogramElement();
    final ChopboxAnchor cba = (ChopboxAnchor) cs.getAnchors().get(0);
    final List<Connection> incomingConnections = cba.getIncomingConnections();
    // There can be dependency incoming connection. Find the unique fifo
    // incoming connection
    Connection preConnection = null;
    for (final Connection connection : incomingConnections) {
      final Object obj = getBusinessObjectForPictogramElement(connection);
      // With setter delay, there can be multiple FIFOs
      // We have to choose the correct one
      if (obj instanceof Fifo && (((Fifo) obj).getDelay() != null)) {
        preConnection = connection;
        break;
      }
    }
    if (preConnection == null) {
      throw new IllegalStateException();
    }
    // There may be multiple connections if the delay has a getter
    final List<Connection> outgoingConnections = cba.getOutgoingConnections();
    Connection postConnection = null;
    // We look for the connection with the same object as the pre connection
    final Object preConnectionObj = getBusinessObjectForPictogramElement(preConnection);
    for (final Connection connection : outgoingConnections) {
      final Object obj = getBusinessObjectForPictogramElement(connection);
      if (obj == preConnectionObj) {
        postConnection = connection;
        break;
      }
    }

    if (postConnection == null) {
      throw new IllegalStateException();
    }

    // Copy the bendpoints to the unique remaining connection.
    // Reconnect it.
    final EList<Point> preBendPoints = ((FreeFormConnection) preConnection).getBendpoints();
    final EList<Point> postBendPoints = ((FreeFormConnection) postConnection).getBendpoints();
    postBendPoints.addAll(0, preBendPoints);
    postConnection.setStart(preConnection.getStart());

    // Remove the preConnection (but not the associated Fifo)
    final IRemoveContext rmCtxt = new RemoveContext(preConnection);
    final IRemoveFeature rmFeature = getFeatureProvider().getRemoveFeature(rmCtxt);
    if (rmFeature.canRemove(rmCtxt)) {
      rmFeature.remove(rmCtxt);
    } else {
      throw new RuntimeException("Could not delete Delay because a Connection could not be removed.");
    }
  }

}
