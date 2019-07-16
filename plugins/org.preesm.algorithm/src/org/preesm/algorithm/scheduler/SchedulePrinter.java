package org.preesm.algorithm.scheduler;

import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.ParallelHiearchicalSchedule;
import org.preesm.model.algorithm.schedule.ParallelSchedule;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.algorithm.schedule.SequentialHiearchicalSchedule;
import org.preesm.model.pisdf.AbstractActor;

/**
 * @author dgageot
 *
 */
public class SchedulePrinter {

  /**
   * @param schedule
   *          schedule to print
   * @return corresponding String
   */
  public static final String printScheduleRec(final Schedule schedule) {
    StringBuilder toPrint = new StringBuilder();
    if (schedule instanceof SequentialHiearchicalSchedule) {
      // Print sequential operator
      for (Schedule e : schedule.getChildren()) {
        toPrint.append(printScheduleRec(e));
        toPrint.append("*");
      }
      toPrint.deleteCharAt(toPrint.length() - 1);
      // Print repetition with parenthesis if needed
      if (schedule.getRepetition() > 1) {
        toPrint.insert(0, schedule.getRepetition() + "(");
        toPrint.append(")");
      }
    } else if (schedule instanceof ParallelHiearchicalSchedule) {
      // Print parallel operator
      for (Schedule e : schedule.getChildren()) {
        toPrint.append(printScheduleRec(e));
        toPrint.append("|");
      }
      toPrint.deleteCharAt(toPrint.length() - 1);
      toPrint.insert(0, "(");
      toPrint.append(")");
      // Print repetition if needed
      if (schedule.getRepetition() > 1) {
        toPrint.insert(0, schedule.getRepetition() + "/");
      }
    } else if (schedule instanceof ActorSchedule) {
      // Print actor name
      for (AbstractActor a : schedule.getActors()) {
        toPrint.append(a.getName());
      }
      // Print repetition with parenthesis if needed
      if (schedule.getRepetition() > 1) {
        if (schedule instanceof ParallelSchedule) {
          toPrint.insert(0, schedule.getRepetition() + "/(");
        } else {
          toPrint.insert(0, schedule.getRepetition() + "(");
        }
        toPrint.append(")");
      }
    }
    return toPrint.toString();
  }

}
