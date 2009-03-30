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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import org.ietr.preesm.core.codegen.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.Buffer;
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
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

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
				List<ICodeElement> taskElements = loopCode.getCodeElements(task);
				if(taskElements.size() != 1){
					PreesmLogger.getLogger().log(Level.SEVERE,"Not one single function call for function: " + task.getName());
				}
				ICodeElement taskElement = taskElements.get(0);
				
				for (SDFAbstractVertex com : ownComVertices) {
					// Creates the semaphore if necessary ; retrieves it
					// otherwise from global declaration and creates the pending
					// function
					Set<SDFEdge> outEdges = (com.getBase().outgoingEdgesOf(com));
					List<Buffer> buffers = container.getBuffers(outEdges);
					
					SemaphorePend pend = new SemaphorePend(container, buffers, com,
							SemaphoreType.full);

					// Creates the semaphore if necessary and creates the
					// posting function
					SemaphorePost post = new SemaphorePost(container, buffers, com,
							SemaphoreType.empty);

					loopCode.addCodeElementBefore(taskElement, pend);
					loopCode.addCodeElementAfter(taskElement, post);
				}
			}

			// Getting outgoing send operations
			ownComVertices = thread.getComVertices(task, false);

			if (!ownComVertices.isEmpty()) {
				List<ICodeElement> taskElements = loopCode.getCodeElements(task);
				if(taskElements.size() != 1){
					PreesmLogger.getLogger().log(Level.SEVERE,"Not one single function call for function: " + task.getName());
				}
				ICodeElement taskElement = taskElements.get(0);
				
				for (SDFAbstractVertex com : ownComVertices) {
					// A first token must initialize the semaphore pend due to
					// a sending operation
					Set<SDFEdge> inEdges = (com.getBase().incomingEdgesOf(com));
					List<Buffer> buffers = container.getBuffers(inEdges);
					
					SemaphorePost init = new SemaphorePost(container, buffers, com,
							SemaphoreType.empty);

					beginningCode.addCodeElementBefore(taskElement, init);

					// Creates the semaphore if necessary ; retrieves it
					// otherwise from global declaration and creates the pending
					// function
					SemaphorePend pend = new SemaphorePend(container, buffers, com,
							SemaphoreType.empty);

					// Creates the semaphore if necessary and creates the
					// posting function
					SemaphorePost post = new SemaphorePost(container, buffers, com,
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
		
		List<SDFAbstractVertex> specialVertices = new ArrayList<SDFAbstractVertex>(vertices);
		List<SDFAbstractVertex> treatedVertices  = new ArrayList<SDFAbstractVertex>();
		while(specialVertices.size() > 0){
			SDFAbstractVertex origin = specialVertices.get(0);
			if(origin instanceof CodeGenSDFForkVertex || origin instanceof CodeGenSDFBroadcastVertex ){
				boolean toBeTreated = true ;
				for(SDFEdge inEdge : origin.getBase().incomingEdgesOf(origin)){
					if(!treatedVertices.contains(inEdge.getSource()) && (inEdge.getSource() instanceof CodeGenSDFJoinVertex || inEdge.getSource() instanceof CodeGenSDFRoundBufferVertex)){
						specialVertices.remove(inEdge.getSource());
						specialVertices.add(0, inEdge.getSource());
						toBeTreated = false ;
					}
				}
				if(toBeTreated){
					CodeElementFactory.treatSpecialBehaviorVertex(origin
							.getName(), loopCode, origin);
					treatedVertices.add(origin);
					specialVertices.remove(origin);
				}
			}
			else if(origin instanceof CodeGenSDFJoinVertex || origin instanceof CodeGenSDFRoundBufferVertex){
				boolean toBeTreated = true ;
				for(SDFEdge outEdge : origin.getBase().outgoingEdgesOf(origin)){
					if(!treatedVertices.contains(outEdge.getTarget()) && (outEdge.getTarget() instanceof CodeGenSDFJoinVertex || outEdge.getTarget() instanceof CodeGenSDFRoundBufferVertex)){
						specialVertices.remove(outEdge.getTarget());
						specialVertices.add(0, outEdge.getTarget());
						toBeTreated = false ;
					}
				}
				if(toBeTreated){
					CodeElementFactory.treatSpecialBehaviorVertex(origin
							.getName(), loopCode, origin);
					treatedVertices.add(origin);
					specialVertices.remove(origin);
				}
			}else{
				specialVertices.remove(origin);
			}
		}
		for (SDFAbstractVertex vertex : vertices) {
			if(vertex instanceof CodeGenSDFForkVertex || vertex instanceof CodeGenSDFJoinVertex || vertex instanceof CodeGenSDFBroadcastVertex || vertex instanceof CodeGenSDFRoundBufferVertex){
				CodeElementFactory.treatSpecialBehaviorVertex(vertex
						.getName(), loopCode, vertex);
			}
		}
		for (SDFAbstractVertex vertex : vertices) {
			if(vertex instanceof ICodeGenSDFVertex && vertex.getGraphDescription() == null){
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
			if(vertex instanceof ICodeGenSDFVertex){
				ICodeElement loopCall = CodeElementFactory.createElement(vertex
						.getName(), loopCode, vertex);
				if(loopCall != null){
					loopCode.addCodeElement(loopCall);
				}
			}
		}
	}
}
