package org.preesm.algorithm.node.partitioner;

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
 * This class partition graph into subgraph assigned to a node For more details, see conference paper: "SimSDP: Dataflow
 * Application Distribution on Heterogeneous Multi-Node Multi-Core Architectures, published at xx 2024
 *
 * @author orenaud
 *
 */

@PreesmTask(id = "node.partitioner.task.identifier", name = "Node Partitioner",
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },
    // outputs= {},
    parameters = {
        @Parameter(name = "Archi path", description = "temp archi description",
            values = { @Value(name = "String", effect = "oui oui") }),
        @Parameter(name = "Workload path", description = "temp workload path",
            values = { @Value(name = "String", effect = "oui oui") }),
        @Parameter(name = "Printer",
            description = "Specify which printer should be used to generate code. Printers are defined in Preesm source"
                + " code using an extension mechanism that make it possible to define"
                + " a single printer name for several " + "targeted architecture. Hence, depending on the type of PEs "
                + "declared in the architecture model, Preesm "
                + "will automatically select the associated printer class, if it exists.",
            values = { @Value(name = "C",
                effect = "Print C code and shared-memory based communications. Currently compatible with x86, c6678, "
                    + "and arm architectures."),
                @Value(name = "InstrumentedC",
                    effect = "Print C code instrumented with profiling code, and shared-memory based communications. "
                        + "Currently compatible with x86, c6678 architectures.."),
                @Value(name = "XML",
                    effect = "Print XML code with all informations used by other printers to print code. "
                        + "Compatible with x86, c6678.") }),

    })
public class NodePartitionerTask extends AbstractTaskImplementation {

  public static final String ARCHI_PATH_DEFAULT    = "";
  public static final String ARCHI_PATH_PARAM      = "archi path";
  public static final String WORKLOAD_PATH_DEFAULT = "";
  public static final String WORKLOAD_PATH_PARAM   = "Workload path";
  public static final String PARAM_PRINTER         = "Printer";
  public static final String VALUE_PRINTER_IR      = "IR";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final String archipath = parameters.get(NodePartitionerTask.ARCHI_PATH_PARAM);
    final String workloadpath = parameters.get(NodePartitionerTask.WORKLOAD_PATH_PARAM);

    final PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design archi = (Design) inputs.get("architecture");
    new NodePartitioner(inputGraph, scenario, archi, archipath, workloadpath).execute();
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(NodePartitionerTask.ARCHI_PATH_PARAM, NodePartitionerTask.ARCHI_PATH_DEFAULT);
    parameters.put(NodePartitionerTask.WORKLOAD_PATH_PARAM, NodePartitionerTask.WORKLOAD_PATH_DEFAULT);
    parameters.put(NodePartitionerTask.PARAM_PRINTER, NodePartitionerTask.VALUE_PRINTER_IR);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of node partitioner Task";
  }

}
