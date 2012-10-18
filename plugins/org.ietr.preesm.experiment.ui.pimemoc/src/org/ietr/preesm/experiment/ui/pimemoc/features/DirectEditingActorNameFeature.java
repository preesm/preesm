package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.util.VertexNameValidator;

/**
 * This class provide the feature that allows the direct edition of an
 * {@link Actor} name.
 * 
 * @author kdesnos
 * 
 */
public class DirectEditingActorNameFeature extends AbstractDirectEditingFeature {

	/**
	 * Default constructor of the {@link DirectEditingActorNameFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DirectEditingActorNameFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		// support direct editing, if it is a Actor, and the user clicked
		// directly on the text and not somewhere else in the rectangle
		if (bo instanceof Actor && ga instanceof Text) {
			return true;
		}
		// direct editing not supported in all other cases
		return false;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the EClass
		PictogramElement pe = context.getPictogramElement();
		Actor actor = (Actor) getBusinessObjectForPictogramElement(pe);
		return actor.getName();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
		PictogramElement pe = context.getPictogramElement();
		Actor renamedActor = (Actor) getBusinessObjectForPictogramElement(pe);
		VertexNameValidator validator = new VertexNameValidator(graph,
				renamedActor);
		
		return validator.isValid(value);
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		// set the new name for the Actor
		PictogramElement pe = context.getPictogramElement();
		Actor actor = (Actor) getBusinessObjectForPictogramElement(pe);
		actor.setName(value);

		// we know, that pe is the Shape of the Text, so its container is the
		// main shape of the Actor
		updatePictogramElement(((Shape) pe).getContainer());
		
		// Call the layout feature
        layoutPictogramElement(((Shape) pe).getContainer());
	}

}
