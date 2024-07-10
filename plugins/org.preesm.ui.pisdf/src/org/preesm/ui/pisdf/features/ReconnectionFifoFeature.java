/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2023)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2014)
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
package org.preesm.ui.pisdf.features;

import java.util.ArrayList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Port;
import org.preesm.ui.pisdf.features.helper.LayoutActorBendpoints;

// TODO: Auto-generated Javadoc
/**
 * Reconnection feature for the {@link Fifo}.
 *
 * @author kdesnos
 *
 */
public class ReconnectionFifoFeature extends DefaultReconnectionFeature implements LayoutActorBendpoints {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /**
   * Default constructor for the {@link ReconnectionFifoFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public ReconnectionFifoFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#canReconnect(org.eclipse.graphiti.features.context.
   * IReconnectionContext)
   */
  @Override
  public boolean canReconnect(final IReconnectionContext context) {

    if (context.getOldAnchor().equals(context.getNewAnchor())) {
      return true;
    }

    final Port newPort = getPort(context.getNewAnchor());
    final Port oldPort = getPort(context.getOldAnchor());
    if ((newPort != null) && newPort.getClass().equals(oldPort.getClass())) {
      // Check that no Fifo is connected to the ports
      if (newPort instanceof final DataOutputPort dop) {
        return dop.getOutgoingFifo() == null;
      }

      if (newPort instanceof final DataInputPort dip) {
        return dip.getIncomingFifo() == null;
      }
    }

    // Also true if the TargetPictogramElement is a vertex that can create ports
    return (CreateFifoFeature.canCreatePort(context.getTargetPictogramElement(), getFeatureProvider(),
        oldPort.getKind()) != null);
  }

  /**
   * Method to retrieve the {@link Port} corresponding to an {@link Anchor}.
   *
   * @param anchor
   *          the anchor to treat
   * @return the found {@link Port}, or <code>null</code> if no port corresponds to this {@link Anchor}
   */
  protected Port getPort(final Anchor anchor) {
    if (anchor != null) {
      final Object obj = getBusinessObjectForPictogramElement(anchor);
      if (obj instanceof final Port port) {
        return port;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
   */
  @Override
  public boolean hasDoneChanges() {
    return this.hasDoneChanges;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#postReconnect(org.eclipse.graphiti.features.context.
   * IReconnectionContext)
   */
  @Override
  public void postReconnect(final IReconnectionContext context) {
    // Apply changes to the BusinessModel
    // If we reconnect to the same anchor: nothing to do
    if (context.getOldAnchor().equals(context.getNewAnchor())) {
      return;
    }

    // Get the Ports
    final Port newPort = getPort(context.getNewAnchor());
    final Port oldPort = getPort(context.getOldAnchor());

    if (oldPort instanceof final DataOutputPort oldDop) {
      final Fifo fifo = oldDop.getOutgoingFifo();
      fifo.setSourcePort((DataOutputPort) newPort);
    }
    if (oldPort instanceof final DataInputPort oldDip) {
      final Fifo fifo = oldDip.getIncomingFifo();
      fifo.setTargetPort((DataInputPort) newPort);
    }

    // Call the move feature of the anchor owner to layout the connection
    final ContainerShape cs = (ContainerShape) context.getNewAnchor().getReferencedGraphicsAlgorithm()
        .getPictogramElement();
    layoutShapeConnectedToBendpoints(cs, this, new ArrayList<>());

    this.hasDoneChanges = true;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#preReconnect(org.eclipse.graphiti.features.context.
   * IReconnectionContext)
   */
  @Override
  public void preReconnect(final IReconnectionContext context) {
    // If we reconnect to the same anchor: nothing to do
    if (context.getOldAnchor().equals(context.getNewAnchor())) {
      return;
    }

    // Get the Ports
    Port newPort = getPort(context.getNewAnchor());
    final Port oldPort = getPort(context.getOldAnchor());

    // If the reconnection involve the creation of a new port
    // Create it
    if (newPort == null) {
      final PictogramElement pe = context.getTargetPictogramElement();
      final AbstractAddActorPortFeature addPortFeature = CreateFifoFeature.canCreatePort(pe, getFeatureProvider(),
          oldPort.getKind());
      if (addPortFeature != null) {
        final CustomContext sourceContext = new CustomContext(new PictogramElement[] { pe });
        addPortFeature.execute(sourceContext);
        ((ReconnectionContext) context).setNewAnchor(addPortFeature.getCreatedAnchor());
        newPort = addPortFeature.getCreatedPort();
      }
    }

    // If the user canceled the creation of the new port, cancel the
    // reconnection
    if (newPort == null) {
      ((ReconnectionContext) context).setNewAnchor(context.getOldAnchor());
    }
  }

}
