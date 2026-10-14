package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiSDFExporterTask;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "passive.actor.exposer", name = "Passive Actor Exposer", category = "Memory Optimization",

    inputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class,
          description = "Input PiGraph algorithm"),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class,
          description = "Input scenario") },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class, description = """
        Output PiGraph algorithm, with passive actors instead of special actors and classic actors
        with passive scripts paths""") },

    parameters = { @Parameter(name = PassiveActorExposerTask.PARAM_ALIGNMENT, values = {}),
      @Parameter(name = PassiveActorExposerTask.PARAM_CLUSTERS_ONLY, values = {}),
      @Parameter(name = PassiveActorExposerTask.PARAM_DEBUG, values = {}),
      @Parameter(name = PassiveActorExposerTask.PARAM_VERBOSE, values = {}) },

    description = "",

    shortDescription = "",

    seeAlso = ""

)
public class PassiveActorExposerTask extends AbstractTaskImplementation {

  public static final String PARAM_ALIGNMENT     = "Alignment";
  public static final String PARAM_CLUSTERS_ONLY = "Clusters only";
  public static final String PARAM_DEBUG         = "Debug";
  public static final String PARAM_VERBOSE       = "Verbose";

  Scenario scenario  = null;
  Long     alignment = null;
  Boolean  debug     = null;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    alignment = Long.parseLong(parameters.get(PARAM_ALIGNMENT));
    debug = Boolean.parseBoolean(parameters.get(PARAM_DEBUG));
    final boolean clustersOnly = Boolean.parseBoolean(parameters.get(PARAM_CLUSTERS_ONLY));

    if (clustersOnly) {
      for (final PiGraph cluster : graph.getAllChildrenGraphs().stream().filter(c -> c.isCluster()).toList()) {
        processGraph(cluster, workflow);
      }
    } else {
      processGraph(graph, workflow);
    }

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
    return outputs;
  }

  private void processGraph(PiGraph graph, Workflow workflow) {
    final PassiveActorEngine engine = new PassiveActorEngine(graph, scenario, alignment);

    /** Step 1: Transforms special actors into passive ones */
    engine.processPassiveActors();
    if (Boolean.TRUE.equals(debug)) {
      exportGraph(engine.getPassiveIR(), "/Algo/generated/alloc/1.passive/", workflow);
    }

    /** Step 2: Fuses adjacent passive actors to gain more reduction */
    engine.composePassiveActors();
    if (Boolean.TRUE.equals(debug)) {
      exportGraph(engine.getPassiveIR(), "/Algo/generated/alloc/2.passive-composed/", workflow);
    }

    /** Step 3: Transforms FIFOs into (simple) passive actors */
    engine.transformFifosIntoPassiveActors();
    if (Boolean.TRUE.equals(debug)) {
      exportGraph(engine.getPassiveIR(), "/Algo/generated/alloc/3.passive-fifos/", workflow);
    }

    /** Step 4: Removes passive FIFOs actors adjacent to other passive actors, if it is possible */
    engine.prunePassiveFifos();
    if (Boolean.TRUE.equals(debug)) {
      exportGraph(engine.getPassiveIR(), "/Algo/generated/alloc/4.passive-fifos-pruned/", workflow);
    }

    /** Step 5: Creates a BroadcastActor between two adjacent passive actors */
    engine.generateBroadcasts();
    if (Boolean.TRUE.equals(debug)) {
      exportGraph(engine.getPassiveIR(), "/Algo/generated/alloc/5.passive-broadcasts/", workflow);
    }
  }

  private static void exportGraph(PiGraph graph, String path, Workflow workflow) {
    final PiSDFExporterTask exporter = new PiSDFExporterTask();
    final Map<String, Object> inputs = new HashMap<>();
    inputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
    final Map<String, String> params = new HashMap<>();
    params.put(PiSDFExporterTask.PARAM_PATH, path);
    exporter.execute(inputs, params, null, null, workflow);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> params = new HashMap<>();
    params.put(PARAM_ALIGNMENT, "8L");
    params.put(PARAM_CLUSTERS_ONLY, "True");
    params.put(PARAM_DEBUG, "False");
    params.put(PARAM_VERBOSE, "False");

    return null;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

}
