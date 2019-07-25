package org.preesm.algorithm.schedule.evaluation.latency;

import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.evaluation.IScheduleEvaluator;
import org.preesm.algorithm.schedule.model.Schedule;

/**
 *
 * @author anmorvan
 *
 */
public class SimpleLatencyEvaluation implements IScheduleEvaluator<LatencyCost> {

  @Override
  public LatencyCost evaluate(final Mapping mapping, final Schedule schedule, final Allocation alloc) {
    // TODO
    return new LatencyCost(0);
  }

}
