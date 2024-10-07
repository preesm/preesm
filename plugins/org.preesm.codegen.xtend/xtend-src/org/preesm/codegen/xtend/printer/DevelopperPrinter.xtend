/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2016 - 2017)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2017)
 * Leonardo Suriano [leonardo.suriano@upm.es] (2019)
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
package org.preesm.codegen.xtend.printer

import java.util.Collection
import java.util.LinkedHashMap
import java.util.List
import org.preesm.codegen.model.Block
import org.preesm.codegen.model.Buffer
import org.preesm.codegen.model.BufferIterator
import org.preesm.codegen.model.CallBlock
import org.preesm.codegen.model.Communication
import org.preesm.codegen.model.Constant
import org.preesm.codegen.model.ConstantString
import org.preesm.codegen.model.CoreBlock
import org.preesm.codegen.model.FifoCall
import org.preesm.codegen.model.FiniteLoopBlock
import org.preesm.codegen.model.FreeDataTransferBuffer
import org.preesm.codegen.model.FpgaLoadAction
import org.preesm.codegen.model.FunctionCall
import org.preesm.codegen.model.IntVar
import org.preesm.codegen.model.GlobalBufferDeclaration
import org.preesm.codegen.model.LoopBlock
import org.preesm.codegen.model.NullBuffer
import org.preesm.codegen.model.OutputDataTransfer
import org.preesm.codegen.model.PapifyAction
import org.preesm.codegen.model.DataTransferAction
import org.preesm.codegen.model.RegisterSetUpAction
import org.preesm.codegen.model.SharedMemoryCommunication
import org.preesm.codegen.model.SpecialCall
import org.preesm.codegen.model.SubBuffer
import org.preesm.codegen.model.Variable
import org.preesm.codegen.printer.CodegenAbstractPrinter
import org.preesm.codegen.model.DistributedMemoryCommunication
import org.preesm.codegen.model.PapifyFunctionCall
import org.preesm.codegen.model.IteratedBuffer
import org.preesm.codegen.model.ClusterBlock
import org.preesm.codegen.model.SectionBlock
import org.preesm.model.pisdf.Actor

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
	 * @see CodegenAbstractPrinter#createSecondaryFiles(List, List)
	 */
	override createSecondaryFiles(List<Block> printerBlocks, Collection<Block> allBlocks) {
		return new LinkedHashMap<String,CharSequence>
	}

	/**
	 */



	/**
	 * Default implementation: does nothing.
	 * @see CodegenAbstractPrinter#preProcessing(List,List)
	 */
	override preProcessing(List<Block> printerBlocks, Collection<Block> allBlocks) {}

	override postProcessing(CharSequence charSeq) {
		return charSeq;
	}

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

	override printConstantString(ConstantString constant) '''<ConstantString>'''

	override printConstantStringDeclaration(ConstantString constant) '''<ConstantString_Declaration>'''

	override printConstantStringDefinition(ConstantString constant) '''<ConstantString_Definition>'''

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

	override printPapifyFunctionCall(PapifyFunctionCall papifyFunctionCall) '''<Papify_Function_Call>'''

	override printJoin(SpecialCall call) '''<Join>'''

	override printLoopBlockFooter(LoopBlock block) '''<Loop_Block_Foot>'''

	override printLoopBlockHeader(LoopBlock block) '''<Loop_Block_Head>'''

	override printFiniteLoopBlockFooter(FiniteLoopBlock block) '''<Finite_Loop_Block_Foot>'''

	override printFiniteLoopBlockHeader(FiniteLoopBlock block) '''<Finite_Loop_Block_Head>'''

	override printClusterBlockFooter(ClusterBlock block) '''<Cluster_Block_Foot>'''

	override printClusterBlockHeader(ClusterBlock block) '''<Cluster_Block_Head>'''

	override printSectionBlockFooter(SectionBlock block) '''<Section_Block_Foot>'''

	override printSectionBlockHeader(SectionBlock block) '''<Section_Block_Head>'''

	override printNullBuffer(NullBuffer nullBuffer) '''<NullBuffer>'''

	override printNullBufferDeclaration(NullBuffer buffer) '''<NullBuffer_Declaration>'''

	override printNullBufferDefinition(NullBuffer buffer) '''<NullBuffer_Definition>'''

	override printRoundBuffer(SpecialCall call) '''<RoundBuffer>'''

	override printSharedMemoryCommunication(SharedMemoryCommunication communication) '''<Shared_Memory_Communication>'''

	override printDistributedMemoryCommunication(DistributedMemoryCommunication communication) '''<Distributed_Memory_Communication>'''

	override printSpecialCall(SpecialCall specialCall) '''<Special_Call>'''

	override printSubBuffer(SubBuffer subBuffer) '''<SubBuffer>'''

	override printSubBufferDeclaration(SubBuffer buffer) '''<Sub_Buffer_Declaration>'''

	override printSubBufferDefinition(SubBuffer buffer) '''<Sub_Buffer_Definition>'''

	override printIntVar(IntVar intVar) '''<IntVar>'''

	override printIntVarDeclaration(IntVar intVar) '''<IntVar_Declaration>'''

	override printIntVarDefinition(IntVar intVar) '''<IntVar_Definition>'''

	override printBufferIterator(BufferIterator bufferIterator) '''<BufferIterator>'''

	override printBufferIteratorDeclaration(BufferIterator bufferIterator) '''<BufferIterator_Declaration>'''

	override printBufferIteratorDefinition(BufferIterator bufferIterator) '''<BufferIterator_Definition>'''

	override printIteratedBuffer(IteratedBuffer iteratedBuffer) '''<IteratedBuffer>'''

	override printDataTansfer(DataTransferAction action) '''<Data_Transfer>'''

	override printRegisterSetUp(RegisterSetUpAction action) '''<Register_SetUp>'''

	override printFpgaLoad(FpgaLoadAction action) '''<Fpga_Load>'''

	override printFreeDataTransferBuffer(FreeDataTransferBuffer action) '''<Free_Buffer_Data_transfer>'''

	override printGlobalBufferDeclaration(GlobalBufferDeclaration action) '''<Global_Buffer_Declaration>'''

	override printOutputDataTransfer(OutputDataTransfer action) '''<Output_Data_Transfer>'''

	override printPapifyActionDefinition(PapifyAction action) '''<Papify_Action_Definition>'''

	override printPapifyActionParam(PapifyAction action) '''<Papify_Action_Param>'''

	// nothing on pre by default
	override printPreFunctionCall(FunctionCall functionCall) ''''''
	// nothing on post by default
	override printPostFunctionCall(FunctionCall functionCall) ''''''
	
	override generateStandardLibFiles(String path) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override printCoreLoopBlockFooter(LoopBlock loopBlock, int nodeID) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override printCoreLoopBlockHeader(LoopBlock loopBlock, int nodeID) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	



	
	
	
//	override printMainSimsdpHeader(MainSimsdpBlock block, int nodes, String[] nodeID) {
//		throw new UnsupportedOperationException("TODO: auto-generated method stub")
//	}
//	
//	override protected printmpi(MainSimsdpBlock block) {
//		throw new UnsupportedOperationException("TODO: auto-generated method stub")
//	}
//	
//	override generateStandardLibFiles(String path) {
//		throw new UnsupportedOperationException("TODO: auto-generated method stub")
//	}
//	
//	override printCoreLoopBlockFooter(LoopBlock loopBlock, int nodeID) {
//		throw new UnsupportedOperationException("TODO: auto-generated method stub")
//	}
//	
//	override printCoreLoopBlockHeader(LoopBlock loopBlock, int nodeID) {
//		throw new UnsupportedOperationException("TODO: auto-generated method stub")
//	}

}
