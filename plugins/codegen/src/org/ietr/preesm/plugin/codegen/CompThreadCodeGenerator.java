package org.ietr.preesm.plugin.codegen;

import java.util.Iterator;
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
import org.sdf4j.model.dag.DAGVertex;

public class CompThreadCodeGenerator {

	private ComputationThreadDeclaration thread;

	public CompThreadCodeGenerator(ComputationThreadDeclaration thread) {
		this.thread = thread;
	}

	/**
	 * Adds one function call for each vertex in the ordered set
	 */
	public void addUserFunctionCalls(SortedSet<DAGVertex> vertices) {
		LinearCodeContainer beginningCode = thread.getBeginningCode();
		ForLoop loopCode = thread.getLoopCode();
		LinearCodeContainer endCode = thread.getEndCode();
		
		Iterator<DAGVertex> iterator = vertices.iterator();
		while (iterator.hasNext()) {
			DAGVertex vertex = iterator.next();

			ICodeElement beginningCall = new UserFunctionCall("init_"
					+ vertex.getName(), vertex, thread);
			beginningCode.addCodeElement(beginningCall);

			ICodeElement loopCall = CodeElementFactory.createElement(vertex
					.getName(), thread, vertex);
			loopCode.addCodeElement(loopCall);

			ICodeElement endCall = new UserFunctionCall("close_"
					+ vertex.getName(), vertex, thread);
			endCode.addCodeElement(endCall);

		}
	}

	/**
	 * Adds semaphores to protect the data transmitted in this thread. Iterates
	 * the task vertices in direct order and adds semaphore pending functions
	 */
	public void addSemaphorePends(SortedSet<DAGVertex> taskVertices) {
		SortedSet<DAGVertex> ownComVertices = null;
		AbstractBufferContainer container = thread.getGlobalContainer();

		Iterator<DAGVertex> taskIterator = taskVertices.iterator();
		
		LinearCodeContainer beginningCode = thread.getBeginningCode();
		ForLoop loopCode = thread.getLoopCode();

		while (taskIterator.hasNext()) {
			DAGVertex task = taskIterator.next();

			// Getting incoming receive operations
			ownComVertices = thread.getComVertices(task, true);

			if (!ownComVertices.isEmpty()) {
				ICodeElement taskElement = loopCode.getCodeElement(task);
				Iterator<DAGVertex> comIterator = ownComVertices.iterator();

				while (comIterator.hasNext()) {
					DAGVertex com = comIterator.next();

					// Creates the semaphore if necessary ; retrieves it
					// otherwise
					// from global declaration and creates the pending function
					SemaphorePend pend = new SemaphorePend(container, com,
							SemaphoreType.full);

					// Creates the semaphore if necessary and creates the
					// posting
					// function
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
				Iterator<DAGVertex> comIterator = ownComVertices.iterator();

				while (comIterator.hasNext()) {
					DAGVertex com = comIterator.next();

					// A first token must initialize the semaphore pend due to
					// a sending operation
					SemaphorePost init = new SemaphorePost(container, com,
							SemaphoreType.empty);

					beginningCode.addCodeElementBefore(taskElement, init);

					// Creates the semaphore if necessary ; retrieves it
					// otherwise
					// from global declaration and creates the pending function
					SemaphorePend pend = new SemaphorePend(container, com,
							SemaphoreType.empty);

					// Creates the semaphore if necessary and creates the
					// posting
					// function
					SemaphorePost post = new SemaphorePost(container, com,
							SemaphoreType.full);

					loopCode.addCodeElementBefore(taskElement, pend);
					loopCode.addCodeElementAfter(taskElement, post);
				}
			}
		}
	}
}
