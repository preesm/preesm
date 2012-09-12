/**
 * 
 */
package org.ietr.preesm.codegen.communication;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MemRouteStep;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;

/**
 * Given a route step, returns the correct communication code generator
 * 
 * @author mpelcat
 */
public class ComCodeGeneratorFactory {

	/**
	 * Communication generators for the different types of communication
	 */
	private Map<AbstractRouteStep, IComCodeGenerator> generators = null;
	
	/**
	 * Loop or initialization code container
	 */
	private AbstractCodeContainer container;

	/**
	 * The considered communication vertices (send, receive)
	 */
	private SortedSet<SDFAbstractVertex> vertices;

	public ComCodeGeneratorFactory(AbstractCodeContainer container,
			SortedSet<SDFAbstractVertex> vertices) {
		super();
		this.generators = new HashMap<AbstractRouteStep, IComCodeGenerator>();
		this.vertices = vertices;
		this.container = container;
	}

	public IComCodeGenerator getCodeGenerator(AbstractRouteStep step) {

		if (!generators.containsKey(step)) {
			generators.put(step, createCodeGenerator(step));
		}

		return generators.get(step);
	}

	private IComCodeGenerator createCodeGenerator(AbstractRouteStep step) {
		IComCodeGenerator generator = null;

		if (step.getType() == DmaRouteStep.type
				|| step.getType() == MessageRouteStep.type
				|| step.getType() == MemRouteStep.type) {
			generator = new GenericComCodeGenerator(container, vertices, step);
		} else {
			WorkflowLogger.getLogger().log(
					Level.SEVERE,
					"A route step with unknown type was found during code generation: "
							+ step);
		}

		return generator;
	}
}
