/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2017 - 2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2016)
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
package org.ietr.preesm.codegen.printer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.ietr.preesm.codegen.model.codegen.Block;
import org.ietr.preesm.codegen.model.codegen.Buffer;
import org.ietr.preesm.codegen.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.model.codegen.CallBlock;
import org.ietr.preesm.codegen.model.codegen.CodeElt;
import org.ietr.preesm.codegen.model.codegen.CodegenPackage;
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
import org.ietr.preesm.codegen.model.codegen.SpecialType;
import org.ietr.preesm.codegen.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.model.codegen.Variable;
import org.ietr.preesm.codegen.model.codegen.util.CodegenSwitch;
import org.ietr.preesm.codegen.xtend.task.CodegenEngine;
import org.ietr.preesm.codegen.xtend.task.CodegenException;

/**
 * The {@link CodegenAbstractPrinter} is used to visit a {@link CodegenPackage Codegen model}. To use a printer, the
 * following function calls should be used:<br>
 * 1. Call {@link #preProcessing(List)} on a {@link List} containing all printed {@link Block blocks}.<br>
 * 2. Call {@link #doSwitch()} on each {@link Block} to print.
 *
 * @author kdesnos
 *
 */
public abstract class CodegenAbstractPrinter extends CodegenSwitch<CharSequence> {

  PrinterState state = PrinterState.IDLE;

  /**
   * Method used to change the current state of the printer.
   *
   * @param newState
   *          the new State of the printer
   *
   */
  protected void setState(final PrinterState newState) {
    this.state = newState;
  }

  /**
   * True if the visitor is currently printing {@link Variable} {@link CoreBlock#getDefinitions() definitions}
   */
  protected boolean printingDefinitions = false;

  /**
   * True if the visitor is currently printing {@link Variable} {@link CoreBlock#getDeclarations() declarations}
   */
  protected boolean printingDeclarations = false;

  /**
   * True if the visitor is currently printing the {@link CallBlock} {@link CoreBlock#getInitBlock() initBlock}
   */
  protected boolean printingInitBlock = false;

  /**
   * True if the visitor is currently printing the {@link LoopBlock} {@link CoreBlock#getInitBlock() initBlock}
   */
  protected boolean printingLoopBlock = false;

  /**
   * Reference to the {@link CoreBlock} currently printed
   */
  private CoreBlock printedCoreBlock;

  private CodegenEngine engine;

  @Override
  public CharSequence caseCommunication(final Communication communication) {
    return printCommunication(communication);
  }

  @Override
  public CharSequence caseSharedMemoryCommunication(final SharedMemoryCommunication communication) {
    return printSharedMemoryCommunication(communication);
  }

