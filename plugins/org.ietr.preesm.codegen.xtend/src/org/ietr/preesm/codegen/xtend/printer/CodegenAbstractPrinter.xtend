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

import java.util.ArrayList
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtend2.lib.StringConcatenation
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Communication
import org.ietr.preesm.codegen.xtend.model.codegen.Constant
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.Variable
import org.ietr.preesm.codegen.xtend.model.codegen.util.CodegenSwitch
import org.ietr.preesm.codegen.xtend.task.CodegenException
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import java.util.Map
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString
import org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer

enum PrinterState {
	PRINTING_DEFINITIONS,
	PRINTING_DECLARATIONS,
	PRINTING_INIT_BLOCK,
	PRINTING_LOOP_BLOCK,
	IDLE
}

/**
 * The {@link CodegenPrinterVisitor} is used to visit a {@link CodegenPackage
 * Codegen model}. To use a printer, the following function calls should be used:<br>
 * 1. Call {@link #preProcessing(List)} on a {@link List} containing all printed {@link Block blocks}.<br>
 * 2. Call {@link #doSwitch()} on each {@link Block} to print.
 * 
 * @author kdesnos
 * 
 */
abstract class CodegenAbstractPrinter extends CodegenSwitch<CharSequence> {

	PrinterState state = PrinterState::IDLE;

	/**
	 * Method used to change the current state of the printer.
	 * @param newState
	 *	the new State of the printer
	 * 
	 */
	def protected void setState(PrinterState newState) {
		this.state = newState;
	}

	/**
	 * True if the visitor is currently printing {@link Variable}
	 * {@link CoreBlock#getDefinitions() definitions}
	 */
	protected boolean printingDefinitions = false

	/**
	 * True if the visitor is currently printing {@link Variable}
	 * {@link CoreBlock#getDeclarations() declarations}
	 */
	protected boolean printingDeclarations = false

	/**
	 * True if the visitor is currently printing the {@link CallBlock}
     * {@link CoreBlock#getInitBlock() initBlock}
	 */
	protected boolean printingInitBlock = false

	/**
	 * True if the visitor is currently printing the {@link LoopBlock}
     * {@link CoreBlock#getInitBlock() initBlock}
	 */
	protected boolean printingLoopBlock = false

	/**
	 * Reference to the {@link CoreBlock} currently printed
	 */
	@Property
	protected CoreBlock printedCoreBlock;

	override caseCommunication(Communication communication) {
		printCommunication(communication);
	}

	override caseSharedMemoryCommunication(SharedMemoryCommunication communication) {
		printSharedMemoryCommunication(communication);
	}

