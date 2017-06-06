/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.xtend.printer.c

import org.ietr.preesm.codegen.xtend.printer.c.C6678CPrinter
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.Call
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.xtend.model.codegen.Direction
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import java.util.Set
import java.util.LinkedHashSet
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory

class DynamicAllocC6678CPrinter extends C6678CPrinter {

	var helperPrinter = new DynamicAllocCPrinter

	/**
	 * Stores the merged {@link Buffer} already
	 * {@link #printBufferDeclaration(Buffer) declared} for the current
	 * {@link CoreBlock}. Must be emptied before printed a new {@link CoreBlock}
	 */
	Set<Buffer> printedMerged

	new() {
		super()
		IGNORE_USELESS_MEMCPY = true
	}

	override printCoreBlockHeader(CoreBlock block) '''
		«{ // Empty the printedMerge
			printedMerged = new LinkedHashSet<Buffer>
			super.printCoreBlockHeader(block)
		}»
		#include <stdlib.h>
		#include <ti/ipc/SharedRegion.h>
		#include <ti/ipc/HeapMemMP.h>
		
		#include <memory.h>
		#include <semaphore6678.h>
		
		
	'''

	override preProcessing(List<Block> printerBlocks, List<Block> allBlocks) {
		super.preProcessing(printerBlocks, allBlocks)

		// Use the DynamicAllocCPrinter to preProcess the blocks
		helperPrinter.IGNORE_USELESS_MEMCPY = true;
		helperPrinter.preProcessing(printerBlocks, allBlocks)

		// Add a character chain to the sem_init call
		for (Block block : printerBlocks) {
			for (CodeElt codeElt : (block as CoreBlock).initBlock.codeElts) {
				if (codeElt instanceof FunctionCall) {
					if (codeElt.name == "sem_init") {
						var semaphore = codeElt.parameters.head
						var string = CodegenFactory.eINSTANCE.createConstantString;
						string.setCreator(block);
						string.setValue(semaphore.name)
						codeElt.addParameter(string, PortDirection.NONE)
					}
				}
			}
		}
	}

	override printCoreInitBlockHeader(CallBlock callBlock) '''
		void «(callBlock.eContainer as CoreBlock).name.toLowerCase»(void){
			HeapMemMP_Handle sharedHeap;
			
			// Initialisation(s)
			communicationInit();
			sharedHeap = SharedRegion_getHeap(1);
			
	'''

	override printBufferDefinition(Buffer buffer) '''
		«IF (!helperPrinter.mergedBuffers.containsKey(buffer))/* No need to declare pointers for merged buffers */»
			#pragma DATA_SECTION(«buffer.name»,".MSMCSRAM");
			#pragma DATA_ALIGN(«buffer.name», CACHE_LINE_SIZE);
			«buffer.type» far *«buffer.name» = 0; // «buffer.comment» size:= «buffer.size»*«buffer.type»
		«ENDIF»
	'''

	override printSubBufferDefinition(SubBuffer buffer) '''
		«printBufferDefinition(buffer)»
	'''

	override printBufferDeclaration(Buffer buffer) {
		if (!helperPrinter.mergedBuffers.containsKey(buffer) && !printedMerged.contains(buffer)) {
			if (helperPrinter.mergedMalloc.containsKey(buffer)) {
				printedMerged.add(buffer)
			}
			return '''
				extern «buffer.type» far *«buffer.name»; // «buffer.comment» size:= «buffer.size»*«buffer.type»
			'''
		} else if (helperPrinter.mergedBuffers.containsKey(buffer)) {
			return printBufferDeclaration(helperPrinter.mergedBuffers.get(buffer))
		} else {
			return ''''''
		}
	}

	override printSubBufferDeclaration(SubBuffer buffer) {
		return printBufferDeclaration(buffer)
	}

	override printBuffer(Buffer buffer) {
		if (!helperPrinter.mergedBuffers.containsKey(buffer)) {

			/* No need to declare pointers for merged buffers */
			return '''«buffer.name»'''
		} else { /* Print the merged buffer name */
			return '''«helperPrinter.mergedBuffers.get(buffer).name»'''
		}
	}

