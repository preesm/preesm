package org.ietr.preesm.latency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;

/**
 * @author hderoui
 *
 */
public class LatencyExplorationTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow)
      throws WorkflowException {

    // get the input graph, the scenario for actors duration, and the total number of cores
    SDFGraph inputGraph = GraphStructureHelper.cloneIBSDF((SDFGraph) inputs.get("SDF"));
    PreesmScenario inputScenario = (PreesmScenario) inputs.get("scenario");
    Integer nbCores = Integer.parseInt(parameters.get("nbCores"));

    // list of latency in function of cores number
    ArrayList<Double> latencyList = new ArrayList<Double>(nbCores);

    // explore the latency
    // no available cores => latency = 0
    latencyList.add(0, 0.);

    // latency of a single core execution
    LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
    double maxLatency = evaluator.getMinLatencySingleCore(inputGraph, inputScenario);
    latencyList.add(1, maxLatency);

    // latency of a multicore execution
    for (int n = 2; n <= nbCores; n++) {
      double l = 1;
      // compute the latency of the graph using n cores
      // call the class for scheduling the graph using n cores
      // parameters(n, null) if null do not construct/return the gantt chart
      latencyList.add(n, l);
    }

    // WorkflowLogger.getLogger().log(Level.INFO, "Throughput value");
    // WorkflowLogger.getLogger().log(Level.WARNING, "ERROR : The graph is deadlock !!");

    // set the outputs
    Map<String, Object> outputs = new HashMap<String, Object>();
    // outputs.put("SDF", inputGraph);
    // outputs.put("scenario", inputScenario);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> parameters = new HashMap<String, String>();
    // parameters.put(,);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Exploring graph latency ...";
  }

}
