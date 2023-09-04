package org.preesm.algorithm.euclide.transfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
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
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    parameters = {
        @Parameter(name = "Core number", description = "number of target cores", values = { @Value(name = "Fixed:=n",
            effect = "the number of cores of the target allows to quantify the number of possible clusters") }),

    })

public class EuclideTransfoTask extends AbstractTaskImplementation {
  public static final String CORE_AMOUNT_DEFAULT = "1";          // 1
  public static final String CORE_PARAM          = "Core number";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // retrieve input parameter
    final String core = parameters.get(EuclideTransfoTask.CORE_PARAM);

    // Task inputs
    final PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design archi = (Design) inputs.get("architecture");
    final Long coreAmount = (long) archi.getOperatorComponentInstances().size();

    final PiGraph transfo = new EuclideTransfo(inputGraph, scenario, archi, coreAmount).execute();

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
    transfo.getFifos().stream().peek(x -> pgcc.caseFifo(x));
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
    // core default
    parameters.put(EuclideTransfoTask.CORE_PARAM, EuclideTransfoTask.CORE_AMOUNT_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Euclide Task";
  }

}
