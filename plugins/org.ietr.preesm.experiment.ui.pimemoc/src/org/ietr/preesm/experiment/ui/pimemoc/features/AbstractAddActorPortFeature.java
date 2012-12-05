package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PortNameValidator;
import org.ietr.preesm.experiment.ui.pimemoc.util.PimemocUtil;

/**
 * Feature called to create and add a port to an actor
 * 
 * @author kdesnos
 * 
 */
public abstract class AbstractAddActorPortFeature extends AbstractCustomFeature {

	/**
	 * Position of the port
	 * 
	 * @author kdesnos
	 * 
	 */
	public enum PortPosition {
		LEFT, RIGHT
	}

	/**
	 * Size of the GA of the anchor
	 */
	public static final int PORT_ANCHOR_GA_SIZE = 8;

	public static final IColorConstant PORT_BACKGROUND = IColorConstant.BLACK;

	public static int PORT_FONT_HEIGHT;

	/**
	 * Size of the space between the label of a port and the GA
	 */
	public static final int PORT_LABEL_GA_SPACE = 2;

	public static final IColorConstant PORT_TEXT_FOREGROUND = IColorConstant.BLACK;

	/**
	 * Store the created port
	 */
	protected Port createdPort = null;

	protected Anchor createdAnchor = null;

	protected boolean hasDoneChanges = false;

	public AbstractAddActorPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	/**
	 * Add a GraphicAlgorithm of the port.
	 * 
	 * @param containerShape
	 *            the shape containing the port {@link GraphicsAlgorithm}.
	 * 
	 * @return the graphic algorithm
	 */
	public abstract GraphicsAlgorithm addPortGA(GraphicsAlgorithm containerShape);

	/**
	 * Add a label to the port
	 * 
	 * @param containerShape
	 *            the shape containing the port
	 * @param label
	 *            the name of the port
	 * @return
	 */
	public abstract GraphicsAlgorithm addPortLabel(
			GraphicsAlgorithm containerShape, String portName);

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow if exactly one pictogram element
		// representing an Actor is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		// Re-check if only one element is selected
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			// Retrieve the container shape (corresponding to the actor)
			ContainerShape containerShape = (ContainerShape) pes[0];
			// Retrieve the rectangle graphic algorithm
			GraphicsAlgorithm gaRectangle = pes[0].getGraphicsAlgorithm();
			// Get the PeCreateService
			IPeCreateService peCreateService = Graphiti.getPeCreateService();
			// Get the GaService
			IGaService gaService = Graphiti.getGaService();
			// Get the actor
			Actor actor = (Actor) getBusinessObjectForPictogramElement(containerShape);

			// Ask the name of the new port
			String portName = "newPort";

			portName = PimemocUtil.askString(this.getName(), this
					.getDescription(), portName, new PortNameValidator(actor,
					null, this.getPortKind()));
			if (portName == null) {
				this.hasDoneChanges = false;
				return;
			}

			// create an box relative anchor
			final BoxRelativeAnchor boxAnchor = peCreateService
					.createBoxRelativeAnchor(containerShape);
			createdAnchor = boxAnchor;
			if (this.getPosition() == PortPosition.LEFT) {
				boxAnchor.setRelativeWidth(0.0);
			} else {
				boxAnchor.setRelativeWidth(1.0);
			}
			boxAnchor.setRelativeHeight(1.0); // The height will be fixed by the
												// layout feature
			boxAnchor.setReferencedGraphicsAlgorithm(gaRectangle);

			// Get the new Port and add it to the Graph
			Port newPort = this.getNewPort(portName, actor);
			createdPort = newPort;

			// create invisible rectangle
			Rectangle invisibleRectangle = gaService
					.createInvisibleRectangle(boxAnchor);

			// Add a text label for the box relative anchor
			this.addPortLabel(invisibleRectangle, portName);

			// add a graphics algorithm for the box relative anchor
			this.addPortGA(invisibleRectangle);

			// link the Pictogram element to the port in the business model
			link(boxAnchor, newPort);

			// Layout the port
			layoutPictogramElement(boxAnchor);

			// Layout the actor
			layoutPictogramElement(containerShape);
			updatePictogramElement(containerShape);

			this.hasDoneChanges = true;
		}
	}

	/**
	 * Get the {@link Anchor} created by the feature
	 * 
	 * @return the {@link Anchor}, or <code>null</code> if not port was created.
	 */
	public Anchor getCreatedAnchor() {
		return createdAnchor;
	}

	/**
	 * Get the {@link Port} created by the feature
	 * 
	 * @return the {@link Port}, or <code>null</code> if not port was created.
	 */
	public Port getCreatedPort() {
		return createdPort;
	}

	/**
	 * Create a new port for the given actor
	 * 
	 * @param portName
	 *            the name of the new port to create
	 * @param actor
	 *            the actor to which we add a port
	 * @return the new port, or <code>null</code> if something went wrong
	 */
	public abstract Port getNewPort(String portName, Actor actor);

	/**
	 * Get the font of the port
	 * 
	 * @return the font
	 */
	public Font getPortFont() {
		// Get the GaService
		IGaService gaService = Graphiti.getGaService();
		Font font = gaService.manageDefaultFont(getDiagram(), false, false);

		PORT_FONT_HEIGHT = GraphitiUi.getUiLayoutService()
				.calculateTextSize("Abcq", font).getHeight();

		return font;
	}

	/**
	 * Get the port of the created port
	 * 
	 * @return the kind of the port
	 */
	public abstract String getPortKind();

	/**
	 * Retrieve the {@link PortPosition} of the port
	 * 
	 * @return the PortPosition
	 */
	public abstract PortPosition getPosition();

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}
