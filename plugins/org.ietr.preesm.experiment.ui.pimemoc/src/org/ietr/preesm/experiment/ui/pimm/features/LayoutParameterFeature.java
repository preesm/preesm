package org.ietr.preesm.experiment.ui.pimm.features;

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
import org.ietr.preesm.experiment.model.pimm.Parameter;

/**
 * Layout Feature for {@link Parameter}s
 * 
 * @author kdesnos
 * 
 */
public class LayoutParameterFeature extends AbstractLayoutFeature {

	/**
	 * The Height of the parameter triangle is not modified when layouting the
	 * {@link Parameter}.
	 */
	protected int height;

	/**
	 * Default constructor of the {@link LayoutParameterFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public LayoutParameterFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to a Parameter
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape)) {
			return false;
		}

		EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
		return businessObjects.size() == 1
				&& businessObjects.get(0) instanceof Parameter;
	}

	/**
	 * Return the new width of the shape. <br>
	 * <br>
	 * The new width is computed so that all text are completely visible.
	 * Consequently, the method check the width and height of the Text children
	 * shape. <br>
	 * <br>
	 * <b> The method does not apply the new width to the shape. </b> Use
	 * {@link LayoutParameterFeature#setNewWith} for that purpose.
	 * 
	 * @param childrenShapes
	 *            the children shapes of the Actor
	 * @return the new width
	 */
	protected int getNewWidth(EList<Shape> childrenShapes) {
		// RETRIEVE THE NAME WIDTH
		IDimension nameSize = null;
		{
			// Scan the children shape looking for the parameter name
			for (Shape shape : childrenShapes) {
				GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
				// The name should be the only children with type text
				if (child instanceof Text) {
					String text = ((Text) child).getValue();
					Font font = ((Text) child).getFont();

					// Retrieve the size of the text
					nameSize = GraphitiUi.getUiLayoutService()
							.calculateTextSize(text, font);
				}
			}
		}

		return nameSize.getWidth() + 6;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		// Retrieve the shape and the graphic algorithm
		ContainerShape containerShape = (ContainerShape) context
				.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
		height = containerGa.getHeight(); // The height is constant

		// Retrieve all contained shapes
		EList<Shape> childrenShapes = containerShape.getChildren();

		int newWidth = getNewWidth(childrenShapes);

		if (newWidth != containerGa.getWidth()) {
			setNewWidth(newWidth, childrenShapes);
			containerGa.setWidth(newWidth);
			return true;
		}

		return false;
	}

	/**
	 * Apply the new width to the shape children
	 * 
	 * @param newWidth
	 *            the new width to apply
	 * @param childrenShapes
	 *            the children shapes
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
			}
		}
	}
}
