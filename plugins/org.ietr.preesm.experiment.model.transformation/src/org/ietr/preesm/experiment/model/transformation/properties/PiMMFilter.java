package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

public class PiMMFilter extends AbstractPropertySectionFilter{

	/**
	 * Retornamos true si queremos que tenga la ventana de propiedades asociada.
	 */
	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
/*
		if (eObject instanceof Graph) {
			return true;
		}
*/
		if(eObject instanceof Parameter){
			return true;
		}

		if(eObject instanceof OutputPort){
			return true;
		}
		
		if(eObject instanceof InputPort){
			return true;
		}

		if(eObject instanceof InterfaceActor){
			return true;
		}

		return false;
	}

}
