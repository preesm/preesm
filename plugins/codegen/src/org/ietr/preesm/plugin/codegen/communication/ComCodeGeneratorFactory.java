/**
 * 
 */
package org.ietr.preesm.plugin.codegen.communication;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Level;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.core.architecture.route.RamRouteStep;
import org.ietr.preesm.core.codegen.threads.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.threads.ComputationThreadDeclaration;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * Given a route step, returns the correct communication code generator
 * 
 * @author mpelcat
 */
public class ComCodeGeneratorFactory {

	private Map<AbstractRouteStep, IComCodeGenerator> generators = null;
	private CommunicationThreadDeclaration comThread;
	private ComputationThreadDeclaration compThread;

	/**
	 * The considered communication vertices (send, receive)
	 */
	private SortedSet<SDFAbstractVertex> vertices;

	public ComCodeGeneratorFactory(ComputationThreadDeclaration compThread,
			CommunicationThreadDeclaration thread,
			SortedSet<SDFAbstractVertex> vertices) {
		super();
		this.generators = new HashMap<AbstractRouteStep, IComCodeGenerator>();
		this.comThread = thread;
		this.vertices = vertices;
		this.compThread = compThread;
	}

	public IComCodeGenerator getCodeGenerator(AbstractRouteStep step) {

		if (!generators.containsKey(step)) {
			generators.put(step, createCodeGenerator(step));
		}

		return generators.get(step);
	}

	private IComCodeGenerator createCodeGenerator(AbstractRouteStep step) {
		IComCodeGenerator generator = null;

		if (step.getType() == MediumRouteStep.type) {
			generator = new MessageComCodeGenerator(compThread, comThread,
					vertices, step);
			WorkflowLogger
					.getLogger()
					.log(Level.FINE,
							"A route step with type medium correspond to a message passing code generation: "
									+ step);
		} else if (step.getType() == DmaRouteStep.type) {
			generator = new DmaComCodeGenerator(compThread, comThread,
					vertices, step);
		} else if (step.getType() == MessageRouteStep.type) {
			generator = new MessageComCodeGenerator(compThread, comThread,
					vertices, step);
		} else if (step.getType() == RamRouteStep.type) {
			generator = new RamComCodeGenerator(compThread, comThread,
					vertices, step);
		} else {
			WorkflowLogger.getLogger().log(
					Level.SEVERE,
					"A route step with unknown type was found during code generation: "
							+ step);
		}

		return generator;
	}
}
