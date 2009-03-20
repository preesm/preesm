/**
 * 
 */
package org.ietr.preesm.core.architecture.route;

import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * Represents a single step in a route between two operators separated by
 * contention nodes and parallel nodes
 * 
 * @author mpelcat
 */
public class NodeRouteStep extends AbstractRouteStep {

	public NodeRouteStep(Operator sender, Operator receiver) {
		super(sender, receiver);
		// TODO Auto-generated constructor stub
	}

}
