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
 * @author koubi
 *
 */
public interface ICommunicationInserter {

  public List<CommunicationActor> insertCommunications(final PiGraph piGraph, final Design slamDesign,
      final Scenario scenario, final Schedule schedule, final Mapping mapping);

  /**
   */
  public static ReceiveEndActor createReceiveEnd(final Fifo fifo, final SlamRouteStep rstep) {
    final ReceiveEndActor receiveEnd = ScheduleFactory.eINSTANCE.createReceiveEndActor();
    receiveEnd.setFifo(fifo);
    receiveEnd.setName("receiveEnd_" + fifo.getSourcePort().getContainingActor().getName() + "_"
        + fifo.getTargetPort().getContainingActor().getName() + "_" + rstep.getReceiver().getInstanceName());
    receiveEnd.setRouteStep(rstep);
    return receiveEnd;
  }

  /**
   */
  public static ReceiveStartActor createReceiveStart(final Fifo fifo, final SlamRouteStep rstep) {
    final ReceiveStartActor receiveStart = ScheduleFactory.eINSTANCE.createReceiveStartActor();
    receiveStart.setFifo(fifo);
    receiveStart.setName("receiveStart_" + fifo.getSourcePort().getContainingActor().getName() + "_"
        + fifo.getTargetPort().getContainingActor().getName() + "_" + rstep.getReceiver().getInstanceName());
    receiveStart.setRouteStep(rstep);
    return receiveStart;
  }

  /**
   */
  public static SendEndActor createSendEnd(final Fifo fifo, final SlamRouteStep rstep) {
    final SendEndActor sendEnd = ScheduleFactory.eINSTANCE.createSendEndActor();
    sendEnd.setFifo(fifo);
    sendEnd.setName("sendEnd_" + fifo.getSourcePort().getContainingActor().getName() + "_"
        + fifo.getTargetPort().getContainingActor().getName() + "_" + rstep.getSender().getInstanceName());
    sendEnd.setRouteStep(rstep);
    return sendEnd;
  }

  /**
   */
  public static SendStartActor createSendStart(final Fifo fifo, final SlamRouteStep rstep) {
    final SendStartActor sendStart = ScheduleFactory.eINSTANCE.createSendStartActor();
    sendStart.setFifo(fifo);
    sendStart.setRouteStep(rstep);
    sendStart.setName("sendStart_" + fifo.getSourcePort().getContainingActor().getName() + "_"
        + fifo.getTargetPort().getContainingActor().getName() + "_" + rstep.getSender().getInstanceName());
    return sendStart;
  }
}
