package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;

abstract public class AbstractSetVisibleDependencyFeature extends AbstractCustomFeature {

	protected IFeatureProvider fp;
	protected boolean visible;
	
	public AbstractSetVisibleDependencyFeature(IFeatureProvider fp, boolean visible) {
		super(fp);
		this.fp = fp;
		this.visible = visible;
	}
	
	protected void setVisible(Dependency d) {
		PictogramElement[] depPes = fp
				.getAllPictogramElementsForBusinessObject(d);
		if (depPes != null) {
			for (PictogramElement pe : depPes) {
				if (pe.isVisible() != visible)
					pe.setVisible(visible);
			}
		}
	}
	
	protected void setVisibleOutgoingDependencies(ISetter setter) {
		for (Dependency d : setter.getOutgoingDependencies()) {
			this.setVisible(d);
		}
	}
	
	protected void setVisibleIngoingDependencies(Parameterizable param) {
		for (ConfigInputPort p : param.getConfigInputPorts()) {
			this.setVisible(p.getIncomingDependency());
		}
	}
}
