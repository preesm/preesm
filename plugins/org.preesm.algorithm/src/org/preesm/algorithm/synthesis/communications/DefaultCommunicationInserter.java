package org.preesm.algorithm.synthesis.communications;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.emf.common.util.ECollections;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamRoute;
import org.preesm.model.slam.SlamRouteStep;
import org.preesm.model.slam.route.SlamRoutingTable;

/**
 * Implementation of communication inserter. Visits all the actors in the schedule order and inserts communications upon
 * encountering the source of a Fifo. send/receive actors are inserted in the SequentialActorSchedule containing
 * respectively the source/target of the Fifo.
 *
 * This communication inserter keeps track of the visited actors per ComponentInstance, and therefore can insert the
 * communication nodes Receive is inserted right before the receive actor, and send is inserted at the peek (current
 * state of the visit) of the schedule containing the source.
 *
 * The route step forwards (send then receive) are inserted at the peek of the schedule containing the last visited
 * actor mapped on the proxy operator.
 *
 * @author anmorvan
 *
 */
public class DefaultCommunicationInserter implements ICommunicationInserter {

  /**
   * Tracks what is the last visited actor for each component instance. This is used to know where to insert the forward
   * communication actors, and on which actor schedule.
   *
   * Note: the algorithm inserts communication when visiting the target node. Since it is iterating on the actors in a
   * topology compliant order, the source actor has been visited when we visit the target. Also, since the source has
   * been visited, this Map entry for the component on which the source mapped is contained and not null.
   */
  protected final Map<ComponentInstance, AbstractActor> lastVisitedActor = new LinkedHashMap<>();

  @Override
  public void insertCommunications(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping) {
    PreesmLogger.getLogger().log(Level.FINER, "[COMINSERT] Communication insertion starting");

    // schedule manager used to query schedule and insert new com nodes.
    final ScheduleOrderManager scheduleOrderManager = new ScheduleOrderManager(schedule);

    final SlamRoutingTable routeTable = new SlamRoutingTable(slamDesign);

    // iterate over actors in scheduling (and topological) order
    final List<AbstractActor> scheduleOrderedList = scheduleOrderManager.buildScheduleAndTopologicalOrderedList();
    for (final AbstractActor sourceActor : scheduleOrderedList) {
      final List<ComponentInstance> sourceMappings = mapping.getMapping(sourceActor);
      if (sourceMappings.size() != 1) {
        // no supported
        throw new UnsupportedOperationException("Cannot insert communications for actors mapped on several operators");
      } else {
        insertActorOutputCommunications(mapping, scheduleOrderManager, routeTable, sourceActor, sourceMappings);
      }
    }

    PreesmLogger.getLogger().log(Level.FINER, "[COMINSERT] Communication insertion done");
  }

  private void insertActorOutputCommunications(final Mapping mapping, final ScheduleOrderManager scheduleOrderManager,
      final SlamRoutingTable routeTable, final AbstractActor sourceActor,
      final List<ComponentInstance> sourceMappings) {

    this.lastVisitedActor.put(sourceMappings.get(0), sourceActor);
    final List<Fifo> fifos = new ArrayList<>(sourceActor.getDataOutputPorts().size());
    for (final DataOutputPort dap : sourceActor.getDataOutputPorts()) {
      fifos.add(dap.getFifo());
    }
    for (final Fifo fifo : fifos) {
      PreesmLogger.getLogger().log(Level.FINER, "[COMINSERT] Handling fifo [" + fifo.getId() + "]");

      final DataInputPort targetPort = fifo.getTargetPort();
      final AbstractActor targetActor = targetPort.getContainingActor();

      final List<ComponentInstance> targetMappings = mapping.getMapping(targetActor);
      if (targetMappings.size() != 1) {
        // no supported
        throw new PreesmRuntimeException("Cannot insert communications for actors mapped on several operators");
      } else {
        final ComponentInstance tgtComponent = targetMappings.get(0);
        final ComponentInstance srcComponent = sourceMappings.get(0);

        if (srcComponent != tgtComponent) {
          // insert communication if operator is different only
          final SlamRoute route = routeTable.getRoute(srcComponent, tgtComponent);
          insertFifoCommunication(fifo, route, scheduleOrderManager, mapping);
        } else {
          PreesmLogger.getLogger().log(Level.FINER, "[COMINSERT]   >> mapped on same component - skipping");
        }
      }
    }
  }

