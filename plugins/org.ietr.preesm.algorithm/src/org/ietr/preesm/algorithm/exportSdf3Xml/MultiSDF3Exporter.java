package org.ietr.preesm.algorithm.exportSdf3Xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.tools.PathTools;

public class MultiSDF3Exporter extends AbstractTaskImplementation {

	static final public String PARAM_PATH = "path";
	static final public String VALUE_PATH_DEFAULT = "./Code/SDF3/graph.xml";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Retrieve the inputs
		@SuppressWarnings("unchecked")
		Set<SDFGraph> sdfs = (Set<SDFGraph>) inputs.get(KEY_SDF_GRAPHS_SET);
		PreesmScenario scenario = (PreesmScenario) inputs.get(KEY_SCENARIO);
		Design archi = (Design) inputs.get(KEY_ARCHITECTURE);
		// Locate the output file
		String sPath = PathTools.getAbsolutePath(parameters.get("path"),
				workflow.getProjectName());
		IPath path = new Path(sPath);

		for (SDFGraph sdf : sdfs) {
			SDF3ExporterEngine engine = new SDF3ExporterEngine();
			engine.printSDFGraphToSDF3File(sdf, scenario, archi, path);
		}

		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_PATH, VALUE_PATH_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Exporting SDF3 File";
	}
}
