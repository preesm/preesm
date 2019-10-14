package org.preesm.algorithm.synthesis.schedule.transform;

import java.util.LinkedList;
import java.util.List;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.SequentialSchedule;

/**
 * @author dgageot
 * 
 *         Identifies and moves actors inside a sequential and repeated schedule that can be parallelized outside to
 *         gain in performance. The input schedule should have been flattened for a better result.
 *
 */
public class ScheduleParallelismOptimizer implements IScheduleTransform {

  private static final int TEMPORARY_HIERARCHY = -1;

  @Override
  public Schedule performTransform(final Schedule schedule) {
    // If it is an hierarchical schedule, explore child
    if (schedule instanceof HierarchicalSchedule) {

      // This boolean variable is used to determine if the schedule being processed is composed of sequential element
      boolean isComposedOfSequentialSchedule = false;
      // Retrieve childrens schedule and actors
      final HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      final List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(hierSchedule.getChildren());
      // Clear list of children schedule
      hierSchedule.getChildren().clear();
      final List<Schedule> parallelSchedules = new LinkedList<>();
      for (final Schedule child : childSchedules) {
        final Schedule processedChild = performTransform(child);
        // Is processed children a temporary hierarchy that has been transformed by this algorithm?
        if (processedChild.getRepetition() == ScheduleParallelismOptimizer.TEMPORARY_HIERARCHY) {
          schedule.getChildren().addAll(processedChild.getChildren());
        } else {
          // Is child a sequential schedule?
          if (processedChild instanceof SequentialSchedule) {
            isComposedOfSequentialSchedule = true;
            // Is child a parallel actor schedule that can be parallelized out of it cluster?
          } else if ((processedChild instanceof ParallelHiearchicalSchedule) && (processedChild.getRepetition() == 1)
              && (processedChild.getChildren().size() == 1)) {
            // Register it as a parallel schedule in SCHEDULE list of children
            parallelSchedules.add(processedChild);
          }
          // Add processed children to SCHEDULE
          schedule.getChildren().add(processedChild);
        }
      }

      return generateTemporaryHierarchy(schedule, parallelSchedules, isComposedOfSequentialSchedule);

    }

    return schedule;
  }

  private Schedule generateTemporaryHierarchy(final Schedule schedule, final List<Schedule> parallelSchedules,
      final boolean isComposedOfSequentialSchedule) {
    // If parallelizable schedule are inside of a cluster that also regroup of sequential schedule,
    // we may pull up parallelizable schedule to the parent hierarchical schedule
    if ((schedule.getRepetition() > 1) && !parallelSchedules.isEmpty() && isComposedOfSequentialSchedule) {
      // This schedule is temporary : we use to carry children that we be insert in the parent hierarchy
      final SequentialHiearchicalSchedule temporaryHierarchy = ScheduleFactory.eINSTANCE
          .createSequentialHiearchicalSchedule();
      temporaryHierarchy.setRepetition(ScheduleParallelismOptimizer.TEMPORARY_HIERARCHY);

      // Retrieve parallel schedule that can be pulled up
      final List<Schedule> leftSchedules = new LinkedList<>();
      final List<Schedule> rightSchedules = new LinkedList<>();
      boolean insertRight = false;
      for (final Schedule child : schedule.getChildren()) {
        // If a child is not contained in parallel child schedule list, it may be a sequential schedule. If it is a
        // sequential schedule, every parallel child that we will find next will be pulled up at the right of original
        // schedule
        if (parallelSchedules.contains(child)) {
          if (insertRight) {
            rightSchedules.add(child);
          } else {
            leftSchedules.add(child);
          }
        } else {
          insertRight = true;
          // If we've found an another sequential schedule in children, we may clear our list of right parallel
          // element because of data dependency
          rightSchedules.clear();
        }
      }

      // Move identified parallel node to temporary hierarchy node
      temporaryHierarchy.getChildren().addAll(leftSchedules);
      temporaryHierarchy.getChildren().add(schedule);
      temporaryHierarchy.getChildren().addAll(rightSchedules);

      // Set repetition value for moved schedules
      final long repetitionSchedule = schedule.getRepetition();
      leftSchedules.stream().forEach(x -> x.getChildren().get(0).setRepetition(repetitionSchedule));
      rightSchedules.stream().forEach(x -> x.getChildren().get(0).setRepetition(repetitionSchedule));

      return temporaryHierarchy;
    }

    return schedule;
  }

}
