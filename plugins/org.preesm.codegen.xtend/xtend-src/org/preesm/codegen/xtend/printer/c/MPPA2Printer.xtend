/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Julien Hascoet <jhascoet@kalray.eu> (2016 - 2017)
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
package org.preesm.codegen.xtend.printer.c

import java.util.Date
import java.util.List
import org.preesm.codegen.model.Buffer
import org.preesm.codegen.model.CallBlock
import org.preesm.codegen.model.Communication
import org.preesm.codegen.model.Constant
import org.preesm.codegen.model.ConstantString
import org.preesm.codegen.model.CoreBlock
import org.preesm.codegen.model.FifoCall
import org.preesm.codegen.model.FifoOperation
import org.preesm.codegen.model.FunctionCall
import org.preesm.codegen.model.LoopBlock
import org.preesm.codegen.model.NullBuffer
import org.preesm.codegen.model.SharedMemoryCommunication
import org.preesm.codegen.model.SpecialCall
import org.preesm.codegen.model.SubBuffer
import org.preesm.codegen.model.Variable
import org.preesm.commons.exceptions.PreesmRuntimeException

class MPPA2Printer extends CPrinter {

	new() {
		// do not generate a main file
		super(false)
	}

	/**
	 * Temporary global var to ignore the automatic suppression of memcpy
	 * whose target and destination are identical.
	 */
	protected boolean IGNORE_USELESS_MEMCPY = true

	override printCoreBlockHeader(CoreBlock block) '''
		/**
		 * @file «block.name».c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 */

		/* system includes */
		#include <stdlib.h>
		#include <stdio.h>
		#include <stdint.h>
		#include <mOS_vcore_u.h>
		#include <mppa_noc.h>
		#include <pthread.h>
		#include <semaphore.h>
		#include <HAL/hal/hal.h>

		/* user includes */
		#include "preesm.h"

		/* local Core variables */
		#ifdef PROFILE
		#define DUMP_MAX_TIME 128
		static uint64_t timestamp[BSP_NB_PE_MAX][DUMP_MAX_TIME]; /* 4KB of data */
		static int current_dump[BSP_NB_PE_MAX] = { 0 };
		#define getTimeProfile() if(current_dump[__k1_get_cpu_id()] < DUMP_MAX_TIME) \
								timestamp[__k1_get_cpu_id()][current_dump[__k1_get_cpu_id()]] = __k1_read_dsu_timestamp(); \
								current_dump[__k1_get_cpu_id()]++;
		#endif

	'''

	override printBufferDefinition(Buffer buffer) '''
	/* When using the Distributed Shared Memory buffers are in DDR (global memory) */
	«buffer.type» «buffer.name»[«buffer.size»]; // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''

	override printDefinitionsHeader(List<Variable> list) '''
	«IF !list.empty»
		// Core Global Definitions