	override printFunctionCall(FunctionCall functionCall) '''
		«printCallWithMallocFree(functionCall, super.printFunctionCall(functionCall))»
	'''

	/**
	 * Add the necessary <code>malloc()</code> and <code>free()</code> calls to
	 * a call passed as a parameter. Before the {@link Call}, a
	 * <code>malloc()</code> for all the output {@link Buffer} of the
	 * {@link Call} is printed. After the {@link Call}, a <code>free()</code> is
	 * printed for each output {@link Buffer} of the {@link Call}.
	 * 
	 * @param call
	 *            the {@link Call} to print.
	 * @param sequence
	 *            the normal printed {@link Call} (by the {@link CPrinter}.
	 * @return the {@link Call} printed with <code>malloc()</code> and
	 */
	def String printCallWithMallocFree(Call call, CharSequence sequence) '''
		«/*Allocate output buffers */IF call.parameters.size > 0»
			«FOR i : 0 .. call.parameters.size - 1»
				«IF call.parameterDirections.get(i) == PortDirection.OUTPUT»
					«printMalloc(call.parameters.get(i) as Buffer)»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
		«sequence»
		«/*Free input buffers*/IF call.parameters.size > 0»
			«FOR i : 0 .. call.parameters.size - 1»
				«IF call.parameterDirections.get(i) == PortDirection.INPUT»
					«printFree(call.parameters.get(i) as Buffer)»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	'''

	override printFifoCall(FifoCall fifoCall) '''
		«IF fifoCall.operation == FifoOperation.INIT»
			«IF fifoCall.bodyBuffer !== null»
				«printMalloc(fifoCall.bodyBuffer)»
			«ENDIF»
		«ENDIF»
		«IF fifoCall.operation == FifoOperation.PUSH || fifoCall.operation == FifoOperation.INIT»
			«printMalloc(fifoCall.headBuffer)»
		«ENDIF»
		«printCallWithMallocFree(fifoCall, super.printFifoCall(fifoCall))»
		«IF fifoCall.operation == FifoOperation.POP»
			«printFree(fifoCall.headBuffer)»
		«ENDIF»
	'''

	override printBroadcast(SpecialCall call) {
		return printCallWithMallocFree(call, super.printBroadcast(call))
	}

	override printRoundBuffer(SpecialCall call) {
		return printCallWithMallocFree(call, super.printRoundBuffer(call))
	}

	override printFork(SpecialCall call) '''
		«printCallWithMallocFree(call, super.printFork(call))»
	'''

	override printJoin(SpecialCall call) '''
		«printCallWithMallocFree(call, super.printJoin(call))»
	'''

