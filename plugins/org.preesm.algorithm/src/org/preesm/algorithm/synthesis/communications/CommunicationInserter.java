package org.preesm.algorithm.synthesis.communications;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapper.model.special.ReceiveVertex;
import org.preesm.algorithm.mapper.model.special.SendVertex;
import org.preesm.algorithm.mapper.tools.CommunicationOrderChecker;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
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
 * @author anmorvan
 *
 */
public class CommunicationInserter {

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
      final Map<AbstractActor, ActorSchedule> actorToScheduleMap, final Mapping mapping) {

    for (final SlamRouteStep rstep : route.getRouteSteps()) {
      // -- create sends
      final SendStartActor sendStart = CommunicationInserter.createSendStart(fifo, rstep);
      final SendEndActor sendEnd = CommunicationInserter.createSendEnd(fifo, rstep);

      // -- create receives
      final ReceiveStartActor receiveStart = CommunicationInserter.createReceiveStart(fifo, rstep);
      final ReceiveEndActor receiveEnd = CommunicationInserter.createReceiveEnd(fifo, rstep);

      // -- Associate com nodes
      receiveEnd.setReceiveStart(receiveStart);
      sendEnd.setSendStart(sendStart);
      sendStart.setTargetReceiveEnd(receiveEnd);
      receiveEnd.setSourceSendStart(sendStart);

      // -- insert
      final ComponentInstance srcCmp = rstep.getSender();
      final ComponentInstance tgtCmp = rstep.getReceiver();

      final AbstractActor srcCmpLastActor = this.lastVisitedActor.get(srcCmp);
      final AbstractActor tgtCmpLastActor = this.lastVisitedActor.get(tgtCmp);

      if ((srcCmpLastActor == null) || (tgtCmpLastActor == null)) {
        throw new UnsupportedOperationException("Cannot use a proxy operator on which no actor has benn mapped");
      }

      final ActorSchedule srcActorSchedule = actorToScheduleMap.get(srcCmpLastActor);
      final ActorSchedule tgtActorSchedule = actorToScheduleMap.get(tgtCmpLastActor);

      // Find insertion position. Insert sendVertices after the current source, and after sendVertex(es) immediately
      // following it. This is done to ensure that communications are inserted in increasing scheduling order.
      // TODO
      final EList<AbstractActor> srcActorList = srcActorSchedule.getActorList();
      CollectionUtil.insertAfter(srcActorList, srcCmpLastActor, sendStart, sendEnd);
      actorToScheduleMap.put(sendStart, srcActorSchedule);
      actorToScheduleMap.put(sendEnd, srcActorSchedule);
      mapping.getMappings().put(sendStart, ECollections.newBasicEList(srcCmp));
      mapping.getMappings().put(sendEnd, ECollections.newBasicEList(srcCmp));

      // Place the receive just before the vertex consuming the corresponding data. (position is not definitive, cf.
      // reorderReceive method)
      // TODO
      final EList<AbstractActor> tgtActorList = tgtActorSchedule.getActorList();
      CollectionUtil.insertBefore(tgtActorList, tgtCmpLastActor, receiveStart, receiveEnd);
      actorToScheduleMap.put(receiveStart, tgtActorSchedule);
      actorToScheduleMap.put(receiveEnd, tgtActorSchedule);
      mapping.getMappings().put(receiveStart, ECollections.newBasicEList(tgtCmp));
      mapping.getMappings().put(receiveEnd, ECollections.newBasicEList(tgtCmp));

      // Reorder receiveVertex if needed. This is done to ensure that send and receive operation between a pair of cores
      // are always in the same order.
      // TODO
    }
  }

  /**
   * The purpose of this method is to reschedule receive vertices of the receiverOperator to comply with constraints on
   * communication primitive order enforced by the {@link CommunicationOrderChecker}. <br>
   * <br>
   * Briefly, if there exists {@link ReceiveVertex}es scheduled after the current {@link #receiveVertex} on the
   * receiverOperator, (but associated to a {@link SendVertex}es scheduled before the current {@link #sendVertex} on the
   * senderOperator), then, these {@link ReceiveVertex}es must be rescheduled before the current {@link #receiveVertex}.
   *
   * @param senderOperator
   *          {@link ComponentInstance} instance on which the current {@link #sendVertex} was scheduled.
   * @param receiverOperator
   *          {@link ComponentInstance} instance on which the current {@link #receiveVertex} was scheduled.
   */
  private void reorderReceiveVertex(final ComponentInstance senderOperator, final ComponentInstance receiverOperator) {
    // TODO
  }

  private void initLastVisitedActor(final Design slamDesign, final Schedule schedule, final Mapping mapping) {
    /**
     */
    class DoneException extends RuntimeException {
      private static final long serialVersionUID = 1L;
    }

    final List<ComponentInstance> cmps = new ArrayList<>(slamDesign.getOperatorComponentInstances());

    try {
      final List<AbstractActor> scheduleAndTopologicalOrderedList = new ScheduleOrderManager(schedule)
          .getScheduleAndTopologicalOrderedList();
      for (final AbstractActor actor : scheduleAndTopologicalOrderedList) {
        final EList<ComponentInstance> actorMappings = mapping.getMapping(actor);
        if (actorMappings.size() != 1) {
          throw new UnsupportedOperationException();
        } else {
          final ComponentInstance componentInstance = actorMappings.get(0);
          if (cmps.contains(componentInstance)) {
            cmps.remove(componentInstance);
            CommunicationInserter.this.lastVisitedActor.put(componentInstance, actor);
            if (cmps.isEmpty()) {
              throw new DoneException();
            }
          }
        }
      }
    } catch (final DoneException e) {
      // nothing
    }
  }

  /**
   *
   */
  public List<CommunicationActor> insertCommunications(final PiGraph piGraph, final Design slamDesign,
      final Scenario scenario, final Schedule schedule, final Mapping mapping) {

    initLastVisitedActor(slamDesign, schedule, mapping);

    // Get edges in scheduling order of their producers
    final Iterator<
        AbstractActor> t = new ScheduleOrderManager(schedule).getScheduleAndTopologicalOrderedList().iterator();
    final List<Fifo> edgesInPrecedenceOrder = new ArrayList<>();

    while (t.hasNext()) {
      final AbstractActor vertex = t.next();
      edgesInPrecedenceOrder
          .addAll(vertex.getDataOutputPorts().stream().map(DataPort::getFifo).collect(Collectors.toList()));
    }

    final int dagEdgeCount = piGraph.getAllFifos().size();
    final int outEdgesCount = edgesInPrecedenceOrder.size();
    if (outEdgesCount != dagEdgeCount) {
      // If this happens, this means that not all edges are covered by the previous while loop.
      throw new PreesmRuntimeException("Some DAG edges are not covered. Input DAG has " + dagEdgeCount
          + " edges whereas there are " + outEdgesCount + " edges connected to vertices.");
    }

    final List<CommunicationActor> res = new ArrayList<>();

    // used to insert communications in the proper actor schedule, before/after the receiver/sender actor
    final Map<AbstractActor, ActorSchedule> actorToScheduleMap = ScheduleUtil.actorToScheduleMap(schedule);
    final SlamRoutingTable routeTable = new SlamRoutingTable(slamDesign);

    final Iterator<Fifo> iterator = edgesInPrecedenceOrder.iterator();
    while (iterator.hasNext()) {
      final Fifo fifo = iterator.next();

      final DataInputPort targetPort = fifo.getTargetPort();
      final AbstractActor targetActor = targetPort.getContainingActor();

      final DataOutputPort sourcePort = fifo.getSourcePort();
      final AbstractActor sourceActor = sourcePort.getContainingActor();

      final List<ComponentInstance> targetMappings = mapping.getMapping(targetActor);
      final List<ComponentInstance> sourceMappings = mapping.getMapping(sourceActor);
      if ((targetMappings.size() == 1) && (sourceMappings.size() == 1)) {
        final ComponentInstance tgtComponent = targetMappings.get(0);
        final ComponentInstance srcComponent = sourceMappings.get(0);
        this.lastVisitedActor.put(tgtComponent, targetActor);
        this.lastVisitedActor.put(srcComponent, sourceActor);

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
    return res;
  }

  /**
   */
  public static ReceiveEndActor createReceiveEnd(final Fifo fifo, final SlamRouteStep rstep) {
    final ReceiveEndActor receiveEnd = ScheduleFactory.eINSTANCE.createReceiveEndActor();
    receiveEnd.setFifo(fifo);
    receiveEnd.setName("receiveEnd_" + fifo.getSourcePort().getContainingActor() + "_"
        + fifo.getTargetPort().getContainingActor() + "_" + rstep.getReceiver().getInstanceName());
    receiveEnd.setRouteStep(rstep);
    return receiveEnd;
  }

  /**
   */
  public static ReceiveStartActor createReceiveStart(final Fifo fifo, final SlamRouteStep rstep) {
    final ReceiveStartActor receiveStart = ScheduleFactory.eINSTANCE.createReceiveStartActor();
    receiveStart.setFifo(fifo);
    receiveStart.setName("receiveStart_" + fifo.getSourcePort().getContainingActor() + "_"
        + fifo.getTargetPort().getContainingActor() + "_" + rstep.getReceiver().getInstanceName());
    receiveStart.setRouteStep(rstep);
    return receiveStart;
  }

  /**
   */
  public static SendEndActor createSendEnd(final Fifo fifo, final SlamRouteStep rstep) {
    final SendEndActor sendEnd = ScheduleFactory.eINSTANCE.createSendEndActor();
    sendEnd.setFifo(fifo);
    sendEnd.setName("sendEnd_" + fifo.getSourcePort().getContainingActor() + "_"
        + fifo.getTargetPort().getContainingActor() + "_" + rstep.getSender().getInstanceName());
    sendEnd.setRouteStep(rstep);
    return sendEnd;
  }

  /**
   */
  public static SendStartActor createSendStart(final Fifo fifo, final SlamRouteStep rstep) {
    final SendStartActor sendStart = ScheduleFactory.eINSTANCE.createSendStartActor();
    sendStart.setFifo(fifo);
    sendStart.setRouteStep(rstep);
    sendStart.setName("sendStart_" + fifo.getSourcePort().getContainingActor() + "_"
        + fifo.getTargetPort().getContainingActor() + "_" + rstep.getSender().getInstanceName());
    return sendStart;
  }
}
