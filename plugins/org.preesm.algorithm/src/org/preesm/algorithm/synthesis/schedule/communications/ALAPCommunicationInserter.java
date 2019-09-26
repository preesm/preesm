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
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderedVisitor;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.algorithm.synthesis.schedule.iterator.ScheduleAndTopologyIterator;
import org.preesm.algorithm.synthesis.schedule.iterator.ScheduleIterator;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
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
 * encountering the target of a Fifo. send/receive actors are inserted in the SequentialActorSchedule containing
 * respectively the source/target of the Fifo. Receive is inserted right before the receive actor, and send is inserted
 * at the peek (current state of the visit) of the schedule containing the source.
 *
 * The route step forwards (send then receive) are inserted at the peek of the schedule containing the last visited
 * actor mapped on the proxy operator.
 *
 * Start/End communication actors are scheduled next to each other.
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
  protected final Map<ComponentInstance, AbstractActor> lastVisitedActor = new LinkedHashMap<>();

  protected void insertCommunication(final Fifo fifo, final SlamRoute route,
      Map<AbstractActor, ActorSchedule> actorToScheduleMap, final Mapping mapping) {

    for (final SlamRouteStep rstep : route.getRouteSteps()) {
      final ComponentInstance srcCmp = rstep.getSender();
      final ComponentInstance tgtCmp = rstep.getReceiver();

      final AbstractActor srcCmpLastActor = lastVisitedActor.get(srcCmp);
      final AbstractActor tgtCmpLastActor = lastVisitedActor.get(tgtCmp);

      if (srcCmpLastActor == null || tgtCmpLastActor == null) {
        throw new UnsupportedOperationException("Cannot use a proxy operator on which no actor has benn mapped");
      }

      final ActorSchedule srcActorSchedule = actorToScheduleMap.get(srcCmpLastActor);
      final ActorSchedule tgtActorSchedule = actorToScheduleMap.get(tgtCmpLastActor);

      // -- Insert sends
      final SendStartActor sendStart = ScheduleFactory.eINSTANCE.createSendStartActor();
      sendStart.setFifo(fifo);
      sendStart.setRouteStep(rstep);
      sendStart.setName("sendStart_" + srcCmpLastActor.getName() + "_" + fifo.getSourcePort().getName());

      final SendEndActor sendEnd = ScheduleFactory.eINSTANCE.createSendEndActor();
      sendEnd.setFifo(fifo);
      sendEnd.setName("sendEnd_" + srcCmpLastActor.getName() + "_" + fifo.getSourcePort().getName());
      sendEnd.setRouteStep(rstep);

      final EList<AbstractActor> srcActorList = srcActorSchedule.getActorList();
      CollectionUtil.insertAfter(srcActorList, srcCmpLastActor, sendStart, sendEnd);
      actorToScheduleMap.put(sendStart, srcActorSchedule);
      actorToScheduleMap.put(sendEnd, srcActorSchedule);
      mapping.getMappings().put(sendStart, ECollections.newBasicEList(srcCmp));
      mapping.getMappings().put(sendEnd, ECollections.newBasicEList(srcCmp));

      // -- Insert receives
      final ReceiveStartActor receiveStart = ScheduleFactory.eINSTANCE.createReceiveStartActor();
      receiveStart.setFifo(fifo);
      receiveStart.setName("receiveStart_" + tgtCmpLastActor.getName() + "_" + fifo.getTargetPort().getName());
      receiveStart.setRouteStep(rstep);

      final ReceiveEndActor receiveEnd = ScheduleFactory.eINSTANCE.createReceiveEndActor();
      receiveEnd.setFifo(fifo);
      receiveEnd.setName("receiveEnd_" + tgtCmpLastActor.getName() + "_" + fifo.getTargetPort().getName());
      receiveEnd.setReceiveStart(receiveStart);
      receiveEnd.setRouteStep(rstep);

      final EList<AbstractActor> tgtActorList = tgtActorSchedule.getActorList();
      CollectionUtil.insertBefore(tgtActorList, tgtCmpLastActor, receiveStart, receiveEnd);
      actorToScheduleMap.put(receiveStart, tgtActorSchedule);
      actorToScheduleMap.put(receiveEnd, tgtActorSchedule);
      mapping.getMappings().put(receiveStart, ECollections.newBasicEList(tgtCmp));
      mapping.getMappings().put(receiveEnd, ECollections.newBasicEList(tgtCmp));

      // -- Associate com nodes
      sendEnd.setSendStart(sendStart);
      sendStart.setTargetReceiveEnd(receiveEnd);
      receiveEnd.setSourceSendStart(sendStart);
    }
  }

  private void initLastVisitedActor(final Design slamDesign, final Schedule schedule, final Mapping mapping) {
    /**
     */
    class DoneException extends RuntimeException {
      private static final long serialVersionUID = 1L;
    }

    final List<ComponentInstance> cmps = new ArrayList<>(slamDesign.getOperatorComponentInstances());

    try {
      new ScheduleOrderedVisitor() {
        @Override
        public void visit(final AbstractActor actor) {
          final EList<ComponentInstance> actorMappings = mapping.getMapping(actor);
          if (actorMappings.size() != 1) {
            throw new UnsupportedOperationException();
          } else {
            final ComponentInstance componentInstance = actorMappings.get(0);
            if (cmps.contains(componentInstance)) {
              cmps.remove(componentInstance);
              lastVisitedActor.put(componentInstance, actor);
              if (cmps.isEmpty()) {
                throw new DoneException();
              }
            }
          }

        }
      }.doSwitch(schedule);
    } catch (DoneException e) {
      // nothing
    }
  }

  @Override
  public List<CommunicationActor> insertCommunications(final PiGraph piGraph, final Design slamDesign,
      final Scenario scenario, final Schedule schedule, final Mapping mapping) {

    final List<CommunicationActor> res = new ArrayList<>();

    // used to insert communications in the proper actor schedule, before/after the receiver/sender actor
    final Map<AbstractActor, ActorSchedule> actorToScheduleMap = ScheduleUtil.actorToScheduleMap(schedule);
    initLastVisitedActor(slamDesign, schedule, mapping);
    final SlamRoutingTable routeTable = new SlamRoutingTable(slamDesign);

    final ScheduleIterator t = new ScheduleAndTopologyIterator(schedule);
    final List<AbstractActor> orderedList = t.getOrderedList();

    for (final AbstractActor targetActor : orderedList) {
      final List<ComponentInstance> targetMappings = mapping.getMapping(targetActor);

      final List<DataInputPort> dataInputPorts = targetActor.getDataInputPorts();
      for (final DataInputPort dip : dataInputPorts) {
        final Fifo fifo = dip.getFifo();
        final DataOutputPort sourcePort = fifo.getSourcePort();
        final AbstractActor sourceActor = sourcePort.getContainingActor();

        final List<ComponentInstance> sourceMappings = mapping.getMapping(sourceActor);
        if (targetMappings.size() == 1 && sourceMappings.size() == 1) {
          final ComponentInstance tgtComponent = targetMappings.get(0);
          final ComponentInstance srcComponent = sourceMappings.get(0);
          this.lastVisitedActor.put(tgtComponent, targetActor);

          if (srcComponent != tgtComponent) {
            // insert communication if operator is different only
            final SlamRoute route = routeTable.getRoute(srcComponent, tgtComponent);
            insertCommunication(fifo, route, actorToScheduleMap, mapping);
          }
        } else {
          // no supported
          throw new PreesmRuntimeException("Cannot insert communications for actors mapped on several operators");
        }
      }
    }
    return res;
  }
}
