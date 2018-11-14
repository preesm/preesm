package org.ietr.preesm.pimm.algorithm.checker.periods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PeriodicElement;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.preesm.commons.logger.PreesmLogger;

/**
 * This class computes necessary conditions for the schedulability of graphs with periods.
 * 
 * @author ahonorat
 *
 */
public class PeriodsPreschedulingChecker extends AbstractTaskImplementation {

  /**
   * Identify the parameter to get the rate of periodic actors to analyze.
   */
  public static final String SELECTION_RATE = "Selection rate (%)";

  /**
   * By default all actors are analyzed.
   */
  public static final String DEFAULT_SELECTION_RATE = "100";

  private static final String GENERIC_RATE_ERROR = "Periodic actors selection rate "
      + "must be an integer between 1 and 100 (%), instead of: ";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {

    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    if (!graph.getChildrenGraphs().isEmpty()) {
      throw new WorkflowException("This task must be called with a flatten PiMM graph, abandon.");
    }

    final String rateStr = parameters.get(SELECTION_RATE);
    int rate = 100;
    try {
      rate = Integer.parseInt(rateStr);
      if (rate < 0 || rate > 100) {
        throw new WorkflowException(GENERIC_RATE_ERROR + rate + ".");
      }
    } catch (NumberFormatException e) {
      throw new WorkflowException(GENERIC_RATE_ERROR + rateStr + ".", e);
    }

    final Map<Actor, Long> periodicActors = new HashMap<>();
    for (final AbstractActor absActor : graph.getActors()) {
      if (absActor instanceof Actor && absActor instanceof PeriodicElement) {
        final Actor actor = (Actor) absActor;
        if (!actor.isHierarchical() && !actor.isConfigurationActor()) {
          final long period = actor.getPeriod().evaluate();
          if (period > 0) {
            periodicActors.put(actor, period);
          }
        }
      }
    }

    // 1. find all actor w/o incoming edges and all other with incoming edges
    final List<Actor> sourceActors = new ArrayList<>();
    final List<Actor> sinkActors = new ArrayList<>();
    for (final AbstractActor absActor : graph.getActors()) {
      if (absActor instanceof PeriodicElement) {
        final Actor actor = (Actor) absActor;
        if (actor.getDataOutputPorts().isEmpty()) {
          sinkActors.add(actor);
        }
        if (actor.getDataInputPorts().isEmpty()) {
          sourceActors.add(actor);
        }
      }
    }

    // 2. perform heuristic to select periodic nodes
    final StringBuilder sbNBFF = new StringBuilder();
    Map<Actor, Long> actorsNBFF = HeuristicPeriodicActorSelection.selectActors(periodicActors, sourceActors, rate,
        graph, scenario, false);
    actorsNBFF.keySet().forEach(a -> sbNBFF.append(a.getName() + " / "));
    PreesmLogger.getLogger().log(Level.WARNING, "Periodic actor for NBFF: " + sbNBFF.toString());

    final StringBuilder sbNBLF = new StringBuilder();
    Map<Actor, Long> actorsNBLF = HeuristicPeriodicActorSelection.selectActors(periodicActors, sinkActors, rate, graph,
        scenario, true);
    actorsNBLF.keySet().forEach(a -> sbNBLF.append(a.getName() + " / "));
    PreesmLogger.getLogger().log(Level.WARNING, "Periodic actor for NBLF: " + sbNBLF.toString());

    // 3. for each selected periodic node for nblf:
    // _a compute subgraph
    // _b compute nblf
    // 4. for each selected periodic node for nbff:
    // _a compute subgraph
    // _b compute nbff

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> parameters = new HashMap<>();
    parameters.put(SELECTION_RATE, DEFAULT_SELECTION_RATE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Checking if periods constraints prevent to have a schedulable application.";
  }

}
