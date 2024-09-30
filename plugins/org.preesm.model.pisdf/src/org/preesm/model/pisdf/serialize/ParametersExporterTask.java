/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiMMHelper;
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
    // 0. we copy the graph since the parameter resolution has side effects (replace symbolic expression by its
    // valuation)
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    // 1. we resolve all parameters since subgraph parameters cannot be evaluated properly otherwise
    PiMMHelper.resolveAllParameters(graphCopy);
    // 2. we export the resolved parameters
    final String params = getParamsHeader(graphCopy);

    try {
      FileUtils.writeStringToFile(file, params, (String) null);
    } catch (final IOException e) {
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
    final StringBuilder sb = new StringBuilder();

    final String fileUnit = graph.getName().toUpperCase() + "_PREESM_PARAMS_H";
    sb.append("#ifndef " + fileUnit + "\n");
    sb.append("#define " + fileUnit + "\n\n");

    for (final org.preesm.model.pisdf.Parameter p : graph.getAllParameters()) {
      if (p.isLocallyStatic()) {
        final long value = p.getValueExpression().evaluateAsLong();
        sb.append("#define " + p.getVertexPath().toUpperCase().replace('/', '_') + " " + value + "\n");
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
