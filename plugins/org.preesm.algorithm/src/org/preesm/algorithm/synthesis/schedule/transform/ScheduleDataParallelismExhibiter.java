package org.preesm.algorithm.synthesis.schedule.transform;

import java.util.LinkedList;
import java.util.List;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 *         Exhibit data parallelism in given schedule tree
 * 
 */
public class ScheduleDataParallelismExhibiter implements IScheduleTransform {

  @Override
  public Schedule performTransform(Schedule schedule) {

    if (schedule instanceof SequentialHiearchicalSchedule) {

      // if data parallelism can be exhibited
      PiGraph graph = (PiGraph) ((SequentialHiearchicalSchedule) schedule).getAttachedActor();
      boolean sequentialPersistenceInside = !graph.getFifosWithDelay().isEmpty();
      if ((schedule.getRepetition() > 1) && !sequentialPersistenceInside) {
        ParallelHiearchicalSchedule parallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
        parallelSchedule.setRepetition(schedule.getRepetition());
        schedule.setRepetition(1);
        parallelSchedule.getChildren().add(schedule);
        return parallelSchedule;
      }

      // Explore childrens
      List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(schedule.getChildren());
      // Clear list of children schedule
      schedule.getChildren().clear();
      for (Schedule child : childSchedules) {
        schedule.getChildren().add(performTransform(child));
      }
    }

    return schedule;
  }

}
