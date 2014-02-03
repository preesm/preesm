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
import java.util.HashSet
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
		«{// Empty the printedMerge
			printedMerged = new HashSet<Buffer>
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
		for(Block block : printerBlocks){
			for(CodeElt codeElt : (block as CoreBlock).initBlock.codeElts){
				if(codeElt instanceof FunctionCall){
					if((codeElt as FunctionCall).name == "sem_init"){
						var semaphore = (codeElt as FunctionCall).parameters.head
						var string = CodegenFactory.eINSTANCE.createConstantString;
						string.setCreator(block);
						string.setValue(semaphore.name)
						(codeElt as FunctionCall).addParameter(string,PortDirection.NONE)
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
		if(!helperPrinter.mergedBuffers.containsKey(buffer) && !printedMerged.contains(buffer)){
			if(helperPrinter.mergedMalloc.containsKey(buffer)){
				printedMerged.add(buffer)
			}
			'''
				extern «buffer.type» far *«buffer.name»; // «buffer.comment» size:= «buffer.size»*«buffer.type»
			'''
		} else if (helperPrinter.mergedBuffers.containsKey(buffer)){
			printBufferDeclaration(helperPrinter.mergedBuffers.get(buffer))
		} else {
			''''''
		}
	}
	
	override printSubBufferDeclaration(SubBuffer buffer) {
		printBufferDeclaration(buffer)
	}
	
	override printBuffer(Buffer buffer) {
		if (!helperPrinter.mergedBuffers.containsKey(buffer)) {
		/* No need to declare pointers for merged buffers */
			'''«buffer.name»'''
		} else 
		{ /* Print the merged buffer name */
			'''«helperPrinter.mergedBuffers.get(buffer).name»'''
		}
	}
	
	override printFunctionCall(FunctionCall functionCall) '''
		«printCallWithMallocFree(functionCall,super.printFunctionCall(functionCall))»
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
	def printCallWithMallocFree(Call call, CharSequence sequence) '''
		«/*Allocate output buffers */
		IF call.parameters.size > 0»
			«FOR i : 0 .. call.parameters.size -1 »
				«IF call.parameterDirections.get(i) == PortDirection.OUTPUT»
					«printMalloc(call.parameters.get(i) as Buffer)»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
		«sequence»
		«/*Free input buffers*/
		IF call.parameters.size > 0»
			«FOR i : 0 .. call.parameters.size -1 »
				«IF call.parameterDirections.get(i) == PortDirection.INPUT»
					«printFree(call.parameters.get(i) as Buffer)»
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	'''

	override printFifoCall(FifoCall fifoCall) '''
		«IF fifoCall.operation == FifoOperation.INIT»
			«IF fifoCall.bodyBuffer != null»
				«printMalloc(fifoCall.bodyBuffer)»
			«ENDIF»
		«ENDIF»
		«IF fifoCall.operation == FifoOperation.PUSH || fifoCall.operation == FifoOperation.INIT»
			«printMalloc(fifoCall.headBuffer)»
		«ENDIF»
		«printCallWithMallocFree(fifoCall,super.printFifoCall(fifoCall))»
		«IF fifoCall.operation == FifoOperation.POP»
			«printFree(fifoCall.headBuffer)»
		«ENDIF»
	'''
	
	override printBroadcast(SpecialCall call) {
		printCallWithMallocFree(call,super.printBroadcast(call))
	}
	
	override printRoundBuffer(SpecialCall call) {
		printCallWithMallocFree(call,super.printRoundBuffer(call))
	}	
	
	override printFork(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printFork(call))»
	'''
	
	override printJoin(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printJoin(call))»
	'''
	/**
	 * Methods used to print a Malloc.
	 * @param buffer 
	 * 			the {@link Buffer} that is allocated  
	 * @return the printed code as a {@link CharSequence}.
 	 */
	def printMalloc(Buffer buffer) {
		if(!helperPrinter.mergedBuffers.containsKey(buffer) && !helperPrinter.mergedMalloc.containsKey(buffer) && !helperPrinter.mergedFree.containsKey(buffer)) {
			// If the buffer is not involved in any merge operation
			// print a regular malloc
			'''
				«buffer.doSwitch» = («buffer.type»*) HeapMemMP_alloc(sharedHeap,«buffer.size»*sizeof(«buffer.type»), CACHE_LINE_SIZE);
				cache_wb(&«buffer.doSwitch», 1);
			'''
		} else if(helperPrinter.mergedMalloc.containsKey(buffer)) {
			// If the buffer is the allocated buffer of a merge
			var string = CodegenFactory.eINSTANCE.createConstantString;
						string.setCreator(buffer.creator);
						string.setValue(buffer.name)
			'''
				«buffer.doSwitch» = («buffer.type»*) merged_malloc(sharedHeap, (void**)&«buffer.doSwitch»,«buffer.size»*sizeof(«buffer.type»), «helperPrinter.mergedMalloc.get(buffer).size + 1»/*nbOfFree*/, «printConstantString(string)»,CACHE_LINE_SIZE);
				cache_wb(&«buffer.doSwitch», 1);
			'''
		}  else if(helperPrinter.mergedFree.containsKey(buffer) || (helperPrinter.mergedBuffers.containsKey(buffer) && helperPrinter.mergedFree.containsKey(helperPrinter.mergedBuffers.get(buffer)))){
			// The buffer is a merged free. It will be allocated several times.
			var mergedBuffer =  
				if(helperPrinter.mergedFree.containsKey(buffer)){
					buffer
				} else {
					helperPrinter.mergedBuffers.get(buffer)
				}
				var string = CodegenFactory.eINSTANCE.createConstantString;
						string.setCreator(buffer.creator);
				string.setValue(mergedBuffer.name)
			'''
				«mergedBuffer.doSwitch» = («mergedBuffer.type»*) multiple_malloc(sharedHeap, (void**)&«mergedBuffer.doSwitch»,«mergedBuffer.size»*sizeof(«mergedBuffer.type»), «helperPrinter.mergedFree.get(mergedBuffer).size + 1»/*nbOfFree*/, «helperPrinter.mergedFreeSemaphore.get(mergedBuffer).doSwitch», «printConstantString(string)», CACHE_LINE_SIZE);
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
	def printFree(Buffer buffer) {
		if(!helperPrinter.mergedBuffers.containsKey(buffer) && !helperPrinter.mergedMalloc.containsKey(buffer) && !helperPrinter.mergedFree.containsKey(buffer)) {
			// If the buffer is not involved in any merge operation
			// print a regular free
			'''
				HeapMemMP_free(sharedHeap, «buffer.doSwitch», «buffer.size»*sizeof(«buffer.type»));
				cache_inv(&«buffer.doSwitch», 1);
			'''
		} else {	
			// The buffer takes part in a merged malloc operation
			// print a merged free
			'''
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
		'''&«semaphore.name»'''
	}
	
	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«IF communication.direction == Direction::SEND && communication.delimiter == Delimiter::START»
			«FOR subbuffer : (communication.data as Buffer).childrens»
				cache_wbInv(«subbuffer.doSwitch», «subbuffer.size»*sizeof(«subbuffer.type»));
			«ENDFOR»
		«ENDIF»
		«super.printSharedMemoryCommunication(communication).toString.replaceAll("(cache_.*?;)","// $1")»
		«IF communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END»
			«FOR subbuffer : (communication.data as Buffer).childrens»
				«IF helperPrinter.mergedBuffers.containsKey(subbuffer) || helperPrinter.mergedFree.containsKey(subbuffer) || helperPrinter.mergedMalloc.containsKey(subbuffer)»
					cache_inv(&«subbuffer.doSwitch», 1);
				«ENDIF»
				cache_inv(«subbuffer.doSwitch», «subbuffer.size»*sizeof(«subbuffer.type»));
			«ENDFOR»
		«ENDIF»	
	'''
	
}