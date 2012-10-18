package org.ietr.preesm.experiment.ui.pimemoc.features;

import java.io.ObjectInputStream.GetField;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimemoc.Actor;

/**
 * Feature called to create and add a port to an actor
 * 
 * @author kdesnos
 * 
 */
public abstract class AbstractAddActorPortFeature extends AbstractCustomFeature {

	/**
	 * Position of the port
	 * @author kdesnos
	 *
	 */
	public enum PortPosition {
		LEFT,
		RIGHT
	}

	public static final IColorConstant PORT_BACKGROUND = IColorConstant.BLACK;

	public static final IColorConstant PORT_TEXT_FOREGROUND = IColorConstant.BLACK;

	public AbstractAddActorPortFeature(IFeatureProvider fp) {
		super(fp);
	}

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
			// Retrieve the container shape
			ContainerShape containerShape = (ContainerShape) pes[0];
			// Retrieve the rectangle graphic algorithm
			GraphicsAlgorithm gaRectangle = pes[0].getGraphicsAlgorithm();
			// Get the PeCreateService
			IPeCreateService peCreateService = Graphiti.getPeCreateService();
			// Get the GaService
			IGaService gaService = Graphiti.getGaService();

			// create an box relative anchor
			final BoxRelativeAnchor boxAnchor = peCreateService
					.createBoxRelativeAnchor(containerShape);
			if(this.getPosition() == PortPosition.LEFT)
			{
				boxAnchor.setRelativeWidth(0.0);
			} else {
				boxAnchor.setRelativeWidth(1.0);
			}
			boxAnchor.setRelativeHeight(0.5); // use golden section
			boxAnchor.setReferencedGraphicsAlgorithm(gaRectangle);
			

			// create invisible rectangle
	        Rectangle invisibleRectangle =
	            gaService.createInvisibleRectangle(boxAnchor);
	        gaService.setLocationAndSize(invisibleRectangle, -30, 0, 30, 14);
	        //invisibleRectangle.setForeground(manageColor(this.getForegreoundColor()));
			//invisibleRectangle.setBackground(manageColor(this.getBackgroundColor()));
			
			// add a rectangle graphics algorithm for the box relative anchor
			final Rectangle rectangle = gaService
					.createPlainRectangle(invisibleRectangle);
			rectangle.setForeground(manageColor(this.getForegreoundColor()));
			rectangle.setBackground(manageColor(this.getBackgroundColor()));
			rectangle.setLineWidth(1);
			gaService.setSize(rectangle, 8, 8);
			gaService.setLocation(rectangle,invisibleRectangle.getWidth()-8, 3);
			
			// Add a text label for the box relative anchor
			final Text text = gaService.createText(invisibleRectangle);
			text.setValue("La");
			text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
			text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
			text.setForeground(manageColor(PORT_TEXT_FOREGROUND));
			gaService.setLocationAndSize(text, 0, 0, 22, 14);

			// Update the Pictogram element
			updatePictogramElement(containerShape);

			// Call the layout feature
			layoutPictogramElement(containerShape);
		}
	}

	/**
	 * Retrieve the background color of the port
	 * 
	 * @return the color
	 */
	public abstract IColorConstant getBackgroundColor();
	
	/**
	 * Retrieve the foreground color of the port
	 * 
	 * @return the color
	 */
	public abstract IColorConstant getForegreoundColor();
	
	public abstract PortPosition getPosition();
}
