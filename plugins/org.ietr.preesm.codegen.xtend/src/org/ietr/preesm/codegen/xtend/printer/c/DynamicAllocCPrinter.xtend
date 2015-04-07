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

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.List
import java.util.Map.Entry
import java.util.Set
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.Call
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.eclipse.xtend.lib.annotations.Accessors
import java.util.Map

/**
 * This printer is currently used to print C code only for X86 processor with
 * shared memory communication. It is very similary to the {@link CPrinter} except
 * that it prints calls to <code>malloc()</code> and <code>free()</code> before and
 * after each printed function call.
 * 
 * @author kdesnos
 */
class DynamicAllocCPrinter extends CPrinter {

	/**
	 * Default constructor.
	 */
	new() {
		super()
		IGNORE_USELESS_MEMCPY = false
	}

	/**
	 * Stores the merged {@link Buffer} already
	 * {@link #printBufferDeclaration(Buffer) declared} for the current
	 * {@link CoreBlock}. Must be emptied before printed a new {@link CoreBlock}
	 */
	Set<Buffer> printedMerged

	/**
	 * Associates a buffer to all its merged buffers.
	 */
	@Accessors Map<Buffer, List<Buffer>> mergedMalloc = new HashMap<Buffer, List<Buffer>>

	/**
	 * Associates a {@link Buffer} to all its merged buffers. Each entry
	 * corresponds to a set of Buffers that are allocated in the same place.
	 * Consequently, only the first malloc and the last free calls must be
	 * effective for these buffers. Extra care must be taken to ensure that only
	 * one malloc is executed.
	 */
	@Accessors Map<Buffer, List<Buffer>> mergedFree = new HashMap<Buffer, List<Buffer>>

	/**
	 * Associates a key from the {@link #mergedFree} map to a {@link Semaphore}
	 * used to ensure that the malloc for this {@link Buffer} is only called
	 * once.
	 */
	@Accessors Map<Buffer, Semaphore> mergedFreeSemaphore = new HashMap<Buffer, Semaphore>

	/**
	 * Associates merged buffers to their allocated buffer.
	 */
	@Accessors Map<Buffer, Buffer> mergedBuffers = new HashMap<Buffer, Buffer>

	override printCoreBlockHeader(CoreBlock block) '''
		«{ // Empty the printedMerge
			printedMerged = new HashSet<Buffer>
			super.printCoreBlockHeader(block)
		}»
		#include <memory.h>
		
		
	'''

	/**
	 * In this Printer, the preprocessing method searches for mergeable broadcasts
	 * and replaces their classic malloc method with a malloc with multiple free.
	 */
	override preProcessing(List<Block> printerBlocks, List<Block> allBlocks) {
		super.preProcessing(printerBlocks, allBlocks)
		if (IGNORE_USELESS_MEMCPY) {

			// Create a helper class for this work
			var helper = new MergeableBroadcastRoundBufferHelper

			// Scan the LoopBlock of the codeblocks to find calls
			// to broadcast
			for (block : printerBlocks) {
				for (codeElt : (block as CoreBlock).loopBlock.codeElts) {
					if (codeElt instanceof SpecialCall && (codeElt as SpecialCall).broadcast) {
						helper.mergeableBuffers = new HashMap
						helper.doSwitch(codeElt)
						if (helper.mergeableBuffers.size != 0) {
							for (Entry<Buffer,List<Buffer>> malloc : helper.mergeableBuffers.entrySet) {

								// Fill the mergedMalloc map
								val mergedInBuffer = if (mergedBuffers.containsKey(malloc.key)) {
										var buffer = mergedBuffers.get(malloc.key)
										mergedMalloc.get(buffer).addAll(malloc.value)
										buffer
									} else {
										mergedMalloc.put(malloc.key, malloc.value)
										malloc.key
									}

								// Fill the mergedBuffers map
								malloc.value.forEach[mergedBuffers.put(it, mergedInBuffer)]
							}
						}
					}

					if (codeElt instanceof SpecialCall && (codeElt as SpecialCall).roundBuffer) {
						helper.mergeableBuffers = new HashMap
						helper.doSwitch(codeElt)
						if (helper.mergeableBuffers.size != 0) {
							for (Entry<Buffer,List<Buffer>> free : helper.mergeableBuffers.entrySet) {

								// Fill the mergedFree map
								// if all buffers are inputs
								if (free.value.fold(true)[res, buffer|
									res && (codeElt as SpecialCall).inputBuffers.contains(buffer)]) {
									mergedFree.put(free.key, free.value)

									// Create the associated mutex semaphore
									val sem = CodegenFactory.eINSTANCE.createSemaphore
									sem.setName("sem_multipleAlloc_" + mergedFreeSemaphore.size)
									sem.setCreator(block)

									// And the associated init function call
									var initSem = CodegenFactory.eINSTANCE.createFunctionCall();
									initSem.addParameter(sem, PortDirection.NONE);

									var cstShared = CodegenFactory.eINSTANCE.createConstant();
									cstShared.setType("int");
									cstShared.setValue(0);
									initSem.addParameter(cstShared, PortDirection.NONE);
									cstShared.setCreator(block);

									var cstInitVal = CodegenFactory.eINSTANCE.createConstant();
									cstInitVal.setType("int");
									cstInitVal.setValue(1);
									cstInitVal.setName("init_val");
									initSem.addParameter(cstInitVal, PortDirection.NONE);
									cstInitVal.setCreator(block);
									initSem.setName("sem_init");

									// Register function to the current block
									(block as CoreBlock).getInitBlock().getCodeElts().add(initSem);

									// Register the semaphore to all its users
									free.key.users.forEach[sem.users.add(it)]
									free.value.forEach [
										sem.users.add(it.creator)
										it.users.forEach [
											sem.users.add(it)
										]
									]

									// Backup the semaphore						
									mergedFreeSemaphore.put(free.key, sem)
								} else {

									// If this entry merges an input and outputs
									mergedMalloc.put(free.key, free.value)
								}

								// Fill the mergedBuffer map
								free.value.forEach[mergedBuffers.put(it, free.key)]
							}
						}
					}
				}
			}

		}
	}

