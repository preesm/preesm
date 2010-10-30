/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.architecture.route;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.architecture.simplemodel.AbstractNode;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNode;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNode;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNodeDefinition;

/**
 * Represents a single step in a route between two operators separated by
 * contention nodes and parallel nodes
 * 
 * @author mpelcat
 */
public class MessageRouteStep extends AbstractRouteStep {

	/**
	 * Communication nodes separating the sender and the receiver
	 */
	protected List<AbstractNode> nodes = null;

	/**
	 * The route step type determines how the communication will be simulated.
	 */
	public static final String type = "NodeRouteStep";

	public MessageRouteStep(Operator sender, List<AbstractNode> inNodes,
			Operator receiver) {
		super(sender, receiver);
		nodes = new ArrayList<AbstractNode>();

		for (AbstractNode node : inNodes) {
			AbstractNode newNode = (AbstractNode) node.clone();
			newNode.setDefinition(node.getDefinition());
			this.nodes.add(newNode);
		}
	}

	/**
	 * The route step type determines how the communication will be simulated.
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * The id is given to code generation. It selects the communication
	 * functions to use
	 */
	@Override
	public String getId() {
		String id = "";
		for (AbstractNode node : nodes) {
			id += node.getDefinition().getVlnv().getName();
		}
		return id;
	}

	public List<ContentionNode> getContentionNodes() {
		List<ContentionNode> contentionNodes = new ArrayList<ContentionNode>();
		for(AbstractNode node : nodes){
			if(node instanceof ContentionNode){
				contentionNodes.add((ContentionNode)node);
			}
		}
		return contentionNodes;
	}
	
	public List<AbstractNode> getNodes() {
		return nodes;
	}

	/**
	 * Returns the longest time a node needs to transfer the data
	 */
	@Override
	public final long getWorstTransferTime(long transfersSize) {
		long time = 0;
		
		for(AbstractNode node: nodes){
			if(node instanceof ContentionNode){
			ContentionNodeDefinition def = (ContentionNodeDefinition)node.getDefinition();
			time = Math.max(time,def.getTransferTime(transfersSize));
			}
			else if (node instanceof ParallelNode){
				ParallelNodeDefinition def = (ParallelNodeDefinition)node.getDefinition();
				time = Math.max(time,def.getTransferTime(transfersSize));
			}
		}
		return time;
	}

	/**
	 * Evaluates the cost of a data transfer with size transferSize.
	 * Can include overheads, additional cost...
	 */
	@Override
	public long getTransferCost(long transfersSize) {
		return getWorstTransferTime(transfersSize);
	}

	@Override
	public String toString() {
		String trace = "{" + getSender().toString() + " -> ";
		for (AbstractNode node : nodes) {
			trace += node + " ";
		}
		trace += "-> " + getReceiver().toString() + "}";
		return trace;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new MessageRouteStep((Operator)getSender().clone(),nodes,(Operator)getReceiver().clone());
	}

}
