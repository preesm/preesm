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

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import org.ietr.preesm.core.codegen.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.Buffer;
import org.ietr.preesm.core.codegen.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.ForLoop;
import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.LinearCodeContainer;
import org.ietr.preesm.core.codegen.SemaphorePend;
import org.ietr.preesm.core.codegen.SemaphorePost;
import org.ietr.preesm.core.codegen.SemaphoreType;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generates code for a communication thread
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class CommThreadCodeGenerator {

	private CommunicationThreadDeclaration thread;

	public CommThreadCodeGenerator(CommunicationThreadDeclaration thread) {
		this.thread = thread;
	}

	/**
	 * Adds semaphores to protect the data transmitted in this thread
	 */
	public void addSemaphoreFunctions(SortedSet<SDFAbstractVertex> comVertices) {
		LinearCodeContainer beginningCode = thread.getBeginningCode();
		ForLoop loopCode = thread.getLoopCode();

		for (SDFAbstractVertex vertex : comVertices) {
			ICodeElement com = loopCode.getCodeElement(vertex);

			AbstractBufferContainer container = thread.getGlobalContainer();
			List<Buffer> buffers = null;

			// First test on the type of vertex that will be protected by a
			// semaphore
			VertexType vType = (VertexType) vertex.getPropertyBean().getValue(
					ImplementationPropertyNames.Vertex_vertexType);

			SemaphoreType sType = null;

			// If the communication operation is an intermediate step of a
			// route, no semaphore is generated
			if (VertexType.isIntermediateReceive(vertex)
					|| VertexType.isIntermediateSend(vertex)) {
				continue;
			}

			if (vType.isSend()) {
				sType = SemaphoreType.full;
				Set<SDFEdge> inEdges = (vertex.getBase().incomingEdgesOf(vertex));
				buffers = container.getBuffers(inEdges);
			} else if (vType.isReceive()) {
				sType = SemaphoreType.empty;
				Set<SDFEdge> outEdges = (vertex.getBase().outgoingEdgesOf(vertex));
				buffers = container.getBuffers(outEdges);
			}

			// A first token must initialize the empty buffer semaphores before
			// receive operations
			if (vType.isReceive()) {
				SemaphorePost init = new SemaphorePost(container,buffers, vertex,
						SemaphoreType.empty);
				beginningCode.addCodeElement(init);
			}

			// Creates the semaphore if necessary ; retrieves it otherwise
			// from global declaration and creates the pending function
			SemaphorePend pend = new SemaphorePend(container, buffers, vertex, sType);

			if (vType.isSend()) {
				sType = SemaphoreType.empty;
			} else if (vType.isReceive()) {
				sType = SemaphoreType.full;
			}

			// Creates the semaphore if necessary and creates the posting
			// function
			SemaphorePost post = new SemaphorePost(container, buffers, vertex, sType);

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
	public void addSendsAndReceives(SortedSet<SDFAbstractVertex> vertices) {
		for (SDFAbstractVertex vertex : vertices) {
			CommunicationFunctionCall com = CommunicationFunctionCall
					.createCall(thread, vertex);
			if (com != null) {
				thread.getLoopCode().addCodeElement(com);
			}
			else{
				PreesmLogger.getLogger().log(Level.SEVERE,"problem creating a send or receive function call: " + vertex.getName());
			}
		}
	}
}
