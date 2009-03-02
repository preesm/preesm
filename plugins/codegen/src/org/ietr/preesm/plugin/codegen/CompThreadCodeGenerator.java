/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.plugin.codegen;

import java.util.SortedSet;

import org.ietr.preesm.core.codegen.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.CodeElementFactory;
import org.ietr.preesm.core.codegen.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.ForLoop;
import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.LinearCodeContainer;
import org.ietr.preesm.core.codegen.SemaphorePend;
import org.ietr.preesm.core.codegen.SemaphorePost;
import org.ietr.preesm.core.codegen.SemaphoreType;
import org.ietr.preesm.core.codegen.UserFunctionCall;
import org.ietr.preesm.core.codegen.UserFunctionCall.CodeSection;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Generates code for a computation thread
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class CompThreadCodeGenerator {

	private ComputationThreadDeclaration thread;

	public CompThreadCodeGenerator(ComputationThreadDeclaration thread) {
		this.thread = thread;
	}

	/**
	 * Adds semaphores to protect the data transmitted in this thread. Iterates
	 * the task vertices in direct order and adds semaphore pending functions
	 */
	public void addSemaphoreFunctions(SortedSet<SDFAbstractVertex> taskVertices) {
		AbstractBufferContainer container = thread.getGlobalContainer();
		LinearCodeContainer beginningCode = thread.getBeginningCode();
		ForLoop loopCode = thread.getLoopCode();
		
		for (SDFAbstractVertex task : taskVertices) {
			// Getting incoming receive operations
			SortedSet<SDFAbstractVertex> ownComVertices = thread.getComVertices(task,
					true);

			if (!ownComVertices.isEmpty()) {
				ICodeElement taskElement = loopCode.getCodeElement(task);
				for (SDFAbstractVertex com : ownComVertices) {
					// Creates the semaphore if necessary ; retrieves it
					// otherwise from global declaration and creates the pending
					// function
					SemaphorePend pend = new SemaphorePend(container, com,
							SemaphoreType.full);

					// Creates the semaphore if necessary and creates the
					// posting function
					SemaphorePost post = new SemaphorePost(container, com,
							SemaphoreType.empty);

					loopCode.addCodeElementBefore(taskElement, pend);
					loopCode.addCodeElementAfter(taskElement, post);
				}
			}

			// Getting outgoing send operations
			ownComVertices = thread.getComVertices(task, false);

			if (!ownComVertices.isEmpty()) {
				ICodeElement taskElement = loopCode.getCodeElement(task);
				for (SDFAbstractVertex com : ownComVertices) {
					// A first token must initialize the semaphore pend due to
					// a sending operation
					SemaphorePost init = new SemaphorePost(container, com,
							SemaphoreType.empty);

					beginningCode.addCodeElementBefore(taskElement, init);

					// Creates the semaphore if necessary ; retrieves it
					// otherwise from global declaration and creates the pending
					// function
					SemaphorePend pend = new SemaphorePend(container, com,
							SemaphoreType.empty);

					// Creates the semaphore if necessary and creates the
					// posting function
					SemaphorePost post = new SemaphorePost(container, com,
							SemaphoreType.full);

					loopCode.addCodeElementBefore(taskElement, pend);
					loopCode.addCodeElementAfter(taskElement, post);
				}
			}
		}
	}

	/**
	 * Adds one function call for each vertex in the ordered set
	 */
	public void addUserFunctionCalls(SortedSet<SDFAbstractVertex> vertices) {
		LinearCodeContainer beginningCode = thread.getBeginningCode();
		ForLoop loopCode = thread.getLoopCode();
		LinearCodeContainer endCode = thread.getEndCode();

		for (SDFAbstractVertex vertex : vertices) {
			if(vertex instanceof CodeGenSDFVertex && vertex.getGraphDescription() == null){
					FunctionCall vertexCall = (FunctionCall) vertex.getRefinement();
					if(vertexCall != null && vertexCall.getInitCall() != null){
						ICodeElement beginningCall = new UserFunctionCall(vertex, thread, CodeSection.INIT);
						beginningCode.addCodeElement(beginningCall);
					}
					if(vertexCall != null && vertexCall.getEndCall() != null){
						ICodeElement endCall = new UserFunctionCall(vertex, thread, CodeSection.END);
						endCode.addCodeElement(endCall);
					}
			}
			ICodeElement loopCall = CodeElementFactory.createElement(vertex
					.getName(), thread, vertex);
			loopCode.addCodeElement(loopCall);
			

		}
	}
}
