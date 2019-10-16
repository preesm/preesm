/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2016)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2018)
 * Leonardo Suriano [leonardo.suriano@upm.es] (2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013 - 2016)
 * Raquel Lazcano [raquel.lazcano@upm.es] (2019)
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
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.Date
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashSet
import java.util.List
import java.util.Map
import java.util.Set
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.preesm.codegen.model.ActorFunctionCall
import org.preesm.codegen.model.Block
import org.preesm.codegen.model.Buffer
import org.preesm.codegen.model.BufferIterator
import org.preesm.codegen.model.Call
import org.preesm.codegen.model.CallBlock
import org.preesm.codegen.model.ClusterBlock
import org.preesm.codegen.model.CodeElt
import org.preesm.codegen.model.Communication
import org.preesm.codegen.model.Constant
import org.preesm.codegen.model.ConstantString
import org.preesm.codegen.model.CoreBlock
import org.preesm.codegen.model.DataTransferAction
import org.preesm.codegen.model.Delimiter
import org.preesm.codegen.model.Direction
import org.preesm.codegen.model.FifoCall
import org.preesm.codegen.model.FifoOperation
import org.preesm.codegen.model.FiniteLoopBlock
import org.preesm.codegen.model.FunctionCall
import org.preesm.codegen.model.IntVar
import org.preesm.codegen.model.IteratedBuffer
import org.preesm.codegen.model.LoopBlock
import org.preesm.codegen.model.NullBuffer
import org.preesm.codegen.model.PapifyAction
import org.preesm.codegen.model.PapifyFunctionCall
import org.preesm.codegen.model.PortDirection
import org.preesm.codegen.model.RegisterSetUpAction
import org.preesm.codegen.model.SectionBlock
import org.preesm.codegen.model.SharedMemoryCommunication
import org.preesm.codegen.model.SpecialCall
import org.preesm.codegen.model.SubBuffer
import org.preesm.codegen.model.Variable
import org.preesm.codegen.printer.BlankPrinter
import org.preesm.codegen.printer.PrinterState
import org.preesm.commons.exceptions.PreesmRuntimeException
import org.preesm.commons.files.PreesmResourcesHelper
import org.preesm.model.pisdf.util.CHeaderUsedLocator

/**
 * This printer is currently used to print C code only for GPP processors
 * supporting pthreads and shared memory communication.
 *
 * @author kdesnos
 * @author mpelcat
 */
class CPrinter extends BlankPrinter {

	boolean monitorAllFifoMD5 = true;

	Map<CoreBlock, Set<FifoCall>> fifoPops = new HashMap();

	/*
	 * Variable to check if we are using PAPIFY or not --> Will be updated during preprocessing
	 */
	int usingPapify = 0;
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
	 * Temporary global var to ignore the automatic suppression of memcpy
	 * whose target and destination are identical.
	 */
	protected static boolean IGNORE_USELESS_MEMCPY = true

	/**
	 * To control the way sizes are printed in memcopy/send/init/...
	 * If false, the code will embed the operation "tokenCount * sizeof(token type)"
	 * If true, the code will embed the result of the operation (type size is taken from scenario)
	 */
	protected static boolean SIMPLE_BUFFER_SIZES = true

