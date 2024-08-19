/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013)
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

import java.util.List
import org.preesm.codegen.model.Buffer
import org.preesm.codegen.model.CallBlock
import org.preesm.codegen.model.Communication
import org.preesm.codegen.model.Constant
import org.preesm.codegen.model.CoreBlock
import org.preesm.codegen.model.Direction
import org.preesm.codegen.model.FifoCall
import org.preesm.codegen.model.FifoOperation
import org.preesm.codegen.model.FunctionCall
import org.preesm.codegen.model.LoopBlock
import org.preesm.codegen.model.SpecialCall
import org.preesm.codegen.model.SubBuffer
import org.preesm.codegen.model.Variable
import org.preesm.codegen.model.util.CodegenModelUserFactory
import org.preesm.codegen.printer.BlankPrinter
import org.preesm.codegen.model.MainSimsdpBlock

class XMLPrinter extends BlankPrinter {

	override printBroadcast(SpecialCall call) '''
		<CompoundCode name="«call.name»">«var input = call.inputBuffers.head»«var index = 0L»
		«FOR output : call.outputBuffers»«var outputIdx = 0L»
			«// TODO: Change how this loop iterates (nbIter is used in a comment only ...)
			FOR nbIter : 0..((output.getNbToken/input.getNbToken)+1) as int /*Worst number the loop exec */»
				«IF outputIdx < output.getNbToken /* Execute only first loop cores */»
					<!-- memcpy #«nbIter» -->
					<userFunctionCall comment="" name="memcpy">
						<bufferAtIndex index="«outputIdx»" name="«output.name»"/>
						<bufferAtIndex index="«index»" name="«input.name»"/>
						<constant name="size" type="string" value="«val value = Math::min(output.getNbToken-outputIdx,input.getNbToken-index)»«value»*sizeof(«output.type»)"/>
					</userFunctionCall>«{index=(index+value)%input.getNbToken;outputIdx=(outputIdx+value); ""}»
				«ENDIF»
			«ENDFOR»
		«ENDFOR»
			</CompoundCode>
	'''

	override printBuffer(Buffer buffer) '''
		<buffer name="«buffer.name»" size="«buffer.getNbToken»" type="«buffer.type»"/>
	'''

	override printBufferDeclaration(Buffer buffer) {
		return printBufferDefinition(buffer)
	}

	override printBufferDefinition(Buffer buffer) '''
		<bufferAllocation
			comment="«buffer.class.simpleName»: «buffer.comment»"
			name="«buffer.name»" size="«buffer.getNbToken»" type="«buffer.type»"/>
	'''

	override printConstant(Constant constant) '''
		<constant name="«constant.name»" type="«constant.type»" value="«constant.value»"/>
	'''

	override printCoreBlockHeader(CoreBlock coreBlock) '''
		<?xml version="1.0" encoding="UTF-8"?>
		<sourceCode xmlns="http://org.ietr.preesm.sourceCode">
			<coreType>«coreBlock.coreType»</coreType>
			<coreName>«coreBlock.name»</coreName>
			<SourceFile>

	'''

	override printCoreBlockFooter(CoreBlock block) '''
			</SourceFile>
		</sourceCode>
	'''

	override printCoreInitBlockHeader(CallBlock callBlock) '''
		<threadDeclaration name="computationThread">
			<bufferContainer/>
			<linearCodeContainer comment="COMINIT"/>
			<linearCodeContainer comment="Fifo Initialization Section"/>
			«IF !callBlock.codeElts.empty»
				<linearCodeContainer comment="Initialization phase number 0">

			«ELSE»
				<linearCodeContainer comment="Initialization phase number 0"/>
			«ENDIF»
	'''

	override printCoreInitBlockFooter(CallBlock callBlock) '''
		«IF !callBlock.codeElts.empty»
			«"\t"»</linearCodeContainer>
		«ELSE»

		«ENDIF»
	'''

	override printCoreLoopBlockHeader(LoopBlock loopBlock) '''
		«"\t"»<forLoop comment="Main loop of computation">

	'''

	override printCoreLoopBlockFooter(LoopBlock loopBlock) '''
			</forLoop>
		</threadDeclaration>
	'''

	override printDeclarationsHeader(List<Variable> list) '''
		<bufferContainer>

	'''

	override printDefinitionsHeader(List<Variable> list) {
		return "\t\r\n" // printEmptyHeaderWithNIndentation(1)
	}

	override printDefinitionsFooter(List<Variable> list) '''
		</bufferContainer>
	'''

	override printFunctionCall(FunctionCall functionCall) '''
		<userFunctionCall comment="«functionCall.actorName»" name="«functionCall.name»">
			«FOR param : functionCall.parameters»
				«param.doSwitch»
			«ENDFOR»
		</userFunctionCall>
	'''

	override printCommunication(
		Communication communication) '''
		<«IF communication.direction == Direction::SEND»send«ELSE»receive«ENDIF»Msg ID="«communication.id»" comment="«communication.name»"
			phase="«communication.delimiter»" «IF communication.direction == Direction::SEND»target="«communication.receiveStart.coreContainer.name»"«ELSE»source="«communication.sendStart.coreContainer.name»"«ENDIF»>
			<routeStep type"msg">
				<sender def="«communication.sendStart.coreContainer.coreType»" name="«communication.sendStart.coreContainer.name»"/>
				<receiver def="«communication.receiveStart.coreContainer.coreType»" name="«communication.receiveStart.coreContainer.name»"/>
				«FOR node : communication.nodes»
					<node def="«node.type»" name="«node.name»"/>
				«ENDFOR»
			</routeStep>
			«communication.data.doSwitch»
		</«IF communication.direction == Direction::SEND»send«ELSE»receive«ENDIF»Msg>
	'''

