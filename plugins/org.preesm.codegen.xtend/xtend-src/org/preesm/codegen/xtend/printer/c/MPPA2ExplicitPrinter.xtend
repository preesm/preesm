/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2016 - 2017)
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
package org.preesm.codegen.xtend.printer.c

import java.io.IOException
import java.io.InputStreamReader
import java.io.StringWriter
import java.net.URL
import java.util.Arrays
import java.util.Collection
import java.util.Date
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.List
import java.util.Set
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.eclipse.emf.common.util.EList
import org.preesm.codegen.model.Block
import org.preesm.codegen.model.Buffer
import org.preesm.codegen.model.CallBlock
import org.preesm.codegen.model.CodeElt
import org.preesm.codegen.model.Communication
import org.preesm.codegen.model.Constant
import org.preesm.codegen.model.ConstantString
import org.preesm.codegen.model.CoreBlock
import org.preesm.codegen.model.Delimiter
import org.preesm.codegen.model.Direction
import org.preesm.codegen.model.DistributedMemoryCommunication
import org.preesm.codegen.model.FifoCall
import org.preesm.codegen.model.FifoOperation
import org.preesm.codegen.model.FiniteLoopBlock
import org.preesm.codegen.model.FunctionCall
import org.preesm.codegen.model.LoopBlock
import org.preesm.codegen.model.NullBuffer
import org.preesm.codegen.model.PapifyFunctionCall
import org.preesm.codegen.model.PapifyType
import org.preesm.codegen.model.SharedMemoryCommunication
import org.preesm.codegen.model.SpecialCall
import org.preesm.codegen.model.SubBuffer
import org.preesm.codegen.model.Variable
import org.preesm.commons.exceptions.PreesmRuntimeException
import org.preesm.commons.files.PreesmResourcesHelper
import org.preesm.model.pisdf.util.CHeaderUsedLocator
import org.preesm.codegen.printer.DefaultPrinter
import org.preesm.codegen.model.PapifyAction
import org.preesm.codegen.model.BufferIterator
import org.preesm.codegen.model.IntVar
import org.preesm.codegen.model.DataTransferAction
import org.preesm.codegen.model.RegisterSetUpAction

class MPPA2ExplicitPrinter extends DefaultPrinter {

	/**
	 * Set to true if a main file should be generated. Set at object creation in constructor.
	 */
	final boolean generateMainFile;

	def boolean generateMainFile() {
		return this.generateMainFile;
	}

	new() {
		// generate a main file by default
		this(true);
	}

	new(boolean generateMainFile) {
		this.generateMainFile = generateMainFile;
	}
	/**
	 * Preprocessing configuration
	 */
	protected int numClusters = 0;
	protected int clusterToSync = 0;
	protected int io_used = 0;
	protected int sharedOnly = 1;
	protected int distributedOnly = 1;
	protected int usingPapify = 0;
	protected int usingClustering = 0;
	protected String peName = "";

	/**
	 * Temporary global var to ignore the automatic suppression of memcpy
	 * whose target and destination are identical.
	 */
	protected boolean IGNORE_USELESS_MEMCPY = true

	protected String local_buffer = "local_buffer"

	protected boolean IS_HIERARCHICAL = false

	protected String scratch_pad_buffer = ""

	protected long local_buffer_size = 0

	override printCoreBlockHeader(CoreBlock block) {

	this.peName = block.name;

	var String printing = '''
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
		#include <mppa_rpc.h>
		#include <mppa_async.h>
		#include <pthread.h>
		#include <semaphore.h>
		#include <assert.h>
		#ifndef __nodeos__
		#include <utask.h>
		#endif

		/* user includes */
		#include "preesm_gen.h"

		extern void *__wrap_memset(void *s, int c, size_t n);
		extern void *__wrap_memcpy(void *dest, const void *src, size_t n);

		#define memset __wrap_memset
		#define memcpy __wrap_memcpy

		«IF (this.distributedOnly == 0)»
			extern mppa_async_segment_t shared_segment;
		«ENDIF»
		«IF (this.sharedOnly == 0 && this.distributedOnly == 1)»
			extern mppa_async_segment_t distributed_segment[PREESM_NB_CLUSTERS + PREESM_IO_USED];
		«ENDIF»


		/* Scratchpad buffer ptr (will be malloced) */
		char *local_buffer = NULL;
		/* Scratchpad buffer size */
		int local_buffer_size = 0;

	'''
	return printing;
	}

