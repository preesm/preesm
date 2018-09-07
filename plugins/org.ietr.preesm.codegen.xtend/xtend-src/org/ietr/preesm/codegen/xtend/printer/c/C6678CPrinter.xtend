/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2017)
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

import java.util.Date
import java.util.LinkedHashSet
import java.util.List
import org.ietr.preesm.codegen.model.codegen.Buffer
import org.ietr.preesm.codegen.model.codegen.Call
import org.ietr.preesm.codegen.model.codegen.CallBlock
import org.ietr.preesm.codegen.model.codegen.CoreBlock
import org.ietr.preesm.codegen.model.codegen.Delimiter
import org.ietr.preesm.codegen.model.codegen.Direction
import org.ietr.preesm.codegen.model.codegen.FifoCall
import org.ietr.preesm.codegen.model.codegen.FifoOperation
import org.ietr.preesm.codegen.model.codegen.FunctionCall
import org.ietr.preesm.codegen.model.codegen.LoopBlock
import org.ietr.preesm.codegen.model.codegen.NullBuffer
import org.ietr.preesm.codegen.model.codegen.PortDirection
import org.ietr.preesm.codegen.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.model.codegen.SpecialCall
import org.ietr.preesm.codegen.model.codegen.Variable

class C6678CPrinter extends CPrinter {

	new() {
		// do not generate a main file
		super(false)
	}

	/**
	 * Set of CharSequence used to avoid calling the same cache operation
	 * multiple times in a broadcast or roundbuffer call.
	 */
	var currentOperationMemcpy = new LinkedHashSet<CharSequence>();

	override printCoreBlockHeader(CoreBlock block) '''
		/**
		 * @file «block.name».c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 */


		#include "cores.h"
		#include "utils.h"
		#include "communication.h"
		#include "fifo.h"
		#include "cache.h"


	'''

	override printBroadcast(SpecialCall call) '''
		«{
			currentOperationMemcpy.clear
			super.printBroadcast(call)
		}»
		«printCacheCoherency(call)»
	'''

	override printBufferDefinition(Buffer buffer) '''
	// Won't work if the shared memory is >= 512 MB
	#pragma DATA_SECTION(«buffer.name», ".mySharedMem")
	«super.printBufferDefinition(buffer)»
	'''

	override printBufferDeclaration(Buffer buffer) '''
		extern «super.printBufferDefinition(buffer)»
	'''

	override printDeclarationsHeader(List<Variable> list) '''
	// Core Global Declaration

	'''
	override printCoreInitBlockHeader(CallBlock callBlock) '''
	void «(callBlock.eContainer as CoreBlock).name.toLowerCase»(void){
		// Initialisation(s)
		communicationInit();

	'''

	override printCoreLoopBlockHeader(LoopBlock block2) '''

		«"\t"»// Begin the execution loop
			while(1){
				busy_barrier();

	'''

	override printFifoCall(FifoCall fifoCall) '''
		«IF fifoCall.operation == FifoOperation::POP»
			cache_inv(«fifoCall.headBuffer.doSwitch», «fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»));
			«IF fifoCall.bodyBuffer !== null»
				cache_inv(«fifoCall.bodyBuffer.doSwitch», «fifoCall.bodyBuffer.size»*sizeof(«fifoCall.bodyBuffer.type»));
			«ENDIF»
		«ENDIF»
		«super.printFifoCall(fifoCall)»
		«IF fifoCall.operation == FifoOperation::PUSH || fifoCall.operation == FifoOperation::INIT»
			cache_wbInv(«fifoCall.headBuffer.doSwitch», «fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»));
			«IF fifoCall.bodyBuffer !== null»
				cache_wbInv(«fifoCall.bodyBuffer.doSwitch», «fifoCall.bodyBuffer.size»*sizeof(«fifoCall.bodyBuffer.type»));
			«ENDIF»
		«ENDIF»
		«printCacheCoherency(fifoCall)»
	'''

	override printFork(SpecialCall call) '''
		«super.printFork(call)»
		«printCacheCoherency(call)»
	'''

	override printFunctionCall(FunctionCall functionCall) '''
		«super.printFunctionCall(functionCall)»
		«printCacheCoherency(functionCall)»
	'''

