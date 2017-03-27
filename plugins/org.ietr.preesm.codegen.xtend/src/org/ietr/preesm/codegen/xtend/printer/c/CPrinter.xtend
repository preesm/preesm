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
package org.ietr.preesm.codegen.xtend.printer.c

import java.util.Date
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Communication
import org.ietr.preesm.codegen.xtend.model.codegen.Constant
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.Variable
import org.ietr.preesm.codegen.xtend.printer.DefaultPrinter
import org.ietr.preesm.codegen.xtend.task.CodegenException
import java.util.ArrayList
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
import org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer

/**
 * This printer is currently used to print C code only for GPP processors
 * supporting pthreads and shared memory communication.
 * 
 * @author kdesnos
 * @author mpelcat
 */
class CPrinter extends DefaultPrinter {

	/** 
	 * Temporary global var to ignore the automatic suppression of memcpy
	 * whose target and destination are identical. 
	 */
	protected var IGNORE_USELESS_MEMCPY = true
	
	override printCoreBlockHeader(CoreBlock block) '''
			/** 
			 * @file «block.name».c
			 * @generated by «this.class.simpleName»
			 * @date «new Date»
			 */
			
			#include "../include/«block.coreType».h"
			
			
	'''
	
	override printDefinitionsHeader(List<Variable> list) '''
	«IF !list.empty»
		// Core Global Definitions
		
	«ENDIF»
	'''
	
	override printBufferDefinition(Buffer buffer) '''
	«buffer.type» «buffer.name»[«buffer.size»]; // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''
	
	override printSubBufferDefinition(SubBuffer buffer) '''
	«buffer.type» *const «buffer.name» = («buffer.type»*) («var offset = 0»«
	{offset = buffer.offset
	 var b = buffer.container;
	 while(b instanceof SubBuffer){
	 	offset = offset + b.offset
	  	b = b.container
	  }
	 b}.name»+«offset»);  // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''
	
	override printDefinitionsFooter(List<Variable> list) '''
	«IF !list.empty»
	
	«ENDIF»
	'''
	
	override printDeclarationsHeader(List<Variable> list) '''
	// Core Global Declaration
	extern pthread_barrier_t iter_barrier;
	extern int stopThreads;
	
	'''
	
	override printBufferDeclaration(Buffer buffer) '''
	extern «printBufferDefinition(buffer)»
	'''
	
	override printSubBufferDeclaration(SubBuffer buffer) '''
	extern «buffer.type» *const «buffer.name»;  // «buffer.comment» size:= «buffer.size»*«buffer.type» defined in «buffer.creator.name»
	'''
	
	override printDeclarationsFooter(List<Variable> list) '''
	«IF !list.empty»
	
	«ENDIF»
	'''
	
	override printCoreInitBlockHeader(CallBlock callBlock) '''
	void *computationThread_«(callBlock.eContainer as CoreBlock).name»(void *arg){
		«IF !callBlock.codeElts.empty»
			// Initialisation(s)
			
		«ENDIF»
	'''
	
	override printCoreLoopBlockHeader(LoopBlock block2) '''
		
		«"\t"»// Begin the execution loop 
#ifdef LOOP_SIZE // Case of a finite loop
			int index;
			for(index=0;index<LOOP_SIZE;index++){
#else // Default case of an infinite loop
			while(1){
#endif
				pthread_barrier_wait(&iter_barrier);
				if(stopThreads){
					pthread_exit(NULL);
				}
				
	'''
	
	
	override printCoreLoopBlockFooter(LoopBlock block2) '''
		}
	}

	«IF block2.codeElts.empty»
	// This call may inform the compiler that the main loop of the thread does not call any function.
	void emptyLoop_«(block2.eContainer as CoreBlock).name»(){

	}
	«ENDIF»
	'''	
	override printFifoCall(FifoCall fifoCall) {
		var result = "fifo" + fifoCall.operation.toString.toLowerCase.toFirstUpper + "("

		if (fifoCall.operation != FifoOperation::INIT) {
			var buffer = fifoCall.parameters.head as Buffer
			result = result + '''«buffer.doSwitch», '''
		}

		result = result +
			'''«fifoCall.headBuffer.name», «fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»), '''
		result = result + '''«IF fifoCall.bodyBuffer != null»«fifoCall.bodyBuffer.name», «fifoCall.bodyBuffer.size»*sizeof(«fifoCall.
			bodyBuffer.type»)«ELSE»NULL, 0«ENDIF»);
			'''
	}
	
	override printFork(SpecialCall call) '''
	// Fork «call.name»«var input = call.inputBuffers.head»«var index = 0»
	{
		«FOR output : call.outputBuffers»
			«printMemcpy(output,0,input,index,output.size,output.type)»«{index=(output.size+index); ""}»
		«ENDFOR»
	}
	'''
	
	override printBroadcast(SpecialCall call) '''
	// Broadcast «call.name»«var input = call.inputBuffers.head»«var index = 0»
	{
	«FOR output : call.outputBuffers»«var outputIdx = 0»
		«FOR nbIter : 0..output.size/input.size+1/*Worst case is output.size exec of the loop */»
			«IF outputIdx < output.size /* Execute loop core until all output for current buffer are produced */»
				«val value = Math::min(output.size-outputIdx,input.size-index)»«
				printMemcpy(output,outputIdx,input,index,value,output.type)»«
				{index=(index+value)%input.size;outputIdx=(outputIdx+value); ""}»
			«ENDIF»
		«ENDFOR»
	«ENDFOR»
	}
	'''
	
    
	
