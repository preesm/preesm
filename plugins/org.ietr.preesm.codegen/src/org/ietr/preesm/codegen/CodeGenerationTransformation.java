/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.codegen.idl.IDLPrototypeFactory;
import org.ietr.preesm.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.codegen.model.CodeGenSDFGraphFactory;
import org.ietr.preesm.codegen.model.allocators.AllocationPolicy;
import org.ietr.preesm.codegen.model.allocators.BufferAllocator;
import org.ietr.preesm.codegen.model.main.SourceFileList;
import org.ietr.preesm.codegen.model.printer.GenericPrinter;
import org.ietr.preesm.codegen.model.visitor.SystemCPrinterVisitor;
import org.ietr.preesm.core.Activator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.tools.PathTools;
import org.ietr.preesm.core.workflow.PreesmException;

/**
 * Code generation.
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class CodeGenerationTransformation extends AbstractTaskImplementation {

	/**
	 * Execution of the code generation workflow task.
	 */
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		Design architecture = (Design) inputs.get("architecture");
		DirectedAcyclicGraph algorithm = (DirectedAcyclicGraph) inputs
				.get("DAG");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		// Resets the parsed IDL prototypes
		IDLPrototypeFactory idlPrototypeFactory = new IDLPrototypeFactory();

		// Default source path is given in the workflow
		String sourcePath = PathTools.getAbsolutePath(
				parameters.get("sourcePath"), workflow.getProjectName());

		// If a source path is defined in the scenario, it overrides the one
		// from the workflow
		if (!scenario.getCodegenManager().getCodegenDirectory().isEmpty()) {
			sourcePath = scenario.getCodegenManager().getCodegenDirectory();
		}

		String xslPath = PathTools.getAbsolutePath(
				parameters.get("xslLibraryPath"), workflow.getProjectName());

		String allocPolicy = parameters.get("allocationPolicy");
		if (allocPolicy != null
				&& BufferAllocator.fromString(allocPolicy) != null) {
			AllocationPolicy.setAllocatorType(BufferAllocator
					.fromString(allocPolicy));
		}
		// Generating the code generation specific graph
		CodeGenSDFGraph codeGenSDFGraph = null;
		try {
			codeGenSDFGraph = generateCodegenGraph(algorithm, scenario, idlPrototypeFactory);
		} catch (PreesmException e) {
			throw (new WorkflowException(e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (parameters.get("printTemplate") != null) {
			String templatePath = parameters.get("printTemplate");
			SystemCPrinterVisitor printer = new SystemCPrinterVisitor(templatePath,sourcePath);
			try {
				codeGenSDFGraph.accept(printer);
			} catch (SDF4JException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Typical static code generation
		SourceFileList list = new SourceFileList();
		CodeGenerator codegen = new CodeGenerator(list);
		// Generate source files
		codegen.generateSourceFiles(codeGenSDFGraph, architecture, scenario, idlPrototypeFactory);

		// Generates the code in xml and translates it to c using XSLT
		// sheets
		GenericPrinter printerChooser = new GenericPrinter(sourcePath, xslPath);
		printerChooser.printList(list);

		outputs.put("SourceFileList", list);

		Activator.updateWorkspace();

		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("sourcePath", "");
		parameters.put("xslLibraryPath", "");
		parameters.put("allocationPolicy", "Global");
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generating Code.";
	}

	/**
	 * Generates the source files from an implementation and an architecture.
	 * The implementation is a tagged SDF graph.
	 * 
	 * @throws SDF4JException
	 * @throws InvalidExpressionException
	 * @throws PreesmException
	 */
	private CodeGenSDFGraph generateCodegenGraph(
			DirectedAcyclicGraph algorithm, PreesmScenario scenario, IDLPrototypeFactory idlPrototypeFactory)
			throws InvalidExpressionException, SDF4JException, PreesmException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iFile = workspace.getRoot().getFile(
				new Path(scenario.getAlgorithmURL()));
		CodeGenSDFGraphFactory factory = new CodeGenSDFGraphFactory(iFile);
		CodeGenSDFGraph sdfGraph = factory.create(algorithm, idlPrototypeFactory);

		return sdfGraph;
	}

}
