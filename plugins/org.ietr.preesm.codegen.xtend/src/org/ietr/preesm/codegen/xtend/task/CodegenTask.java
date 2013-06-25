package org.ietr.preesm.codegen.xtend.task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

import org.eclipse.core.resources.IFile;
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
import org.ietr.preesm.codegen.xtend.printer.XMLPrinter;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class CodegenTask extends AbstractTaskImplementation {

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

		Set<Block> codeBlock = generator.generate();

		// Save the intermediate model

		// Register the XMI resource factory for the .website extension
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("codegen", new XMIResourceFactoryImpl());

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.getRoot().getLocation();
		String codegenPath = scenario.getCodegenManager().getCodegenDirectory()
				+ "/codegen/";

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		for (Block b : codeBlock) {
			// Create a resource
			scenario.getCodegenManager().getCodegenDirectory();
			Resource resource = resSet.createResource(URI.createURI(codegenPath
					+ b.getName() + ".codegen"));
			// Get the first model element and cast it to the right type, in my
			// example everything is hierarchical included in this first node
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

		// Test XMLPrinter
		XMLPrinter printer = new XMLPrinter();
		for (Block b : codeBlock) {
			IFile iFile = workspace.getRoot().getFile(
					new Path(codegenPath + b.getName() + ".xml"));
			try {
				if (!iFile.exists()) {
					iFile.create(null, false, new NullProgressMonitor());
				}
				iFile.setContents(new ByteArrayInputStream(printer.doSwitch(b)
						.toString().getBytes()), true, false,
						new NullProgressMonitor());
			} catch (CoreException e1) {
				e1.printStackTrace();
			}

		}

		// Create empty output map
		Map<String, Object> output = new HashMap<String, Object>();
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generate xtend code";
	}

}