  @Override
  public CharSequence caseCoreBlock(final CoreBlock coreBlock) {

    // The coreBlock is composed of Blocks
    // Each block is printed in the following way
    // <BlockHeader><BlockContent><BlockFooter>
    // The the BlockContent may itself contain Block(s)
    //
    // The Coreblock is structured as follow:
    // CoreBlock
    // - DeclarationBlock
    // - DefinitionBlock
    // - CoreInitBlock
    // - CoreLoopBlock
    //
    // The indentation applied to all lines of a BlockContent is determined
    // by the last line of the corresponding BlockHeader:
    // - If the BlockHeader ends with an empty line that contains n
    // tabulations, the indentation applied will be n tabulations in
    // addition to the indentation of the blockHeader
    // - If the BlockHeader ends with a line containing something else
    // than tabulations, there will be no indentation (except the
    // one of the blockHeader that will also be applied to its content)
    //
    // A line is skipped between the BlockContent and the BlockFooter
    // if the last line of the BlockHeader is an empty line
    // (with only spaces or tabulations characters)
    //
    // String concatenation is done manually because of complex
    // tabulation handling
    StringConcatenation result = new StringConcatenation();
    final CharSequence coreBlockHeader = printCoreBlockHeader(coreBlock);
    result.append(coreBlockHeader);
    final String indentationCoreBlock = (coreBlockHeader.length() > 0)
        ? CodegenAbstractPrinter.getLastLineIndentation(result)
        : "";
    boolean coreBlockHasNewLine;
    if (coreBlockHeader.length() > 0) {
      result = CodegenAbstractPrinter.trimLastEOL(result);
      coreBlockHasNewLine = CodegenAbstractPrinter.endWithEOL(result);
    } else {
      coreBlockHasNewLine = false;
    }
    String indentation;
    boolean hasNewLine;

    // Visit Declarations
    {
      setState(PrinterState.PRINTING_DECLARATIONS);

      final List<Variable> declsNotDefs = new ArrayList<>();
      final EList<Variable> declarations = coreBlock.getDeclarations();
      for (final Variable v : declarations) {
        if (!coreBlock.getDefinitions().contains(v)) {
          declsNotDefs.add(v);
        }
      }
      final CharSequence declarationsHeader = printDeclarationsHeader(declsNotDefs);

      result.append(declarationsHeader, indentationCoreBlock);
      if (declarationsHeader.length() > 0) {
        indentation = CodegenAbstractPrinter.getLastLineIndentation(result);
        result = CodegenAbstractPrinter.trimLastEOL(result);
        hasNewLine = CodegenAbstractPrinter.endWithEOL(result);
      } else {
        indentation = indentationCoreBlock;
        hasNewLine = false;
      }

      for (final Variable v : declsNotDefs) {
        final CharSequence code = doSwitch(v);
        result.append(code, indentation);
      }

      if (hasNewLine) {
        result.newLineIfNotEmpty();
        result.append(indentationCoreBlock);
      }
      result.append(printDeclarationsFooter(declarations), indentationCoreBlock);
    }

    // Visit Definitions
    {
      setState(PrinterState.PRINTING_DEFINITIONS);
      final EList<Variable> definitions = coreBlock.getDefinitions();
      final CharSequence definitionsHeader = printDefinitionsHeader(definitions);
      result.append(definitionsHeader, indentationCoreBlock);
      if (definitionsHeader.length() > 0) {
        indentation = CodegenAbstractPrinter.getLastLineIndentation(result);
        result = CodegenAbstractPrinter.trimLastEOL(result);
        hasNewLine = CodegenAbstractPrinter.endWithEOL(result);
      } else {
        indentation = indentationCoreBlock;
        hasNewLine = false;
      }

      // result.append(coreBlock.definitions.map[doSwitch].join(''), indentation)
      for (final Variable v : definitions) {
        final CharSequence code = doSwitch(v);
        result.append(code, indentation);
      }

      if (hasNewLine) {
        result.newLineIfNotEmpty();
        result.append(indentationCoreBlock);
      }
      result.append(printDefinitionsFooter(definitions), indentationCoreBlock);
    }

    // Visit init block
    {
      setState(PrinterState.PRINTING_INIT_BLOCK);
      final CharSequence coreInitHeader = printCoreInitBlockHeader(coreBlock.getInitBlock());
      result.append(coreInitHeader, indentationCoreBlock);
      if (coreInitHeader.length() > 0) {
        indentation = CodegenAbstractPrinter.getLastLineIndentation(result);
        result = CodegenAbstractPrinter.trimLastEOL(result);
        hasNewLine = CodegenAbstractPrinter.endWithEOL(result);
      } else {
        indentation = indentationCoreBlock;
        hasNewLine = false;
      }
      result.append(doSwitch(coreBlock.getInitBlock()), indentation);
      if (hasNewLine) {
        result.newLineIfNotEmpty();
        result.append(indentationCoreBlock);
      }
      result.append(printCoreInitBlockFooter(coreBlock.getInitBlock()), indentationCoreBlock);
    }

    // Visit loop block
    {
      setState(PrinterState.PRINTING_LOOP_BLOCK);
      final CharSequence coreLoopHeader = printCoreLoopBlockHeader(coreBlock.getLoopBlock());
      result.append(coreLoopHeader, indentationCoreBlock);
      if (coreLoopHeader.length() > 0) {
        indentation = CodegenAbstractPrinter.getLastLineIndentation(result);
        result = CodegenAbstractPrinter.trimLastEOL(result);
        hasNewLine = CodegenAbstractPrinter.endWithEOL(result);
      } else {
        indentation = indentationCoreBlock;
        hasNewLine = false;
      }
      result.append(doSwitch(coreBlock.getLoopBlock()), indentation);
      if (hasNewLine) {
        result.newLineIfNotEmpty();
        result.append(indentationCoreBlock);
      }
      result.append(printCoreLoopBlockFooter(coreBlock.getLoopBlock()), indentationCoreBlock);
    }

    if (coreBlockHasNewLine) {
      result.newLineIfNotEmpty();
    }
    result.append(printCoreBlockFooter(coreBlock));
    return result;
  }

