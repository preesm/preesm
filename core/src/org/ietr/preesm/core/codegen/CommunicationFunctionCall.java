/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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


/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.Iterator;
import java.util.Set;

import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * code Element used to launch inter-core communication send or receive
 * 
 * @author mpelcat
 */
public class CommunicationFunctionCall extends AbstractCodeElement {

	/**
	 * creates a send or a receive depending on the vertex type
	 */
	public static CommunicationFunctionCall createCall(
			AbstractBufferContainer parentContainer, DAGVertex vertex) {

		CommunicationFunctionCall call = null;

		// retrieving the vertex type
		VertexType type = (VertexType) vertex.getPropertyBean().getValue(
				VertexType.propertyBeanName);

		Medium medium = (Medium)vertex.getPropertyBean().getValue(Medium.propertyBeanName);
		
		if (type != null && medium != null) {
			if (type.isSend()){

				DAGEdge outEdge = (DAGEdge)(vertex.getBase().outgoingEdgesOf(vertex).toArray()[0]);
				Set<Buffer> bufferSet = parentContainer.getBuffers(outEdge);
				
				// The target is the operator on which the corresponding receive operation is mapped
				DAGVertex receive = outEdge.getTarget();
				Operator target = (Operator)receive.getPropertyBean().getValue(Operator.propertyBeanName);
				call = new Send(parentContainer, vertex, bufferSet, medium, target);
			}
			else if (type.isReceive()){
				DAGEdge inEdge = (DAGEdge)(vertex.getBase().incomingEdgesOf(vertex).toArray()[0]);
				Set<Buffer> bufferSet = parentContainer.getBuffers(inEdge);
				
				// The source is the operator on which the corresponding send operation is allocated
				DAGVertex send = inEdge.getSource();
				Operator source = (Operator)send.getPropertyBean().getValue(Operator.propertyBeanName);
				call = new Receive(parentContainer, vertex, bufferSet, medium, source);
			}
		}

		return call;
	}

	/**
	 * Transmitted buffers
	 */
	private Set<Buffer> bufferSet;

	/**
	 * Medium used
	 */
	private Medium medium;

	public CommunicationFunctionCall(String name,
			AbstractBufferContainer parentContainer, Set<Buffer> bufferSet, Medium medium, DAGVertex correspondingVertex) {

		super(name, parentContainer, correspondingVertex);

		this.bufferSet = bufferSet;
		
		this.medium = medium;
	}

	public void accept(AbstractPrinter printer) {

		printer.visit(this,0); // Visit self
		
		Iterator<Buffer> iterator = bufferSet.iterator();
		
		while(iterator.hasNext()){
			Buffer buf = iterator.next();
			
			buf.accept(printer); // Accept the code container
			printer.visit(this,1); // Visit self
		}
	}

	public Set<Buffer> getBufferSet() {
		return bufferSet;
	}

	public Medium getMedium() {
		return medium;
	}
	
	@Override
	public String toString() {

		String code = "";

		code += medium.getName() + ",";
		
		Iterator<Buffer> iterator = bufferSet.iterator();
		
		while(iterator.hasNext()){
			Buffer buf = iterator.next();
			
			code += buf.toString();
		}

		return code;
	}

}
