package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.ietr.preesm.experiment.model.pimm.Actor;

public class UpdateActorFeature extends UpdateAbstractVertexFeature {

	/**
	 * Default constructor of the {@link UpdateActorFeature}
	 * 
	 * @param fp
	 */
	public UpdateActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context
				.getPictogramElement());
		return (bo instanceof Actor);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {

		// Check if a name update is required
		IReason ret = super.updateNeeded(context);

		return ret;
	}

	@Override
	public boolean update(IUpdateContext context) {
		return super.updateName(context);
	}
}
