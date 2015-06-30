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
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.printer.CodegenAbstractPrinter;
import org.ietr.preesm.core.scenario.PreesmScenario;

public class CodegenEngine {

	private PreesmScenario scenario;
	private IWorkspace workspace;
	private String codegenPath;
	private List<Block> codeBlocks;
	private Map<IConfigurationElement, List<Block>> registeredPrintersAndBlocks;
	private Map<IConfigurationElement, CodegenAbstractPrinter> realPrinters;

	public CodegenEngine(PreesmScenario scenario, IWorkspace workspace,
			String codegenPath, List<Block> codeBlocks) {
		this.scenario = scenario;
		this.workspace = workspace;
		this.codegenPath = codegenPath;
		this.codeBlocks = codeBlocks;
	}

	public void initializePrinterIR(String codegenPath) {

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
			Resource resource = resSet.createResource(URI.createURI(codegenPath
					+ b.getName() + ".codegen"));
			// Get the first model element and cast it to the right type, in
			// my example everything is hierarchical included in this first
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

	public void registerPrintersAndBlocks(String selectedPrinter)
			throws WorkflowException {
		registeredPrintersAndBlocks = new HashMap<IConfigurationElement, List<Block>>();

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
					List<Block> blocks = registeredPrintersAndBlocks
							.get(foundPrinter);
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
	}

	public void preprocessPrinters() throws WorkflowException {
		// Pre-process the printers one by one to:
		// - Erase file with the same extension from the destination directory
		// - Do the pre-processing
		// - Save the printers in a map
		realPrinters = new HashMap<IConfigurationElement, CodegenAbstractPrinter>();
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
			if (fList != null) {
				// Searches .extension
				for (int i = 0; i < fList.length; i++) {
					String pes = fList[i].getName();
					if (pes.endsWith(extension)) {
						// and deletes
						fList[i].delete();
					}
				}
			} else {
				throw new WorkflowException(
						"The code generation directory was not found.");
			}

			// Do the pre-processing
			printer.preProcessing(printerAndBlocks.getValue(), codeBlocks);
			realPrinters.put(printerAndBlocks.getKey(), printer);
		}
	}

	public void print() {
		for (Entry<IConfigurationElement, List<Block>> printerAndBlocks : registeredPrintersAndBlocks
				.entrySet()) {
			String extension = printerAndBlocks.getKey().getAttribute(
					"extension");
			CodegenAbstractPrinter printer = realPrinters.get(printerAndBlocks
					.getKey());

			for (Block b : printerAndBlocks.getValue()) {
				IFile iFile = workspace.getRoot().getFile(
						new Path(codegenPath + b.getName() + extension));
				try {
					IFolder iFolder = workspace.getRoot().getFolder(new Path(codegenPath));
					if(!iFolder.exists()){
						iFolder.create(false, true, new NullProgressMonitor());
					}
					if (!iFile.exists()) {
						iFile.create(new ByteArrayInputStream("".getBytes()), false, new NullProgressMonitor());
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
					IFolder iFolder = workspace.getRoot().getFolder(new Path(codegenPath));
					if(!iFolder.exists()){
						iFolder.create(false, true, new NullProgressMonitor());
					}
					if (!iFile.exists()) {
						iFile.create(new ByteArrayInputStream("".getBytes()), false, new NullProgressMonitor());
					}
					iFile.setContents(new ByteArrayInputStream(entry.getValue()
							.toString().getBytes()), true, false,
							new NullProgressMonitor());
				} catch (CoreException e1) {
					e1.printStackTrace();
				}

			}

		}
	}
}
