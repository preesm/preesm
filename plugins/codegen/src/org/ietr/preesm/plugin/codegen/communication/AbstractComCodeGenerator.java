/**
 * 
 */
package org.ietr.preesm.plugin.codegen.communication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.codegen.CodeSectionType;
import org.ietr.preesm.core.codegen.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionInit;
import org.ietr.preesm.core.codegen.com.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.model.FunctionCall;
import org.ietr.preesm.core.codegen.model.ICodeGenSDFVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Generating communication code (initialization and calls) for a given type of
 * Route Step The abstract class gathers the common commands
 * 
 * @author mpelcat
 */
public abstract class AbstractComCodeGenerator implements IComCodeGenerator {

	protected CommunicationThreadDeclaration comThread = null;
	protected ComputationThreadDeclaration compThread = null;

	// Initializing the Send and Receive channels only for the channels
	// really used and only once per channel
	protected Set<CommunicationFunctionInit> alreadyInits = null;

	protected SortedSet<SDFAbstractVertex> vertices = null;

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
		// Creating and adding the calls to send and receive functions
		List<CommunicationFunctionCall> beginningComs = createCalls(comThread,
				vertex, CodeSectionType.beginning);
		if (!beginningComs.isEmpty()) {
			for (CommunicationFunctionCall call : beginningComs) {
				comThread.getBeginningCode().addCodeElement(call);
				createinits(call, comThread.getGlobalContainer(), alreadyInits);
			}
		}

		List<CommunicationFunctionCall> loopComs = createCalls(comThread,
				vertex, CodeSectionType.loop);
		if (!loopComs.isEmpty()) {
			for (CommunicationFunctionCall call : loopComs) {
				comThread.getLoopCode().addCodeElement(call);
				createinits(call, comThread.getGlobalContainer(), alreadyInits);
			}
		}

		List<CommunicationFunctionCall> endComs = createCalls(comThread,
				vertex, CodeSectionType.end);
		if (!endComs.isEmpty()) {
			for (CommunicationFunctionCall call : endComs) {
				comThread.getEndCode().addCodeElement(call);
				createinits(call, comThread.getGlobalContainer(), alreadyInits);
			}
		}
	}

	protected abstract void createinits(CommunicationFunctionCall call,
			AbstractBufferContainer bufferContainer,
			Set<CommunicationFunctionInit> alreadyInits);

	protected abstract List<CommunicationFunctionCall> createCalls(
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex,
			CodeSectionType codeContainerType);

	/**
	 * Returns true if a call exists for the given vertex in the current thread
	 * container of the given type (beginning, loop or end code)
	 */
	protected boolean hasCallForCodeContainerType(SDFAbstractVertex vertex,
			CodeSectionType codeContainerType) {

		if (vertex instanceof ICodeGenSDFVertex
				&& vertex.getGraphDescription() == null) {
			FunctionCall vertexCall = (FunctionCall) vertex.getRefinement();
			if (vertexCall != null) {

				if (!vertexCall.getFunctionName().isEmpty()
						&& codeContainerType.equals(CodeSectionType.loop)) {
					return true;
				}

				if (vertexCall.getInitCall() != null
						&& codeContainerType
								.equals(CodeSectionType.beginning)) {
					return true;
				}

				if (vertexCall.getEndCall() != null
						&& codeContainerType.equals(CodeSectionType.end)) {
					return true;
				}
			}
		}

		return false;
	}
}
