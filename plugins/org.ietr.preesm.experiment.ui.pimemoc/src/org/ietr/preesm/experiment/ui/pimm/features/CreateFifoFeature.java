package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

/**
 * Create feature to create a new Fifo in the Diagram
 * 
 * @author kdesnos
 * 
 */
public class CreateFifoFeature extends AbstractCreateConnectionFeature {

	private static final String FEATURE_NAME = "Fifo";

	private static final String FEATURE_DESCRIPTION = "Create Fifo";

	/**
	 * Default constructor for the {@link CreateFifoFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateFifoFeature(IFeatureProvider fp) {
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// This function is called when selecting the end of a created
		// connection.
		// We assume that the canStartConnection is already true

		// Refresh to remove all remaining tooltip;
		getDiagramEditor().refresh();

		// True if the connection is created between an input and an output port
		Port target = getPort(context.getTargetAnchor());
		boolean targetOK = (target != null && target instanceof InputPort);
		if (targetOK) {
			// Check that no Fifo is connected to the ports
			if (((InputPort) target).getIncomingFifo() == null) {
				return true;
			} else {
				// Create tooltip message
				PiMMUtil.setToolTip(getFeatureProvider(), context
						.getTargetAnchor().getGraphicsAlgorithm(),
						getDiagramEditor(),
						"A port cannot be connected to several FIFOs");
				return false;
			}
		}

		// False if the target is an outputPort
		if (target != null && target instanceof OutputPort) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor()
					.getGraphicsAlgorithm(), getDiagramEditor(),
					"A FIFO cannot end at an output port");
			return false;
		}

		// False if the target is an outputPort
		if (target != null && target instanceof ConfigInputPort) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor()
					.getGraphicsAlgorithm(), getDiagramEditor(),
					"A FIFO cannot end at an config. input port");
			return false;
		}

		// Check if the target can create a port
		boolean targetCanCreatePort = (canCreatePort(
				context.getTargetPictogramElement(), "input") != null);

		// The method also returns true if the the target can
		// create a new port.
		if ((targetCanCreatePort || targetOK)) {
			return true;
		}

		return false;
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
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		// get Ports which should be connected
		Anchor sourceAnchor = context.getSourceAnchor();
		Anchor targetAnchor = context.getTargetAnchor();
		Port source = getPort(sourceAnchor);
		Port target = getPort(targetAnchor);

		// Create the sourcePort if needed
		if (source == null) {
			PictogramElement sourcePe = context.getSourcePictogramElement();
			AbstractAddActorPortFeature addPortFeature = canCreatePort(
					sourcePe, "output");
			if (addPortFeature != null) {
				CustomContext sourceContext = new CustomContext(
						new PictogramElement[] { sourcePe });
				addPortFeature.execute(sourceContext);
				sourceAnchor = addPortFeature.getCreatedAnchor();
				source = addPortFeature.getCreatedPort();
			}
		}

		// Create the targetPort if needed
		if (target == null) {
			PictogramElement targetPe = context.getTargetPictogramElement();
			AbstractAddActorPortFeature addPortFeature = canCreatePort(
					targetPe, "input");
			if (addPortFeature != null) {
				CustomContext targetContext = new CustomContext(
						new PictogramElement[] { targetPe });
				addPortFeature.execute(targetContext);
				targetAnchor = addPortFeature.getCreatedAnchor();
				target = addPortFeature.getCreatedPort();
			}
		}

		if (source != null && target != null && source instanceof OutputPort
				&& target instanceof InputPort) {
			// create new business object
			Fifo fifo = createFifo((OutputPort) source, (InputPort) target);

			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					sourceAnchor, targetAnchor);
			addContext.setNewObject(fifo);
			newConnection = (Connection) getFeatureProvider().addIfPossible(
					addContext);
		}

		return newConnection;
	}

	@Override
	public void endConnecting() {
		// Refresh to remove all remaining tooltip;
		getDiagramEditor().refresh();
		super.endConnecting();
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// Return true if the connection starts at an output port
		Port source = getPort(context.getSourceAnchor());
		if (source != null && source instanceof OutputPort) {
			// Check that no Fifo is connected to the ports
			if (((OutputPort) source).getOutgoingFifo() == null) {
				return true;
			} else {
				// Create tooltip message
				PiMMUtil.setToolTip(getFeatureProvider(), context
						.getSourceAnchor().getGraphicsAlgorithm(),
						getDiagramEditor(),
						"A port cannot be connected to several FIFOs");
				return false;
			}
		}

		if (source != null
				&& (source instanceof InputPort || source instanceof ConfigInputPort)) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor()
					.getGraphicsAlgorithm(), getDiagramEditor(),
					"A FIFO cannot start at an input port");
			return false;
		}

		// Also true if the source is a vertex that can create ports
		if (canCreatePort(context.getSourcePictogramElement(), "output") != null) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a {@link Fifo} between the two {@link Port}s. Also add the
	 * created {@link Fifo} to the {@link Graph} of the current {@link Diagram}.
	 * 
	 * @param source
	 *            the source {@link OutputPort} of the {@link Fifo}
	 * @param target
	 *            the target {@link InputPort} of the {@link Fifo}
	 * @return the created {@link Fifo}
	 */
	protected Fifo createFifo(OutputPort source, InputPort target) {

		// Refresh to remove all remaining tooltip;
		getDiagramEditor().refresh();

		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Create the Fifo
		Fifo fifo = PiMMFactory.eINSTANCE.createFifo();
		fifo.setSourcePort(source);
		fifo.setTargetPort(target);

		// Add the new Fifo to the graph
		graph.getFifos().add(fifo);

		return fifo;
	}
}
