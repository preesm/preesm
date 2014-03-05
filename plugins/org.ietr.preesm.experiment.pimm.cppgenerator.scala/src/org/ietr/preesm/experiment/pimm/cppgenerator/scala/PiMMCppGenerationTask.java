/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
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
package org.ietr.preesm.experiment.pimm.cppgenerator.scala;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.pimm.cppgenerator.scala.visitor.CPPCodeGenerationLauncher;

public class PiMMCppGenerationTask extends AbstractTaskImplementation {

	private static String graph_key = "PiMM";
	private static String scenario_key = "scenario";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Retrieve inputs
		// TODO: Correct when PiScenario are added as automatic inputs
		// PreesmScenario scenario = (PreesmScenario) inputs.get(scenario_key);
		PiGraph pg = (PiGraph) inputs.get(graph_key);

		CPPCodeGenerationLauncher launcher = new CPPCodeGenerationLauncher();

		String finalResult = launcher.generateCPPCode(pg);

		// Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get the project
		String projectName = workflow.getProjectName();
		IProject project = root.getProject(projectName);

		// Get the name of the folder for code generation
		// TODO: Correct when a codegen part is added to PiScenarios
		String codegenFolder = "/Code/generated/cpp/";// scenario.getCodegenManager().getCodegenDirectory();

		// Create the folder and its parent if necessary
		String folderPath = project.getLocation() + codegenFolder;
		File parent = new File(folderPath);
		parent.mkdirs();

		// Create the file
		String filePath = pg.getName() + ".cpp";
		File file = new File(parent, filePath);

		// Write the file
		FileWriter out;
		try {
			out = new FileWriter(file);
			out.write(finalResult);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return an empty output map
		return Collections.emptyMap();
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		// Create an empty parameters map
		Map<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generating C++ code.";
	}

}
