package org.ietr.preesm.mapper.stats.exporter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.mapper.abc.IAbc;

public class MultiStatsExporterTask extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		@SuppressWarnings("unchecked")
		Set<IAbc> simulators = (Set<IAbc>) inputs.get(KEY_SDF_ABC_SET);
		String folderPath = parameters.get("path");

		// Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get the project
		String projectName = workflow.getProjectName();
		IProject project = root.getProject(projectName);

		// Get a complete valid path with all folders existing
		folderPath = project.getLocation() + folderPath;
		File parent = new File(folderPath);
		parent.mkdirs();

		int i = 0;
		for (IAbc abc : simulators) {
			// Create a file from filePath
			String filePath = "stats_" + i + ".pgantt";
			File file = new File(parent, filePath);
			i++;
			// Generate the stats from the abc and write them in file
			XMLStatsExporter exporter = new XMLStatsExporter();
			exporter.exportXMLStats(abc, file);
		}

		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("path", "/stats/xml/");
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generate the stats";
	}
}