	override CharSequence caseCoreBlock(CoreBlock coreBlock) {

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
		//   tabulations, the indentation applied will be n tabulations in
		//   addition to the indentation of the blockHeader
		// - If the BlockHeader ends with a line containing something else
		//   than tabulations, there will be no indentation (except the 
		//   one of the blockHeader that will also be applied to its content)
		// 
		// A line is skipped between the BlockContent and the BlockFooter
		// if the last line of the BlockHeader is an empty line
		// (with only spaces or tabulations characters)
		//	
		// String concatenation is done manually because of complex
		// tabulation handling
		var result = new StringConcatenation
		val coreBlockHeader = printCoreBlockHeader(coreBlock)
		result.append(coreBlockHeader)
		val String indentationCoreBlock = if (coreBlockHeader.length > 0) {
				result.lastLineIndentation
			} else {
				""
			}
		val coreBlockHasNewLine = if (coreBlockHeader.length > 0) {
				result = result.trimLastEOL
				result.endWithEOL
			} else {
				false
			}
		var String indentation
		var boolean hasNewLine

		// Visit Declarations
		{
			setState(PrinterState::PRINTING_DECLARATIONS);
			val declarationsHeader = printDeclarationsHeader(
				coreBlock.declarations.filter[!coreBlock.definitions.contains(it)].toList)
			result.append(declarationsHeader, indentationCoreBlock)
			if (declarationsHeader.length > 0) {
				indentation = result.lastLineIndentation
				result = result.trimLastEOL
				hasNewLine = result.endWithEOL
			} else {
				indentation = indentationCoreBlock
				hasNewLine = false
			}
			result.append(coreBlock.declarations.filter[!coreBlock.definitions.contains(it)].map[doSwitch].join(''),
				indentation)
			if (hasNewLine) {
				result.newLineIfNotEmpty()
				result.append(indentationCoreBlock)
			}
			result.append(printDeclarationsFooter(coreBlock.declarations), indentationCoreBlock)
		}

		// Visit Definitions
		{
			setState(PrinterState::PRINTING_DEFINITIONS);
			val definitionsHeader = printDefinitionsHeader(coreBlock.definitions)
			result.append(definitionsHeader, indentationCoreBlock)
			if (definitionsHeader.length > 0) {
				indentation = result.lastLineIndentation
				result = result.trimLastEOL
				hasNewLine = result.endWithEOL
			} else {
				indentation = indentationCoreBlock
				hasNewLine = false
			}
			result.append(coreBlock.definitions.map[doSwitch].join(''), indentation)
			if (hasNewLine) {
				result.newLineIfNotEmpty()
				result.append(indentationCoreBlock)
			}
			result.append(printDefinitionsFooter(coreBlock.definitions), indentationCoreBlock)
		}

		// Visit init block
		{
			setState(PrinterState::PRINTING_INIT_BLOCK)
			val coreInitHeader = printCoreInitBlockHeader(coreBlock.initBlock)
			result.append(coreInitHeader, indentationCoreBlock)
			if (coreInitHeader.length > 0) {
				indentation = result.lastLineIndentation
				result = result.trimLastEOL
				hasNewLine = result.endWithEOL
			} else {
				indentation = indentationCoreBlock
				hasNewLine = false
			}
			result.append(coreBlock.initBlock.doSwitch, indentation)
			if (hasNewLine) {
				result.newLineIfNotEmpty()
				result.append(indentationCoreBlock)
			}
			result.append(printCoreInitBlockFooter(coreBlock.initBlock), indentationCoreBlock)
		}

		// Visit loop block
		{
			setState(PrinterState::PRINTING_LOOP_BLOCK)
			val coreLoopHeader = printCoreLoopBlockHeader(coreBlock.loopBlock)
			result.append(coreLoopHeader, indentationCoreBlock)
			if (coreLoopHeader.length > 0) {
				indentation = result.lastLineIndentation
				result = result.trimLastEOL
				hasNewLine = result.endWithEOL
			} else {
				indentation = indentationCoreBlock
				hasNewLine = false
			}
			result.append(coreBlock.loopBlock.doSwitch, indentation)
			if (hasNewLine) {
				result.newLineIfNotEmpty()
				result.append(indentationCoreBlock)
			}
			result.append(printCoreLoopBlockFooter(coreBlock.loopBlock), indentationCoreBlock)
		}

		if (coreBlockHasNewLine) {
			result.newLineIfNotEmpty()
		}
		result.append(printCoreBlockFooter(coreBlock))
		result
	}

	/**
	 * Returns <code>True</code> if the {@link StringConcatenation} ends with 
	 * an empty line. (i.e. it ends with a \n)
	 * @param concatenation
	 * 		the {@link StringConcatenation} to test
	 * @return <code>True</code> if the {@link StringConcatenation} ends with 
	 * an empty line. (i.e. it ends with a \n)
	 */
	static def endWithEOL(StringConcatenation concatenation) {
		val char n = '\n'
		concatenation.charAt(concatenation.length - 1) == n
	}

