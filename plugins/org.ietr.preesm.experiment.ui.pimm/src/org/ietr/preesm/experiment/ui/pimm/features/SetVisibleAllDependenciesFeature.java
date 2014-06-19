package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public class SetVisibleAllDependenciesFeature extends
		AbstractSetVisibleDependencyFeature {	

	public SetVisibleAllDependenciesFeature(
			IFeatureProvider fp, boolean visible) {
		super(fp, visible);
	}

	@Override
	public String getName() {
		if (visible)
			return "Show all dependencies";
		else
			return "Hide all dependencies";
	}

	@Override
	public String getDescription() {
		if (visible)
			return "Show all dependencies in the graph.";
		else
			return "Hide all dependencies in the graph.";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// Allow setting if exactly one pictogram element
		// representing a Parameter is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof PiGraph) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		Object bo = getBusinessObjectForPictogramElement(pes[0]);
		PiGraph graph = (PiGraph) bo;
		for (Dependency d : graph.getDependencies()) {
			this.setVisible(d);
		}
	}

}
