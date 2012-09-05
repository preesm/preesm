/**
 * 
 */
package org.ietr.preesm.codegen.communication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.codegen.model.FunctionCall;
import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionCall;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionInit;
import org.ietr.preesm.codegen.model.threads.ComputationThreadDeclaration;
import org.ietr.preesm.codegen.model.types.CodeSectionType;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;

/**
 * Generating communication code (initialization and calls) for a given type of
 * Route Step The abstract class gathers the common commands
 * 
 * @author mpelcat
 */
public abstract class AbstractComCodeGenerator implements IComCodeGenerator {

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
			SortedSet<SDFAbstractVertex> vertices, AbstractRouteStep step) {
		super();
		this.compThread = compThread;
		this.alreadyInits = new HashSet<CommunicationFunctionInit>();
		this.vertices = vertices;
		this.step = step;
	}

	/**
	 * Creating coms for a given communication vertex
	 */
	@Override
	public void createComs(SDFAbstractVertex vertex) {

		// Creating and adding the inits and calls to send and receive
		// functions: loop phase
		List<CommunicationFunctionCall> loopComs = createStartCalls(compThread,
				vertex, CodeSectionType.loop);
		if (loopComs != null && !loopComs.isEmpty()) {
			for (CommunicationFunctionCall call : loopComs) {
				compThread.getLoopCode().addCodeElement(call);
				createinits(call, compThread.getGlobalContainer(), alreadyInits);
			}
		}

	}

	protected abstract void createinits(CommunicationFunctionCall call,
			AbstractBufferContainer bufferContainer,
			Set<CommunicationFunctionInit> alreadyInits);

	protected abstract List<CommunicationFunctionCall> createStartCalls(
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex,
			CodeSectionType codeContainerType);
	
	/**
	 * Returns the call if a call exists for the given vertex in the current thread
	 * container of the given type (beginning, loop or end code). This is
	 * checked in the IDL prototype.
	 */
	protected FunctionCall getCallForCodeContainerType(SDFAbstractVertex vertex,
			CodeSectionType codeContainerType) {

		if (vertex instanceof ICodeGenSDFVertex
				&& vertex.getGraphDescription() == null) {

			// Looks for function call in the prototype
			FunctionCall vertexCall = (FunctionCall) vertex.getRefinement();
			
			// A special vertex is considered to have always loop code
			if (codeContainerType.equals(CodeSectionType.loop)
					&& (vertex instanceof CodeGenSDFBroadcastVertex
							|| vertex instanceof CodeGenSDFForkVertex
							|| vertex instanceof CodeGenSDFJoinVertex || vertex instanceof CodeGenSDFRoundBufferVertex)) {
				return vertexCall;
			}
			
			if(vertexCall == null){
				WorkflowLogger.getLogger().log(Level.WARNING,"Function call " + vertex.getName() + " has no refinement. Its associated send/receive will not be generated. Check IDL prototype.");
			} else {

				if (!vertexCall.getFunctionName().isEmpty()
						&& codeContainerType.equals(CodeSectionType.loop)) {
					return vertexCall;
				}

				if (vertexCall.getInitCall() != null
						&& codeContainerType.equals(CodeSectionType.beginning)) {
					return vertexCall;
				}

				if (vertexCall.getEndCall() != null
						&& codeContainerType.equals(CodeSectionType.end)) {
					return vertexCall;
				}
			}
		} else if (vertex.getGraphDescription() != null) {
			WorkflowLogger.getLogger().log(Level.WARNING,"Function call " + vertex.getName() + " is hierarchical. Yet to be managed.");
			return null;
		}

		return null;
	}
}
