/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2017)
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
package org.preesm.codegen.xtend.printer.c.instrumented

import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import org.preesm.codegen.model.ActorFunctionCall
import org.preesm.codegen.model.Block
import org.preesm.codegen.model.Buffer
import org.preesm.codegen.model.CodeElt
import org.preesm.codegen.model.CodegenFactory
import org.preesm.codegen.model.CoreBlock
import org.preesm.codegen.model.FifoCall
import org.preesm.codegen.model.FunctionCall
import org.preesm.codegen.model.LoopBlock
import org.preesm.codegen.model.PortDirection
import org.preesm.codegen.model.SharedMemoryCommunication
import org.preesm.codegen.model.SpecialCall
import org.preesm.codegen.model.Variable
import org.preesm.codegen.printer.PrinterState
import org.preesm.codegen.xtend.printer.c.CPrinter
import org.preesm.commons.exceptions.PreesmRuntimeException

/**
 * This printer currently prints instrumented C code for X86 cores with all
 * communications made in the shared memory.
 * 
 * Only actor firings are instrumented now.
 *
 * @author kdesnos
 */
class InstrumentedCPrinter extends CPrinter {

	new() {
		// generate a main file
		this(true);
	}

	/**
	 * expose argument to child classes
	 */
	new(boolean generateMainFile) {
		super(generateMainFile)
	}

	/**
	 * Buffer storing the timing dumped by the actors
	 */
	protected Buffer dumpTimedBuffer

	/**
	 * Buffer storing the number of execution of the actors
	 */
	protected Buffer nbExec

	/**
	 * This map associates each codeElt to its ID
	 */
	protected var LinkedHashMap<CodeElt, Integer> codeEltID = new LinkedHashMap<CodeElt,Integer>()

	/**
	 * Map associating actor names to their different IDs
	 */
	var actorIDs = new LinkedHashMap<String, List<Integer>>()

	/**
	 * Add instrumentation code to the {@link Block blocks}.<br>
	 * In the current version, the instrumentation consists of:<br>
	 * - A shared {@link Buffer} that stores all measured durations.<br>
	 * - Calls to two <code>dumpTime(ID, Buffer)</code> around each actor firings.<br>
	 *
	 * @param blocks
	 * 			List of the blocks printed by the printer. (will be
	 * 			modified)
	 */
	override preProcessing(List<Block> printerBlocks, Collection<Block> allBlocks) {
		super.preProcessing(printerBlocks, allBlocks)

		// Create the Buffers
		dumpTimedBuffer = CodegenFactory::eINSTANCE.createBuffer
		dumpTimedBuffer.name = "dumpedTimes"
		dumpTimedBuffer.type = "uint64_t"
		dumpTimedBuffer.typeSize = 8 // size of a long

		nbExec = CodegenFactory::eINSTANCE.createBuffer
		nbExec.name = "nbExec"
		nbExec.type = "int"

		// 1. Scan the blocks to add the dumpTime calls
		// globalID uniquely identify all calls to dumpTime(globalID)
		var globalID = 0;

		// Map associating each ID with the name of what is measures
		var globalFunctionID = new LinkedHashMap<Integer, String>()

		for (Block block : printerBlocks) {
			if (dumpTimedBuffer.creator === null) {
				dumpTimedBuffer.reaffectCreator(block);
				nbExec.reaffectCreator(block)
			}
			dumpTimedBuffer.users.add(block)
			nbExec.users.add(block)

			var coreLoop = (block as CoreBlock).loopBlock

			// Insert a call after each codeElt.
			var i = 1;
			while (i < coreLoop.codeElts.size) {

				// Retrieve the function ID
				val elt = coreLoop.codeElts.get(i)
				val functionID = switch elt {
					FunctionCall:
						elt.name
					SpecialCall:
						elt.name
					SharedMemoryCommunication:
						elt.direction.toString.toLowerCase + elt.delimiter.toString.toLowerCase.toFirstUpper +
							elt.data.name
					FifoCall:
						elt.name
					default:
						"undefined"
				}

				if (elt instanceof ActorFunctionCall) {
					val ovp = elt.originalVertexPath

					// Do the pre insertion
					val preDumpCall = CodegenFactory.eINSTANCE.createFunctionCall
					preDumpCall.name = "dumpTime"
					preDumpCall.addParameter(
					{
						val const = CodegenFactory::eINSTANCE.createConstant
						const.name = "globalID"
						const.type = "int"
						const.value = globalID
						const
					}, PortDirection.NONE)
					preDumpCall.addParameter(dumpTimedBuffer, PortDirection.NONE)
					coreLoop.codeElts.add(i, preDumpCall)

					// Register the call
					globalFunctionID.put(globalID, ovp)
					var actorID = actorIDs.get(ovp) ?: {
						actorIDs.put(ovp, new ArrayList<Integer>)
						actorIDs.get(ovp)
					}
					actorID.add(globalID/2);
					codeEltID.put(elt, globalID+1)

					// Do the pre insertion
					val postDumpCall = CodegenFactory.eINSTANCE.createFunctionCall
					postDumpCall.name = "dumpTime"
					postDumpCall.addParameter(
					{
						val const = CodegenFactory::eINSTANCE.createConstant
						const.name = "globalID"
						const.type = "int"
						const.value = globalID+1
						const
					}, PortDirection.NONE)
					postDumpCall.addParameter(dumpTimedBuffer, PortDirection.NONE)
					coreLoop.codeElts.add(i + 2, postDumpCall)


					// Increment the indices
					globalID = globalID + 2
					i = i + 3

				} else {
					i = i + 1
	
				}			
			}
		}

		// Set the final size of the Buffer
		dumpTimedBuffer.size = globalID
		nbExec.size = globalID/2

		// Create the init method
		var initCall = CodegenFactory.eINSTANCE.createFunctionCall;
		initCall.name = "initNbExec"
		initCall.addParameter(nbExec, PortDirection.NONE)
		initCall.addParameter(
			{
				var const = CodegenFactory::eINSTANCE.createConstant
				const.name = "nbDump"
				const.type = "int"
				const.value = dumpTimedBuffer.size
				const
			}, PortDirection.NONE)
		(printerBlocks.head as CoreBlock).initBlock.codeElts.add(initCall)
	}

