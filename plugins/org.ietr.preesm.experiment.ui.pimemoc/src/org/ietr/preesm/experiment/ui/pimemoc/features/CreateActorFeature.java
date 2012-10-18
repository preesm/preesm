package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory;
import org.ietr.preesm.experiment.model.pimemoc.util.VertexNameValidator;
import org.ietr.preesm.experiment.ui.pimemoc.util.PimemocUtil;

public class CreateActorFeature extends AbstractCreateFeature {

	private static final String FEATURE_NAME = "Actor";

	private static final String FEATURE_DESCRIPTION = "Create Actor";

	protected Boolean hasDoneChanges;

	/**
	 * Default constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public CreateActorFeature(IFeatureProvider fp) {
		// Set name and description of the creation feature
		super(fp, FEATURE_NAME, FEATURE_DESCRIPTION);
		hasDoneChanges = false;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Ask user for Actor name until a valid name is entered.
		String question = "Enter new actor name";
		String newActorName = "ActorName";

		newActorName = PimemocUtil.askString("Create Actor", question,
				newActorName, new VertexNameValidator(graph, null));
		if (newActorName == null || newActorName.trim().length() == 0) {
			this.hasDoneChanges = false; // If this is not done, the graph is considered modified.
			return EMPTY;
		}

		// create EClass
		Actor newActor = PIMeMoCFactory.eINSTANCE.createActor();
		newActor.setName(newActorName);

		// Add new actor to the graph.
		if(graph.getVertices().add(newActor))
		{
			this.hasDoneChanges = true;
		}

		// do the add to the Diagram
		addGraphicalRepresentation(context, newActor);

		// return newly created business object(s)
		return new Object[] { newActor };
	}
	
	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}
