package org.ietr.preesm.experiment.model.transformation.test;

import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeCreateService;
import org.ietr.preesm.experiment.model.pimm.Parameter;

public class PropertyUtil {

	 public static final String SHAPE_KEY = "shape-id";
	 
	    public static final String SHAPE_VALUE_PARAMETER = "e-parameter";
	 
	    public static final void setParameterShape(PictogramElement pe) {
	        Graphiti.getPeService().setPropertyValue(pe, SHAPE_KEY,
	            SHAPE_VALUE_PARAMETER);
	    }
	 
	    public static boolean isParameterShape(PictogramElement pe) {
	        return SHAPE_VALUE_PARAMETER.equals(Graphiti.getPeService()
	           .getPropertyValue(pe, SHAPE_KEY));
	    }
	    
	    public PictogramElement add(IAddContext context) {
	        //EClass addedClass = (EClass) context.getNewObject();
	    	Parameter addedClass = (Parameter) context.getNewObject();
	        Diagram targetDiagram = (Diagram) context.getTargetContainer();
	     
	        // CONTAINER SHAPE WITH ROUNDED RECTANGLE
	        IPeCreateService peCreateService = Graphiti.getPeCreateService();
	        ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);
	        PropertyUtil.setParameterShape(containerShape);
	        // ... EXISTING CODING ...
	           
	        return containerShape;
	    }
	    
	    public boolean canLayout(ILayoutContext context) {
	        // return true, if pictogram element is a EClass shape
	        PictogramElement pe = context.getPictogramElement();
	        return PropertyUtil.isParameterShape(pe);
	    }
}
