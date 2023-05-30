package org.preesm.algorithm.schedule.fpga;

import java.util.List;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
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
    final Parameter init_size = PiMMUserFactory.instance
        .createParameter(delay.getContainingFifo().getId() + "_init_size", delay.getExpression().evaluate());
    connectParameter(delayActor, init_size, "INIT_S");

    // Create data ports
    final DataInputPort delayActorInput = createDataInputPort(delayActor, 1);
    final DataOutputPort delayActorOutput = createDataOutputPort(delayActor, 1);

    // Create and connect FIFO
    // Connect actor after delay to facilitate initialization during simulation
    createFifo(delay.getContainingFifo().getType(), delayActorOutput, delay.getContainingFifo().getTargetPort());
    delay.getContainingFifo().setTargetPort(delayActorInput);
  }
}
