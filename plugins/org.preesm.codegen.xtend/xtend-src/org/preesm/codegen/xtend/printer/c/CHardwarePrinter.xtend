/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Hascoet [jhascoet@kalray.eu] (2016)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2018)
 * Leonardo Suriano [leonardo.suriano@upm.es] (2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013 - 2016)
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
package org.preesm.codegen.xtend.printer.c

import java.io.IOException
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.Date
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import java.util.logging.Level
import org.preesm.codegen.model.Block
import org.preesm.codegen.model.CallBlock
import org.preesm.codegen.model.CodeElt
import org.preesm.codegen.model.CoreBlock
import org.preesm.codegen.model.Constant
import org.preesm.codegen.model.DataTransferAction
import org.preesm.codegen.model.FpgaLoadAction
import org.preesm.codegen.model.FreeDataTransferBuffer
import org.preesm.codegen.model.FunctionCall
import org.preesm.codegen.model.GlobalBufferDeclaration
import org.preesm.codegen.model.IntVar
import org.preesm.codegen.model.LoopBlock
import org.preesm.codegen.model.OutputDataTransfer
import org.preesm.codegen.model.PapifyFunctionCall
import org.preesm.codegen.model.RegisterSetUpAction
import org.preesm.codegen.model.util.CodegenModelUserFactory
import org.preesm.commons.exceptions.PreesmRuntimeException
import org.preesm.commons.files.PreesmResourcesHelper
import org.preesm.commons.logger.PreesmLogger
import org.preesm.codegen.model.ActorFunctionCall
import org.preesm.codegen.model.PapifyType
import org.preesm.codegen.model.PapifyAction

/**
 * This printer extends the CPrinter and override some of its methods
 *
 * @author lsuriano
 */
class CHardwarePrinter extends CPrinter {

	/*
	 * Variable to check if we are using PAPIFY or not --> Will be updated during preprocessing
	 */
	int usingPapify = 0;

	/**
	 * Variable that store the number or iteration in hardware. Using it, it will be possible to
	 * compress all the function calls in just one.
	 */
	protected var int factorNumber = 0;
	protected var int functionCallNumber = 0;
	protected var int dataTransferCallNumber = 0;
	protected var int dataOutputTransferCallNumber = 0;
	protected var int numberHardwareAcceleratorSlots = 0;
	protected var int threadHardwarePrintedDeclaration = 0;
	protected var int threadHardwarePrintedUsage = 0;
	protected var int DataTransferActionNumber = 0;
	protected var int FreeDataTransferBufferNumber = 0;
	protected var int PapifyFunctionCallNumberInitBlock = 0;
	protected var int PapifyDefinitionsNumbers =0
	protected var long CoreIDHardwareFpgaPapify = -1;
	protected var Map<String, String> listOfHwFunctions = new LinkedHashMap<String, String>();

	override printCoreBlockHeader(CoreBlock block) '''
		«super.printCoreBlockHeader(block)»
			#include "hardwarelib.h"
		#include "hardware_accelerator_setup.h"
	'''

	override printCoreInitBlockHeader(CallBlock callBlock) '''
		«super.printCoreInitBlockHeader(callBlock)»
		// Initialize Hardware infrastructure
		hardware_init();
	'''

	override printCoreLoopBlockFooter(LoopBlock block2) '''
			pthread_barrier_wait(&iter_barrier);
		}
		// Release kernel instance of the function
		hardware_kernel_release(«IF this.listOfHwFunctions.size == 1»"«this.listOfHwFunctions.entrySet.get(0).key»"«ELSE»«PreesmLogger.getLogger().log(Level.SEVERE, "Hardware Codegen ERROR. Multiple hardware functions were detected. This feature is still under developing")»«ENDIF»);

		// Clean Hardware setup
		hardware_exit();

		return NULL;
		}

		«IF block2.codeElts.empty»
		// This call may inform the compiler that the main loop of the thread does not call any function.
		void emptyLoop_«(block2.eContainer as CoreBlock).name»(){

		}
		«ENDIF»
	'''

