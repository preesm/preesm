package org.preesm.algorithm.synthesis.evaluation.energy;

import org.preesm.algorithm.mapper.energyawareness.EnergyAwarenessProvider;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.synthesis.evaluation.ISynthesisEvaluator;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * Compute the total energy consumed by the actors.
 * 
 * @author ahonorat
 */
public class SimpleEnergyEvaluation implements ISynthesisEvaluator<SimpleEnergyCost> {

  @Override
  public SimpleEnergyCost evaluate(PiGraph algo, Design slamDesign, Scenario scenario, Mapping mapping,
      ScheduleOrderManager scheduleOM) {

    final long value = EnergyAwarenessProvider.computeDynamicEnergy(mapping, scenario);

    return new SimpleEnergyCost(value);
  }

}