	«ENDIF»
	'''

	override printSubBufferDefinition(SubBuffer buffer) '''
	«buffer.type» *const «buffer.name» = («buffer.type»*) («var offset = 0L»«
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
	extern pthread_barrier_t pthread_barrier;

	/* will link with it if the lflag is put, else it won't bother */
	extern void mppa_dsm_client_global_purge(void) __attribute__((weak));
	extern void mppa_dsm_client_global_fence(void) __attribute__((weak));

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
			int __iii __attribute__((unused));
			for(__iii=0;__iii<GRAPH_ITERATION;__iii++){

				pthread_barrier_wait(&pthread_barrier);
				#ifdef PROFILE
				getTimeProfile();
				#endif

	'''


	override printCoreLoopBlockFooter(LoopBlock block2) '''
		#ifdef VERBOSE
				if(__k1_get_cpu_id() == 0){
					printf("Cluster %d Graph Iteration %d / %d Done !\n", __k1_get_cluster_id(), __iii+1, GRAPH_ITERATION);
				}
		#endif
				/* commit local changes to the global memory */
				pthread_barrier_wait(&pthread_barrier); /* barrier to make sure all threads have commited data in smem */
				if(__k1_get_cpu_id() == 0){
					if(mppa_dsm_client_global_purge && mppa_dsm_client_global_fence){
						mppa_dsm_client_global_purge();
						mppa_dsm_client_global_fence();
					}
		#ifdef PROFILE
					int ii, jj;
					for(jj=0;jj<BSP_NB_PE_MAX;jj++){
						if(current_dump[jj] != 0){
							printf("C%d PE%d : Number of actors %d\n", __k1_get_cluster_id(), jj, current_dump[jj]);
							printf("\t# Profile %d Timestamp %lld\n", 0, (long long)timestamp[jj][0]);
							for(ii=1;ii<current_dump[jj];ii++){
								printf("\t# Profile %d Timestamp %lld Cycle %lld Time %.4f ms\n",
										ii,
										(long long)timestamp[jj][ii],
										(long long)timestamp[jj][ii]-(long long)timestamp[jj][ii-1],
										((float)timestamp[jj][ii]-(float)timestamp[jj][ii-1])/400000.0f /* chip freq */);
							}
						}
					}
		#endif
				}

			}
		}
	'''
	override printFifoCall(FifoCall fifoCall) {
		var result = "fifo" + fifoCall.operation.toString.toLowerCase.toFirstUpper + "("

		if (fifoCall.operation != FifoOperation::INIT) {
			var buffer = fifoCall.parameters.head as Buffer
			result = result + '''«buffer.doSwitch», '''
		}

		result = result +
			'''«fifoCall.headBuffer.name», «fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»), '''
		result = result + '''«IF fifoCall.bodyBuffer !== null»«fifoCall.bodyBuffer.name», «fifoCall.bodyBuffer.size»*sizeof(«fifoCall.
			bodyBuffer.type»)«ELSE»NULL, 0«ENDIF»);
			'''

		return result
	}

	override printFork(SpecialCall call) '''
	// Fork «call.name»«var input = call.inputBuffers.head»«var index = 0L»
	{
		«FOR output : call.outputBuffers»
			«printMemcpy(output,0,input,index,output.size,output.type)»«{index=(output.size+index); ""}»
		«ENDFOR»
	}
	'''

	override printBroadcast(SpecialCall call) '''
		«{
			super.printBroadcast(call)
		}»
	'''

	override printRoundBuffer(SpecialCall call) '''
		«{
			super.printRoundBuffer(call)
		}»
	'''

	override printJoin(SpecialCall call) '''
	// Join «call.name»«var output = call.outputBuffers.head»«var index = 0L»
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
	override printMemcpy(Buffer output, long outOffset, Buffer input, long inOffset, long size, String type) {

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
			return ''''''
		} else {
			return '''memcpy(«output.doSwitch»+«outOffset», «input.doSwitch»+«inOffset», «size»*sizeof(«type»));'''
		}
	}

	override printNullBuffer(NullBuffer Buffer) {
		return printBuffer(Buffer)
	}

	override caseCommunication(Communication communication) {

		if(communication.nodes.forall[type == "SHARED_MEM"]) {
			return super.caseCommunication(communication)
		} else {
			throw new PreesmRuntimeException("Communication "+ communication.name +
				 " has at least one unsupported communication node"+
				 " for the " + this.class.name + " printer")
		}
	}

	override printSharedMemoryCommunication(SharedMemoryCommunication communication) {
	  /*Since everything is already in shared memory, communications are simple synchronizations here*/
	  throw new PreesmRuntimeException("This method should be updated to comply with updates of the Kalray platform and the removal of Semaphore from code generation model.")
	}

	override printFunctionCall(FunctionCall functionCall) '''
	«functionCall.name»(«FOR param : functionCall.parameters SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
	#ifdef PROFILE
	getTimeProfile();
	#endif
	'''

	override printConstant(Constant constant) '''«constant.value»«IF !constant.name.nullOrEmpty»/*«constant.name»*/«ENDIF»'''

	override printConstantString(ConstantString constant) '''"«constant.value»"'''

	override printBuffer(Buffer buffer) '''«buffer.name»'''

	override printSubBuffer(SubBuffer buffer) {
		return printBuffer(buffer)
	}
}
