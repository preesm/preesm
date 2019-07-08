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
package org.preesm.algorithm.scheduler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.algorithm.mapping.Mapping;
import org.preesm.model.algorithm.mapping.MappingFactory;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.HierarchicalSchedule;
import org.preesm.model.algorithm.schedule.ScheduleFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.switches.PiSDFTopologicalSorter;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.utils.LexicographicComponentInstanceComparator;

/**
 *
 * @author dmadronal
 *
 */
public class EnergyAwareScheduler extends AbstractScheduler {

  protected void mappingAndScheduling(Mapping createMapping, HierarchicalSchedule topParallelSchedule, PiGraph piGraph,
      Scenario scenario, List<AbstractActor> depthFirstSort) {

    final Map<ComponentInstance, ActorSchedule> cmpSchedules = new LinkedHashMap<>();
    for (final AbstractActor orderedActor : depthFirstSort) {
      final ComponentInstance targetCmpIntance = scenario.getPossibleMappings(orderedActor).stream()
          .sorted(new LexicographicComponentInstanceComparator()).findFirst()
          .orElse(scenario.getSimulationInfo().getMainOperator());
      createMapping.getMappings().put(orderedActor, ECollections.singletonEList(targetCmpIntance));
      if (!cmpSchedules.containsKey(targetCmpIntance)) {
        final ActorSchedule createActorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
        cmpSchedules.put(targetCmpIntance, createActorSchedule);
        topParallelSchedule.getScheduleTree().add(createActorSchedule);
      }
      cmpSchedules.get(targetCmpIntance).getActors().add(orderedActor);
    }
  }

  @Override
  protected SchedulerResult exec(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {

    final HierarchicalSchedule topParallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    final Mapping createMapping = MappingFactory.eINSTANCE.createMapping();

    final List<AbstractActor> allActors = piGraph.getAllActors();
    final List<AbstractActor> depthFirstSort = PiSDFTopologicalSorter.depthFirstSort(allActors);
    Scenario scenarioMapping = ScenarioUserFactory.createScenario();

    Map<String, Integer> coresOfEachType = new LinkedHashMap<>();
    Map<String, Integer> coresUsedOfEachType = new LinkedHashMap<>();

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

      /**
       * Try the mapping
       */
      System.out.println("Doing: " + coresUsedOfEachType.toString());
      mappingAndScheduling(createMapping, topParallelSchedule, piGraph, scenarioMapping, depthFirstSort);
      for (Entry<AbstractActor, EList<ComponentInstance>> coreMapping : createMapping.getMappings()) {
        System.out.println(
            "actor = " + coreMapping.getKey().getVertexPath() + " mapped on " + coreMapping.getValue().toString());
      }

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
     * Fill scenario with everything again to avoid further problems
     */
    scenario.getConstraints().getGroupConstraints().addAll(scenarioMapping.getConstraints().getGroupConstraints());

    final int span = topParallelSchedule.getSpan();
    PreesmLogger.getLogger().log(Level.INFO, "span = " + span);
    for (Entry<AbstractActor, EList<ComponentInstance>> coreMapping : createMapping.getMappings()) {
      PreesmLogger.getLogger().log(Level.INFO,
          "actor = " + coreMapping.getKey().getVertexPath() + " mapped on " + coreMapping.getValue().toString());
    }

    return new SchedulerResult(createMapping, topParallelSchedule);
  }

}