	override generateStandardLibFiles() {
		val result = super.generateStandardLibFiles();
		val String stdFileFolderHardware = "/stdfiles/hardware/"
		val filesHardware = Arrays.asList(#[
			"hardware.c",
			"hardware.h",
			"hardware_hw.c",
			"hardware_hw.h",
			"hardware_rcfg.c",
			"hardware_rcfg.h",
			"hardware_dbg.h"
		]);
		filesHardware.forEach [ it |
			try {
				result.put(it, PreesmResourcesHelper.instance.read(stdFileFolderHardware + it, this.class))
			} catch (IOException exc) {
				throw new PreesmRuntimeException("Could not generated content for " + it, exc)
			}
		]
		return result
	}

	override String printMain(List<Block> printerBlocks) '''
		/**
		 * @file main.c
		 * @generated by «this.class.simpleName»
		 * @date «new Date»
		 *
		 */
		// no monitoring by default

		#define _PREESM_NBTHREADS_ «engine.codeBlocks.size-(this.numberHardwareAcceleratorSlots)+1»
		#define _PREESM_MAIN_THREAD_ «mainOperatorId»

		// application dependent includes
		#include "preesm_gen.h"

		// Declare computation thread functions
		«FOR coreBlock : engine.codeBlocks»
			«IF !((coreBlock as CoreBlock).coreType.equals("Hardware"))»
				void *computationThread_Core«(coreBlock as CoreBlock).coreID»(void *arg);
			«ELSE»
				«IF this.threadHardwarePrintedDeclaration == 0»
					void *computationThread_Core«(coreBlock as CoreBlock).coreID»(void *arg);
					//«this.threadHardwarePrintedDeclaration=1»
				«ENDIF»
			«ENDIF»
		«ENDFOR»

		pthread_barrier_t iter_barrier;
		int preesmStopThreads;


		unsigned int launch(unsigned int core_id, pthread_t * thread, void *(*start_routine) (void *)) {

		#ifdef _WIN32
			SYSTEM_INFO sysinfo;
			GetSystemInfo(&sysinfo);
			unsigned int numCPU = sysinfo.dwNumberOfProcessors;
		#else
			unsigned int numCPU = sysconf(_SC_NPROCESSORS_ONLN);
		#endif

			// init pthread attributes
			pthread_attr_t attr;
			pthread_attr_init(&attr);

			// check CPU id is valid
			if (core_id >= numCPU) {
			// leave attribute uninitialized
			printf("** Warning: thread %d will not be set with specific core affinity \n   due to the lack of available dedicated cores.\n",core_id);
			} else {
		#ifdef __APPLE__
				// NOT SUPPORTED
		#else
				// init cpuset struct
				cpu_set_t cpuset;
				CPU_ZERO(&cpuset);
				CPU_SET(core_id, &cpuset);

				// set pthread affinity
				pthread_attr_setaffinity_np(&attr, sizeof(cpuset), &cpuset);
		#endif
			}

			// create thread
			pthread_create(thread, &attr, start_routine, NULL);
			return 0;
		}


		int main(void) {
			«IF this.usingPapify == 1»
				#ifdef _PREESM_PAPIFY_MONITOR
				mkdir("papify-output", 0777);
				event_init_multiplex();
				#endif
			«ENDIF»
			// Declaring thread pointers
			pthread_t coreThreads[_PREESM_NBTHREADS_];
			void *(*coreThreadComputations[_PREESM_NBTHREADS_])(void *) = {
		«FOR coreBlock : engine.codeBlocks»
			«IF !((coreBlock as CoreBlock).coreType.equals("Hardware"))»		&computationThread_Core«(coreBlock as CoreBlock).coreID»«if(engine.codeBlocks.last == coreBlock) {""} else {", "}»
			«ELSE»
				«IF this.threadHardwarePrintedUsage == 0»		&computationThread_Core«(coreBlock as CoreBlock).coreID»
					// «this.threadHardwarePrintedUsage=1»
				«ENDIF»
			«ENDIF»
		«ENDFOR»
			};

		#ifdef PREESM_VERBOSE
			printf("Launched main\n");
		#endif

			// Creating a synchronization barrier
			preesmStopThreads = 0;
			pthread_barrier_init(&iter_barrier, NULL, _PREESM_NBTHREADS_);

			communicationInit();

			// Creating threads
			for (int i = 0; i < _PREESM_NBTHREADS_; i++) {
			if (i != _PREESM_MAIN_THREAD_) {
				if(launch(i,&coreThreads[i],coreThreadComputations[i])) {
					printf("Error: could not launch thread %d\n",i);
					return 1;
				}
			}
			}

			// run main operator code in this thread
			coreThreadComputations[_PREESM_MAIN_THREAD_](NULL);

			// Waiting for thread terminations
			for (int i = 0; i < _PREESM_NBTHREADS_; i++) {
			if (i != _PREESM_MAIN_THREAD_) {
				pthread_join(coreThreads[i], NULL);
			}
			}
			«IF this.usingPapify == 1»
				#ifdef _PREESM_PAPIFY_MONITOR
				event_destroy();
				#endif
			«ENDIF»
			return 0;
		}

	'''

	override printFunctionCall(FunctionCall functionCall) '''
		hardware_kernel_execute("«functionCall.name»",gsize_TO_BE_CHANGED«IF (functionCall.factorNumber > 0)» * «functionCall.factorNumber»«ENDIF», lsize_TO_BE_CHANGED); // executing hardware kernel
		hardware_kernel_wait("«functionCall.name»");
	'''

	override printDataTansfer(DataTransferAction action) '''
		// Hardware³ data transfer token into Global Buffer
		«var count = 0»
		«FOR buffer : action.buffers»
			«IF (action.parameterDirections.get(count).toString == 'INPUT')»
				memcpy((void *) global_hardware_«count» + («buffer.size» * «this.dataTransferCallNumber» * sizeof(a3data_t)), (void *)«buffer.name», «buffer.size»*sizeof(a3data_t)); // input «count++»
			«ELSE»
				// output «count++»
			«ENDIF»
		«ENDFOR»
		//«this.dataTransferCallNumber++»
	'''

	override printOutputDataTransfer(OutputDataTransfer action) '''
		// Hardware³ data transfer token output
		«var count = 0»
		«FOR buffer : action.buffers»
			«IF (action.parameterDirections.get(count).toString == 'INPUT')»
				// input «count++»
			«ELSE»
				memcpy((void *)«buffer.name», (void *) global_hardware_«count» + («buffer.size» * «this.dataOutputTransferCallNumber» * sizeof(a3data_t)), «buffer.size»*sizeof(a3data_t)); // output «count++»
			«ENDIF»
		«ENDFOR»
		//«this.dataOutputTransferCallNumber++»
	'''

	override printRegisterSetUp(RegisterSetUpAction action) '''
		«var count = 0»
		«FOR param : action.parameters»
			for (int i = 0; i < MAX_NACCS; i++) {
				wcfg_temp[i] = «param.doSwitch»;
			}
			hardware_kernel_wcfg("«action.name»", A3_ACCELERATOR_REG_«(count++).toString()», wcfg_temp);
		«ENDFOR»
	'''

	override printFpgaLoad(FpgaLoadAction action) '''

		// Create kernel instance
		hardware_kernel_create("«action.name»", SIZE_MEM_HW, N_MEMORY_BANKS, N_REGISTERS);

		a3data_t wcfg_temp[MAX_NACCS];

		for (int i = 0; i < MAX_NACCS; i++) {
			hardware_load("«action.name»", i, 0, 0, 1);
		}
	'''

	override printFreeDataTransferBuffer(FreeDataTransferBuffer action) ''''''

	override printGlobalBufferDeclaration(GlobalBufferDeclaration action) '''
		// Hardware³ global data buffer declaration
		«var count = 0»
		«FOR buffer : action.buffers»
			a3data_t *global_hardware_«count» = NULL;
			global_hardware_«count» = hardware_alloc(«buffer.size»«IF (this.factorNumber > 0)» * «this.factorNumber»«ENDIF» * sizeof *«buffer.name», "«action.name»", "«buffer.doSwitch»",  «action.parameterDirections.get(count++)»);
		«ENDFOR»
	'''
	
	override printPapifyFunctionCall(PapifyFunctionCall papifyFunctionCall) '''
	«IF papifyFunctionCall.opening == true»
		#ifdef _PREESM_PAPIFY_MONITOR
	«ENDIF»
	«papifyFunctionCall.name»(«FOR param : papifyFunctionCall.parameters SEPARATOR ','»«param.doSwitch»«ENDFOR»); // «papifyFunctionCall.actorName»
	«IF papifyFunctionCall.closing == true»
		#endif
	«ENDIF»
	'''
	
	override printPapifyActionDefinition(PapifyAction action) '''
	«IF action.opening == true»
		#ifdef _PREESM_PAPIFY_MONITOR
	«ENDIF»
	«action.type» «action.name»; // «action.comment»
	«IF action.closing == true»
		#endif
	«ENDIF»
	'''
	
	override printPapifyActionParam(PapifyAction action) '''&«action.name»'''

	override preProcessing(List<Block> printerBlocks, Collection<Block> allBlocks) {
		PreesmLogger.getLogger().info("[HARDWARE] preProcessing for Hardware. The elements to be processed are " +
			printerBlocks.size());
		var RegisterSetUpNumber = 0;
		var lastFunctionCallIndex = 0;
		var currentFunctionPosition = 0;

		this.numberHardwareAcceleratorSlots = printerBlocks.size();

//		var CoreBlock coreLoopMerged = CodegenModelUserFactory.createCoreBlock(null);
		var Block coreLoopMerged = CodegenModelUserFactory.createCoreBlock(null);

		/*
		 * To insert all the elements of loopBlock of every instance of printerBlocks in a Unique Block
		 * called blockMerged.
		 */
		for (Block block : printerBlocks) {
			var coreLoop = (block as CoreBlock).loopBlock
			var blockMerged = (coreLoopMerged as CoreBlock)
			var clonedElts = coreLoop.codeElts.clone()
			blockMerged.loopBlock.codeElts.addAll(clonedElts)
		}

		/* to add all the elements of the blockMerged.loopBlock.codeElts inside
		 * the first block of the printersBlock: only this one will be printed and the others deleted */
		var Block firstBlock = printerBlocks.get(0)
		var coreLoopFinal = (firstBlock as CoreBlock).loopBlock
		var blockLoopMerged = (coreLoopMerged as CoreBlock).loopBlock
		var clonedElts = blockLoopMerged.codeElts.clone()
		coreLoopFinal.codeElts.addAll(clonedElts)

		/* The following commented lines are going to be VERY important for possible future modification where
		 * multiple GlobalBufferDeclaration should be used in the same file.
		 */
//		/* to repeat the same operation for the initBlock (only the buffers declarations) */
//
//		var List bufferCopyList = new ArrayList();
//		var List parameterDirectionsCopyList = new ArrayList();
//		for (Block block : printerBlocks) {
//			var coreInit = (block as CoreBlock).initBlock
//			var iteratorInit = coreInit.codeElts.size
//			for (var i = 0; i < iteratorInit; i++){
//				var elementInit = coreInit.codeElts.get(i)
//				if (elementInit instanceof GlobalBufferDeclaration){
//					var buffersCopy = (elementInit as GlobalBufferDeclarationImpl).getBuffers
//					var parameterDirectionCopy = (elementInit as GlobalBufferDeclarationImpl).getParameterDirections
//					bufferCopyList.addAll(buffersCopy)
//					parameterDirectionsCopyList.addAll(parameterDirectionCopy)
//
//					//PreesmLogger.getLogger().info("[LEO] try to copy the buffers and subbuffers.");
//				}
//			}
//		}
//		// inserting all the buffers found in the first element of the printersBlock
//		var coreInitFinal = (firstBlock as CoreBlock).initBlock
//		var iteratorInit = coreInitFinal.codeElts.size
//		for (var i = 0; i < iteratorInit; i++){
//			var elementInit = coreInitFinal.codeElts.get(i)
//			if (elementInit instanceof GlobalBufferDeclaration){
//				(elementInit as GlobalBufferDeclarationImpl).getBuffers.addAll(bufferCopyList)
//				(elementInit as GlobalBufferDeclarationImpl).getParameterDirections.addAll(parameterDirectionsCopyList)
//			}
//		}
		/* the same operation MUST be done with the global declaration as well. The declaration and definition can be found directly inside the codeBlock */
		
		// Declarations
		var bufferCopyDeclarationList = new ArrayList();
		// Definitions
		var bufferCopyDefinitionsList = new ArrayList();
		// init
		var bufferCopyInitList = new ArrayList();
		
		
		for (Block block : printerBlocks) {
			bufferCopyDeclarationList.addAll((block as CoreBlock).declarations)
			bufferCopyDefinitionsList.addAll((block as CoreBlock).definitions)
			bufferCopyInitList.addAll((block as CoreBlock).initBlock.codeElts)
			PreesmLogger.getLogger().info("[HARDWARE] copying buffers and subbuffers and definitions.");
		}
		(firstBlock as CoreBlock).declarations.addAll(bufferCopyDeclarationList)
		(firstBlock as CoreBlock).definitions.addAll(bufferCopyDefinitionsList)
		(firstBlock as CoreBlock).initBlock.codeElts.addAll(bufferCopyInitList)
		
		// even with more that one hardware SLOT, the file that should be created is just one.
		// Storing the values that MUST be always used
		this.CoreIDHardwareFpgaPapify = (firstBlock as CoreBlock).coreID
		var block = printerBlocks.get(0);

		/*
		 * The strategy is:
		 * to delete all the FunctionCallImpl and to keep just the LAST one. All the other FunctionCallImpl are
		 * replaced with the "Data Motion".
		 *
		 */
		/*
		 * In order to have a unique file calling all the PEs of the Hardware (many SLOTs can be used), the
		 * operation (described above) MUST be performed for every element of the printerBlocks.
		 * Operator in the S-LAM ---> one element of the printerBlocks.
		 * Additionally, only one element of the printerBlocks should be kept in the list (that MUST be created to reflect the actors firing
		 * of the original set of files).
		 *
		 */
		var coreLoop = (block as CoreBlock).loopBlock
		var i = 0;
		this.functionCallNumber = 0;
		// This Loop just locate where the function are and how many they are.
		while (i < coreLoop.codeElts.size) {
			// Retrieve the function ID
			val elt = coreLoop.codeElts.get(i)
			if (elt instanceof ActorFunctionCall) {
				this.functionCallNumber++;
				lastFunctionCallIndex = i;
				if (this.functionCallNumber > 0) {
					// coreLoop.codeElts.remove(i);
				}
			}
			i++;
		}

		// This loop adds the new information on the class to be printed and substitute the old functions with the new ones
		// Note that the function to be kept is the last one!
		if (this.functionCallNumber > 0) {
			currentFunctionPosition = lastFunctionCallIndex;
			var functionCallImplOld = (block as CoreBlock).loopBlock.codeElts.get(lastFunctionCallIndex);
			// checking that the function to be changed is the right one
			if (! (functionCallImplOld instanceof ActorFunctionCall)) {
				PreesmLogger.getLogger().log(Level.SEVERE,
					"Hardware Codegen ERROR in the preProcessing function. The functionCall to be modified was NOT found");
			} else {
				// create a new function identical to the Old one
				var functionCallImplNew = (functionCallImplOld as FunctionCall);
				// storing the name of the function to be executed in hardware in a global dictionary
				if (this.listOfHwFunctions.empty) {
					this.listOfHwFunctions.put(functionCallImplNew.name, functionCallImplNew.name);
				} else {
					if (!this.listOfHwFunctions.containsKey(functionCallImplNew.name)) {
						this.listOfHwFunctions.put(functionCallImplNew.name, functionCallImplNew.name);
					}
				}

				// set the new value in the new version of the element of the list
				functionCallImplNew.factorNumber = this.functionCallNumber;
				// replace the old element with the new one
				(block as CoreBlock).loopBlock.codeElts.set(lastFunctionCallIndex, functionCallImplNew);
			}
			PreesmLogger.getLogger().info("[HARDWARE] number of FunctionCallImpl " + this.functionCallNumber);
		}

		// this loop is to delete all the functions but not the last one (the new one!)
		// the variable i start from the end and goes back until the first function
		i = coreLoop.codeElts.size - 1;
		var flagFirstFunctionFound = 0;
		while (i > 0) {
			// Retrieve the function ID
			val elt = coreLoop.codeElts.get(i)
			if ((elt instanceof ActorFunctionCall) && flagFirstFunctionFound == 0) {
				//keep the last one detected
				flagFirstFunctionFound++;

			} else if ((elt instanceof ActorFunctionCall) && flagFirstFunctionFound > 0) {
				//remove all the other different from the last one
				coreLoop.codeElts.remove(i);
				currentFunctionPosition--;
			}
			i--;
		}

		// this loop is for the data transfer. A new DataTrasfer (a replica) is added AFTER the FunctionCall,
		// one for every buffer used!
		i = 0;
		this.DataTransferActionNumber = 0;
		var positionOfNewDataTransfer = currentFunctionPosition;
		while (i < coreLoop.codeElts.size) {
			// Retrieve the function ID
			val elt = coreLoop.codeElts.get(i)
			if (elt instanceof DataTransferAction) {
				this.DataTransferActionNumber++;
				positionOfNewDataTransfer++;
				if (DataTransferActionNumber > 0) {
					// coreLoop.codeElts.remove(i);
				}
			}
			i++;
		}
		// the last loop is for the Free data transfer (that actually does nothing)
		i = 0;
		FreeDataTransferBufferNumber = 0;
		while (i < coreLoop.codeElts.size) {
			// Retrieve the function ID
			val elt = coreLoop.codeElts.get(i)
			if ((elt instanceof FreeDataTransferBuffer) && FreeDataTransferBufferNumber == 0) {
				FreeDataTransferBufferNumber++;
			// firstFreeDataIndex = i;
			} else if ((elt instanceof FreeDataTransferBuffer) && FreeDataTransferBufferNumber > 0) {
				FreeDataTransferBufferNumber++;
			// coreLoop.codeElts.remove(i);
			}
			i++;
		}

		// this loop is for the OutputDataTransfer. All the OUTPUT DataTransfer will be deleted
		// and inserted after the function execution
		i = 0;
		var OutputDataTransferActionNumber = 0;
		val cloneCoreLoop = coreLoop.codeElts.clone
		while (i < coreLoop.codeElts.size) {
			// Retrieve the function ID
			val elt = coreLoop.codeElts.get(i)
			if (elt instanceof OutputDataTransfer) {
				OutputDataTransferActionNumber++;
				currentFunctionPosition--
				coreLoop.codeElts.remove(i);
			}
			i++;
		}
		// the last one is deleted after the function execution and does not count
		currentFunctionPosition++

		// all the OutputDataTransfer should be inserted again but AFTER the function call
		var countOutputDataTransferInserted = 0
		i = 0
		while (i < cloneCoreLoop.size) {
			// Retrieve the function ID
			val elt = cloneCoreLoop.get(i)
			if (elt instanceof OutputDataTransfer) {
				countOutputDataTransferInserted++
				coreLoop.codeElts.add(currentFunctionPosition + countOutputDataTransferInserted, elt)
			}
			i++
		}
		PreesmLogger.getLogger().info("[HARDWARE] number of OutputDataTransfer inserted is " +
			countOutputDataTransferInserted);
		// it is enough to set up the register just once at the beginning.
		i = 0;
		RegisterSetUpNumber = 0;
		while (i < coreLoop.codeElts.size) {
			// Retrieve the function ID
			val elt = coreLoop.codeElts.get(i)
			if ((elt instanceof RegisterSetUpAction) && RegisterSetUpNumber == 0) {
				RegisterSetUpNumber++;
			// firstRegisterSetUp = i;
			} else if ((elt instanceof RegisterSetUpAction) && RegisterSetUpNumber > 0) {
				RegisterSetUpNumber++;
				coreLoop.codeElts.remove(i);
			}
			i++;
		}

		// storing the functionCallNumber that may be used by other printers
		if (functionCallNumber == DataTransferActionNumber && functionCallNumber == FreeDataTransferBufferNumber) {
			factorNumber = functionCallNumber;
		} else {
			PreesmLogger.getLogger().log(Level.SEVERE,
				"Hardware Codegen ERROR in the preProcessing function. Different number of function calls and data transfers were detected");
		
		// ----------- PAPIFY - automatic instrumentation of ARTICo³ using PAPIFY --------------
		
		//PAPIFY - delete all functions
		// this loop is to delete all the functions but not the last one (the new one!)
		// the variable i start from the end and goes back until the first function
		i = coreLoop.codeElts.size-1;
		var flagFirstFunctionPAPIFYFoundEVENTSTART = 0;
		var flagFirstFunctionPAPIFYFoundEVENTSTOP = 0;
		var flagFirstFunctionPAPIFYFoundTIMINGSTART = 0;
		var flagFirstFunctionPAPIFYFoundTIMINGSTOP = 0;
		var flagFirstFunctionPAPIFYFoundWRITE = 0;
		var firstPapifyFunctionFound = 0;
		while (i > 0) {
			// Retrieve the function ID
			val elt = coreLoop.codeElts.get(i)
			if ((elt instanceof PapifyFunctionCall)) {
				var papifyTypeVariable =elt.papifyType
				// even with more that one hardware SLOT, the file that should be created is just one.
				// Storing the values that MUST be always used
				//this.CoreIDHardwareFpgaPapify
				if (firstPapifyFunctionFound == 0 ){
					for (param : elt.parameters){
						if (param instanceof Constant && param.name.equals("PE_id")){
							this.CoreIDHardwareFpgaPapify = (param as Constant).value
							PreesmLogger.getLogger().info("[HARDWARE] PE_id set up to " + this.CoreIDHardwareFpgaPapify);
						}
					}
					//this.CoreIDHardwareFpgaPapify = elt.parameters
					firstPapifyFunctionFound++
				} else {
					for (param : elt.parameters){
						if (param instanceof Constant && param.name.equals("PE_id")){
							(param as Constant).value = this.CoreIDHardwareFpgaPapify
						}
					}
				}
				elt.closing = false
				elt.opening = false
				switch (papifyTypeVariable){
					case EVENTSTART:
						if(flagFirstFunctionPAPIFYFoundEVENTSTART == 0){
							//keep the last one detected
							flagFirstFunctionPAPIFYFoundEVENTSTART++;
						}
						else if(flagFirstFunctionPAPIFYFoundEVENTSTART > 0 ){
							//remove all the other different from the last one
							flagFirstFunctionPAPIFYFoundEVENTSTART++;
							coreLoop.codeElts.remove(i);
						} 
					case EVENTSTOP:
						if(flagFirstFunctionPAPIFYFoundEVENTSTOP == 0){
							//keep the last one detected
							flagFirstFunctionPAPIFYFoundEVENTSTOP++;
						}
						else if(flagFirstFunctionPAPIFYFoundEVENTSTOP > 0 ){
							//remove all the other different from the last one
							flagFirstFunctionPAPIFYFoundEVENTSTOP++;
							coreLoop.codeElts.remove(i);
						}
					case TIMINGSTART:
						if(flagFirstFunctionPAPIFYFoundTIMINGSTART == 0){
							//keep the last one detected
							flagFirstFunctionPAPIFYFoundTIMINGSTART++;
						}
						else if(flagFirstFunctionPAPIFYFoundTIMINGSTART > 0 ){
							//remove all the other different from the last one
							flagFirstFunctionPAPIFYFoundTIMINGSTART++;
							coreLoop.codeElts.remove(i);
						}
					case TIMINGSTOP:
						if(flagFirstFunctionPAPIFYFoundTIMINGSTOP == 0){
							//keep the last one detected
							flagFirstFunctionPAPIFYFoundTIMINGSTOP++;
						}
						else if(flagFirstFunctionPAPIFYFoundTIMINGSTOP > 0 ){
							//remove all the other different from the last one
							flagFirstFunctionPAPIFYFoundTIMINGSTOP++;
							coreLoop.codeElts.remove(i);
						}
					case WRITE:
						if(flagFirstFunctionPAPIFYFoundWRITE == 0){
							//keep the last one detected
							flagFirstFunctionPAPIFYFoundWRITE++;
						}
						else if(flagFirstFunctionPAPIFYFoundWRITE > 0 ){
							//remove all the other different from the last one
							flagFirstFunctionPAPIFYFoundWRITE++;
							coreLoop.codeElts.remove(i);
						}
					default:
					PreesmLogger.getLogger().log(Level.SEVERE, "Hardware Codegen ERROR in the preProcessing function. papifyType NOT recognized.")
				}				
			}
			i--;
		}
		
		//deleting all the PAPIFY function useless when using hardware. Keeping just the last one.
		//var coreLoop = (block as CoreBlock).loopBlock
		var initBlock = (block as CoreBlock).initBlock
		var iteratorPapify = initBlock.codeElts.size-1;
		// This Loop just locate where the function are and how many they are.
		while (iteratorPapify >= 0) {
			// Retrieve the function ID
			val elt = initBlock.codeElts.get(iteratorPapify)
			if (elt instanceof PapifyFunctionCall) {
				if (this.PapifyFunctionCallNumberInitBlock != 0 && (elt.papifyType != PapifyType.CONFIGPE ) ) {
					initBlock.codeElts.remove(iteratorPapify);
				}
				this.PapifyFunctionCallNumberInitBlock++;
				elt.closing = false
				elt.opening = false
			}
			iteratorPapify--;
		}
		
		//deleting all the definitions and keeping just the last one
		
		var definitionsBlock = (block as CoreBlock).definitions
		var iteratorDefinitionsPapify = definitionsBlock.size -1
		while (iteratorDefinitionsPapify >= 0) {
			val elt = definitionsBlock.get(iteratorDefinitionsPapify)
			if (elt instanceof PapifyAction) {
				if (this.PapifyDefinitionsNumbers != 0){
					definitionsBlock.remove(iteratorDefinitionsPapify)
				}
				this.PapifyDefinitionsNumbers++
				elt.closing = false
				elt.opening = false
			} 
			iteratorDefinitionsPapify--
		}
		

		/* Removing unuseful elements in the list of printersBlock. To keep just the fist one */
		var numberOfSlotDetected = printerBlocks.size
		for (var j = numberOfSlotDetected - 1; j >= 1; j--) {
			printerBlocks.remove(j)
		}

		PreesmLogger.getLogger().info("[HARDWARE] End of the Hardware preProcessing.");
		/*
		 * Preprocessing for Papify
		 */
		for (cluster : allBlocks) {
			if (cluster instanceof CoreBlock) {
				for (CodeElt codeElt : cluster.loopBlock.codeElts) {
					if (codeElt instanceof PapifyFunctionCall) {
						this.usingPapify = 1;
					}
				}
			}
		}
	}

}