	override printCoreBlockHeader(CoreBlock block) '''
			/**
			 * @file «block.name».c
			 * @generated by «this.class.simpleName»
			 * @date «new Date»
			 *
			 * Code generated for processing element «block.name» (ID=«block.coreID»).
			 */

			#include "preesm_gen.h"

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

	«IF monitorAllFifoMD5»
	#ifdef PREESM_MD5_UPDATE
	// +All FIFO MD5 contexts«var long counter = 0L»
	«FOR buffer : getAllBuffers(printedCoreBlock)»
	const unsigned long preesm_md5_ctx_«buffer.name»_id = «counter++»;
	«ENDFOR»
	PREESM_MD5_CTX bufferMd5[«counter»];
	PREESM_MD5_CTX preStateBufferMd5[«counter»];

	char * idToBufferName_«printedCoreBlock.coreID»[«counter»] = {
		"«getAllBuffers(printedCoreBlock).map[it.name].join("\",\"")»"
	};
	// -All FIFO MD5 contexts

	void preesmBackupPreStateMD5_«printedCoreBlock.coreID»() {
		int i;
		for (i = 0; i < «counter»; i++) {
			PREESM_MD5_Copy(&preStateBufferMd5[i], &bufferMd5[i]);
		}
	}

	void preesmUpdateMD5Array_«printedCoreBlock.coreID»(PREESM_MD5_CTX md5Array[«counter»]) {
		«FOR buffer : getAllBuffers(printedCoreBlock)»
		PREESM_MD5_Update(&md5Array[preesm_md5_ctx_«buffer.name»_id],(char *)«buffer.name», «buffer.size * buffer.typeSize»);
		«ENDFOR»
	}

	void preesmPrintMD5Array_«printedCoreBlock.coreID»(PREESM_MD5_CTX md5Array[«counter»], int loopIndex, const int actorId) {
		// +All FIFO MD5 contexts
		rk_sema_wait(&preesmPrintSema);
	 	char md5String[32];
		«FOR buffer : getAllBuffers(printedCoreBlock)»
		PREESM_MD5_tostring_no_final(md5String, &md5Array[preesm_md5_ctx_«buffer.name»_id]);
		printf("iteration %09d - pos %09d - preesm_md5_«buffer.name» : %s\n", loopIndex, actorId, md5String); fflush(stdout);
		«ENDFOR»
		rk_sema_post(&preesmPrintSema);
		// -All FIFO MD5 contexts
	}



	void preesmCheckMD5Array_«printedCoreBlock.coreID»(PREESM_MD5_CTX backupMd5Array[«counter»], PREESM_MD5_CTX currentMd5Array[«counter»],
		unsigned int authorizedBufferCount, unsigned long authorizedBufferIDs[authorizedBufferCount], const char* actorname) {

		unsigned int md5counter = 0;
		for (md5counter = 0; md5counter < «counter»; md5counter++) {
			char backupMd5[33] = { '\0' };
			char currentMd5[33] = { '\0' };
			PREESM_MD5_tostring_no_final(backupMd5, &backupMd5Array[md5counter]);
			PREESM_MD5_tostring_no_final(currentMd5, &currentMd5Array[md5counter]);
			int cmp = strcmp(backupMd5, currentMd5);
			if (cmp != 0) {
				int authBuffsCounter = 0;
				unsigned char isAuthorized = 0;
				for (authBuffsCounter = 0; authBuffsCounter < authorizedBufferCount; authBuffsCounter++) {
					if (authorizedBufferIDs[authBuffsCounter] == md5counter) {
						isAuthorized = 1;
						break;
					}
				}
				if (!isAuthorized) {
					printf("Actor %s accessed unauthorized buffer id %d ('%s') \n", actorname, md5counter, idToBufferName_«printedCoreBlock.coreID»[md5counter]);
				}
			} else {
				// same md5 => actor did not write on this buffer.
			}

		}
	}
	#endif
	«ENDIF»
	'''

	override printDeclarationsHeader(List<Variable> list) '''
	// Core Global Declaration
	extern pthread_barrier_t iter_barrier;
	extern int preesmStopThreads;

	«IF monitorAllFifoMD5 || !monitorAllFifoMD5 && !printedCoreBlock.sinkFifoBuffers.isEmpty»
	#ifdef PREESM_MD5_UPDATE
	extern struct rk_sema preesmPrintSema;
	unsigned char preesm_md5_chars[16];
	#endif
	«ENDIF»

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
	void *computationThread_Core«(callBlock.eContainer as CoreBlock).coreID»(void *arg){
		if (arg != NULL) {
			printf("Warning: expecting NULL arguments\n");
		}

	«IF monitorAllFifoMD5»
	#ifdef PREESM_MD5_UPDATE
	// +All FIFO MD5 contexts
	«FOR buffer : getAllBuffers(printedCoreBlock)»
	PREESM_MD5_Init(&bufferMd5[preesm_md5_ctx_«buffer.name»_id]);
	«ENDFOR»
	// -All FIFO MD5 contexts
	#endif
	«ENDIF»

	«IF !monitorAllFifoMD5 && !printedCoreBlock.sinkFifoBuffers.isEmpty»
#ifdef PREESM_MD5_UPDATE
	«FOR buffer : printedCoreBlock.sinkFifoBuffers»
	PREESM_MD5_CTX preesm_md5_ctx_«buffer.name»;
	PREESM_MD5_Init(&preesm_md5_ctx_«buffer.name»);
	«ENDFOR»
#endif
	«ENDIF»
		«IF !callBlock.codeElts.empty»// Initialisation(s)«"\n\n"»«ENDIF»
	'''

