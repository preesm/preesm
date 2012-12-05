package org.ietr.preesm.experiment.ui.pimemoc.features;

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
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
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

	/**
	 * Method to check whether it is possible to create a {@link Port} for the
	 * given source/target {@link PictogramElement}
	 * 
	 * @param pe
	 *            the {@link PictogramElement} tested
	 * @param direction
	 *            the direction of the port we want to create ("input" or
	 *            "output")
	 * @return an {@link AbstractAddActorPortFeature} if the given
	 *         {@link PictogramElement} can create a {@link Port} with the given
	 *         direction. Return <code>null</code> else.
	 */
	protected AbstractAddActorPortFeature canCreatePort(PictogramElement pe,
			String direction) {
		boolean canCreatePort = false;
		PictogramElement peSource = pe;

		// Create the FeatureProvider
		CustomContext sourceContext = new CustomContext(
				new PictogramElement[] { peSource });
		AbstractAddActorPortFeature addPortFeature = null;
		if (direction.equals("input")) {
			addPortFeature = new AddInputPortFeature(getFeatureProvider());
		}
		if (direction.equals("output")) {
			addPortFeature = new AddOutputPortFeature(getFeatureProvider());
		}
		if (addPortFeature != null) {
			canCreatePort = addPortFeature.canExecute(sourceContext);
		}
		if (canCreatePort) {
			return addPortFeature;
		} else {
			return null;
		}
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
			if (newPort instanceof OutputPort) {
				if (((OutputPort) newPort).getOutgoingFifo() == null) {
					return true;
				} else {
					return false;
				}
			}

			if (newPort instanceof InputPort) {
				if (((InputPort) newPort).getIncomingFifo() == null) {
					return true;
				} else {
					return false;
				}
			}
		}

		// Also true if the TargetPictogramElement is a vertex that can create
		// ports
		if (canCreatePort(context.getTargetPictogramElement(),
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

		if (oldPort instanceof OutputPort) {
			Fifo fifo = ((OutputPort) oldPort).getOutgoingFifo();
			fifo.setSourcePort((OutputPort) newPort);
		}
		if (oldPort instanceof InputPort) {
			Fifo fifo = ((InputPort) oldPort).getIncomingFifo();
			fifo.setTargetPort((InputPort) newPort);
		}

		// Call the move feature of the anchor owner to layout the connection
		MoveAbstractVertexFeature moveFeature = new MoveAbstractVertexFeature(
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
			AbstractAddActorPortFeature addPortFeature = canCreatePort(pe,
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
