package org.preesm.algorithm.clustering.scape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
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
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) })
public class EuclideTransfoTask extends AbstractTaskImplementation {
  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Task inputs

    final Scenario scenario = (Scenario) inputs.get("scenario");
    final PiGraph transfo = new EuclideTransfo(scenario).execute();

    for (final Entry<ComponentInstance, EList<AbstractActor>> gp : scenario.getConstraints().getGroupConstraints()) {
      final int size = gp.getValue().size();
      for (int i = 1; !gp.getValue().isEmpty(); i++) {
        final int k = size - i;
        gp.getValue().remove(k);
      }
      for (final AbstractActor actor : transfo.getAllActors()) {

        gp.getValue().add(actor);
      }
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

    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Euclide Task";
  }

}
