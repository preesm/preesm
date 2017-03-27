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
package org.ietr.preesm.algorithm.importSdf3Xml;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.utils.paths.PathTools;

/**
 * This class is a {@link Workflow} task that parse a SDF in the SDF3 XML format
 * and output its corresponding {@link SDFGraph}.
 * 
 * @author kdesnos
 * 
 */
public class Sdf3Importer extends AbstractTaskImplementation {

	static final public String PARAM_PATH = "path";
	static final public String VALUE_PATH_DEFAULT = "./Code/SDF3/graph.xml";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		// Retrieve the inputs
		Design architecture = (Design) inputs.get(KEY_ARCHITECTURE);
		PreesmScenario scenario = (PreesmScenario) inputs.get(KEY_SCENARIO);

		// Locate the intput file
		String sPath = PathTools.getAbsolutePath(parameters.get("path"),
				workflow.getProjectName());
		IPath path = new Path(sPath);

		SDF3ImporterEngine engine = new SDF3ImporterEngine();

		SDFGraph graph = engine
				.importFrom(path, scenario, architecture, logger);

		Map<String, Object> outputs = null;

		// If there was no problem while parsing the graph
		if (graph != null) {
			// put it in the outputs
			outputs = new HashMap<String, Object>();
			outputs.put(KEY_SDF_GRAPH, graph);
			outputs.put(KEY_SCENARIO, scenario);
		}

		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_PATH, VALUE_PATH_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Importing SDF3 Xml File";
	}

}
