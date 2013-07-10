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

import java.util.ArrayList
import java.util.HashMap
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.Variable

/**
 * This printer currently prints instrumented C code for X86 cores with all
 * communications made in the shared memory.
 */
class InstrumentedCPrinter extends CPrinter {

	/**
	 * Buffer storing the timing dumped by the actors
	 */
	Buffer dumpTimedBuffer
	
	/**
	 * Buffer storing the number of execution of the actors
	 */
	Buffer nbExec
	
	/**
	 * This map associates each codeElt to its ID 
	 */
	var codeEltID = new HashMap<CodeElt,Integer>()

	/**
	 * Add instrumentation code to the {@link Block blocks}.<br>
	 * In the current version, the instrumentation consists of:<br>
	 * - A shared {@link Buffer} that stores all measured durations.<br>
	 * - Calls to <code>dumpTime(ID, Buffer)</code> between all actors.<br> 
	 * 
	 * @param blocks
	 * 			List of the blocks printed by the printer. (will be 
	 * 			modified)
	 */
	override preProcessing(List<Block> blocks) {
		super.preProcessing(blocks)

		// Create the Buffers
		dumpTimedBuffer = CodegenFactory::eINSTANCE.createBuffer
		dumpTimedBuffer.name = "dumpedTimes"
		dumpTimedBuffer.type = "long"

		nbExec = CodegenFactory::eINSTANCE.createBuffer
		nbExec.name = "nbExec"
		nbExec.type = "int"

		// 1. Scan the blocks to add the dumpTime calls
		// globalID uniquely identify all calls to dumpTime(globalID)		
		var globalID = 0;

		// Map associating each ID with the name of what is measures
		var globalFunctionID = new HashMap<Integer, String>()

		// Map associating actor names to their different IDs
		var actorIDs = new HashMap<String, List<Integer>>()

		for (Block block : blocks) {
			if (dumpTimedBuffer.creator == null) {
				dumpTimedBuffer.creator = block
				nbExec.creator = block
			}
			dumpTimedBuffer.users.add(block)
			nbExec.users.add(block)

			var coreLoop = (block as CoreBlock).loopBlock

			// Insert first dumpCall of the core (does not correspond to any actor)
			{
				var dumpCall = CodegenFactory.eINSTANCE.createFunctionCall
				dumpCall.name = "dumpTime"
				dumpCall.addParameter(
					{
						var const = CodegenFactory::eINSTANCE.createConstant
						const.name = "globalID"
						const.type = "int"
						const.value = globalID
						const
					})
				globalID = globalID + 1
				dumpCall.addParameter(dumpTimedBuffer)
				coreLoop.codeElts.add(0, dumpCall)
			}

			// Insert a call after each codeElt.
			var i = 1;
			while (i < coreLoop.codeElts.size) {

				// Do the insertion
				val dumpCall = CodegenFactory.eINSTANCE.createFunctionCall
				dumpCall.name = "dumpTime"
				dumpCall.addParameter(
					{
						val const = CodegenFactory::eINSTANCE.createConstant
						const.name = "globalID"
						const.type = "int"
						const.value = globalID
						const
					})
				dumpCall.addParameter(dumpTimedBuffer)
				coreLoop.codeElts.add(i + 1, dumpCall)

				// Retrieve the function ID
				val elt = coreLoop.codeElts.get(i)
				val functionID = switch elt {
					FunctionCall case true:
						elt.actorName
					SpecialCall case true:
						elt.name
					SharedMemoryCommunication case true:
						elt.direction.toString.toLowerCase + elt.delimiter.toString.toLowerCase.toFirstUpper +
							elt.data.name
					FifoCall case true:
						elt.name
					default:
						"undefined"
				}

				// Register the call
				globalFunctionID.put(globalID, functionID)
				var actorID = actorIDs.get(functionID) ?: {
					actorIDs.put(functionID, new ArrayList<Integer>)
					actorIDs.get(functionID)
				}
				actorID.add(globalID);
				codeEltID.put(elt, globalID)

				// Increment the indices
				globalID = globalID + 1;
				i = i + 2;
			}
		}

		// Set the final size of the Buffer
		dumpTimedBuffer.size = globalID
		nbExec.size = globalID

		// Create the init method
		var initCall = CodegenFactory.eINSTANCE.createFunctionCall;
		initCall.name = "initNbExec"
		initCall.addParameter(nbExec)
		initCall.addParameter(
			{
				var const = CodegenFactory::eINSTANCE.createConstant
				const.name = "nbDump"
				const.type = "int"
				const.value = globalID
				const
			})
		(blocks.head as CoreBlock).initBlock.codeElts.add(initCall)
	}
	
	override printDefinitionsFooter(List<Variable> list) '''
		int idx;
		«super.printDefinitionsFooter(list)»
	'''

	/*override printCoreLoopBlockHeader(LoopBlock block2) '''
		«{  var result = new StringConcatenation
			result.append(super.printCoreLoopBlockHeader(block2))
			result.trimLastEOL
		}»
				«IF dumpTimedBuffer.creator == block2.eContainer» 
						writeTime(«dumpTimedBuffer.doSwitch»,«{
							val const = CodegenFactory::eINSTANCE.createConstant
							const.name = "nbDump"
							const.type = "int"
							const.value = dumpTimedBuffer.size
							const
						}.doSwitch», «nbExec.doSwitch»);
				«ENDIF»
				pthread_barrier_wait(&iter_barrier);
				
	'''*/
	
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
	
	def printInstrumentedCall(CodeElt elt, CharSequence superPrint)'''
	«IF (state == PrinterState::PRINTING_LOOP_BLOCK) && codeEltID.get(elt) != null»
	for(idx=0; idx<*(«nbExec.doSwitch»+«codeEltID.get(elt)»); idx++){
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
		«IF (state == PrinterState::PRINTING_LOOP_BLOCK) && codeEltID.get(communication) != null»*(«nbExec.doSwitch»+«codeEltID.get(communication)») = 0;«ENDIF»
		«super.printSharedMemoryCommunication(communication)»
	'''
	
	override printFunctionCall(FunctionCall functionCall) {
		printInstrumentedCall(functionCall,super.printFunctionCall(functionCall))
	}
	
	/** 
	 * Special call englobes printFork, Join, Broadcast, RoundBuffer
	 */
	override caseSpecialCall(SpecialCall specialCall) {
		printInstrumentedCall(specialCall,super.caseSpecialCall(specialCall))
	}
	
	/**
	 * We do not instrument fifo call since this would mess up with the memory
	 */
	override printFifoCall(FifoCall fifoCall)''' 
	«IF (state == PrinterState::PRINTING_LOOP_BLOCK) && codeEltID.get(fifoCall) != null»*(«nbExec.doSwitch»+«codeEltID.get(fifoCall)») = 0;«ENDIF»
	«super.printFifoCall(fifoCall)»
	'''
}
