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
import java.util.List
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
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
import org.ietr.preesm.codegen.xtend.model.codegen.util.CodegenSwitch
import org.ietr.preesm.codegen.xtend.task.CodegenException

/**
 * The {@link CodegenPrinterVisitor} is used to visit a {@link CodegenPackage
 * Codegen model}.
 * 
 * @author kdesnos
 * 
 */
abstract class CodegenAbstractPrinter extends CodegenSwitch<CharSequence> {
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

	override CharSequence caseCoreBlock(CoreBlock coreBlock) {
		var result = new ArrayList<CharSequence>

		result.add(printCoreBlockHeader(coreBlock))

		// Visit Definitions
		printingDefinitions = true
		result.add(printDefinitionsHeader(coreBlock.definitions))
		result.addAll(coreBlock.definitions.map[doSwitch])
		result.add(printDefinitionsFooter(coreBlock.definitions))
		printingDefinitions = false

		// Visit Declarations
		printingDeclarations = true
		result.add(printDeclarationsHeader(coreBlock.declarations))
		result.addAll(coreBlock.declarations.map[doSwitch])
		result.add(printDeclarationsFooter(coreBlock.declarations))
		printingDeclarations = false

		// Visit init block
		printingInitBlock = true
		result.add(printCoreInitBlockHeader(coreBlock.initBlock))
		result.add(coreBlock.initBlock.doSwitch)
		result.add(printCoreInitBlockFooter(coreBlock.initBlock))
		printingInitBlock = false

		// Visit loop block
		printingLoopBlock = true
		result.add(printCoreLoopBlockHeader(coreBlock.loopBlock))
		result.add(coreBlock.loopBlock.doSwitch)
		result.add(printCoreLoopBlockFooter(coreBlock.loopBlock))
		printingLoopBlock = false

		result.join('');
	}

	override caseBuffer(Buffer buffer) {
		if (printingDefinitions)
			return printBufferDefinition(buffer)

		if (printingDeclarations)
			return printBufferDeclaration(buffer)

		return super.caseBuffer(buffer)
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
		if (printingDefinitions)
			return printConstantDefinition(constant)

		if (printingDeclarations)
			return printConstantDeclaration(constant)

		return super.caseConstant(constant)
	}

	override defaultCase(EObject object) {
		throw new CodegenException(
			"Object " + object + " is not supported by the printer" + this + "in its current state. "
		);
	}

	override caseFifoCall(FifoCall fifoCall) {
		printFifoCall(fifoCall)
	}

	override caseFunctionCall(FunctionCall functionCall) {
		printFunctionCall(functionCall)
	}

	override caseLoopBlock(LoopBlock loopBlock) {
		var result = new ArrayList<CharSequence>

		printLoopBlockHeader(loopBlock)

		// Visit all codeElements
		result.addAll(loopBlock.codeElts.map[doSwitch])

		printLoopBlockFooter(loopBlock)

		result.join('')
	}

	override caseSpecialCall(SpecialCall specialCall) {
		printSpecialCall(specialCall)
	}

	override caseSubBuffer(SubBuffer subBuffer) {
		if (printingDefinitions)
			return printSubBufferDefinition(subBuffer)

		if (printingDeclarations)
			return printSubBufferDeclaration(subBuffer)

		return super.caseSubBuffer(subBuffer)
	}

	/**
	 * Method called to print a {@link Buffer} within the
	 * {@link CoreBlock#getDeclarations() declaration} {@link CallBlock} of a
	 * {@link CoreBlock}
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
	def CharSequence printDeclarationsFooter(EList<Variable> variableList)

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
	def CharSequence printDeclarationsHeader(EList<Variable> variableList)

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
	def CharSequence printDefinitionsFooter(EList<Variable> variableList)

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
	def CharSequence printDefinitionsHeader(EList<Variable> vaeiableList)

	/**
	 * Method called to print a {@link FifoCall}.
	 * 
	 * @param communication
	 *             the printed {@link FifoCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printFifoCall(FifoCall fifoCall)

	/**
	 * Method called to print a {@link FunctionCall}.
	 * 
	 * @param functionCall
	 *             the printed {@link FunctionCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printFunctionCall(FunctionCall functionCall)

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
	 * Method called to print a {@link SpecialCall}.
	 * 
	 * @param specialCall
	 *             the printed {@link SpecialCall}.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSpecialCall(SpecialCall specialCall)

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
	 * {@link CoreBlock#getDefinitions() definition} {@link LoopBlock} of a
	 * {@link CoreBlock}
	 * 
	 * @param subBuffer
	 *            the {@link SubBuffer} to print.
	 * @return the printed {@link CharSequence}
	 */
	def CharSequence printSubBufferDefinition(SubBuffer subBuffer)

}
