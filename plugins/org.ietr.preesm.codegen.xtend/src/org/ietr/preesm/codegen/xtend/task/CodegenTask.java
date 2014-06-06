package org.ietr.preesm.codegen.xtend.task;

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
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class CodegenTask extends AbstractTaskImplementation {

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
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

		// Generate intermediate model
		CodegenModelGenerator generator = new CodegenModelGenerator(archi, dag,
				memEx, scenario, workflow);

		List<Block> codeBlocks = new ArrayList<>(generator.generate());

		// Retrieve the desired printer
		String selectedPrinter = parameters.get(PARAM_PRINTER);

		// Do the print
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.getRoot().getLocation();
		String codegenPath = scenario.getCodegenManager().getCodegenDirectory()
				+ "/";
		
		// Create the codegen engine
		CodegenEngine engine = new CodegenEngine(scenario, workspace, codegenPath, codeBlocks);

		if (selectedPrinter.equals(VALUE_PRINTER_IR)) {
			engine.initializePrinterIR(codegenPath);
		}

		// Fill a map associating printers with printed files
		engine.registerPrintersAndBlocks(selectedPrinter);

		// Pre-process the printers one by one to:
		engine.preprocessPrinters();

		// Do the print for all Blocks
		engine.print();

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