	override printFifoCall(FifoCall fifoCall) '''
		<userFunctionCall comment="«fifoCall.name»" name="«switch (fifoCall.operation)
			{
				case FifoOperation::POP: "pull"
				case FifoOperation::INIT: "new_fifo"
				case FifoOperation::PUSH: "push"
			}»">
			<variable name="&amp;«fifoCall.headBuffer.name»"/>
			«IF fifoCall.bodyBuffer !== null»
				<variable name="&amp;«fifoCall.bodyBuffer.name»"/>
			«ENDIF»
			«IF fifoCall.operation != FifoOperation::INIT»
				«fifoCall.parameters.head.doSwitch»
				<constant name="nb_token" type="long" value="«(fifoCall.parameters.head as Buffer).getNbToken»"/>
			«ENDIF»
			<constant name="size" type="string" value="sizeof(«fifoCall.headBuffer.type»)"/>
			«{
			var const = CodegenModelUserFactory::eINSTANCE.createConstant
			const.name = "head_size"
			const.type = "int"
			const.value = fifoCall.headBuffer.getNbToken
			const
			}.doSwitch»
			«{
			var const = CodegenModelUserFactory::eINSTANCE.createConstant
			const.name = "fifo_size"
			const.type = "int"
			const.value = fifoCall.headBuffer.getNbToken + (if(fifoCall.bodyBuffer === null)0 else fifoCall.bodyBuffer.getNbToken)
			const
			}.doSwitch»
		</userFunctionCall>
	'''

	override printFork(SpecialCall call) '''
		<CompoundCode name="«call.name»">«var input = call.inputBuffers.head»«var index = 0L»
			«FOR output : call.outputBuffers»
				<userFunctionCall comment="" name="memcpy">
					«output.doSwitch»
					<bufferAtIndex index="«index»" name="«input.name»"/>
					<constant name="size" type="string" value="«output.getNbToken»*sizeof(«output.type»)"/>
				</userFunctionCall>«{index=(index+output.getNbToken); ""}»
			«ENDFOR»
		</CompoundCode>	«/*<CompoundCode name="«call.name»"> «var input = call.inputBuffers.head»
			«FOR index : 0 .. call.outputBuffers.size - 1»
				<Assignement var="«call.outputBuffers.get(index).name»">&amp;«input.name»[«call.outputBuffers.get(index).size *
			index»]</Assignement>
			«ENDFOR»
		</CompoundCode>*/ »
	'''

	override printJoin(SpecialCall call) '''
		<CompoundCode name="«call.name»">«var output = call.outputBuffers.head»«var index = 0L»
			«FOR input : call.inputBuffers»
				<userFunctionCall comment="" name="memcpy">
					<bufferAtIndex index="«index»" name="«output.name»"/>
					«input.doSwitch»
					<constant name="size" type="string" value="«input.getNbToken»*sizeof(«input.type»)"/>
				</userFunctionCall>«{index=(index+input.getNbToken); ""}»
			«ENDFOR»
			</CompoundCode>	«/*<CompoundCode name="«call.name»">«var output = call.outputBuffers.head»
			«FOR index : 0 .. call.inputBuffers.size - 1»
				<userFunctionCall comment="" name="memcpy">
					<bufferAtIndex index="«call.inputBuffers.get(index).size * index»" name="«output.name»"/>
					«call.inputBuffers.get(index).doSwitch»
					«{
			var const = CodegenModelUserFactory::eINSTANCE.createConstant
			const.name = "size"
			const.type = "long"
			const.value = call.inputBuffers.get(index).size
			const
			}.doSwitch»
				</userFunctionCall>
			«ENDFOR»
		</CompoundCode> */»
	'''

	override printRoundBuffer(SpecialCall call) '''
		<CompoundCode name="«call.name»">«var output = call.outputBuffers.head»«var index = 0L»
		«FOR buffer : call.inputBuffers»«var inputIdx = 0L»
			«// TODO: Change how this loop iterates (nbIter is used in a comment only ...)
			FOR nbIter : 0..(buffer.getNbToken/output.getNbToken+1) as int/*Worst case id buffer.size exec of the loop */»
				«IF inputIdx < buffer.getNbToken /* Execute only first loop core */»
					<!-- memcpy #«nbIter» -->
					<userFunctionCall comment="" name="memcpy">
						<bufferAtIndex index="«index»" name="«output.name»"/>
						<bufferAtIndex index="«inputIdx»" name="«buffer.name»"/>
						<constant name="size" type="string" value="«val value = Math::min(buffer.getNbToken-inputIdx,output.getNbToken-index)»«value»*sizeof(«buffer.type»)"/>
					</userFunctionCall>«{index=(index+value)%output.getNbToken;inputIdx=(inputIdx+value); ""}»
				«ENDIF»
			«ENDFOR»
		«ENDFOR»
			</CompoundCode>
	'''

	override printSubBuffer(SubBuffer subBuffer) {
		return printBuffer(subBuffer)
	}

	override printSubBufferDeclaration(SubBuffer buffer) {
		return printBufferDefinition(buffer)
	}

	override printSubBufferDefinition(SubBuffer buffer) {
		return printBufferDefinition(buffer)
	}
	
	override printMainSimsdpHeader(MainSimsdpBlock block, int nodes) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override printMainSimsdpHeader(MainSimsdpBlock block, int nodes, String[] nodeID) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override protected printmpi(MainSimsdpBlock block) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

}
