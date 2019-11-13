/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.synthesis.communications;

import java.util.List;
import java.util.stream.Collectors;
import org.preesm.algorithm.mapper.abc.transaction.AddSendReceiveTransaction;
import org.preesm.algorithm.mapper.model.special.ReceiveVertex;
import org.preesm.algorithm.mapper.model.special.SendVertex;
import org.preesm.algorithm.mapper.tools.CommunicationOrderChecker;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.SlamRoute;
import org.preesm.model.slam.SlamRouteStep;

/**
 * Inspired from {@link AddSendReceiveTransaction}
 */
public class OptimizedCommunicationInserter extends DefaultCommunicationInserter {

  /**
   * On the last step, insert receive right before the fifo target, reorder if needed (see
   * {@link #reorderReceiveVertices}}
   */
  @Override
  protected void insertReceive(final ScheduleOrderManager scheduleOrderManager, final Mapping mapping,
      final SlamRouteStep routeStep, final SlamRoute route, final Fifo fifo, final ReceiveStartActor receiveStart,
      final ReceiveEndActor receiveEnd) {
    final ComponentInstance targetOperator = routeStep.getReceiver();
    final boolean isLastRouteStep = targetOperator == route.getTarget();
    if (isLastRouteStep) {
      final AbstractActor containingActor = fifo.getTargetPort().getContainingActor();
      scheduleOrderManager.insertComStEdBeforeInSchedule(mapping, containingActor, receiveStart, receiveEnd);
      reorderReceiveVertices(scheduleOrderManager, mapping, routeStep.getSender(), routeStep.getReceiver(), receiveEnd);
      // do not add call super.lastVisitedActor.put(targetOperator, sendEnd) since sendEnd is not added at the peek of
      // the current visit
    } else {
      // does not work if only relying on super
      super.insertReceive(scheduleOrderManager, mapping, routeStep, route, fifo, receiveStart, receiveEnd);
    }

  }

  /**
   * The purpose of this method is to reschedule {@link ReceiveVertex} of the receiverOperator to comply with
   * constraints on communication primitive order enforced by the {@link CommunicationOrderChecker}. <br>
   * <br>
   * Briefly, if there exists {@link ReceiveVertex}es scheduled after the current {@link #receiveVertex} on the
   * receiverOperator, (but associated to a {@link SendVertex}es scheduled before the current {@link #sendVertex} on the
   * senderOperator), then, these {@link ReceiveVertex}es must be rescheduled before the current {@link #receiveVertex}.
   */
  private void reorderReceiveVertices(final ScheduleOrderManager scheduleOrderManager, final Mapping mapping,
      final ComponentInstance senderOperator, final ComponentInstance receiverOperator,
      final ReceiveEndActor currentREA) {

    final SendStartActor currentSSA = currentREA.getSourceSendStart();

    final List<CommunicationActor> receiverOperatorActors = scheduleOrderManager
        .buildScheduleAndTopologicalOrderedComm(mapping, receiverOperator);
    final List<CommunicationActor> senderOperatorActors = scheduleOrderManager
        .buildScheduleAndTopologicalOrderedComm(mapping, senderOperator);

    final int indexOfCurrentSend = senderOperatorActors.indexOf(currentSSA);
    final int indexOfCurrentReceive = receiverOperatorActors.indexOf(currentREA);

    final List<CommunicationActor> afterCurrentREA = receiverOperatorActors.subList(indexOfCurrentReceive + 1,
        receiverOperatorActors.size());
    final List<CommunicationActor> beforeCurrentSSA = senderOperatorActors.subList(0, indexOfCurrentSend);

    final List<ReceiveEndActor> reActorsToReorder = afterCurrentREA.stream()
        // keep receive vertices
        .filter(v -> v instanceof ReceiveEndActor).map(ReceiveEndActor.class::cast)
        // Keep only receiveVertex scheduled after the inserted one.
        // Keep only those with the same sender operator
        // Keep only those whose sender is scheduled before the current one
        .filter(v -> beforeCurrentSSA.contains(v.getSourceSendStart())).collect(Collectors.toList());

    reActorsToReorder.forEach(v -> {
      final ReceiveEndActor receiveEnd = v;
      final ReceiveStartActor receiveStart = v.getReceiveStart();

      scheduleOrderManager.removeCom(mapping, receiveEnd);
      scheduleOrderManager.removeCom(mapping, receiveStart);

      scheduleOrderManager.insertComStEdBeforeInSchedule(mapping, currentREA.getReceiveStart(), receiveStart,
          receiveEnd);
    });
  }
}
