package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;

public class PiMMFilter extends AbstractPropertySectionFilter{

	@Override
	protected boolean accept(PictogramElement pictogramElement) {
	EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
	if (eObject instanceof Graph) {
			return true;
		}
	if(eObject instanceof Parameter){
		return true;
	}
	if(eObject instanceof OutputPort){
		return true;
	}
/*
 	if(eObject instanceof SourceInterface){
		return true;
	}
*/	
	return false;
	}
	
}
