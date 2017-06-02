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
 
class PapiCPrinter extends CPrinter {
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
					(block as Block).definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_actions.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "papi_action_s"
						const.comment = const.name.concat("_buffer_definition")
						const
					})
					//Add FILE variable to store PAPI data 
					(block as Block).definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_output.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "FILE *"
						const.comment = const.name.concat("_output_file")
						const
					})	
					//Create a constant string with the actor name
					(block as Block).definitions.add({
						var const = CodegenFactory.eINSTANCE.createConstantString
						const.name = actor_name.concat((elts as FunctionCall).actorName)
						const.value = (elts as FunctionCall).actorName
						const
					})	
					//Create a constant for the Code_set_size
					(block as Block).definitions.add({
						var const = CodegenFactory.eINSTANCE.createConstant
						const.name = "Code_set_size"
						const.value = 2
						const
					})
					//Create a constant for use 0 as a funtion parameter
					(block as Block).definitions.add({
						var const = CodegenFactory.eINSTANCE.createConstant
						const.name = "Zero"
						const.value = 0
						const
					})
					//Create a function to initialize PAPI actions
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_init_papi_actions"
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-5), PortDirection.OUTPUT)
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-3), PortDirection.INPUT)
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-2), PortDirection.INPUT)
						func.actorName = "PAPI Init_papi_actions_".concat((elts as FunctionCall).actorName)
						func
					})	
					//Create a function to initialize output file
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_init_output_file"
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-4), PortDirection.INPUT)
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-3), PortDirection.INPUT)
						func.actorName = "PAPI Init_output_file_".concat((elts as FunctionCall).actorName)
						func
					})	
					//Create a variable to store the start of the actor execution
					(block as Block).definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_start_usec.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "long long"
						const.comment = const.name.concat("_start_time_measure")
						const
					})	
					//Create a variable to store the end of the actor execution
					(block as Block).definitions.add({
						var const = CodegenFactory.eINSTANCE.createBuffer
						const.name = PAPI_end_usec.concat((elts as FunctionCall).actorName)
						const.size = 1
						const.type = "long long"
						const.comment = const.name.concat("_end_time_measure")
						const
					})	
					//Create a variable to store the event code set
					(block as Block).definitions.add({
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
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-1), PortDirection.OUTPUT)
						func.actorName = "PAPI Init_code_set_".concat((elts as FunctionCall).actorName)
						func
					})		
					//Create a variable to store the event set					
					(block as Block).definitions.add({
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
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-1), PortDirection.OUTPUT)
						func.actorName = "PAPI Init_event_set_".concat((elts as FunctionCall).actorName)
						func
					})
					//Create a function to initialize the event list	
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "event_create_eventList"
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-1), PortDirection.OUTPUT)
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-6), PortDirection.INPUT)
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-2), PortDirection.INPUT)
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-5), PortDirection.INPUT)
						func.actorName = "PAPI create_eventlist_".concat((elts as FunctionCall).actorName)
						func
					})	
					//Create a function to initialize the event monitor multiplexing	
					(block as CoreBlock).initBlock.codeElts.add({
						var func = CodegenFactory.eINSTANCE.createFunctionCall()
						func.name = "eventList_set_multiplex"
						func.addParameter((block as Block).definitions.get((block as Block).definitions.length-1), PortDirection.OUTPUT)
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
			// Papi Start
			PAPI_start_usec_«functionCall.actorName»[0] = PAPI_get_real_usec();
			event_start(&(PAPI_eventSet_«functionCall.actorName»[0]), 0);
			«super.printFunctionCall(functionCall)»
			event_stop(&(PAPI_eventSet_«functionCall.actorName»[0]), 2, PAPI_actions_«functionCall.actorName»[0].counterValues, 0);
			PAPI_end_usec_«functionCall.actorName»[0] = PAPI_get_real_usec();
			PAPI_output_«functionCall.actorName»[0] = fopen("papi-output/papi_output_«functionCall.actorName».csv","a+");	
			fprintf(PAPI_output_«functionCall.actorName»[0], "%s,%s,%llu,%llu,%lu,%lu\n", "Sobel", PAPI_actions_«functionCall.actorName»[0].action_id, PAPI_start_usec_«functionCall.actorName»[0], PAPI_end_usec_«functionCall.actorName»[0], PAPI_actions_«functionCall.actorName»[0].counterValues[0], PAPI_actions_«functionCall.actorName»[0].counterValues[1]);
			fclose(PAPI_output_«functionCall.actorName»[0]);
			// Papi Stop
		«ELSE»
			«super.printFunctionCall(functionCall)»
		«ENDIF»
	'''
	
}