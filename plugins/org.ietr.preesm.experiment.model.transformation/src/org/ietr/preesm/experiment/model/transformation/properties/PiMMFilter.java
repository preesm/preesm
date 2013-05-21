package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;

public class PiMMFilter extends AbstractPropertySectionFilter{

	/**
	 * Check the given pictogram element for acceptance.
	 * Returns true, if pictogram element is accepted, otherwise false.
	 */
	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);

		/**
		 * Parameter, SourceInterface, SinkInterface are EObject has associated properties window.
		 */
		
		if(eObject instanceof ConfigInputInterface){
			//Check ConfigInputInterface before checking the Parameter
			return false;
		}
		
		if(eObject instanceof Parameter){
			return true;
		}
		
		if(eObject instanceof OutputPort){
			return true;
		}
		
		if(eObject instanceof InputPort){
			//If the "InputPort" is contained in "ConfigOutputInterface" (triangle image)
			//must not have visible properties window.
			if(eObject.eContainer() instanceof ConfigOutputInterface){
				return false;
			}
			return true; //If the "InputPort" is contained in "SinkInterface"
		}
		
		if(eObject instanceof InterfaceActor){
			if(eObject instanceof ConfigOutputInterface){
				return false;
			}	
			return true;
		}

		return false;
	}

}