	/**
	 * Returns a copy of the input {@link StringConcatenation}. If the final
	 * line of the {@link StringConcatenation} is empty, the last "\r\n" (or
	 * "\n") are removed from the returned {@link StringConcatenation}, else the
	 * input {@link StringConcatenation} is returned as is
	 * 
	 * @param sequence
	 *            the {@link StringConcatenation} to process
	 * @return the input {@link StringConcatenation} as is or without its final
	 *         "\r\n" or "\n"
	 */
	static def trimLastEOL(StringConcatenation sequence) {
		var String result = sequence.toString
		val char newLine = '\n'
		val char r = '\r'

		// if the last character is a \n, remove it
		if (result.length > 0 && result.charAt(result.length - 1) == newLine) {
			result = result.subSequence(0, result.length - 1).toString
			if (result.length > 0 && result.charAt(result.length - 1) == r) {
				result = result.subSequence(0, result.length - 1).toString
			}
		}

		var res = new StringConcatenation
		res.append(result)
		res
	}

	/**
	 * Get a {@link String} that corresponds to the the number of tabulations
	 * that form the last line of the input {@link CharSequence}. If the
	 * {@link CharSequence} ends with an '\n' or with a '\r\n' {@link String},
	 * the penultimate line is processed instead. If the last line contains
	 * something else than tabulations, an empty {@link String} is returned<br>
	 * <br>
	 * Examples:<br>
	 * "<code>I'm a line of 
	 * <br>		code</code>" <br>
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
	 *            the processed {@link CharSequence}
	 * @return the {@link String} containing only '\t' or nothing
	 */
	static def getLastLineIndentation(CharSequence input) {
		val char newLine = '\n'
		val char tab = '\t'
		val char r = '\r'

		// First, find the beginning of the last line
		var lastLineBeginning = input.length - 1
		var int lastLineIndentationEnd
		var exitLoop = false

		// If the input ends with an EOL, skip it
		if (lastLineBeginning >= 0 && input.charAt(lastLineBeginning) == newLine) {
			lastLineBeginning = lastLineBeginning - 1
			if (lastLineBeginning >= 0 && input.charAt(lastLineBeginning) == r) {
				lastLineBeginning = lastLineBeginning - 1
			}
		}

		lastLineIndentationEnd = lastLineBeginning + 1

		while (lastLineBeginning >= 0 && !exitLoop) {

			// look for \n (works also for \r\n) but only if characters are \t
			var currentChar = input.charAt(lastLineBeginning)

			if (currentChar == newLine) {
				lastLineBeginning = lastLineBeginning + 1
				exitLoop = true
			} else {
				if (currentChar != tab) {
					return "";
				}
				lastLineBeginning = lastLineBeginning - 1
			}

		}

		return input.subSequence(lastLineBeginning, lastLineIndentationEnd).toString
	}

	override caseBuffer(Buffer buffer) {
		if (state.equals(PrinterState::PRINTING_DEFINITIONS))
			return printBufferDefinition(buffer)

		if (state.equals(PrinterState::PRINTING_DECLARATIONS))
			return printBufferDeclaration(buffer)

		return printBuffer(buffer)
	}

	override caseCallBlock(CallBlock callBlock) {
		var result = new ArrayList<CharSequence>

		printCallBlockHeader(callBlock)

		// Visit all codeElements
		result.addAll(callBlock.codeElts.map[doSwitch])

		printCallBlockFooter(callBlock)

		result.join('')
	}

	override caseConstant(Constant constant) {
		if (state.equals(PrinterState::PRINTING_DEFINITIONS))
			return printConstantDefinition(constant)

		if (state.equals(PrinterState::PRINTING_DECLARATIONS))
			return printConstantDeclaration(constant)

		return printConstant(constant)
	}

	override caseConstantString(ConstantString constant) {
		if (state.equals(PrinterState::PRINTING_DEFINITIONS))
			return printConstantStringDefinition(constant)

		if (state.equals(PrinterState::PRINTING_DECLARATIONS))
			return printConstantStringDeclaration(constant)

		return printConstantString(constant)
	}

	override defaultCase(EObject object) {
		throw new CodegenException(
			"Object " + object + " is not supported by the printer" + this + "in its current state. "
		);
	}

	/**
	 * Get the current {@link PrinterState} of the printer
	 */
	def getState() {
		state
	}

	override caseFifoCall(FifoCall fifoCall) {
		printFifoCall(fifoCall)
	}

