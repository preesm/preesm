package org.preesm.algorithm.synthesis.timer;

import org.preesm.model.pisdf.Actor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * This timer only returns the timings of regular actors, other actors are 0 by default.
 * 
 * @author ahonorat
 *
 */
public class AgnosticTimer extends AbstractTimer {

  protected final Scenario scenario;

  protected final long defaultTime;

  /**
   * Compute WCET of actors, based on the scenario information.
   * 
   * @param scenario
   *          Scenario of the application.
   * @param defaultTime
   *          Default time of an actor if not stored in scenario nor computed.
   */
  public AgnosticTimer(final Scenario scenario, final long defaultTime) {
    super();
    this.scenario = scenario;
    this.defaultTime = defaultTime;
  }

  @Override
  protected long computeActorTiming(final Actor actor) {
    long wcet = 1L;
    for (final ComponentInstance operatorDefinitionID : scenario.getPossibleMappings(actor)) {
      long et = scenario.getTimings().evaluateTimingOrDefault(actor, operatorDefinitionID.getComponent());
      if (et > wcet) {
        wcet = et;
      }
    }
    return wcet;
  }

  @Override
  protected long defaultTime() {
    return defaultTime;
  }

}