  /**
   * Returns <code>True</code> if the {@link StringConcatenation} ends with an empty line. (i.e. it ends with a \n)
   *
   * @param concatenation
   *          the {@link StringConcatenation} to test
   * @return <code>True</code> if the {@link StringConcatenation} ends with an empty line. (i.e. it ends with a \n)
   */
  static boolean endWithEOL(final StringConcatenation concatenation) {
    final char n = '\n';
    return concatenation.charAt(concatenation.length() - 1) == n;
  }

  /**
   * Returns a copy of the input {@link StringConcatenation}. If the final line of the {@link StringConcatenation} is
   * empty, the last "\r\n" (or "\n") are removed from the returned {@link StringConcatenation}, else the input
   * {@link StringConcatenation} is returned as is
   *
   * @param sequence
   *          the {@link StringConcatenation} to process
   * @return the input {@link StringConcatenation} as is or without its final "\r\n" or "\n"
   */
  static StringConcatenation trimLastEOL(final StringConcatenation sequence) {
    String result = sequence.toString();
    final char newLine = '\n';
    final char r = '\r';

    // if the last character is a \n, remove it
    if ((result.length() > 0) && (result.charAt(result.length() - 1) == newLine)) {
      result = result.subSequence(0, result.length() - 1).toString();
      if ((result.length() > 0) && (result.charAt(result.length() - 1) == r)) {
        result = result.subSequence(0, result.length() - 1).toString();
      }
    }

    final StringConcatenation res = new StringConcatenation();
    res.append(result);
    return res;
  }

  /**
   * Get a {@link String} that corresponds to the the number of tabulations that form the last line of the input
   * {@link CharSequence}. If the {@link CharSequence} ends with an '\n' or with a '\r\n' {@link String}, the
   * penultimate line is processed instead. If the last line contains something else than tabulations, an empty
   * {@link String} is returned<br>
   * <br>
   * Examples:<br>
   * "<code>I'm a line of
   * <br>   code</code>" <br>
   * returns ''<br>
   * <br>
   * "<code>I'm another line of code
   * <br>\t\t</code>" <br>
   * returns '\t\t' <br>
   * <br>
   * "<code>I'm a last line of code
   * <br>\t\t<br>
   * </code>" <br>
   * returns '\t\t'
   *
   * @param input
   *          the processed {@link CharSequence}
   * @return the {@link String} containing only '\t' or nothing
   */
  static String getLastLineIndentation(final CharSequence input) {
    final char newLine = '\n';
    final char tab = '\t';
    final char r = '\r';

    // First, find the beginning of the last line
    int lastLineBeginning = input.length() - 1;
    int lastLineIndentationEnd;
    boolean exitLoop = false;

    // If the input ends with an EOL, skip it
    if ((lastLineBeginning >= 0) && (input.charAt(lastLineBeginning) == newLine)) {
      lastLineBeginning = lastLineBeginning - 1;
      if ((lastLineBeginning >= 0) && (input.charAt(lastLineBeginning) == r)) {
        lastLineBeginning = lastLineBeginning - 1;
      }
    }

    lastLineIndentationEnd = lastLineBeginning + 1;

    while ((lastLineBeginning >= 0) && !exitLoop) {
      // look for \n (works also for \r\n) but only if characters are \t
      final char currentChar = input.charAt(lastLineBeginning);

      if (currentChar == newLine) {
        lastLineBeginning = lastLineBeginning + 1;
        exitLoop = true;
      } else {
        if (currentChar != tab) {
          return "";
        }
        lastLineBeginning = lastLineBeginning - 1;
      }
    }
    return input.subSequence(lastLineBeginning, lastLineIndentationEnd).toString();
  }

