/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Daniel Madroñal <daniel.madronal@upm.es> (2017)
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
package org.ietr.preesm.codegen.xtend.printer.c.instrumented

import org.ietr.preesm.codegen.xtend.printer.c.CPrinter
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import java.util.List
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory
import org.ietr.preesm.codegen.xtend.printer.PrinterState
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection

/**
 * This printer currently papify C code for X86 cores..
 * 
 * @author dmadronal
 */
 
class PapifiedCPrinter extends CPrinter {
	/**
	 * Strings with the headers of the PAPI variables
	 */
		String actor_name = "actor_name_";
		String PAPI_actions = "PAPI_actions_";
		String PAPI_output = "PAPI_output_";
		String PAPI_start_usec = "PAPI_start_usec_";
		String PAPI_end_usec = "PAPI_end_usec_";
		String PAPI_eventCodeSet = "PAPI_eventCodeSet_";
		String PAPI_eventSet = "PAPI_eventSet_";
	
	/**
	 * Add a required library for PAPI utilization 
	 * 
	 * @param blocks
	 * 			List of the Coreblocks printed by the printer
	 */
	override printCoreBlockHeader(CoreBlock block) '''
		«super.printCoreBlockHeader(block)»
		#include "eventLib.h"
		
		
	'''
	
