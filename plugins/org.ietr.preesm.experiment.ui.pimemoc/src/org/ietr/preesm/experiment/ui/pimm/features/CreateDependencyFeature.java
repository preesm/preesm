package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

/**
 * Create feature to create a new {@link Dependency} in the {@link Diagram}.
 * 
 * @author kdesnos
 * 
 */
public class CreateDependencyFeature extends AbstractCreateConnectionFeature {

	protected boolean hasDoneChanges = false;

	private static final String FEATURE_NAME = "Dependency";

	private static final String FEATURE_DESCRIPTION = "Create Dependency";

	/**
	 * The default constructor of the {@link CreateDependencyFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateDependencyFeature(IFeatureProvider fp) {
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// This function is called when selecting the end of a created
		// dependency. We assume that the canStartConnection is already true.

		// Refresh to remove all remaining tooltip;
		getDiagramEditor().refresh();

		// True if the target is a ConfigInputPort
		Port target = getPort(context.getTargetAnchor());
		boolean targetOK = (target != null && target instanceof ConfigInputPort);
		if (targetOK) {
			// Check that no dependency is connected to the ports
			if (((ConfigInputPort) target).getIncomingDependency() == null) {
				return true;
			} else {
				// Create tooltip message
				PiMMUtil.setToolTip(getFeatureProvider(), context
						.getTargetAnchor().getGraphicsAlgorithm(),
						getDiagramEditor(),
						"A config port cannot be connected to several Dependencies");
				return false;
			}
		}

		// True if the target is "Parameterizable"
		PictogramElement targetPE = context.getTargetPictogramElement();
		Object targetObj = getBusinessObjectForPictogramElement(targetPE);
		if (targetObj instanceof Parameterizable) {
			return true;
		}

		// False if the target is an outputPort
		if (target != null
				&& (target instanceof OutputPort || target instanceof InputPort)) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getTargetAnchor()
					.getGraphicsAlgorithm(), getDiagramEditor(),
					"A Dependency cannot end at a data port");
			return false;
		}

		// TODO Check if the target can create a port

		return false;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {

		// Refresh to remove all remaining tooltip;
		getDiagramEditor().refresh();

		// Return true if the connection starts at a ISetter
		ISetter setter = getSetter(context.getSourceAnchor());

		if (setter != null) {
			return true;
		}

		Anchor anchor = context.getSourceAnchor();
		Object obj = getBusinessObjectForPictogramElement(anchor);
		if (obj instanceof InputPort || obj instanceof ConfigInputPort) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor()
					.getGraphicsAlgorithm(), getDiagramEditor(),
					"A Dependency cannot start at an input port");
			return false;
		}

		if (obj instanceof OutputPort) {
			// Create tooltip message
			PiMMUtil.setToolTip(getFeatureProvider(), context.getSourceAnchor()
					.getGraphicsAlgorithm(), getDiagramEditor(),
					"A Dependency cannot start at an data output port");
			return false;
		}

		return false;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		// We suppose that this method is called only if the canStart and
		// canCreate methods were both positive

		Connection newConnection = null;

		// get anchors which should be connected
		Anchor setterAnchor = context.getSourceAnchor();
		Anchor getterAnchor = context.getTargetAnchor();
		ISetter setter = getSetter(setterAnchor);
		Port getter = getPort(getterAnchor);

		// If setter is null, something went wrong
		// TODO implement the creation of configOutputPort
		if (setter == null) {
			return null;
		}

		// If getter port is null
		if (getter == null) {
			// If the target is a Parameterizable item
			// Create a configInputPort
			PictogramElement tgtPE = context.getTargetPictogramElement();
			Object tgtObj = getBusinessObjectForPictogramElement(tgtPE);
			if (tgtObj instanceof Parameterizable) {
				// TODO implement the creation of configInputPort
			}
			return null;
		}

		// Create new business object
		Dependency dependendy = createDependency(setter,
				(ConfigInputPort) getter);
		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(
				setterAnchor, getterAnchor);
		addContext.setNewObject(dependendy);
		newConnection = (Connection) getFeatureProvider().addIfPossible(
				addContext);
		hasDoneChanges = true;

		return newConnection;
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

	/**
	 * Method to retrieve the {@link ISetter} corresponding to an {@link Anchor}
	 * 
	 * @param anchor
	 *            the {@link Anchor} to treat
	 * @return the found {@link ISetter}, or <code>null</code> if no
	 *         {@link ISetter} corresponds to this {@link Anchor}
	 */
	protected ISetter getSetter(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor);
			if (obj instanceof ISetter) {
				return (ISetter) obj;
			}
		}
		return null;
	}

	@Override
	public boolean hasDoneChanges() {
		return hasDoneChanges;
	}

	/**
	 * Creates a {@link Dependency} between the {@link ISetter} and the
	 * {@link ConfigInputPort}. Also add the created {@link Dependency} to the
	 * {@link Graph} of the current {@link Diagram}.
	 * 
	 * @param setter
	 *            the source {@link ISetter} of the {@link Dependency}
	 * @param getter
	 *            the target {@link ConfigInputPort} of the {@link Dependency}
	 * @return the created {@link Dependency}
	 */
	protected Dependency createDependency(ISetter setter, ConfigInputPort getter) {

		// Refresh to remove all remaining tooltip;
		getDiagramEditor().refresh();

		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Create the Fifo
		Dependency dependency = PiMMFactory.eINSTANCE.createDependency();
		dependency.setSetter(setter);
		dependency.setGetter(getter);

		// Add the new Fifo to the graph
		graph.getDependencies().add(dependency);

		return dependency;
	}

}
