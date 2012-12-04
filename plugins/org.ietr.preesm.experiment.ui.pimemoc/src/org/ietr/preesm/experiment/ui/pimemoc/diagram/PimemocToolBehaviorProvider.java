package org.ietr.preesm.experiment.ui.pimemoc.diagram;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.examples.common.ui.DiagramTypeWizardPage;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.ietr.preesm.experiment.ui.pimemoc.features.OpenRefinementFeature;

/**
 * {@link IToolBehaviorProvider} for the {@link Diagram} with type
 * {@link PimemocDiagramTypeProvider}.
 * 
 * @author kdesnos
 * 
 */
public class PimemocToolBehaviorProvider extends DefaultToolBehaviorProvider {

	/**
	 * Store the message to display when a ga is under the mouse.
	 */
	protected Map<GraphicsAlgorithm, String> toolTips;

	/**
	 * The default constructor of {@link PimemocToolBehaviorProvider}.
	 * 
	 * @param diagramTypeProvider
	 *            the {@link DiagramTypeWizardPage}
	 */
	public PimemocToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
		toolTips = new HashMap<GraphicsAlgorithm, String>();
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature customFeature = new OpenRefinementFeature(getFeatureProvider());
		
		// canExecute() tests especially if the context contains a Actor with a valid refinement
		if(customFeature.canExecute(context)){
			return customFeature;
		}
		
		return super.getDoubleClickFeature(context);
	}

	@Override
	public String getToolTip(GraphicsAlgorithm ga) {
		return toolTips.get(ga);
	}
	
	/**
	 * Set the tooltip message for a given {@link GraphicsAlgorithm}
	 * 
	 * @param ga
	 *            the {@link GraphicsAlgorithm}
	 * @param toolTip
	 *            the tooltip message to display
	 */
	public void setToolTip(GraphicsAlgorithm ga, String toolTip) {
		toolTips.put(ga, toolTip);
	}

}
