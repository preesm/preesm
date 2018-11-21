/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.codegen.printer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.BufferIterator;
import org.preesm.codegen.model.CallBlock;
import org.preesm.codegen.model.Communication;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.ConstantString;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.NullBuffer;
import org.preesm.codegen.model.PapifyAction;
import org.preesm.codegen.model.SharedMemoryCommunication;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;

/**
 * This {@link DefaultPrinter} is a dummy implementation of the {@link CodegenAbstractPrinter} where all print methods
 * print nothing. The only purpose of this class is to ease the developer life by making it possible to create a new
 * printer simply by implementing print methods that actually print something.
 *
 * @author kdesnos
 */
public class DefaultPrinter extends CodegenAbstractPrinter {

  /**
   * Default implementation: does nothing.
   *
   * @see CodegenAbstractPrinter#createSecondaryFiles(List,List)
   */
  public Map<String, CharSequence> createSecondaryFiles(List<Block> printerBlocks, Collection<Block> allBlocks) {
    return new LinkedHashMap<>();
  }

  /**
   */
  public Map<String, CharSequence> generateStandardLibFiles() {
    return new LinkedHashMap<>();
  }

  public CharSequence printBroadcast(SpecialCall call) {
    return "";
  }

  public CharSequence printBuffer(Buffer buffer) {
    return "";
  }

  public CharSequence printBufferDeclaration(Buffer buffer) {
    return "";
  }

  public CharSequence printBufferDefinition(Buffer buffer) {
    return "";
  }

  public CharSequence printCallBlockFooter(CallBlock block) {
    return "";
  }

  public CharSequence printCallBlockHeader(CallBlock block) {
    return "";
  }

  public CharSequence printCommunication(Communication communication) {
    return "";
  }

  public CharSequence printConstant(Constant constant) {
    return "";
  }

  public CharSequence printPapifyAction(PapifyAction action) {
    return "";
  }

  public CharSequence printConstantDeclaration(Constant constant) {
    return "";
  }

  public CharSequence printConstantDefinition(Constant constant) {
    return "";
  }

  public CharSequence printConstantString(ConstantString constant) {
    return "";
  }

  public CharSequence printConstantStringDeclaration(ConstantString constant) {
    return "";
  }

  public CharSequence printConstantStringDefinition(ConstantString constant) {
    return "";
  }

  public CharSequence printIntVar(IntVar intVar) {
    return "";
  }

  public CharSequence printIntVarDeclaration(IntVar intVar) {
    return "";
  }

  public CharSequence printIntVarDefinition(IntVar intVar) {
    return "";
  }

  public CharSequence printCoreBlockFooter(CoreBlock block) {
    return "";
  }

  public CharSequence printCoreBlockHeader(CoreBlock block) {
    return "";
  }

  public CharSequence printCoreInitBlockFooter(CallBlock callBlock) {
    return "";
  }

  public CharSequence printCoreInitBlockHeader(CallBlock callBlock) {
    return "";
  }

  public CharSequence printCoreLoopBlockFooter(LoopBlock block2) {
    return "";
  }

  public CharSequence printCoreLoopBlockHeader(LoopBlock block2) {
    return "";
  }

  public CharSequence printDeclarationsFooter(List<Variable> list) {
    return "";
  }

  public CharSequence printDeclarationsHeader(List<Variable> list) {
    return "";
  }

  public CharSequence printDefinitionsFooter(List<Variable> list) {
    return "";
  }

  public CharSequence printDefinitionsHeader(List<Variable> list) {
    return "";
  }

  public CharSequence printFifoCall(FifoCall fifoCall) {
    return "";
  }

  public CharSequence printFork(SpecialCall call) {
    return "";
  }

  public CharSequence printFunctionCall(FunctionCall functionCall) {
    return "";
  }

  public CharSequence printJoin(SpecialCall call) {
    return "";
  }

  public CharSequence printLoopBlockFooter(LoopBlock block) {
    return "";
  }

  public CharSequence printLoopBlockHeader(LoopBlock block) {
    return "";
  }

  public CharSequence printFiniteLoopBlockFooter(FiniteLoopBlock block) {
    return "";
  }

  public CharSequence printFiniteLoopBlockHeader(FiniteLoopBlock block) {
    return "";
  }

  public CharSequence printNullBuffer(NullBuffer buffer) {
    return "";
  }

  public CharSequence printNullBufferDeclaration(NullBuffer buffer) {
    return "";
  }

  public CharSequence printNullBufferDefinition(NullBuffer buffer) {
    return "";
  }

  public CharSequence printRoundBuffer(SpecialCall call) {
    return "";
  }

  public CharSequence printSharedMemoryCommunication(SharedMemoryCommunication communication) {
    return "";
  }

  public CharSequence printSpecialCall(SpecialCall specialCall) {
    return "";
  }

  public CharSequence printSubBuffer(SubBuffer subBuffer) {
    return "";
  }

  public CharSequence printSubBufferDeclaration(SubBuffer buffer) {
    return "";
  }

  public CharSequence printSubBufferDefinition(SubBuffer buffer) {
    return "";
  }

  public CharSequence printBufferIterator(BufferIterator bufferIterator) {
    return "";
  }

  public CharSequence printBufferIteratorDeclaration(BufferIterator bufferIterator) {
    return "";
  }

  public CharSequence printBufferIteratorDefinition(BufferIterator bufferIterator) {
    return "";
  }

}
