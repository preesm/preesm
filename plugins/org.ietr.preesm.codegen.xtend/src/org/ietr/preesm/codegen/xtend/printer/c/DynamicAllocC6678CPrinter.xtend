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

class DynamicAllocC6678CPrinter extends C6678CPrinter {
	new() {
		super()
		IGNORE_USELESS_MEMCPY = false
	}
	
	override printCoreBlockHeader(CoreBlock block) '''
		«super.printCoreBlockHeader(block)»
		#include <stdlib.h>
		#include <ti/ipc/SharedRegion.h>
		#include <ti/ipc/HeapMemMP.h>
		
		
	'''
	
	override printCoreInitBlockHeader(CallBlock callBlock) '''
	void «(callBlock.eContainer as CoreBlock).name.toLowerCase»(void){
		HeapMemMP_Handle sharedHeap;
		
		// Initialisation(s)
		communicationInit();
		sharedHeap = SharedRegion_getHeap(1);
		
	'''

	override printBufferDefinition(Buffer buffer) '''
		#pragma DATA_SECTION(«buffer.name»,".MSMCSRAM");
		#pragma DATA_ALIGN(«buffer.name», 64);
		«buffer.type» far *«buffer.name»; // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''
	
	override printSubBufferDefinition(SubBuffer buffer) '''
		«printBufferDefinition(buffer)»
	'''
	override printBufferDeclaration(Buffer buffer) '''
		extern  «buffer.type» far *«buffer.name»; // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''
	
	override printSubBufferDeclaration(SubBuffer buffer) '''
		«printBufferDeclaration(buffer)»
	'''
	
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
					«call.parameters.get(i).doSwitch» = («(call.parameters.get(i) as Buffer).type»*) HeapMemMP_alloc(sharedHeap,«(call.parameters.get(i) as Buffer).size»*sizeof(«(call.parameters.get(i) as Buffer).type»), 128);
					cache_wb(&«call.parameters.get(i).doSwitch», 1);
				«ENDIF»
			«ENDFOR»
		«ENDIF»
		«sequence»
		«/*Free input buffers*/
		IF call.parameters.size > 0»
			«FOR i : 0 .. call.parameters.size -1 »
				«IF call.parameterDirections.get(i) == PortDirection.INPUT»
					HeapMemMP_free(sharedHeap,«call.parameters.get(i).doSwitch», «(call.parameters.get(i) as Buffer).size»*sizeof(«(call.parameters.get(i) as Buffer).type»));
					cache_inv(&«call.parameters.get(i).doSwitch», 1);
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	'''

	override printFifoCall(FifoCall fifoCall) '''
		«IF fifoCall.operation == FifoOperation.INIT»
			«IF fifoCall.bodyBuffer != null»
				«fifoCall.bodyBuffer.doSwitch» = («fifoCall.bodyBuffer.type»*) HeapMemMP_alloc(sharedHeap, «fifoCall.bodyBuffer.size»*sizeof(«fifoCall.bodyBuffer.type»), 128);
				cache_wb(&«fifoCall.bodyBuffer.doSwitch», 1);
			«ENDIF»
		«ENDIF»
		«IF fifoCall.operation == FifoOperation.PUSH || fifoCall.operation == FifoOperation.INIT»
			«fifoCall.headBuffer.doSwitch» = («fifoCall.headBuffer.type»*) HeapMemMP_alloc(sharedHeap, «fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»), 128);
			cache_wb(&«fifoCall.headBuffer.doSwitch», 1);
		«ENDIF»
		«printCallWithMallocFree(fifoCall,super.printFifoCall(fifoCall))»
		«IF fifoCall.operation == FifoOperation.POP»
			HeapMemMP_free(sharedHeap, «fifoCall.headBuffer.doSwitch», «fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»));
			cache_inv(&«fifoCall.headBuffer.doSwitch», 1);
		«ENDIF»
	'''
	
	override printBroadcast(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printBroadcast(call))»
	'''	
	
	override printRoundBuffer(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printRoundBuffer(call))»
	'''	
	
	override printFork(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printFork(call))»
	'''
	
	override printJoin(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printJoin(call))»
	'''
	
	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«IF communication.direction == Direction::SEND && communication.delimiter == Delimiter::START»
			«FOR subbuffer : (communication.data as Buffer).childrens»
				cache_wbInv(«subbuffer.doSwitch», «subbuffer.size»*sizeof(«subbuffer.type»)); //<=============
			«ENDFOR»
		«ENDIF»
		«super.printSharedMemoryCommunication(communication).toString.replaceAll("(cache_.*?;)","// $1")»
		«IF communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END»
			«FOR subbuffer : (communication.data as Buffer).childrens»
				cache_inv(«subbuffer.doSwitch», «subbuffer.size»*sizeof(«subbuffer.type»)); //<=============
			«ENDFOR»
		«ENDIF»	
	'''
	
}