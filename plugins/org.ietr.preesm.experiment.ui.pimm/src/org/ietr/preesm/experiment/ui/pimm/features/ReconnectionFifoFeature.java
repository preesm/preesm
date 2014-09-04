/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * Reconnection feature for the {@link Fifo}.
 * 
 * @author kdesnos
 * 
 */
public class ReconnectionFifoFeature extends DefaultReconnectionFeature {

	protected boolean hasDoneChanges = false;

	/**
	 * Default constructor for the {@link ReconnectionFifoFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public ReconnectionFifoFeature(IFeatureProvider fp) {
		super(fp);
	}

	

	@Override
	public boolean canReconnect(IReconnectionContext context) {

		if (context.getOldAnchor().equals(context.getNewAnchor())) {
			return true;
		}

		Port newPort = getPort(context.getNewAnchor());
		Port oldPort = getPort(context.getOldAnchor());
		if (newPort != null && newPort.getClass().equals(oldPort.getClass())) {
			// Check that no Fifo is connected to the ports
			if (newPort instanceof DataOutputPort) {
				if (((DataOutputPort) newPort).getOutgoingFifo() == null) {
					return true;
				} else {
					return false;
				}
			}

			if (newPort instanceof DataInputPort) {
				if (((DataInputPort) newPort).getIncomingFifo() == null) {
					return true;
				} else {
					return false;
				}
			}
		}

		// Also true if the TargetPictogramElement is a vertex that can create
		// ports
		if (CreateFifoFeature.canCreatePort(context.getTargetPictogramElement(), getFeatureProvider(), 
				oldPort.getKind()) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Method to retrieve the {@link Port} corresponding to an {@link Anchor}
	 * 
	 * @param anchor
	 *            the anchor to treat
	 * @return the found {@link Port}, or <code>null</code> if no port
	 *         corresponds to this {@link Anchor}
	 */
	protected Port getPort(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor);
			if (obj instanceof Port) {
				return (Port) obj;
			}
		}
		return null;
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

	@Override
	public void postReconnect(IReconnectionContext context) {
		// Apply changes to the BusinessModel
		// If we reconnect to the same anchor: nothing to do
		if (context.getOldAnchor().equals(context.getNewAnchor())) {
			return;
		}

		// Get the Ports
		Port newPort = getPort(context.getNewAnchor());
		Port oldPort = getPort(context.getOldAnchor());

		if (oldPort instanceof DataOutputPort) {
			Fifo fifo = ((DataOutputPort) oldPort).getOutgoingFifo();
			fifo.setSourcePort((DataOutputPort) newPort);
		}
		if (oldPort instanceof DataInputPort) {
			Fifo fifo = ((DataInputPort) oldPort).getIncomingFifo();
			fifo.setTargetPort((DataInputPort) newPort);
		}

		// Call the move feature of the anchor owner to layout the connection
		MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature(
				getFeatureProvider());
		ContainerShape cs = (ContainerShape) context.getNewAnchor()
				.getReferencedGraphicsAlgorithm().getPictogramElement();
		MoveShapeContext moveCtxt = new MoveShapeContext(cs);
		moveCtxt.setDeltaX(0);
		moveCtxt.setDeltaY(0);
		ILocation csLoc = Graphiti.getPeLayoutService()
				.getLocationRelativeToDiagram(cs);
		moveCtxt.setLocation(csLoc.getX(), csLoc.getY());
		moveFeature.moveShape(moveCtxt);

		hasDoneChanges = true;
	}

	@Override
	public void preReconnect(IReconnectionContext context) {
		// If we reconnect to the same anchor: nothing to do
		if (context.getOldAnchor().equals(context.getNewAnchor())) {
			return;
		}

		// Get the Ports
		Port newPort = getPort(context.getNewAnchor());
		Port oldPort = getPort(context.getOldAnchor());

		// If the reconnection involve the creation of a new port
		// Create it
		if (newPort == null) {
			PictogramElement pe = context.getTargetPictogramElement();
			AbstractAddActorPortFeature addPortFeature = CreateFifoFeature.canCreatePort(pe, getFeatureProvider(), 
					oldPort.getKind());
			if (addPortFeature != null) {
				CustomContext sourceContext = new CustomContext(
						new PictogramElement[] { pe });
				addPortFeature.execute(sourceContext);
				((ReconnectionContext) context).setNewAnchor(addPortFeature
						.getCreatedAnchor());
				newPort = addPortFeature.getCreatedPort();
			}
		}

		// If the user canceled the creation of the new port, cancel the
		// reconnection
		if (newPort == null) {
			((ReconnectionContext) context)
					.setNewAnchor(context.getOldAnchor());
		}
		return;
	}

}
