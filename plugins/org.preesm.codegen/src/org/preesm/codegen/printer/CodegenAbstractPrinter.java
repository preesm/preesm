/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 * Leonardo Suriano [leonardo.suriano@upm.es] (2019)
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
package org.preesm.codegen.printer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.BufferIterator;
import org.preesm.codegen.model.CallBlock;
import org.preesm.codegen.model.ClusterBlock;
import org.preesm.codegen.model.CodeElt;
import org.preesm.codegen.model.CodegenPackage;
import org.preesm.codegen.model.Communication;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.ConstantString;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.DataTransferAction;
import org.preesm.codegen.model.DistributedMemoryCommunication;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.FpgaLoadAction;
import org.preesm.codegen.model.FreeDataTransferBuffer;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.GlobalBufferDeclaration;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.IteratedBuffer;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.NullBuffer;
import org.preesm.codegen.model.OutputDataTransfer;
import org.preesm.codegen.model.PapifyAction;
import org.preesm.codegen.model.PapifyFunctionCall;
import org.preesm.codegen.model.RegisterSetUpAction;
import org.preesm.codegen.model.SectionBlock;
import org.preesm.codegen.model.SharedMemoryCommunication;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenSwitch;
import org.preesm.codegen.xtend.task.CodegenEngine;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.slam.ComponentInstance;

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

  protected PrinterState state = PrinterState.IDLE;

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
   *
   */
  protected final int getMainOperatorId() {
    final ComponentInstance mainOperatorName = getEngine().getScenario().getSimulationInfo().getMainOperator();
    final Collection<Block> codeBlocks = getEngine().getCodeBlocks();
    for (final Block block : codeBlocks) {
      if (block.getName().equals(mainOperatorName.getInstanceName()) && block instanceof CoreBlock) {
        return ((CoreBlock) block).getCoreID();
      }
    }
    // If nothing is mapped on the main operator, there is no code block that has been generated for
    // it. By default, use the first code block in the list.
    return 0;
  }

  /**
   * Reference to the {@link CoreBlock} currently printed
   */
  private CoreBlock printedCoreBlock;

  private CodegenEngine engine;

  public CoreBlock getPrintedCoreBlock() {
    return this.printedCoreBlock;
  }

  public void setPrintedCoreBlock(final CoreBlock printedCoreBlock) {
    this.printedCoreBlock = printedCoreBlock;
  }

  public CodegenEngine getEngine() {
    return this.engine;
  }

  public void setEngine(final CodegenEngine engine) {
    this.engine = engine;
  }

  /**
   * apolloEnabled Enable intra-actor optimization with Apollo
   */

  private boolean apolloEnabled = false;

  public boolean getApolloEnabled() {
    return this.apolloEnabled;
  }

  public void setApolloEnabled(final boolean apolloEnabled) {
    this.apolloEnabled = apolloEnabled;
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
  public void preProcessing(final List<Block> printerBlocks, final Collection<Block> allBlocks) {
    // by default, check that all Operators have a unique hardware ID
    // this can be overriden.
    final List<CoreBlock> operatorBlocks = allBlocks.stream().filter(block -> block instanceof CoreBlock)
        .map(block -> (CoreBlock) block).collect(Collectors.toList());
    final long operatorBlockCount = operatorBlocks.size();
    for (int i = 0; i < operatorBlockCount; i++) {
      final CoreBlock coreBlocki = operatorBlocks.get(i);
      final int coreBlockiID = coreBlocki.getCoreID();
      for (int j = i + 1; j < operatorBlockCount; j++) {
        final CoreBlock coreBlockj = operatorBlocks.get(j);
        final int coreBlockjID = coreBlockj.getCoreID();
        if (coreBlockiID == coreBlockjID) {
          throw new PreesmRuntimeException("Operators '" + coreBlocki.getName() + "' and '" + coreBlockj.getName()
              + "' in the design have the same hardware ID '" + coreBlockiID + "'. The codegen '"
              + this.getClass().getSimpleName() + "' does not support this.");
        }
      }
    }
  }

  /**
   * Method called after printing a set of {@link Block blocks}. This method can perform some printer specific
   * modification on the blocks passed as parameters. For example, it can be used to insert instrumentation primitives
   * in the code. This method will NOT print the code of the {@link Block blocks}, use {@link #doSwitch()} on each
   * {@link Block} to print after the pre-processing to do so.
   *
   */
  public CharSequence postProcessing(final CharSequence charSeq) {
    // no postProcess by default
    return charSeq;
  }

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

  // public abstract Map<String, CharSequence> generateStandardLibFiles();

  /**
   * @param path
   *          path of the generated clusters
   *
   */
  public abstract Map<String, CharSequence> generateStandardLibFiles(String path);

  @Override
  public CharSequence defaultCase(final EObject object) {
    throw new PreesmRuntimeException("Object " + object + " is not supported by the printer " + this);
  }

  /**
   * Get the current {@link PrinterState} of the printer
   */
  public PrinterState getState() {
    return this.state;
  }

  @Override
  public CharSequence caseCommunication(final Communication communication) {
    return printCommunication(communication);
  }

  @Override
  public CharSequence caseSharedMemoryCommunication(final SharedMemoryCommunication communication) {
    return printSharedMemoryCommunication(communication);
  }

  @Override
  public CharSequence caseDistributedMemoryCommunication(final DistributedMemoryCommunication communication) {
    return printDistributedMemoryCommunication(communication);
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
    setPrintedCoreBlock(coreBlock);

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
    final EList<Variable> definitions = coreBlock.getDefinitions();
    // Visit Declarations
    result = printDeclarations(coreBlock, result, indentationCoreBlock, definitions);

    // Visit Definitions
    result = printDefinitions(result, indentationCoreBlock, definitions);

    // Visit init block
    result = printInitBlock(coreBlock, result, indentationCoreBlock);

    // Visit loop block
    result = printLoopBlock(coreBlock, result, indentationCoreBlock);

    if (coreBlockHasNewLine) {
      result.newLineIfNotEmpty();
    }
    result.append(printCoreBlockFooter(coreBlock));

    setPrintedCoreBlock(null);

    return result;
  }

  private StringConcatenation printDeclarations(final CoreBlock coreBlock, StringConcatenation result,
      final String indentationCoreBlock, final EList<Variable> definitions) {
    String indentation;
    boolean hasNewLine;
    setState(PrinterState.PRINTING_DECLARATIONS);

    final List<Variable> declsNotDefs = new ArrayList<>();
    final EList<Variable> declarations = coreBlock.getDeclarations();
    for (final Variable v : declarations) {
      if (!definitions.contains(v)) {
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
    setState(PrinterState.IDLE);
    return result;
  }

  private StringConcatenation printLoopBlock(final CoreBlock coreBlock, StringConcatenation result,
      final String indentationCoreBlock) {
    String indentation;
    boolean hasNewLine;
    setState(PrinterState.PRINTING_LOOP_BLOCK);
    final CharSequence coreLoopHeader;
    if (!coreBlock.isMultinode()) {
      coreLoopHeader = printCoreLoopBlockHeader(coreBlock.getLoopBlock());
    } else {
      coreLoopHeader = printCoreLoopBlockHeader(coreBlock.getLoopBlock(), coreBlock.getNodeID());
    }
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
    if (!coreBlock.isMultinode()) {
      result.append(printCoreLoopBlockFooter(coreBlock.getLoopBlock()), indentationCoreBlock);
    } else {
      result.append(printCoreLoopBlockFooter(coreBlock.getLoopBlock(), coreBlock.getNodeID()), indentationCoreBlock);
    }

    setState(PrinterState.IDLE);
    return result;
  }

  private StringConcatenation printInitBlock(final CoreBlock coreBlock, StringConcatenation result,
      final String indentationCoreBlock) {
    String indentation;
    boolean hasNewLine;
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
    setState(PrinterState.IDLE);
    return result;
  }

  private StringConcatenation printDefinitions(StringConcatenation result, final String indentationCoreBlock,
      final EList<Variable> definitions) {
    String indentation;
    boolean hasNewLine;
    setState(PrinterState.PRINTING_DEFINITIONS);
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
    setState(PrinterState.IDLE);
    return result;
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
    if (this.state.equals(PrinterState.PRINTING_DEFINITIONS)) {
      return printPapifyActionDefinition(action);
    }
    return printPapifyActionParam(action);

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
  public CharSequence caseFifoCall(final FifoCall fifoCall) {
    return printFifoCall(fifoCall);
  }

  @Override
  public CharSequence caseFunctionCall(final FunctionCall functionCall) {
    final StringBuilder res = new StringBuilder();
    res.append(printPreFunctionCall(functionCall));
    res.append(printFunctionCall(functionCall));
    res.append(printPostFunctionCall(functionCall));
    return res.toString();
  }

  @Override
  public CharSequence casePapifyFunctionCall(final PapifyFunctionCall functionCall) {
    return printPapifyFunctionCall(functionCall);
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
    }

    result.append(printFiniteLoopBlockFooter(loopBlock), "");

    return result;
  }

  @Override
  public CharSequence caseClusterBlock(final ClusterBlock clusterBlock) {
    StringConcatenation result = new StringConcatenation();
    String indentation = "";
    boolean hasNewLine;

    final CharSequence clusterBlockheader = printClusterBlockHeader(clusterBlock);
    result.append(clusterBlockheader, indentation);

    if (clusterBlockheader.length() > 0) {
      indentation = CodegenAbstractPrinter.getLastLineIndentation(result);
      result = CodegenAbstractPrinter.trimLastEOL(result);
      hasNewLine = CodegenAbstractPrinter.endWithEOL(result);
    } else {
      hasNewLine = false;
    }

    final EList<Variable> variables = clusterBlock.getDefinitions();
    for (final Variable variable : variables) {
      if (variable instanceof Buffer) {
        final CharSequence code = printBufferDefinition((Buffer) variable);
        result.append(code, indentation);
      }
    }

    // Visit all codeElements
    final EList<CodeElt> codeElts = clusterBlock.getCodeElts();
    for (final CodeElt codeElt : codeElts) {
      final CharSequence code = doSwitch(codeElt);
      result.append(code, indentation);
    }

    if (hasNewLine) {
      result.newLineIfNotEmpty();
    }

    result.append(printClusterBlockFooter(clusterBlock), "");

    return result;
  }

  @Override
  public CharSequence caseSectionBlock(final SectionBlock sectionBlock) {
    StringConcatenation result = new StringConcatenation();
    String indentation = "";
    boolean hasNewLine;

    final CharSequence sectionBlockheader = printSectionBlockHeader(sectionBlock);
    result.append(sectionBlockheader, indentation);

    if (sectionBlockheader.length() > 0) {
      indentation = CodegenAbstractPrinter.getLastLineIndentation(result);
      result = CodegenAbstractPrinter.trimLastEOL(result);
      hasNewLine = CodegenAbstractPrinter.endWithEOL(result);
    } else {
      hasNewLine = false;
    }

    // Visit all codeElements
    final EList<CodeElt> codeElts = sectionBlock.getCodeElts();
    for (final CodeElt codeElt : codeElts) {
      final CharSequence code = doSwitch(codeElt);
      result.append(code, indentation);
    }

    if (hasNewLine) {
      result.newLineIfNotEmpty();
    }

    result.append(printSectionBlockFooter(sectionBlock), "");

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

  @Override
  public CharSequence caseIteratedBuffer(final IteratedBuffer iteratedBuffer) {
    return printIteratedBuffer(iteratedBuffer);
  }

  @Override
  public CharSequence caseDataTransferAction(DataTransferAction object) {
    return printDataTansfer(object);
  }

  @Override
  public CharSequence caseOutputDataTransfer(OutputDataTransfer object) {
    return printOutputDataTransfer(object);
  }

  @Override
  public CharSequence caseRegisterSetUpAction(RegisterSetUpAction object) {
    return printRegisterSetUp(object);
  }

  @Override
  public CharSequence caseFpgaLoadAction(FpgaLoadAction object) {
    return printFpgaLoad(object);
  }

  @Override
  public CharSequence caseFreeDataTransferBuffer(FreeDataTransferBuffer object) {
    return printFreeDataTransferBuffer(object);
  }

  @Override
  public CharSequence caseGlobalBufferDeclaration(GlobalBufferDeclaration object) {
    return printGlobalBufferDeclaration(object);
  }

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
   * Method called to print a {@link PapifyAction} in the {@link CoreBlock#getDefinitions() definition}
   *
   * @param action
   *          the {@link PapifyAction} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printPapifyActionDefinition(PapifyAction action);

  /**
   * Method called to print a {@link PapifyAction} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param action
   *          the {@link PapifyAction} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printPapifyActionParam(PapifyAction action);

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

  public abstract CharSequence printCoreLoopBlockFooter(LoopBlock loopBlock, int nodeID);

  /**
   * Method called before printing all {@link CodeElt} belonging to the {@link CoreBlock#getLoopBlock() loopBlock}
   * {@link CallBlock} of a {@link CoreBlock}.
   *
   * @param loopBlock
   *          the {@link LoopBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printCoreLoopBlockHeader(LoopBlock loopBlock);

  public abstract CharSequence printCoreLoopBlockHeader(LoopBlock loopBlock, int nodeID);

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
   * Method called right before {@link #printFunctionCall}
   */
  public abstract CharSequence printPreFunctionCall(FunctionCall functionCall);

  /**
   * Method called right after {@link #printFunctionCall}
   */
  public abstract CharSequence printPostFunctionCall(FunctionCall functionCall);

  /**
   * Method called to print a {@link PapifyFunctionCall}.
   *
   * @param papifyFunctionCall
   *          the printed {@link PapifyFunctionCall}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printPapifyFunctionCall(PapifyFunctionCall papifyFunctionCall);

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
   * Method called after printing all {@link CodeElt} belonging to a {@link ClusterBlock}.
   *
   * @param block
   *          the {@link ClusterBlock} whose {@link CodeElt} were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printClusterBlockFooter(ClusterBlock block);

  /**
   * Method called before printing all {@link CodeElt} belonging to a {@link ClusterBlock}.
   *
   * @param block
   *          the {@link ClusterBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printClusterBlockHeader(ClusterBlock block);

  /**
   * Method called after printing all {@link CodeElt} belonging to a {@link SectionBlock}.
   *
   * @param block
   *          the {@link SectionBlock} whose {@link CodeElt} were printed before calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSectionBlockFooter(SectionBlock block);

  /**
   * Method called before printing all {@link CodeElt} belonging to a {@link SectionBlock}.
   *
   * @param block
   *          the {@link SectionBlock} whose {@link CodeElt} will be printed after calling this method.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSectionBlockHeader(SectionBlock block);

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
   * Method called to print a {@link SharedMemoryCommunication}.
   *
   * @param communication
   *          the printed {@link SharedMemoryCommunication}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printSharedMemoryCommunication(SharedMemoryCommunication communication);

  /**
   * ethod called to print a {@link DistributedMemoryCommunication}.
   *
   * @param communication
   *          the printed {@link DistributedMemoryCommunication}.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDistributedMemoryCommunication(DistributedMemoryCommunication communication);

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
   * Method called to print a {@link IteratedBuffer} outside the {@link CoreBlock#getDefinitions() definition} or the
   * {@link CoreBlock#getDeclarations() declaration} of a {@link CoreBlock}
   *
   * @param iteratedBuffer
   *          the {@link IteratedBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printIteratedBuffer(IteratedBuffer iteratedBuffer);

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

  /**
   * Method called to print a {@link DataTansferAction} within the {@link CoreBlock#getDefinitions() definition}
   * {@link LoopBlock} of a {@link CoreBlock}
   *
   * @param action
   *          the {@link DataTransferAction} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printDataTansfer(final DataTransferAction action);

  /**
   * Method called to print a {@link OutputDataTransfer} within the {@link CoreBlock#getDefinitions() definition}
   * {@link LoopBlock} of a {@link CoreBlock}
   *
   * @param action
   *          the {@link OutputDataTransfer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printOutputDataTransfer(final OutputDataTransfer action);

  /**
   * Method called to print a {@link RegisterSetUpAction} within the {@link CoreBlock#getDefinitions() definition}
   * {@link LoopBlock} of a {@link CoreBlock}
   *
   * @param action
   *          the {@link RegisterSetUpAction} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printRegisterSetUp(final RegisterSetUpAction action);

  /**
   * Method called to print a {@link FpgaLoadAction} within the {@link CoreBlock#getDefinitions() definition}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param action
   *          the {@link FpgaLoadAction} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFpgaLoad(final FpgaLoadAction action);

  /**
   * Method called to print a {@link FreeDataTransferBuffer} within the {@link CoreBlock#getDefinitions() definition}
   * {@link CallBlock} of a {@link CoreBlock}
   *
   * @param action
   *          the {@link FreeDataTransferBuffer} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printFreeDataTransferBuffer(final FreeDataTransferBuffer action);

  /**
   * Method called to print a {@link GlobalBufferDeclaration} within the {@link InitBlock#getDefinitions() definition}
   * {@link InitBlock} of a {@link InitBlock}
   *
   * @param action
   *          the {@link GlobalBufferDeclaration} to print.
   * @return the printed {@link CharSequence}
   */
  public abstract CharSequence printGlobalBufferDeclaration(final GlobalBufferDeclaration action);
}
