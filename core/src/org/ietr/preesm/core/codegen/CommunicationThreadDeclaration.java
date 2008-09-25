package org.ietr.preesm.core.codegen;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.logging.Level;

import org.ietr.preesm.core.log.PreesmLogger;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Declaration of a communication thread for code generation. A communication
 * thread launches communication operations
 * 
 * @author mpelcat
 */
public class CommunicationThreadDeclaration extends ThreadDeclaration {

	public CommunicationThreadDeclaration(
			AbstractBufferContainer parentContainer) {
		super("communicationThread", parentContainer);
	}

	/**
	 * Adds semaphores to protect the data transmitted in this thread
	 */
	public void addSemaphores(SortedSet<DAGVertex> comVertices) {

		Iterator<DAGVertex> iterator = comVertices.iterator();

		while (iterator.hasNext()) {
			DAGVertex vertex = iterator.next();
			ICodeElement com = loopCode.getCodeElement(vertex);

			AbstractBufferContainer container = getGlobalContainer();
			
			// First test on the type of vertex that will be protected by a semaphore
			VertexType vType = (VertexType)vertex.getPropertyBean().getValue(VertexType.propertyBeanName);

			SemaphoreType sType = null;
			
			// If the communication operation is an intermediate step of a route, no semaphore is generated
			if(VertexType.isIntermediateReceive(vertex))
				continue;
			
			if(VertexType.isIntermediateSend(vertex))
				continue;
			
			// A first token must initialize the empty buffer semaphores before receive operations
			if(vType.isReceive()){

				SemaphorePost init = new SemaphorePost(container, vertex, SemaphoreType.empty);
				beginningCode.addCodeElement(init);
			}
				
			if(vType.isSend())
				sType = SemaphoreType.full;
			else if(vType.isReceive())
				sType = SemaphoreType.empty;
			
			// Creates the semaphore if necessary ; retrieves it otherwise
			// from global declaration and creates the pending function
			SemaphorePend pend = new SemaphorePend(container, vertex, sType);

			if(vType.isSend())
				sType = SemaphoreType.empty;
			else if(vType.isReceive())
				sType = SemaphoreType.full;
			
			// Creates the semaphore if necessary and creates the posting
			// function
			SemaphorePost post = new SemaphorePost(container, vertex, sType);

			if (pend != null && post != null) {
				// Adding a semaphore pend before the communication call and
				// a semaphore post after it
				loopCode.addCodeElementBefore(com, pend);
				loopCode.addCodeElementAfter(com, post);
			} else {
				PreesmLogger.getLogger().log(Level.SEVERE,
						"semaphore creation failed");
			}
		}
	}

	/**
	 * Adds send and receive functions from vertices allocated on the current
	 * core. Vertices are already in the correct order.
	 */
	public void addSendsAndReceives(SortedSet<DAGVertex> vertices) {

		Iterator<DAGVertex> iterator = vertices.iterator();

		while (iterator.hasNext()) {
			DAGVertex vertex = iterator.next();

			CommunicationFunctionCall com = CommunicationFunctionCall
					.createCall(this, vertex);
			if (com != null)
				loopCode.addCodeElement(com);
		}
	}

}
