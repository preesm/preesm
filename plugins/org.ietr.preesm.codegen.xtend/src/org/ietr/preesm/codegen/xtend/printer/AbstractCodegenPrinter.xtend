package org.ietr.preesm.codegen.xtend.printer

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph
import net.sf.dftools.architecture.slam.Design
import org.ietr.preesm.core.scenario.PreesmScenario
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph

/**
 * The {@link org.ietr.preesm.codegen.xtend.task.CodegenPrinter} class is the main class of the Xtend code 
 * generation plugin.<br>
 *
 * @author kdesnos
 */
class AbstractCodegenPrinter {
	/**
	 * Targeted {@link Design Architecture} of the code generation 
	 */
	Design archi

	/**
	 * {@link DirectedAcyclicGraph DAG} used to generate code.
	 * This {@link DirectedAcyclicGraph DAG} must be the result of
	 * mapping/scheduling process.
	 */
	DirectedAcyclicGraph dag

	/**
	 * {@link MemoryExclusionGraph MemEx} used to generate code.
	 * This {@link MemoryExclusionGraph MemEx} must be the result of
	 * an allocation process.
	 * @see MemoryAllocator
	 */
	MemoryExclusionGraph memEx

	/**
	 * {@link PreesmScenario Scenario} at the origin of the call
	 * to the {@link org.ietr.preesm.codegen.xtend.task.CodegenPrinter Code Generator}.
	 */
	PreesmScenario scenario

	/**
 	 *  Constructor of the {@link org.ietr.preesm.codegen.xtend.task.CodegenPrinter}.
 	 *  @param archi See {@link org.ietr.preesm.codegen.xtend.task.CodegenPrinter#archi}
 	 *  @param dag See {@link org.ietr.preesm.codegen.xtend.task.CodegenPrinter#dag}
 	 *  @param memEx See {@link org.ietr.preesm.codegen.xtend.task.CodegenPrinter#memEx}
 	 *  @param scenario See {@link org.ietr.preesm.codegen.xtend.task.CodegenPrinter#scenario}
 	 */
	new(Design archi, DirectedAcyclicGraph dag, MemoryExclusionGraph memEx, PreesmScenario scenario) {
		this.archi = archi
		this.dag = dag
		this.memEx = memEx
		this.scenario = scenario
	}

	/**
	 * Main method to execute the code generation.
	 * This method will produce a Source code file for each core of the 
	 * targeted {@link Design architecture}.
	 */
	def void print() {
		// Create empty source code files
		
		
	}
	
	/**
	 * Create all empty source code files. The location where to create the file is
	 * defined in the scenario
	 */
	//def abstract protected  createSourceFiles() 

}
