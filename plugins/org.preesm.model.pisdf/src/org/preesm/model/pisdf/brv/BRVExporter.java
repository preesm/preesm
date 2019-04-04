package org.preesm.model.pisdf.brv;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.pisdf.util.PiGraphConsistenceChecker;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This task computes and exports BRV of a PiSDF graph, as a CSV file.
 * 
 * @author ahonorat
 */
public class BRVExporter extends AbstractTaskImplementation {

  /**
   * @see StatsExporterTask
   */
  public static final String DEFAULT_PATH = "/stats/";

  public static final String PARAM_PATH = "path";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    PreesmLogger.getLogger().log(Level.INFO, "Computing Repetition Vector for graph [" + graph.getName() + "]");

    PiGraphConsistenceChecker.check(graph);
    // 1. First we resolve all parameters.
    // It must be done first because, when removing persistence, local parameters have to be known at upper level
    PiMMHelper.resolveAllParameters(graph);
    // 2. Compute BRV following the chosen method
    Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);

    final Map<PiGraph, Long> levelRV = new HashMap<>();
    final Map<AbstractVertex, Long> fullRV = new HashMap<>();

    for (final Entry<AbstractVertex, Long> en : brv.entrySet()) {
      final AbstractVertex av = en.getKey();
      final PiGraph container = av.getContainingPiGraph();
      if (!levelRV.containsKey(container)) {
        levelRV.put(container, PiMMHelper.getHierarchichalRV(container, brv));
      }
      final long actorRV = en.getValue();
      final long actorFullRV = actorRV * levelRV.get(container);
      fullRV.put(av, actorFullRV);
    }

    // TODO: write to file csv or xml?

    return null;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PARAM_PATH, DEFAULT_PATH);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computes and exports repetition vector as csv.";
  }

}
