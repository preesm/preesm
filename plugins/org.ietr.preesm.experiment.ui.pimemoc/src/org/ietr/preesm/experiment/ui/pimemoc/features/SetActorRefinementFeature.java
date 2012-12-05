package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.ui.pimemoc.util.PimemocUtil;

/**
 * Custom Feature to set a new {@link Refinement} of an {@link Actor}
 * 
 * @author kdesnos
 * 
 */
public class SetActorRefinementFeature extends AbstractCustomFeature {

	protected boolean hasDoneChanges = false;

	/**
	 * Default Constructor of {@link SetActorRefinementFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public SetActorRefinementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Set Refinement";
	}

	@Override
	public String getDescription() {
		return "Set/Change the Refinement of an Actor";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// Allow setting if exactly one pictogram element
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
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				Actor actor = (Actor) bo;
				Refinement refinement = actor.getRefinement();

				// Ask user for Actor name until a valid name is entered.
				// TODO replace with a file/browsing solution
				String question = "Enter new file name";
				String newFileName = refinement.getFileName();

				newFileName = PimemocUtil.askString("Change file", question,
						newFileName, null);

				if (newFileName != null
						&& !newFileName.equals(refinement.getFileName())) {
					this.hasDoneChanges = true;
					refinement.setFileName(newFileName);
				}
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}
