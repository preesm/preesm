package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class partition subdivide data-parallelism task in order to prepare the scaling of the SCAPE method For more
 * details, see conference paper: "SimSDP: Dataflow Application Distribution on Heterogeneous Multi-Node Multi-Core
 * Architectures, published at xx 2024
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "euclide.transfo.task.identifier", name = "Euclide Task",
    inputs = { @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    parameters = {
        @Parameter(name = "Level number", description = "number of level to cluster",
            values = { @Value(name = "Fixed:=n",
                effect = "the number of level to cluster in order to reach flattener performance "
                    + "and compromising analysis time") }),

        @Parameter(name = "SCAPE mode",
            description = "choose the clustering mode : 1 = set of clustering config + only fit data parallelism,"
                + " 2 = set of clustering config + fit data & pip parallelism, 3 = best clustering config ",
            values = { @Value(name = "Fixed:=n", effect = "switch of clustering algorithm") }), })
public class EuclideTransfoTask extends AbstractTaskImplementation {
  public static final String CLUSTERING_MODE_DEFAULT = "0";           // SCAPE1
  public static final String CLUSTERING_PARAM        = "SCAPE mode";
  public static final String LEVEL_NUMBER_DEFAULT    = "1";           // 1
  public static final String LEVEL_PARAM             = "Level number";
  protected int              cluster;
  protected int              mode;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Task inputs
    // retrieve input parameter
    final String clusterStr = parameters.get(EuclideTransfoTask.LEVEL_PARAM);
    this.cluster = Integer.decode(clusterStr);
    // retrieve input parameter
    final String modeStr = parameters.get(EuclideTransfoTask.CLUSTERING_PARAM);
    this.mode = Integer.decode(modeStr);
    if (!(this.mode == 0L || this.mode == 1L || this.mode == 2L)) {
      final String errorMessage = "SCAPE mode-> 0: data// + set of config,"
          + " 1: all// +set of config, 2: all//+best config";
      PreesmLogger.getLogger().log(Level.SEVERE, errorMessage);
    }
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final PiGraph transfo = new EuclideTransfo(scenario, mode, cluster).execute();

    for (final Entry<ComponentInstance, EList<AbstractActor>> gp : scenario.getConstraints().getGroupConstraints()) {
      final int size = gp.getValue().size();
      for (int i = 1; !gp.getValue().isEmpty(); i++) {
        final int k = size - i;
        gp.getValue().remove(k);
      }

      transfo.getAllActors().forEach(actor -> gp.getValue().add(actor));
    }

    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
        CheckerErrorLevel.FATAL_ALL);
    pgcc.check(transfo);
    // Build output map
    final Map<String, Object> output = new HashMap<>();
    // return topGraph
    output.put("PiMM", transfo);
    // return scenario updated
    output.put("scenario", scenario);

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put(ClusteringScapeTask.LEVEL_PARAM, ClusteringScapeTask.LEVEL_NUMBER_DEFAULT);

    parameters.put(ClusteringScapeTask.CLUSTERING_PARAM, ClusteringScapeTask.CLUSTERING_MODE_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Euclide Task";
  }

}
