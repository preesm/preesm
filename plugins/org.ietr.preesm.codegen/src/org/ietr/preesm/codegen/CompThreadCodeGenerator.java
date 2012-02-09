/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.codegen;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.parameters.Parameter;
import net.sf.dftools.algorithm.model.parameters.ParameterSet;
import net.sf.dftools.algorithm.model.psdf.PSDFInitVertex;
import net.sf.dftools.algorithm.model.psdf.PSDFSubInitVertex;
import net.sf.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.codegen.ICodeElement;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.calls.UserFunctionCall;
import org.ietr.preesm.core.codegen.calls.Variable;
import org.ietr.preesm.core.codegen.containers.AbstractCodeContainer;
import org.ietr.preesm.core.codegen.containers.ForLoop;
import org.ietr.preesm.core.codegen.containers.LinearCodeContainer;
import org.ietr.preesm.core.codegen.factories.CodeElementFactory;
import org.ietr.preesm.core.codegen.model.CodeGenSDFTokenInitVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePend;
import org.ietr.preesm.core.codegen.semaphore.SemaphorePost;
import org.ietr.preesm.core.codegen.semaphore.SemaphoreType;
import org.ietr.preesm.core.codegen.threads.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.ietr.preesm.core.codegen.types.DataType;
import org.jgrapht.alg.DirectedNeighborIndex;


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
	@SuppressWarnings("unchecked")
	public void addSemaphoreFunctions(
			SortedSet<SDFAbstractVertex> taskVertices,
			CodeSectionType codeContainerType) {
		AbstractBufferContainer container = thread.getGlobalContainer();
		AbstractCodeContainer codeContainer = null;

		if (codeContainerType.equals(CodeSectionType.beginning)) {
			codeContainer = thread.getBeginningCode();
		} else if (codeContainerType.equals(CodeSectionType.loop)) {
			codeContainer = thread.getLoopCode();
		} else if (codeContainerType.equals(CodeSectionType.end)) {
			codeContainer = thread.getEndCode();
		}

		for (SDFAbstractVertex task : taskVertices) {
			// Getting incoming receive operations
			SortedSet<SDFAbstractVertex> ownComVertices = thread
					.getComVertices(task, true);

			if (!ownComVertices.isEmpty()) {
				List<ICodeElement> taskElements = codeContainer
						.getCodeElements(task);
				if (taskElements.size() != 1) {
					WorkflowLogger.getLogger().log(
							Level.FINE,
							"Not one single function call for function: "
									+ task.getName() + " in section "
									+ codeContainerType.toString());
				} else {
					ICodeElement taskElement = taskElements.get(0);

					for (SDFAbstractVertex com : ownComVertices) {
						// Creates the semaphore if necessary ; retrieves it
						// otherwise from global declaration and creates the
						// pending
						// function
						Set<SDFEdge> outEdges = (com.getBase()
								.outgoingEdgesOf(com));
						List<Buffer> buffers = container.getBuffers(outEdges);

						DirectedNeighborIndex<SDFAbstractVertex, SDFEdge> neighborindex = new DirectedNeighborIndex<SDFAbstractVertex, SDFEdge>(
								task.getBase());

						SDFAbstractVertex send = (SDFAbstractVertex) (neighborindex
								.predecessorListOf(com).get(0));
						SDFAbstractVertex senderVertex = (SDFAbstractVertex) (neighborindex
								.predecessorListOf(send).get(0));

						if (SourceFileCodeGenerator
								.usesBuffersInCodeContainerType(senderVertex,
										codeContainerType, buffers, "output")) {
							SemaphorePend pend = new SemaphorePend(container,
									buffers, com, SemaphoreType.full,
									codeContainerType);
							codeContainer.addCodeElementBefore(taskElement,
									pend);

							if (codeContainerType.equals(CodeSectionType.loop)) {
								// Creates the semaphore if necessary and
								// creates
								// the
								// posting function
								SemaphorePost post = new SemaphorePost(
										container, buffers, com,
										SemaphoreType.empty, codeContainerType);
								codeContainer.addCodeElementAfter(taskElement,
										post);
							}
						}
					}
				}
			}

			// Getting outgoing send operations
			ownComVertices = thread.getComVertices(task, false);

			if (!ownComVertices.isEmpty()) {
				List<ICodeElement> loopElements = codeContainer
						.getCodeElements(task);
				if (loopElements.size() != 1) {
					WorkflowLogger.getLogger().log(
							Level.FINE,
							"Not one single function call for function: "
									+ task.getName() + " in section "
									+ codeContainerType.toString());
				} else {
					ICodeElement taskElement = loopElements.get(0); // error if
					// the array
					// size is
					// equal to
					// 0

					for (SDFAbstractVertex com : ownComVertices) {
						// A first token must initialize the semaphore pend due
						// to
						// a sending operation
						Set<SDFEdge> inEdges = (com.getBase()
								.incomingEdgesOf(com));
						List<Buffer> buffers = container.getBuffers(inEdges);

						if (SourceFileCodeGenerator
								.usesBuffersInCodeContainerType(task,
										codeContainerType, buffers, "output")) {
							// If the semaphore is generated for the loop, a
							// first
							// post is done in initialization and then a loop
							// pending and posting. In beginning and end cases,
							// only
							// a post is generated when the send is ready to be
							// called.
							if (codeContainerType.equals(CodeSectionType.loop)) {
								SemaphorePost initPost = new SemaphorePost(
										container, buffers, com,
										SemaphoreType.empty, codeContainerType);

								thread.getBeginningCode().addCodeElementBefore(
										taskElement, initPost);

								// Creates the semaphore if necessary ;
								// retrieves it
								// otherwise from global declaration and creates
								// the
								// pending function
								SemaphorePend pend = new SemaphorePend(
										container, buffers, com,
										SemaphoreType.empty, codeContainerType);
								codeContainer.addCodeElementBefore(taskElement,
										pend);
							}

							// Creates the semaphore if necessary and creates
							// the
							// posting function
							SemaphorePost post = new SemaphorePost(container,
									buffers, com, SemaphoreType.full,
									codeContainerType);

							codeContainer
									.addCodeElementAfter(taskElement, post);
						}
					}
				}
			}
		}
	}

	public void addDynamicParameter(ParameterSet params) {
		if (params != null) {
			for (Parameter param : params.values()) {
				if (param instanceof PSDFDynamicParameter) {
					thread.getLoopCode()
							.addVariable(
									new Variable(param.getName(), new DataType(
											"long")));
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

		// next section will need to be tested for multi-processor code
		// generation. As communication involve protecting buffers, allocation
		// explode, implode broadcast and roundBuffer as sub-buffers is not
		// possible at this time to ensure buffers reliability

		// Filtering special vertices (Fork , Broadcast...) from regular tasks
		//
		// List<SDFAbstractVertex> specialVertices = new
		// ArrayList<SDFAbstractVertex>(vertices);
		// List<SDFAbstractVertex> treatedVertices = new
		// ArrayList<SDFAbstractVertex>();
		// while(specialVertices.size() > 0){
		// SDFAbstractVertex origin = specialVertices.get(0);
		// if(origin instanceof CodeGenSDFForkVertex || origin instanceof
		// CodeGenSDFBroadcastVertex ){
		// boolean toBeTreated = true ;
		// for(SDFEdge inEdge : origin.getBase().incomingEdgesOf(origin)){
		// if(!treatedVertices.contains(inEdge.getSource()) &&
		// (inEdge.getSource() instanceof CodeGenSDFJoinVertex ||
		// inEdge.getSource() instanceof CodeGenSDFRoundBufferVertex)){
		// specialVertices.remove(inEdge.getSource());
		// specialVertices.add(0, inEdge.getSource());
		// toBeTreated = false ;
		// }
		// }
		// if(toBeTreated){
		// CodeElementFactory.treatSpecialBehaviorVertex(origin
		// .getName(), loopCode, origin);
		// treatedVertices.add(origin);
		// specialVertices.remove(origin);
		// }
		// }
		// else if(origin instanceof CodeGenSDFJoinVertex || origin instanceof
		// CodeGenSDFRoundBufferVertex){
		// boolean toBeTreated = true ;
		// for(SDFEdge outEdge : origin.getBase().outgoingEdgesOf(origin)){
		// if(!treatedVertices.contains(outEdge.getTarget()) &&
		// (outEdge.getTarget() instanceof CodeGenSDFJoinVertex ||
		// outEdge.getTarget() instanceof CodeGenSDFRoundBufferVertex)){
		// specialVertices.remove(outEdge.getTarget());
		// specialVertices.add(0, outEdge.getTarget());
		// toBeTreated = false ;
		// }
		// }
		// if(toBeTreated){
		// CodeElementFactory.treatSpecialBehaviorVertex(origin
		// .getName(), loopCode, origin);
		// treatedVertices.add(origin);
		// specialVertices.remove(origin);
		// }
		// }else{
		// specialVertices.remove(origin);
		// }
		// }
		//
		// for (SDFAbstractVertex vertex : vertices) {
		// if(vertex instanceof CodeGenSDFForkVertex || vertex instanceof
		// CodeGenSDFJoinVertex || vertex instanceof CodeGenSDFBroadcastVertex
		// || vertex instanceof CodeGenSDFRoundBufferVertex){
		// CodeElementFactory.treatSpecialBehaviorVertex(vertex
		// .getName(), loopCode, vertex);
		// }
		// }

		// Treating regular vertices
		for (SDFAbstractVertex vertex : vertices) {
			if (vertex instanceof ICodeGenSDFVertex
					&& vertex.getGraphDescription() == null) {
				FunctionCall vertexCall = (FunctionCall) vertex.getRefinement();
				if (vertex instanceof CodeGenSDFTokenInitVertex) {
					ICodeElement beginningCall = new UserFunctionCall(
							(CodeGenSDFTokenInitVertex) vertex, thread,
							CodeSectionType.beginning, false);
					// Adding init call if any
					beginningCode.addCodeElement(beginningCall);
					// PreesmLogger.getLogger().log(Level.INFO, "");
				} else if ((vertexCall != null && vertexCall.getInitCall() != null)) {
					ICodeElement beginningCall = new UserFunctionCall(vertex,
							thread, CodeSectionType.beginning, false);
					// Adding init call if any
					beginningCode.addCodeElement(beginningCall);
				}
				if (vertexCall != null && vertexCall.getEndCall() != null) {
					ICodeElement endCall = new UserFunctionCall(vertex, thread,
							CodeSectionType.end, false);
					// Adding end call if any
					endCode.addCodeElement(endCall);
				}

			}

			if (vertex instanceof ICodeGenSDFVertex) {
				ICodeElement loopCall = CodeElementFactory.createElement(
						vertex.getName(), loopCode, vertex);
				if (loopCall != null) {
					if (vertex instanceof PSDFInitVertex) {
						loopCode.addInitCodeElement(loopCall);
					} else if (vertex instanceof PSDFSubInitVertex) {
						loopCode.addCodeElementFirst(loopCall);
					} else {
						// Adding loop call if any
						WorkflowLogger.getLogger()
								.log(Level.FINE,
										"Adding code elt "
												+ loopCall.toString()
												+ " on operator "
												+ thread.getParentContainer()
														.getName());
						loopCode.addCodeElement(loopCall);
					}
				}
			}
		}
	}
}