	override printRoundBuffer(SpecialCall call) '''
	// RoundBuffer «call.name»«var output = call.outputBuffers.head»«var index = 0»«var inputIdx = 0»
	«/*Compute a list of useful memcpy (the one writing the outputed value) */
	var copiedInBuffers = {var totalSize = call.inputBuffers.fold(0)[res, buf | res+buf.size]
		 var lastInputs = new ArrayList
		 inputIdx = totalSize
		 var i = call.inputBuffers.size	- 1	 
		 while(totalSize-inputIdx < output.size){
		 	inputIdx = inputIdx - call.inputBuffers.get(i).size
		 	lastInputs.add(0,call.inputBuffers.get(i))
		 	i=i-1
		 }
		 inputIdx = inputIdx %  output.size
		 lastInputs
		 }»
	{
		«FOR input : copiedInBuffers»
			«FOR nbIter : 0..input.size/output.size+1/*Worst number the loop exec */»
				«IF inputIdx < input.size /* Execute loop core until all input for current buffer are produced */»
					«val value = Math::min(input.size-inputIdx,output.size-index)»«
					printMemcpy(output,index,input,inputIdx,value,input.type)»«
					{index=(index+value)%output.size;inputIdx=(inputIdx+value); ""}»
				«ENDIF»
			«ENDFOR»
		«ENDFOR»
	}
	'''
	
	override printJoin(SpecialCall call) '''
	// Join «call.name»«var output = call.outputBuffers.head»«var index = 0»
	{
		«FOR input : call.inputBuffers»
			«printMemcpy(output,index,input,0,input.size,input.type)»«{index=(input.size+index); ""}»
		«ENDFOR»
	}
	'''
	
	/**
	 * Print a memcpy call in the generated code. Unless
	 * {@link #IGNORE_USELESS_MEMCPY} is set to <code>true</code>, this method
	 * checks if the destination and the source of the memcpy are superimposed.
	 * In such case, the memcpy is useless and nothing is printed.
	 * 
	 * @param output
	 *            the destination {@link Buffer}
	 * @param outOffset
	 *            the offset in the destination {@link Buffer}
	 * @param input
	 *            the source {@link Buffer}
	 * @param inOffset
	 *            the offset in the source {@link Buffer}
	 * @param size
	 *            the amount of memory to copy
	 * @param type
	 *            the type of objects copied
	 * @return a {@link CharSequence} containing the memcpy call (if any)
	 */
	def printMemcpy(Buffer output, int outOffset, Buffer input, int inOffset, int size, String type) {

		// Retrieve the container buffer of the input and output as well
		// as their offset in this buffer
		var totalOffsetOut = outOffset*output.typeSize
		var bOutput = output
		while (bOutput instanceof SubBuffer) {
			totalOffsetOut = totalOffsetOut + bOutput.offset
			bOutput = bOutput.container
		}
		
		var totalOffsetIn = inOffset*input.typeSize
		var bInput = input
		while (bInput instanceof SubBuffer) {
			totalOffsetIn = totalOffsetIn + bInput.offset
			bInput = bInput.container
		}
		
		// If the Buffer and offsets are identical, or one buffer is null
		// there is nothing to print
		if((IGNORE_USELESS_MEMCPY && bInput == bOutput && totalOffsetIn == totalOffsetOut) ||
			output instanceof NullBuffer || input instanceof NullBuffer){
			''''''
		} else {
			'''memcpy(«output.doSwitch»+«outOffset», «input.doSwitch»+«inOffset», «size»*sizeof(«type»));'''
		}	
	}
	
	override printNullBuffer(NullBuffer Buffer) {
		printBuffer(Buffer)
	}
	
	override caseCommunication(Communication communication) {
		
		if(communication.nodes.forall[type == "SHARED_MEM"]) {
			super.caseCommunication(communication)
		} else {
			throw new CodegenException("Communication "+ communication.name +
				 " has at least one unsupported communication node"+ 
				 " for the " + this.class.name + " printer")
		}
	}
	
	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
	«/*Since everything is already in shared memory, communications are simple synchronizations here*/
	»«communication.direction.toString.toLowerCase»«communication.delimiter.toString.toLowerCase.toFirstUpper»(«
		IF communication.semaphore != null»&«
		communication.semaphore.name»/*ID*/«ENDIF»); // «communication.sendStart.coreContainer.name» > «communication.receiveStart.coreContainer.name»: «communication.data.doSwitch» 
	'''
	
	override printFunctionCall(FunctionCall functionCall) '''
	«functionCall.name»(«FOR param : functionCall.parameters SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
	'''
	
	override printConstant(Constant constant) '''«constant.value»«IF !constant.name.nullOrEmpty»/*«constant.name»*/«ENDIF»'''
	
	override printConstantString(ConstantString constant) '''"«constant.value»"'''

	override printBuffer(Buffer buffer) '''«buffer.name»'''
	
	override printSubBuffer(SubBuffer buffer) {printBuffer(buffer)}
	
	override printSemaphore(Semaphore semaphore) '''&«semaphore.name»'''
	
	override printSemaphoreDefinition(Semaphore semaphore) '''
	sem_t «semaphore.name»;
	'''
	
	override printSemaphoreDeclaration(Semaphore semaphore) '''
	extern sem_t «semaphore.name»; 
	'''

}
