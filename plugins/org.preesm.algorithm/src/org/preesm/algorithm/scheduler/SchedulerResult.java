package org.preesm.algorithm.scheduler;

import java.util.List;
import java.util.stream.Collectors;
import org.preesm.model.algorithm.mapping.Mapping;
import org.preesm.model.algorithm.schedule.HierarchicalSchedule;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;

/**
 *
 */
public class SchedulerResult {
  public final Mapping  mapping;
  public final Schedule schedule;

  public SchedulerResult(final Mapping mapping, final Schedule schedule) {
    this.mapping = mapping;
    this.schedule = schedule;
  }

  @Override
  public String toString() {
    return "\n\n" + buildString(schedule, mapping, "").toString();
  }

  private static StringBuilder buildString(final Schedule sched, final Mapping mapp, final String indent) {
    final StringBuilder res = new StringBuilder("");
    res.append(indent + sched.getClass().getSimpleName() + " {\n");
    if (sched instanceof HierarchicalSchedule) {
      for (final Schedule child : sched.getChildren()) {
        res.append(buildString(child, mapp, indent + "  ").toString());
      }
    } else {
      for (final AbstractActor actor : sched.getActors()) {
        final List<String> collect = mapp.getMapping(actor).stream().map(m -> m.getInstanceName())
            .collect(Collectors.toList());
        res.append(indent + "  " + collect + " " + actor.getName() + "\n");
      }
    }
    res.append(indent + "}\n");
    return res;
  }

}
