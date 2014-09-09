package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Parameter;

public class SetVisibleDependenciesFromParameterFeature extends
		AbstractSetVisibleDependencyFeature {

	public SetVisibleDependenciesFromParameterFeature(
			IFeatureProvider fp, boolean visible) {
		super(fp, visible);
	}

	@Override
	public String getName() {
		if (visible)
			return "Show incoming/outgoing dependencies";
		else
			return "Hide incoming/outgoing dependencies";
	}

	@Override
	public String getDescription() {
		if (visible)
			return "Show dependencies which source or target is this parameter.";
		else
			return "Hide dependencies which source or target is this parameter.";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// Allow setting if exactly one pictogram element
		// representing a Parameter is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Parameter) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		Object bo = getBusinessObjectForPictogramElement(pes[0]);
		Parameter param = (Parameter) bo;
		this.setVisibleOutgoingDependencies(param);
		this.setVisibleIngoingDependencies(param);
	}

}
