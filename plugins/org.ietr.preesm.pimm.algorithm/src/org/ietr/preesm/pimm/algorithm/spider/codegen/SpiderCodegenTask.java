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
package org.ietr.preesm.pimm.algorithm.spider.codegen;

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
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.spider.codegen.visitor.SpiderCodegen;

public class SpiderCodegenTask extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Retrieve inputs
		PreesmScenario scenario = (PreesmScenario) inputs.get(KEY_SCENARIO);
		PiGraph pg = (PiGraph) inputs.get(KEY_PI_GRAPH);

		SpiderCodegen launcher = new SpiderCodegen(scenario);

		launcher.initGenerator(pg);
		String graphCode = launcher.generateGraphCode(pg);
		String fctCode = launcher.generateFunctionCode(pg);
		String hCode = launcher.generateHeaderCode(pg);

		// Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get the project
		String projectName = workflow.getProjectName();
		IProject project = root.getProject(projectName);

		// Get the name of the folder for code generation
		String codegenFolder = scenario.getCodegenManager().getCodegenDirectory();
		if (codegenFolder == null) codegenFolder = "/Code/generated/cpp/";

		// Create the folder and its parent if necessary
		String folderPath = project.getLocation() + codegenFolder;
		File parent = new File(folderPath);
		parent.mkdirs();

		// Create the files
		String hFilePath = pg.getName() + ".h";
		File hFile = new File(parent, hFilePath);
		
		String piGraphfilePath = "pi_" + pg.getName() + ".cpp";
		File piGraphFile = new File(parent, piGraphfilePath);
		
		String piFctfilePath = "fct_" + pg.getName() + ".cpp";
		File piFctFile = new File(parent, piFctfilePath);
		
		// Write the files
		FileWriter piGraphWriter;
		FileWriter piFctWriter;
		FileWriter hWriter;
		try {
			hWriter = new FileWriter(hFile);
			hWriter.write(hCode);
			hWriter.close();
			piGraphWriter = new FileWriter(piGraphFile);
			piGraphWriter.write(graphCode);
			piGraphWriter.close();
			piFctWriter = new FileWriter(piFctFile);
			piFctWriter.write(fctCode);
			piFctWriter.close();
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
