package org.ietr.preesm.experiment.ui.pimm.decorators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * Class providing methods to retrieve the {@link IDecorator} of a
 * {@link Port}
 * 
 * @author jheulot
 * 
 */
public class PortDecorators {

	/**
	 * Methods that returns all the {@link IDecorator} for a given {@link Port}
	 * .
	 * 
	 * @param port
	 *            the treated {@link Port}
	 * @param pe
	 *            the {@link PictogramElement} to decorate
	 * @return the {@link IDecorator} table.
	 */
	public static IDecorator[] getDecorators(Port port, PictogramElement pe) {

		List<IDecorator> decorators = new ArrayList<IDecorator>();
		
		// Check if the actor is a configuration actor
		IDecorator portDecorator = getPortExpressionDecorator(port, pe);
		if (portDecorator != null) {
			decorators.add(portDecorator);
		}

		IDecorator[] result = new IDecorator[decorators.size()];
		decorators.toArray(result);

		return result;
	}

	/**
	 * Get the {@link IDecorator} indicating if the
	 * {@link Port} have a valid expression.
	 * 
	 * @param port
	 *            the {@link Port} to test
	 * @param pe
	 *            the {@link PictogramElement} of the {@link Port}
	 * @return the {@link IDecorator} or <code>null</code>.
	 */
	protected static IDecorator getPortExpressionDecorator(Port port, PictogramElement pe) {
		ImageDecorator imageRenderingDecorator = new ImageDecorator(
				IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
		imageRenderingDecorator.setMessage("Problems in parameter resolution");
		
		BoxRelativeAnchor a = (BoxRelativeAnchor)pe;
		
		if(port instanceof InputPort){
			if(((InputPort)port).getExpression().evaluate().contains("Error")){
				imageRenderingDecorator.setX(-5);
				imageRenderingDecorator.setY((int)(a.getRelativeHeight()*a.getReferencedGraphicsAlgorithm().getHeight())-1);

				return imageRenderingDecorator;
			}
		}
		if(port instanceof OutputPort && !(port instanceof ConfigOutputPort)){			
			if(((OutputPort)port).getExpression().evaluate().contains("Error")){				
				imageRenderingDecorator.setX(a.getReferencedGraphicsAlgorithm().getWidth()-13);
				imageRenderingDecorator.setY((int)(a.getRelativeHeight()*a.getReferencedGraphicsAlgorithm().getHeight())-1);

				return imageRenderingDecorator;
			}
		}
		return null;
	}
}
