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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.buffer.allocators.AllocationPolicy;
import org.ietr.preesm.core.codegen.buffer.allocators.BufferAllocator;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.ICodeGeneration;
import org.ietr.preesm.core.task.PreesmException;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.codegen.jobposting.JobPostingCodeGenerator;
import org.ietr.preesm.plugin.codegen.jobposting.JobPostingPrinter;
import org.ietr.preesm.plugin.codegen.jobposting.JobPostingSource;
import org.ietr.preesm.plugin.codegen.model.CodeGenSDFGraphFactory;
import org.ietr.preesm.plugin.codegen.model.idl.IDLFunctionFactory;
import org.ietr.preesm.plugin.codegen.print.GenericPrinter;
import org.sdf4j.demo.SDFAdapterDemo;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * Code generation.
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class CodeGenerationTransformation implements ICodeGeneration {


	/**
	 * Generates the source files from an implementation and an architecture.
	 * The implementation is a tagged SDF graph.
	 * 
	 * @throws SDF4JException
	 * @throws InvalidExpressionException
	 * @throws PreesmException
	 */
	private CodeGenSDFGraph generateCodegenGraph(DirectedAcyclicGraph algorithm, IScenario scenario) throws InvalidExpressionException,
			SDF4JException, PreesmException {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iFile = workspace.getRoot().getFile(
				new Path(scenario.getAlgorithmURL()));
		CodeGenSDFGraphFactory factory = new CodeGenSDFGraphFactory(iFile);
		CodeGenSDFGraph sdfGraph = factory.create(algorithm);
		
		// Displays the DAG
		if(false) {
			SDFAdapterDemo applet2 = new SDFAdapterDemo();
			applet2.init(sdfGraph);
		}
		
		return sdfGraph;
	}

	/**
	 * Transformation method called by the workflow
	 */
	@Override
	public TaskResult transform(DirectedAcyclicGraph algorithm,
			MultiCoreArchitecture architecture, IScenario scenario,
			TextParameters parameters) throws PreesmException {

		// Resets the parsed IDL prototypes
		IDLFunctionFactory.getInstance().resetPrototypes();
		
		// Default source path is given in the workflow
		String sourcePath = parameters.getVariable("sourcePath");

		// If a source path is defined in the scenario, it overrides the one
		// from the workflow
		if (!scenario.getCodegenManager().getCodegenDirectory().isEmpty()) {
			sourcePath = scenario.getCodegenManager().getCodegenDirectory();
		}

		String xslPath = parameters.getVariable("xslLibraryPath");
		TaskResult result = new TaskResult();
		
		String allocPolicy = parameters.getVariable("allocationPolicy");
		if(allocPolicy != null && BufferAllocator.fromString(allocPolicy) != null){
			AllocationPolicy.setAllocatorType(BufferAllocator.fromString(allocPolicy));
		}
		// Generating the code generation specific graph
		CodeGenSDFGraph codeGenSDFGraph = null;
		try {
			codeGenSDFGraph = generateCodegenGraph(algorithm, scenario);
		} catch (PreesmException e) {
			throw (e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!parameters.getBooleanVariable("jobPosting")) {
			// Typical static code generation
			SourceFileList list = new SourceFileList();
			CodeGenerator codegen = new CodeGenerator(list);
			// Generate source files
			codegen.generateSourceFiles(codeGenSDFGraph, architecture);
			
			// Generates the code in xml and translates it to c using XSLT sheets
			GenericPrinter printerChooser = new GenericPrinter(sourcePath,
					xslPath);
			printerChooser.printList(list);

			result.setSourcefilelist(list);
		} else{
			// Job posting code generation
			boolean timedSimulation = parameters.getBooleanVariable("timedSimulation");
			JobPostingCodeGenerator jobGen = new JobPostingCodeGenerator(codeGenSDFGraph, scenario, timedSimulation);
			JobPostingSource source = jobGen.generate();
			JobPostingPrinter printer = new JobPostingPrinter();
			printer.addData(source);
			printer.writeDom(GenericPrinter.createFile("jobList.xml", sourcePath));
		}

		return result;
	}

}
