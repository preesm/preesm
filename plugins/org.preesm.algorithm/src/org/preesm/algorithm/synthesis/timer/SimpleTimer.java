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

import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.scenario.MemoryCopySpeedValue;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * Gives same results as the old loosely timed LatencyABC
 *
 * @author anmorvan
 *
 */
public class SimpleTimer extends AbstractTimer {

  protected final Scenario scenario;
  protected final Mapping  mapping;

  /**
   */
  public SimpleTimer(final PiGraph pigraph, final Mapping mapping, final Scenario scenario) {
    super(pigraph);
    this.mapping = mapping;
    this.scenario = scenario;
  }

  /**
   * Fork/Join/Broadcast/Roundbuffer
   */
  public long computeSpecialActorTiming(final UserSpecialActor userSpecialActor) {

    final long totalInRate = userSpecialActor.getDataInputPorts().stream()
        .mapToLong(port -> port.getPortRateExpression().evaluate()).sum();
    final long totalOutRate = userSpecialActor.getDataOutputPorts().stream()
        .mapToLong(port -> port.getPortRateExpression().evaluate()).sum();

    final long maxRate = Math.max(totalInRate, totalOutRate);

    final ComponentInstance operator = this.mapping.getSimpleMapping(userSpecialActor);
    final MemoryCopySpeedValue memTimings = this.scenario.getTimings().getMemTimings().get(operator.getComponent());

    final double timePerUnit = memTimings.getTimePerUnit();
    return (long) ((maxRate) * timePerUnit) + memTimings.getSetupTime();
  }

  @Override
  protected long computeActorTiming(final Actor actor) {
    final ComponentInstance operator = this.mapping.getSimpleMapping(actor);
    return this.scenario.getTimings().evaluateTimingOrDefault(actor, operator.getComponent());
  }

  @Override
  protected long computeForkActorTiming(final ForkActor forkActor) {
    return computeSpecialActorTiming(forkActor);
  }

  @Override
  protected long computeJoinActorTiming(final JoinActor joinActor) {
    return computeSpecialActorTiming(joinActor);
  }

  @Override
  protected long computeBroadcastActorTiming(final BroadcastActor broadcastActor) {
    return computeSpecialActorTiming(broadcastActor);
  }

  @Override
  protected long computeRoundBufferActorTiming(final RoundBufferActor roundbufferActor) {
    return computeSpecialActorTiming(roundbufferActor);
  }

  @Override
  protected long computeInitActorTiming(final InitActor initActor) {
    return 0L;
  }

  @Override
  protected long computeEndActorTiming(final EndActor endActor) {
    return 0L;
  }

  @Override
  protected long computeSendStartActorTiming(final SendStartActor sendStartActor) {
    return 0L;
  }

  @Override
  protected long computeSendEndActorTiming(final SendEndActor sendEndActor) {
    return 0L;
  }

  @Override
  protected long computeReceiveStartActorTiming(final ReceiveStartActor receiveStartActor) {
    return 0L;
  }

  @Override
  protected long computeReceiveEndActorTiming(final ReceiveEndActor receiveEndActor) {
    return 0L;
  }

}
