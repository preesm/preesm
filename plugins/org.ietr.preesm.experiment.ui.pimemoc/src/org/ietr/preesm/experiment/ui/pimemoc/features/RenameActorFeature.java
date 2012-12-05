package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;
import org.ietr.preesm.experiment.ui.pimemoc.util.PimemocUtil;

/**
 * Custom feature to rename an actor.
 * 
 * @author kdesnos
 * 
 */
public class RenameActorFeature extends AbstractCustomFeature {
	
	protected boolean hasDoneChanges = false;

	/**
	 * Default Constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public RenameActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Rename Actor";
	}

	@Override
	public String getDescription() {
		return "Change the name of the Actor";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow rename if exactly one pictogram element
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
		// Retrieve the graph
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Re-check if only one element is selected
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				Actor actor = (Actor) bo;
				String currentName = actor.getName();

				// Ask user for Actor name until a valid name is entered.
				String question = "Enter new actor name";
				String newActorName = actor.getName();

				newActorName = PimemocUtil.askString("Create Actor", question,
						newActorName, new VertexNameValidator(graph, actor));

				if (newActorName != null && !newActorName.equals(currentName)) {
					this.hasDoneChanges = true;
					actor.setName(newActorName);
			        
					// Update the Pictogram element
					updatePictogramElement(pes[0]);
					
					// Call the layout feature
					layoutPictogramElement(pes[0]);
				}
			}
		}
	}
	
	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}
