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
import java.util.logging.Level;
import org.eclipse.emf.common.util.ECollections;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.algorithm.mapping.Mapping;
import org.preesm.model.algorithm.mapping.MappingFactory;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.HierarchicalSchedule;
import org.preesm.model.algorithm.schedule.ScheduleFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.utils.LexicographicComponentInstanceComparator;

/**
 *
 * @author anmorvan
 *
 */
public class SimpleScheduler extends AbstractScheduler {

  @Override
  protected SchedulerResult exec(final PiGraph piGraph, final Design slamDesign, final Scenario scenario) {

    final HierarchicalSchedule topParallelSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    final Mapping createMapping = MappingFactory.eINSTANCE.createMapping();
    final Map<ComponentInstance, ActorSchedule> cmpSchedules = new LinkedHashMap<>();

    final List<AbstractActor> allActors = piGraph.getAllActors();
    final List<AbstractActor> depthFirstSort = PiSDFTopologyHelper.sort(allActors);

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

    final int span = topParallelSchedule.getSpan();
    PreesmLogger.getLogger().log(Level.INFO, "span = " + span);

    return new SchedulerResult(createMapping, topParallelSchedule);
  }

}
