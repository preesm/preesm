package org.preesm.codegen.xtend.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.ClusterCreationTask;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.identification.ClusterIdentifier;
import org.preesm.algorithm.clustering.synthesis.ClusterSynthesis;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.FunctionCoreBlock;
import org.preesm.codegen.model.generator2.PiCodegenModelGenerator2;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiSDFExporterTask;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "clustering.condensed", name = "Clustering task", category = "Clustering",

    parameters = { @Parameter(name = ClusterCreationTask.PARAM_VERTICAL_HEURISTIC, description = "", values = {}),
      @Parameter(name = ClusterCreationTask.PARAM_HORIZONTAL_HEURISTIC, description = "", values = {}),
      @Parameter(name = ClusterCreationTask.PARAM_MAPPING_HEURISTIC, description = "", values = {}),
      @Parameter(name = ClusterCreationTask.PARAM_BALANCING_HEURISTIC, description = "", values = {}),
      @Parameter(name = ClusterCreationTask.PARAM_SCHEDULING_HEURISTIC, description = "", values = {}),
      @Parameter(name = ClusterCreationTask.PARAM_ALLOCATION_HEURISTIC, description = "", values = {}),
      @Parameter(name = ClusterCodegenTask.PARAM_PRINTER, description = "", values = {}),
      @Parameter(name = ClusterCreationTask.PARAM_VERBOSE, description = "", values = {}),
      @Parameter(name = ClusterCreationTask.PARAM_DEBUG, description = "", values = {}) },

    inputs = { @Port(name = "scenario", type = Scenario.class, description = "Input scenario"),
      @Port(name = "PiMM", type = PiGraph.class, description = "Input PiGraph algorithm"),
      @Port(name = "architecture", type = Design.class, description = "Input S-LAM architecture graph") },

    outputs = {
      @Port(name = "PiMM", type = PiGraph.class, description = "Output PiGraph algorithm, modified by heuristics"),
      @Port(name = "scenario", type = Scenario.class, description = "Modified scenario"),
      @Port(name = "architecture", type = Design.class,
          description = "Untouched architecture, just to avoid having an ugly workflow graph.") },

    description = "Workflow task responsible for clustering actors. It regroups the 2 clustering tasks "
        + "(ClusterCreationTask and ClusterCodegenTask) in one. It is prefered to use the 2 separated classes."
        + "For more infos on clustering, go see the two following classes : "
        + "ClusterCreationTask and ClusterCodegenTask.",
    shortDescription = "Workflow task responsible for clustering actors. "

)
public class BigClusteringTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting inputs
    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final boolean debug = "true".equals(parameters.get(ClusterCreationTask.PARAM_DEBUG));

    // ========================================================================
    //
    // GRAPH TRANSFO & CLUSTER ID
    //
    // ========================================================================
    final Map<PiGraph,
        Component> clustersWithComponent = ClusterIdentifier.identify(algorithm, scenario, architecture, parameters);
    final List<PiGraph> clusters = new ArrayList<>(clustersWithComponent.keySet());

    // Debug
    Map<String, Object> exportInputs = null;
    Map<String, String> exportParameters = null;
    if (debug) {
      exportInputs = new HashMap<>();
      exportInputs.put("PiMM", algorithm);
      exportParameters = new HashMap<>();
      exportParameters.put("path", "/Algo/generated/clustering/debug/");
      exportParameters.put("hierarchical", "true");
      new PiSDFExporterTask().execute(exportInputs, exportParameters, monitor, nodeName, workflow);
    }

    // ========================================================================
    //
    // CLUSTERS SCHEDULING & ALLOCATION
    //
    // ========================================================================
    final Set<SynthesisResult> clusterSyntheses = ClusterSynthesis.scheduleAndAllocate(algorithm, scenario,
        architecture, clusters, parameters);
    final List<Schedule> schedules = clusterSyntheses.stream().map(x -> x.schedule).toList();
    final List<Allocation> allocations = clusterSyntheses.stream().map(x -> x.alloc).toList();

    final Map<String, Object> outputs = new HashMap<>();

    if (clusters.size() != clusterSyntheses.size()) {
      throw new PreesmRuntimeException("wtf");
    }

    // ========================================================================
    //
    // CLUSTERS CODEGEN
    //
    // ========================================================================

    final List<Block> codeBlocks = new ArrayList<>();

    for (int i = 0; i < clusters.size(); i++) {
      final Schedule schedule = schedules.get(i);
      final Allocation alloc = allocations.get(i);

      // Debug
      if (debug) {
        exportInputs = new HashMap<>();
        exportInputs.put("PiMM", ((HierarchicalSchedule) schedule).getAttachedActor());
        new PiSDFExporterTask().execute(exportInputs, exportParameters, monitor, nodeName, workflow);
      }

      final PiGraph cluster = clusters.get(i);
      final Component clusterComponentType = clustersWithComponent.get(cluster);

      final var clusterModelGen = new PiCodegenModelGenerator2(cluster, scenario);
      clusterModelGen.generate(schedule, alloc);

      final FunctionCoreBlock clusterBlock = CodegenModelUserFactory.eINSTANCE.createFunctionCoreBlock();
      clusterBlock.setCoreType(clusterComponentType.getVlnv().getName());
      clusterBlock.setInitBlock(clusterModelGen.getCallFunctionBlock());
      clusterBlock.setLoopBlock(clusterModelGen.getLoopFunctionBlock());
      clusterBlock.setName(cluster.getName());
      codeBlocks.add(clusterBlock);

    }

    // Retrieve the desired printer and target folder path
    final String selectedPrinter = parameters.get(ClusterCodegenTask.PARAM_PRINTER);
    final String codegenPath = scenario.getCodegenDirectory() + File.separator;

    final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, algorithm, architecture, scenario);

    if (CodegenTask2.VALUE_PRINTER_IR.equals(selectedPrinter)) {
      engine.initializePrinterIR(codegenPath);
    }
    engine.registerPrintersAndBlocks(selectedPrinter);
    engine.preprocessPrinters();
    engine.print(false);

    // ========================================================================
    //
    // OUTPUTS
    //
    // ========================================================================
    outputs.put("PiMM", algorithm);
    outputs.put("scenario", scenario);
    outputs.put("architecture", architecture);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> result = new HashMap<>();
    result.put(ClusterCreationTask.PARAM_VERTICAL_HEURISTIC, "");
    result.put(ClusterCreationTask.PARAM_HORIZONTAL_HEURISTIC, HeuristicGetter.URC_IDENTIFIER);
    result.put(ClusterCreationTask.PARAM_MAPPING_HEURISTIC, HeuristicGetter.CLASSIC_MAPPER);
    result.put(ClusterCreationTask.PARAM_BALANCING_HEURISTIC, HeuristicGetter.COMPLETE_BALANCING);
    result.put(ClusterCreationTask.PARAM_SCHEDULING_HEURISTIC, HeuristicGetter.APGAN_SCHEDULING);
    result.put(ClusterCodegenTask.PARAM_PRINTER, "C");
    result.put(ClusterCreationTask.PARAM_VERBOSE, "true");
    result.put(ClusterCreationTask.PARAM_DEBUG, "true");
    return result;
  }

  @Override
  public String monitorMessage() {
    return "Identifies, creates, maps, schedules, allocates, and generates code for clusters";
  }

}
