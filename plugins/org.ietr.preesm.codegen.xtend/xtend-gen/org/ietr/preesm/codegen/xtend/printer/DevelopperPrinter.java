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
package org.ietr.preesm.codegen.xtend.printer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.codegen.xtend.printer.CodegenAbstractPrinter;

/**
 * This {@link DevelopperPrinter} is a dummy implementation of the
 * {@link CodegenAbstractPrinter} where all print methods print
 * "<What_I_Print>". The only purpose of this class is to ease the developer
 * life by providing him with a dummy implementation that can help him see
 * what is printed.
 * 
 * @author kdesnos
 */
@SuppressWarnings("all")
public class DevelopperPrinter extends CodegenAbstractPrinter {
  /**
   * Default implementation: does nothing.
   * @see CodegenAbstractPrinter#createSecondaryFiles(List, List)
   */
  public Map<String,CharSequence> createSecondaryFiles(final List<Block> printerBlocks, final List<Block> allBlocks) {
    HashMap<String,CharSequence> _hashMap = new HashMap<String,CharSequence>();
    return _hashMap;
  }
  
  /**
   * Default implementation: does nothing.
   * @see CodegenAbstractPrinter#preProcessing(List,List)
   */
  public void preProcessing(final List<Block> printerBlocks, final List<Block> allBlocks) {
  }
  
  public CharSequence printBroadcast(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Broadcast>");
    return _builder;
  }
  
  public CharSequence printBuffer(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Buffer>");
    return _builder;
  }
  
  public CharSequence printBufferDeclaration(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Buffer_Decl>");
    return _builder;
  }
  
  public CharSequence printBufferDefinition(final Buffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Buffer_Def>");
    return _builder;
  }
  
  public CharSequence printCallBlockFooter(final CallBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Call_Block_Foot>");
    return _builder;
  }
  
  public CharSequence printCallBlockHeader(final CallBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Call_Block_Head>");
    return _builder;
  }
  
  public CharSequence printCommunication(final Communication communication) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Communication>");
    return _builder;
  }
  
  public CharSequence printConstant(final Constant constant) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Constant>");
    return _builder;
  }
  
  public CharSequence printConstantDeclaration(final Constant constant) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Constant_Declaration>");
    return _builder;
  }
  
  public CharSequence printConstantDefinition(final Constant constant) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Consant_Definition>");
    return _builder;
  }
  
  public CharSequence printConstantString(final ConstantString constant) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<ConstantString>");
    return _builder;
  }
  
  public CharSequence printConstantStringDeclaration(final ConstantString constant) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<ConstantString_Declaration>");
    return _builder;
  }
  
  public CharSequence printConstantStringDefinition(final ConstantString constant) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<ConstantString_Definition>");
    return _builder;
  }
  
  public CharSequence printCoreBlockFooter(final CoreBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Core_Block_Foot>");
    return _builder;
  }
  
  public CharSequence printCoreBlockHeader(final CoreBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Core_Block_Head>");
    return _builder;
  }
  
  public CharSequence printCoreInitBlockFooter(final CallBlock callBlock) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Core_Init_Block_Foot>");
    return _builder;
  }
  
  public CharSequence printCoreInitBlockHeader(final CallBlock callBlock) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Core_Init_Block_Head>");
    return _builder;
  }
  
  public CharSequence printCoreLoopBlockFooter(final LoopBlock block2) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Core_Loop_Block_Foot>");
    return _builder;
  }
  
  public CharSequence printCoreLoopBlockHeader(final LoopBlock block2) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Core_Loop_Block_Head>");
    return _builder;
  }
  
  public CharSequence printDeclarationsFooter(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Declarations_Foot>");
    return _builder;
  }
  
  public CharSequence printDeclarationsHeader(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Declarations_Head>");
    return _builder;
  }
  
  public CharSequence printDefinitionsFooter(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Definitions_Foot>");
    return _builder;
  }
  
  public CharSequence printDefinitionsHeader(final List<Variable> list) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Definitions_Head>");
    return _builder;
  }
  
  public CharSequence printFifoCall(final FifoCall fifoCall) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Fifo_Call>");
    return _builder;
  }
  
  public CharSequence printFork(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Fork>");
    return _builder;
  }
  
  public CharSequence printFunctionCall(final FunctionCall functionCall) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Function_Call>");
    return _builder;
  }
  
  public CharSequence printJoin(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Join>");
    return _builder;
  }
  
  public CharSequence printLoopBlockFooter(final LoopBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Loop_Block_Foot>");
    return _builder;
  }
  
  public CharSequence printLoopBlockHeader(final LoopBlock block) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Loop_Block_Head>");
    return _builder;
  }
  
  public CharSequence printRoundBuffer(final SpecialCall call) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<RoundBuffer>");
    return _builder;
  }
  
  public CharSequence printSemaphore(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Semaphore>");
    return _builder;
  }
  
  public CharSequence printSemaphoreDeclaration(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Semaphore_Declaration>");
    return _builder;
  }
  
  public CharSequence printSemaphoreDefinition(final Semaphore semaphore) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Semaphore_Definition>");
    return _builder;
  }
  
  public CharSequence printSharedMemoryCommunication(final SharedMemoryCommunication communication) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Shared_Memory_Communication>");
    return _builder;
  }
  
  public CharSequence printSpecialCall(final SpecialCall specialCall) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Special_Call>");
    return _builder;
  }
  
  public CharSequence printSubBuffer(final SubBuffer subBuffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<SubBuffer>");
    return _builder;
  }
  
  public CharSequence printSubBufferDeclaration(final SubBuffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Sub_Buffer_Declaration>");
    return _builder;
  }
  
  public CharSequence printSubBufferDefinition(final SubBuffer buffer) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<Sub_Buffer_Definition>");
    return _builder;
  }
}
