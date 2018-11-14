package org.ietr.preesm.codegen.printer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.preesm.codegen.model.codegen.Block;
import org.ietr.preesm.codegen.model.codegen.Buffer;
import org.ietr.preesm.codegen.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.model.codegen.CallBlock;
import org.ietr.preesm.codegen.model.codegen.Communication;
import org.ietr.preesm.codegen.model.codegen.Constant;
import org.ietr.preesm.codegen.model.codegen.ConstantString;
import org.ietr.preesm.codegen.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.model.codegen.FifoCall;
import org.ietr.preesm.codegen.model.codegen.FiniteLoopBlock;
import org.ietr.preesm.codegen.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.model.codegen.IntVar;
import org.ietr.preesm.codegen.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.model.codegen.NullBuffer;
import org.ietr.preesm.codegen.model.codegen.PapifyAction;
import org.ietr.preesm.codegen.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.model.codegen.Variable;

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
