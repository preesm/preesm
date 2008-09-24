/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Special function call posting a semaphore
 * 
 * @author mpelcat
 */
public class SemaphorePost extends CodeElement {

	private Semaphore semaphore;
	
	/**
	 * Creates a semaphore post function to protect the data transmitted by a communication vertex.
	 * protectCom = true means that the pending function is being put in the communication thread.
	 * protectCom = false means that the pending function is being put in the computation thread
	 * to protect the sender and receiver function calls
	 */
	public SemaphorePost(AbstractBufferContainer globalContainer, DAGVertex vertex, SemaphoreType semType) {
		super("semaphorePost", globalContainer, vertex);

		SemaphoreContainer semContainer = globalContainer.getSemaphoreContainer();
		
		SDFEdge outEdge = (SDFEdge)(vertex.getBase().outgoingEdgesOf(vertex).toArray()[0]);
		BufferAggregate agg = (BufferAggregate)outEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
		
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
