package org.ietr.preesm.algorithm.importSdf3Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
		SDFGraph graph = (new Sdf3XmlParser().parse(iStream));
		Map<String, Object> outputs = null;
		if (graph != null) {
			outputs = new HashMap<String, Object>();
			outputs.put("SDF", graph);
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
