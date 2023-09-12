package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class cluster actor in order to match the target architecture For more details, see conference paper: "SCAPE:
 * HW-Aware Clustering of Dataflow Actors for Tunable Scheduling Complexity", published at DASIP 2023, "Automated
 * Clustering and Pipelining of Dataflow Actors for Controlled Scheduling Complexity" published at EUSIPCO 2023, and,
 * "Automated Level-Based Clustering of Dataflow Actors for Controlled Scheduling Complexity", published at JSA 2023
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "clustering.raiser.task.identifier", name = "Clustering Task",
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },

    parameters = { @Parameter(name = "Printer",
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

        @Parameter(name = "Stack size", description = "stack size (in Byte)",
            values = { @Value(name = "Fixed:=n",
                effect = "the size of the stack allows to quantify the number of allocable buffer "
                    + "in the stack the rest in the heap") }),

        @Parameter(name = "Core number", description = "number of target cores",
            values = { @Value(name = "Fixed:=n",
                effect = "the number of cores of the target allows to quantify the number of possible clusters") }),

        @Parameter(name = "Level number", description = "number of level to cluster",
            values = { @Value(name = "Fixed:=n",
                effect = "the number of level to cluster in order to reach flattener performance "
                    + "and compromising analysis time") }),

        @Parameter(name = "SCAPE mode",
            description = "choose the clustering mode : 1 = set of clustering config + only fit data parallelism,"
                + " 2 = set of clustering config + fit data & pip parallelism, 3 = best clustering config ",
            values = { @Value(name = "Fixed:=n", effect = "switch of clustering algorithm") }),

        @Parameter(name = "Optimized Cluster",
            description = "compute the best cluster number at : false = level, true = APGAN(level)",
            values = { @Value(name = "true/false", effect = "switch of cluster level") }),

        @Parameter(name = "Non-cluster actor", description = "does not allow to group the actors entered in parameter",
            values = { @Value(name = "String", effect = "disable cluster") }), })
public class ClusteringScapeTask extends AbstractTaskImplementation {
  public static final String STACK_SIZE_DEFAULT      = "1000000";          // 1MB
  public static final String STACK_PARAM             = "Stack size";
  public static final String CORE_AMOUNT_DEFAULT     = "1";                // 1
  public static final String CORE_PARAM              = "Core number";
  public static final String CLUSTERING_MODE_DEFAULT = "0";                // SCAPE1
  public static final String CLUSTERING_PARAM        = "SCAPE mode";
  public static final String LEVEL_NUMBER_DEFAULT    = "1";                // 1
  public static final String LEVEL_PARAM             = "Level number";
  public static final String NON_CLUSTER_DEFAULT     = "";
  public static final String NON_CLUSTER_PARAM       = "Non-cluster actor";

  public static final String PARAM_PRINTER    = "Printer";
  public static final String VALUE_PRINTER_IR = "IR";

  protected long stack;
  protected long core;
  protected int  cluster;
  protected int  mode;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // retrieve input parameter stack size
    final String stackStr = parameters.get(ClusteringScapeTask.STACK_PARAM);
    this.stack = Integer.decode(stackStr);
    // retrieve input parameter
    final String coreStr = parameters.get(ClusteringScapeTask.CORE_PARAM);
    this.core = Integer.decode(coreStr);
    // retrieve input parameter
    final String clusterStr = parameters.get(ClusteringScapeTask.LEVEL_PARAM);
    this.cluster = Integer.decode(clusterStr);
    // retrieve input parameter
    final String modeStr = parameters.get(ClusteringScapeTask.CLUSTERING_PARAM);
    this.mode = Integer.decode(modeStr);
    if (!(this.mode == 0L || this.mode == 1L || this.mode == 2L)) {
      final String errorMessage = "SCAPE mode-> 0: data// + set of config,"
          + " 1: all// +set of config, 2: all//+best config";
      PreesmLogger.getLogger().log(Level.SEVERE, errorMessage);
    }

    final String nonClusterable = parameters.get(ClusteringScapeTask.NON_CLUSTER_PARAM);
    final String[] nonClusterableListStr = nonClusterable.split("\\*");

    // Task inputs
    final PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design archi = (Design) inputs.get("architecture");
    final Long stackSize = this.stack;
    final Long coreAmount = (long) archi.getOperatorComponentInstances().size();
    final int clusterNumber = this.cluster;
    final int clusteringMode = this.mode;

    final List<AbstractActor> nonClusterableList = new LinkedList<>();
    for (final String element : nonClusterableListStr) {
      for (final AbstractActor a : inputGraph.getExecutableActors()) {
        if (a.getName().equals(element) && !nonClusterableList.contains(a)) {
          nonClusterableList.add(a);
        }
      }
    }

    final PiGraph tempGraph = new ClusteringScape(inputGraph, scenario, archi, stackSize, coreAmount, clusteringMode,
        clusterNumber, nonClusterableList).execute();

    final Map<String, Object> output = new HashMap<>();
    // return topGraph
    output.put("PiMM", tempGraph);
    for (final Entry<ComponentInstance, EList<AbstractActor>> gp : scenario.getConstraints().getGroupConstraints()) {
      final int size = gp.getValue().size();
      for (int i = 1; !gp.getValue().isEmpty(); i++) {
        final int k = size - i;
        gp.getValue().remove(k);
      }
      for (final AbstractActor actor : tempGraph.getAllActors()) {

        gp.getValue().add(actor);
      }
    }

    // return scenario updated
    output.put("scenario", scenario);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    final StringBuilder avilableLanguages = new StringBuilder("? C {");

    // Retrieve the languages registered with the printers
    final Set<String> languages = new LinkedHashSet<>();
    final IExtensionRegistry registry = Platform.getExtensionRegistry();

    final IConfigurationElement[] elements = registry
        .getConfigurationElementsFor("org.ietr.preesm.codegen.xtend.printers");
    for (final IConfigurationElement element : elements) {
      languages.add(element.getAttribute("language"));
    }

    for (final String lang : languages) {
      avilableLanguages.append(lang + ", ");
    }
    avilableLanguages.append(ClusteringScapeTask.VALUE_PRINTER_IR + "}");

    parameters.put(ClusteringScapeTask.PARAM_PRINTER, avilableLanguages.toString());

    // stack default
    parameters.put(ClusteringScapeTask.STACK_PARAM, ClusteringScapeTask.STACK_SIZE_DEFAULT);
    // core default
    parameters.put(ClusteringScapeTask.CORE_PARAM, ClusteringScapeTask.CORE_AMOUNT_DEFAULT);
    // core default
    parameters.put(ClusteringScapeTask.LEVEL_PARAM, ClusteringScapeTask.LEVEL_NUMBER_DEFAULT);
    // mode default
    parameters.put(ClusteringScapeTask.CLUSTERING_PARAM, ClusteringScapeTask.CLUSTERING_MODE_DEFAULT);
    // mode default
    parameters.put(ClusteringScapeTask.NON_CLUSTER_PARAM, ClusteringScapeTask.NON_CLUSTER_DEFAULT);

    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of clustering Task";
  }

}
