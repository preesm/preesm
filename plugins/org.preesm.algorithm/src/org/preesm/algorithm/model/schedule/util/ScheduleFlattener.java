package org.preesm.algorithm.model.schedule.util;

import java.util.LinkedList;
import java.util.List;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;

/**
 * @author dgageot
 * 
 *         Perform a flattening of schedule tree (uniformize repetition on sequential hierarchical schedule)
 *
 */
public class ScheduleFlattener implements IScheduleTransform {

  @Override
  public Schedule performTransform(Schedule schedule) {
    // If it is an hierarchical schedule, explore and cluster actors
    if (schedule instanceof HierarchicalSchedule) {
      HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      // Retrieve childrens schedule and actors
      List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(hierSchedule.getChildren());
      // Clear list of children schedule
      hierSchedule.getChildren().clear();
      for (Schedule child : childSchedules) {
        Schedule processesChild = performTransform(child);
        if ((hierSchedule instanceof SequentialHiearchicalSchedule) && (child instanceof SequentialHiearchicalSchedule)
            && (child.getRepetition() == 1)) {
          hierSchedule.getChildren().addAll(processesChild.getChildren());
        } else {
          hierSchedule.getChildren().add(processesChild);
        }
      }
    }

    return schedule;
  }

}
