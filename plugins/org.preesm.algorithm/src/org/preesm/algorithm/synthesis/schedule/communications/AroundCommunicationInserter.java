package org.preesm.algorithm.synthesis.schedule.communications;

import java.util.Map;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.commons.CollectionUtil;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.SlamRoute;
import org.preesm.model.slam.SlamRouteStep;

/**
 * Specialization of the ALAP com inserter: the initial send is inserted right after the fifo source actor.
 *
 * @author anmorvan
 *
 */
public class AroundCommunicationInserter extends ALAPCommunicationInserter {

  @Override

  protected void insertCommunication(final Fifo fifo, final SlamRoute route,
      final Map<AbstractActor, ActorSchedule> actorToScheduleMap, final Mapping mapping) {

    for (final SlamRouteStep rstep : route.getRouteSteps()) {
      final ComponentInstance srcCmp = rstep.getSender();
      final ComponentInstance tgtCmp = rstep.getReceiver();

      final AbstractActor srcCmpLastActor = this.lastVisitedActor.get(srcCmp);
      final AbstractActor tgtCmpLastActor = this.lastVisitedActor.get(tgtCmp);

      if ((srcCmpLastActor == null) || (tgtCmpLastActor == null)) {
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
      if (route.getSource() == rstep.getSender()) {
        CollectionUtil.insertAfter(srcActorList, fifo.getSourcePort().getContainingActor(), sendStart, sendEnd);
      } else {
        CollectionUtil.insertAfter(srcActorList, srcCmpLastActor, sendStart, sendEnd);
      }
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
}
