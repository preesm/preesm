/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Jonathan Piat [jpiat@laas.fr] (2008 - 2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2017 - 2018)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.mapper;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.energyAwareness.EnergyAwarenessHelper;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.pisdf.pimm2srdag.StaticPiMM2MapperDAGVisitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 */
@PreesmTask(id = "pisdf-mapper.list", name = "List Scheduling from PiSDF", category = "Schedulers",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) },

    outputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class), @Port(name = "ABC", type = LatencyAbc.class) },

    parameters = { @Parameter(name = "edgeSchedType", values = { @Value(name = "Simple") }),
        @Parameter(name = "simulatorType", values = { @Value(name = "LooselyTimed") }),
        @Parameter(name = "Check", values = { @Value(name = "True") }),
        @Parameter(name = "Optimize synchronization", values = { @Value(name = "False") }),
        @Parameter(name = "EnergyAwareness", values = { @Value(name = "False") }),
        @Parameter(name = "balanceLoads", values = { @Value(name = "false") })

    })
public class ListSchedulingMappingFromPiMM extends ListSchedulingMappingFromDAG {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    boolean usingEnergy = false;
    String messageLogger = "";

    if (parameters.containsKey("EnergyAwareness") && parameters.get("EnergyAwareness").equalsIgnoreCase("true")) {
      usingEnergy = true;
    }

    Map<String, Object> mapping = null;
    Map<String, Object> mappingFPS = new LinkedHashMap<>();
    Map<String, Object> mappingBest = new LinkedHashMap<>();

    if (usingEnergy) {
      messageLogger = "Energy-awareness enabled. This option will increase the mapping/scheduling "
          + "task so it may take a while";
      PreesmLogger.getLogger().log(Level.INFO, messageLogger);
      /**
       * Energy stuff
       */
      Map<String, Integer> bestConfig = new LinkedHashMap<>();
      double minEnergy = Double.MAX_VALUE;
      double energyNoObjective = Double.MAX_VALUE;
      double closestFPS = Double.MAX_VALUE;
      double objective = scenario.getEnergyConfig().getPerformanceObjective().getObjectiveEPS();
      double tolerance = scenario.getEnergyConfig().getPerformanceObjective().getToleranceEPS();
      double maxObjective = objective + (objective * tolerance / 100);
      double minObjective = objective - (objective * tolerance / 100);

      /**
       * Copy scenario
       */
      Scenario scenarioMapping = ScenarioUserFactory.createScenario();
      EnergyAwarenessHelper.copyScenario(scenario, scenarioMapping);
      inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenarioMapping);

      /**
       * Analyze the constraints and initialize the configs
       */
      Set<Map<String, Integer>> configsAlreadyUsed = new LinkedHashSet<>();
      Map<String, Integer> coresOfEachType = EnergyAwarenessHelper.getCoresOfEachType(scenarioMapping);
      Set<String> pesAlwaysAdded = EnergyAwarenessHelper.getImprescindiblePes(scenarioMapping);

      messageLogger = "PE instances that will always be added are = " + pesAlwaysAdded.toString();
      PreesmLogger.getLogger().log(Level.INFO, messageLogger);

      Map<String, Integer> coresUsedOfEachType = EnergyAwarenessHelper.getFirstConfig(coresOfEachType, "first");

      while (true) {
        /**
         * Reset --> Like this so as to keep group constraints order unaltered
         */
        scenarioMapping.getConstraints().getGroupConstraints().addAll(scenario.getConstraints().getGroupConstraints());
        scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());

        /**
         * Add the constraints that represents the new config
         */
        EnergyAwarenessHelper.updateConfigConstrains(scenario, scenarioMapping, pesAlwaysAdded, coresUsedOfEachType);
        EnergyAwarenessHelper.updateConfigSimu(scenario, scenarioMapping);
        Map<String, Integer> configToAdd = EnergyAwarenessHelper.getCoresOfEachType(scenarioMapping);

        /**
         * Check whether we have tested everything or not
         */
        if (!EnergyAwarenessHelper.configValid(configToAdd, configsAlreadyUsed)) {
          break;
        } else {
          configsAlreadyUsed.add(configToAdd);
        }

        /**
         * Try the mapping
         */
        messageLogger = "Using (coreType = NbPEs) combination = " + configToAdd.toString();
        PreesmLogger.getLogger().log(Level.INFO, messageLogger);
        final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenarioMapping);
        inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
        mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);

        /**
         * Check the energy
         */
        double powerPlatform = EnergyAwarenessHelper.computePlatformPower(configToAdd, scenarioMapping);
        MapperDAG dagMapping = (MapperDAG) mapping.get("DAG");
        double energyDynamic = EnergyAwarenessHelper.computeDynamicEnergy(dagMapping, scenarioMapping);

        LatencyAbc abcMapping = (LatencyAbc) mapping.get("ABC");
        // We consider that timing tab is filled with us (extracted with PAPIFY timing, for example)
        double fps = 1000000.0 / abcMapping.getFinalLatency();
        // We consider that energy tab is filled with uJ
        double totalDynamicEnergy = (energyDynamic / 1000000.0) * fps;
        double energyThisOne = powerPlatform + totalDynamicEnergy;
        messageLogger = configToAdd.toString() + " reaches " + fps + " FPS consuming " + energyThisOne
            + " joules per second";
        PreesmLogger.getLogger().log(Level.INFO, messageLogger);

        /**
         * Check if it is the best one
         */
        if (fps <= maxObjective && fps >= minObjective) {
          if (minEnergy > energyThisOne) {
            minEnergy = energyThisOne;
            closestFPS = fps;
            bestConfig.putAll(configToAdd);
            mappingBest.putAll(mapping);
          }
        } else if (Math.abs(objective - fps) < Math.abs(objective - closestFPS)) {
          closestFPS = fps;
          energyNoObjective = energyThisOne;
          bestConfig.putAll(configToAdd);
          mappingFPS.putAll(mapping);
        }
        /**
         * Compute the next configuration
         */
        if (fps < objective) {
          EnergyAwarenessHelper.getNextConditionalConfig(coresUsedOfEachType, coresOfEachType, "up",
              configsAlreadyUsed);
        } else {
          EnergyAwarenessHelper.getNextConditionalConfig(coresUsedOfEachType, coresOfEachType, "down",
              configsAlreadyUsed);
        }
      }
      /**
       * Reset --> Like this so as to keep group constraints order unaltered
       */
      scenarioMapping.getConstraints().getGroupConstraints().addAll(scenario.getConstraints().getGroupConstraints());
      scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());

      /**
       * Getting the best one
       */
      if (minEnergy == Double.MAX_VALUE) {
        minEnergy = energyNoObjective;
        mapping.putAll(mappingFPS);
      } else {
        mapping.putAll(mappingBest);
      }

      messageLogger = "The best one is " + bestConfig.toString() + ". Retrieving its result";
      PreesmLogger.getLogger().log(Level.INFO, messageLogger);
      messageLogger = "Performance reached =  " + closestFPS + " FPS with an energy consumption of " + minEnergy
          + " joules per second";
      PreesmLogger.getLogger().log(Level.INFO, messageLogger);
      /**
       * Fill scenario with everything again to avoid further problems
       */
      EnergyAwarenessHelper.copyScenario(scenarioMapping, scenario);
      inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    } else {
      final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenario);
      inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
      mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);
    }
    return mapping;
  }

}
