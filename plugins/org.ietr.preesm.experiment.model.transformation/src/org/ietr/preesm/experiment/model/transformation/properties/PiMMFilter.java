package org.ietr.preesm.experiment.model.transformation.properties;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

public class PiMMFilter extends AbstractPropertySectionFilter{

	/**
	 * Check the given pictogram element for acceptance.
	 * Returns true, if pictogram element is accepted, otherwise false.
	 */
	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);

		/**
		 * Parameter, SourceInterface, SinkInterface and Delay are EObject has associated properties window.
		 */

		//System.out.println(eObject.toString()+" Container: "+eObject.eContainer());
		
		// Parameter and ConfigInputInterface.
		if(eObject instanceof Parameter){
			if(((Parameter) eObject).isConfigurationInterface())	// is an ConfigInputInterface
				return false;
			return true;
		}
	
		// ConfigOutputPort contained in the Actor.
		if(eObject instanceof ConfigOutputPort)
			return false;
		
		// OutputPort contained in the SourceInterface and Actor
		if(eObject instanceof OutputPort){
			if(eObject.eContainer() instanceof SourceInterface)
				return true;
			if(eObject.eContainer() instanceof Actor)
				return true;
		}
		
		// InputPort contained in the SinkInterface and Actor
		if(eObject instanceof InputPort){
			if(eObject.eContainer() instanceof SinkInterface)
				return true;
			if(eObject.eContainer() instanceof Actor)
				return true;
		}
		
		if(eObject instanceof SinkInterface)
			return true;
		
		if(eObject instanceof SourceInterface)
			return true;
		
		if(eObject instanceof Delay)
			return true;

		
		return false;
	}

}
