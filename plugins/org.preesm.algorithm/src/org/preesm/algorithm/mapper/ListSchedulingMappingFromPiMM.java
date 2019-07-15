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
import java.util.Map;
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

    if (parameters.get("EnergyAwareness").equalsIgnoreCase("true")) {
      usingEnergy = true;
    }

    Map<String, Object> mapping = null;

    if (usingEnergy) {
      /**
       * Energy stuff
       */
      Map<String, Integer> bestConfig = new LinkedHashMap<>();
      double minEnergy = Double.MAX_VALUE;
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
      Map<String, Integer> coresOfEachType = EnergyAwarenessHelper.getCoresOfEachType(scenarioMapping);
      Map<String, Integer> coresUsedOfEachType = EnergyAwarenessHelper.getFirstConfig(coresOfEachType, "thorough");

      while (true) {
        /**
         * Reset
         */
        scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());

        /**
         * Add the constraints that represents the new config
         */
        System.out.println("Doing: " + coresUsedOfEachType.toString());
        EnergyAwarenessHelper.updateConfigConstrains(scenario, scenarioMapping, coresUsedOfEachType);
        EnergyAwarenessHelper.updateConfigSimu(scenario, scenarioMapping);

        /**
         * Try the mapping
         */
        final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenarioMapping);
        inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
        mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);

        /**
         * Check the energy
         */
        double powerPlatform = EnergyAwarenessHelper.computePlatformPower(coresUsedOfEachType, scenarioMapping);
        MapperDAG dagMapping = (MapperDAG) mapping.get("DAG");
        double energyDynamic = EnergyAwarenessHelper.computeDynamicEnergy(dagMapping, scenarioMapping);

        LatencyAbc abcMapping = (LatencyAbc) mapping.get("ABC");
        // We consider that timing tab is filled with us (extracted with PAPIFY timing, for example)
        double fps = 1000000.0 / abcMapping.getFinalLatency();
        // We consider that energy tab is filled with uJ
        double totalDynamicEnergy = (energyDynamic / 1000000.0) * fps;
        double energyThisOne = powerPlatform + totalDynamicEnergy;
        System.out.println("Total energy = " + energyThisOne + " --- FPS = " + fps);

        /**
         * Check if it is the best one
         */
        if (fps <= maxObjective && fps >= minObjective) {
          if (minEnergy > energyThisOne) {
            minEnergy = energyThisOne;
            closestFPS = fps;
            bestConfig.putAll(coresUsedOfEachType);
          }
        } else if (Math.abs(objective - fps) < Math.abs(objective - closestFPS)) {
          closestFPS = fps;
          minEnergy = energyThisOne;
          bestConfig.putAll(coresUsedOfEachType);
        }
        System.out.println("Best energy = " + minEnergy + " --- best FPS = " + closestFPS);
        /**
         * Compute the next configuration
         */
        EnergyAwarenessHelper.getNextConfig(coresUsedOfEachType, coresOfEachType, "thorough");

        /**
         * Check whether we have tested everything or not
         */
        if (!EnergyAwarenessHelper.configValid(coresUsedOfEachType)) {
          break;
        }
      }
      /**
       * Reset
       */
      scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());

      /**
       * Repeating for the best one
       */
      EnergyAwarenessHelper.updateConfigConstrains(scenario, scenarioMapping, bestConfig);
      EnergyAwarenessHelper.updateConfigSimu(scenario, scenarioMapping);

      System.out.println("Repeating for the best one");
      System.out.println("Doing: " + bestConfig.toString());
      System.out.println("Best energy = " + minEnergy + " --- best FPS = " + closestFPS);
      final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenarioMapping);
      inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
      mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);

      /**
       * Fill scenario with everything again to avoid further problems
       */
      scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());
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
