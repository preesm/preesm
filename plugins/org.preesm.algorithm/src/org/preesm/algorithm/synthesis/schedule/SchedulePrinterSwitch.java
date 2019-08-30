package org.preesm.algorithm.synthesis.schedule;

import java.util.LinkedList;
import java.util.List;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.model.pisdf.AbstractActor;

/**
 * @author dgageot
 *
 */
public class SchedulePrinterSwitch extends ScheduleSwitch<String> {

  public static final String print(final Schedule s) {
    return new SchedulePrinterSwitch().doSwitch(s);
  }

  Schedule parentNode;

  public SchedulePrinterSwitch() {
    super();
    this.parentNode = null;
  }

  @Override
  public String caseActorSchedule(ActorSchedule object) {
    StringBuilder toPrint = new StringBuilder();
    if (object.getRepetition() > 1) {
      toPrint.append(object.getRepetition());
      if (object.isParallel()) {
        toPrint.append("/");
      }
      toPrint.append("(");
    }

    // Print actors names
    List<String> actorsNames = new LinkedList<>();
    for (AbstractActor actor : object.getActors()) {
      actorsNames.add(actor.getName());
    }
    toPrint.append(String.join("", actorsNames));

    if (object.getRepetition() > 1) {
      toPrint.append(")");
    }
    return toPrint.toString();
  }

  @Override
  public String caseSequentialHiearchicalSchedule(SequentialHiearchicalSchedule object) {
    StringBuilder toPrint = new StringBuilder();

    if (object.getRepetition() > 1) {
      toPrint.append(object.getRepetition());
      if (this.parentNode != null) {
        if (this.parentNode.isParallel()) {
          toPrint.append("/");
        }
      }
      toPrint.append("(");
    }

    // Print sequential operator
    List<String> schedulesExpressions = new LinkedList<>();
    for (Schedule children : object.getChildren()) {
      this.parentNode = object;
      schedulesExpressions.add(doSwitch(children));
    }
    toPrint.append(String.join("*", schedulesExpressions));

    if (object.getRepetition() > 1) {
      toPrint.append(")");
    }

    return toPrint.toString();
  }

  @Override
  public String caseParallelHiearchicalSchedule(ParallelHiearchicalSchedule object) {
    StringBuilder toPrint = new StringBuilder();

    if (object.getRepetition() > 1) {
      toPrint.append(object.getRepetition() + "/(");
    }

    // Print parallel operator
    List<String> schedulesExpressions = new LinkedList<>();
    for (Schedule children : object.getChildren()) {
      this.parentNode = object;
      schedulesExpressions.add(doSwitch(children));
    }

    toPrint.append(String.join("|", schedulesExpressions));

    if (object.getRepetition() > 1) {
      toPrint.append(")");
    }

    return toPrint.toString();
  }

}
