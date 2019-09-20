package org.preesm.algorithm.synthesis.schedule.communications;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.algorithm.synthesis.schedule.iterator.ScheduleAndTopologyIterator;
import org.preesm.algorithm.synthesis.schedule.iterator.ScheduleIterator;
import org.preesm.commons.CollectionUtil;
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
 *
 * @author anmorvan
 *
 */
public class ALAPCommunicationInserter implements CommunicationInserter {

  /**
   * Tracks what is the last visited actor for each component instance. This is used to know where to insert the forward
   * communication actors, and on which actor schedule.
   *
   * Note: the algorithm inserts communication when visiting the target node. Since it is iterating on the actors in a
   * topology compliant order, the source actor has been visited when we visit the target. Also, since the source has
   * been visited, this Map entry for the component on which the source mapped is contained and not null.
   */
  private final Map<ComponentInstance, AbstractActor> lastVisitedActor = new LinkedHashMap<>();

  private void insertCommunication(final Fifo fifo, final SlamRoute route,
      Map<AbstractActor, ActorSchedule> actorToScheduleMap, final Mapping mapping) {

    for (final SlamRouteStep rstep : route.getRouteSteps()) {
      final ComponentInstance srcCmp = rstep.getSender();
      final ComponentInstance tgtCmp = rstep.getReceiver();

      // 1- create initial send
      final SendStartActor sendStart = ScheduleFactory.eINSTANCE.createSendStartActor();
      sendStart.setFifo(fifo);
      sendStart.setRouteStep(rstep);
      sendStart.setName("sendStart" + fifo.getId());

      final SendEndActor sendEnd = ScheduleFactory.eINSTANCE.createSendEndActor();
      sendEnd.setFifo(fifo);
      sendEnd.setName("sendEnd" + fifo.getId());
      sendEnd.setRouteStep(rstep);

      // insert send start/end in the schedule and map it on proper component
      final AbstractActor srcCmpLastActor = lastVisitedActor.get(srcCmp);
      final ActorSchedule srcActorSchedule = actorToScheduleMap.get(srcCmpLastActor);
      final EList<AbstractActor> srcActorList = srcActorSchedule.getActorList();
      CollectionUtil.insertAfter(srcActorList, srcCmpLastActor, sendStart, sendEnd);
      mapping.getMappings().put(sendStart, ECollections.newBasicEList(srcCmp));
      mapping.getMappings().put(sendEnd, ECollections.newBasicEList(srcCmp));

      // X- create final receive
      final ReceiveStartActor receiveStart = ScheduleFactory.eINSTANCE.createReceiveStartActor();
      receiveStart.setFifo(fifo);
      receiveStart.setName("receiveStart" + fifo.getId());
      receiveStart.setRouteStep(rstep);

      final ReceiveEndActor receiveEnd = ScheduleFactory.eINSTANCE.createReceiveEndActor();
      receiveEnd.setFifo(fifo);
      receiveEnd.setName("receiveEnd" + fifo.getId());
      receiveEnd.setReceiveStart(receiveStart);
      receiveEnd.setRouteStep(rstep);

      final AbstractActor tgtCmpLastActor = lastVisitedActor.get(srcCmp);
      final ActorSchedule tgtActorSchedule = actorToScheduleMap.get(tgtCmpLastActor);
      final EList<AbstractActor> tgtActorList = tgtActorSchedule.getActorList();
      CollectionUtil.insertBefore(tgtActorList, tgtCmpLastActor, receiveStart, receiveEnd);
      mapping.getMappings().put(receiveStart, ECollections.newBasicEList(tgtCmp));
      mapping.getMappings().put(receiveEnd, ECollections.newBasicEList(tgtCmp));

      // associate com nodes
      sendEnd.setSendStart(sendStart);
      sendStart.setTargetReceiveEnd(receiveEnd);
      receiveEnd.setSourceSendStart(sendStart);
    }

  }

  @Override
  public List<CommunicationActor> insertCommunications(final PiGraph piGraph, final Design slamDesign,
      final Scenario scenario, final Schedule schedule, final Mapping mapping) {

    final List<CommunicationActor> res = new ArrayList<>();

    // used to insert communications in the proper actor schedule, before/after the receiver/sender actor
    final Map<AbstractActor, ActorSchedule> actorToScheduleMap = ScheduleUtil.actorToScheduleMap(schedule);

    final SlamRoutingTable routeTable = new SlamRoutingTable(slamDesign);

    final ScheduleIterator t = new ScheduleAndTopologyIterator(schedule);
    final List<AbstractActor> orderedList = t.getOrderedList();

    for (final AbstractActor targetActor : orderedList) {
      final List<ComponentInstance> targetMappings = mapping.getMapping(targetActor);
      if (targetMappings.size() == 1) {
        final ComponentInstance tgtComponent = targetMappings.get(0);
        this.lastVisitedActor.put(tgtComponent, targetActor);

        final List<DataInputPort> dataInputPorts = targetActor.getDataInputPorts();
        for (final DataInputPort dip : dataInputPorts) {
          final Fifo fifo = dip.getFifo();
          final DataOutputPort sourcePort = fifo.getSourcePort();
          final AbstractActor sourceActor = sourcePort.getContainingActor();

          final List<ComponentInstance> sourceMappings = mapping.getMapping(sourceActor);
          if (sourceMappings.size() == 1) {
            final ComponentInstance srcComponent = sourceMappings.get(0);
            if (srcComponent != tgtComponent) {
              // insert communication if operator is different only
              final SlamRoute route = routeTable.getRoute(srcComponent, tgtComponent);
              insertCommunication(fifo, route, actorToScheduleMap, mapping);
            }
          } else {
            // no supported
          }
        }
      } else {
        // not supported
      }
    }
    return res;
  }
}
