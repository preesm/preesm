package org.ietr.preesm.experiment.ui.pimemoc.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;

/**
 * The {@link IDiagramTypeProvider} for the Pimemoc diagram type
 * 
 * @author kdesnos
 * 
 */
public class PimemocDiagramTypeProvider extends AbstractDiagramTypeProvider {

	/**
	 * The default constructor of {@link PimemocDiagramTypeProvider}
	 */
	public PimemocDiagramTypeProvider() {
		super();
		setFeatureProvider(new PimemocFeatureProvider(this));
	}
}