	override printBufferDefinition(Buffer buffer) {
		var result = '''
		«IF buffer.name == "Shared"»
		//#define Shared ((char*)0x10000000ULL) 	/* Shared buffer in DDR */
		«ELSE»
		«buffer.type» «buffer.name»[«buffer.size»] __attribute__ ((aligned(64))); // «buffer.comment» size:= «buffer.size»*«buffer.type» aligned on data cache line
		int local_memory_size = «buffer.size»;
		«ENDIF»
		'''
		return result;
	}

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

	override printFiniteLoopBlockHeader(FiniteLoopBlock block2) '''
	«{
	 	IS_HIERARCHICAL = true
	"\t"}»
	{ // Begin the hierarchical actor
		«{
		var gets = ""
		var local_offset = 0L;
			/* go through eventual out param first because of foot FiniteLoopBlock */
			for(param : block2.outBuffers){
				var b = param.container;
				var offset = param.offset;
				while(b instanceof SubBuffer){
					offset += b.offset;
					b = b.container;
				}
				/* put out buffer here */
				if(b.name == "Shared"){
					gets += "void *" + param.name + " = local_buffer+" + local_offset +";\n";
					local_offset += param.typeSize * param.size;
				}
			}
			for(param : block2.inBuffers){
				var b = param.container;
				var offset = param.offset;
				while(b instanceof SubBuffer){
					offset += b.offset;
					b = b.container;
				}
				//System.out.print("===> " + b.name + "\n");
				if(b.name == "Shared"){
					gets += "void *" + param.name + " = local_buffer+" + local_offset +";\n";
					gets += "	{\n"
					gets += "		if(mppa_async_get(local_buffer + " + local_offset + ",";
					gets += " &shared_segment,";
					//gets += "	" + b.name + " + " + offset + ",\n";
					gets += " /* Shared + */ " + offset + ",";
					gets += " " + param.typeSize * param.size + ",";
					gets += " NULL) != 0){\n";
					gets += "			assert(0 && \"mppa_async_get\\n\");\n";
					gets += "		}\n";
					gets += "	}\n"
					local_offset += param.typeSize * param.size;
					//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
				}
			}

			gets += "int " + block2.iter.name + ";\n"
			if(block2.nbIter > 1 && this.sharedOnly == 0){
				gets += "#pragma omp parallel for private(" + block2.iter.name + ")\n"
			}
			gets += "for(" + block2.iter.name + "=0;" + block2.iter.name +"<" + block2.nbIter + ";" + block2.iter.name + "++){\n"

			if(local_offset > local_buffer_size)
				local_buffer_size = local_offset
	gets}»

	'''

	override printFiniteLoopBlockFooter(FiniteLoopBlock block2) '''

			}// End the for loop
		«{
				var puts = ""
				var local_offset = 0L;
				for(param : block2.outBuffers){
					var b = param.container
					var offset = param.offset
					while(b instanceof SubBuffer){
						offset += b.offset;
						b = b.container;
						//System.out.print("Running through all buffer " + b.name + "\n");
					}
					//System.out.print("===> " + b.name + "\n");
					if(b.name == "Shared"){
						puts += "	{\n"
						puts += "		if(mppa_async_put(local_buffer + " + local_offset + ",";
						puts += " &shared_segment,";
						puts += " /* Shared + */" + offset + ",";
						puts += " " + param.typeSize * param.size + ",";
						puts += " NULL) != 0){\n";
						puts += "			assert(0 && \"mppa_async_put\\n\");\n";
						puts += "		}\n";
						puts += "	}\n"
						local_offset += param.typeSize * param.size;
						//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}
				}
				if(local_offset > local_buffer_size)
					local_buffer_size = local_offset
			puts}»
		«{
			 	IS_HIERARCHICAL = false
			""}»
		}
	'''
	override printPapifyFunctionCall(PapifyFunctionCall papifyFunctionCall) {
		if(!(papifyFunctionCall.papifyType.equals(PapifyType.CONFIGACTOR))){
			papifyFunctionCall.parameters.remove(papifyFunctionCall.parameters.size-1);
		}
		var printing = '''
			«IF papifyFunctionCall.opening == true»
				#ifdef _PREESM_PAPIFY_MONITOR
			«ENDIF»
			«IF !(papifyFunctionCall.papifyType.equals(PapifyType.CONFIGACTOR))»
				«IF (papifyFunctionCall.papifyType.equals(PapifyType.CONFIGPE)) && this.usingClustering == 1»
					char namingArray[50];
					for(int i = 0; i < PREESM_NB_CORES_CC; i++){
						snprintf(namingArray, 50, "«this.peName»-PE%d", i);
						«papifyFunctionCall.name»(namingArray, «papifyFunctionCall.parameters.get(1).doSwitch», i); // «papifyFunctionCall.actorName»
					}
				«ELSE»
				«papifyFunctionCall.name»(«FOR param : papifyFunctionCall.parameters SEPARATOR ', '»«param.doSwitch»«ENDFOR», __k1_get_cpu_id()/*PE_id*/); // «papifyFunctionCall.actorName»
				«ENDIF»
			«ELSE»
			«papifyFunctionCall.name»(«FOR param : papifyFunctionCall.parameters SEPARATOR ', '»«param.doSwitch»«ENDFOR»); // «papifyFunctionCall.actorName»
			«ENDIF»
			«IF papifyFunctionCall.closing == true»
				#endif
			«ENDIF»
			'''
			return printing;
	}

