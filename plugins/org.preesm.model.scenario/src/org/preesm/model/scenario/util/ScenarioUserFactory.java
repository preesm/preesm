/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.model.scenario.util;

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.scenario.EnergyConfig;
import org.preesm.model.scenario.MemoryCopySpeedValue;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapiEventModifier;
import org.preesm.model.scenario.PapifyConfig;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioConstants;
import org.preesm.model.scenario.ScenarioFactory;

/**
 *
 */
public class ScenarioUserFactory {

  private ScenarioUserFactory() {
    // forbid instantiation
  }

  private static final ScenarioFactory factory = ScenarioFactory.eINSTANCE;

  /**
   *
   */
  public static final PapifyConfig createPapifyConfig() {
    final PapifyConfig res = factory.createPapifyConfig();
    res.setPapiData(factory.createPapiEventInfo());
    return res;
  }

  /**
  *
  */
  public static final EnergyConfig createEnergyConfig() {
    final EnergyConfig res = factory.createEnergyConfig();
    res.setPerformanceObjective(factory.createPerformanceObjective());
    return res;
  }

  /**
   *
   */
  public static final PapiEvent createTimingEvent() {
    final PapiEvent timingEvent = ScenarioFactory.eINSTANCE.createPapiEvent();
    timingEvent.setName("Timing");
    timingEvent.setDescription("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);
    final List<PapiEventModifier> modifTimingList = new ArrayList<>();
    timingEvent.getModifiers().addAll(modifTimingList);
    return timingEvent;
  }

  /**
   *
   */
  public static final Scenario createScenario() {
    final Scenario createScenario = factory.createScenario();
    createScenario.setConstraints(factory.createConstraints());
    createScenario.setPapifyConfig(createPapifyConfig());
    createScenario.setSimulationInfo(factory.createSimulationInfo());
    createScenario.setTimings(factory.createTimings());
    createScenario.setEnergyConfig(createEnergyConfig());
    return createScenario;
  }

  public static final MemoryCopySpeedValue createMemoryCopySpeedValue() {
    return createMemoryCopySpeedValue(ScenarioConstants.DEFAULT_MEMCOPY_SETUP_TIME.getValue(),
        1.0D / ScenarioConstants.DEFAULT_MEMCOPY_UNIT_PER_TIME.getValue());
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
