package org.preesm.codegen.xtend.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.FunctionCoreBlock;
import org.preesm.codegen.model.generator2.PiCodegenModelGenerator2;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiSDFExporterTask;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "clustering.codegen", name = "Clustering task", category = "Clustering",

    parameters = {
      @Parameter(name = "Printer",
          description = "Will select the printer for the right language. For now, only C is available.",
          values = { @Value(name = "C") }),
      @Parameter(name = "Verbose", description = "", values = { @Value(name = "True"), @Value(name = "False") }),
      @Parameter(name = "Debug", description = "", values = { @Value(name = "True"), @Value(name = "False") }) },

    inputs = {
      @Port(name = "scenario", type = Scenario.class,
          description = "Input scenario, containing constraints of clusters"),
      @Port(name = "clusters", type = List.class, description = "Input clusters"),
      @Port(name = "schedules", type = List.class, description = "Input schedules of clusters"),
      @Port(name = "allocations", type = List.class, description = "Input allocations of clusters"), },

    description = """
            This task works with the task ClusterCreationTask. It will generate the code for every clusters in
            separate files. They will be used during the top graph synthesis process.
        """, shortDescription = "Workflow task responsible of generating the files "
        + "that contain the code of all the identified clusters.")

public class ClusterCodegenTask extends AbstractTaskImplementation {

  public static final String PARAM_PRINTER = "Printer";
  public static final String PARAM_VERBOSE = "Verbose";
  public static final String PARAM_DEBUG   = "Debug";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting inputs
    final List<PiGraph> clusters = (List<PiGraph>) inputs.get("clusters");
    final List<Schedule> schedules = (List<Schedule>) inputs.get("schedules");
    final List<Allocation> allocations = (List<Allocation>) inputs.get("allocations");

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final boolean debug = "true".equals(parameters.get(PARAM_DEBUG));

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
      Map<String, Object> exportInputs = null;
      final Map<String, String> exportParameters = null;
      if (debug) {
        exportInputs = new HashMap<>();
        exportInputs.put("PiMM", ((HierarchicalSchedule) schedule).getAttachedActor());
        new PiSDFExporterTask().execute(exportInputs, exportParameters, monitor, nodeName, workflow);
      }

      final PiGraph cluster = clusters.get(i);

      // It is supposed that a cluster has only one component type.
      final Component clusterComponentType = scenario.getPossibleMappings(cluster).getFirst().getComponent();

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
    final String selectedPrinter = parameters.get(PARAM_PRINTER);
    final String codegenPath = scenario.getCodegenDirectory() + File.separator;

    // algorithm and archi can here be set to null because there are not used in this particular codegen engine.
    final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, null, null, scenario);

    if (CodegenTask2.VALUE_PRINTER_IR.equals(selectedPrinter)) {
      engine.initializePrinterIR(codegenPath);
    }
    engine.registerPrintersAndBlocks(selectedPrinter);
    engine.preprocessPrinters();
    engine.print(false);

    // Create empty output map (codegen doesn't have output)
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> result = new HashMap<>();
    result.put(PARAM_PRINTER, "C");
    result.put(PARAM_VERBOSE, "true");
    result.put(PARAM_DEBUG, "true");
    return result;
  }

  @Override
  public String monitorMessage() {
    return "Identifies, creates, maps, schedules, allocates, and generates code for clusters";
  }

}
