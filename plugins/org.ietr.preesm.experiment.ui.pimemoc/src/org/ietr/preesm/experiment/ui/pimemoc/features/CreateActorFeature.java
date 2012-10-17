package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory;

public class CreateActorFeature extends AbstractCreateFeature {

	private static final String FEATURE_NAME = "Actor";

	private static final String FEATURE_DESCRIPTION = "Create Actor";

	public CreateActorFeature(IFeatureProvider fp) {
		// Set name and description of the creation feature
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// ask user for EClass name
		String newClassName = ExampleUtil.askString("Create Actor",
				"Enter new actor name", "ActorName");
		if (newClassName == null || newClassName.trim().length() == 0) {
			return EMPTY;
		}

		// create EClass
		Actor newActor = PIMeMoCFactory.eINSTANCE.createActor();
		newActor.setName(newClassName);

		// Add new actor to the graph.
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
		graph.getVertices().add(newActor);

		// do the add to the Diagram
		addGraphicalRepresentation(context, newActor);

		// return newly created business object(s)
		return new Object[] { newActor };
	}

}
