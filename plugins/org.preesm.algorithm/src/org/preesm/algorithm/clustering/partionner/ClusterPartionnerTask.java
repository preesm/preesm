package org.preesm.algorithm.clustering.partionner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Cluster Partionner Task
 * 
 * @author dgageot
 *
 */
@PreesmTask(id = "cluster-partionner", name = "Cluster Partionner",
    inputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Input PiSDF graph"),
        @Port(name = "scenario", type = Scenario.class, description = "Scenario"),
        @Port(name = "architecture", type = Design.class, description = "Architecture") },
    outputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Output PiSDF graph") },
    parameters = { @Parameter(name = "Number of PEs in clusters",
        description = "The number of PEs in clusters which is used to balance firings upon coarse-grained "
            + "and fine-grained levels.",
        values = { @Value(name = "Fixed:=n", effect = "Where $$n\\in \\mathbb{N}^*$$.") }) })
public class ClusterPartionnerTask extends AbstractTaskImplementation {

  public static final String NB_PE         = "Number of PEs in clusters";
  public static final String DEFAULT_NB_PE = "1";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Task inputs
    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    Scenario scenario = (Scenario) inputs.get("scenario");
    Design architecture = (Design) inputs.get("architecture");

    // Parameters
    String nbPE = parameters.get(NB_PE);

    PiGraph outputGraph = new ClusterPartionner(inputGraph, scenario, architecture, Integer.parseInt(nbPE)).cluster();

    // Build output map
    Map<String, Object> output = new HashMap<>();
    output.put("PiMM", outputGraph);

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> defaultParams = new LinkedHashMap<>();
    defaultParams.put(NB_PE, DEFAULT_NB_PE);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Cluster Partionner Task";
  }

}
