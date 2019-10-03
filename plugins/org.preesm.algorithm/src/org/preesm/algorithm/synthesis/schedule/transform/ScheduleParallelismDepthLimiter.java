/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.synthesis.schedule.transform;

import java.util.LinkedList;
import java.util.List;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.model.pisdf.AbstractActor;

/**
 * @author dgageot
 *
 *         Limit parallelism depth in schedule tree to a specified target
 *
 */
public class ScheduleParallelismDepthLimiter implements IScheduleTransform {

  long depthTarget;

  public ScheduleParallelismDepthLimiter(final long depthTarget) {
    this.depthTarget = depthTarget;
  }

  @Override
  public Schedule performTransform(final Schedule schedule) {
    return setParallelismDepth(schedule, 0);
  }

  private final Schedule setParallelismDepth(final Schedule schedule, long iterator) {

    // Parallel schedule? Increment iterator because of new parallel layer
    if (schedule instanceof ParallelSchedule) {
      iterator++;
    }

    // Explore and replace children
    if (schedule instanceof HierarchicalSchedule) {
      final HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      final List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(hierSchedule.getChildren());
      // Clear list of children schedule
      hierSchedule.getChildren().clear();
      for (final Schedule child : childSchedules) {
        hierSchedule.getChildren().add(setParallelismDepth(child, iterator));
      }
    }

    // Rework if parallel is below the depth target
    if ((schedule instanceof ParallelSchedule) && (iterator > this.depthTarget)) {
      if (schedule instanceof HierarchicalSchedule) {
        if (!schedule.hasAttachedActor()) {
          final Schedule childrenSchedule = schedule.getChildren().get(0);
          childrenSchedule.setRepetition(schedule.getRepetition());
          return childrenSchedule;
        } else {
          final SequentialHiearchicalSchedule sequenceSchedule = ScheduleFactory.eINSTANCE
              .createSequentialHiearchicalSchedule();
          sequenceSchedule.setAttachedActor(((HierarchicalSchedule) schedule).getAttachedActor());
          sequenceSchedule.setRepetition(schedule.getRepetition());
          sequenceSchedule.getChildren().addAll(schedule.getChildren());
          return sequenceSchedule;
        }
      } else if (schedule instanceof ActorSchedule) {
        final SequentialActorSchedule actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
        actorSchedule.setRepetition(schedule.getRepetition());

        final List<AbstractActor> actors = new ScheduleOrderManager(schedule).getSimpleOrderedList();
        actorSchedule.getActorList().addAll(actors);
        return actorSchedule;
      }
    }

    return schedule;
  }

}
