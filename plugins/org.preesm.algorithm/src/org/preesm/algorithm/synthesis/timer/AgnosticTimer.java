package org.preesm.algorithm.synthesis.timer;

import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.RoundBufferActor;
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

  /**
   * Compute WCET of actors, based on the scenario information.
   * 
   * @param scenario
   *          Scenario of the application.
   */
  public AgnosticTimer(final Scenario scenario) {
    super();
    this.scenario = scenario;
  }

  @Override
  protected long computeActorTiming(final Actor actor) {
    long wcet = 0L;
    for (final ComponentInstance operatorDefinitionID : scenario.getPossibleMappings(actor)) {
      long et = scenario.getTimings().evaluateTimingOrDefault(actor, operatorDefinitionID.getComponent());
      if (et > wcet) {
        wcet = et;
      }
    }
    return wcet;
  }

  @Override
  protected long computeForkActorTiming(final ForkActor forkActor) {
    return 0L;
  }

  @Override
  protected long computeJoinActorTiming(final JoinActor joinActor) {
    return 0L;
  }

  @Override
  protected long computeBroadcastActorTiming(final BroadcastActor broadcastActor) {
    return 0L;
  }

  @Override
  protected long computeRoundBufferActorTiming(final RoundBufferActor roundbufferActor) {
    return 0L;
  }

  @Override
  protected long computeInitActorTiming(final InitActor initActor) {
    return 0L;
  }

  @Override
  protected long computeEndActorTiming(final EndActor endActor) {
    return 0L;
  }

  @Override
  protected long computeSendStartActorTiming(final SendStartActor sendStartActor) {
    return 0L;
  }

  @Override
  protected long computeSendEndActorTiming(final SendEndActor sendEndActor) {
    return 0L;
  }

  @Override
  protected long computeReceiveStartActorTiming(final ReceiveStartActor receiveStartActor) {
    return 0L;
  }

  @Override
  protected long computeReceiveEndActorTiming(final ReceiveEndActor receiveEndActor) {
    return 0L;
  }

}