  private void insertFifoCommunication(final Fifo fifo, final SlamRoute route,
      final ScheduleOrderManager scheduleOrderManager, final Mapping mapping) {
    for (final SlamRouteStep rstep : route.getRouteSteps()) {
      final ComponentInstance srcCmp = rstep.getSender();
      final ComponentInstance tgtCmp = rstep.getReceiver();

      // -- create sends
      final SendStartActor sendStart = ICommunicationInserter.createSendStart(fifo, rstep);
      final SendEndActor sendEnd = ICommunicationInserter.createSendEnd(fifo, rstep);

      // -- create receives
      final ReceiveStartActor receiveStart = ICommunicationInserter.createReceiveStart(fifo, rstep);
      final ReceiveEndActor receiveEnd = ICommunicationInserter.createReceiveEnd(fifo, rstep);

      // -- Associate com nodes
      receiveEnd.setReceiveStart(receiveStart);
      sendEnd.setSendStart(sendStart);
      sendStart.setTargetReceiveEnd(receiveEnd);
      receiveEnd.setSourceSendStart(sendStart);

      // -- Map communication nodes
      mapping.getMappings().put(sendStart, ECollections.newBasicEList(srcCmp));
      mapping.getMappings().put(sendEnd, ECollections.newBasicEList(srcCmp));
      mapping.getMappings().put(receiveStart, ECollections.newBasicEList(tgtCmp));
      mapping.getMappings().put(receiveEnd, ECollections.newBasicEList(tgtCmp));

      // -- insert
      final boolean isFirstRouteStep = srcCmp == route.getSource();
      insertSend(scheduleOrderManager, srcCmp, sendStart, sendEnd, isFirstRouteStep);
      this.lastVisitedActor.put(srcCmp, sendEnd);

      final boolean isLastRouteStep = tgtCmp == route.getTarget();
      insertReceive(scheduleOrderManager, mapping, tgtCmp, receiveStart, receiveEnd, isLastRouteStep);
      this.lastVisitedActor.put(tgtCmp, receiveEnd);
    }
  }

  private void insertReceive(final ScheduleOrderManager scheduleOrderManager, final Mapping mapping,
      final ComponentInstance tgtCmp, final ReceiveStartActor receiveStart, final ReceiveEndActor receiveEnd,
      final boolean isLastRouteStep) {
    final AbstractActor tgtCmpLastActor = this.lastVisitedActor.get(tgtCmp);
    if (isLastRouteStep) {
      if (tgtCmpLastActor == null) {
        // find appropriate schedule
        final List<AbstractActor> list = scheduleOrderManager.buildScheduleAndTopologicalOrderedList(mapping, tgtCmp);
        if (list.isEmpty()) {
          throw new UnsupportedOperationException(
              "Proxy send/receive using operator on which no actor is mapped is not supported");
        } else {
          final AbstractActor abstractActor = list.get(0);
          scheduleOrderManager.insertBefore(abstractActor, receiveStart, receiveEnd);
          PreesmLogger.getLogger().log(Level.FINER,
              "[COMINSERT]  * receive inserted before '" + abstractActor.getName() + "'");
        }
      } else {
        scheduleOrderManager.insertAfter(tgtCmpLastActor, receiveStart, receiveEnd);
        PreesmLogger.getLogger().log(Level.FINER,
            "[COMINSERT]  * receive inserted after '" + tgtCmpLastActor.getName() + "'");
      }
    } else {
      // TODO add proxy receive
      throw new UnsupportedOperationException("Proxy receive not supported yet");
    }
  }

  private void insertSend(final ScheduleOrderManager scheduleOrderManager, final ComponentInstance srcOperator,
      final SendStartActor sendStart, final SendEndActor sendEnd, final boolean isFirstRouteStep) {
    final AbstractActor srcOperatorPeekActor = this.lastVisitedActor.get(srcOperator);
    if (isFirstRouteStep) {
      if (srcOperatorPeekActor == null) {
        // should never happen since the source actor of the Fifo has been inserted in the map before reaching this
        // method. But well ...
        throw new PreesmRuntimeException("guru meditation");
      } else {
        // insert after srcCmpLastActor (the "peek" ui.e. last visited) actor for source oeprator
        scheduleOrderManager.insertAfter(srcOperatorPeekActor, sendStart, sendEnd);
        PreesmLogger.getLogger().log(Level.FINER,
            "[COMINSERT]  * send inserted after '" + srcOperatorPeekActor.getName() + "'");
        // update last visited list
      }
    } else {
      // TODO add proxy send
      throw new UnsupportedOperationException("Proxy send not supported yet");
    }
  }
}