	def List<Buffer> getAllBuffers(CoreBlock cb) {
		val Set<Buffer> allBuffers = new LinkedHashSet()
		val List<ActorFunctionCall> allActorCalls = printedCoreBlock.loopBlock.codeElts.filter[it instanceof ActorFunctionCall].map[it as ActorFunctionCall].toList
		allActorCalls.forEach[allBuffers.addAll(getInBuffers(it)); allBuffers.addAll(getOutBuffers(it))]

		val List<FifoCall> allFifoCalls = printedCoreBlock.loopBlock.codeElts.filter[it instanceof FifoCall].map[it as FifoCall].filter[it.operation != FifoOperation::INIT].toList
		allFifoCalls.forEach[{
			val FifoCall fifoCall = it;
			val inBuffers2 = getInBuffers(fifoCall)
			val outBuffers2 = getOutBuffers(fifoCall)
			allBuffers.addAll(inBuffers2);
			allBuffers.addAll(outBuffers2)
		}]

		return new ArrayList(allBuffers);
	}

	def List<Buffer> getInBuffers(Call afc) {
		val dirs = afc.parameterDirections
		val params = afc.parameters
		val Set<Buffer> inBuffers = new LinkedHashSet()
		for( var i = 0; i < dirs.size; i++) {
			val PortDirection dir = dirs.get(i)
			val Variable param = params.get(i)
			if (param instanceof Buffer && dir == PortDirection.INPUT) {
				inBuffers.add(param as Buffer)
			}
		}
		if (afc instanceof FifoCall && (afc as FifoCall).operation == FifoOperation.POP) {
			val headBuffer = (afc as FifoCall).headBuffer
			if (headBuffer !== null) {
				inBuffers.add(headBuffer)
			}
		}
		if (afc instanceof FifoCall && (afc as FifoCall).operation == FifoOperation.PUSH) {
			inBuffers.add((afc as FifoCall).parameters.head as Buffer)
		}
		return new ArrayList(inBuffers)
	}

	def List<Buffer> getOutBuffers(Call afc) {
		val dirs = afc.parameterDirections
		val params = afc.parameters
		val Set<Buffer> outBuffers = new LinkedHashSet()
		for( var i = 0; i < dirs.size; i++) {
			val PortDirection dir = dirs.get(i)
			val Variable param = params.get(i)
			if (param instanceof Buffer && dir == PortDirection.OUTPUT) {
				outBuffers.add(param as Buffer)
			}
		}
		if (afc instanceof FifoCall && (afc as FifoCall).operation == FifoOperation.PUSH) {
			val headBuffer = (afc as FifoCall).headBuffer
			if (headBuffer !== null) {
				outBuffers.add(headBuffer)
			}
		}
		if (afc instanceof FifoCall && (afc as FifoCall).operation == FifoOperation.POP) {
			outBuffers.add((afc as FifoCall).parameters.head as Buffer)
		}
		return new ArrayList(outBuffers)
	}

	override printCoreLoopBlockHeader(LoopBlock block2) '''

	// Begin the execution loop«"\n\t"»
	pthread_barrier_wait(&iter_barrier);
#ifdef PREESM_LOOP_SIZE // Case of a finite loop
	int index;
	for(index=0;index<PREESM_LOOP_SIZE && !preesmStopThreads;index++){
#else // Default case of an infinite loop
	while(!preesmStopThreads){
#endif
		// loop body«"\n\n"»
		'''

