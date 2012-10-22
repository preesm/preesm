package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimemoc.Fifo;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.InputPort;
import org.ietr.preesm.experiment.model.pimemoc.OutputPort;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory;
import org.ietr.preesm.experiment.model.pimemoc.Port;

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
		// True if the connection is created between an input and an output port
		Port source = getPort(context.getSourceAnchor());
		Port target = getPort(context.getTargetAnchor());
		if (source != null && target != null && source instanceof OutputPort
				&& target instanceof InputPort) {
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
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		// get EClasses which should be connected
		Port source = getPort(context.getSourceAnchor());
		Port target = getPort(context.getTargetAnchor());

		if (source != null && target != null && source instanceof OutputPort
				&& target instanceof InputPort) {
			// create new business object
			Fifo fifo = createFifo((OutputPort) source, (InputPort) target);

			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(
					context.getSourceAnchor(), context.getTargetAnchor());
			addContext.setNewObject(fifo);
			newConnection = (Connection) getFeatureProvider().addIfPossible(
					addContext);
		}

		return newConnection;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// Return true if the connection starts at an output port
		Port source = getPort(context.getSourceAnchor());
		if (source != null && source instanceof OutputPort) {
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
		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Create the Fifo
		Fifo fifo = PIMeMoCFactory.eINSTANCE.createFifo();
		fifo.setSourcePort(source);
		fifo.setTargetPort(target);

		// Add the new Fifo to the graph
		graph.getFifos().add(fifo);

		return fifo;
	}
}
