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
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 *         Perform a flattening of schedule tree (uniformize repetition on sequential hierarchical schedule)
 *
 */
public class ScheduleFlattener implements IScheduleTransform {

  @Override
  public Schedule performTransform(final Schedule schedule) {

    // If it is an hierarchical schedule, explore and cluster actors
    if (schedule instanceof final HierarchicalSchedule hierSchedule && schedule.hasAttachedActor()) {

      // Retrieve children schedule and actors
      final List<Schedule> childSchedules = new LinkedList<>(hierSchedule.getChildren());

      // Clear list of children schedule
      hierSchedule.getChildren().clear();

      for (final Schedule child : childSchedules) {

        final Schedule processedChild = performTransform(child);

        // Sequential flattening
        if ((hierSchedule instanceof SequentialHiearchicalSchedule) && (child instanceof SequentialHiearchicalSchedule)
            && (child.getRepetition() == 1)) {
          hierSchedule.getChildren().addAll(processedChild.getChildren());

          final PiGraph flattenedActor = (PiGraph) hierSchedule.getAttachedActor();
          if (processedChild instanceof final HierarchicalSchedule childHierSched
              && flattenedActor == childHierSched.getAttachedActor().getContainingGraph()) {
            moveUp(flattenedActor, (PiGraph) childHierSched.getAttachedActor());

          }
          hierSchedule.setAttachedActor(flattenedActor);

          // Parallel flattening
        } else if ((hierSchedule instanceof ParallelHiearchicalSchedule)
            && (child instanceof ParallelHiearchicalSchedule) && (child.getRepetition() == 1)) {
          hierSchedule.getChildren().addAll(processedChild.getChildren());

        } else {
          hierSchedule.getChildren().add(processedChild);
        }
      }
    }

    return schedule;
  }

  private static void moveUp(PiGraph top, PiGraph sub) {
    for (final AbstractActor a : sub.getActors()) {
      top.addActor(a);
    }
    for (final DataInterface di : sub.getDataInterfaces()) {
      final Fifo outFifo = di.getGraphPort().getFifo();
      DataPort inPort = null;
      if (di instanceof final DataInputInterface dii) {
        inPort = dii.getDataPort().getFifo().getTargetPort();
        if (inPort.getContainingActor() instanceof DataInterface) {
          throw new PreesmRuntimeException("can't handle that for the moment");
        }
        outFifo.setTargetPort((DataInputPort) inPort);
      }
      if (di instanceof final DataOutputInterface doi) {
        inPort = doi.getDataPort().getFifo().getSourcePort();
        if (inPort.getContainingActor() instanceof DataInterface) {
          throw new PreesmRuntimeException("can't handle that for the moment");
        }
        outFifo.setSourcePort((DataOutputPort) inPort);
      }
    }
    top.removeActor(sub);
  }
}
