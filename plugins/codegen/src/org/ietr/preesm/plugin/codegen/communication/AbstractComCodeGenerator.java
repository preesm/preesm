/**
 * 
 */
package org.ietr.preesm.plugin.codegen.communication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.codegen.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.CommunicationFunctionInit;
import org.ietr.preesm.core.codegen.CommunicationThreadDeclaration;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Generating communication code (initialization and calls) for a given type of Route Step
 * The abstract class gathers the common commands
 * 
 * @author mpelcat
 */
public abstract class AbstractComCodeGenerator implements IComCodeGenerator{
	
	protected CommunicationThreadDeclaration comThread = null;

	// Initializing the Send and Receive channels only for the channels
	// really used and only once per channel
	Set<CommunicationFunctionInit> alreadyInits = null;

	public AbstractComCodeGenerator(CommunicationThreadDeclaration comThread) {
		super();
		this.comThread = comThread;
		alreadyInits = new HashSet<CommunicationFunctionInit>();
	}

	@Override
	public void createComs(SDFAbstractVertex vertex) {
		List<CommunicationFunctionCall> coms = createCalls(comThread,
				vertex);
		if (!coms.isEmpty()) {
			for (CommunicationFunctionCall call : coms) {
				comThread.getLoopCode().addCodeElement(call);
				createinits(call, comThread.getGlobalContainer(), alreadyInits);
			}
		} else {
			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"problem creating a send or receive function call: "
							+ vertex.getName());
		}
	}
	
	protected abstract void createinits(CommunicationFunctionCall call,
			AbstractBufferContainer bufferContainer,
			Set<CommunicationFunctionInit> alreadyInits);
	
	protected abstract List<CommunicationFunctionCall> createCalls(
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex);
	
}
