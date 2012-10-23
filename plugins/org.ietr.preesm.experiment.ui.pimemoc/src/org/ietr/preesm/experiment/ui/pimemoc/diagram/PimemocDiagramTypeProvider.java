package org.ietr.preesm.experiment.ui.pimemoc.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * The {@link IDiagramTypeProvider} for the Pimemoc diagram type
 * 
 * @author kdesnos
 * 
 */
public class PimemocDiagramTypeProvider extends AbstractDiagramTypeProvider {
	/**
	 * The {@link IToolBehaviorProvider} of this type of {@link Diagram}
	 */
	private IToolBehaviorProvider[] toolBehaviorProviders;

	/**
	 * The default constructor of {@link PimemocDiagramTypeProvider}
	 */
	public PimemocDiagramTypeProvider() {
		super();
		setFeatureProvider(new PimemocFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {

		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { //
			new PimemocToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}
}
