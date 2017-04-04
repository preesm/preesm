/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/

package org.ietr.preesm.algorithm.exportDif;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.utils.paths.PathTools;

/**
 * Workflow element taking a *Single-Rate* SDF and the scenario as inputs and
 * writing the corresponding graph in the DIF format as an output.
 * 
 * @author kdesnos
 * 
 */
public class DIFExporter extends AbstractTaskImplementation {

	static final public String PARAM_PATH = "path";
	static final public String VALUE_PATH_DEFAULT = "./Code/DIF/graph.dif";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Rem: Logger is used to display messages in the console
		//Logger logger = WorkflowLogger.getLogger();

		// Retrieve the inputs
		SDFGraph sdf = (SDFGraph) inputs.get("SDF");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		// The visitor that will create the DIF File
		DIFExporterVisitor exporter = new DIFExporterVisitor(scenario);

		try {

			// Locate the output file
			String sPath = PathTools.getAbsolutePath(parameters.get("path"),
					workflow.getProjectName());
			IPath path = new Path(sPath);
			if (path.getFileExtension() == null
					|| !path.getFileExtension().equals("dif")) {
				path = path.addFileExtension("dif");
			}
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IFile iFile = workspace.getRoot().getFile(path);
			if (!iFile.exists()) {
				iFile.create(null, false, new NullProgressMonitor());
			}
			File file = new File(iFile.getRawLocation().toOSString());

			// Apply the visitor to the graph
			sdf.accept(exporter);

			// Write the result into the text file
			exporter.write(file);

		} catch (SDF4JException | CoreException e) {
			e.printStackTrace();
		}

		// Output output
		Map<String, Object> output = new HashMap<String, Object>();
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_PATH, VALUE_PATH_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Exporting DIF File";
	}
}
