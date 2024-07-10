/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2023 - 2024) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2023)
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

package org.preesm.algorithm.schedule.fpga;

import java.util.List;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.TimingType;

/**
 * This class introduces an explicit actor for delay initialization
 *
 * @author mdardail
 */

public class DelayActorTransform extends ActorConstructTransform {
  public static void transform(Scenario scenario, PiGraph flatGraph) {
    final List<Delay> delays = flatGraph.getAllDelays();
    for (final Delay delay : delays) {
      if (delay.getActor().hasValidRefinement()) {
        throw new PreesmRuntimeException("FPGA codegen doesn't support delay with initialization in " + delay.getId());
      }
      delayActorInstantiation(scenario, flatGraph, delay);
    }
  }

  private static void delayActorInstantiation(Scenario scenario, PiGraph flatGraph, final Delay delay) {
    final Actor delayActor = PiMMUserFactory.instance.createActor(delay.getId() + "_delayActor");
    createRefinement(delayActor, "delayActor<INIT_S, in_t, out_t>", "/delay_actor.hpp");
    flatGraph.addActor(delayActor);

    // Map delayActor to FPGA targeted by source of delay
    final ComponentInstance target = scenario.getPossibleMappings((AbstractActor) delay.getContainingFifo().getSource())
        .get(0);
    scenario.getConstraints().addConstraint(target, delayActor);

    // Add delayActor timing infos
    scenario.getTimings().setTiming(delayActor, target.getComponent(), TimingType.EXECUTION_TIME, "1");
    scenario.getTimings().setTiming(delayActor, target.getComponent(), TimingType.INITIATION_INTERVAL, "1");

    // Connect size parameters
    final Parameter initSize = PiMMUserFactory.instance
        .createParameter(delay.getContainingFifo().getId() + "_init_size", delay.getExpression().evaluate());
    connectParameter(delayActor, initSize, "INIT_S");

    // Create data ports
    final DataInputPort delayActorInput = createDataInputPort(delayActor, 1);
    final DataOutputPort delayActorOutput = createDataOutputPort(delayActor, 1);

    // Create and connect FIFO
    // Connect actor after delay to facilitate initialization during simulation
    final Fifo fifo = PiMMUserFactory.instance.createFifo(delayActorOutput, delay.getContainingFifo().getTargetPort(),
        delay.getContainingFifo().getType());
    flatGraph.addFifo(fifo);
    delay.getContainingFifo().setTargetPort(delayActorInput);
  }
}
