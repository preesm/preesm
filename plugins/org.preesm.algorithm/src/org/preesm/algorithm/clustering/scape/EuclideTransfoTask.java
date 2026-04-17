package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RefinementContainer;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class partition subdivide data-parallelism task in order to prepare the scaling of the SCAPE method. For more
 * details, see conference paper: "SimSDP: Dataflow Application Distribution on Heterogeneous Multi-Node Multi-Core
 * Architectures, published at xx 2024
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "euclide.transfo.task.identifier", name = "Euclide Task",
    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },
    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) })

public class EuclideTransfoTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph transfo = new EuclideTransfo(scenario).execute();

    for (final Entry<ComponentInstance, EList<Refinement>> gp : scenario.getConstraints().getRefinementConstraints()) {
      final int size = gp.getValue().size();
      for (int i = 1; !gp.getValue().isEmpty(); i++) {
        final int k = size - i;
        gp.getValue().remove(k);
      }

      transfo.getAllActors().stream().filter(RefinementContainer.class::isInstance).map(a -> (RefinementContainer) a)
          .forEach(actor -> gp.getValue().addAll(actor.getRefinements()));
    }

    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
        CheckerErrorLevel.FATAL_ALL);
    pgcc.check(transfo);
    // Build output map
    final Map<String, Object> output = new HashMap<>();
    // return topGraph
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, transfo);
    // return scenario updated
    output.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Euclide Task";
  }

}
