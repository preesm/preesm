/**
 * 
 */
package org.ietr.preesm.core.architecture.route;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.architecture.simplemodel.AbstractNode;
import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * Represents a single step in a route between two operators separated by
 * contention nodes and parallel nodes
 * 
 * @author mpelcat
 */
public class NodeRouteStep extends AbstractRouteStep {

	/**
	 * Communication nodes separating the sender and the receiver
	 */
	List<AbstractNode> nodes = null;
	
	public static final String id = "NodeRouteStep";
	
	public NodeRouteStep(Operator sender, List<AbstractNode> nodes, Operator receiver) {
		super(sender, receiver);
		nodes = new ArrayList<AbstractNode>(nodes);
	}

	@Override
	public String getId() {
		return id;
	}
}
