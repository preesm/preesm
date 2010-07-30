/**
 * 
 */
package org.ietr.preesm.plugin.codegen.communication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionInit;
import org.ietr.preesm.core.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.core.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.core.codegen.threads.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.threads.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Generating communication code (initialization and calls) for a given type of
 * Route Step The abstract class gathers the common commands
 * 
 * @author mpelcat
 */
public abstract class AbstractComCodeGenerator implements IComCodeGenerator {

	/**
	 * Communication thread
	 */
	protected CommunicationThreadDeclaration comThread = null;

	/**
	 * Computation thread
	 */
	protected ComputationThreadDeclaration compThread = null;

	/**
	 * Initializing the Send and Receive channels only for the channels really
	 * used and only once per channel
	 */
	protected Set<CommunicationFunctionInit> alreadyInits = null;

	/**
	 * The considered communication vertices (send, receive)
	 */
	protected SortedSet<SDFAbstractVertex> vertices = null;

	/**
	 * The considered communication vertices (send, receive)
	 */
	protected AbstractRouteStep step = null;

	public AbstractComCodeGenerator(ComputationThreadDeclaration compThread,
			CommunicationThreadDeclaration comThread,
			SortedSet<SDFAbstractVertex> vertices, AbstractRouteStep step) {
		super();
		this.comThread = comThread;
		this.compThread = compThread;
		this.alreadyInits = new HashSet<CommunicationFunctionInit>();
		this.vertices = vertices;
		this.step = step;
	}

	@Override
	public void createComs(SDFAbstractVertex vertex) {
		// Creating and adding the inits and calls to send and receive
		// functions: init phase
		/*
		 * List<CommunicationFunctionCall> beginningComs =
		 * createCalls(comThread, vertex, CodeSectionType.beginning);
		 * 
		 * if (beginningComs != null && !beginningComs.isEmpty()) { for
		 * (CommunicationFunctionCall call : beginningComs) {
		 * comThread.getBeginningCode().addCodeElement(call); createinits(call,
		 * comThread.getGlobalContainer(), alreadyInits); } }
		 */

		// Creating and adding the inits and calls to send and receive
		// functions: loop phase
		List<CommunicationFunctionCall> loopComs = createCalls(comThread,
				vertex, CodeSectionType.loop);
		if (loopComs != null && !loopComs.isEmpty()) {
			for (CommunicationFunctionCall call : loopComs) {
				comThread.getLoopCode().addCodeElement(call);
				createinits(call, comThread.getGlobalContainer(), alreadyInits);
			}
		}

		// Creating and adding the inits and calls to send and receive
		// functions: end phase
		/*
		 * List<CommunicationFunctionCall> endComs = createCalls(comThread,
		 * vertex, CodeSectionType.end); if (endComs != null &&
		 * !endComs.isEmpty()) { for (CommunicationFunctionCall call : endComs)
		 * { comThread.getEndCode().addCodeElement(call); createinits(call,
		 * comThread.getGlobalContainer(), alreadyInits); } }
		 */
	}

	protected abstract void createinits(CommunicationFunctionCall call,
			AbstractBufferContainer bufferContainer,
			Set<CommunicationFunctionInit> alreadyInits);

	protected abstract List<CommunicationFunctionCall> createCalls(
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex,
			CodeSectionType codeContainerType);

	/**
	 * Returns true if a call exists for the given vertex in the current thread
	 * container of the given type (beginning, loop or end code). This is
	 * checked in the IDL prototype.
	 */
	protected boolean hasCallForCodeContainerType(SDFAbstractVertex vertex,
			CodeSectionType codeContainerType) {

		if (vertex instanceof ICodeGenSDFVertex
				&& vertex.getGraphDescription() == null) {

			// A special vertex is considered to have always loop code
			if (codeContainerType.equals(CodeSectionType.loop)
					&& (vertex instanceof CodeGenSDFBroadcastVertex
							|| vertex instanceof CodeGenSDFForkVertex
							|| vertex instanceof CodeGenSDFJoinVertex || vertex instanceof CodeGenSDFRoundBufferVertex)) {
				return true;
			}

			// Looks for function call in the prototype
			FunctionCall vertexCall = (FunctionCall) vertex.getRefinement();
			if (vertexCall != null) {

				if (!vertexCall.getFunctionName().isEmpty()
						&& codeContainerType.equals(CodeSectionType.loop)) {
					return true;
				}

				if (vertexCall.getInitCall() != null
						&& codeContainerType.equals(CodeSectionType.beginning)) {
					return true;
				}

				if (vertexCall.getEndCall() != null
						&& codeContainerType.equals(CodeSectionType.end)) {
					return true;
				}
			}
		}else if(vertex.getGraphDescription() != null){
			return true ;
		}

		return false;
	}
}
