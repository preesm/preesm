package org.preesm.algorithm.synthesis.timer;

import org.preesm.model.pisdf.AbstractActor;

/**
 * Simple class to structure the representation of an actor timings, i.e. start/end/duration.
 *
 * @author anmorvan
 *
 */
public final class ActorExecutionTiming {

  private final long          startTime;
  private final long          duration;
  private final AbstractActor timedActor;

  /**
   */
  public ActorExecutionTiming(final AbstractActor timedActor, final long startTime, final long duration) {
    this.timedActor = timedActor;
    this.startTime = startTime;
    this.duration = duration;
  }

  public long getStartTime() {
    return this.startTime;
  }

  public long getDuration() {
    return this.duration;
  }

  public long getEndTime() {
    return this.startTime + this.duration;
  }

  public AbstractActor getTimedActor() {
    return this.timedActor;
  }

}