	/**
	 * Methods used to print a Malloc.
	 * @param buffer 
	 * 			the {@link Buffer} that is allocated  
	 * @return the printed code as a {@link CharSequence}.
 	 */
	def String printMalloc(Buffer buffer) {
		if (!helperPrinter.mergedBuffers.containsKey(buffer) && !helperPrinter.mergedMalloc.containsKey(buffer) &&
			!helperPrinter.mergedFree.containsKey(buffer)) {

			// If the buffer is not involved in any merge operation
			// print a regular malloc
			return '''
				«buffer.doSwitch» = («buffer.type»*) HeapMemMP_alloc(sharedHeap,«buffer.size»*sizeof(«buffer.type»), CACHE_LINE_SIZE);
				cache_wb(&«buffer.doSwitch», 1);
			'''
		} else if (helperPrinter.mergedMalloc.containsKey(buffer)) {

			// If the buffer is the allocated buffer of a merge
			var string = CodegenFactory.eINSTANCE.createConstantString;
			string.setCreator(buffer.creator);
			string.setValue(buffer.name)
			return '''
				«buffer.doSwitch» = («buffer.type»*) merged_malloc(sharedHeap, (void**)&«buffer.doSwitch»,«buffer.size»*sizeof(«buffer.
					type»), «helperPrinter.mergedMalloc.get(buffer).size + 1»/*nbOfFree*/, «printConstantString(string)»,CACHE_LINE_SIZE);
				cache_wb(&«buffer.doSwitch», 1);
			'''
		} else if (helperPrinter.mergedFree.containsKey(buffer) ||
			(helperPrinter.mergedBuffers.containsKey(buffer) &&
				helperPrinter.mergedFree.containsKey(helperPrinter.mergedBuffers.get(buffer)))) {

			// The buffer is a merged free. It will be allocated several times.
			var mergedBuffer = if (helperPrinter.mergedFree.containsKey(buffer)) {
					buffer
				} else {
					helperPrinter.mergedBuffers.get(buffer)
				}
			var string = CodegenFactory.eINSTANCE.createConstantString;
			string.setCreator(buffer.creator);
			string.setValue(mergedBuffer.name)
			return '''
				«mergedBuffer.doSwitch» = («mergedBuffer.type»*) multiple_malloc(sharedHeap, (void**)&«mergedBuffer.doSwitch»,«mergedBuffer.
					size»*sizeof(«mergedBuffer.type»), «helperPrinter.mergedFree.get(mergedBuffer).size + 1»/*nbOfFree*/, «helperPrinter.
					mergedFreeSemaphore.get(mergedBuffer).doSwitch», «printConstantString(string)», CACHE_LINE_SIZE);
				cache_wb(&«buffer.doSwitch», 1);
			'''
		}

	// else, the buffer is a merged buffer that does not need to be allocated
	}

	/**
	 * Methods used to print a Free.
	 * @param buffer 
	 * 			the {@link Buffer} that freed  
	 * @return the printed code as a {@link CharSequence}.
 	 */
	def String printFree(Buffer buffer) {
		if (!helperPrinter.mergedBuffers.containsKey(buffer) && !helperPrinter.mergedMalloc.containsKey(buffer) &&
			!helperPrinter.mergedFree.containsKey(buffer)) {

			// If the buffer is not involved in any merge operation
			// print a regular free
			return '''
				HeapMemMP_free(sharedHeap, «buffer.doSwitch», «buffer.size»*sizeof(«buffer.type»));
				cache_inv(&«buffer.doSwitch», 1);
			'''
		} else {

			// The buffer takes part in a merged malloc operation
			// print a merged free
			return '''
				«buffer.doSwitch» = («buffer.type»*) merged_free(sharedHeap, «buffer.doSwitch», «buffer.size»*sizeof(«buffer.type»));
				cache_wbInv(&«buffer.doSwitch», 1);
			'''
		}
	}

	override printSemaphoreDefinition(Semaphore semaphore) '''
		#pragma DATA_SECTION(«semaphore.name»,".MSMCSRAM");
		#pragma DATA_ALIGN(«semaphore.name», CACHE_LINE_SIZE);
		sem_t far «semaphore.name»;
	'''

	override printSemaphoreDeclaration(Semaphore semaphore) '''
		extern sem_t far «semaphore.name»; 
	'''

	override printSemaphore(Semaphore semaphore) {
		return '''&«semaphore.name»'''
	}

	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«IF communication.direction == Direction::SEND && communication.delimiter == Delimiter::START»
			«FOR subbuffer : communication.data.childrens»
				cache_wbInv(«subbuffer.doSwitch», «subbuffer.size»*sizeof(«subbuffer.type»));
			«ENDFOR»
		«ENDIF»
		«super.printSharedMemoryCommunication(communication).toString.replaceAll("(cache_.*?;)", "// $1")»
		«IF communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END»
			«FOR subbuffer : communication.data.childrens»
				«IF helperPrinter.mergedBuffers.containsKey(subbuffer) || helperPrinter.mergedFree.containsKey(subbuffer) ||
			helperPrinter.mergedMalloc.containsKey(subbuffer)»
					cache_inv(&«subbuffer.doSwitch», 1);
				«ENDIF»
				cache_inv(«subbuffer.doSwitch», «subbuffer.size»*sizeof(«subbuffer.type»));
			«ENDFOR»
		«ENDIF»	
	'''

}
