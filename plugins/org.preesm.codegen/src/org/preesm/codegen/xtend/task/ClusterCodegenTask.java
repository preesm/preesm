package org.preesm.codegen.xtend.task;

import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "cluster.codegen", name = "Cluster Codegen", category = "Cluster Codegen",

    inputs = { @Port(name = "subgraphs", type = List.class), @Port(name = "scenario", type = Scenario.class),
      @Port(name = "architecture", type = Design.class), @Port(name = "schedules", type = List.class),
      @Port(name = "mappings", type = List.class), @Port(name = "allocations", type = List.class) },

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

    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design architecture = (Design) inputs.get("architecture");
    final List<PiGraph> clusters = (List<PiGraph>) inputs.get("subgraphs");
    final List<Schedule> schedules = (List<Schedule>) inputs.get("schedules");
    final List<Mapping> mappings = (List<Mapping>) inputs.get("mappings");
    final List<Allocation> allocations = (List<Allocation>) inputs.get("allocations");

    for (int i = 0; i < clusters.size(); i++) {
      final PiGraph cluster = clusters.get(i);
      final Schedule schedule = schedules.get(i);
      final Mapping mapping = mappings.get(i);
      final Allocation allocation = allocations.get(i);

    }

    return null;
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
