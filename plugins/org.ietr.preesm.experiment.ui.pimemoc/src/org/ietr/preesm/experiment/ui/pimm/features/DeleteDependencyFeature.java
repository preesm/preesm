package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.ui.pimm.diagram.PiMMFeatureProviderWithRemove;

/**
 * Delete Feature for {@link Dependency}.
 * 
 * @author kdesnos
 * 
 */
public class DeleteDependencyFeature extends DefaultDeleteFeature {

	/**
	 * Default constructor for {@link DeleteDependencyFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DeleteDependencyFeature(IFeatureProvider fp) {
		super(new PiMMFeatureProviderWithRemove(fp.getDiagramTypeProvider()));
	}

	public void preDelete(IDeleteContext context) {
		super.preDelete(context);

		PictogramElement pe = context.getPictogramElement();
		Object obj = getBusinessObjectForPictogramElement(pe);
		ConfigInputPort iCfgPort = ((Dependency) obj).getGetter();
		Parameterizable portOwner = (Parameterizable) iCfgPort.eContainer();
		// If the getter is not an actor, delete the configInputPort
		if (!(portOwner instanceof Actor)) {
			portOwner.getConfigInputPorts().remove(iCfgPort);
		}
	}
}
