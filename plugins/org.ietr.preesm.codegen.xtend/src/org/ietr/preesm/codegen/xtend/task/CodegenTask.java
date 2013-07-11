package org.ietr.preesm.codegen.xtend.task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.printer.CPrinter;
import org.ietr.preesm.codegen.xtend.printer.CodegenAbstractPrinter;
import org.ietr.preesm.codegen.xtend.printer.InstrumentedCPrinter;
import org.ietr.preesm.codegen.xtend.printer.XMLPrinter;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class CodegenTask extends AbstractTaskImplementation {

	public static final String PARAM_PRINTER = "Printer";
	public static final String VALUE_PRINTER_XML = "XML";
	public static final String VALUE_PRINTER_C = "C";
	public static final String VALUE_PRINTER_INSTRUMENTED_C = "InstrumentedC";
	public static final String VALUE_PRINTER_IR = "IR";
	public static final String VALUE_PRINTER_DEFAULT = "? C {"
			+ VALUE_PRINTER_XML + ", " + VALUE_PRINTER_C + ", "
			+ VALUE_PRINTER_INSTRUMENTED_C + ", " + VALUE_PRINTER_IR + "}";

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
				memEx, scenario);

		List<Block> codeBlocks = new ArrayList<>(generator.generate());

		// Retrieve the desired printer
		String selectedPrinter = parameters.get(PARAM_PRINTER);

		// Do the print
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.getRoot().getLocation();
		String codegenPath = scenario.getCodegenManager().getCodegenDirectory()
				+ "/";

		if (selectedPrinter.equals(VALUE_PRINTER_IR)) {
			// Save the intermediate model
			// Register the XMI resource factory for the .codegen extension
			Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
			Map<String, Object> m = reg.getExtensionToFactoryMap();
			m.put("codegen", new XMIResourceFactoryImpl());

			// Obtain a new resource set
			ResourceSet resSet = new ResourceSetImpl();

			for (Block b : codeBlocks) {
				// Create a resource
				scenario.getCodegenManager().getCodegenDirectory();
				Resource resource = resSet.createResource(URI
						.createURI(codegenPath + b.getName() + ".codegen"));
				// Get the first model element and cast it to the right type, in
				// my
				// example everything is hierarchical included in this first
				// node
				resource.getContents().add(b);
			}

			// Now save the content.
			for (Resource resource : resSet.getResources()) {
				try {
					resource.save(Collections.EMPTY_MAP);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		CodegenAbstractPrinter printer;
		String extension = null;
		switch (selectedPrinter) {
		case VALUE_PRINTER_C:
			printer = new CPrinter();
			extension = ".c";
			break;
		case VALUE_PRINTER_INSTRUMENTED_C:
			printer = new InstrumentedCPrinter();
			extension = ".c";
			break;
		case VALUE_PRINTER_XML:
			printer = new XMLPrinter();
			extension = ".xml";
			break;
		case VALUE_PRINTER_IR:
			// Already printed
			printer = null;
			break;
		default:
			throw new WorkflowException(selectedPrinter
					+ " is not a valid printer name. "
					+ "Select a printer in the following list: "
					+ VALUE_PRINTER_DEFAULT);

		}

		// Print
		if (printer != null && extension != null) {

			// Erase previous files with extension
			// Lists all files in folder
			IFolder f = workspace.getRoot().getFolder(new Path(codegenPath));
			File folder = new File(f.getRawLocation().toOSString());
			File fList[] = folder.listFiles();
			// Searches .extension
			for (int i = 0; i < fList.length; i++) {
				String pes = fList[i].getName();
				if (pes.endsWith(extension)) {
					// and deletes
					fList[i].delete();
				}
			}

			// Print the file
			printer.preProcessing(codeBlocks);
			for (Block b : codeBlocks) {
				IFile iFile = workspace.getRoot().getFile(
						new Path(codegenPath + b.getName() + extension));
				try {
					if (!iFile.exists()) {
						iFile.create(null, false, new NullProgressMonitor());
					}
					iFile.setContents(new ByteArrayInputStream(printer
							.doSwitch(b).toString().getBytes()), true, false,
							new NullProgressMonitor());
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}

			// Print secondary files
			for (Entry<String, CharSequence> entry : printer
					.createSecondaryFiles(codeBlocks).entrySet()) {
				IFile iFile = workspace.getRoot().getFile(
						new Path(codegenPath + entry.getKey()));
				try {
					if (!iFile.exists()) {
						iFile.create(null, false, new NullProgressMonitor());
					}
					iFile.setContents(new ByteArrayInputStream(entry.getValue()
							.toString().getBytes()), true, false,
							new NullProgressMonitor());
				} catch (CoreException e1) {
					e1.printStackTrace();
				}

			}
		}

		// Create empty output map
		Map<String, Object> output = new HashMap<String, Object>();
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_PRINTER, VALUE_PRINTER_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generate xtend code";
	}

}