	override printCoreLoopBlockFooter(LoopBlock block2) '''
			// loop footer
			pthread_barrier_wait(&iter_barrier);

		}

	«IF !monitorAllFifoMD5 && !printedCoreBlock.sinkFifoBuffers.isEmpty»
#ifdef PREESM_MD5_UPDATE
	// Print MD5
	rk_sema_wait(&preesmPrintSema);
	unsigned char preesm_md5_chars[16];
	«FOR buffer : printedCoreBlock.sinkFifoBuffers»
	PREESM_MD5_Final(preesm_md5_chars, &preesm_md5_ctx_«buffer.name»);
	printf("preesm_md5_«buffer.name» : ");
	for (int i = 16; i > 0; i -= 1){
		printf("%02x", *(preesm_md5_chars + i - 1));
	}
	printf("\n");
	fflush(stdout);
	«ENDFOR»
	rk_sema_post(&preesmPrintSema);
#endif
	«ENDIF»

		return NULL;
	}

	«IF block2.codeElts.empty»
	// This call may inform the compiler that the main loop of the thread does not call any function.
	void emptyLoop_«(block2.eContainer as CoreBlock).name»(){

	}
	«ENDIF»
	'''

	//#pragma omp parallel for private(«block2.iter.name»)
	override printFiniteLoopBlockHeader(FiniteLoopBlock block2) '''
		// Begin the for loop
		{
			int «block2.iter.name»;
			«IF block2.parallel.equals(true)»
			#pragma omp parallel for private(«block2.iter.name»)
			«ENDIF»
			for(«block2.iter.name»=0;«block2.iter.name»<«block2.nbIter»;«block2.iter.name»++) {

				'''

	override printFiniteLoopBlockFooter(FiniteLoopBlock block2) '''
			}
		}
	'''

	override printClusterBlockHeader(ClusterBlock block) '''
		// Cluster: «block.name»
		// Schedule: «block.schedule»
		«IF block.parallel.equals(true)»
		#pragma omp parallel sections
		«ENDIF»
		{

			'''

	override printClusterBlockFooter(ClusterBlock block) '''
		}
	'''

	override printSectionBlockHeader(SectionBlock block) '''
		#pragma omp section
		{

			'''

	override printSectionBlockFooter(SectionBlock block) '''
		}
	'''

