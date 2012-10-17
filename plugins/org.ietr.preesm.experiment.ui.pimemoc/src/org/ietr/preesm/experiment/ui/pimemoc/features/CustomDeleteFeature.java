package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.ui.pimemoc.diagram.PimemocFeatureProviderWithRemove;

/**
 * Extension of the defaultDeleteFeature only to access the removeFeature which
 * is masked in the feature provider. The remove feature was masked because we
 * do not wat it to appear in the GUI. For more explanation, see {@link PimemocFeatureProviderWithRemove}.
 * 
 * @see http://www.eclipse.org/forums/index.php/mv/msg/234410/720417/#msg_720417
 * @see PimemocFeatureProviderWithRemove
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
		super(new PimemocFeatureProviderWithRemove(fp.getDiagramTypeProvider()));
	}

}