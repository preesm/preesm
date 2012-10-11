package org.ietr.preesm.experiment.ui.pimemoc.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.ui.pimemoc.features.AddActorFeature;
import org.ietr.preesm.experiment.ui.pimemoc.features.CreateActorFeature;

public class PimemocFeatureProvider extends DefaultFeatureProvider {

	public PimemocFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request an Actor?
		if (context.getNewObject() instanceof Actor) {
			return new AddActorFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] { new CreateActorFeature(this) };

	}
}
