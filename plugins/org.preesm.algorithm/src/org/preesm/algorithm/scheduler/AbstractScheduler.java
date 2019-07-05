package org.preesm.algorithm.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.algorithm.mapping.Mapping;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 *
 */
public abstract class AbstractScheduler implements IScheduler {

  @Override
  public SchedulerResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {
    verifyInputs(piGraph, slamDesign, scenario);
    final SchedulerResult res = exec(piGraph, slamDesign, scenario);
    verifyOutputs(piGraph, slamDesign, scenario, res.schedule, res.mapping);

    PreesmLogger.getLogger().log(Level.FINEST, res.toString());

    return res;
  }

  /**
   * Defines how the actors of the PiGraph are scheduled between them and mapped onto the slamDesign, respecting
   * constraints from the scenario.
   */
  protected abstract SchedulerResult exec(final PiGraph piGraph, final Design slamDesign, final Scenario scenario);

  /**
   * Verifies the consistency of the inputs.
   */
  private void verifyInputs(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {
    /*
     * Check graph
     */
    final PiGraph originalPiGraph = PreesmCopyTracker.getOriginalSource(piGraph);
    if (originalPiGraph != scenario.getAlgorithm()) {
      throw new PreesmSchedulerException("Input PiSDF graph is not derived from the scenario algorithm.");
    }

    /*
     * Check design
     */
    final Design originalDesign = PreesmCopyTracker.getOriginalSource(slamDesign);
    if (originalDesign != scenario.getDesign()) {
      throw new PreesmSchedulerException("Input Slam design is not derived from the scenario design.");
    }

  }

  /**
   * Verifies the consistency of the outputs.
   */
  private void verifyOutputs(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping) {

    // make sure all actors have been scheduled and schedule contains only actors from the input graph
    final List<AbstractActor> piGraphAllActors = new ArrayList<>(piGraph.getAllActors());
    final List<AbstractActor> scheduledActors = new ArrayList<>(schedule.getActors());
    if (!piGraphAllActors.containsAll(scheduledActors)) {
      throw new PreesmSchedulerException("Schedule refers actors not present in the input PiSDF.");
    }
    if (!scheduledActors.containsAll(piGraphAllActors)) {
      throw new PreesmSchedulerException("Schedule is missing order for some actors of the input PiSDF.");
    }

    final List<ComponentInstance> slamCmpInstances = new ArrayList<>(slamDesign.getComponentInstances());
    final List<ComponentInstance> usedCmpInstances = new ArrayList<>(mapping.getAllInvolvedComponentInstances());
    if (!slamCmpInstances.containsAll(usedCmpInstances)) {
      throw new PreesmSchedulerException("Mapping is using unknown component instances.");
    }

    for (final AbstractActor actor : piGraphAllActors) {
      verifyActor(scenario, mapping, actor);
    }
  }

  private void verifyActor(final Scenario scenario, final Mapping mapping, final AbstractActor actor) {
    final List<ComponentInstance> actorMapping = new ArrayList<>(mapping.getMapping(actor));

    final List<ComponentInstance> possibleMappings = new ArrayList<>(scenario.getPossibleMappings(actor));
    if (!possibleMappings.containsAll(actorMapping)) {
      throw new PreesmSchedulerException("Actor '" + actor + "' is mapped on '" + actorMapping
          + "' which is not in the authorized components list '" + possibleMappings + "'.");
    }

    if (actor instanceof InitActor) {
      final AbstractActor endReference = ((InitActor) actor).getEndReference();
      final List<ComponentInstance> targetMappings = new ArrayList<>(mapping.getMapping(endReference));
      if (!targetMappings.equals(actorMapping)) {
        throw new PreesmSchedulerException("Init and End actors are not mapped onto the same PE.");
      }
    } else if (actor instanceof EndActor) {
      final AbstractActor initReference = ((EndActor) actor).getInitReference();
      final List<ComponentInstance> targetMappings = new ArrayList<>(mapping.getMapping(initReference));
      if (!targetMappings.equals(actorMapping)) {
        throw new PreesmSchedulerException("Init and End actors are not mapped onto the same PE.");
      }
    }
  }

}