	override printDefinitionsFooter(List<Variable> list) '''
		int idx;
		«super.printDefinitionsFooter(list)»
	'''

	override printCoreLoopBlockFooter(LoopBlock block2) '''
				pthread_barrier_wait(&iter_barrier);
				«IF dumpTimedBuffer.creator == block2.eContainer»
						writeTime(«dumpTimedBuffer.doSwitch»,«{
							val const = CodegenFactory::eINSTANCE.createConstant
							const.name = "nbDump"
							const.type = "int"
							const.value = dumpTimedBuffer.size
							const
						}.doSwitch», «nbExec.doSwitch»);
				«ENDIF»
		«super.printCoreLoopBlockFooter(block2)»
	'''

	def String printInstrumentedCall(CodeElt elt, CharSequence superPrint)'''
	«IF (getState()== PrinterState::PRINTING_LOOP_BLOCK) && codeEltID.get(elt) !== null»
	for(idx=0; idx<*(«nbExec.doSwitch»+«codeEltID.get(elt)/2»); idx++){
		«superPrint»
	}
	«ELSE»
	«superPrint»
	«ENDIF»
	'''

	/**
	 * We do not instrument fifo call since this would mess up with the semaphores
	 */
	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«IF (state == PrinterState::PRINTING_LOOP_BLOCK) && codeEltID.get(communication) !== null»*(«nbExec.doSwitch»+«codeEltID.get(communication)/2») = 0;«ENDIF»
		«super.printSharedMemoryCommunication(communication)»
	'''

	override printFunctionCall(FunctionCall functionCall) {
		return printInstrumentedCall(functionCall,super.printFunctionCall(functionCall))
	}

	/**
	 * Special call englobes printFork, Join, Broadcast, RoundBuffer
	 */
	override caseSpecialCall(SpecialCall specialCall) {
		return printInstrumentedCall(specialCall,super.caseSpecialCall(specialCall))
	}

	/**
	 * We do not instrument fifo call since this would mess up with the memory
	 */
	override printFifoCall(FifoCall fifoCall)'''
	«IF (state == PrinterState::PRINTING_LOOP_BLOCK) && codeEltID.get(fifoCall) !== null»*(«nbExec.doSwitch»+«codeEltID.get(fifoCall)/2») = 0;«ENDIF»
	«super.printFifoCall(fifoCall)»
	'''

	/**
 	 * Write the csv file.
	 */
	override createSecondaryFiles(List<Block> printerBlocks, Collection<Block> allBlocks) {
		val result = super.createSecondaryFiles(printerBlocks,allBlocks);
		val mapActorNbRow = new HashMap<String, Integer>()
		val mapActorNbFiring = new HashMap<String, Integer>()
		var i = 1
		for (entry: actorIDs.entrySet) {
			val name = entry.key
			// We can register the apparition order because it is a LinkedHashMap
			// And we need that fot the SUM generation
			mapActorNbRow.put(name, i)
			mapActorNbFiring.put(name, entry.value.size)
			i += 1
		}
		result.put("analysis.csv", printAnalysisCsvFile(mapActorNbRow, mapActorNbFiring))
		return result
	}

	/**
	 * Generate the average formula for each actor, and the sum of everything.
	 */
	def String printAnalysisCsvFile(Map<String,Integer> mapActorNbRow, Map<String,Integer> mapActorNbFiring)'''
	«FOR entry : actorIDs.entrySet»
	«entry.key»;"=MROUND(AVERAGE(«FOR id : entry.value SEPARATOR ';'»«(id+1).intToColumn»«actorIDs.size + 4»:«(id+1).intToColumn»65536«ENDFOR»);1)"
	«ENDFOR»
	"TIMINGS WEIGHTED SUM";"=SUM(«FOR entry : actorIDs.entrySet SEPARATOR ','»«mapActorNbFiring.get(entry.key)»*B«mapActorNbRow.get(entry.key)»«ENDFOR»)"
	'''

	/**
	 * Returns corresponding number in the base of the 26 letters A-Z.
	 * Starts from 1 (returns A).
	 */
	def String intToColumn(int i){
		if (i <= 0) {
			throw new PreesmRuntimeException ("Invalid non strictly positive argument.")
		}
		val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
		var result = ""
		var digit = 0
		var rest = i
		do {
			digit = (rest-1) % 26
			rest = (rest-digit) / 26
			result = alphabet.charAt(digit) + result
		} while (rest > 0)
		return result
	}

}
