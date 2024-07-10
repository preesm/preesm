/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

/**
 * @author dgageot
 *
 */
public class ClusterScheduler {

  private ClusterScheduler() {
    // Forbid instantiation
  }

  /**
   * Schedule all clusters of input graph.
   *
   * @param inputGraph
   *          Input graph.
   * @return Map of Cluster Schedule.
   */
  public static Map<AbstractActor, Schedule> schedule(final PiGraph inputGraph, final Scenario scenario,
      final boolean optimizePerformance, final boolean parallelism) {
    // Build a map for Cluster Schedule
    final Map<AbstractActor, Schedule> schedulesMap = new HashMap<>();

    // Build a PGAN scheduler
    final PGANScheduler scheduler = new PGANScheduler(inputGraph, scenario, optimizePerformance, parallelism);

    // Compute a schedule for every cluster
    for (final AbstractActor actor : inputGraph.getAllActors()) {
      if (actor instanceof final PiGraph subgraph && subgraph.isCluster()) {
        registerClusterSchedule(schedulesMap, scheduler, inputGraph, subgraph);
      }
    }

    return schedulesMap;
  }

  private static void registerClusterSchedule(Map<AbstractActor, Schedule> scheduleMap, PGANScheduler scheduler,
      final PiGraph inputGraph, final PiGraph subgraph) {
    // Schedule subgraph
    final Schedule clusterSchedule = scheduler.scheduleCluster(subgraph);

    // Register the schedule in the map
    scheduleMap.put(subgraph, clusterSchedule);

  }

}
