package org.preesm.algorithm.synthesis.timer;

import org.preesm.model.pisdf.AbstractActor;

/**
 *
 * @author anmorvan
 *
 */
public class ExecutionTiming {

  private final long          startTime;
  private final long          duration;
  private final AbstractActor timedActor;

  /**
   */
  public ExecutionTiming(final AbstractActor timedActor, final long startTime, final long duration) {
    this.timedActor = timedActor;
    this.startTime = startTime;
    this.duration = duration;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getDuration() {
    return duration;
  }

  public long getEndTime() {
    return startTime + duration;
  }

  public AbstractActor getTimedActor() {
    return timedActor;
  }

}
