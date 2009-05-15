package org.ietr.preesm.plugin.codegen.communication;

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
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Generating communication code (initialization and calls) for a shared ram Route Step
 * 
 * @author mpelcat
 */
public class RamComCodeGenerator extends AbstractComCodeGenerator {
	
	public RamComCodeGenerator(ComputationThreadDeclaration compThread,CommunicationThreadDeclaration comThread, SortedSet<SDFAbstractVertex> vertices, AbstractRouteStep step) {
		super(compThread,comThread,vertices, step);
	}

	/**
	 * Calls the initialization functions at the beginning of computation and
	 * communication thread executions
	 */
	protected void createinits(CommunicationFunctionCall call,
			AbstractBufferContainer bufferContainer,
			Set<CommunicationFunctionInit> alreadyInits) {
	}

	/**
	 * creates a send or a receive depending on the vertex type
	 */
	protected List<CommunicationFunctionCall> createCalls(
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex, CodeSectionType codeContainerType) {



		return null;
	}
}
