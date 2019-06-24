package org.preesm.algorithm.scheduler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.ECollections;
import org.preesm.model.algorithm.mapping.Mapping;
import org.preesm.model.algorithm.mapping.MappingFactory;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.ParallelSchedule;
import org.preesm.model.algorithm.schedule.ScheduleFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.switches.PiSDFTopologicalSorter;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 *
 * @author anmorvan
 *
 */
public class SimpleScheduler extends AbstractScheduler {

  @Override
  protected SchedulerResult exec(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {

    final ParallelSchedule topParallelSchedule = ScheduleFactory.eINSTANCE.createParallelSchedule();
    final Mapping createMapping = MappingFactory.eINSTANCE.createMapping();
    final Map<ComponentInstance, ActorSchedule> cmpSchedules = new LinkedHashMap<>();

    final List<AbstractActor> allActors = piGraph.getAllActors();
    final List<AbstractActor> depthFirstSort = PiSDFTopologicalSorter.depthFirstSort(allActors);

    for (final AbstractActor orderedActor : depthFirstSort) {
      final ComponentInstance targetCmpIntance = scenario.getPossibleMappings(orderedActor).stream().findFirst()
          .orElse(scenario.getSimulationInfo().getMainOperator());
      createMapping.getMappings().put(orderedActor, ECollections.singletonEList(targetCmpIntance));

      if (!cmpSchedules.containsKey(targetCmpIntance)) {
        final ActorSchedule createActorSchedule = ScheduleFactory.eINSTANCE.createActorSchedule();
        cmpSchedules.put(targetCmpIntance, createActorSchedule);
        topParallelSchedule.getScheduleTree().add(createActorSchedule);
      }
      cmpSchedules.get(targetCmpIntance).getActors().add(orderedActor);
    }

    return new SchedulerResult(createMapping, topParallelSchedule);
  }

}
