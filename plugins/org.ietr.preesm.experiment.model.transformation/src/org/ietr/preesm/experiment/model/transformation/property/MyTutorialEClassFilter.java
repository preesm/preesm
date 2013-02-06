package org.ietr.preesm.experiment.model.transformation.property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.ietr.preesm.experiment.model.pimm.Graph;

public class MyTutorialEClassFilter extends AbstractPropertySectionFilter{

	public MyTutorialEClassFilter(){
		System.out.println("Esto se llama");
	}
	
	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
		System.out.println("Clase Filter: "+ eObject.toString());
		if (eObject instanceof Graph) {
			return true;
		}
		return false;

		//Diagram diagram = pictogramElement.eContainingFeature().get.getDiagramTypeProvider().getDiagram();
		/*	Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
		EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement); 			 
		        if (eObject instanceof Diagram) {
		            return true;
		        }
		        return false;
		 */        
	}
}
