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
import org.eclipse.emf.common.util.EList
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Communication
import org.ietr.preesm.codegen.xtend.model.codegen.Constant
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.Variable
import org.ietr.preesm.codegen.xtend.model.codegen.util.CodegenSwitch
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.eclipse.emf.ecore.EObject
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

	def CharSequence printBufferDeclaration(Buffer buffer)

	def CharSequence printBufferDefinition(Buffer buffer)

	def CharSequence printCallBlockFooter(CallBlock block)

	def CharSequence printCallBlockHeader(CallBlock block)

	def CharSequence printCommunication(Communication communication)

	def CharSequence printConstantDeclaration(Constant constant)

	def CharSequence printConstantDefinition(Constant constant)

	def CharSequence printCoreBlockFooter(CoreBlock block)

	def CharSequence printCoreBlockHeader(CoreBlock block)

	def CharSequence printCoreInitBlockFooter(CallBlock callBlock)

	def CharSequence printCoreInitBlockHeader(CallBlock callBlock)

	def CharSequence printCoreLoopBlockFooter(LoopBlock block2)

	def CharSequence printCoreLoopBlockHeader(LoopBlock block2)

	def CharSequence printDeclarationsFooter(EList<Variable> list)

	def CharSequence printDeclarationsHeader(EList<Variable> list)

	def CharSequence printDefinitionsFooter(EList<Variable> list)

	def CharSequence printDefinitionsHeader(EList<Variable> list)

	def CharSequence printFifoCall(FifoCall fifoCall)

	def CharSequence printFunctionCall(FunctionCall functionCall)

	def CharSequence printLoopBlockFooter(LoopBlock block)

	def CharSequence printLoopBlockHeader(LoopBlock block)

	def CharSequence printSpecialCall(SpecialCall specialCall)

	def CharSequence printSubBufferDeclaration(SubBuffer buffer)

	def CharSequence printSubBufferDefinition(SubBuffer buffer)

}
