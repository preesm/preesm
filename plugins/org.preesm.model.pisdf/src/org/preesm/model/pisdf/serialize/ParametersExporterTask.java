package org.preesm.model.pisdf.serialize;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author ahonorat
 *
 */
@PreesmTask(id = "pisdf-export.parameters", name = "Parameters exporter", category = "Graph Exporters",
    description = "Export parameters of the graph as C header with define. "
        + "Exports only static parameters. Name of file is: <graphName>_preesm_params.h",
    inputs = { @Port(name = "PiMM", type = PiGraph.class) },
    parameters = { @Parameter(name = ParametersExporterTask.PARAM_PATH, values = {
        @Value(name = ParametersExporterTask.DEFAULT_PATH, effect = "default path, relative to the project") }) })
public class ParametersExporterTask extends AbstractTaskImplementation {

  public static final String DEFAULT_PATH = "/Code";

  public static final String PARAM_PATH = "path";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    String folderPath = parameters.get(PARAM_PATH);

    // Get the root of the workspace
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    // Get the project
    final String projectName = workflow.getProjectName();
    final IProject project = root.getProject(projectName);

    // Get a complete valid path with all folders existing
    folderPath = project.getLocation() + folderPath;
    final File parent = new File(folderPath);
    parent.mkdirs();

    final String filePath = graph.getName() + "_preesm_params.h";
    final File file = new File(parent, filePath);

    // print parameters
    String params = getParamsHeader(graph);

    try {
      FileUtils.writeStringToFile(file, params, (String) null);
    } catch (IOException e) {
      throw new PreesmRuntimeException("Unable to write graph parameters on file " + filePath, e);
    }

    return new LinkedHashMap<>();
  }

  /**
   * Format parameters of a graph in a C header.
   * 
   * @param graph
   *          Graph to get parameters from.
   * @return String being the header.
   */
  public static String getParamsHeader(PiGraph graph) {
    StringBuilder sb = new StringBuilder();

    String fileUnit = graph.getName().toUpperCase() + "_PREESM_PARAMS_H";
    sb.append("#ifndef " + fileUnit + "\n");
    sb.append("#define " + fileUnit + "\n\n");

    for (org.preesm.model.pisdf.Parameter p : graph.getAllParameters()) {
      if (p.isLocallyStatic()) {
        long value = p.getValueExpression().evaluate();
        sb.append("#define " + p.getName().toUpperCase() + " " + value + "\n");
      }
    }

    sb.append("\n#endif\n");

    return sb.toString();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PARAM_PATH, DEFAULT_PATH);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Exporting graph parameters";
  }

}
