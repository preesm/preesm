package org.preesm.algorithm.synthesis.communications;

import java.util.List;
import java.util.stream.Stream;
import org.preesm.algorithm.mapper.abc.transaction.AddSendReceiveTransaction;
import org.preesm.algorithm.mapper.model.special.ReceiveVertex;
import org.preesm.algorithm.mapper.model.special.SendVertex;
import org.preesm.algorithm.mapper.tools.CommunicationOrderChecker;
import org.preesm.algorithm.mapping.model.Mapping;
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
      scheduleOrderManager.insertBefore(containingActor, receiveStart, receiveEnd);
    } else {
      super.insertReceive(scheduleOrderManager, mapping, routeStep, route, fifo, receiveStart, receiveEnd);
    }

    reorderReceiveVertices(scheduleOrderManager, mapping, routeStep.getSender(), routeStep.getReceiver(), receiveEnd);
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

    final List<AbstractActor> operatorActors = scheduleOrderManager.buildScheduleAndTopologicalOrderedList(mapping,
        receiverOperator);
    final List<AbstractActor> targetOperatorActors = scheduleOrderManager
        .buildScheduleAndTopologicalOrderedList(mapping, senderOperator);

    final int indexOfCurrentSend = targetOperatorActors.indexOf(currentSSA);
    final int indexOfCurrentReceive = operatorActors.indexOf(currentREA);

    final Stream<ReceiveEndActor> stream = operatorActors.stream()
        // keep receive vertices
        .filter(v -> v instanceof ReceiveEndActor).map(ReceiveEndActor.class::cast)
        // Keep only receiveVertex scheduled after the inserted one.
        .filter(v -> operatorActors.indexOf(v) > indexOfCurrentReceive)
        // Keep only those with the same sender operator
        .filter(v -> mapping.getMapping(v.getSourceSendStart()).size() == 1
            && mapping.getMapping(v.getSourceSendStart()).get(0) == senderOperator)
        // Keep only those whose sender is scheduled before the current one
        .filter(v -> {
          final SendStartActor vSend = v.getSourceSendStart();
          final int indexOfv = targetOperatorActors.indexOf(vSend);
          return indexOfv < indexOfCurrentSend;
        });

    stream.forEach(r -> {
      final ReceiveEndActor receiveEnd = r;
      final ReceiveStartActor receiveStart = r.getReceiveStart();

      scheduleOrderManager.remove(receiveEnd);
      scheduleOrderManager.remove(receiveStart);

      scheduleOrderManager.insertBefore(currentREA.getReceiveStart(), receiveStart, receiveEnd);
    });
  }
}