	override printPapifyActionDefinition(PapifyAction action) '''
	«IF action.opening == true»
		#ifdef _PREESM_PAPIFY_MONITOR
	«ENDIF»
	«action.type» «action.name»; // «action.comment»
	«IF action.closing == true»
		#endif
	«ENDIF»
	'''
	override printPapifyActionParam(PapifyAction action) '''&«action.name»'''
	
	override printFunctionCall(FunctionCall functionCall) '''
	«{
		var gets = ""
		var local_offset = 0L;
		if(IS_HIERARCHICAL == false){
			//if(this.sharedOnly == 1){
				gets += "{\n"
			//}
			for(param : functionCall.parameters){

				if(param instanceof SubBuffer){
					var port = functionCall.parameterDirections.get(functionCall.parameters.indexOf(param))
					var b = param.container;
					var offset = param.offset;
					while(b instanceof SubBuffer){
						offset += b.offset;
						b = b.container;
						//System.out.print("Running through all buffer " + b.name + "\n");
					}
					//System.out.print("===> " + b.name + "\n");
					if(b.name == "Shared"){
						gets += "	void *" + param.name + " = local_buffer+" + local_offset +";\n";
						if(port.getName == "INPUT"){ /* we get data from DDR -> cluster only when INPUT */
							gets += "	if(mppa_async_get(local_buffer+" + local_offset + ", &shared_segment, /* Shared + */ " + offset + ", " + param.typeSize * param.size + ", NULL) != 0){\n";
							gets += "		assert(0 && \"mppa_async_get\\n\");\n";
							gets += "	}\n";
						}
						local_offset += param.typeSize * param.size;
						//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}
					/*else{
						System.out.print("A==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}*/
				}
			}
			gets += "\t"
		}else{
			gets += " /* gets are normaly generated before */ \n"
		}
		if(local_offset > local_buffer_size)
			local_buffer_size = local_offset
	gets}»
		«functionCall.name»(«FOR param : functionCall.parameters SEPARATOR ', '»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
	«{
		var puts = ""
		var local_offset = 0L;
		if(IS_HIERARCHICAL == false){
			for(param : functionCall.parameters){
				if(param instanceof SubBuffer){
					var port = functionCall.parameterDirections.get(functionCall.parameters.indexOf(param))
					var b = param.container
					var offset = param.offset
					while(b instanceof SubBuffer){
						offset += b.offset;
						b = b.container;
						//System.out.print("Running through all buffer " + b.name + "\n");
					}
					//System.out.print("===> " + b.name + "\n");
					if(b.name == "Shared"){
						if(port.getName == "OUTPUT"){ /* we put data from cluster -> DDR only when OUTPUT */
							puts += "	if(mppa_async_put(local_buffer+" + local_offset + ", &shared_segment, /* Shared + */ " + offset + ", " + param.typeSize * param.size + ", NULL) != 0){\n";
							puts += "		assert(0 && \"mppa_async_put\\n\");\n";
							puts += "	}\n";
						}
						local_offset += param.typeSize * param.size;
						//System.out.print("==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}
					/*else{
						System.out.print("B==> " + b.name + " " + param.name + " size " + param.size + " port_name "+ port.getName + "\n");
					}*/
				}
			}
			//if(this.sharedOnly == 1){
			 	puts += "}\n"
			//}
		}else{
			puts += " /* puts are normaly generated before */ \n"
		}
		if(local_offset > local_buffer_size)
			local_buffer_size = local_offset
	puts}»
	'''

	override printDefinitionsFooter(List<Variable> list) '''
	«IF !list.empty»

	«ENDIF»
	'''

	override printDeclarationsHeader(List<Variable> list) '''
	// Core Global Declaration
	extern pthread_barrier_t iter_barrier;
	extern int preesmStopThreads;

	'''

