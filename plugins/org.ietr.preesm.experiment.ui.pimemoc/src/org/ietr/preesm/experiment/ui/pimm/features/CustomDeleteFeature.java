package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.ui.pimm.diagram.PiMMFeatureProviderWithRemove;

/**
 * Extension of the defaultDeleteFeature only to access the removeFeature which
 * is masked in the feature provider. The remove feature was masked because we
 * do not wat it to appear in the GUI. For more explanation, see {@link PiMMFeatureProviderWithRemove}.
 * 
 * @see http://www.eclipse.org/forums/index.php/mv/msg/234410/720417/#msg_720417
 * @see PiMMFeatureProviderWithRemove
 * @author kdesnos
 * 
 */
public class CustomDeleteFeature extends DefaultDeleteFeature {

	/**
	 * Default constructor
	 * 
	 * @param dtp
	 *            The Diagram Type Provider
	 */
	public CustomDeleteFeature(IFeatureProvider fp) {
		super(new PiMMFeatureProviderWithRemove(fp.getDiagramTypeProvider()));
	}

}