	override printJoin(SpecialCall call) '''
		«super.printJoin(call)»
		«printCacheCoherency(call)»
	'''

	/**
	 * This methods prints a call to the cache invalidate method for each
	 * {@link PortDirection#INPUT input} {@link Buffer} of the given
	 * {@link Call}. If the input port is a {@link NullBuffer}, nothing is
	 * printed.
	 *
	 * @param call
	 *            the {@link Call} whose {@link PortDirection#INPUT input}
	 *            {@link Buffer buffers} must be invalidated.
	 * @return the corresponding code.
	 */
	def String printCacheCoherency(Call call)'''
		«IF call.parameters.size > 0»
			«FOR i :  0 .. call.parameters.size - 1»
				«IF call.parameterDirections.get(i) == PortDirection.INPUT && !((call.parameters.get(i) as Buffer).local)  && !(call.parameters.get(i) instanceof NullBuffer)»
					«IF (call.parameters.get(i) as Buffer).mergedRange !== null»
						«FOR range : (call.parameters.get(i) as Buffer).mergedRange»
							cache_wb(((char*)«call.parameters.get(i).doSwitch») + «range.start», «range.length»);
						«ENDFOR»
					«ENDIF»
					cache_inv(«call.parameters.get(i).doSwitch», «(call.parameters.get(i) as Buffer).size»*sizeof(«call.parameters.get(i).type»));
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	'''

	override printMemcpy(Buffer output, int outOffset, Buffer input, int inOffset, int size, String type) {

		// Cast pointers into void pointers for non-aligned memory access
		var result = super.printMemcpy(output, outOffset, input, inOffset, size, type).toString;
		var regex = "(memcpy\\()(.*?)[,](.*?)[,](.*?[;])"
		result = result.replaceAll(regex, "$1(void*)($2),(void*)($3),$4")

		// Also if nothing was printed (i.e. if source and destination are identical
		// Then a writeback is needed for the output to make sure that when a consumer
		// finish its execution and invalidate the buffer, if another consumer of the same
		// merged buffer is executed on the same core, its data will still be valid
		// Unless the buffer is in a local memory
		if (result.empty && !input.local) {
			if (!(input instanceof NullBuffer)) {
				result = '''cache_wb(«input.doSwitch», «input.size»*sizeof(«input.type»));'''
			} else {

				// The input buffer is null write back the output instead
				// since if the input is null, it means it has been exploded
				// into the output by the memory scripts.
				result = '''cache_wb(«output.doSwitch», «output.size»*sizeof(«output.type»));'''
			}
			if (!currentOperationMemcpy.contains(result)) {
				currentOperationMemcpy.add(result)
			} else {
				result = ''''''
			}
		}

		return result;
	}

	override printRoundBuffer(SpecialCall call) '''
		«{
			currentOperationMemcpy.clear
			super.printRoundBuffer(call)
		}»
		«printCacheCoherency(call)»
	'''

	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«IF communication.direction == Direction::SEND && communication.delimiter == Delimiter::START»
			«IF !(communication.data instanceof NullBuffer)»
				cache_wbInv(«communication.data.doSwitch», «communication.data.size»*sizeof(«communication.data.type»));
			«ENDIF»
		«ENDIF»
		«/** TODO: replace with super.printSharedMemoryCommunication() */
		communication.direction.toString.toLowerCase»«communication.delimiter.toString.toLowerCase.toFirstUpper»(«IF (communication.
			direction == Direction::SEND && communication.delimiter == Delimiter::START) ||
			(communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END)»«{
			var coreName = if (communication.direction == Direction::SEND) {
					communication.receiveStart.coreContainer.name
				} else {
					communication.sendStart.coreContainer.name
				}
			coreName.charAt(coreName.length - 1)
		}»«ENDIF»); // «communication.sendStart.coreContainer.name» > «communication.receiveStart.coreContainer.name»: «communication.
			data.doSwitch»
		«IF communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END»
			«IF !(communication.data instanceof NullBuffer)»
				cache_inv(«communication.data.doSwitch», «communication.data.size»*sizeof(«communication.data.type»));
			«ENDIF»
		«ENDIF»
	'''
}