	override printBufferDeclaration(Buffer buffer) '''
	extern «printBufferDefinition(buffer)»
	'''

	override printSubBufferDeclaration(SubBuffer buffer) '''
			«buffer.type» *const «buffer.name» = («buffer.type»*) («var offset = 0L»«
			{offset = buffer.offset
			 var b = buffer.container;
			 while(b instanceof SubBuffer){
			 	offset = offset + b.offset
			  	b = b.container
			  }
			 b}.name»+«offset»);  // «buffer.comment» size:= «buffer.size»*«buffer.type»
			'''

	override printDeclarationsFooter(List<Variable> list) '''
	«IF !list.empty»

	«ENDIF»
	'''

	override printCoreInitBlockHeader(CallBlock callBlock) '''
	void *computationTask_«(callBlock.eContainer as CoreBlock).name»(void *arg __attribute__((__unused__))){
«/*	#ifdef PREESM_VERBOSE
		//printf("Cluster %d runs on task «(callBlock.eContainer as CoreBlock).name»\n", __k1_get_cluster_id());
	#endif*/»
		«IF !callBlock.codeElts.empty»
			// Initialisation(s)

		«ENDIF»
	'''

	override printCoreLoopBlockHeader(LoopBlock block2) '''

		«"\t"»// Begin the execution loop
		#ifdef PREESM_LOOP_SIZE // Case of a finite loop
			int __iii;
			for(__iii=0;__iii<PREESM_LOOP_SIZE;__iii++){
		#else // Default case of an infinite loop
			while(!preesmStopThreads){
		#endif

				//pthread_barrier_wait(&iter_barrier);«"\n\n"»
	'''


	override printCoreLoopBlockFooter(LoopBlock block2) '''

				/* commit local changes to the global memory */
				//pthread_barrier_wait(&iter_barrier); /* barrier to make sure all threads have commited data in smem */
				«IF (this.io_used == 1)»
					if(__k1_get_cluster_id() == «clusterToSync»){
						mppa_rpc_barrier(1,2);
					}
				«ENDIF»
				mppa_rpc_barrier_all();
				«IF (this.io_used == 1)»
					if(__k1_get_cluster_id() == «clusterToSync»){
						mppa_rpc_barrier(1,2);
					}
				«ENDIF»
			}
			return NULL;
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
	def printMemcpy(Buffer output, long outOffset, Buffer input, long inOffset, long size, String type) {

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
			return ""
		} else {
			return '''memcpy(«output.doSwitch»+«outOffset», «input.doSwitch»+«inOffset», «size»*sizeof(«type»)); '''
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

	override CharSequence caseDistributedMemoryCommunication(DistributedMemoryCommunication communication) {

		return printDistributedMemoryCommunication(communication);

	}
	override CharSequence printDistributedMemoryCommunication(DistributedMemoryCommunication communication) {
		var boolean receiveStartOrNot = false; // false is RE, SS, SE --- True is RS
		if(communication.direction.toString.toLowerCase.equals("receive") && communication.delimiter.toString.toLowerCase.equals("start")){
			receiveStartOrNot = true;
		}
		var int receiverCoreID;
		if(communication.direction == Direction::SEND){
			receiverCoreID = communication.receiveStart.coreContainer.coreID
		} else{
			receiverCoreID = communication.sendStart.coreContainer.coreID
		}
		var String printing = ""
		if(!receiveStartOrNot){
			printing = '''
				«communication.direction.toString.toLowerCase»Distributed«communication.delimiter.toString.toLowerCase.toFirstUpper»(/*Remote PE id*/«
				receiverCoreID»);  // «communication.sendStart.coreContainer.name» > «
				communication.receiveStart.coreContainer.name»: «communication.data.doSwitch»
		'''
		} else{
			var SubBuffer sendBuffer = (communication.sendStart.data as SubBuffer);
			var SubBuffer receiveBuffer = (communication.receiveStart.data as SubBuffer);
			printing = '''
				«communication.direction.toString.toLowerCase»Distributed«communication.delimiter.toString.toLowerCase.toFirstUpper
				»(/*Remote PE id*/ «receiverCoreID
				»,/*Remote offset*/ «sendBuffer.offset
				»,/*Local address*/ «receiveBuffer.name
				»,/*Transmission Size*/ «receiveBuffer.size
				»);  // «communication.sendStart.coreContainer.name» > «
				communication.receiveStart.coreContainer.name»: «communication.data.doSwitch»
		'''
		}

	return printing;
	}

	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
		«communication.direction.toString.toLowerCase»«communication.delimiter.toString.toLowerCase.toFirstUpper»(«IF (communication.
			direction == Direction::SEND && communication.delimiter == Delimiter::START) ||
			(communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END)»«{
			var coreID = if (communication.direction == Direction::SEND) {
					communication.receiveStart.coreContainer.coreID
				} else {
					communication.sendStart.coreContainer.coreID
				}
			var ret = coreID
			ret
		}»«ENDIF»); // «communication.sendStart.coreContainer.name» > «communication.receiveStart.coreContainer.name»: «communication.
			data.doSwitch»
	'''

