package org.ietr.preesm.experiment.ui.pimm.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

/**
 * The {@link IDiagramTypeProvider} for the PiMM diagram type
 * 
 * @author kdesnos
 * 
 */
public class PiMMDiagramTypeProvider extends AbstractDiagramTypeProvider {
	/**
	 * The {@link IToolBehaviorProvider} of this type of {@link Diagram}
	 */
	private IToolBehaviorProvider[] toolBehaviorProviders;

	/**
	 * The default constructor of {@link PiMMDiagramTypeProvider}
	 */
	public PiMMDiagramTypeProvider() {
		super();
		setFeatureProvider(new PiMMFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {

		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { //
			new PiMMToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}
}
