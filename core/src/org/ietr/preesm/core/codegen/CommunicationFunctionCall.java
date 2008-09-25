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
