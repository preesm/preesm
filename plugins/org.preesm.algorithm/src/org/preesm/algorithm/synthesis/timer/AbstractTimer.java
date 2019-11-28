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
package org.preesm.algorithm.synthesis.timer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * Abstract class that defines the structure for computing actor timings
 *
 * @author anmorvan
 *
 */
public abstract class AbstractTimer extends PiMMSwitch<Long> {

  protected final Schedule schedule;
  protected final PiGraph  pigraph;

  /**
   */
  public AbstractTimer(final PiGraph pigraph, final Schedule schedule) {
    this.pigraph = pigraph;
    this.schedule = schedule;
  }

  /**
   * Build a map that associate a timing (i.e. start/end/duration) for every actor in the schedule.
   */
  public Map<AbstractActor, ActorExecutionTiming> computeTimings() {
    final Map<AbstractActor, ActorExecutionTiming> res = new LinkedHashMap<>();
    final ScheduleOrderManager scheduleOrderManager = new ScheduleOrderManager(this.pigraph, this.schedule);
    final List<AbstractActor> orderedActors = scheduleOrderManager.buildScheduleAndTopologicalOrderedList();
    for (final AbstractActor actor : orderedActors) {
      final long duration = this.doSwitch(actor);

      final long startTime = scheduleOrderManager.getPredecessors(actor).stream()
          .mapToLong(a -> res.get(a).getEndTime()).max().orElse(0L);

      final ActorExecutionTiming executionTiming = new ActorExecutionTiming(actor, startTime, duration);
      res.put(actor, executionTiming);
    }
    return res;
  }

  /**
   * Special actors (i.e. communication actors) with dedicated timing switch.
   */
  @Override
  public Long defaultCase(final EObject object) {
    if (object instanceof CommunicationActor) {
      return new CommunicationTiming().doSwitch(object);
    }
    // Add more dedicated switches if required.
    throw new UnsupportedOperationException(
        "Could not compute timings for object of class [" + object.getClass() + "] : " + object);
  }

  /**
   * Special switch for communication actors introduced in the Schedule XCore model.
   */
  final class CommunicationTiming extends ScheduleSwitch<Long> {

    @Override
    public Long caseSendStartActor(final SendStartActor sendStartActor) {
      return computeSendStartActorTiming(sendStartActor);
    }

    @Override
    public Long caseSendEndActor(final SendEndActor sendEndActor) {
      return computeSendEndActorTiming(sendEndActor);
    }

    @Override
    public Long caseReceiveStartActor(final ReceiveStartActor receiveStartActor) {
      return computeReceiveStartActorTiming(receiveStartActor);
    }

    @Override
    public Long caseReceiveEndActor(final ReceiveEndActor receiveEndActor) {
      return computeReceiveEndActorTiming(receiveEndActor);
    }
  }

  @Override
  public Long caseActor(final Actor actor) {
    return computeActorTiming(actor);
  }

  @Override
  public Long caseInitActor(final InitActor initActor) {
    return computeInitActorTiming(initActor);
  }

  @Override
  public Long caseEndActor(final EndActor endActor) {
    return computeEndActorTiming(endActor);
  }

  @Override
  public Long caseForkActor(final ForkActor forkActor) {
    return computeForkActorTiming(forkActor);
  }

  @Override
  public Long caseJoinActor(final JoinActor joinActor) {
    return computeJoinActorTiming(joinActor);
  }

  @Override
  public Long caseBroadcastActor(final BroadcastActor broadcastActor) {
    return computeBroadcastActorTiming(broadcastActor);
  }

  @Override
  public Long caseRoundBufferActor(final RoundBufferActor roundbufferActor) {
    return computeRoundBufferActorTiming(roundbufferActor);
  }

  protected abstract long computeActorTiming(final Actor actor);

  protected abstract long computeForkActorTiming(final ForkActor forkActor);

  protected abstract long computeJoinActorTiming(final JoinActor joinActor);

  protected abstract long computeBroadcastActorTiming(final BroadcastActor broadcastActor);

  protected abstract long computeRoundBufferActorTiming(final RoundBufferActor roundbufferActor);

  protected abstract long computeInitActorTiming(final InitActor initActor);

  protected abstract long computeEndActorTiming(final EndActor endActor);

  protected abstract long computeSendStartActorTiming(final SendStartActor sendStartActor);

  protected abstract long computeSendEndActorTiming(final SendEndActor sendEndActor);

  protected abstract long computeReceiveStartActorTiming(final ReceiveStartActor receiveStartActor);

  protected abstract long computeReceiveEndActorTiming(final ReceiveEndActor receiveEndActor);

}
