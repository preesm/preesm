/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2017 - 2018)
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

import java.util.ArrayList
import java.util.Collection
import java.util.Date
import java.util.LinkedHashSet
import java.util.List
import java.util.Set
import org.ietr.preesm.codegen.xtend.model.codegen.Block
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory
import org.ietr.preesm.codegen.xtend.model.codegen.Constant
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
import org.ietr.preesm.codegen.xtend.printer.PrinterState
import org.ietr.preesm.codegen.xtend.printer.c.CPrinter
import org.ietr.preesm.core.scenario.papi.PapiComponent
import org.ietr.preesm.core.scenario.papi.PapiEvent
import org.ietr.preesm.core.scenario.papi.PapiEventModifier
import org.ietr.preesm.core.scenario.papi.PapifyConfig
import org.ietr.preesm.core.scenario.papi.PapifyConfigManager

/**
 * This printer currently papify C code for X86 cores..
 *
 * @author dmadronal
 */

class PapifyCPrinterActors extends CPrinter {

	new() {
		super(true)
	}

	/**
	 * Strings with the headers of the Papify variables
	 */
		String actor_name = "actor_name_";
		String papify_actions = "papify_actions_";
	/**
	 * variables to configure the papification
	 */
		var int code_set_size;
		var int PE_id;
		var int actor_count;
		var boolean eventMonitoring = false;
		var boolean timingMonitoring = false;
		var boolean config_added = false;
		var int config_position;
		PapifyConfigManager papifyConfig;
		List<PapifyConfig> configSet = new ArrayList();
		PapifyConfig config;
		PapiComponent comp;
		Set<PapiEvent> events;
		Set<PapiEvent> includedEvents = new LinkedHashSet();
      	PapiEvent timingEvent = new PapiEvent();
      	List<PapiEventModifier> modifTimingList = new ArrayList();
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
	override preProcessing(List<Block> printerBlocks, Collection<Block> allBlocks) {

		timingEvent.setName("Timing");
	    timingEvent.setDesciption("Event to time through PAPI_get_time()");
	    timingEvent.setIndex(9999);
	    timingEvent.setModifiers(modifTimingList);

		if(this.engine.scenario.papifyConfigManager !== null){
			papifyConfig = this.engine.scenario.papifyConfigManager;
		}
		var String event_names = "";

		// for each block
		PE_id = 0;
		actor_count = 0;
		for (Block block : printerBlocks){
			//The configuration is performed per actor
			for(CodeElt elts : (block as CoreBlock).loopBlock.codeElts){
				includedEvents = new LinkedHashSet();
				event_names = "";
				if(elts.eClass.name.equals("FunctionCall")){
					//Add Papify action variable
					var String name = (elts as FunctionCall).actorName;
					config = papifyConfig.getCorePapifyConfigGroups(name);
					(elts as FunctionCall).actorName = name.concat("_" + actor_count);
					//if(!configSet.contains(config)){
					if(config !== null){
						config_added = false;
						config_position = -1;
						for (PapifyConfig tmp : configSet){
							if(tmp.PAPIComponent.equals(config.PAPIComponent) && tmp.PAPIEvents.equals(config.PAPIEvents)){
								config_added = true
								config_position = configSet.indexOf(tmp);
							}
						}	
						if(!config_added){
							configSet.add(config);
							config_position = configSet.indexOf(config);						
						}	
						//Get component
						//Get events
						comp = config.getPAPIComponent();
						events = config.getPAPIEvents();
						timingMonitoring = false;
						eventMonitoring = false;
						for(PapiEvent singleEvent : events){
							if(singleEvent.equals(timingEvent)){
								timingMonitoring = true;
							}else if(comp.containsEvent(singleEvent)){
								includedEvents.add(singleEvent);
								eventMonitoring = true;
								if(event_names.equals("")){
									event_names = singleEvent.getName();
								}else{
									event_names = event_names.concat("," + singleEvent.getName());
								}
							}
						}
						code_set_size = includedEvents.length;
						//Add Papify action variable
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createBuffer
							const.name = papify_actions.concat((elts as FunctionCall).actorName)
							const.size = 1
							const.type = "papify_action_s"
							const.comment = "papify configuration variable"
							const
						})
						//Create a constant string with the PAPI component name
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstantString
							const.name = "component_name".concat((elts as FunctionCall).actorName)
							const.value = comp.id
							const.comment = "PAPI component name"
							const
						})
						//Create a constant string with the PE name
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstantString
							const.name = "PE_name".concat((elts as FunctionCall).actorName)
							const.value = block.name
							const.comment = "PE name"
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
							const.value = event_names
							const.comment = "Papify events"
							const
						})
						//Create a constant with the actor id
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstant
							const.name = "Actor_id"
							const.value = config_position;
							const
						})
						(elts as FunctionCall).addParameter(block.definitions.get(block.definitions.length-1), PortDirection.NONE);
						//Create a function to configure the papification
						(block as CoreBlock).initBlock.codeElts.add({
							var func = CodegenFactory.eINSTANCE.createFunctionCall()
							func.name = "configure_papify_actor_dynamic"
							func.addParameter(block.definitions.get(block.definitions.length-7), PortDirection.OUTPUT)
							func.addParameter(block.definitions.get(block.definitions.length-6), PortDirection.INPUT)
							//func.addParameter(block.definitions.get(block.definitions.length-5), PortDirection.INPUT)
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
							if(timingMonitoring){
								if(!eventMonitoring){
									const.value = 0				// Only timing
								}else{
							 		const.value = 1			// Both timing and event monitoring
								}
							}
							else{
							 	const.value = 2				// Only events
							}
							const
						})
						(elts as FunctionCall).addParameter(block.definitions.get(block.definitions.length-1), PortDirection.NONE);
						block.definitions.remove(block.definitions.length-1);
						
						//Create a constant with the PE id
						block.definitions.add({
							var const = CodegenFactory.eINSTANCE.createConstant
							const.name = "PE_id"
							const.value = PE_id;
							const
						})
						(elts as FunctionCall).addParameter(block.definitions.get(block.definitions.length-1), PortDirection.NONE);
						block.definitions.remove(block.definitions.length-1);
					}
					actor_count++;
				}
			}
			//Create a constant with the PE id
			block.definitions.add({
				var const = CodegenFactory.eINSTANCE.createConstant
				const.name = "PE_id"
				const.value = PE_id;
				const
			})
			(block as CoreBlock).initBlock.codeElts.add({
				var func = CodegenFactory.eINSTANCE.createFunctionCall()
				func.name = "configure_papify_PE_dynamic"
				func.addParameter(block.definitions.get(block.definitions.length-6), PortDirection.INPUT)
				func.addParameter(block.definitions.get(block.definitions.length-1), PortDirection.INPUT)
				func.actorName = "Papify --> configure papification of ".concat(block.name)
				func
			})
			block.definitions.remove(block.definitions.length-1);
			PE_id++;
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
		«IF state == PrinterState::PRINTING_LOOP_BLOCK && functionCall.parameters.get(functionCall.parameters.length-2).name.equals("Papified")»
			«IF (functionCall.parameters.get(functionCall.parameters.length-2) as Constant).value == 0»
				// Monitoring Start for «functionCall.actorName»
				event_start_papify_timing(papify_actions_«functionCall.actorName»);
				«functionCall.name»(«FOR param : functionCall.parameters.subList(0, functionCall.parameters.length-3) SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
				// Monitoring Stop for «functionCall.actorName»
				event_stop_papify_timing(papify_actions_«functionCall.actorName»);
				event_write_file(papify_actions_«functionCall.actorName»);
			«ENDIF»
			«IF (functionCall.parameters.get(functionCall.parameters.length-2) as Constant).value == 1»
				// Monitoring Start for «functionCall.actorName»
				event_start_dynamic(papify_actions_«functionCall.actorName», «(functionCall.parameters.get(functionCall.parameters.length-1) as Constant).getValue»);
				event_start_papify_timing(papify_actions_«functionCall.actorName»);
				«functionCall.name»(«FOR param : functionCall.parameters.subList(0, functionCall.parameters.length-3) SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
				// Monitoring Stop for «functionCall.actorName»
				event_stop_papify_timing(papify_actions_«functionCall.actorName»);
				event_stop_dynamic(papify_actions_«functionCall.actorName», «(functionCall.parameters.get(functionCall.parameters.length-1) as Constant).getValue»);
				event_write_file(papify_actions_«functionCall.actorName»);
			«ENDIF»
			«IF (functionCall.parameters.get(functionCall.parameters.length-2) as Constant).value == 2»
				// Monitoring Start for «functionCall.actorName»
				event_start_dynamic(papify_actions_«functionCall.actorName», «(functionCall.parameters.get(functionCall.parameters.length-1) as Constant).getValue»);
				«functionCall.name»(«FOR param : functionCall.parameters.subList(0, functionCall.parameters.length-3) SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «functionCall.actorName»
				// Monitoring Stop for «functionCall.actorName»
				event_stop_dynamic(papify_actions_«functionCall.actorName», «(functionCall.parameters.get(functionCall.parameters.length-1) as Constant).getValue»);
				event_write_file(papify_actions_«functionCall.actorName»);
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

			#define _GNU_SOURCE
			#include <unistd.h>
			#include <pthread.h>
			#include <stdio.h>

			// application dependent includes
			#include "preesm.h"
			#include "eventLib.h"

			// Declare computation thread functions
			«FOR coreBlock : printerBlocks»
			void *computationThread_Core«(coreBlock as CoreBlock).coreID»(void *arg);
			«ENDFOR»

			pthread_barrier_t iter_barrier;
			int stopThreads;

			// setting a setting core affinity
			int set_affinity_to_core(pthread_t* thread, int core_id) {
			int num_cores = sysconf(_SC_NPROCESSORS_ONLN);
			if (core_id < 0 || core_id >= num_cores)
			  printf("Wrong core number %d\n", core_id);
			  cpu_set_t cpuset;
			  CPU_ZERO(&cpuset);
			  CPU_SET(core_id, &cpuset);
			  return pthread_setaffinity_np(*thread, sizeof(cpu_set_t), &cpuset);
			}

			int main(void)
			{
			  // Papify output folder
			  mkdir("papify-output", 0777);
			  // Init Papify
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
			  set_affinity_to_core(&threadCore«(coreBlock as CoreBlock).coreID»,«(coreBlock as CoreBlock).coreID»);
			  «ENDFOR»

			  // Waiting for thread terminations
			  «FOR coreBlock : printerBlocks»
			    pthread_join(threadCore«(coreBlock as CoreBlock).coreID»,NULL);
			  «ENDFOR»

			  #ifdef VERBOSE
			  printf("Press any key to stop application\n");
			  #endif

			  return 0;
			}

		'''

}
