/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Daniel Madroñal <daniel.madronal@upm.es> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2017)
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

import java.util.Date
import java.util.List
import org.ietr.preesm.codegen.xtend.printer.c.CPrinter
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory
import org.ietr.preesm.codegen.xtend.printer.PrinterState
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
import org.ietr.dftools.architecture.slam.Design
import org.ietr.dftools.architecture.slam.ComponentInstance



/**
 * This printer currently papify C code for X86 cores..
 *
 * @author dmadronal
 */

class PapifyCPrinter extends CPrinter {

	new() {
		super(true)
	}

	/**
	 * Strings with the headers of the Papify variables
	 */
		String actor_name = "actor_name_";
		String Papify_actions = "Papify_actions_";
	/**
	 * variables to configure the papification
	 */			
		int instance;
		var int code_set_size;
		var String all_event_names;
		var String timing_active;
		var boolean papifying = false;

	/**
	 * Add a required library for Papify utilization
	 *
	 * @param blocks
	 * 			List of the Coreblocks printed by the printer
	 */
	override printCoreBlockHeader(CoreBlock block) '''
		«super.printCoreBlockHeader(block)»
		#include "eventLib.h"


	'''

	/**
	 * Configure the Papify instrumentation of each {@link Block blocks}.<br>
	 * In the current version, the instrumentation could be configured in terms of:<br>
	 * - Which cores are being monitored.<br>
	 * - Monitoring only time or time and events.<br>
	 * 
	 * This configuration is based on user defined parameters within the SLAM model.<br>
	 * The results of the monitoring will be stored in a .csv file.<br>
	 *
	 * @param printerBlocks
	 * 			List of the blocks printed by the printer. (will be
	 * 			modified)
	 */
	override preProcessing(List<Block> printerBlocks, List<Block> allBlocks) {
		
		var Design slamDesign = this.engine.archi;		
		var List<ComponentInstance> compInstances = slamDesign.componentInstances; 
	
		var ComponentInstance pe;			
		var String[] event_names;
		
		var int search_index;
		
		// for each block				
		for (Block block : printerBlocks){
			// associate the block with its instance in the slam model
			search_index = 0;
			instance = 0;
			for(ComponentInstance searching : compInstances){
				if(searching.instanceName.equals(block.name)){
					instance = search_index;
				}
				else{
					search_index++;
				}
			}
			pe = compInstances.get(instance);
			
			// get the events to be monitored and/or if there will be only a timing measurement
			all_event_names = org.ietr.preesm.core.architecture.util.DesignTools.getParameter(pe, "PAPI_AVAIL_EVENTS");
			timing_active = org.ietr.preesm.core.architecture.util.DesignTools.getParameter(pe, "PAPI_TIMING");
						
			// configure Papify
			if(all_event_names !== null && all_event_names != ""){					
				event_names = all_event_names.split(",");	
				code_set_size = event_names.length;
				papifying = true;
			}else if(timing_active == "1"){
				code_set_size = 0;
				papifying = true;
			}
			
			// if the core instance is being papified
			if(papifying == true){									
				for(CodeElt elts : (block as CoreBlock).loopBlock.codeElts){	
					//For all the FunctionCalls within the main code loop
					if(elts.eClass.name.equals("FunctionCall")){
						//Add Papify action variable
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createBuffer
							const.name = Papify_actions.concat((elts as FunctionCall).actorName)
							const.size = 1
							const.type = "Papify_action_s"
							const.comment = "Papify configuration variable"
							const
						})
						//Create a constant string with the instance name
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstantString
							const.name = "component_name".concat((elts as FunctionCall).actorName)
							const.value = block.name
							const.comment = "Instance name"
							const
						})
						//Create a constant string with the actor name
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstantString
							const.name = actor_name.concat((elts as FunctionCall).actorName)
							const.value = (elts as FunctionCall).actorName
							const.comment = "Actor name"
							const
						})
						//Create a constant for the Code_set_size
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstant
							const.name = "Code_set_size"
							const.value = code_set_size
							const
						})
						//Create a constant string with the events to be measured
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstantString
							const.name = "all_event_names"
							const.value = all_event_names
							const.comment = "Papify events"
							const
						})
						//Create a function to configure the papification
						(block as CoreBlock).initBlock.codeElts.add({
							var func = CodegenFactory.eINSTANCE.createFunctionCall()
							func.name = "configure_papify"
							func.addParameter(block.definitions.get(block.definitions.length-5), PortDirection.OUTPUT)
							func.addParameter(block.definitions.get(block.definitions.length-4), PortDirection.INPUT)
							func.addParameter(block.definitions.get(block.definitions.length-3), PortDirection.INPUT)
							func.addParameter(block.definitions.get(block.definitions.length-2), PortDirection.INPUT)
							func.addParameter(block.definitions.get(block.definitions.length-1), PortDirection.INPUT)
							func.actorName = "Papify --> configure papification of ".concat((elts as FunctionCall).actorName)
							func
						})				
						//Include a parameter in each actor to point out that it will be monitored								
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstant
							const.comment = "Actor to be papified"
							const.name = "Papified"
							//We cannot read the value of the parameter, so timing and event monitoring
							//is distinguished using the type of variable
							if(code_set_size == 0){
								const.type = "int"						
							}
							else{
							 	const.type = "boolean"							
							}
							const.value = 1		
							const
						})
						(elts as FunctionCall).addParameter(block.definitions.get(block.definitions.length-1), PortDirection.NONE);
					}
				}
			}
			papifying = false;
		}		
		super.preProcessing(printerBlocks, allBlocks)
	}

	/**
	 * Add Papify instrumentation code in the code generation.<br>
	 * In the current version, there are three possiblities for each core instance:<br>
	 * - Monitoring timing (last parameter of the functionCall is Papified and its type is "int").<br>
	 * - Monitoring timing and events (last parameter of the functionCall is Papified and its type is "boolean").<br>
	 * - No monitoring at all.<br>
	 * 
	 * The results will be written into a .csv file.<br>
	 *
	 * @param functionCall
	 * 			Funtion that is being printed
	 */
	override printFunctionCall(FunctionCall functionCall) '''
		«IF state == PrinterState::PRINTING_LOOP_BLOCK && functionCall.parameters.get(functionCall.parameters.length-1).name.equals("Papified")»
			«IF functionCall.parameters.get(functionCall.parameters.length-1).type == "int"»
				// Monitoring Start for «functionCall.actorName»
				event_start_Papify_timing(Papify_actions_«functionCall.actorName»);
				«functionCall.name»(«FOR param : functionCall.parameters.subList(0, functionCall.parameters.length-1) SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
				// Monitoring Stop for «functionCall.actorName»
				event_stop_Papify_timing(Papify_actions_«functionCall.actorName»);
				event_write_file(Papify_actions_«functionCall.actorName»);
			«ELSE»
				// Monitoring Start for «functionCall.actorName»
				event_start(Papify_actions_«functionCall.actorName», 0);
				event_start_Papify_timing(Papify_actions_«functionCall.actorName»);
				«functionCall.name»(«FOR param : functionCall.parameters.subList(0, functionCall.parameters.length-1) SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
				// Monitoring Stop for «functionCall.actorName»
				event_stop_Papify_timing(Papify_actions_«functionCall.actorName»);
				event_stop(Papify_actions_«functionCall.actorName», 0);
				event_write_file(Papify_actions_«functionCall.actorName»);
			«ENDIF»
		«ELSE»
			«super.printFunctionCall(functionCall)»
		«ENDIF»
	'''

	/**
	 * Almost the same main as the default CPrinter one.
	 * The only difference is the program beginning where we add Papify specific initialization (mkdir + event init).
	 */
	override String printMain(List<Block> printerBlocks) '''
		/**
		 * @file main.c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 *
		 */


		#include "x86.h"
		#include "eventLib.h"

		// Declare computation thread functions
		«FOR coreBlock : printerBlocks»
		void *computationThread_Core«(coreBlock as CoreBlock).coreID»(void *arg);
		«ENDFOR»

		pthread_barrier_t iter_barrier;
		int stopThreads;


		int main(void)
		{
		  mkdir("Papify-output", 0777);

		 event_init_multiplex();

		  // Declaring thread pointers
		  «FOR coreBlock : printerBlocks »
		    pthread_t threadCore«(coreBlock as CoreBlock).coreID»;
		  «ENDFOR»

		  #ifdef VERBOSE
		  printf("Launched main\n");
		  #endif

		  // Creating a synchronization barrier
		  stopThreads = 0;
		  pthread_barrier_init(&iter_barrier, NULL, «printerBlocks.size»);

		  communicationInit();

		  // Creating threads
		  «FOR coreBlock : printerBlocks»
		    pthread_create(&threadCore«(coreBlock as CoreBlock).coreID», NULL, computationThread_Core«(coreBlock as CoreBlock).coreID», NULL);
		  «ENDFOR»

		  // Waiting for thread terminations
		  «FOR coreBlock : printerBlocks»
		    pthread_join(threadCore«(coreBlock as CoreBlock).coreID»,NULL);
		  «ENDFOR»

		  #ifdef VERBOSE
		    printf("Press any key to stop application\n");
		  #endif

		  // Waiting for the user to end the procedure
		  getchar();
		  exit(0);
		}

	'''

}
