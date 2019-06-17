package org.preesm.model.scenario.util;

import org.preesm.model.scenario.MemoryCopySpeedValue;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.ScenarioFactory;

/**
 *
 */
public class ScenarioUserFactory {

  private static final ScenarioFactory factory = ScenarioFactory.eINSTANCE;

  /**
   *
   */
  public static final Scenario createScenario() {
    final Scenario createScenario = factory.createScenario();
    createScenario.setConstraints(factory.createConstraints());
    createScenario.setPapifyConfig(factory.createPapifyConfig());
    createScenario.setSimulationInfo(factory.createSimulationInfo());
    createScenario.setTimings(factory.createTimings());
    return createScenario;
  }

  public static final MemoryCopySpeedValue createMemoryCopySpeedValue() {
    return createMemoryCopySpeedValue(ScenarioConstants.DEFAULT_MEMCOPY_SETUP_TIME.getValue(),
        ScenarioConstants.DEFAULT_MEMCOPY_TIME_PER_UNIT.getValue());
  }

  /**
   *
   */
  public static final MemoryCopySpeedValue createMemoryCopySpeedValue(final long initTime, final double timePerUnit) {
    final MemoryCopySpeedValue res = factory.createMemoryCopySpeedValue();
    res.setSetupTime(initTime);
    res.setTimePerUnit(timePerUnit);
    return res;
  }

}