	override caseFunctionCall(FunctionCall functionCall) {
		printFunctionCall(functionCall)
	}

	override caseLoopBlock(LoopBlock loopBlock) {
		var result = new ArrayList<CharSequence>

		result.add(printLoopBlockHeader(loopBlock))

		// Visit all codeElements
		result.addAll(loopBlock.codeElts.map[doSwitch])

		result.add(printLoopBlockFooter(loopBlock))

		result.join('')
	}
	
	override caseNullBuffer(NullBuffer nullBuffer) {
		if (state.equals(PrinterState::PRINTING_DEFINITIONS))
			return printNullBufferDefinition(nullBuffer)

		if (state.equals(PrinterState::PRINTING_DECLARATIONS))
			return printNullBufferDeclaration(nullBuffer)

		return printNullBuffer(nullBuffer)
	}

	override caseSemaphore(Semaphore semaphore) {
		if (state.equals(PrinterState::PRINTING_DEFINITIONS))
			return printSemaphoreDefinition(semaphore)

		if (state.equals(PrinterState::PRINTING_DECLARATIONS))
			return printSemaphoreDeclaration(semaphore)

		return printSemaphore(semaphore)
	}

	override caseSpecialCall(SpecialCall specialCall) {
		var result = switch specialCall {
			case specialCall.fork: printFork(specialCall)
			case specialCall.join: printJoin(specialCall)
			case specialCall.broadcast: printBroadcast(specialCall)
			case specialCall.roundBuffer: printRoundBuffer(specialCall)
		}
		result ?: printSpecialCall(specialCall)
	}

	override caseSubBuffer(SubBuffer subBuffer) {
		if (state.equals(PrinterState::PRINTING_DEFINITIONS))
			return printSubBufferDefinition(subBuffer)

		if (state.equals(PrinterState::PRINTING_DECLARATIONS))
			return printSubBufferDeclaration(subBuffer)

		return printSubBuffer(subBuffer)
	}

	/**
	 * Method called before printing a set of {@link Block blocks}. This method
	 * can perform some printer specific modification on the blocks passed as
	 * parameters. For example, it can be used to insert instrumentation
	 * primitives in the code. This method will NOT print the code of the
	 * {@link Block blocks}, use {@link #doSwitch()} on each {@link Block} to
	 * print after the pre-processing to do so.
	 * 
	 * @param printerBlocks
	 * 				The list of {@link Block blocks} that will be printer by the
	 * 				printer
	 * @param allBlocks
	 * 				The list of all {@link Block blocks} printed during a workflow execution. 
	 * 				This list includes all printerBlocks
	 */
	def void preProcessing(List<Block> printerBlocks, List<Block> allBlocks);

	/**
	 * This method is called after all the {@link Block blocks} have been 
	 * printed by the printer to give the opportunity to print secondary
	 * files. (eg. project files, main files, ...).<br>
	 * This method returns a {@link Map} where each {@link Entry} associates 
	 * a {@link String} to a {@link CharSequence} respectively corresponding
	 * to a file name (including the extension) and the its content.
	 * 
	 * @param printerBlocks
	 *   	The list of {@link Block blocks} that were printed by the
	 * 		printer
	 * @param allBlocks
	 *		The list of all {@link Block blocks} printed during a workflow execution. 
	 *		This list includes all printerBlocks
	 */
	def Map<String, CharSequence> createSecondaryFiles(List<Block> printerBlocks, List<Block> allBlocks);

