/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.codegen.SourceFileList;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.ICodeGeneration;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.codegen.model.CodeGenSDFGraphFactory;
import org.ietr.preesm.plugin.codegen.print.GenericPrinter;
import org.sdf4j.model.dag.DirectedAcyclicGraph;

/**
 * Code generation.
 * 
 * @author Matthieu Wipliez
 */
public class CodeGeneration implements ICodeGeneration {

	/**
	 * Main for test
	 */
	public static void main(String[] args) {
		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		logger.log(Level.FINER, "Testing code generation with an example");

		// Creating generator
		@SuppressWarnings("unused")
		CodeGeneration gen = new CodeGeneration();

		// Input archi & algo
		// TODO: parse algorithm
		@SuppressWarnings("unused")
		MultiCoreArchitecture architecture = Examples.get2C64Archi();
		@SuppressWarnings("unused")
		DirectedAcyclicGraph algorithm = null;

		// Input file list
		Map<String, String> map = new HashMap<String, String>();
		map.put("sourcePath", "d:/Test");
		@SuppressWarnings("unused")
		TextParameters params = new TextParameters(map);

		//gen.transform(algorithm, architecture, params);

		logger.log(Level.FINER, "Code generated");
	}

	/**
	 * Generates the source files from an implementation and an architecture.
	 * The implementation is a tagged SDF graph.
	 */
	private void generateSourceFiles(DirectedAcyclicGraph algorithm,
			MultiCoreArchitecture architecture, IScenario scenario, SourceFileList list) {
		CodeGenerator codegen = new CodeGenerator(list);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iFile = workspace.getRoot().getFile(new Path(scenario.getAlgorithmURL()));
		CodeGenSDFGraphFactory factory = new CodeGenSDFGraphFactory(iFile);
		CodeGenSDFGraph sdfGraph = factory.create(algorithm);
		codegen.generateSourceFiles(sdfGraph, architecture);
	}

	/**
	 * Transformation method called by the workflow
	 */
	@Override
	public TaskResult transform(DirectedAcyclicGraph algorithm,
			MultiCoreArchitecture architecture, IScenario scenario, TextParameters parameters) {
		String sourcePath = parameters.getVariable("sourcePath");
		String xslPath = parameters.getVariable("xslLibraryPath");
		TaskResult result = new TaskResult();
		SourceFileList list = new SourceFileList();
		
		try{
		// Generate source file class
		generateSourceFiles(algorithm, architecture, scenario, list);
		}catch(Exception e){
			e.printStackTrace();
		}
		// Generates the code
		GenericPrinter printerChooser = new GenericPrinter(sourcePath,xslPath);
		printerChooser.printList(list);

		result.setSourcefilelist(list);

		return result;
	}

}
