/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.plugin.codegen.communication;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.model.VertexType;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePend;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePost;
import org.ietr.preesm.core.codegen.semaphore.SemaphoreType;
import org.ietr.preesm.core.codegen.threads.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.threads.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.ietr.preesm.workflow.tools.WorkflowLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generates code for a communication thread
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class CommThreadCodeGenerator {

	private CommunicationThreadDeclaration comThread;
	private ComputationThreadDeclaration compThread;

	public CommThreadCodeGenerator(ComputationThreadDeclaration compThread,
			CommunicationThreadDeclaration comThread) {
		this.comThread = comThread;
		this.compThread = compThread;
	}

	/**
	 * Adds semaphores to protect the data transmitted in this thread
	 */
	public void addSemaphoreFunctions(SortedSet<SDFAbstractVertex> comVertices,
			CodeSectionType codeContainerType) {

		AbstractCodeContainer codeContainer = null;

		if (codeContainerType.equals(CodeSectionType.beginning)) {
			codeContainer = comThread.getBeginningCode();
		} else if (codeContainerType.equals(CodeSectionType.loop)) {
			codeContainer = comThread.getLoopCode();
		} else if (codeContainerType.equals(CodeSectionType.end)) {
			codeContainer = comThread.getEndCode();
		}

		for (SDFAbstractVertex vertex : comVertices) {
			List<ICodeElement> coms = codeContainer.getCodeElements(vertex);

			if (!coms.isEmpty()) {
				AbstractBufferContainer container = comThread
						.getGlobalContainer();
				List<Buffer> buffers = null;

				// First test on the type of vertex that will be protected by a
				// semaphore
				VertexType vType = (VertexType) vertex
						.getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_vertexType);

				SemaphoreType sType = null;

				// If the communication operation is an intermediate step of a
				// route, no semaphore is generated
				if (VertexType.isIntermediateReceive(vertex)
						|| VertexType.isIntermediateSend(vertex)) {
					continue;
				}

				if (vType.isSend()) {
					sType = SemaphoreType.full;
					Set<SDFEdge> inEdges = (vertex.getBase()
							.incomingEdgesOf(vertex));
					buffers = container.getBuffers(inEdges);
				} else if (vType.isReceive()) {
					sType = SemaphoreType.empty;
					Set<SDFEdge> outEdges = (vertex.getBase()
							.outgoingEdgesOf(vertex));
					buffers = container.getBuffers(outEdges);
				}

				// A first token must initialize the empty buffer semaphores
				// before
				// receive operations if we deal with the communication loop
				if (vType.isReceive()
						&& codeContainerType.equals(CodeSectionType.loop)) {
					SemaphorePost init = new SemaphorePost(container, buffers,
							vertex, SemaphoreType.empty, codeContainerType);
					comThread.getBeginningCode().addCodeElement(init);
				}

				// Creates the semaphore if necessary ; retrieves it otherwise
				// from global declaration and creates the pending function
				SemaphorePend pend = new SemaphorePend(container, buffers,
						vertex, sType, codeContainerType);

				if (vType.isSend()) {
					sType = SemaphoreType.empty;
				} else if (vType.isReceive()) {
					sType = SemaphoreType.full;
				}

				// Creates the semaphore if necessary and creates the posting
				// function
				SemaphorePost post = new SemaphorePost(container, buffers,
						vertex, sType, codeContainerType);

				if (pend != null && post != null) {
					// Adding a semaphore pend before the communication calls
					// and
					// a semaphore post after them
					codeContainer.addCodeElementBefore(coms.get(0), pend);

					if (codeContainerType.equals(CodeSectionType.loop)) {
						codeContainer.addCodeElementAfter(
								coms.get(coms.size() - 1), post);
					}
				} else {
					WorkflowLogger.getLogger().log(Level.SEVERE,
							"semaphore creation failed");
				}
			}
		}
	}

	/**
	 * Adds send and receive functions from vertices allocated on the current
	 * core. Vertices are already in the correct order. The code thread com
	 * generator delegates com creation to each route step appropriate generator
	 */
	public void addSendsAndReceives(SortedSet<SDFAbstractVertex> vertices,
			AbstractBufferContainer bufferContainer) {

		// a code generator factory always outputs the same generator for a
		// given route step
		ComCodeGeneratorFactory factory = new ComCodeGeneratorFactory(
				compThread, comThread, vertices);
		for (SDFAbstractVertex vertex : vertices) {
			AbstractRouteStep step = (AbstractRouteStep) vertex
					.getPropertyBean().getValue(
							ImplementationPropertyNames.SendReceive_routeStep);

			// Delegates the com creation to the appropriate generator
			IComCodeGenerator generator = factory.getCodeGenerator(step);

			// Creates all functions and buffers related to the given vertex
			generator.createComs(vertex);
		}
	}
}