	override printBufferDefinition(Buffer buffer) '''
		«IF (!mergedBuffers.containsKey(buffer))/* No need to declare pointers for merged buffers */» 
			«buffer.type» *«buffer.name» = 0; // «buffer.comment» size:= «buffer.size»*«buffer.type»
		«ENDIF»
	'''

	override printBufferDeclaration(Buffer buffer) {
		if (!mergedBuffers.containsKey(buffer) && !printedMerged.contains(buffer)) {
			if (mergedMalloc.containsKey(buffer)) {
				printedMerged.add(buffer)
			}
			'''
				extern «buffer.type» *«buffer.name»; // «buffer.comment» size:= «buffer.size»*«buffer.type»
			'''
		} else if (mergedBuffers.containsKey(buffer)) {
			printBufferDeclaration(mergedBuffers.get(buffer))
		} else {
			''''''
		}
	}

	override printSubBufferDefinition(SubBuffer buffer) '''
		«printBufferDefinition(buffer)»
	'''

	override printSubBufferDeclaration(SubBuffer buffer) {
		printBufferDeclaration(buffer)
	}

	override printBuffer(Buffer buffer) {
		if (!mergedBuffers.containsKey(buffer)) {

			/* No need to declare pointers for merged buffers */
			'''«buffer.name»'''
		} else { /* Print the merged buffer name */
			'''«mergedBuffers.get(buffer).name»'''
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
	def printCallWithMallocFree(Call call, CharSequence sequence) '''
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

	/**
	 * Methods used to print a Malloc.
	 * @param buffer 
	 * 			the {@link Buffer} that is allocated  
	 * @return the printed code as a {@link CharSequence}.
 	 */
	def printMalloc(Buffer buffer) {
		if (!mergedBuffers.containsKey(buffer) && !mergedMalloc.containsKey(buffer) && !mergedFree.containsKey(buffer)) {

			// If the buffer is not involved in any merge operation
			// print a regular malloc
			'''«buffer.doSwitch» = («buffer.type»*) malloc(«buffer.size»*sizeof(«buffer.type»));'''
		} else if (mergedMalloc.containsKey(buffer)) {

			// If the buffer is the allocated buffer of a merge
			'''«buffer.doSwitch» = («buffer.type»*) merged_malloc(«buffer.size»*sizeof(«buffer.type»),«mergedMalloc.
				get(buffer).size + 1»/*nbOfFree*/);'''
		} else if (mergedFree.containsKey(buffer) ||
			(mergedBuffers.containsKey(buffer) && mergedFree.containsKey(mergedBuffers.get(buffer)))) {

			// The buffer is a merged free. It will be allocated several times.
			var mergedBuffer = if (mergedFree.containsKey(buffer)) {
					buffer
				} else {
					mergedBuffers.get(buffer)
				}
			'''«mergedBuffer.doSwitch» = («mergedBuffer.type»*) multiple_malloc(&«mergedBuffer.doSwitch», «mergedBuffer.
				size»*sizeof(«mergedBuffer.type»),«mergedFree.get(mergedBuffer).size + 1»/*nbOfFree*/, «mergedFreeSemaphore.
				get(mergedBuffer).doSwitch»);'''
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
		if (!mergedBuffers.containsKey(buffer) && !mergedMalloc.containsKey(buffer) && !mergedFree.containsKey(buffer)) {

			// If the buffer is not involved in any merge operation
			// print a regular free
			'''free(«buffer.doSwitch»);'''
		} else {

			// The buffer takes part in a merged malloc operation
			// print a merged free
			'''«buffer.doSwitch» = («buffer.type»*) merged_free(«buffer.doSwitch», «buffer.size»*sizeof(«buffer.type»));'''
		}
	}

	override printFifoCall(FifoCall fifoCall) '''
		«IF fifoCall.operation == FifoOperation.INIT»
			«IF fifoCall.bodyBuffer != null»
				«printMalloc(fifoCall.bodyBuffer)»
			«ENDIF»
		«ENDIF»
		«IF fifoCall.operation == FifoOperation.PUSH || fifoCall.operation == FifoOperation.INIT»
			«printMalloc(fifoCall.headBuffer)»
		«ELSE/*POP */»
			cache_inv(&«fifoCall.headBuffer.doSwitch»,1);
		«ENDIF»
		«printCallWithMallocFree(fifoCall, super.printFifoCall(fifoCall))»
		«IF fifoCall.operation == FifoOperation.POP»
			«printFree(fifoCall.headBuffer)»
		«ENDIF»
	'''

	override printBroadcast(SpecialCall call) '''
		«printCallWithMallocFree(call, super.printBroadcast(call))»
	'''

	override printRoundBuffer(SpecialCall call) ''' 
		«printCallWithMallocFree(call, super.printRoundBuffer(call))»
	'''

	override printFork(SpecialCall call) '''
		«printCallWithMallocFree(call, super.printFork(call))»
	'''

	override printJoin(SpecialCall call) '''
		«printCallWithMallocFree(call, super.printJoin(call))»
	'''
}

/**
 * This class is used within the {@link DynamicAllocCPrinter} to identify the
 * {@link Buffer buffers} that do not need to be allocated separately.
 */
class MergeableBroadcastRoundBufferHelper extends CPrinter {

	new() {
		super()
		IGNORE_USELESS_MEMCPY = true
	}

	/**
	 * When calling {@link #doSwitch(org.eclipse.emf.ecore.EObject)} on a 
	 * Broadcast or a RoundBuffer {@link SpecialCall}, this {@link Map}
	 * is filled with lists of mergeable {@link Buffer buffers}.
	 */
	@Property
	HashMap<Buffer, List<Buffer>> mergeableBuffers

	override printMemcpy(Buffer output, int outOffset, Buffer input, int inOffset, int size, String type) {

		// Retrieve the container buffer of the input and output as well
		// as their offset in this buffer
		var totalOffsetOut = outOffset
		var bOutput = output
		while (bOutput instanceof SubBuffer) {
			totalOffsetOut = totalOffsetOut + bOutput.offset
			bOutput = bOutput.container
		}

		var totalOffsetIn = inOffset
		var bInput = input
		while (bInput instanceof SubBuffer) {
			totalOffsetIn = totalOffsetIn + bInput.offset
			bInput = bInput.container
		}

		// If the Buffer and offsets are identical, add them to the map of mergeable buffers
		if (IGNORE_USELESS_MEMCPY && bInput == bOutput && totalOffsetIn == totalOffsetOut) {
			var list = mergeableBuffers.get(input) ?: new ArrayList<Buffer>()
			list.add(output)
			mergeableBuffers.put(input, list)

		} else {
		}
		''''''
	}

	override printRoundBuffer(SpecialCall call) {

		// Get all inputs except the last
		var inputs = call.inputBuffers.subList(0, call.inputBuffers.size - 1)

		// Among the inputs, retrieve those that are mergeable (i.e. those
		// that are allocated in the same Buffer, and at the same offset)
		var List<Buffer> mergeableInputs
		for (i : 0 .. inputs.size - 2) {

			var totalOffsetRef = 0
			var ref = inputs.get(i)
			while (ref instanceof SubBuffer) {
				totalOffsetRef = totalOffsetRef + ref.offset
				ref = ref.container
			}

			val offRef = totalOffsetRef
			val r = ref
			var mergeableBuffers = inputs.filter [
				var totalOffset = 0
				var b = it
				while (b instanceof SubBuffer) {
					totalOffset = totalOffset + b.offset
					b = b.container
				}
				(totalOffset == offRef && b == r)
			]

			if (mergeableBuffers.size > 1) {
				mergeableInputs = new ArrayList<Buffer>
				mergeableInputs.addAll(mergeableBuffers)
			}
		}

		// If there are mergeable inputs
		if (mergeableInputs != null) {
			var mergedBuffer = mergeableInputs.head
			mergeableInputs.remove(0)
			mergeableBuffers.put(mergedBuffer, mergeableInputs)
		}

		// Check if the last input is mergeable with the output
		{
			var totalOffsetOut = 0
			var bOutput = call.outputBuffers.head
			while (bOutput instanceof SubBuffer) {
				totalOffsetOut = totalOffsetOut + bOutput.offset
				bOutput = bOutput.container
			}

			var totalOffsetIn = 0
			var bInput = call.inputBuffers.last
			while (bInput instanceof SubBuffer) {
				totalOffsetIn = totalOffsetIn + bInput.offset
				bInput = bInput.container
			}

			if (totalOffsetIn == totalOffsetOut && bInput == bOutput) {

				// The last input and the output are mergeable
				var out = new ArrayList()
				out.add(call.outputBuffers.head)
				mergeableBuffers.put(call.inputBuffers.last, out)
			}
		}

		''''''
	}
}
