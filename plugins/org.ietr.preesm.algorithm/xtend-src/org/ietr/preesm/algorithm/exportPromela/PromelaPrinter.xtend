/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.algorithm.exportPromela

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date
import java.util.List
import java.util.Map
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.ietr.preesm.core.scenario.PreesmScenario

/**
 * Printer used to generate a Promela program as specified in : <br>
 * <br>
 * <code>Marc Geilen, Twan Basten, and Sander Stuijk. 2005. Minimising buffer 
 * requirements of synchronous dataflow graphs with model checking. In 
 * Proceedings of the 42nd annual Design Automation Conference (DAC '05). ACM, 
 * New York, NY, USA, 819-824. DOI=10.1145/1065579.1065796 
 * http://doi.acm.org/10.1145/1065579.1065796 </code>
 * 
 * @author kdesnos
 * 
 */
class PromelaPrinter {

	/**
	 * The {@link SDFGraph} printed by the current instance of {@link 
	 * SDFPrinter}.
	 */
	@Accessors
	val SDFGraph sdf

	/**
	 * The {@link PreesmScenario} used to obtain data type sizes
	 * for the FIFOs of the printed {@link SDFGraph}.
	 */
	@Accessors
	val PreesmScenario scenario

	/**
	 * List of the edges of the graph.
	 * This list has a constant order, which is needed to make sure that the 
	 * index used to access a FIFO in the generated code will always be the
	 * same. 
	 */
	@Accessors
	val List<SDFEdge> fifos

	/**
	 * Stores the GCD of all fifos
	 * GCD is computed with the number of token produces, consumed and delayed 
	 * on the fifo.
	 */
	@Accessors
	val Map<SDFEdge, Integer> fifoGCDs

	/**
	 * Stores the data type size of all fifos
	 * Data type size is retrieved from the scenario
	 */
	@Accessors
	val Map<SDFEdge, Integer> fifoDataSizes

	/**
	 * Specify whether the FIFOs are to be allocated in dedicated separate 
	 * spaces, or are sharing memory.
	 */
	@Accessors
	var boolean fifoSharedAlloc = false

	/**
	 * Specify whether the production and consumption of tokens are to be
	 * considered as sychronous (i.e. if the occur virtually simultaneously
	 * or if they occur one after the other during the firing of an actor).
	 * 
	 */
	@Accessors
	var boolean synchronousActor = true

	new(SDFGraph sdf, PreesmScenario scenario) {
		this.sdf = sdf
		this.scenario = scenario
		this.fifos = sdf.edgeSet.toList
		fifoGCDs = fifos.toInvertedMap [
			it.prod.intValue.gcd(it.cons.intValue).gcd((it.delay ?: new SDFIntEdgePropertyType(0)).intValue)
		]
		fifoDataSizes = fifos.toInvertedMap [
			scenario.simulationManager.getDataTypeSizeOrDefault(it.dataType.toString)
		]
	}

	/**
	 * Computes the Greatest Common Divisor of two numbers.
	 */
	static def int gcd(int a, int b) {
		if(b == 0) return a
		return gcd(b, a % b)
	}

	/** 
	 * Write the result of a call to the {@link #print()} method in the given {@link File} 
	 * 
	 * @param file the File where to print the code. 
	 */
	def void write(File file) {
		try {
			val writer = new FileWriter(file);
			writer.write(this.print().toString);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method to print the {@link SDFGraph} in the Promela format.
	 * 
	 * @return the {@link CharSequence} containing the PML representation of 
	 * the graph.
	 */
	def String print() '''
		// Printed from «sdf.name» 
		// at «new Date»
		
		#define UPDATE(c) if :: ch[c]>sz[c] -> sz[c] = ch[c] :: else fi
		#define PRODUCE(c,n) ch[c] = ch[c] + n; UPDATE(c)
		#define CONSUME(c,n) ch[c] = ch[c] - n
		#define WAIT(c,n) ch[c]>=n
		#define BOUND «fifos.fold(0,[
			res, fifo | res + (fifo.prod.intValue 
			+ fifo.cons.intValue - fifoGCDs.get(fifo) 
			+ (fifo.delay?:new SDFIntEdgePropertyType(0)).intValue % fifoGCDs.get(fifo))*fifoDataSizes.get(fifo) 
		])» // Initialized with the sum of prod + cons - gcd(prod,cons,delay) + delay % gcd(prod,cons,delay)
		#define SUM «FOR fifo : fifos SEPARATOR " + "»«if(fifoSharedAlloc)"ch" else "sz"»[«fifos.indexOf(fifo)»]*«fifoGCDs.get(fifo)»*«fifoDataSizes.get(fifo)»«ENDFOR»
		#define t (SUM>BOUND)
		
		ltl P1 { <> (SUM>BOUND) }
		
		int ch[«fifos.size»]; int sz[«fifos.size»];
		«IF !synchronousActor»
		bool inProgress = false;
		«ENDIF»
		
		«FOR actor : sdf.vertexSet»
			«actor.print»
		«ENDFOR»
		
		init {
			atomic {
				«FOR fifo : fifos.filter[it.delay !== null && it.delay.intValue>0]»
					PRODUCE(«fifos.indexOf(fifo)», «fifo.delay.intValue / fifoGCDs.get(fifo)»);
				«ENDFOR»
				
				«FOR actor : sdf.vertexSet»
					run «actor.name»();
				«ENDFOR»
			}
		}		
	'''

	/**
	 * Print an {@link SDFAbstractVertex} of the graph.
	 * 
	 * @param actor
	 * 	the printed {@link SDFAbstractVertex}.
	 * 
	 * @return the {@link CharSequence} containing the PML code for the
	 * actor and its ports in the Promela format.
	 */
	def String print(
		SDFAbstractVertex actor
	) '''
		proctype «actor.name»(){
			«val isMultistateActor = !synchronousActor && sdf.incomingEdgesOf(actor).size !=0 && sdf.outgoingEdgesOf(actor).size !=0»
			«IF isMultistateActor »
				bool executing = false;
			«ENDIF»
			do
			:: d_step {
				«IF isMultistateActor»!inProgress && !executing &&«ENDIF»
				«FOR input : sdf.incomingEdgesOf(actor) SEPARATOR " && \n" AFTER "->\n"»WAIT(«fifos.indexOf(input)», «input.cons.intValue / fifoGCDs.get(input)»)«ENDFOR»
				«IF isMultistateActor»inProgress=true;«ENDIF»
				«FOR output : sdf.outgoingEdgesOf(actor)»
					PRODUCE(«fifos.indexOf(output)», «output.prod.intValue / fifoGCDs.get(output)»);
				«ENDFOR»
				«IF isMultistateActor»executing = true;«ENDIF»
			«IF isMultistateActor»
			}
			:: d_step {	
			«ENDIF»
				«IF isMultistateActor»
					executing ->
					inProgress = false;
				«ENDIF»
				«FOR input : sdf.incomingEdgesOf(actor)»
					CONSUME(«fifos.indexOf(input)», «input.cons.intValue / fifoGCDs.get(input)»);
				«ENDFOR»
				«IF isMultistateActor»executing = false;«ENDIF»
			}
			od
		}
	'''

}
