package org.ietr.preesm.experiment.ui.pimm.diagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.examples.common.ui.DiagramTypeWizardPage;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.ui.pimm.decorators.ActorDecorators;
import org.ietr.preesm.experiment.ui.pimm.decorators.DelayDecorators;
import org.ietr.preesm.experiment.ui.pimm.decorators.ParameterDecorators;
import org.ietr.preesm.experiment.ui.pimm.decorators.PortDecorators;
import org.ietr.preesm.experiment.ui.pimm.features.OpenRefinementFeature;

/**
 * {@link IToolBehaviorProvider} for the {@link Diagram} with type
 * {@link PiMMDiagramTypeProvider}.
 * 
 * @author kdesnos
 * @author jheulot
 * 
 */
public class PiMMToolBehaviorProvider extends DefaultToolBehaviorProvider {

	/**
	 * Store the message to display when a ga is under the mouse.
	 */
	protected Map<GraphicsAlgorithm, String> toolTips;

	/**
	 * The default constructor of {@link PiMMToolBehaviorProvider}.
	 * 
	 * @param diagramTypeProvider
	 *            the {@link DiagramTypeWizardPage}
	 */
	public PiMMToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
		toolTips = new HashMap<GraphicsAlgorithm, String>();
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		IFeatureProvider featureProvider = getFeatureProvider();
		Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);

		if (bo instanceof Actor) {
			// Add decorators for each ports of the actor
			List<IDecorator> decorators = new ArrayList<IDecorator>();
			for(Anchor a : ((ContainerShape)pe).getAnchors()){
				for(Object pbo : a.getLink().getBusinessObjects()){
					if(pbo instanceof Port){
						for(IDecorator d : PortDecorators.getDecorators((Port) pbo, a)){
							decorators.add(d);				
						}
					}
				}
			}			
			
			// Add decorators to the actor itself
			for(IDecorator d : ActorDecorators.getDecorators((Actor) bo, pe)){
				decorators.add(d);				
			}
			
			IDecorator[] result = new IDecorator[decorators.size()];
			decorators.toArray(result);			
			return result;
		}

		if (bo instanceof Parameter
				&& !((Parameter) bo).isConfigurationInterface()) {
			return ParameterDecorators.getDecorators((Parameter) bo, pe);
		}

		if (bo instanceof Delay){
			return DelayDecorators.getDecorators((Delay) bo, pe);
		}
	
		return super.getDecorators(pe);
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature customFeature = new OpenRefinementFeature(
				getFeatureProvider());

		// canExecute() tests especially if the context contains a Actor with a
		// valid refinement
		if (customFeature.canExecute(context)) {
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
