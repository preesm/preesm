package org.preesm.algorithm.synthesis.schedule.communications;

import java.util.List;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamRouteStep;

/**
 *
 * @author anmorvan
 *
 */
public interface CommunicationInserter {

  /**
   * Insert communication actors in the schedule and mapping (but not in the PiGraph). Returns the list of inserted
   * nodes.
   */
  public List<CommunicationActor> insertCommunications(final PiGraph piGraph, final Design slamDesign,
      final Scenario scenario, final Schedule schedule, final Mapping mapping);

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
