/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
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