  @Override
  public CharSequence caseBuffer(final Buffer buffer) {
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printBufferDefinition(buffer);
    }

    if (this.state.equals(PrinterState.PRINTING_DECLARATIONS)) {
      return printBufferDeclaration(buffer);
    }

    return printBuffer(buffer);
  }

  @Override
  public CharSequence caseCallBlock(final CallBlock callBlock) {
    final StringConcatenation result = new StringConcatenation();

    result.append(printCallBlockHeader(callBlock));

    // Visit all codeElements
    final EList<CodeElt> codeElts = callBlock.getCodeElts();
    for (final CodeElt codeElt : codeElts) {
      final CharSequence code = doSwitch(codeElt);
      result.append(code);
    }

    result.append(printCallBlockFooter(callBlock));

    return result;
  }

  @Override
  public CharSequence caseConstant(final Constant constant) {
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printConstantDefinition(constant);
    }

    if (this.state.equals(PrinterState.PRINTING_DECLARATIONS)) {
      return printConstantDeclaration(constant);
    }

    return printConstant(constant);
  }

  @Override
  public CharSequence casePapifyAction(final PapifyAction action) {
    return printPapifyAction(action);
  }

  @Override
  public CharSequence caseConstantString(final ConstantString constant) {
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printConstantStringDefinition(constant);
    }

    if (this.state.equals(PrinterState.PRINTING_DECLARATIONS)) {
      return printConstantStringDeclaration(constant);
    }

    return printConstantString(constant);
  }

  @Override
  public CharSequence caseIntVar(final IntVar intVar) {
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printIntVarDefinition(intVar);
    }

    if (this.state.equals(PrinterState.PRINTING_DECLARATIONS)) {
      return printIntVarDeclaration(intVar);
    }

    return printIntVar(intVar);
  }

  @Override
  public CharSequence defaultCase(final EObject object) {
    throw new CodegenException(
        "Object " + object + " is not supported by the printer" + this + "in its current state. ");
  }

  /**
   * Get the current {@link PrinterState} of the printer
   */
  public PrinterState getState() {
    return this.state;
  }

  @Override
  public CharSequence caseFifoCall(final FifoCall fifoCall) {
    return printFifoCall(fifoCall);
  }

  @Override
  public CharSequence caseFunctionCall(final FunctionCall functionCall) {
    return printFunctionCall(functionCall);
  }

  @Override
  public CharSequence caseLoopBlock(final LoopBlock loopBlock) {
    final StringConcatenation result = new StringConcatenation();

    result.append(printLoopBlockHeader(loopBlock));

    // Visit all codeElements
    final EList<CodeElt> codeElts = loopBlock.getCodeElts();
    for (final CodeElt codeElt : codeElts) {
      final CharSequence code = doSwitch(codeElt);
      result.append(code);
    }

    final CharSequence printLoopBlockFooter2 = printLoopBlockFooter(loopBlock);
    result.append(printLoopBlockFooter2);

    return result;
  }

  @Override
  public CharSequence caseFiniteLoopBlock(final FiniteLoopBlock loopBlock) {
    StringConcatenation result = new StringConcatenation();
    String indentation = "";
    boolean hasNewLine;

    final CharSequence finiteLoopBlockheader = printFiniteLoopBlockHeader(loopBlock);
    result.append(finiteLoopBlockheader, indentation);

    if (finiteLoopBlockheader.length() > 0) {
      indentation = CodegenAbstractPrinter.getLastLineIndentation(result);
      result = CodegenAbstractPrinter.trimLastEOL(result);
      hasNewLine = CodegenAbstractPrinter.endWithEOL(result);
    } else {
      hasNewLine = false;
    }

    // Visit all codeElements
    final EList<CodeElt> codeElts = loopBlock.getCodeElts();
    for (final CodeElt codeElt : codeElts) {
      final CharSequence code = doSwitch(codeElt);
      result.append(code, indentation);
    }

    if (hasNewLine) {
      result.newLineIfNotEmpty();
      result.append(indentation);
    }

    result.append(printFiniteLoopBlockFooter(loopBlock), "");

    return result;
  }

  @Override
  public CharSequence caseNullBuffer(final NullBuffer nullBuffer) {
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printNullBufferDefinition(nullBuffer);
    }

    if (this.state.equals(PrinterState.PRINTING_DECLARATIONS)) {
      return printNullBufferDeclaration(nullBuffer);
    }

    return printNullBuffer(nullBuffer);
  }

  @Override
  public CharSequence caseSpecialCall(final SpecialCall specialCall) {
    CharSequence result;
    switch (specialCall.getType()) {
      case FORK:
        result = printFork(specialCall);
        break;
      case JOIN:
        result = printJoin(specialCall);
        break;
      case BROADCAST:
        result = printBroadcast(specialCall);
        break;
      case ROUND_BUFFER:
        result = printRoundBuffer(specialCall);
        break;
      default:
        result = printSpecialCall(specialCall);
    }
    return result;
  }

  @Override
  public CharSequence caseSubBuffer(final SubBuffer subBuffer) {
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printSubBufferDefinition(subBuffer);
    }

    if (this.state.equals(PrinterState.PRINTING_DECLARATIONS)) {
      return printSubBufferDeclaration(subBuffer);
    }

    return printSubBuffer(subBuffer);
  }

  @Override
  public CharSequence caseBufferIterator(final BufferIterator bufferIterator) {
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printBufferIteratorDefinition(bufferIterator);
    }

    if (this.state.equals(PrinterState.PRINTING_DECLARATIONS)) {
      return printBufferIteratorDeclaration(bufferIterator);
    }

    return printBufferIterator(bufferIterator);
  }

  /**
   * Method called before printing a set of {@link Block blocks}. This method can perform some printer specific
   * modification on the blocks passed as parameters. For example, it can be used to insert instrumentation primitives
   * in the code. This method will NOT print the code of the {@link Block blocks}, use {@link #doSwitch()} on each
   * {@link Block} to print after the pre-processing to do so.
   *
   * @param printerBlocks
   *          The list of {@link Block blocks} that will be printer by the printer
   * @param allBlocks
   *          The list of all {@link Block blocks} printed during a workflow execution. This list includes all
   *          printerBlocks
   */
  public abstract void preProcessing(List<Block> printerBlocks, Collection<Block> allBlocks);

  /**
   * Method called after printing a set of {@link Block blocks}. This method can perform some printer specific
   * modification on the blocks passed as parameters. For example, it can be used to insert instrumentation primitives
   * in the code. This method will NOT print the code of the {@link Block blocks}, use {@link #doSwitch()} on each
   * {@link Block} to print after the pre-processing to do so.
   *
   */
  public abstract CharSequence postProcessing(CharSequence charSeq);

  /**
   * This method is called after all the {@link Block blocks} have been printed by the printer to give the opportunity
   * to print secondary files. (eg. project files, main files, ...).<br>
   * This method returns a {@link Map} where each {@link Entry} associates a {@link String} to a {@link CharSequence}
   * respectively corresponding to a file name (including the extension) and the its content.
   *
   * @param printerBlocks
   *          The list of {@link Block blocks} that were printed by the printer
   * @param allBlocks
   *          The list of all {@link Block blocks} printed during a workflow execution. This list includes all
   *          printerBlocks
   */
  public abstract Map<String, CharSequence> createSecondaryFiles(List<Block> printerBlocks,
      Collection<Block> allBlocks);

  /**
   * Method called to print a {@link SpecialCall} with {@link SpecialCall#getType() type} {@link SpecialType#BROADCAST}.
   * If this method returns <code>null</code>, the result of {@link #printSpecialCall(SpecialCall) } will be used
   * instead (in case the method is called through doSwitch).
   *
   * @param call
   *          the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBroadcast(SpecialCall call);

  /**
   * Method called to print a {@link Buffer} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param buffer
   *          the {@link Buffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBuffer(Buffer buffer);

  /**
   * Method called to print a {@link Buffer} within the {@link CoreBlock#getDeclarations() declaration} of a
   * {@link CoreBlock}. If a {@link Buffer} was defined in the current block, it will not be declared.
   *
   * @param buffer
   *          the {@link Buffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBufferDeclaration(Buffer buffer);

  /**
   * Method called to print a {@link Buffer} within the {@link CoreBlock#getDefinitions() definition} {@link LoopBlock}
   * of a {@link CoreBlock}
   *
   * @param buffer
   *          the {@link Buffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBufferDefinition(Buffer buffer);

  /**
   * Method called after printing all {@link CodeElt} belonging to a {@link CallBlock}.
   *
   * @param callBlock
   *          the {@link CallBlock} whose {@link CodeElt} were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCallBlockFooter(CallBlock callBlock);

  /**
   * Method called before printing all {@link CodeElt} belonging to a {@link CallBlock}.
   *
   * @param block
   *          the {@link CallBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCallBlockHeader(CallBlock block);

  /**
   * Method called to print a {@link Communication}.
   *
   * @param communication
   *          the printed {@link Communication}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCommunication(Communication communication);

  /**
   * Method called to print a {@link Constant} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param constant
   *          the {@link Constant} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstant(Constant constant);

  /**
   * Method called to print a {@link PapifyAction} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param action
   *          the {@link PapifyAction} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printPapifyAction(PapifyAction action);

  /**
   * Method called to print a {@link Constant} within the {@link CoreBlock#getDeclarations() declaration}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param constant
   *          the {@link Constant} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantDeclaration(Constant constant);

  /**
   * Method called to print a {@link Constant} within the {@link CoreBlock#getDefinitions() definition}
   * {@link LoopBlock} of a {@link CoreBlock}
   *
   * @param constant
   *          the {@link Constant} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantDefinition(Constant constant);

  /**
   * Method called to print a {@link ConstantString} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param constant
   *          the {@link ConstantString} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantString(ConstantString constant);

  /**
   * Method called to print a {@link ConstantString} within the {@link CoreBlock#getDeclarations() declaration}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param constant
   *          the {@link ConstantString} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantStringDeclaration(ConstantString constant);

  /**
   * Method called to print a {@link ConstantString} within the {@link CoreBlock#getDefinitions() definition}
   * {@link LoopBlock} of a {@link CoreBlock}
   *
   * @param constant
   *          the {@link ConstantString} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printConstantStringDefinition(ConstantString constant);

  /**
   * Method called to print a {@link IntVar} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param intVar
   *          the {@link IntVar} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printIntVar(IntVar intVar);

  /**
   * Method called to print a {@link IntVar} within the {@link CoreBlock#getDeclarations() declaration}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param intVar
   *          the {@link IntVar} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printIntVarDeclaration(IntVar intVar);

  /**
   * Method called to print a {@link IntVar} within the {@link CoreBlock#getDefinitions() definition} {@link LoopBlock}
   * of a {@link CoreBlock}
   *
   * @param intVar
   *          the {@link IntVar} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printIntVarDefinition(IntVar intVar);

  /**
   * Method called after printing all code belonging to a {@link CoreBlock}.
   *
   * @param coreBlock
   *          the {@link CoreBlock} whose code was printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreBlockFooter(CoreBlock coreBlock);

  /**
   * Method called before printing all code belonging to a {@link CoreBlock}.
   *
   * @param coreBlock
   *          the {@link CoreBlock} whose code will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreBlockHeader(CoreBlock coreBlock);

  /**
   * Method called after printing all {@link CodeElt} belonging to the {@link CoreBlock#getInitBlock() initBlock}
   * {@link CallBlock} of a {@link CoreBlock}.
   *
   * @param callBlock
   *          the {@link CallBlock} whose {@link CodeElt} were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreInitBlockFooter(CallBlock callBlock);

  /**
   * Method called before printing all {@link CodeElt} belonging to the {@link CoreBlock#getInitBlock() initBlock}
   * {@link CallBlock} of a {@link CoreBlock}.
   *
   * @param callBlock
   *          the {@link CallBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreInitBlockHeader(CallBlock callBlock);

  /**
   * Method called after printing all {@link CodeElt} belonging to the {@link CoreBlock#getLoopBlock() loopBlock}
   * {@link CallBlock} of a {@link CoreBlock}.
   *
   * @param loopBlock
   *          the {@link LoopBlock} whose {@link CodeElt} were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreLoopBlockFooter(LoopBlock loopBlock);

  /**
   * Method called before printing all {@link CodeElt} belonging to the {@link CoreBlock#getLoopBlock() loopBlock}
   * {@link CallBlock} of a {@link CoreBlock}.
   *
   * @param loopBlock
   *          the {@link LoopBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreLoopBlockHeader(LoopBlock loopBlock);

  /**
   * Method called after printing all {@link Variable} belonging to the {@link CoreBlock#getDeclarations() declarations}
   * of a {@link CoreBlock}.
   *
   * @param variableList
   *          the {@link List} of {@link Variable} that were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDeclarationsFooter(List<Variable> variableList);

  /**
   * Method called before printing all {@link Variable} belonging to the {@link CoreBlock#getDeclarations()
   * declarations} of a {@link CoreBlock}.
   *
   * @param variableList
   *          the {@link List} of {@link Variable} that will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDeclarationsHeader(List<Variable> variableList);

  /**
   * Method called after printing all {@link Variable} belonging to the {@link CoreBlock#getDefinitions() definitions}
   * of a {@link CoreBlock}.
   *
   * @param variableList
   *          the {@link List} of {@link Variable} that were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDefinitionsFooter(List<Variable> variableList);

  /**
   * Method called before printing all {@link Variable} belonging to the {@link CoreBlock#getDefinitions() definitions}
   * of a {@link CoreBlock}.
   *
   * @param variableList
   *          the {@link List} of {@link Variable} that will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDefinitionsHeader(List<Variable> variableList);

  /**
   * This method should be called when printing a "printXXHeader" method when the desired behavior is to print nothing
   * but indent the "content" of the Block (i.e. what will be printed between the header and the corresponding footer")
   *
   * @param n
   *          the number of indentation desired for the block content
   *
   * @return a {@link CharSequence}
   */
  static CharSequence printEmptyHeaderWithNIndentation(final int n) {
    final char[] indent = new char[n];
    for (int i = 0; i < n; i++) {
      indent[i] = '\t';
    }
    return "\r\n" + new String(indent) + "\r\n";
  }

  /**
   * Method called to print a {@link FifoCall}.
   *
   * @param fifoCall
   *          the printed {@link FifoCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFifoCall(FifoCall fifoCall);

  /**
   * Method called to print a {@link SpecialCall} with {@link SpecialCall#getType() type} {@link SpecialType#FORK}. If
   * this method returns <code>null</code>, the result of {@link #printSpecialCall(SpecialCall) } will be used instead
   * (in case the method is called through doSwitch).
   *
   * @param call
   *          the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFork(SpecialCall call);

  /**
   * Method called to print a {@link FunctionCall}.
   *
   * @param functionCall
   *          the printed {@link FunctionCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFunctionCall(FunctionCall functionCall);

  /**
   * Method called to print a {@link SpecialCall} with {@link SpecialCall#getType() type} {@link SpecialType#JOIN}. If
   * this method returns <code>null</code>, the result of {@link #printSpecialCall(SpecialCall) } will be used instead
   * (in case the method is called through doSwitch).
   *
   * @param call
   *          the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printJoin(SpecialCall call);

  /**
   * Method called after printing all {@link CodeElt} belonging to a {@link LoopBlock}.
   *
   * @param loopBlock
   *          the {@link LoopBlock} whose {@link CodeElt} were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printLoopBlockFooter(LoopBlock loopBlock);

  /**
   * Method called before printing all {@link CodeElt} belonging to a {@link LoopBlock}.
   *
   * @param block
   *          the {@link LoopBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printLoopBlockHeader(LoopBlock block);

  /**
   * Method called after printing all {@link CodeElt} belonging to a {@link LoopBlock}.
   *
   * @param block
   *          the {@link FiniteLoopBlock} whose {@link CodeElt} were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFiniteLoopBlockFooter(FiniteLoopBlock block);

  /**
   * Method called before printing all {@link CodeElt} belonging to a {@link FiniteLoopBlock}.
   *
   * @param block
   *          the {@link FiniteLoopBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFiniteLoopBlockHeader(FiniteLoopBlock block);

  /**
   * Method called to print a {@link NullBuffer} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param nullBuffer
   *          the {@link NullBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printNullBuffer(NullBuffer nullBuffer);

  /**
   * Method called to print a {@link NullBuffer} within the {@link CoreBlock#getDeclarations() declaration}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param nullBuffer
   *          the {@link NullBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printNullBufferDeclaration(NullBuffer nullBuffer);

  /**
   * Method called to print a {@link NullBuffer} within the {@link CoreBlock#getDefinitions() definition}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param nullBuffer
   *          the {@link NullBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printNullBufferDefinition(NullBuffer nullBuffer);

  /**
   * Method called to print a {@link SpecialCall} with {@link SpecialCall#getType() type}
   * {@link SpecialType#ROUND_BUFFER}. If this method returns <code>null</code>, the result of
   * {@link #printSpecialCall(SpecialCall) } will be used instead (in case the method is called through doSwitch).
   *
   * @param call
   *          the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printRoundBuffer(SpecialCall call);

  /**
   * ethod called to print a {@link SharedMemoryCommunication}.
   *
   * @param communication
   *          the printed {@link SharedMemoryCommunication}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSharedMemoryCommunication(SharedMemoryCommunication communication);

  /**
   * Method called to print a {@link SpecialCall}.
   *
   * @param specialCall
   *          the printed {@link SpecialCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSpecialCall(SpecialCall specialCall);

  /**
   * Method called to print a {@link SubBuffer} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param subBuffer
   *          the {@link SubBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSubBuffer(SubBuffer subBuffer);

  /**
   * Method called to print a {@link SubBuffer} within the {@link CoreBlock#getDeclarations() declaration}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param subBuffer
   *          the {@link SubBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSubBufferDeclaration(SubBuffer subBuffer);

  /**
   * Method called to print a {@link SubBuffer} within the {@link CoreBlock#getDefinitions() definition}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param subBuffer
   *          the {@link SubBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSubBufferDefinition(SubBuffer subBuffer);

  /**
   * Method called to print a {@link BufferIterator} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param bufferIterator
   *          the {@link BufferIterator} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBufferIterator(BufferIterator bufferIterator);

  /**
   * Method called to print a {@link BufferIterator} within the {@link CoreBlock#getDeclarations() declaration}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param bufferIterator
   *          the {@link BufferIterator} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBufferIteratorDeclaration(BufferIterator bufferIterator);

  /**
   * Method called to print a {@link BufferIterator} within the {@link CoreBlock#getDefinitions() definition}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param bufferIterator
   *          the {@link BufferIterator} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printBufferIteratorDefinition(BufferIterator bufferIterator);

  public CoreBlock getPrintedCoreBlock() {
    return printedCoreBlock;
  }

  public void setPrintedCoreBlock(CoreBlock printedCoreBlock) {
    this.printedCoreBlock = printedCoreBlock;
  }

  public CodegenEngine getEngine() {
    return engine;
  }

  public void setEngine(CodegenEngine engine) {
    this.engine = engine;
  }

}
