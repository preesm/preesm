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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.pisdf.pimm2srdag.StaticPiMM2MapperDAGVisitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.impl.ActorImpl;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
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
        @Parameter(name = "balanceLoads", values = { @Value(name = "false") })

    })
public class ListSchedulingMappingFromPiMM extends ListSchedulingMappingFromDAG {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    /**
     * Energy stuff
     */
    /**
     * Copy scenario
     */
    Scenario scenarioMapping = ScenarioUserFactory.createScenario();
    scenarioMapping.setAlgorithm(scenario.getAlgorithm());
    scenarioMapping.setDesign(scenario.getDesign());
    scenarioMapping.setTimings(scenario.getTimings());
    scenarioMapping.setEnergyConfig(scenario.getEnergyConfig());

    Map<String, Integer> coresOfEachType = new LinkedHashMap<>();
    Map<String, Integer> coresUsedOfEachType = new LinkedHashMap<>();

    Map<String, Integer> bestConfig = new LinkedHashMap<>();
    double minEnergy = Double.MAX_VALUE;
    double closestFPS = Double.MAX_VALUE;

    /**
     * Analyze the constraints and initialize the configs
     */
    for (Entry<ComponentInstance, EList<AbstractActor>> constraint : scenario.getConstraints().getGroupConstraints()) {
      String typeOfPe = constraint.getKey().getComponent().getVlnv().getName();
      if (!coresOfEachType.containsKey(typeOfPe)) {
        coresOfEachType.put(typeOfPe, 0);
        if (coresUsedOfEachType.isEmpty()) {
          coresUsedOfEachType.put(typeOfPe, 1);
        } else {
          coresUsedOfEachType.put(typeOfPe, 0);
        }
      }
      coresOfEachType.put(typeOfPe, coresOfEachType.get(typeOfPe) + 1);
    }
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenarioMapping);
    Map<String, Object> mapping = null;
    double objective = scenarioMapping.getEnergyConfig().getPerformanceObjective().getObjectiveEPS();
    double tolerance = scenarioMapping.getEnergyConfig().getPerformanceObjective().getToleranceEPS();
    double maxObjective = objective + (objective * tolerance);
    double minObjective = objective - (objective * tolerance);
    while (true) {
      /**
       * Reset
       */

      scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());

      /**
       * Add the constraints that represents the new config
       */
      for (Entry<String, Integer> instance : coresUsedOfEachType.entrySet()) {
        List<Entry<ComponentInstance, EList<AbstractActor>>> constraints = scenario.getConstraints()
            .getGroupConstraints().stream()
            .filter(e -> e.getKey().getComponent().getVlnv().getName().equals(instance.getKey()))
            .collect(Collectors.toList()).subList(0, instance.getValue());
        scenarioMapping.getConstraints().getGroupConstraints().addAll(constraints);
      }
      if (scenarioMapping.getConstraints().getGroupConstraints().stream()
          .filter(e -> e.getKey().getInstanceName().equals(scenario.getSimulationInfo().getMainOperator()))
          .count() == 0) {
        ComponentInstance newMainNode = scenarioMapping.getConstraints().getGroupConstraints().get(0).getKey();
        scenarioMapping.getSimulationInfo().setMainOperator(newMainNode);
      } else {
        scenarioMapping.getSimulationInfo().setMainOperator(scenario.getSimulationInfo().getMainOperator());
      }
      scenarioMapping.getSimulationInfo().setMainComNode(scenario.getSimulationInfo().getMainComNode());

      /**
       * Try the mapping
       */
      System.out.println("Doing: " + coresUsedOfEachType.toString());
      final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenarioMapping);
      inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
      mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);

      /**
       * Check the energy
       */
      double energyThisOne = scenarioMapping.getEnergyConfig().getPlatformPower().get("Base");
      double energyDynamic = 0.0;
      for (Entry<String, Integer> instance : coresUsedOfEachType.entrySet()) {
        double powerPe = scenarioMapping.getEnergyConfig().getPlatformPower().get(instance.getKey());
        energyThisOne = energyThisOne + (powerPe * instance.getValue());
      }
      MapperDAG dagMapping = (MapperDAG) mapping.get("DAG");
      LatencyAbc abcMapping = (LatencyAbc) mapping.get("ABC");
      for (DAGVertex vertex : dagMapping.getHierarchicalVertexSet()) {
        ComponentInstance componentInstance = vertex.getPropertyBean().getValue("Operator");
        Component component = componentInstance.getComponent();
        AbstractActor actor = vertex.getReferencePiVertex();
        if (actor != null && actor.getClass().equals(ActorImpl.class)) {
          double energyActor = scenarioMapping.getEnergyConfig().getEnergyActorOrDefault(actor, component);
          energyDynamic = energyDynamic + energyActor;
        }
      }

      double fps = 1000000.0 / abcMapping.getFinalLatency();
      double totalDynamicEnergy = (energyDynamic / 1000000.0) * fps;
      energyThisOne = energyThisOne + totalDynamicEnergy;
      System.out.println("Total energy = " + energyThisOne + " --- FPS = " + fps);
      /**
       * Check if it is the best one
       */
      if (fps <= maxObjective && fps >= minObjective) {
        if (minEnergy > energyThisOne) {
          minEnergy = energyThisOne;
          closestFPS = fps;
          for (Entry<String, Integer> config : coresUsedOfEachType.entrySet()) {
            bestConfig.put(config.getKey(), config.getValue());
          }
        }
      } else if (Math.abs(objective - fps) < Math.abs(closestFPS - fps)) {
        closestFPS = fps;
        for (Entry<String, Integer> config : coresUsedOfEachType.entrySet()) {
          bestConfig.put(config.getKey(), config.getValue());
        }
      }
      System.out.println("Best energy = " + minEnergy + " --- best FPS = " + closestFPS);
      /**
       * Compute the next configuration
       */
      for (Entry<String, Integer> peType : coresUsedOfEachType.entrySet()) {
        peType.setValue(peType.getValue() + 1);
        if (peType.getValue() > coresOfEachType.get(peType.getKey())) {
          peType.setValue(0);
        } else {
          break;
        }
      }
      /**
       * Check whether we have tested everything or not
       */
      if (coresUsedOfEachType.entrySet().stream().filter(e -> e.getValue() != 0).collect(Collectors.toList())
          .isEmpty()) {
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
    for (Entry<String, Integer> instance : bestConfig.entrySet()) {
      List<Entry<ComponentInstance, EList<AbstractActor>>> constraints = scenario.getConstraints().getGroupConstraints()
          .stream().filter(e -> e.getKey().getComponent().getVlnv().getName().equals(instance.getKey()))
          .collect(Collectors.toList()).subList(0, instance.getValue());
      scenarioMapping.getConstraints().getGroupConstraints().addAll(constraints);
    }
    if (scenarioMapping.getConstraints().getGroupConstraints().stream()
        .filter(e -> e.getKey().getInstanceName().equals(scenario.getSimulationInfo().getMainOperator()))
        .count() == 0) {
      ComponentInstance newMainNode = scenarioMapping.getConstraints().getGroupConstraints().get(0).getKey();
      scenarioMapping.getSimulationInfo().setMainOperator(newMainNode);
    } else {
      scenarioMapping.getSimulationInfo().setMainOperator(scenario.getSimulationInfo().getMainOperator());
    }
    scenarioMapping.getSimulationInfo().setMainComNode(scenario.getSimulationInfo().getMainComNode());

    System.out.println("Repeating for the best one");
    System.out.println("Doing: " + bestConfig.toString());
    final MapperDAG dag = StaticPiMM2MapperDAGVisitor.convert(algorithm, architecture, scenarioMapping);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, dag);
    mapping = super.execute(inputs, parameters, monitor, nodeName, workflow);

    /**
     * Fill scenario with everything again to avoid further problems
     */
    scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());
    scenario.setAlgorithm(scenarioMapping.getAlgorithm());
    scenario.setDesign(scenarioMapping.getDesign());
    scenario.setTimings(scenarioMapping.getTimings());
    scenario.setEnergyConfig(scenarioMapping.getEnergyConfig());
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    return mapping;
  }

}
