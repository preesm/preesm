package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.ui.pimm.util.Triangle;

/**
 * Add feature to add a {@link Parameter} to the Diagram
 * 
 * @author kdesnos
 * 
 */
public class AddParameterFeature extends AbstractAddFeature {

	public static final IColorConstant PARAMETER_TEXT_FOREGROUND = IColorConstant.BLACK;

	public static final IColorConstant PARAMETER_FOREGROUND = new ColorConstant(
			98, 131, 167);

	public static final IColorConstant PARAMETER_BACKGROUND = new ColorConstant(
			187, 218, 247);

	/**
	 * Default constructor of the {@link AddParameterFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public AddParameterFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		// Check that the user wants to add a Parameter to the Diagram
		return context.getNewObject() instanceof Parameter
				&& context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		Parameter addedParameter = (Parameter) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		// CONTAINER SHAPE WITH Triangle
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(
				targetDiagram, true);

		// define a default size for the shape
		int width = 80;
		int height = 40;
		IGaService gaService = Graphiti.getGaService();

		Polygon triangle;
		{
			triangle = new Triangle(containerShape);
			triangle.setBackground(manageColor(PARAMETER_BACKGROUND));
			triangle.setForeground(manageColor(PARAMETER_FOREGROUND));
			triangle.setLineWidth(2);
			gaService.setLocationAndSize(triangle, context.getX(),
					context.getY(), width, height);

			// if added Class has no resource we add it to the resource
			// of the graph
			if (addedParameter.eResource() == null) {
				Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
				graph.getParameters().add(addedParameter);
			}

			// create link and wire it
			link(containerShape, addedParameter);
		}

		// Name of the actor - SHAPE WITH TEXT
		{
			// create shape for text
			Shape shape = peCreateService.createShape(containerShape, false);

			// create and set text graphics algorithm
			Text text = gaService.createText(shape, addedParameter.getName());
			text.setForeground(manageColor(PARAMETER_TEXT_FOREGROUND));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			
			// vertical alignment has as default value "center"
			text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
			text.getWidth();
			gaService.setLocationAndSize(text, 0, height-18, width, 20);

			// create link and wire it
			link(shape, addedParameter);
		}

		return containerShape;
	}

}