	/**
	 * Method called to print a {@link SpecialCall} with
	 * {@link SpecialCall#getType() type} {@link SpecialType#BROADCAST}. If this
	 * method returns <code>null</code>, the result of
	 * {@link #printSpecialCall(SpecialCall) } will be used
	 * instead (in case the method is called through doSwitch).
	 * 
	 * @param specialCall
	 *            the printed {@link SpecialCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printBroadcast(SpecialCall call)

	/**
	 * Method called to print a {@link Buffer} outside the
	 * {@link CoreBlock#getDefinitions() definition} or the
	 * {@link CoreBlock#getDeclarations() declaration} of a
	 * {@link CoreBlock}
	 * 
	 * @param buffer
	 *            the {@link Buffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printBuffer(Buffer buffer)

	/**
	 * Method called to print a {@link Buffer} within the
	 * {@link CoreBlock#getDeclarations() declaration} of a
	 * {@link CoreBlock}. If a {@link Buffer} was defined in
	 * the current block, it will not be declared.
	 * 
	 * @param buffer
	 *            the {@link Buffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printBufferDeclaration(Buffer buffer)

	/**
	 * Method called to print a {@link Buffer} within the
	 * {@link CoreBlock#getDefinitions() definition} {@link LoopBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param buffer
	 *            the {@link Buffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printBufferDefinition(Buffer buffer)

	/**
	 * Method called after printing all {@link CodeElement} belonging 
	 * to a {@link CallBlock}.
	 * 
	 * @param callBlock
	 *            the {@link CallBlock} whose {@link CodeElement} were 
	 * 			  printed before calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCallBlockFooter(CallBlock callBlock)

	/**
	 * Method called before printing all {@link CodeElement} belonging 
	 * to a {@link CallBlock}.
	 * 
	 * @param callBlock
	 *            the {@link CallBlock} whose {@link CodeElement} will be 
	 * 			  printed after calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCallBlockHeader(CallBlock block)

	/**
	 * Method called to print a {@link Communication}.
	 * 
	 * @param communication
	 *             the printed {@link Communication}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCommunication(Communication communication)

	/**
	 * Method called to print a {@link Constant} outside the
	 * {@link CoreBlock#getDefinitions() definition} or the
	 * {@link CoreBlock#getDeclarations() declaration} of a
	 * {@link CoreBlock}
	 * 
	 * @param constant
	 *            the {@link Constant} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printConstant(Constant constant)

	/**
	 * Method called to print a {@link Constant} within the
	 * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param constant
	 *            the {@link Constant} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printConstantDeclaration(Constant constant)

	/**
	 * Method called to print a {@link Constant} within the
	 * {@link CoreBlock#getDefinitions() definition} {@link LoopBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param constant
	 *            the {@link Constant} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printConstantDefinition(Constant constant)
	
	/**
	 * Method called to print a {@link ConstantString} outside the
	 * {@link CoreBlock#getDefinitions() definition} or the
	 * {@link CoreBlock#getDeclarations() declaration} of a
	 * {@link CoreBlock}
	 * 
	 * @param constant
	 *            the {@link ConstantString} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printConstantString(ConstantString constant)

	/**
	 * Method called to print a {@link ConstantString} within the
	 * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param constant
	 *            the {@link ConstantString} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printConstantStringDeclaration(ConstantString constant)

	/**
	 * Method called to print a {@link ConstantString} within the
	 * {@link CoreBlock#getDefinitions() definition} {@link LoopBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param constant
	 *            the {@link ConstantString} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printConstantStringDefinition(ConstantString constant)

	/**
	 * Method called after printing all code belonging 
	 * to a {@link CoreBlock}.
	 * 
	 * @param coreBlock
	 *            the {@link CoreBlock} whose code was 
	 * 			  printed before calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCoreBlockFooter(CoreBlock coreBlock)

	/**
	 * Method called before printing all code belonging 
	 * to a {@link CoreBlock}.
	 * 
	 * @param coreBlock
	 *            the {@link CoreBlock} whose code will be 
	 * 			  printed after calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCoreBlockHeader(CoreBlock coreBlock)

	/**
	 * Method called after printing all {@link CodeElement} belonging 
	 * to the {@link CoreBlock#getInitBlock() initBlock} {@link CallBlock} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param callBlock
	 *            the {@link CallBlock} whose {@link CodeElement} were 
	 * 			  printed before calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCoreInitBlockFooter(CallBlock callBlock)

	/**
	 * Method called before printing all {@link CodeElement} belonging 
	 * to the {@link CoreBlock#getInitBlock() initBlock} {@link CallBlock} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param callBlock
	 *            the {@link CallBlock} whose {@link CodeElement} will be
	 * 			  printed after calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCoreInitBlockHeader(CallBlock callBlock)

	/**
	 * Method called after printing all {@link CodeElement} belonging 
	 * to the {@link CoreBlock#getLoopBlock() loopBlock} {@link CallBlock} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param loopBlock
	 *            the {@link LoopBlock} whose {@link CodeElement} were 
	 * 			  printed before calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCoreLoopBlockFooter(LoopBlock loopBlock)

	/**
	 * Method called before printing all {@link CodeElement} belonging 
	 * to the {@link CoreBlock#getLoopBlock() loopBlock} {@link CallBlock} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param loopBlock
	 *            the {@link LoopBlock} whose {@link CodeElement} will be 
	 * 			  printed after calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printCoreLoopBlockHeader(LoopBlock loopBlock)

	/**
	 * Method called after printing all {@link Variable} belonging 
	 * to the {@link CoreBlock#getDeclarations() declarations} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param variableList
	 *            the {@link List} of {@link Variable} that were 
	 * 			  printed before calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printDeclarationsFooter(List<Variable> variableList)

	/**
	 * Method called before printing all {@link Variable} belonging 
	 * to the {@link CoreBlock#getDeclarations() declarations} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param variableList
	 *            the {@link List} of {@link Variable} that will be 
	 * 			  printed after calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printDeclarationsHeader(List<Variable> variableList)

	/**
	 * Method called after printing all {@link Variable} belonging 
	 * to the {@link CoreBlock#getDefinitions() definitions} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param variableList
	 *            the {@link List} of {@link Variable} that were 
	 * 			  printed before calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printDefinitionsFooter(List<Variable> variableList)

	/**
	 * Method called before printing all {@link Variable} belonging 
	 * to the {@link CoreBlock#getDefinitions() definitions} of 
	 * a {@link CoreBlock}.
	 * 
	 * @param variableList
	 *            the {@link List} of {@link Variable} that will be 
	 * 			  printed after calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printDefinitionsHeader(List<Variable> vaeiableList)

	/**
 	* This method should be called when printing a "printXXXHeader" method 
 	* when the desired behavior is to print nothing but indent the 
 	* "content" of the Block (i.e. what will be printed between the header
 	*  and the corresponding footer")
 	* 
 	* @param n 
 	*		the number of indentation desired for the block content
 	* 
 	* @return a {@link CharSequence}
 	*/
	static def CharSequence printEmptyHeaderWithNIndentation(int n) {
		val char tab = '\t'
		var char[] indent = newCharArrayOfSize(n)
		if (indent.length > 0) {
			for (i : 0 .. indent.length - 1) {
				indent.set(i, tab)
			}
		}
		return "\r\n" + indent.join + "\r\n"

	}

