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
package org.ietr.preesm.codegen.xtend.multiSDFTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.task.CodegenEngine;
import org.ietr.preesm.codegen.xtend.task.CodegenModelGenerator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class MultiCodegenTask extends AbstractTaskImplementation {

	public static final String PARAM_PRINTER = "Printer";
	public static final String VALUE_PRINTER_IR = "IR";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(
	 * java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
	 * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
	 */
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Retrieve inputs
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
		Design archi = (Design) inputs.get("architecture");
		@SuppressWarnings("unchecked")
		Map<DirectedAcyclicGraph, Map<String, MemoryExclusionGraph>> dagsAndMemExs = (Map<DirectedAcyclicGraph, Map<String,MemoryExclusionGraph>>) inputs
				.get(KEY_DAG_AND_MEM_EX_MAP);

		for (DirectedAcyclicGraph dag : dagsAndMemExs.keySet()) {
			Map<String, MemoryExclusionGraph> megs = dagsAndMemExs.get(dag);

			// Generate intermediate model
			CodegenModelGenerator generator = new CodegenModelGenerator(archi,
					dag, megs, scenario, workflow);

			List<Block> codeBlocks = new ArrayList<>(generator.generate());

			// Retrieve the desired printer
			String selectedPrinter = parameters.get(PARAM_PRINTER);

			// Do the print
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.getRoot().getLocation();
			String codegenPath = scenario.getCodegenManager()
					.getCodegenDirectory() + "/";

			// Create the codegen engine
			CodegenEngine engine = new CodegenEngine(scenario, workspace,
					codegenPath, codeBlocks);

			if (selectedPrinter.equals(VALUE_PRINTER_IR)) {
				engine.initializePrinterIR(codegenPath);
			}

			// Fill a map associating printers with printed files
			engine.registerPrintersAndBlocks(selectedPrinter);

			// Pre-process the printers one by one to:
			engine.preprocessPrinters();

			// Do the print for all Blocks
			engine.print();
		}

		// Create empty output map
		Map<String, Object> output = new HashMap<String, Object>();
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		String avilableLanguages = "? C {";

		// Retrieve the languages registered with the printers
		Set<String> languages = new HashSet<String>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IConfigurationElement[] elements = registry
				.getConfigurationElementsFor("org.ietr.preesm.codegen.xtend.printers");
		for (IConfigurationElement element : elements) {
			languages.add(element.getAttribute("language"));
		}

		for (String lang : languages) {
			avilableLanguages += lang + ", ";
		}

		avilableLanguages += VALUE_PRINTER_IR + "}";

		parameters.put(PARAM_PRINTER, avilableLanguages);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generate xtend code";
	}
}
