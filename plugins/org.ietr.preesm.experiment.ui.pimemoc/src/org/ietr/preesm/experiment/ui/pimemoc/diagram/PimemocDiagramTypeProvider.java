package org.ietr.preesm.experiment.ui.pimemoc.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;

public class PimemocDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public PimemocDiagramTypeProvider() {
		super();
		setFeatureProvider(new PimemocFeatureProvider(this));
	}
}
