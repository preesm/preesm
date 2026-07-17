package org.preesm.codegen.xtend.task;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.generator.pisdf.CodegenModelGeneratorPiSDF;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * @author rcazoulat
 */
@PreesmTask(id = "clustering.codegen", name = "Cluster Codegen", category = "Cluster Codegen",

    inputs = { @Port(name = "subgraphs", type = List.class), @Port(name = "scenario", type = Scenario.class),
      @Port(name = "architecture", type = Design.class), @Port(name = "schedules", type = List.class),
      @Port(name = "allocations", type = List.class), @Port(name = "Mapping", type = Mapping.class) },

    parameters = {
      @Parameter(name = "Printer",
          description = "Specify which printer should be used to generate code. Printers are defined in Preesm source"
              + " code using an extension mechanism that make it possible to define a single printer name for several "
              + "targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm "
              + "will automatically select the associated printer class, if it exists.",
          values = {
            @Value(name = "C",
                effect = "Print C code and shared-memory based communications. Currently compatible with x86, c6678, "
                    + "and arm architectures."),
            @Value(name = "InstrumentedC",
                effect = "Print C code instrumented with profiling code, and shared-memory based communications. "
                    + "Currently compatible with x86, c6678 architectures.."),
            @Value(name = "XML",
                effect = "Print XML code with all informations used by other printers to print code. "
                    + "Compatible with x86, c6678.") }),
      @Parameter(name = "Papify", description = "Enable the PAPI-based code instrumentation provided by PAPIFY",
          values = { @Value(name = "true/false",
              effect = "Print C code instrumented with PAPIFY function calls based on the user-defined configuration"
                  + " of PAPIFY tab in the scenario. Currently compatibe with x86 and MPPA-256") }) })
public class ClusterCodegenTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    PreesmLogger.getLogger().info(" --  Cluster codegen (PiSDF codegen) --");

    final Scenario scenario = (Scenario) inputs.get("scenario");
    if (scenario.getCodegenDirectory() == null) {
      throw new PreesmRuntimeException("Codegen path has not been specified in scenario, cannot go further.");
    }
    final Design archi = (Design) inputs.get("architecture");
    final Mapping mapping = (Mapping) inputs.get("Mapping");
    final List<Allocation> allocations = (List<Allocation>) inputs.get("allocations");
    final List<PiGraph> clusters = (List<PiGraph>) inputs.get("subgraphs");
    final List<Schedule> schedules = (List<Schedule>) inputs.get("schedules");

    final boolean papify = "true".equalsIgnoreCase(parameters.get(CodegenTask2.PARAM_PAPIFY));

    for (int i = 0; i < clusters.size(); i++) {
      final PiGraph cluster = clusters.get(i);
      final Schedule schedule = schedules.get(i);
      final Allocation alloc = allocations.get(i);

      PreesmLogger.getLogger().log(Level.INFO, "Generating blocks.");
      final List<Block> codeBlocks = CodegenModelGeneratorPiSDF.generate(archi, cluster, scenario, schedule, mapping,
          alloc, papify);

      PreesmLogger.getLogger().info(String.format("Printing blocks of cluster %s.", cluster.getName()));

      // Retrieve the desired printer and target folder path
      final String selectedPrinter = parameters.get(CodegenTask2.PARAM_PRINTER);
      final String codegenPath = scenario.getCodegenDirectory() + File.separator + "clusters" + File.separator;

      // Create the codegen engine
      final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, cluster, archi, scenario);

      if (CodegenTask2.VALUE_PRINTER_IR.equals(selectedPrinter)) {
        engine.initializePrinterIR(codegenPath);
      }

      engine.registerPrintersAndBlocks(selectedPrinter);
      engine.preprocessPrinters();
      engine.print();

    }
    // Create empty output map (codegen doesn't have output)
    return Collections.emptyMap();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

}