	/**
	 * Method called to print a {@link FifoCall}.
	 * 
	 * @param communication
	 *             the printed {@link FifoCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printFifoCall(FifoCall fifoCall)

	/**
	 * Method called to print a {@link SpecialCall} with
	 * {@link SpecialCall#getType() type} {@link SpecialType#FORK}. If this
	 * method returns <code>null</code>, the result of
	 * {@link #printSpecialCall(SpecialCall) } will be used
	 * instead (in case the method is called through doSwitch).
	 * 
	 * @param specialCall
	 *            the printed {@link SpecialCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printFork(SpecialCall call)

	/**
	 * Method called to print a {@link FunctionCall}.
	 * 
	 * @param functionCall
	 *             the printed {@link FunctionCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printFunctionCall(FunctionCall functionCall)

	/**
	 * Method called to print a {@link SpecialCall} with
	 * {@link SpecialCall#getType() type} {@link SpecialType#JOIN}. If this
	 * method returns <code>null</code>, the result of
	 * {@link #printSpecialCall(SpecialCall) } will be used
	 * instead (in case the method is called through doSwitch).
	 * 
	 * @param specialCall
	 *            the printed {@link SpecialCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printJoin(SpecialCall call)

	/**
	 * Method called after printing all {@link CodeElement} belonging 
	 * to a {@link LoopBlock}.
	 * 
	 * @param loopBlock
	 *            the {@link LoopBlock} whose {@link CodeElement} were 
	 * 			  printed before calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printLoopBlockFooter(LoopBlock loopBlock)

	/**
	 * Method called before printing all {@link CodeElement} belonging 
	 * to a {@link LoopBlock}.
	 * 
	 * @param loopBlock
	 *            the {@link LoopBlock} whose {@link CodeElement} will be 
	 * 			  printed after calling this method.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printLoopBlockHeader(LoopBlock block)
	
	/**
	 * Method called to print a {@link NullBuffer} outside the
	 * {@link CoreBlock#getDefinitions() definition} or the
	 * {@link CoreBlock#getDeclarations() declaration} of a
	 * {@link CoreBlock}
	 * 
	 * @param nullBuffer
	 *            the {@link NullBuffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printNullBuffer(NullBuffer nullBuffer)

	/**
	 * Method called to print a {@link NullBuffer} within the
	 * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param nullBuffer
	 *            the {@link NullBuffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printNullBufferDeclaration(NullBuffer nullBuffer)

	/**
	 * Method called to print a {@link NullBuffer} within the
	 * {@link CoreBlock#getDefinitions() definition} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param nullBuffer
	 *            the {@link NullBuffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printNullBufferDefinition(NullBuffer nullBuffer)

	/**
	 * Method called to print a {@link SpecialCall} with
	 * {@link SpecialCall#getType() type} {@link SpecialType#ROUND_BUFFER}. If this
	 * method returns <code>null</code>, the result of
	 * {@link #printSpecialCall(SpecialCall) } will be used
	 * instead (in case the method is called through doSwitch).
	 * 
	 * @param specialCall
	 *            the printed {@link SpecialCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printRoundBuffer(SpecialCall call)

	/**
	 * Method called to print a {@link Semaphore} outside the
	 * {@link CoreBlock#getDefinitions() definition} or the
	 * {@link CoreBlock#getDeclarations() declaration} of a
	 * {@link CoreBlock}
	 * 
	 * @param semaphore
	 *            the {@link Semaphore} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSemaphore(Semaphore semaphore)

	/**
	 * Method called to print a {@link Semaphore} within the
	 * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param semaphore
	 *            the {@link Semaphore} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSemaphoreDeclaration(Semaphore semaphore)

	/**
	 * Method called to print a {@link Semaphore} within the
	 * {@link CoreBlock#getDefinitions() definition} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param semaphore
	 *            the {@link Semaphore} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSemaphoreDefinition(Semaphore semaphore)

	/**
	 * ethod called to print a {@link SharedMemoryCommunication}.
	 * 
	 * @param communication
	 *             the printed {@link SharedMemoryCommunication}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSharedMemoryCommunication(SharedMemoryCommunication communication)

	/**
	 * Method called to print a {@link SpecialCall}.
	 * 
	 * @param specialCall
	 *             the printed {@link SpecialCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSpecialCall(SpecialCall specialCall)

	/**
	 * Method called to print a {@link SubBuffer} outside the
	 * {@link CoreBlock#getDefinitions() definition} or the
	 * {@link CoreBlock#getDeclarations() declaration} of a
	 * {@link CoreBlock}
	 * 
	 * @param subBuffer
	 *            the {@link SubBuffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSubBuffer(SubBuffer subBuffer)

	/**
	 * Method called to print a {@link SubBuffer} within the
	 * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param subBuffer
	 *            the {@link SubBuffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSubBufferDeclaration(SubBuffer subBuffer)

	/**
	 * Method called to print a {@link SubBuffer} within the
	 * {@link CoreBlock#getDefinitions() definition} {@link CallBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param subBuffer
	 *            the {@link SubBuffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSubBufferDefinition(SubBuffer subBuffer)

}
