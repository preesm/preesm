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
	private List<AbstractNode> nodes = null;

	public static final String type = "NodeRouteStep";

	public NodeRouteStep(Operator sender, List<AbstractNode> inNodes,
			Operator receiver) {
		super(sender, receiver);
		nodes = new ArrayList<AbstractNode>();
		
		for (AbstractNode node : inNodes) {
			AbstractNode newNode = (AbstractNode)node.clone();
			newNode.setDefinition(node.getDefinition());
			 this.nodes.add(newNode);
		}
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getId() {
		String id = "";
		for (AbstractNode node : nodes) {
			id += node.getDefinition().getId();
		}
		return id;
	}
	
	protected List<AbstractNode> getNodes() {
		return nodes;
	}

}
