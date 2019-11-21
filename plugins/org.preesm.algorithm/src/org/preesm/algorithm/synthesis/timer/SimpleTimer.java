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
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
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
public class SimpleTimer extends AgnosticTimer {

  protected final Mapping mapping;

  /**
   * Compute WCET of actors, based on the scenario information.
   * 
   * @param scenario
   *          Scenario of the application.
   * @param mapping
   *          May be null, if so returned time is wcet.
   */
  public SimpleTimer(final Scenario scenario, final Mapping mapping) {
    super(scenario);
    this.mapping = mapping;
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

    long wcet = 0L;
    if (mapping != null) {
      final ComponentInstance operator = this.mapping.getSimpleMapping(userSpecialActor);
      final MemoryCopySpeedValue memTimings = this.scenario.getTimings().getMemTimings().get(operator.getComponent());
      wcet = (long) ((maxRate) * memTimings.getTimePerUnit()) + memTimings.getSetupTime();
    } else {
      for (final ComponentInstance operatorDefinitionID : scenario.getPossibleMappings(userSpecialActor)) {
        final MemoryCopySpeedValue memTimings = this.scenario.getTimings().getMemTimings()
            .get(operatorDefinitionID.getComponent());
        long et = (long) ((maxRate) * memTimings.getTimePerUnit()) + memTimings.getSetupTime();
        if (et > wcet) {
          wcet = et;
        }
      }
    }

    return wcet;
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

}
