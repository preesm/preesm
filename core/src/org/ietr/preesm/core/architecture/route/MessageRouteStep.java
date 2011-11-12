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

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.component.ComNode;
import net.sf.dftools.architecture.slam.component.Component;
import net.sf.dftools.architecture.slam.component.impl.ComNodeImpl;

/**
 * Represents a single step in a route between two operators separated by
 * communication nodes
 * 
 * @author mpelcat
 */
public class MessageRouteStep extends AbstractRouteStep {

	/**
	 * Communication nodes separating the sender and the receiver
	 */
	protected List<ComponentInstance> nodes = null;

	/**
	 * The route step type determines how the communication will be simulated.
	 */
	public static final String type = "NodeRouteStep";

	public MessageRouteStep(ComponentInstance sender,
			List<ComponentInstance> inNodes, ComponentInstance receiver) {
		super(sender, receiver);
		nodes = new ArrayList<ComponentInstance>();

		for (ComponentInstance node : inNodes) {
			this.nodes.add(node);
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
		for (ComponentInstance node : nodes) {
			id += node.getComponent().getVlnv().getName();
		}
		return id;
	}

	public List<ComponentInstance> getContentionNodes() {
		List<ComponentInstance> contentionNodes = new ArrayList<ComponentInstance>();
		for (ComponentInstance node : nodes) {
			if (node.getComponent() instanceof ComNodeImpl) {
				if (!((ComNode) node.getComponent()).isParallel()) {
					contentionNodes.add(node);
				}
			}
		}
		return contentionNodes;
	}

	public List<ComponentInstance> getNodes() {
		return nodes;
	}

	/**
	 * Returns the longest time a node needs to transfer the data
	 */
	@Override
	public final long getWorstTransferTime(long transfersSize) {
		long time = 0;

		for (ComponentInstance node : nodes) {
			Component def = node.getComponent();
			if (def instanceof ComNodeImpl) {
				time = Math.max(time,
						(long) (transfersSize / ((ComNode) def).getSpeed()));
			}
		}

		// No zero transfer time is alloweds
		if (time <= 0) {
			time = 1;
		}

		return time;
	}

	/**
	 * Evaluates the cost of a data transfer with size transferSize. Can include
	 * overheads, additional cost...
	 */
	@Override
	public long getTransferCost(long transfersSize) {
		return getWorstTransferTime(transfersSize);
	}

	@Override
	public String toString() {
		String trace = "{" + getSender().toString() + " -> ";
		for (ComponentInstance node : nodes) {
			trace += node.getInstanceName() + " ";
		}
		trace += "-> " + getReceiver().toString() + "}";
		return trace;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new MessageRouteStep(getSender(), nodes, getReceiver());
	}

}
