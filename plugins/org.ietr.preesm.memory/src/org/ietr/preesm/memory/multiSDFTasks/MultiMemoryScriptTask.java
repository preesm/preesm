/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
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
 ******************************************************************************/

package org.ietr.preesm.memory.multiSDFTasks;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.script.AbstractMemoryScriptTask;
import org.ietr.preesm.memory.script.MemoryScriptEngine;

public class MultiMemoryScriptTask extends AbstractMemoryScriptTask {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		// Get verbose parameter
		boolean verbose = false;
		verbose = parameters.get(PARAM_VERBOSE).equals(VALUE_TRUE);

		// Get the log parameter
		String log = parameters.get(PARAM_LOG);

		// Retrieve the alignment param
		String valueAlignment = parameters
				.get(AbstractMemoryAllocatorTask.PARAM_ALIGNMENT);

		// Get the data types from the scenario
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
		Map<String, DataType> dataTypes = scenario.getSimulationManager()
				.getDataTypes();

		// Get check policy
		String checkString = parameters.get(PARAM_CHECK);

		// Retrieve the input of the task
		@SuppressWarnings("unchecked")
		Map<DirectedAcyclicGraph, MemoryExclusionGraph> dagsAndMemExs = (Map<DirectedAcyclicGraph, MemoryExclusionGraph>) inputs
				.get(KEY_DAG_AND_MEM_EX_MAP);

		for (DirectedAcyclicGraph dag : dagsAndMemExs.keySet()) {
			MemoryExclusionGraph meg = dagsAndMemExs.get(dag);

			// execute
			MemoryScriptEngine engine = new MemoryScriptEngine(valueAlignment,
					log, verbose, scenario);
			engine.runScripts(dag, dataTypes, checkString);
			engine.updateMemEx(meg);

			if (!log.equals("")) {
				// generate
				engine.generateCode(scenario, log + dag.getName());
			}
		}

		// Outputs
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(KEY_DAG_AND_MEM_EX_MAP, dagsAndMemExs);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> param = new HashMap<String, String>();
		param.put(PARAM_VERBOSE, "? C {" + VALUE_TRUE + ", " + VALUE_FALSE
				+ "}");
		param.put(PARAM_CHECK, "? C {" + VALUE_CHECK_NONE + ", "
				+ VALUE_CHECK_FAST + ", " + VALUE_CHECK_THOROUGH + "}");
		param.put(AbstractMemoryAllocatorTask.PARAM_ALIGNMENT,
				AbstractMemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
		param.put(PARAM_LOG, VALUE_LOG);

		return param;
	}

	@Override
	public String monitorMessage() {
		return "Running Memory Optimization Scripts.";
	}

}
