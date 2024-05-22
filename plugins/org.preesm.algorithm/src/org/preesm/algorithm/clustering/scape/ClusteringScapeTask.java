package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.partitioner.ScapeMode;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class cluster actors in order to match parallelism the target architecture.
 *
 * @see conference paper: "SCAPE: HW-Aware Clustering of Dataflow Actors for Tunable Scheduling
 *      Complexity", published at DASIP 2023, "Automated Clustering and Pipelining of Dataflow Actors for Controlled
 *      Scheduling Complexity" published at EUSIPCO 2023, and,
 *      "Automated Level-Based Clustering of Dataflow Actors for Controlled Scheduling Complexity", published at JSA
 *      2023
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "scape.task.identifier", name = "Clustering Task",
    inputs = { @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "cMem", type = Map.class) },

    parameters = {

        @Parameter(name = ClusteringScapeTask.STACK_PARAM, description = "stack size (in Byte)",
            values = { @Value(name = "Fixed:=n",
                effect = "the size of the stack allows to quantify the number of allocable buffer "
                    + "in the stack the rest in the heap") }),

        @Parameter(name = ClusteringScapeTask.LEVEL_PARAM, description = "number of level to cluster",
            values = { @Value(name = "Fixed:=n",
                effect = "the number of level to cluster in order to reach flattener performance "
                    + "and compromising analysis time") }),

        @Parameter(name = ClusteringScapeTask.CLUSTERING_PARAM,
            description = "choose the clustering mode : 1 = set of clustering config + only fit data parallelism,"
                + " 2 = set of clustering config + fit data & pip parallelism, 3 = best clustering config ",
            values = { @Value(name = "Fixed:=n", effect = "switch of clustering algorithm") }),

    })

public class ClusteringScapeTask extends AbstractTaskImplementation {

  public static final String STACK_PARAM        = "Stack size";
  public static final String STACK_SIZE_DEFAULT = "1048576";   // 1MB

  public static final String CLUSTERING_PARAM        = "SCAPE mode";
  public static final String CLUSTERING_MODE_DEFAULT = "0";         // SCAPE1

  public static final String LEVEL_PARAM          = "Level number";
  public static final String LEVEL_NUMBER_DEFAULT = "1";           // 1

  public static final String PARAM_PRINTER    = "Printer";
  public static final String VALUE_PRINTER_IR = "IR";

  protected long      stack;
  protected long      core;
  protected int       cluster;
  protected ScapeMode scapeMode;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // retrieve input parameter stack size
    final String stackStr = parameters.get(STACK_PARAM);
    this.stack = Integer.decode(stackStr);

    // retrieve input parameter
    final String clusterStr = parameters.get(LEVEL_PARAM);
    this.cluster = Integer.decode(clusterStr);
    // retrieve input parameter
    final String modeStr = parameters.get(CLUSTERING_PARAM);

    this.scapeMode = switch (modeStr) {
      case "0" -> ScapeMode.DATA;
      case "1" -> ScapeMode.DATA_PIPELINE;
      case "2" -> ScapeMode.DATA_PIPELINE_HIERARCHY;
      default -> throw new PreesmRuntimeException("Unrecognized Scape mode.");
    };

    // Task inputs
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Long stackSize = this.stack;
    final int clusterNumber = this.cluster;

    final ClusteringScape clusteringScape = new ClusteringScape(scenario, stackSize, scapeMode, clusterNumber);

    final Scenario outputScenario = clusteringScape.execute();
    final Map<Actor, Long> clusterMemory = clusteringScape.getClusterMemory();

    final Map<String, Object> output = new HashMap<>();
    // return topGraph
    output.put("PiMM", outputScenario.getAlgorithm());
    // update scenario, clear the intra cluster actors
    // for (final Entry<ComponentInstance, EList<AbstractActor>> gp : scenario.getConstraints().getGroupConstraints()) {
    // gp.getValue().clear();
    // }
    // // add all actor on all resources if full CPU
    // // Smarter alloc : Ewen (smart)
    //
    // final Timings time = scenario.getTimings();
    //
    // final EMap<AbstractActor, EMap<Component, EMap<TimingType, String>>> map = time.getActorTimings();
    //
    // int timingOnCPU = 0;
    // int timingOnGPU = 0;
    //
    // for (final Entry<AbstractActor, EMap<Component, EMap<TimingType, String>>> actorEntry : map.entrySet()) {
    // final AbstractActor actor = actorEntry.getKey();
    // final EMap<Component, EMap<TimingType, String>> componentMap = actorEntry.getValue();
    //
    // for (final Entry<Component, EMap<TimingType, String>> componentEntry : componentMap.entrySet()) {
    // final Component component = componentEntry.getKey();
    // final EMap<TimingType, String> timingMap = componentEntry.getValue();
    //
    // for (final Entry<TimingType, String> timingEntry : timingMap.entrySet()) {
    // final TimingType timingType = timingEntry.getKey();
    // final String timingValue = timingEntry.getValue();
    //
    // if (component.toString().contains("CPU")) {
    // if (actor.getName().contains("srv") || actor.getName().contains("urc")) {
    // timingOnCPU += Integer.parseInt(timingValue);
    // }
    // } else if (component.toString().contains("GPU")) {
    // if (actor.getName().contains("srv") || actor.getName().contains("urc")) {
    // timingOnGPU += Integer.parseInt(timingValue);
    // }
    // }
    // }
    // }
    // }
    //
    // // End of smarter alloc
    //
    // if (SlamDesignPEtypeChecker.isOnlyCPU(scenario.getDesign())
    // || (SlamDesignPEtypeChecker.isDualCPUGPU(scenario.getDesign()) && timingOnGPU > timingOnCPU)) {
    // scenario.getConstraints().getGroupConstraints()
    // .forEach(groupConstraint -> outputScenario.getAllActors().forEach(actor ->
    // groupConstraint.getValue().add(actor)));
    // // temporary force srv/urc cluster to be map on GPU other on CPU if dual design
    // // TODO: implementing smarter allocation
    //
    // } else if (SlamDesignPEtypeChecker.isDualCPUGPU(scenario.getDesign()) && timingOnGPU < timingOnCPU) {
    // scenario.getConstraints().getGroupConstraints()
    // .forEach(gp -> outputScenario.getAllActors().stream()
    // .filter(actor -> (gp.getKey().getComponent() instanceof GPU
    // && (actor.getName().contains("srv") || actor.getName().contains("urc")))
    // || (gp.getKey().getComponent() instanceof CPU
    // && !(actor.getName().contains("srv") || actor.getName().contains("urc"))))
    // .forEach(gp.getValue()::add));
    // }

    // return scenario updated
    output.put("scenario", scenario);

    output.put("cMem", clusterMemory);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    // stack default
    parameters.put(STACK_PARAM, STACK_SIZE_DEFAULT);
    parameters.put(LEVEL_PARAM, LEVEL_NUMBER_DEFAULT);
    parameters.put(CLUSTERING_PARAM, CLUSTERING_MODE_DEFAULT);

    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of clustering Task";
  }

}
