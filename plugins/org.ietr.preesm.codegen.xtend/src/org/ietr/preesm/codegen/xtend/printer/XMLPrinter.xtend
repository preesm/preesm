package org.ietr.preesm.codegen.xtend.printer

import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory
import org.ietr.preesm.codegen.xtend.model.codegen.Communication
import org.ietr.preesm.codegen.xtend.model.codegen.Constant
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.Direction
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.Variable
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation

class XMLPrinter extends DefaultPrinter {

	override printBuffer(Buffer buffer) '''
		<buffer name="«buffer.name»" size="«buffer.size»" type="«buffer.type»"/>
	'''

	override printBufferDeclaration(Buffer buffer) {
		printBufferDefinition(buffer)
	}

	override printBufferDefinition(Buffer buffer) '''
		<bufferAllocation
			comment="«buffer.class.simpleName»: «buffer.comment»"
			name="«buffer.name»" size="«buffer.size»" type="«buffer.type»"/>
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

	override printDeclarationsHeader(List<Variable> list) {
		return "\t\r\n" //printEmptyHeaderWithNIndentation(1)
	}

	override printDefinitionsHeader(List<Variable> list) '''
		<bufferContainer>
			
	'''

	override printDeclarationsFooter(List<Variable> list) '''
		</bufferContainer>
	'''

	override printFunctionCall(FunctionCall functionCall) '''
		<userFunctionCall comment="«"XXX"»" name="«functionCall.name»">
			«FOR param : functionCall.parameters»
				«param.doSwitch»
			«ENDFOR»
		</userFunctionCall>
	'''

	override printCommunication(Communication communication) '''
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
		<userFunctionCall comment="«fifoCall.name»" name="«
			switch (fifoCall.operation) 
			{
				case FifoOperation::POP: "pull"
				case FifoOperation::INIT: "new_fifo"
				case FifoOperation::PUSH: "push"
			}
		»">
			<variable name="&amp;«fifoCall.storageBuffer.name»"/>
			«IF fifoCall.operation != FifoOperation::INIT»
				«fifoCall.parameters.head.doSwitch»
				<constant name="nb_token" type="int" value="«(fifoCall.parameters.head as Buffer).size»"/>
			«ELSE»
			<constant name="size" type="string" value="sizeof(«fifoCall.storageBuffer.type»)"/>
			«{
			var const = CodegenFactory::eINSTANCE.createConstant
			const.name = "fifo_size"
			const.type = "int"
			const.value = fifoCall.storageBuffer.size
			const
			}.doSwitch»
			«ENDIF»
		</userFunctionCall>
	'''

	override printFork(SpecialCall call) '''
		<CompoundCode name="«call.name»"> «var input = call.inputBuffers.head»
			«FOR index : 0 .. call.outputBuffers.size - 1»
				<Assignement var="«call.outputBuffers.get(index).name»">&amp;«input.name»[«call.outputBuffers.get(index).size *
			index»]</Assignement>
			«ENDFOR»
		</CompoundCode>
	'''

	override printJoin(SpecialCall call) '''
		<CompoundCode name="«call.name»">«var output = call.outputBuffers.head»
			«FOR index : 0 .. call.inputBuffers.size - 1»
				<userFunctionCall comment="" name="memcpy">
					<bufferAtIndex index="«call.inputBuffers.get(index).size * index»" name="«output.name»"/>
					«call.inputBuffers.get(index).doSwitch»
					«{
			var const = CodegenFactory::eINSTANCE.createConstant
			const.name = "size"
			const.type = "long"
			const.value = call.inputBuffers.get(index).size
			const
		}.doSwitch»
				</userFunctionCall>
			«ENDFOR»
		</CompoundCode>
	'''

	override printSubBuffer(SubBuffer subBuffer) {
		printBuffer(subBuffer)
	}

	override printSubBufferDeclaration(SubBuffer buffer) {
		printBufferDefinition(buffer)
	}

	override printSubBufferDefinition(SubBuffer buffer) {
		printBufferDefinition(buffer)
	}

}
