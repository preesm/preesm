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
package org.ietr.preesm.codegen.xtend.printer.c

import org.ietr.preesm.codegen.xtend.printer.c.CPrinter
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.Call
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation

/**
 * This printer is currently used to print C code only for X86 processor with
 * shared memory communication. It is very similary to the {@link CPrinter} except
 * that it prints calls to <code>malloc()</code> and <code>free()</code> before and
 * after each printed function call.
 * 
 * @author kdesnos
 */
class DynamicAllocCPrinter extends CPrinter {
		
	new() {
		super()
		IGNORE_USELESS_MEMCPY = false
	}

	override printBufferDefinition(Buffer buffer) '''
	«buffer.type» *«buffer.name»; // «buffer.comment» size:= «buffer.size»*«buffer.type»
	'''
	
	override printSubBufferDefinition(SubBuffer buffer) '''
	«printBufferDefinition(buffer)»
	'''
	
	override printSubBufferDeclaration(SubBuffer buffer) '''
	extern «printBufferDefinition(buffer)»
	'''
	
	override printFunctionCall(FunctionCall functionCall) '''
		«printCallWithMallocFree(functionCall,super.printFunctionCall(functionCall))»
	'''
		/**
	 * Add the necessary <code>malloc()</code> and <code>free()</code> calls to
	 * a call passed as a parameter. Before the {@link Call}, a
	 * <code>malloc()</code> for all the output {@link Buffer} of the
	 * {@link Call} is printed. After the {@link Call}, a <code>free()</code> is
	 * printed for each output {@link Buffer} of the {@link Call}.
	 * 
	 * @param call
	 *            the {@link Call} to print.
	 * @param sequence
	 *            the normal printed {@link Call} (by the {@link CPrinter}.
	 * @return the {@link Call} printed with <code>malloc()</code> and
	 */
	def printCallWithMallocFree(Call call, CharSequence sequence) '''
		«/*Allocate output buffers */
		IF call.parameters.size > 0»
			«FOR i : 0 .. call.parameters.size -1 »
				«IF call.parameterDirections.get(i) == PortDirection.OUTPUT»
					«call.parameters.get(i).doSwitch» = («(call.parameters.get(i) as Buffer).type»*) malloc(«(call.parameters.get(i) as Buffer).size»*sizeof(«(call.parameters.get(i) as Buffer).type»));
				«ENDIF»
			«ENDFOR»
		«ENDIF»
		«sequence»
		«/*Free input buffers*/
		IF call.parameters.size > 0»
			«FOR i : 0 .. call.parameters.size -1 »
				«IF call.parameterDirections.get(i) == PortDirection.INPUT»
					free(«call.parameters.get(i).doSwitch»);
				«ENDIF»
			«ENDFOR»
		«ENDIF»
	'''

	override printFifoCall(FifoCall fifoCall) '''
		«IF fifoCall.operation == FifoOperation.INIT»
			«IF fifoCall.bodyBuffer != null»
				«fifoCall.bodyBuffer.doSwitch» = («fifoCall.bodyBuffer.type»*) malloc(«fifoCall.bodyBuffer.size»*sizeof(«fifoCall.bodyBuffer.type»));
			«ENDIF»
		«ENDIF»
		«IF fifoCall.operation == FifoOperation.PUSH || fifoCall.operation == FifoOperation.INIT»
			«fifoCall.headBuffer.doSwitch» = («fifoCall.headBuffer.type»*) malloc(«fifoCall.headBuffer.size»*sizeof(«fifoCall.headBuffer.type»));
		«ENDIF»
		«printCallWithMallocFree(fifoCall,super.printFifoCall(fifoCall))»
		«IF fifoCall.operation == FifoOperation.POP»
			free(«fifoCall.headBuffer.doSwitch»);
		«ENDIF»
	'''
	
	override printBroadcast(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printBroadcast(call))»
	'''	
	
	override printRoundBuffer(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printRoundBuffer(call))»
	'''	
	
	override printFork(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printFork(call))»
	'''
	
	override printJoin(SpecialCall call) '''
		«printCallWithMallocFree(call,super.printJoin(call))»
	'''
}
