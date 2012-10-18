package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.pimemoc.Actor;

/**
 * Layout Feature for Actors
 * 
 * @author kdesnos
 * 
 */
public class LayoutActorFeature extends AbstractLayoutFeature {

	/**
	 * Default Constructor of the {@link LayoutActorFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public LayoutActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an EClass
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape)) {
			return false;
		}

		EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
		return businessObjects.size() == 1
				&& businessObjects.get(0) instanceof Actor;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;

		// Retrieve the shape and the graphic algorithm
		ContainerShape containerShape = (ContainerShape) context
				.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

		// Retrieve all contained shapes
		EList<Shape> childrenShapes = containerShape.getChildren();

		// Get the new Width of the shape
		int newWidth = getNewWidth(childrenShapes);

		// Apply change if newWidth is different from the current
		if (newWidth != containerGa.getWidth()) {
			setNewWidth(newWidth, childrenShapes);
			containerGa.setWidth(newWidth);
			anythingChanged = true;
		}

		return anythingChanged;
	}

	/**
	 * Apply the new width of the shape. <br>
	 * <br>
	 * 
	 * The new width is computed is applied to all children.
	 * 
	 * @param newWidth
	 *            the new width of the actor
	 * @param childrenShapes
	 *            the children shapes to resize
	 */
	protected void setNewWidth(int newWidth, EList<Shape> childrenShapes) {
		// Scan the children shapes
		for (Shape shape : childrenShapes) {
			GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
			if (child instanceof Text) {
				Orientation align = ((Text) child).getHorizontalAlignment();

				// If the text is the name of the object
				if (align == Orientation.ALIGNMENT_CENTER) {
					// The name is centered and has the same width as the actor
					// rounded rectangle
					child.setWidth(newWidth);
				}

				// TODO resize ports size if needed
			}
		}
	}

	/**
	 * Return the new width of the shape. <br>
	 * <br>
	 * 
	 * The new width is computed so that all text are completely visible.
	 * Consequently, the method check the width of all Text children shape.
	 * 
	 * @param childrenShapes
	 * @return
	 */
	protected int getNewWidth(EList<Shape> childrenShapes) {
		int nameWidth = 0;
		// Scan the children shape
		for (Shape shape : childrenShapes) {
			GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
			if (child instanceof Text) {
				Orientation align = ((Text) child).getHorizontalAlignment();
				String text = ((Text) child).getValue();
				Font font = ((Text) child).getFont();

				// Retrieve the size of the text
				IDimension size = GraphitiUi.getUiLayoutService()
						.calculateTextSize(text, font);

				// If the text is the name of the object
				if (align == Orientation.ALIGNMENT_CENTER) {
					// Retrieve the space of the name
					// (+20 to add space and lighten the actor representation)
					nameWidth = size.getWidth() + 20;
				}

				// TODO take ports size into account
			}
		}
		return nameWidth;
	}
}
