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
import org.ietr.preesm.core.tools.PathTools;

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
		
		SDFGraph graph = engine.importFrom(path, scenario, architecture, logger);
		
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
