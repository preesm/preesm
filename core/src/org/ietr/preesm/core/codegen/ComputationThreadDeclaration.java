/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Declaration of a communication thread for code generation. 
 * A computation thread calls the functions corresponding
 * to the dag tasks.
 * 
 * @author mpelcat
 */
public class ComputationThreadDeclaration extends ThreadDeclaration {

	public ComputationThreadDeclaration(AbstractBufferContainer parentContainer) {
		super("computationThread",parentContainer);
	}

	/**
	 * Adds semaphores to protect the data transmitted in this thread. Iterates
	 * the task vertices in direct order and adds semaphore pending functions
	 */
	public void addSemaphorePends(SortedSet<DAGVertex> taskVertices) {

		SortedSet<DAGVertex> ownComVertices = null;
		AbstractBufferContainer container = getGlobalContainer();

		Iterator<DAGVertex> taskIterator = taskVertices.iterator();

		while (taskIterator.hasNext()) {
			DAGVertex task = taskIterator.next();

			// Getting incoming receive operations
			ownComVertices = getComVertices(task, true);
			
			if(!ownComVertices.isEmpty()){

				CodeElement taskElement = loopCode.getCodeElement(task);
				Iterator<DAGVertex> comIterator = ownComVertices.iterator();

				while (comIterator.hasNext()) {
					DAGVertex com = comIterator.next();

					// Creates the semaphore if necessary ; retrieves it otherwise 
					// from global declaration and creates the pending function
					SemaphorePend pend = new SemaphorePend(container,com, SemaphoreType.full);

					// Creates the semaphore if necessary and creates the posting
					// function
					SemaphorePost post = new SemaphorePost(container, com, SemaphoreType.empty);
					
					loopCode.addCodeElementBefore(taskElement, pend);
					loopCode.addCodeElementAfter(taskElement, post);
					
				}
			}

			// Getting outgoing send operations
			ownComVertices = getComVertices(task, false);
			
			if(!ownComVertices.isEmpty()){

				CodeElement taskElement = loopCode.getCodeElement(task);
				Iterator<DAGVertex> comIterator = ownComVertices.iterator();

				while (comIterator.hasNext()) {
					DAGVertex com = comIterator.next();

					// A first token must initialize the semaphore pend due to
					// a sending operation
					SemaphorePost init = new SemaphorePost(container, com, SemaphoreType.empty);

					beginningCode.addCodeElementBefore(taskElement, init);
					
					// Creates the semaphore if necessary ; retrieves it otherwise 
					// from global declaration and creates the pending function
					SemaphorePend pend = new SemaphorePend(container,com, SemaphoreType.empty);
					
					// Creates the semaphore if necessary and creates the posting
					// function
					SemaphorePost post = new SemaphorePost(container, com, SemaphoreType.full);
					
					loopCode.addCodeElementBefore(taskElement, pend);
					loopCode.addCodeElementAfter(taskElement, post);
					
				}
			}
		}
	}

	/**
	 * Adds one function call for each vertex in the ordered set
	 */
	public void addUserFunctionCalls(SortedSet<DAGVertex> vertices){
		
		Iterator<DAGVertex> iterator = vertices.iterator();
		
		while(iterator.hasNext()){
			DAGVertex vertex = iterator.next();
			

			UserFunctionCall beginningCall = new UserFunctionCall("init_" + vertex.getName(), vertex, this);
			beginningCode.addCodeElement(beginningCall);
			
			UserFunctionCall loopCall = new UserFunctionCall(vertex.getName(), vertex, this);
			loopCode.addCodeElement(loopCall);

			UserFunctionCall endCall = new UserFunctionCall("close_" + vertex.getName(), vertex, this);
			endCode.addCodeElement(endCall);
			
		}
	}

	/**
	 * Gets the communication vertices preceding or following the vertex vertex.
	 * If preceding = true, returns the communication vertices preceding the
	 * vertex, otherwise, returns the communication vertices following the
	 * vertex.
	 * The communication vertices are returned in scheduling order
	 */
	public SortedSet<DAGVertex> getComVertices(
			DAGVertex vertex, boolean preceding) {

		DAGVertex currentVertex = null;
		
		ConcurrentSkipListSet<DAGVertex> schedule = new ConcurrentSkipListSet<DAGVertex>(
				new SchedulingOrderComparator());

		Iterator<DAGEdge> iterator = null;

		if (preceding)
			iterator = vertex.getBase().incomingEdgesOf(vertex).iterator();
		else
			iterator = vertex.getBase().outgoingEdgesOf(vertex).iterator();

		while (iterator.hasNext()) {
			DAGEdge edge = iterator.next();

			if (preceding)
				currentVertex = edge.getSource();
			else
				currentVertex = edge.getTarget();
			
			// retrieving the type of the vertex
			VertexType vertexType = (VertexType) currentVertex.getPropertyBean()
					.getValue(VertexType.propertyBeanName);

			if (vertexType != null
					&& (vertexType.equals(VertexType.send) || vertexType
							.equals(VertexType.receive))) {
				schedule.add(currentVertex);
			}
		}

		return schedule;
	}
}
