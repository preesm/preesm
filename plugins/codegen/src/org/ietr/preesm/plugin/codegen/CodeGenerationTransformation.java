/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.plugin.codegen;

import java.util.HashMap;
import java.util.Map;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.buffer.allocators.AllocationPolicy;
import org.ietr.preesm.core.codegen.buffer.allocators.BufferAllocator;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.task.PreesmException;
import org.ietr.preesm.plugin.codegen.jobposting.JobPostingCodeGenerator;
import org.ietr.preesm.plugin.codegen.jobposting.JobPostingPrinter;
import org.ietr.preesm.plugin.codegen.jobposting.JobPostingSource;
import org.ietr.preesm.plugin.codegen.model.CodeGenSDFGraphFactory;
import org.ietr.preesm.plugin.codegen.model.idl.IDLFunctionFactory;
import org.ietr.preesm.plugin.codegen.print.GenericPrinter;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * Code generation.
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class CodeGenerationTransformation extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		MultiCoreArchitecture architecture = (MultiCoreArchitecture) inputs
				.get("architecture");
		DirectedAcyclicGraph algorithm = (DirectedAcyclicGraph) inputs
				.get("DAG");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		// Resets the parsed IDL prototypes
		IDLFunctionFactory.getInstance().resetPrototypes();

		// Default source path is given in the workflow
		String sourcePath = parameters.get("sourcePath");

		// If a source path is defined in the scenario, it overrides the one
		// from the workflow
		if (!scenario.getCodegenManager().getCodegenDirectory().isEmpty()) {
			sourcePath = scenario.getCodegenManager().getCodegenDirectory();
		}

		String xslPath = parameters.get("xslLibraryPath");

		String allocPolicy = parameters.get("allocationPolicy");
		if (allocPolicy != null
				&& BufferAllocator.fromString(allocPolicy) != null) {
			AllocationPolicy.setAllocatorType(BufferAllocator
					.fromString(allocPolicy));
		}
		// Generating the code generation specific graph
		CodeGenSDFGraph codeGenSDFGraph = null;
		try {
			codeGenSDFGraph = generateCodegenGraph(algorithm, scenario);
		} catch (PreesmException e) {
			throw (new WorkflowException(e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!Boolean.valueOf(parameters.get("jobPosting"))) {
			// Typical static code generation
			SourceFileList list = new SourceFileList();
			CodeGenerator codegen = new CodeGenerator(list);
			// Generate source files
			codegen.generateSourceFiles(codeGenSDFGraph, architecture);

			// Generates the code in xml and translates it to c using XSLT
			// sheets
			GenericPrinter printerChooser = new GenericPrinter(sourcePath,
					xslPath);
			printerChooser.printList(list);

			outputs.put("SourceFileList", list);
		} else {
			// Job posting code generation
			boolean timedSimulation = Boolean.valueOf(parameters
					.get("timedSimulation"));
			JobPostingCodeGenerator jobGen = new JobPostingCodeGenerator(
					codeGenSDFGraph, scenario, timedSimulation);
			JobPostingSource source = jobGen.generate();
			JobPostingPrinter printer = new JobPostingPrinter();
			printer.addData(source);
			printer.writeDom(GenericPrinter.createFile("jobList.xml",
					sourcePath));
		}

		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("sourcePath", "");
		parameters.put("xslLibraryPath", "");
		parameters.put("allocationPolicy", "Global");
		parameters.put("jobPosting", "false");
		parameters.put("timedSimulation", "false");
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
			DirectedAcyclicGraph algorithm, PreesmScenario scenario)
			throws InvalidExpressionException, SDF4JException, PreesmException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iFile = workspace.getRoot().getFile(
				new Path(scenario.getAlgorithmURL()));
		CodeGenSDFGraphFactory factory = new CodeGenSDFGraphFactory(iFile);
		CodeGenSDFGraph sdfGraph = factory.create(algorithm);

		// Displays the DAG
		/*
		 * SDFAdapterDemo applet2 = new SDFAdapterDemo();
		 * applet2.init(sdfGraph);
		 */

		return sdfGraph;
	}

}
