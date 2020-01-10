/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.emf.common.util.ECollections;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.CommunicationActor;
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
 * communication nodes
 *
 * Receive is inserted right before the receive actor, and send is inserted at the peek (current state of the visit) of
 * the schedule containing the source.
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
  protected final Map<ComponentInstance, AbstractActor> lastVisitedActor;

  protected final ScheduleOrderManager scheduleOM;

  public DefaultCommunicationInserter(ScheduleOrderManager scheduleOM) {
    this.lastVisitedActor = new LinkedHashMap<>();
    this.scheduleOM = scheduleOM;
  }

  @Override
  public List<CommunicationActor> insertCommunications(final PiGraph piGraph, final Design slamDesign,
      final Scenario scenario, final Schedule schedule, final Mapping mapping) {
    PreesmLogger.getLogger().log(Level.FINER, "[COMINSERT] Communication insertion starting");

    final List<CommunicationActor> res = new ArrayList<>();
    final SlamRoutingTable routeTable = new SlamRoutingTable(slamDesign);

    PreesmLogger.getLogger().log(Level.INFO, "Build total ordering of tasks.");
    // iterate over actors in scheduling (and topological) order
    // we copy the whole list since it will be modified by the insertion of the communication.
    final List<
        AbstractActor> scheduleOrderedList = new ArrayList<>(scheduleOM.buildScheduleAndTopologicalOrderedList());

    PreesmLogger.getLogger().log(Level.INFO, "Insertion of communication tasks in the schedule.");
    for (final AbstractActor sourceActor : scheduleOrderedList) {
      final List<ComponentInstance> sourceMappings = mapping.getMapping(sourceActor);
      if (sourceMappings.size() != 1) {
        // no supported
        throw new UnsupportedOperationException("Cannot insert communications for actors mapped on several operators");
      } else {
        res.addAll(insertActorOutputCommunications(mapping, scheduleOM, routeTable, sourceActor, sourceMappings));
      }
    }

    PreesmLogger.getLogger().log(Level.FINER, "[COMINSERT] Communication insertion done");
    return res;
  }

  protected List<CommunicationActor> insertActorOutputCommunications(final Mapping mapping,
      final ScheduleOrderManager scheduleOrderManager, final SlamRoutingTable routeTable,
      final AbstractActor sourceActor, final List<ComponentInstance> sourceMappings) {

    final List<CommunicationActor> res = new ArrayList<>();

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
          res.addAll(insertFifoCommunication(fifo, route, scheduleOrderManager, mapping));
        } else {
          PreesmLogger.getLogger().log(Level.FINER, "[COMINSERT]   >> mapped on same component - skipping");
        }
      }
    }
    return res;
  }

  /**
   * Insert communication nodes for the fifo following the route.
   */
  protected List<CommunicationActor> insertFifoCommunication(final Fifo fifo, final SlamRoute route,
      final ScheduleOrderManager scheduleOrderManager, final Mapping mapping) {
    final List<CommunicationActor> res = new ArrayList<>();
    for (final SlamRouteStep rstep : route.getRouteSteps()) {
      final ComponentInstance sourceOperator = rstep.getSender();
      final ComponentInstance targetOperator = rstep.getReceiver();

      // -- create sends
      final SendStartActor sendStart = ICommunicationInserter.createSendStart(fifo, rstep);
      final SendEndActor sendEnd = ICommunicationInserter.createSendEnd(fifo, rstep);

      // -- create receives
      final ReceiveStartActor receiveStart = ICommunicationInserter.createReceiveStart(fifo, rstep);
      final ReceiveEndActor receiveEnd = ICommunicationInserter.createReceiveEnd(fifo, rstep);

      res.addAll(Arrays.asList(receiveStart, receiveEnd, sendStart, sendEnd));

      // -- Associate com nodes
      receiveEnd.setReceiveStart(receiveStart);
      sendEnd.setSendStart(sendStart);
      sendStart.setTargetReceiveEnd(receiveEnd);
      receiveEnd.setSourceSendStart(sendStart);

      // -- Map communication nodes
      mapping.getMappings().put(sendStart, ECollections.singletonEList(sourceOperator));
      mapping.getMappings().put(sendEnd, ECollections.singletonEList(sourceOperator));
      mapping.getMappings().put(receiveStart, ECollections.singletonEList(targetOperator));
      mapping.getMappings().put(receiveEnd, ECollections.singletonEList(targetOperator));

      // -- insert
      insertSend(scheduleOrderManager, mapping, rstep, route, fifo, sendStart, sendEnd);
      insertReceive(scheduleOrderManager, mapping, rstep, route, fifo, receiveStart, receiveEnd);
    }
    return res;
  }

  /**
   * Insert sendStart en sendEnd actors in the peek of the source operator's current schedule.
   */
  protected void insertSend(final ScheduleOrderManager scheduleOrderManager, final Mapping mapping,
      final SlamRouteStep routeStep, final SlamRoute route, final Fifo fifo, final SendStartActor sendStart,
      final SendEndActor sendEnd) {
    final ComponentInstance sourceOperator = routeStep.getSender();
    final boolean isFirstRouteStep = sourceOperator == route.getSource();

    final AbstractActor sourceOperatorPeekActor = this.lastVisitedActor.get(sourceOperator);
    if (isFirstRouteStep) {
      if (sourceOperatorPeekActor == null) {
        // should never happen since the source actor of the Fifo has been inserted in the map before reaching this
        // method. But well ...
        throw new PreesmRuntimeException("guru meditation");
      } else {
        // insert after srcCmpLastActor (the "peek" ui.e. last visited) actor for source operator
        scheduleOrderManager.insertComStEdAfterInSchedule(mapping, sourceOperatorPeekActor, sendStart, sendEnd, false);
        PreesmLogger.getLogger().log(Level.FINER,
            "[COMINSERT]  * send inserted after '" + sourceOperatorPeekActor.getName() + "'");
      }
    } else {
      // TODO add proxy send
      throw new UnsupportedOperationException("Proxy send not supported yet");
    }
    this.lastVisitedActor.put(sourceOperator, sendEnd);
  }

  /**
   * Insert receiveStart and receiveEnd actors in the peek of the target operator's current schedule. If no actor mapped
   * on this operator has been visited, finds the first operator mapped on it and insert receive actors before.
   *
   * TODO: If no actor is mapped on this operator, throws an exception. A new schedule could be created (see todo in the
   * method)
   */
  protected void insertReceive(final ScheduleOrderManager scheduleOrderManager, final Mapping mapping,
      final SlamRouteStep routeStep, final SlamRoute route, final Fifo fifo, final ReceiveStartActor receiveStart,
      final ReceiveEndActor receiveEnd) {
    final ComponentInstance targetOperator = routeStep.getReceiver();
    final boolean isLastRouteStep = targetOperator == route.getTarget();

    final AbstractActor targetOperatorPeekActor = this.lastVisitedActor.get(targetOperator);
    if (isLastRouteStep) {
      if (targetOperatorPeekActor == null) {
        // find appropriate schedule
        final List<
            AbstractActor> list = scheduleOrderManager.buildScheduleAndTopologicalOrderedList(mapping, targetOperator);
        if (list.isEmpty()) {
          // no actor is actually mapped on the target operator. Only happens when using proxy operator
          // TODO: Schedule trees usually have a parallel root schedule. Insert a new schedule child there and add the
          // receive nodes in it.
          throw new UnsupportedOperationException(
              "Proxy send/receive using operator on which no actor is mapped is not supported");
        } else {
          final AbstractActor abstractActor = list.get(0);
          scheduleOrderManager.insertComStEdBeforeInSchedule(mapping, abstractActor, receiveStart, receiveEnd, false);
          PreesmLogger.getLogger().log(Level.FINER,
              "[COMINSERT]  * receive inserted before '" + abstractActor.getName() + "'");
        }
      } else {
        scheduleOrderManager.insertComStEdAfterInSchedule(mapping, targetOperatorPeekActor, receiveStart, receiveEnd,
            false);
        PreesmLogger.getLogger().log(Level.FINER,
            "[COMINSERT]  * receive inserted after '" + targetOperatorPeekActor.getName() + "'");
      }
    } else {
      // TODO add proxy receive
      throw new UnsupportedOperationException("Proxy receive not supported yet");
    }
    this.lastVisitedActor.put(targetOperator, receiveEnd);
  }

}
