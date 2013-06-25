/**
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
 
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
