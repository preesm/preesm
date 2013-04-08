package org.ietr.preesm.codegen.xtend.task;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.architecture.slam.Design;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

/**
 * The {@link CodegenPrinter} class is the main class of the Xtend code
 * generation plugin.<br>
 * 
 * @author kdesnos
 */
@SuppressWarnings("all")
public class CodegenPrinter {
	/**
	 * Targeted {@link Design Architecture} of the code generation
	 */
	private Design archi;

	/**
	 * {@link DirectedAcyclicGraph DAG} used to generate code. This
	 * {@link DirectedAcyclicGraph DAG} must be the result of mapping/scheduling
	 * process.
	 */
	private DirectedAcyclicGraph dag;

	/**
	 * {@link MemoryExclusionGraph MemEx} used to generate code. This
	 * {@link MemoryExclusionGraph MemEx} must be the result of an allocation
	 * process.
	 * 
	 * @see MemoryAllocator
	 */
	private MemoryExclusionGraph memEx;

	/**
	 * {@link PreesmScenario Scenario} at the origin of the call to the
	 * {@link CodegenPrinter Code Generator}.
	 */
	private PreesmScenario scenario;

	/**
	 * Constructor of the {@link CodegenPrinter}.
	 * 
	 * @param archi
	 *            See {@link CodegenPrinter#archi}
	 * @param dag
	 *            See {@link CodegenPrinter#dag}
	 * @param memEx
	 *            See {@link CodegenPrinter#memEx}
	 * @param scenario
	 *            See {@link CodegenPrinter#scenario}
	 */
	public CodegenPrinter(final Design archi, final DirectedAcyclicGraph dag,
			final MemoryExclusionGraph memEx, final PreesmScenario scenario) {
		this.archi = archi;
		this.dag = dag;
		this.memEx = memEx;
		this.scenario = scenario;
	}

	/**
	 * Main method to execute the code generation. This method will produce a
	 * Source code file for each core of the targeted {@link Design
	 * architecture}.
	 */
	public void print() {
	}
}
