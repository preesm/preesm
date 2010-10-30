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
import org.ietr.preesm.core.architecture.simplemodel.Ram;

/**
 * Route step where the sender uses a shared RAM to send data
 * 
 * @author mpelcat
 */
public class RamRouteStep extends MessageRouteStep {

	private Ram ram;
	
	/**
	 * Index of the communication node connected to the shared ram
	 */
	private int ramNodeIndex = -1;

	/**
	 * The route step type determines how the communication will be simulated.
	 */
	public static final String type = "RamRouteStep";
	
	public RamRouteStep(Operator sender, List<AbstractNode> nodes, Operator receiver, Ram ram, int ramNodeIndex) {
		super(sender,nodes, receiver);		
		this.ram = ram;
		this.ramNodeIndex = ramNodeIndex;
	}

	/**
	 * The route step type determines how the communication will be simulated.
	 */
	@Override
	public String getType() {
		return type;
	}
	
	public Ram getRam() {
		return ram;
	}

	@Override
	public String toString() {
		String trace = super.toString();
		trace = trace.substring(0,trace.length()-1);
		trace += "[" + ram + "]}";
		return trace;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Ram newRam = (Ram)ram.clone();
		newRam.setDefinition(ram.getDefinition());
		return new RamRouteStep((Operator)getSender().clone(),getNodes(),(Operator)getReceiver().clone(),newRam, ramNodeIndex);
	}

	/**
	 * Returns the longest time a contention node needs to transfer the data before the RAM in the route steps
	 */
	public long getSenderSideWorstTransferTime(long transfersSize) {
		long time = 0;
		
		for(ContentionNode node: getSenderSideContentionNodes()){
			ContentionNodeDefinition def = (ContentionNodeDefinition)node.getDefinition();
			time = Math.max(time,def.getTransferTime(transfersSize));
		}
		return time;
	}

	/**
	 * Returns the longest time a contention node needs to transfer the data after the RAM in the route steps
	 */
	public long getReceiverSideWorstTransferTime(long transfersSize) {
		long time = 0;
		
		for(ContentionNode node: getReceiverSideContentionNodes()){
			ContentionNodeDefinition def = (ContentionNodeDefinition)node.getDefinition();
			time = Math.max(time,def.getTransferTime(transfersSize));
		}
		return time;
	}

	public List<ContentionNode> getSenderSideContentionNodes() {
		List<ContentionNode> contentionNodes = new ArrayList<ContentionNode>();
		for(int i=0; i<=ramNodeIndex;i++){
			AbstractNode node = nodes.get(i);
			if(node instanceof ContentionNode){
				contentionNodes.add((ContentionNode)node);
			}
		}
		return contentionNodes;
	}

	public List<ContentionNode> getReceiverSideContentionNodes() {
		List<ContentionNode> contentionNodes = new ArrayList<ContentionNode>();
		for(int i=ramNodeIndex; i<nodes.size();i++){
			AbstractNode node = nodes.get(i);
			if(node instanceof ContentionNode){
				contentionNodes.add((ContentionNode)node);
			}
		}
		return contentionNodes;
	}
}