	override printConstant(Constant constant) '''«constant.value»«IF !constant.name.nullOrEmpty»/*«constant.name»*/«ENDIF»'''

	override printConstantString(ConstantString constant) '''"«constant.value»"'''

	override printBuffer(Buffer buffer) '''«buffer.name»'''

	override printSubBuffer(SubBuffer buffer) {
		return printBuffer(buffer)
	}
	override printBufferIterator(BufferIterator bufferIterator) '''«bufferIterator.name» + «printIntVar(bufferIterator.iter)» * «bufferIterator.iterSize»'''

	override printBufferIteratorDeclaration(BufferIterator bufferIterator) ''''''

	override printBufferIteratorDefinition(BufferIterator bufferIterator) ''''''

	override printIntVar(IntVar intVar) '''«intVar.name»'''

	override printIntVarDeclaration(IntVar intVar) '''
	extern int «intVar.name»;
	'''
	
	override printIntVarDefinition(IntVar intVar) '''
	int «intVar.name»;
	'''
	
	override printDataTansfer(DataTransferAction action) ''''''

	override printRegisterSetUp(RegisterSetUpAction action) ''''''
	
	def CharSequence generatePreesmHeader() {
	    // 0- without the following class loader initialization, I get the following exception when running as Eclipse
	    // plugin:
	    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
	    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
	    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
	    val ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
	    Thread.currentThread().setContextClassLoader(CPrinter.classLoader);

	    // 1- init engine
	    val VelocityEngine engine = new VelocityEngine();
	    engine.init();

	    // 2- init context
	    val VelocityContext context = new VelocityContext();
	    val findAllCHeaderFileNamesUsed = CHeaderUsedLocator.findAllCHeaderFileNamesUsed(getEngine.algo.referencePiMMGraph)
	    context.put("USER_INCLUDES", findAllCHeaderFileNamesUsed.map["#include \""+ it +"\""].join("\n"));

		var String constants = "#define NB_DESIGN_ELTS "+getEngine.archi.componentInstances.size+"\n";
		constants = constants.concat("#define PREESM_NB_CLUSTERS "+numClusters+"\n");
		constants = constants.concat("#define PREESM_IO_USED " + io_used + " \n");
		if(this.usingPapify == 1){
			constants = constants.concat("\n\n#ifdef _PREESM_PAPIFY_MONITOR\n#include \"eventLib.h\"\n#endif");
		}
	    context.put("CONSTANTS", constants);

	    // 3- init template reader
	    val String templateLocalPath = "templates/mppa2Explicit/preesm_gen.h";
	    val URL mainTemplate = PreesmResourcesHelper.instance.resolve(templateLocalPath, this.class);
	    var InputStreamReader reader = null;
	    try {
	      reader = new InputStreamReader(mainTemplate.openStream());
	    } catch (IOException e) {
	      throw new PreesmRuntimeException("Could not locate main template [" + templateLocalPath + "].", e);
	    }

	    // 4- init output writer
	    val StringWriter writer = new StringWriter();

	    engine.evaluate(context, writer, "org.apache.velocity", reader);

	    // 99- set back default class loader
	    Thread.currentThread().setContextClassLoader(oldContextClassLoader);

	    return writer.getBuffer().toString();
	}
	override generateStandardLibFiles() {
		val result = new LinkedHashMap<String, CharSequence>()
		val String stdFilesFolder = "/stdfiles/mppa2Explicit/"
		val files = Arrays.asList(#[
						"communication.c",
						"communication.h",
						"dump.c",
						"dump.h",
						"fifo.c",
						"fifo.h",
						"memory.c",
						"memory.h",
						"clock.c",
						"clock.h"
					]);
		files.forEach[it | try {
			result.put(it, PreesmResourcesHelper.instance.read(stdFilesFolder + it, this.class))
		} catch (IOException exc) {
			throw new PreesmRuntimeException("Could not generated content for " + it, exc)
		}]
		result.put("preesm_gen.h",generatePreesmHeader())
		return result
	}
	override createSecondaryFiles(List<Block> printerBlocks, Collection<Block> allBlocks) {
		val result = new LinkedHashMap<String, CharSequence>()
		if (generateMainFile()) {
			result.put("cluster_main.c", printMainCluster(printerBlocks));
			if(io_used == 0){
				result.put("io_main.c", printMainIO(printerBlocks));
			}
			result.put("host_main.c", printMainHost(printerBlocks));
		}
		return result
	}
	def String printMainCluster(List<Block> printerBlocks) '''
		/**
		 * @file cluster_main.c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 *
		 */
		/*
		 * Copyright (C) 2016 Kalray SA.
		 *
		 * All rights reserved.
		 */
		#include "mOS_common_types_c.h"
		#include "mOS_constants_c.h"
		#include "mOS_vcore_u.h"
		#include "mOS_segment_manager_u.h"
		#include "stdlib.h"
		#include "stdio.h"
		#include "vbsp.h"
		#include <mppa_rpc.h>
		#include <mppa_remote.h>
		#include <mppa_async.h>
		#include "HAL/hal/hal_ext.h"
		#include <math.h>
		#include <stdlib.h>
		#include <assert.h>

		#ifdef __nodeos__
		#define CONFIGURE_DEFAULT_TASK_STACK_SIZE (1U<<12)
		#define CONFIGURE_AMP_MAIN_STACK_SIZE (1U<<12)
		#include <mppa/osconfig.h>
		#include <omp.h>
		#else
		#include <utask.h>
		#endif

		#include <pthread.h>

		#include <assert.h>

		#include "preesm_gen.h"
		#include "communication.h"

		«IF (this.distributedOnly == 0)»
		/* Shared Segment ID */
		mppa_async_segment_t shared_segment;
		«ENDIF»
		«IF (this.sharedOnly == 0 && this.distributedOnly == 1)»
		/* Distributed Segments ID */
		mppa_async_segment_t distributed_segment[PREESM_NB_CLUSTERS + PREESM_IO_USED];
		extern int local_memory_size;
		«ENDIF»

		/* MPPA PREESM Thread definition */
		typedef void* (*mppa_preesm_task_t)(void *args);

		/* pthread_t declaration */
		static pthread_t threads[PREESM_NB_CORES_CC-1] __attribute__((__unused__));

		/* thread function pointers declaration */
		static mppa_preesm_task_t mppa_preesm_task[PREESM_NB_CLUSTERS];

		/* global barrier called at each execution of ALL of the dataflow graph */
		pthread_barrier_t iter_barrier;
		int preesmStopThreads __attribute__((weak));

		/* extern reference of generated code */
		«FOR clusters : printerBlocks.toSet»
			«IF (clusters instanceof CoreBlock)»
				extern void *computationTask_«clusters.name»(void *arg) __attribute__((weak));
			«ENDIF»
		«ENDFOR»
		/* extern reference of shared memories */
		«FOR clusters : printerBlocks.toSet»
			«IF (clusters instanceof CoreBlock)»
				extern char *«clusters.name» __attribute__((weak));
			«ENDIF»
		«ENDFOR»

		/* Main executed on PE0 */
		int
		main(void)
		{
			if (mppa_rpc_client_init() != 0){
				assert(0 && "mppa_rpc_client_init\n");
			}
			if (mppa_async_init() != 0){
				assert(0 && "mppa_async_init\n");
			}
			if (mppa_remote_client_init() != 0){
				assert(0 && "mppa_remote_client_init\n");
			}
			«IF this.usingPapify == 1»
				#ifdef _PREESM_PAPIFY_MONITOR
				mkdir("papify-output", 0777);
				event_init();
				#endif
			«ENDIF»
			«IF (this.distributedOnly == 0)»
			if(mppa_async_segment_clone(&shared_segment, SHARED_SEGMENT_ID, NULL, 0, NULL) != 0){
				assert(0 && "mppa_async_segment_clone\n");
			}
			«ENDIF»

			«IF (this.sharedOnly == 0 && this.distributedOnly == 1)»
				/* Inter cluster communication support */
				int cc_id = __k1_get_cluster_id();
				void *cc_ptr = NULL;
				switch (cc_id){
					«FOR clusters : printerBlocks.toSet»
						«IF (clusters instanceof CoreBlock)»
							case «clusters.coreID»:
								cc_ptr = (void*)&«clusters.name»;
								break;
						«ENDIF»
					«ENDFOR»
					default:
						break;
				}
				if (mppa_async_segment_create(&distributed_segment[cc_id], INTERCC_BASE_SEGMENT_ID+cc_id, (void*)cc_ptr, local_memory_size, 0, 0, NULL) != 0){
					assert(0 && "mppa_async_segment_create\n");
				}
			«ENDIF»

			// init comm
			communicationInit();
			/* Threads wrapper to function pointers */
		«FOR clusters : printerBlocks.toSet»
			«IF (clusters instanceof CoreBlock)»
				#if (CLUSTER_ID==«clusters.coreID»)
				mppa_preesm_task[«clusters.coreID»] = computationTask_«clusters.name»;
				#endif // Cluster «clusters.coreID»
			«ENDIF»
		«ENDFOR»

			preesmStopThreads = 0;
			pthread_barrier_init(&iter_barrier, NULL, PREESM_NB_CORES_CC);
			__builtin_k1_wpurge();
			__builtin_k1_fence();
			mOS_dinval();
			mppa_rpc_barrier_all();
			«IF (io_used == 1)»
				if(__k1_get_cluster_id() == «clusterToSync»){
					mppa_rpc_barrier(1, 2);
				}
				mppa_rpc_barrier_all();
			«ENDIF»
			«IF (this.sharedOnly == 0 && this.distributedOnly == 1)»
				int i;
				for(i = 0; i < PREESM_NB_CLUSTERS + PREESM_IO_USED; i++){
					if(cc_id != i){
						if (mppa_async_segment_clone(&distributed_segment[i], INTERCC_BASE_SEGMENT_ID+i, NULL, 0, NULL) != 0){
							assert(0 && "mppa_async_segment_clone\n");
						}
					}
				}
				mppa_rpc_barrier_all();
				«IF (io_used == 1)»
					if(__k1_get_cluster_id() == «clusterToSync»){
						mppa_rpc_barrier(1, 2);
					}
					mppa_rpc_barrier_all();
				«ENDIF»
			«ENDIF»

			/* PE0 work */
			if(mppa_preesm_task[__k1_get_cluster_id()] != 0){
				//printf("Cluster %d starts task\n", __k1_get_cluster_id());
				mppa_preesm_task[__k1_get_cluster_id()](NULL);
			}else{
				printf("Cluster %d Error on code generator wrapper\n", __k1_get_cluster_id());
			}

			mppa_rpc_barrier_all();
			«IF this.usingPapify == 1»
				#ifdef _PREESM_PAPIFY_MONITOR
				event_destroy();
				#endif
			«ENDIF»
			mppa_async_final();
			return 0;
		}
	'''

	def String printMainIO(List<Block> printerBlocks) '''
		/**
		 * @file io_main.c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 *
		 */
		/*
		 * Copyright (C) 2016 Kalray SA.
		 *
		 * All rights reserved.
		 */

		#include <stdio.h>
		#include <stdlib.h>
		#include "mppa_boot_args.h"
		#include <mppa_power.h>
		#include <assert.h>
		#include "mppa_bsp.h"
		#include <utask.h>
		#include <pcie_queue.h>
		#include <mppa_rpc.h>
		#include <mppa_remote.h>
		#include <mppa_async.h>
		#include <HAL/hal/board/boot_args.h>
		#include "preesm_gen.h"

		static utask_t t;
		static mppadesc_t pcie_fd = 0;

		«IF (this.sharedOnly == 0 && this.distributedOnly == 1)»
		/* Distributed Segments ID */
		mppa_async_segment_t distributed_segment[PREESM_NB_CLUSTERS + PREESM_IO_USED] __attribute__ ((unused));
		«ENDIF»

		int
		main(int argc __attribute__ ((unused)), char *argv[] __attribute__ ((unused)))
		{
			int id;
			int j;
			int ret ;

			if(__k1_spawn_type() == __MPPA_PCI_SPAWN){
				#if 1
				long long *ptr = (void*)(uintptr_t)Shared;
				long long i;
				for(i=0;i<(long long)((1<<30ULL)/sizeof(long long));i++)
				{
					ptr[i] = -1LL;
				}
				__builtin_k1_wpurge();
				__builtin_k1_fence();
				mOS_dinval();
				#endif
			}

			if (__k1_spawn_type() == __MPPA_PCI_SPAWN) {
				pcie_fd = pcie_open(0);
					ret = pcie_queue_init(pcie_fd);
					assert(ret == 0);
			}

			if(mppa_rpc_server_init(	1 /* rm where to run server */,
									0 /* offset ddr */,
									PREESM_NB_CLUSTERS /* nb_cluster to serve*/) != 0){
				assert(0 && "mppa_rpc_server_init\n");
			}
			if(mppa_async_server_init() != 0){
				assert(0 && "mppa_async_server_init\n");
			}
			if(mppa_remote_server_init(pcie_fd, PREESM_NB_CLUSTERS) != 0){
				assert(0 && "mppa_remote_server_init\n");
			}
			if (mppa_remote_server_enable_scall() != 0){
				assert(0 && "mppa_remote_server_enable_scall\n");
			}
			if(utask_create(&t, NULL, (void*)mppa_rpc_server_start, NULL) != 0){
				assert(0 && "utask_create\n");
			}

			mppa_async_segment_t shared_segment;
			if(mppa_async_segment_create(&shared_segment, SHARED_SEGMENT_ID, (void*)(uintptr_t)Shared, 1024*1024*1024, 0, 0, NULL) != 0){
				assert(0 && "mppa_async_segment_create\n");
			}

			for( j = 0 ; j < PREESM_NB_CLUSTERS ; j++ ) {

				char elf_name[30];
				sprintf(elf_name, "cluster%d_bin", j);
				id = mppa_power_base_spawn(j, elf_name, NULL, NULL, MPPA_POWER_SHUFFLING_ENABLED);
				if (id < 0)
					return -2;
			}

			int err;
			for( j = 0 ; j < PREESM_NB_CLUSTERS ; j++ ) {
			    mppa_power_base_waitpid (j, &err, 0);
			}

			if (__k1_spawn_type() == __MPPA_PCI_SPAWN) {
				pcie_queue_barrier(pcie_fd, 0, &ret);
				pcie_queue_exit(pcie_fd, ret, NULL);
			}
			return 0;
		}

	'''

	def String printMainHost(List<Block> printerBlocks) '''
		/**
		 * @file host_main.c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 *
		 */
		/*
		 * Copyright (C) 2016 Kalray SA.
		 *
		 * All rights reserved.
		 */
		#include <pcie.h>

		int
		main(int argc, char **argv)
		{
			«IF this.usingPapify == 1»
			mkdir("papify-output", 0777);
			«ENDIF»
			mppadesc_t fd = pcie_open_device(0);
			/* check for correct number of arguments */
			if (argc < 3) {
				printf("Error, no multibinary provided to host executatble\n");
				return -1;
			}

			/* load on the MPPA the k1 multi-binary */
			pcie_load_io_exec_args_mb(fd, argv[1], argv[2], NULL, 0, PCIE_LOAD_FULL);

			//pcie_load_io_exec(fd, "ddr_paging");

			pcie_queue_init(fd);

			int status;
			pcie_queue_barrier(fd, 0, &status);

			pcie_queue_exit(fd, 0, &status);

			return status;
		}

	'''
	override preProcessing(List<Block> printerBlocks, Collection<Block> allBlocks){
		var Set<String> coresNames = new LinkedHashSet<String>();
		for (cluster : allBlocks){
			if (cluster instanceof CoreBlock) {
				coresNames.add(cluster.name);
			}
		}
		for (cluster : allBlocks){
			if (cluster instanceof CoreBlock) {
				if(!cluster.loopBlock.codeElts.empty){
					if(cluster.coreType.equals("MPPA2Explicit")){
						numClusters = numClusters + 1;
						clusterToSync = cluster.coreID;
					}
					else if(cluster.coreType.equals("MPPA2IOExplicit")){
						io_used = 1;
					}
					for(CodeElt codeElt : cluster.loopBlock.codeElts){
						if(codeElt instanceof PapifyFunctionCall){
							this.usingPapify = 1;
						} else if(codeElt instanceof FiniteLoopBlock){
							this.usingClustering = 1;
						}
					}
	       		 	var EList<Variable> definitions = cluster.getDefinitions();
	       		 	var EList<Variable> declarations = cluster.getDeclarations();
	       		 	for(Variable variable : definitions){
	       		 		if(variable instanceof Buffer){
	       		 			if(variable.name.equals("Shared")){
								this.distributedOnly = 0;
	       		 			}else if(coresNames.contains(variable.name)){
								this.sharedOnly = 0;
	       		 			}
	       		 		}
	       		 	}
	       		 	for(Variable variable : declarations){
	       		 		if(variable instanceof Buffer){
	       		 			if(variable.name.equals("Shared")){
								this.distributedOnly = 0;
	       		 			}else if(coresNames.contains(variable.name)){
								this.sharedOnly = 0;
	       		 			}
	       		 		}
	       		 	}	       		 	
	       		 }
			}
		}
		local_buffer_size = 0;
	}
	override postProcessing(CharSequence charSequence){
		var ret = charSequence.toString.replace("int local_buffer_size = 0;", "int local_buffer_size = " + local_buffer_size + ";");
		return ret;
	}
}
