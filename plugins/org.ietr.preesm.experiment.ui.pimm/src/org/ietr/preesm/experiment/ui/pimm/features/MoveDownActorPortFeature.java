package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * Custom feature to move down a port.
 * 
 * @author jheulot
 * 
 */
public class MoveDownActorPortFeature extends AbstractCustomFeature {

	protected boolean hasDoneChanges = false;

	/**
	 * Default Constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public MoveDownActorPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Move down Port";
	}

	@Override
	public String getDescription() {
		return "Move down the Port";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow move up if exactly one pictogram element
		// representing a Port is selected 
		// and it is not the first port
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Port) {
				Port port = (Port) bo;
				if (port.eContainer() instanceof Actor) {
					Actor actor = (Actor)(port.eContainer());
					String kind = port.getKind();
					if(kind.compareTo("input") == 0){
						ret = actor.getInputPorts().size() > 1;
						ret = ret && actor.getInputPorts().indexOf(port) < actor.getInputPorts().size()-1;
					}else if(kind.compareTo("output") == 0){
						ret = actor.getOutputPorts().size() > 1;
						ret = ret && actor.getOutputPorts().indexOf(port) < actor.getOutputPorts().size()-1;
					}else if(kind.compareTo("cfg_input") == 0){
						ret = actor.getConfigInputPorts().size() > 1;
						ret = ret && actor.getConfigInputPorts().indexOf(port) < actor.getConfigInputPorts().size()-1;
					}else if(kind.compareTo("cfg_output") == 0){
						ret = actor.getConfigOutputPorts().size() > 1;
						ret = ret && actor.getConfigOutputPorts().indexOf(port) < actor.getConfigOutputPorts().size()-1;
					}
				}
			}
		}
		return ret;
	}
	
	protected boolean avoidNegativeCoordinates() {
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		// Re-check if only one element is selected
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Port) {
				Port portToMoveUp, portToMoveDown;
				int portToMoveUpIndex, portToMoveDownIndex;
				Actor actor;
				
				portToMoveDown = (Port) bo;
				actor = (Actor)(portToMoveDown.eContainer());
				String portKind = portToMoveDown.getKind();

				// Switch Port into Actor Object
				if(portKind.compareTo("input") == 0){
					portToMoveDownIndex = actor.getInputPorts().indexOf(portToMoveDown);
					portToMoveUp = actor.getInputPorts().get(portToMoveDownIndex+1);
				}else if(portKind.compareTo("output") == 0){
					portToMoveDownIndex = actor.getOutputPorts().indexOf(portToMoveDown);
					portToMoveUp = actor.getOutputPorts().get(portToMoveDownIndex+1);
				}else if(portKind.compareTo("cfg_input") == 0){
					portToMoveDownIndex = actor.getConfigInputPorts().indexOf(portToMoveDown);
					portToMoveUp = actor.getConfigInputPorts().get(portToMoveDownIndex+1);
				}else if(portKind.compareTo("cfg_output") == 0){
					portToMoveDownIndex = actor.getConfigOutputPorts().indexOf(portToMoveDown);
					portToMoveUp = actor.getConfigOutputPorts().get(portToMoveDownIndex+1);
				}else{
					return;
				}
				portToMoveUpIndex = portToMoveDownIndex+1;
				
				// Switch Graphical Elements
				int anchorToMoveUpIndex=-1, anchorToMoveDownIndex;
				ContainerShape CsActor = (ContainerShape) ((BoxRelativeAnchor) pes[0]).getReferencedGraphicsAlgorithm().getPictogramElement();
				EList<Anchor> anchors = CsActor.getAnchors();
				
				anchorToMoveDownIndex = anchors.indexOf(pes[0]);
				
				for(Anchor a : anchors){
					if(a.getLink().getBusinessObjects().get(0).equals(portToMoveUp)){
						anchorToMoveUpIndex = anchors.indexOf(a);					
						break;
					}
				}
				
				if(anchorToMoveUpIndex == -1)
					return;

				// Do Modifications
				this.hasDoneChanges = true;
				
				if(portKind.compareTo("input") == 0){
					actor.getInputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
				}else if(portKind.compareTo("output") == 0){
					actor.getOutputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
				}else if(portKind.compareTo("cfg_input") == 0){
					actor.getConfigInputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
				}else if(portKind.compareTo("cfg_output") == 0){
					actor.getConfigOutputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
				}
				
				anchors.move(anchorToMoveDownIndex, anchorToMoveUpIndex);
				
				
				// Layout the Port
				layoutPictogramElement(pes[0]);
				updatePictogramElement(pes[0]);

				// Layout the actor				
				layoutPictogramElement(CsActor);
				updatePictogramElement(CsActor);
				
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}