	override String printFifoCall(FifoCall fifoCall) {
		var preCheck = '''
			«IF state == PrinterState.PRINTING_LOOP_BLOCK && monitorAllFifoMD5»
			#ifdef PREESM_MD5_UPDATE
				preesmBackupPreStateMD5_«printedCoreBlock.coreID»();
				preesmUpdateMD5Array_«printedCoreBlock.coreID»(preStateBufferMd5);
			#endif
			«ENDIF»'''

		var postCheck = '''
			«IF state == PrinterState.PRINTING_LOOP_BLOCK && monitorAllFifoMD5»
			#ifdef PREESM_MD5_UPDATE
				preesmUpdateMD5Array_«printedCoreBlock.coreID»(bufferMd5);
				«val outBuffers = getOutBuffers(fifoCall)»
				unsigned long authorizedBufferIds_«(fifoCall).hashCode»[«outBuffers.size»] = {
					«outBuffers.map["preesm_md5_ctx_"+it.name+"_id"].join(", ")»
				};
				preesmCheckMD5Array_«printedCoreBlock.coreID»(preStateBufferMd5, bufferMd5,
						«outBuffers.size»,authorizedBufferIds_«(fifoCall).hashCode», "FIFO «(fifoCall).name»");
			#endif
			«ENDIF»'''
		var result = "fifo" + fifoCall.operation.toString.toLowerCase.toFirstUpper + "("

		if (!fifoPops.containsKey(printedCoreBlock)) {
			fifoPops.put(printedCoreBlock, new HashSet());
		}
		if (fifoCall.operation == FifoOperation::POP) {
			fifoPops.get(printedCoreBlock).add(fifoCall)
		}
		if (fifoCall.operation == FifoOperation::PUSH) {
			val FifoCall correspondingPop = fifoCall.fifoHead;
			if (fifoPops.get(printedCoreBlock).contains(correspondingPop)) {
				throw new PreesmRuntimeException("Fifo pop/push issue");
			}
		}
		if (fifoCall.operation != FifoOperation::INIT) {
			var buffer = fifoCall.parameters.head as Buffer
			result = result + '''«buffer.name», '''
		}

		result = preCheck + result +
			'''«fifoCall.headBuffer.name», «if (SIMPLE_BUFFER_SIZES) fifoCall.headBuffer.size * engine.scenario.simulationInfo.getDataTypeSizeOrDefault(fifoCall.headBuffer.type) else fifoCall.headBuffer.size+"*sizeof("+fifoCall.headBuffer.type+")"», '''
		return result = result + '''«IF fifoCall.bodyBuffer !== null»«fifoCall.bodyBuffer.name», «
		if (SIMPLE_BUFFER_SIZES) fifoCall.bodyBuffer.size  * engine.scenario.simulationInfo.getDataTypeSizeOrDefault(fifoCall.bodyBuffer.type) else fifoCall.bodyBuffer.size+"*sizeof("+fifoCall.bodyBuffer.type+")"»«ELSE»NULL, 0«ENDIF»);
			«postCheck»'''
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
	// Broadcast «call.name»«var input = call.inputBuffers.head»«var index = 0L»
	{
	«FOR output : call.outputBuffers»«var outputIdx = 0L»
		«// TODO: Change how this loop iterates (nbIter is used in a comment only ...)
		FOR nbIter : 0..(output.size/input.size+1) as int/*Worst case is output.size exec of the loop */»
			«IF outputIdx < output.size /* Execute loop core until all output for current buffer are produced */»
				«val value = Math::min(output.size-outputIdx,input.size-index)»// memcpy #«nbIter»
				«printMemcpy(output,outputIdx,input,index,value,output.type)»«
				{index=(index+value)%input.size;outputIdx=(outputIdx+value); ""}»
			«ENDIF»
		«ENDFOR»
	«ENDFOR»
	}
	'''



	override printRoundBuffer(SpecialCall call) '''
	// RoundBuffer «call.name»«var output = call.outputBuffers.head»«var index = 0L»«var inputIdx = 0L»
	«/*Compute a list of useful memcpy (the one writing the outputed value) */
	var copiedInBuffers = {var totalSize = call.inputBuffers.fold(0L)[res, buf | res+buf.size]
		 var lastInputs = new ArrayList
		 inputIdx = totalSize
		 var i = call.inputBuffers.size	- 1
		 while(totalSize-inputIdx < output.size){
		 	inputIdx = inputIdx - call.inputBuffers.get(i).size
		 	lastInputs.add(0,call.inputBuffers.get(i))
			if (i < 0) {
				throw new PreesmRuntimeException("Invalid RoundBuffer sizes: output size is greater than cumulative input size.")
			}
		 	i=i-1
		 }
		 inputIdx = inputIdx %  output.size
		 lastInputs
		 }»
	{
		«FOR input : copiedInBuffers»
			«// TODO: Change how this loop iterates (nbIter is used in a comment only ...)
			FOR nbIter : 0..(input.size/output.size+1) as int/*Worst number the loop exec */»
				«IF inputIdx < input.size /* Execute loop core until all input for current buffer are produced */»
					«val value = Math::min(input.size-inputIdx,output.size-index)»// memcpy #«nbIter»
					«printMemcpy(output,index,input,inputIdx,value,input.type)»«{
						index=(index+value)%output.size;
						inputIdx=(inputIdx+value); ""
					}»
				«ENDIF»
			«ENDFOR»
		«ENDFOR»
	}
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
	def String printMemcpy(Buffer output, long outOffset, Buffer input, long inOffset, long size, String type) {

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
			return '''memcpy(«output.doSwitch»+«outOffset», «input.doSwitch»+«inOffset», «if (SIMPLE_BUFFER_SIZES) size * engine.scenario.simulationInfo.getDataTypeSizeOrDefault(type) else size+"*sizeof("+type+")"»);'''
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

	def CharSequence generatePreesmHeader(List<String> stdLibFiles) {
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
	    val findAllCHeaderFileNamesUsed = CHeaderUsedLocator.findAllCHeaderFileNamesUsed(getEngine.algo)

	    context.put("PREESM_INCLUDES", stdLibFiles.filter[it.endsWith(".h")].map["#include \""+ it +"\""].join("\n"));

	    context.put("USER_INCLUDES", findAllCHeaderFileNamesUsed.map["#include \""+ it +"\""].join("\n"));

		var String constants = "#define NB_DESIGN_ELTS "+getEngine.archi.componentInstances.size+"\n#define NB_CORES "+getEngine.codeBlocks.size;
		if(this.usingPapify == 1){
			constants = constants.concat("\n\n#ifdef _PREESM_PAPIFY_MONITOR\n#include \"eventLib.h\"\n#endif");
		}
		if(this.apolloEnabled){
			constants = constants.concat("\n\n#ifdef PREESM_APOLLO_ENABLED\n#include \"apolloAPI.h\"\n#endif");
		}
	    context.put("CONSTANTS", constants);

	    // 3- init template reader
	    val String templateLocalPath = "templates/c/preesm_gen.h";
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
		val result = super.generateStandardLibFiles();
		val String stdFilesFolder = "/stdfiles/c/"
		val files = Arrays.asList(#[
						"communication.c",
						"communication.h",
						"dump.c",
						"dump.h",
						"fifo.c",
						"fifo.h",
						"preesm_md5.c",
						"preesm_md5.h",
						"mac_barrier.c",
						"mac_barrier.h"
					]);
		files.forEach[it | try {
			result.put(it, PreesmResourcesHelper.instance.read(stdFilesFolder + it, this.class))
		} catch (IOException exc) {
			throw new PreesmRuntimeException("Could not generated content for " + it, exc)
		}]
		result.put("preesm_gen.h",generatePreesmHeader(files))
		return result
	}

	override createSecondaryFiles(List<Block> printerBlocks, Collection<Block> allBlocks) {
		val result = super.createSecondaryFiles(printerBlocks, allBlocks);
		if (generateMainFile()) {
			result.put("main.c", printMain(printerBlocks))
		}
		return result
	}

	def String printMain(List<Block> printerBlocks) '''
		/**
		 * @file main.c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 *
		 */
		// no monitoring by default

		#define _PREESM_NBTHREADS_ «engine.codeBlocks.size»
		#define _PREESM_MAIN_THREAD_ «mainOperatorId»

		// application dependent includes
		#include "preesm_gen.h"

		// Declare computation thread functions
		«FOR coreBlock : engine.codeBlocks»
		void *computationThread_Core«(coreBlock as CoreBlock).coreID»(void *arg);
		«ENDFOR»

		pthread_barrier_t iter_barrier;
		int preesmStopThreads;

#ifdef PREESM_MD5_UPDATE
		struct rk_sema preesmPrintSema;
#endif

		unsigned int launch(unsigned int core_id, pthread_t * thread, void *(*start_routine) (void *)) {

			// init pthread attributes
			pthread_attr_t attr;
			pthread_attr_init(&attr);

		#ifndef PREESM_NO_AFFINITY
		#ifdef _WIN32
			SYSTEM_INFO sysinfo;
			GetSystemInfo(&sysinfo);
			unsigned int numCPU = sysinfo.dwNumberOfProcessors;
		#else
			unsigned int numCPU = sysconf(_SC_NPROCESSORS_ONLN);
		#endif

			// check CPU id is valid
			if (core_id >= numCPU) {
				// leave attribute uninitialized
				printf("** Warning: thread %d will not be set with specific core affinity \n   due to the lack of available dedicated cores.\n",core_id);
			} else {
		#if defined __APPLE__ || defined _WIN32
				// NOT SUPPORTED
		#else
				// init cpuset struct
				cpu_set_t cpuset;
				CPU_ZERO(&cpuset);
				CPU_SET(core_id, &cpuset);

				// set pthread affinity
				pthread_attr_setaffinity_np(&attr, sizeof(cpuset), &cpuset);
		#endif
			}
		#endif

			// create thread
			pthread_create(thread, &attr, start_routine, NULL);
			return 0;
		}


		int main(void) {
			// Set affinity of main thread to proper core ID
		#ifndef PREESM_NO_AFFINITY
		#if defined __APPLE__ || defined _WIN32
			// NOT SUPPORTED
		#else
			cpu_set_t cpuset;
			CPU_ZERO(&cpuset);
			CPU_SET(_PREESM_MAIN_THREAD_, &cpuset);
			«IF this.apolloEnabled»
			#ifndef APOLLO_AVAILABLE
			«ENDIF»
			sched_setaffinity(getpid(),  sizeof(cpuset), &cpuset);
			«IF this.apolloEnabled»
			#endif
			«ENDIF»

		#endif
		#endif

			«IF this.usingPapify == 1»
				#ifdef _PREESM_PAPIFY_MONITOR
				mkdir("papify-output", 0777);
				event_init_multiplex();
				#endif
			«ENDIF»
			// Declaring thread pointers
			pthread_t coreThreads[_PREESM_NBTHREADS_];
			void *(*coreThreadComputations[_PREESM_NBTHREADS_])(void *) = {
				«FOR coreBlock : engine.codeBlocks»&computationThread_Core«(coreBlock as CoreBlock).coreID»«if(engine.codeBlocks.last == coreBlock) {""} else {", "}»«ENDFOR»
			};

		#ifdef PREESM_VERBOSE
			printf("Launched main\n");
		#endif

			// Creating a synchronization barrier
			preesmStopThreads = 0;
			pthread_barrier_init(&iter_barrier, NULL, _PREESM_NBTHREADS_);

#ifdef PREESM_MD5_UPDATE
			rk_sema_init(&preesmPrintSema, 1);
#endif
			communicationInit();

			«IF this.apolloEnabled»
			#ifdef APOLLO_AVAILABLE
				initApolloForDataflow();
			#endif
			«ENDIF»

			// Creating threads
			for (int i = 0; i < _PREESM_NBTHREADS_; i++) {
				if (i != _PREESM_MAIN_THREAD_) {
					if(launch(i,&coreThreads[i],coreThreadComputations[i])) {
						printf("Error: could not launch thread %d\n",i);
						return 1;
					}
				}
			}

			// run main operator code in this thread
			coreThreadComputations[_PREESM_MAIN_THREAD_](NULL);

			// Waiting for thread terminations
			for (int i = 0; i < _PREESM_NBTHREADS_; i++) {
				if (i != _PREESM_MAIN_THREAD_) {
					pthread_join(coreThreads[i], NULL);
				}
			}
			«IF this.usingPapify == 1»
				#ifdef _PREESM_PAPIFY_MONITOR
				event_destroy();
				#endif
			«ENDIF»

			return 0;
		}

	'''


	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''
	«/*Since everything is already in shared memory, communications are simple synchronizations here*/
	IF (communication.comment !== null && !communication.comment.empty)
	»«IF (communication.comment.contains("\n"))
	»/* «ELSE»// «ENDIF»«communication.comment»
	«IF (communication.comment.contains("\n"))» */
	«ENDIF»«ENDIF»«IF communication.isRedundant»//«ENDIF»«communication.direction.toString.toLowerCase»«communication.delimiter.toString.toLowerCase.toFirstUpper»(«IF (communication.
		direction == Direction::SEND && communication.delimiter == Delimiter::START) ||
		(communication.direction == Direction::RECEIVE && communication.delimiter == Delimiter::END)»«communication.sendStart.coreContainer.coreID», «communication.receiveStart.coreContainer.coreID»«ENDIF
		»); // «communication.sendStart.coreContainer.name» > «communication.receiveStart.coreContainer.name»
	'''


	override printPostFunctionCall(FunctionCall functionCall) '''
	«IF state == PrinterState.PRINTING_LOOP_BLOCK && !monitorAllFifoMD5 && !printedCoreBlock.sinkFifoBuffers.isEmpty && functionCall instanceof ActorFunctionCall && (functionCall as ActorFunctionCall).actor.dataOutputPorts.isEmpty»#ifdef PREESM_MD5_UPDATE
	«FOR buffer : printedCoreBlock.sinkFifoBuffers»«IF functionCall.parameters.contains(buffer)»
	PREESM_MD5_Update(&preesm_md5_ctx_«buffer.name»,(char *)«buffer.name», «buffer.size * buffer.typeSize»);
	«ENDIF»«ENDFOR»#endif
	«ENDIF»

	«IF state == PrinterState.PRINTING_LOOP_BLOCK && monitorAllFifoMD5 && functionCall instanceof ActorFunctionCall»
	#ifdef PREESM_MD5_UPDATE
		preesmUpdateMD5Array_«printedCoreBlock.coreID»(bufferMd5);
		«val outBuffers = getOutBuffers((functionCall as ActorFunctionCall))»
		unsigned long authorizedBufferIds_«(functionCall as ActorFunctionCall).hashCode»[«outBuffers.size»] = {
			«outBuffers.map["preesm_md5_ctx_"+it.name+"_id"].join(", ")»
		};
		preesmCheckMD5Array_«printedCoreBlock.coreID»(preStateBufferMd5, bufferMd5,
				«outBuffers.size»,authorizedBufferIds_«(functionCall as ActorFunctionCall).hashCode», "«(functionCall as ActorFunctionCall).actor.name»");
	#endif
	«ENDIF»
	'''

	override printPreFunctionCall(FunctionCall functionCall) '''
	«IF state == PrinterState.PRINTING_LOOP_BLOCK && monitorAllFifoMD5 && functionCall instanceof ActorFunctionCall»
	#ifdef PREESM_MD5_UPDATE
		preesmBackupPreStateMD5_«printedCoreBlock.coreID»();
		preesmUpdateMD5Array_«printedCoreBlock.coreID»(preStateBufferMd5);
	#endif
	«ENDIF»
	'''

	override printFunctionCall(FunctionCall functionCall) '''
	«IF this.apolloEnabled && (functionCall instanceof ActorFunctionCall)»
	#ifdef APOLLO_AVAILABLE
	apolloAddActorConfig(0, pthread_self(), "«functionCall.actorName»");
	#endif
	«ENDIF»
	«functionCall.name»(«FOR param : functionCall.parameters SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
	'''

	override printPapifyFunctionCall(PapifyFunctionCall papifyFunctionCall) '''
	«IF papifyFunctionCall.opening == true»
		#ifdef _PREESM_PAPIFY_MONITOR
	«ENDIF»
	«printFunctionCall(papifyFunctionCall)»
	«IF papifyFunctionCall.closing == true»
		#endif
	«ENDIF»
	'''

	override printConstant(Constant constant) '''«constant.value»«IF !constant.name.nullOrEmpty»/*«constant.name»*/«ENDIF»'''

	override printConstantString(ConstantString constant) '''"«constant.value»"'''

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

	override printBuffer(Buffer buffer) '''«buffer.name»'''

	override printSubBuffer(SubBuffer buffer) {return printBuffer(buffer)}

	override printBufferIterator(BufferIterator bufferIterator) '''«bufferIterator.name» + «printIntVar(bufferIterator.iter)» * «bufferIterator.iterSize»'''

	override printBufferIteratorDeclaration(BufferIterator bufferIterator) ''''''

	override printBufferIteratorDefinition(BufferIterator bufferIterator) ''''''

	override printIteratedBuffer(IteratedBuffer iteratedBuffer) '''«doSwitch(iteratedBuffer.buffer)» + «printIntVar(iteratedBuffer.iter)» * «iteratedBuffer.size»'''

	override printIntVar(IntVar intVar) '''«intVar.name»'''

	override printIntVarDeclaration(IntVar intVar) '''
	extern int «intVar.name»;
	'''

	override printIntVarDefinition(IntVar intVar) '''
	int «intVar.name»;
	'''

	override printDataTansfer(DataTransferAction action) ''''''

	override printRegisterSetUp(RegisterSetUpAction action) ''''''

	override preProcessing(List<Block> printerBlocks, Collection<Block> allBlocks){
		super.preProcessing(printerBlocks, allBlocks);
		for (cluster : allBlocks){
			if (cluster instanceof CoreBlock) {
				for(CodeElt codeElt : cluster.loopBlock.codeElts){
					if(codeElt instanceof PapifyFunctionCall){
						this.usingPapify = 1;
					}
				}
			}
		}
	}

}
