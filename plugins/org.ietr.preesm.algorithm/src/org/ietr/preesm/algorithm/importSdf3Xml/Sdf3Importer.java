package org.ietr.preesm.algorithm.importSdf3Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.ConstraintGroupManager;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.tools.PathTools;
import org.ietr.preesm.core.types.DataType;

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
		Design architecture = (Design) inputs.get("architecture");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		// Locate the intput file
		String sPath = PathTools.getAbsolutePath(parameters.get("path"),
				workflow.getProjectName());
		IPath path = new Path(sPath);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iFile = workspace.getRoot().getFile(path);

		if (!iFile.exists()) {
			String message = "The parsed xml file does not exists: " + sPath;
			logger.log(Level.SEVERE, message);
			throw new WorkflowException(message);
		}

		File file = new File(iFile.getRawLocation().toOSString());
		InputStream iStream = null;
		try {
			iStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Parse the input SDF3 graph
		Sdf3XmlParser sdf3Parser = new Sdf3XmlParser();
		SDFGraph graph = null;
		try{
		graph = (sdf3Parser.parse(iStream));
		} catch (RuntimeException e){
		 	logger.log(Level.SEVERE, "SDF3 Parser Error: "+e.getMessage());
		 	return null;
		}
		Map<String, Object> outputs = null;

		// If there was no problem while parsing the graph
		if (graph != null) {
			// put it in the outputs
			outputs = new HashMap<String, Object>();
			outputs.put("SDF", graph);

			// Update the input scenario so that all task can be scheduled
			// on all operators, and all have the same runtime.
			ConstraintGroupManager constraint = scenario
					.getConstraintGroupManager();
			// For each operator of the architecture
			for (ComponentInstance component : architecture
					.getComponentInstances()) {
				// for each actor of the graph
				for (Entry<SDFAbstractVertex, Integer> entry : sdf3Parser
						.getActorExecTimes().entrySet()) {
					// Add the operator to the available operator for the
					// current actor
					entry.getKey().setInfo(entry.getKey().getName());
					constraint.addConstraint(component.getInstanceName(),
							entry.getKey());
					// Set the timing of the actor
					Timing t = scenario.getTimingManager().addTiming(
							entry.getKey().getName(),
							component.getComponent().getVlnv().getName());
					t.setTime(entry.getValue());
				}
			}
			
			// Add the data types of the SDF3 graph to the scenario
			for (Entry<String, Integer> entry : sdf3Parser.getDataTypes()
					.entrySet()) {
				DataType type = new DataType(entry.getKey(), entry.getValue());
				scenario.getSimulationManager().putDataType(type);
			}
			outputs.put("scenario", scenario);
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
