package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.OutputPort;
import org.ietr.preesm.experiment.model.pimemoc.SourceInterface;

/**
 * Add feature to add a new {@link SourceInterface} to the {@link Graph}
 * 
 * @author kdesnos
 * 
 */
public class AddSourceInterfaceFeature extends AbstractAddFeature {

	public static final IColorConstant SRC_TEXT_FOREGROUND = IColorConstant.BLACK;

	public static final IColorConstant SRC_FOREGROUND = AddInputPortFeature.INPUT_PORT_FOREGROUND;

	public static final IColorConstant SRC_BACKGROUND = AddInputPortFeature.INPUT_PORT_BACKGROUND;

	/**
	 * The default constructor of {@link AddSourceInterfaceFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public AddSourceInterfaceFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		SourceInterface srcInterface = (SourceInterface) context.getNewObject();
		OutputPort port = srcInterface.getOutputPorts().get(0);
		Diagram targetDiagram = (Diagram) context.getTargetContainer();

		// CONTAINER SHAPE WITH ROUNDED RECTANGLE
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(
				targetDiagram, true);

		// define a default size for the shape
		int width = 16;
		int height = 16;
		int invisibRectHeight = 20;
		IGaService gaService = Graphiti.getGaService();

		Rectangle invisibleRectangle = gaService
				.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(invisibleRectangle, context.getX(),
				context.getY(), 0, invisibRectHeight);

		RoundedRectangle roundedRectangle; // need to access it later
		{
			final BoxRelativeAnchor boxAnchor = peCreateService
					.createBoxRelativeAnchor(containerShape);
			boxAnchor.setRelativeWidth(1.0);
			boxAnchor
					.setRelativeHeight((((double) invisibRectHeight - (double) height))
							/ 2.0 / (double) invisibRectHeight);
			boxAnchor.setReferencedGraphicsAlgorithm(invisibleRectangle);

			// create and set graphics algorithm for the anchor
			roundedRectangle = gaService
					.createRoundedRectangle(boxAnchor, 5, 5);
			roundedRectangle.setForeground(manageColor(SRC_FOREGROUND));
			roundedRectangle.setBackground(manageColor(SRC_BACKGROUND));
			roundedRectangle.setLineWidth(2);
			gaService.setLocationAndSize(roundedRectangle, -width, 0, width,
					height);

			// if added SourceInterface has no resource we add it to the
			// resource of the graph
			if (srcInterface.eResource() == null) {
				Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
				graph.addInterfaceVertex(srcInterface);
			}
			link(boxAnchor, port);
		}

		// Name of the SrcInterface - SHAPE WITH TEXT
		{
			// create and set text graphics algorithm
			Text text = gaService.createText(invisibleRectangle,
					srcInterface.getName());
			text.setForeground(manageColor(SRC_TEXT_FOREGROUND));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
			// vertical alignment has as default value "center"
			text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
			text.setHeight(20);
		}
		// create link and wire it
		link(containerShape, srcInterface);

		// Call the layout feature
		layoutPictogramElement(containerShape);

		return containerShape;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		// Check that the user wants to add an Actor to the Diagram
		return context.getNewObject() instanceof SourceInterface
				&& context.getTargetContainer() instanceof Diagram;
	}

}
