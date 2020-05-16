package org.preesm.algorithm.clustering.partitioner;

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
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Cluster Partitioner Task
 * 
 * @author dgageot
 *
 */
@PreesmTask(id = "cluster-partitioner", name = "Cluster Partitioner",
    inputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Input PiSDF graph"),
        @Port(name = "scenario", type = Scenario.class, description = "Scenario") },
    outputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Output PiSDF graph") },
    parameters = { @Parameter(name = "Number of PEs in clusters",
        description = "The number of PEs in compute clusters. This information is used to balance actor firings"
            + " between coarse and fine-grained levels.",
        values = { @Value(name = "Fixed:=n", effect = "Where $$n\\in \\mathbb{N}^*$$.") }) })
public class ClusterPartitionerTask extends AbstractTaskImplementation {

  public static final String NB_PE         = "Number of PEs in compute clusters";
  public static final String DEFAULT_NB_PE = "1";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Task inputs
    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    Scenario scenario = (Scenario) inputs.get("scenario");

    // Parameters
    String nbPE = parameters.get(NB_PE);

    // Cluster input graph
    PiGraph outputGraph = new ClusterPartitioner(inputGraph, scenario, Integer.parseInt(nbPE)).cluster();

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
    return "Starting Execution of Cluster Partitioner Task";
  }

}