	/**
	 * Add PAPI instrumentation code to the {@link Block blocks}.<br>
	 * In the current version, the instrumentation consists of:<br>
	 * - Measuring execution time for each actor.<br>
	 * - Calls to PAPI library to monitor the total amount of instructions.<br>
	 * - Calls to PAPI library to monitor the total amount of L1 cache misses.<br>
	 * - Writing the results into a .csv file.<br> 
	 * 
	 * @param blocks
	 * 			List of the blocks printed by the printer. (will be 
	 * 			modified)
	 */
	override preProcessing(List<Block> printerBlocks, List<Block> allBlocks) {
		for (Block block : printerBlocks){
			for(CodeElt elts : (block as CoreBlock).loopBlock.codeElts){
				//For all the FunctionCalls within the main code loop
				if(elts.eClass.name.equals("FunctionCall")){	
					//Add PAPI action variable 
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_actions.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "papi_action_s"
						const.comment = const.name.concat("_buffer_definition")
						const
					})
					//Add FILE variable to store PAPI data 
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_output.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "FILE *"
						const.comment = const.name.concat("_output_file")
						const
					})	
					//Create a constant string with the actor name
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createConstantString
						const.name = actor_name.concat((elts as FunctionCall).actorName)
						const.value = (elts as FunctionCall).actorName
						const
					})	
					//Create a constant for the Code_set_size
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createConstant
						const.name = "Code_set_size"
						const.value = 2
						const
					})
					//Create a constant for use 0 as a funtion parameter
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createConstant
						const.name = "Zero"
						const.value = 0
						const
					})
					//Create a function to initialize PAPI actions
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_init_papi_actions"
						func.addParameter(block.definitions.get(block.definitions.length-5), PortDirection.OUTPUT)
						func.addParameter(block.definitions.get(block.definitions.length-3), PortDirection.INPUT)
						func.addParameter(block.definitions.get(block.definitions.length-2), PortDirection.INPUT)
						func.actorName = "PAPI Init_papi_actions_".concat((elts as FunctionCall).actorName)
						func
					})	
					//Create a function to initialize output file
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_init_output_file"
						func.addParameter(block.definitions.get(block.definitions.length-4), PortDirection.INPUT)
						func.addParameter(block.definitions.get(block.definitions.length-3), PortDirection.INPUT)
						func.actorName = "PAPI Init_output_file_".concat((elts as FunctionCall).actorName)
						func
					})	
					//Create a variable to store the start of the actor execution
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_start_usec.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "unsigned long long"
						const.comment = const.name.concat("_start_time_measure")
						const
					})	
					//Create a variable to store the end of the actor execution
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_end_usec.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "unsigned long long"
						const.comment = const.name.concat("_end_time_measure")
						const
					})	
					//Create a variable to store the event code set
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_eventCodeSet.concat((elts as FunctionCall).actorName)
						const.size = 2
						const.type = "int"
						const.comment = const.name.concat("_code_set")
						const
					})	
					//Create a function to initialize the event code set
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_init_event_code_set"
						func.addParameter(block.definitions.get(block.definitions.length-1), PortDirection.OUTPUT)
						func.actorName = "PAPI Init_code_set_".concat((elts as FunctionCall).actorName)
						func
					})		
					//Create a variable to store the event set					
					block.definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_eventSet.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "unsigned long"
						const.comment = const.name.concat("_event_set")
						const
					})	
					//Create a function to initialize the event set	
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_init_event_set"
						func.addParameter(block.definitions.get(block.definitions.length-1), PortDirection.OUTPUT)
						func.actorName = "PAPI Init_event_set_".concat((elts as FunctionCall).actorName)
						func
					})
					//Create a function to initialize the event list	
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_create_eventList"
						func.addParameter(block.definitions.get(block.definitions.length-1), PortDirection.OUTPUT)
						func.addParameter(block.definitions.get(block.definitions.length-6), PortDirection.INPUT)
						func.addParameter(block.definitions.get(block.definitions.length-2), PortDirection.INPUT)
						func.addParameter(block.definitions.get(block.definitions.length-5), PortDirection.INPUT)
						func.actorName = "PAPI create_eventlist_".concat((elts as FunctionCall).actorName)
						func
					})	
					//Create a function to initialize the event monitor multiplexing	
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "eventList_set_multiplex"
						func.addParameter(block.definitions.get(block.definitions.length-1), PortDirection.OUTPUT)
						func.actorName = "PAPI eventList_set_multiplex_".concat((elts as FunctionCall).actorName)
						func
					})	
				}			
			}
		}
		super.preProcessing(printerBlocks, allBlocks)
	}
	
	/**
	 * Add PAPI instrumentation code to the funtions.<br>
	 * In the current version, the monitoring consists of:<br>
	 * - Measuring execution time for each actor.<br>
	 * - Calls to PAPI library to monitor the total amount of instructions.<br>
	 * - Calls to PAPI library to monitor the total amount of L1 cache misses.<br>
	 * - Writing the results into a .csv file.<br> 
	 * 
	 * @param blocks
	 * 			List of the blocks printed by the printer. (will be 
	 * 			modified)
	 */	
	override printFunctionCall(FunctionCall functionCall) '''
		«IF state == PrinterState::PRINTING_LOOP_BLOCK»
			 
			// Papi Start for «functionCall.actorName»
			PAPI_start_usec_«functionCall.actorName»[0] = PAPI_get_real_usec();
			event_start(&(PAPI_eventSet_«functionCall.actorName»[0]), 0);
						
			«super.printFunctionCall(functionCall)»
			
			// Papi Stop for «functionCall.actorName»
			event_stop(&(PAPI_eventSet_«functionCall.actorName»[0]), 2, PAPI_actions_«functionCall.actorName»[0].counterValues, 0);
			PAPI_end_usec_«functionCall.actorName»[0] = PAPI_get_real_usec();
			PAPI_output_«functionCall.actorName»[0] = fopen("papi-output/papi_output_«functionCall.actorName».csv","a+");	
			fprintf(PAPI_output_«functionCall.actorName»[0], "%s,%s,%llu,%llu,%lu,%lu\n", "Sobel", PAPI_actions_«functionCall.actorName»[0].action_id, PAPI_start_usec_«functionCall.actorName»[0], PAPI_end_usec_«functionCall.actorName»[0], PAPI_actions_«functionCall.actorName»[0].counterValues[0], PAPI_actions_«functionCall.actorName»[0].counterValues[1]);
			fclose(PAPI_output_«functionCall.actorName»[0]);
			 
		«ELSE»
			«super.printFunctionCall(functionCall)»
		«ENDIF»
	'''
	
}
