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

				ICodeElement taskElement = loopCode.getCodeElement(task);
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

				ICodeElement taskElement = loopCode.getCodeElement(task);
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
