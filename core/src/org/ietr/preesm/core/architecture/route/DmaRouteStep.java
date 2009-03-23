/**
 * 
 */
package org.ietr.preesm.core.architecture.route;

import org.ietr.preesm.core.architecture.simplemodel.Dma;
import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * Route step where the sender uses a dma to send data in
 * parallel with its processing
 * 
 * @author mpelcat
 */
public class DmaRouteStep extends NodeRouteStep {

	private Dma dma;
	
	public DmaRouteStep(Operator sender, Operator receiver, Dma dma) {
		super(sender, receiver);
		this.dma = dma;
	}
	
	
}
