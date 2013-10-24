package org.ietr.preesm.codegen.xtend.task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.printer.CodegenAbstractPrinter;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class CodegenTask extends AbstractTaskImplementation {

	public static final String PARAM_PRINTER = "Printer";
	public static final String VALUE_PRINTER_IR = "IR";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.dftools.workflow.implement.AbstractTaskImplementation#execute(
	 * java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
	 * java.lang.String, net.sf.dftools.workflow.elements.Workflow)
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

		// Fill a map associating printers with printed files
		Map<IConfigurationElement, List<Block>> registeredPrintersAndBlocks 
						= new HashMap<IConfigurationElement, List<Block>>();

		// 1. Get the printers of the desired "language"
		Set<IConfigurationElement> usablePrinters = new HashSet<IConfigurationElement>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry
				.getConfigurationElementsFor("org.ietr.preesm.codegen.xtend.printers");
		for (IConfigurationElement element : elements) {
			if (element.getAttribute("language").equals(selectedPrinter)) {
				for (IConfigurationElement child : element.getChildren()) {
					if (child.getName().equals("printer")) {
						usablePrinters.add(child);
					}
				}
			}
		}

		// 2. Get a printer for each Block
		for (Block b : codeBlocks) {
			IConfigurationElement foundPrinter = null;
			if (b instanceof CoreBlock) {
				String coreType = ((CoreBlock) b).getCoreType();
				for (IConfigurationElement printer : usablePrinters) {
					IConfigurationElement[] supportedCores = printer
							.getChildren();
					for (IConfigurationElement supportedCore : supportedCores) {
						if (supportedCore.getAttribute("type").equals(coreType)) {
							foundPrinter = printer;
							break;
						}
					}
					if (foundPrinter != null) {
						break;
					}
				}
				if (foundPrinter != null) {
					List<Block> blocks = registeredPrintersAndBlocks.get(foundPrinter);
					if (blocks == null) {
						blocks = new ArrayList<Block>();
						registeredPrintersAndBlocks.put(foundPrinter, blocks);
					}
					blocks.add(b);
				} else {
					throw new WorkflowException(
							"Could not find a printer for language \""
									+ selectedPrinter + "\" and core type \""
									+ coreType + "\".");
				}
			} else {
				throw new WorkflowException(
						"Only CoreBlock CodeBlocks can be printed in the current version of Preesm.");
			}
		}

		// Pre-process the printers one by one to:
		// - Erase file with the same extension from the destination directory
		// - Do the pre-processing
		// - Save the printers in a map
		Map<IConfigurationElement, CodegenAbstractPrinter> realPrinters
					= new HashMap<IConfigurationElement,CodegenAbstractPrinter>();
		for (Entry<IConfigurationElement, List<Block>> printerAndBlocks : registeredPrintersAndBlocks
				.entrySet()) {
			String extension = printerAndBlocks.getKey().getAttribute(
					"extension");
			CodegenAbstractPrinter printer = null;
			try {
				printer = (CodegenAbstractPrinter) printerAndBlocks.getKey()
						.createExecutableExtension("class");
			} catch (CoreException e) {
				throw new WorkflowException(e.getMessage());
			}

			// Erase previous files with extension
			// Lists all files in folder
			IFolder f = workspace.getRoot().getFolder(new Path(codegenPath));
			File folder = new File(f.getRawLocation().toOSString());
			File fList[] = folder.listFiles();
			if(fList != null){
				// Searches .extension
				for (int i = 0; i < fList.length; i++) {
					String pes = fList[i].getName();
					if (pes.endsWith(extension)) {
						// and deletes
						fList[i].delete();
					}
				}
			}
			else{
				throw new WorkflowException(
						"The code generation directory was not found.");
			}

			// Do the pre-processing
			printer.preProcessing(printerAndBlocks.getValue(), codeBlocks);
			realPrinters.put(printerAndBlocks.getKey(), printer);
		}

		// Do the print for all Blocks
		for (Entry<IConfigurationElement, List<Block>> printerAndBlocks : registeredPrintersAndBlocks
				.entrySet()) {
			String extension = printerAndBlocks.getKey().getAttribute(
					"extension");
			CodegenAbstractPrinter printer = realPrinters.get(printerAndBlocks.getKey());
			

			for (Block b : printerAndBlocks.getValue()) {
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
					.createSecondaryFiles(printerAndBlocks.getValue(),
							codeBlocks).entrySet()) {
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
