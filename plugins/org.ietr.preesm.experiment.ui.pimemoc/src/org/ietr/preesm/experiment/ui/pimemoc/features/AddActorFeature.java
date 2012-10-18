package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
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
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.model.pimemoc.Graph;

public class AddActorFeature extends AbstractAddFeature {
	
	public static final IColorConstant ACTOR_TEXT_FOREGROUND = IColorConstant.BLACK;

	public static final IColorConstant ACTOR_FOREGROUND = new ColorConstant(
			98, 131, 167);

	public static final IColorConstant ACTOR_BACKGROUND = new ColorConstant(
			187, 218, 247);

	public AddActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		// Check that the user wants to add an Actor to the Diagram
		return context.getNewObject() instanceof Actor && context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		Actor addedActor = (Actor) context.getNewObject();
        Diagram targetDiagram = (Diagram) context.getTargetContainer();
 
        // CONTAINER SHAPE WITH ROUNDED RECTANGLE
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        ContainerShape containerShape =
             peCreateService.createContainerShape(targetDiagram, true);
 
        // define a default size for the shape
        //TODO: Automatic size as a function of the content
        int width = 100;
        int height = 50;
        IGaService gaService = Graphiti.getGaService();
        
        RoundedRectangle roundedRectangle; // need to access it later
        {
            // create and set graphics algorithm
            roundedRectangle =
                gaService.createRoundedRectangle(containerShape, 5, 5);
            roundedRectangle.setForeground(manageColor(ACTOR_FOREGROUND));
            roundedRectangle.setBackground(manageColor(ACTOR_BACKGROUND));
            roundedRectangle.setLineWidth(2);
            gaService.setLocationAndSize(roundedRectangle,
                context.getX(), context.getY(), width, height);
 
            // if added Class has no resource we add it to the resource
            // of the diagram
            // in a real scenario the business model would have its own resource
            if (addedActor.eResource() == null) {
            	Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
                graph.getVertices().add(addedActor);
            }
            // create link and wire it
            link(containerShape, addedActor);
        }
 
 
        // Name of the actor - SHAPE WITH TEXT
        {
            // create shape for text
            Shape shape = peCreateService.createShape(containerShape, false);
 
            // create and set text graphics algorithm
            Text text = gaService.createText(shape, addedActor.getName());
            text.setForeground(manageColor(ACTOR_TEXT_FOREGROUND));
            text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER );
            // vertical alignment has as default value "center"
            text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
            gaService.setLocationAndSize(text, 0, 0, width, 20);
 
            // create link and wire it
            link(shape, addedActor);
        }
        
        // Call the layout feature
        layoutPictogramElement(containerShape);
 
        return containerShape;
    }

}
