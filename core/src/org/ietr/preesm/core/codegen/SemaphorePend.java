/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Special function call pending a semaphore
 * 
 * @author mpelcat
 */
public class SemaphorePend extends CodeElement {

	private Semaphore semaphore;
	
	/**
	 * Creates a semaphore pend function to protect the data transmitted by a communication vertex.
	 * protectCom = true means that the pending function is being put in the communication thread.
	 * protectCom = false means that the pending function is being put in the computation thread
	 * to protect the sender and receiver function calls
	 */
	public SemaphorePend(AbstractBufferContainer globalContainer, DAGVertex vertex, SemaphoreType semType) {
		super("semaphorePend", globalContainer, vertex);

		SemaphoreContainer semContainer = globalContainer.getSemaphoreContainer();
		
		DAGEdge outEdge = (DAGEdge)(vertex.getBase().outgoingEdgesOf(vertex).toArray()[0]);
		BufferAggregate agg = (BufferAggregate)outEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
		
		// The pending semaphore of a full buffer will be put before the send
		semaphore = semContainer.createSemaphore(agg,semType);
	}

	public void accept(AbstractPrinter printer) {
		printer.visit(this,0); // Visit self
		semaphore.accept(printer); // Accept the code container
		printer.visit(this,1); // Visit self
	}
	
	public Semaphore getSemaphore() {
		return semaphore;
	}
	
	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		
		String code =  super.getName();
		
		code += "(" + semaphore.toString() + ");";
		
		return code;
	}
}
