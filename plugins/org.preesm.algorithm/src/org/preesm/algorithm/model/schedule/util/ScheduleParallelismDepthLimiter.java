package org.preesm.algorithm.model.schedule.util;

import java.util.LinkedList;
import java.util.List;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;

/**
 * @author dgageot
 *
 *         Limit parallelism depth in schedule tree to a specified target
 *
 */
public class ScheduleParallelismDepthLimiter implements IScheduleTransform {

  long depthTarget;

  public ScheduleParallelismDepthLimiter(long depthTarget) {
    this.depthTarget = depthTarget;
  }

  @Override
  public Schedule performTransform(Schedule schedule) {
    return setParallelismDepth(schedule, 0);
  }

  private final Schedule setParallelismDepth(Schedule schedule, long iterator) {

    // Parallel schedule? Increment iterator because of new parallel layer
    if (schedule instanceof ParallelSchedule) {
      iterator++;
    }

    // Explore and replace children
    if (schedule instanceof HierarchicalSchedule) {
      HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(hierSchedule.getChildren());
      // Clear list of children schedule
      hierSchedule.getChildren().clear();
      for (Schedule child : childSchedules) {
        hierSchedule.getChildren().add(setParallelismDepth(child, iterator));
      }
    }

    // Rework if parallel is below the depth target
    if ((schedule instanceof ParallelSchedule) && (iterator > depthTarget)) {
      if (schedule instanceof HierarchicalSchedule) {
        if (!schedule.hasAttachedActor()) {
          Schedule childrenSchedule = schedule.getChildren().get(0);
          childrenSchedule.setRepetition(schedule.getRepetition());
          return childrenSchedule;
        } else {
          SequentialHiearchicalSchedule sequenceSchedule = ScheduleFactory.eINSTANCE
              .createSequentialHiearchicalSchedule();
          sequenceSchedule.setAttachedActor(((HierarchicalSchedule) schedule).getAttachedActor());
          sequenceSchedule.setRepetition(schedule.getRepetition());
          sequenceSchedule.getChildren().addAll(schedule.getChildren());
          return sequenceSchedule;
        }
      } else if (schedule instanceof ActorSchedule) {
        SequentialActorSchedule actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
        actorSchedule.setRepetition(schedule.getRepetition());
        actorSchedule.getActorList().addAll(((ActorSchedule) schedule).getActors());
        return actorSchedule;
      }
    }

    return schedule;
  }

}
