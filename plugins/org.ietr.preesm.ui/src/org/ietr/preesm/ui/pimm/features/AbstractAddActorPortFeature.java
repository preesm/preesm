/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

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
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PortNameValidator;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;

/**
 * Feature called to create and add a port to an actor
 * 
 * @author kdesnos
 * 
 */
public abstract class AbstractAddActorPortFeature extends AbstractCustomFeature {

	/**
	 * The {@link ICustomContext} given to the {@link #execute(ICustomContext)}
	 * method can be associated to properties. The {@link #NAME_PROPERTY} key is
	 * associated to a {@link String} that should be used as a name for the
	 * created port, thus bypassing the need to ask for a port name to the user.
	 */
	public static final String NAME_PROPERTY = "name";

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
			if (bo instanceof ExecutableActor) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		execute(context, "newPort");
	}
	
	public void execute(ICustomContext context, String portName) {
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
			ExecutableActor actor = (ExecutableActor) getBusinessObjectForPictogramElement(containerShape);

			// If a name was given in the property, bypass the dialog box
			Object nameProperty = context.getProperty(NAME_PROPERTY);
			if (nameProperty != null && nameProperty instanceof String) {
				portName = (String) nameProperty;
			} else {
				portName = PiMMUtil.askString(this.getName(), this
						.getDescription(), portName, new PortNameValidator(
						actor, null));
				if (portName == null) {
					this.hasDoneChanges = false;
					return;
				}
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
			boxAnchor.setRelativeHeight(0.5); // The height will be fixed by the
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
	public abstract Port getNewPort(String portName, ExecutableActor actor);

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
