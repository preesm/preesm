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

import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Communication
import org.ietr.preesm.codegen.xtend.model.codegen.Constant
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.Variable
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import java.util.HashMap
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
import org.ietr.preesm.codegen.xtend.model.codegen.IntVar
import org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator

/**
 * This {@link DefaultPrinter} is a dummy implementation of the 
 * {@link CodegenAbstractPrinter} where all print methods print nothing.
 * The only purpose of this class is to ease the developer life by making
 * it possible to create a new printer simply by implementing print methods
 * that actually print something.
 * @author kdesnos
 */
class DefaultPrinter extends CodegenAbstractPrinter {
	
	/**
	 * Default implementation: does nothing.
	 * @see CodegenAbstractPrinter#createSecondaryFiles(List,List)
	 */
	override createSecondaryFiles(List<Block> printerBlocks, List<Block> allBlocks) {
		return new HashMap<String,CharSequence>
	}
	
	/**
	 * Default implementation: does nothing.
	 * @see CodegenAbstractPrinter#preProcessing(List,List)
	 */
	override preProcessing(List<Block> printerBlocks, List<Block> allBlocks) {}
	
	/**
	 * Default implementation: does nothing.
	 * @see CodegenAbstractPrinter#postProcessing(CharSequence)
	 */
	override postProcessing(CharSequence charSequence) {
		return charSequence;
	}
	
	override printBroadcast(SpecialCall call) ''''''

	override printBuffer(Buffer buffer) ''''''

	override printBufferDeclaration(Buffer buffer) ''''''

	override printBufferDefinition(Buffer buffer) ''''''

	override printCallBlockFooter(CallBlock block) ''''''

	override printCallBlockHeader(CallBlock block) ''''''

	override printCommunication(Communication communication) ''''''

	override printConstant(Constant constant) ''''''

	override printConstantDeclaration(Constant constant) ''''''

	override printConstantDefinition(Constant constant) ''''''
	
	override printConstantString(ConstantString constant) ''''''

	override printConstantStringDeclaration(ConstantString constant) ''''''

	override printConstantStringDefinition(ConstantString constant) ''''''

	override printIntVar(IntVar intVar) ''''''

	override printIntVarDeclaration(IntVar intVar) ''''''

	override printIntVarDefinition(IntVar intVar) ''''''

	override printCoreBlockFooter(CoreBlock block) ''''''

	override printCoreBlockHeader(CoreBlock block) ''''''

	override printCoreInitBlockFooter(CallBlock callBlock) ''''''

	override printCoreInitBlockHeader(CallBlock callBlock) ''''''

	override printCoreLoopBlockFooter(LoopBlock block2) ''''''

	override printCoreLoopBlockHeader(LoopBlock block2) ''''''

	override printDeclarationsFooter(List<Variable> list) ''''''

	override printDeclarationsHeader(List<Variable> list) ''''''

	override printDefinitionsFooter(List<Variable> list) ''''''

	override printDefinitionsHeader(List<Variable> list) ''''''

	override printFifoCall(FifoCall fifoCall) ''''''
	
	override printFork(SpecialCall call) ''''''

	override printFunctionCall(FunctionCall functionCall) ''''''
	
	override printJoin(SpecialCall call) ''''''

	override printLoopBlockFooter(LoopBlock block) ''''''

	override printLoopBlockHeader(LoopBlock block) ''''''

	override printFiniteLoopBlockFooter(FiniteLoopBlock block) ''''''

	override printFiniteLoopBlockHeader(FiniteLoopBlock block) ''''''
	
	override printNullBuffer(NullBuffer Buffer) ''''''

	override printNullBufferDeclaration(NullBuffer buffer) ''''''

	override printNullBufferDefinition(NullBuffer buffer) ''''''
	
	override printRoundBuffer(SpecialCall call) ''''''
	
	override printSemaphore(Semaphore semaphore) ''''''
	
	override printSemaphoreDeclaration(Semaphore semaphore) ''''''
	
	override printSemaphoreDefinition(Semaphore semaphore) ''''''
	
	override printSharedMemoryCommunication(SharedMemoryCommunication communication) ''''''

	override printSpecialCall(SpecialCall specialCall) ''''''

	override printSubBuffer(SubBuffer subBuffer) ''''''

	override printSubBufferDeclaration(SubBuffer buffer) ''''''

	override printSubBufferDefinition(SubBuffer buffer) ''''''
	
	override printBufferIterator(BufferIterator bufferIterator) ''''''

	override printBufferIteratorDeclaration(BufferIterator bufferIterator) ''''''

	override printBufferIteratorDefinition(BufferIterator bufferIterator) ''''''
	
}
