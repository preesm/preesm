package org.preesm.algorithm.schedule.evaluation.latency;

import org.preesm.algorithm.schedule.evaluation.IScheduleCost;

/**
 *
 * @author anmorvan
 *
 */
public class LatencyCost implements IScheduleCost<Long> {

  private final long latency;

  public LatencyCost(final long latency) {
    this.latency = latency;
  }

  public Long getValue() {
    return latency;
  }

  @Override
  public int compareTo(IScheduleCost<Long> o) {
    final long diff = this.latency - o.getValue();
    return (diff > 0) ? 1 : ((diff < 0) ? -1 : 0);
  }

}
