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

/**
 * This {@link DevelopperPrinter} is a dummy implementation of the 
 * {@link CodegenAbstractPrinter} where all print methods print
 * "<What_I_Print>". The only purpose of this class is to ease the developer 
 * life by providing him with a dummy implementation that can help him see 
 * what is printed.
 *  
 * @author kdesnos
 */
class DevelopperPrinter extends CodegenAbstractPrinter {
	
	/**
	 * Default implementation: does nothing.
	 * @see CodegenAbstractPrinter#createSecondaryFiles(List)
	 */
	override createSecondaryFiles(List<Block> blocks) {
		return new HashMap<String,CharSequence>
	}
	
	/**
	 * Default implementation: does nothing.
	 * @see CodegenAbstractPrinter#preProcessing(List)
	 */
	override preProcessing(List<Block> blocks) {}
	
	override printBroadcast(SpecialCall call) '''<Broadcast>'''
	
	override printBuffer(Buffer buffer) '''<Buffer>'''

	override printBufferDeclaration(Buffer buffer) '''<Buffer_Decl>'''

	override printBufferDefinition(Buffer buffer) '''<Buffer_Def>'''

	override printCallBlockFooter(CallBlock block) '''<Call_Block_Foot>'''

	override printCallBlockHeader(CallBlock block) '''<Call_Block_Head>'''

	override printCommunication(Communication communication) '''<Communication>'''
	
	override printConstant(Constant constant) '''<Constant>'''

	override printConstantDeclaration(Constant constant) '''<Constant_Declaration>'''

	override printConstantDefinition(Constant constant) '''<Consant_Definition>'''

	override printCoreBlockFooter(CoreBlock block) '''<Core_Block_Foot>'''

	override printCoreBlockHeader(CoreBlock block) '''<Core_Block_Head>'''

	override printCoreInitBlockFooter(CallBlock callBlock) '''<Core_Init_Block_Foot>'''

	override printCoreInitBlockHeader(CallBlock callBlock) '''<Core_Init_Block_Head>'''

	override printCoreLoopBlockFooter(LoopBlock block2) '''<Core_Loop_Block_Foot>'''

	override printCoreLoopBlockHeader(LoopBlock block2) '''<Core_Loop_Block_Head>'''

	override printDeclarationsFooter(List<Variable> list) '''<Declarations_Foot>'''

	override printDeclarationsHeader(List<Variable> list) '''<Declarations_Head>'''

	override printDefinitionsFooter(List<Variable> list) '''<Definitions_Foot>'''

	override printDefinitionsHeader(List<Variable> list) '''<Definitions_Head>'''

	override printFifoCall(FifoCall fifoCall) '''<Fifo_Call>'''
	
	override printFork(SpecialCall call) '''<Fork>'''

	override printFunctionCall(FunctionCall functionCall) '''<Function_Call>'''
	
	override printJoin(SpecialCall call) '''<Join>'''

	override printLoopBlockFooter(LoopBlock block) '''<Loop_Block_Foot>'''

	override printLoopBlockHeader(LoopBlock block) '''<Loop_Block_Head>'''
	
	override printRoundBuffer(SpecialCall call) '''<RoundBuffer>'''
	
	override printSemaphore(Semaphore semaphore) '''<Semaphore>'''
	
	override printSemaphoreDeclaration(Semaphore semaphore) '''<Semaphore_Declaration>'''
	
	override printSemaphoreDefinition(Semaphore semaphore) '''<Semaphore_Definition>'''
	
	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''<Shared_Memory_Communication>'''

	override printSpecialCall(SpecialCall specialCall) '''<Special_Call>'''
	
	override printSubBuffer(SubBuffer subBuffer) '''<SubBuffer>'''

	override printSubBufferDeclaration(SubBuffer buffer) '''<Sub_Buffer_Declaration>'''

	override printSubBufferDefinition(SubBuffer buffer) '''<Sub_Buffer_Definition>'''
	
}
