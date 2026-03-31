/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2025) :
 *
 * Ophelie-Renaud [ophelie.renaud@insa-rennes.fr] (2025)
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

package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.clustering.partitioner.ScapeMode;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.ClusteringPatternSeekerLoop;

/**
 * This class arranges the hierarchical levels for efficient routing. Level 0 is the top n++ for the subgraph below.
 */

public class HierarchicalRoute {
  private HierarchicalRoute() {

  }

  /**
   * Order the hierarchical subgraph in order to compute cluster in the bottom up way
   */
  public static Map<Long, List<PiGraph>> fillHierarchicalStructure(PiGraph graph) {
    final Map<Long, List<PiGraph>> hierarchicalLevelOrdered = new HashMap<>();
    for (final PiGraph g : graph.getAllChildrenGraphs()) {
      Long count = 0L;
      PiGraph tempg = g;
      while (tempg.getContainingPiGraph() != null) {
        tempg = tempg.getContainingPiGraph();
        count++;
      }
      final List<PiGraph> list = new ArrayList<>();
      list.add(g);
      if (hierarchicalLevelOrdered.get(count) == null) {
        hierarchicalLevelOrdered.put(count, list);
      } else {
        hierarchicalLevelOrdered.get(count).add(g);
      }

    }
    final List<PiGraph> list = new ArrayList<>();
    list.add(graph);
    hierarchicalLevelOrdered.put(0L, list);
    return hierarchicalLevelOrdered;
  }

  /**
   * Compute the hierarchical level to be coarsely clustered and identify hierarchical level to be cleverly clustered.
   *
   * @return levelBound level bound
   */

  public static Long computeClusterableLevel(PiGraph graph, ScapeMode scapeMode, int levelNumber,

      Map<Long, List<PiGraph>> hierarchicalLevelOrdered) {

    // TODO Debug this function

    final Long totalLevelNumber = (long) hierarchicalLevelOrdered.size();

    if (scapeMode == ScapeMode.DATA || scapeMode == ScapeMode.DATA_PIPELINE) {
      final String message = "Level configuration: 0-> full clustering, " + (totalLevelNumber + 1)
          + "-> nothing, between -> cluster partially";
      PreesmLogger.getLogger().log(Level.INFO, () -> message);
      // check if the value is in between 0 and totalLevelNumber +1
      if (levelNumber > totalLevelNumber + 1) {
        return totalLevelNumber + 1;
      }
      return (long) levelNumber;
    }

    Long count = totalLevelNumber - 1;
    // detect the highest delay
    for (final Fifo fd : graph.getFifosWithDelay()) {
      // detect loop --> no pipeline and contains hierarchical graph
      final List<AbstractActor> graphSingleLOOPs = new ClusteringPatternSeekerLoop(graph).singleLocalseek();
      if (!graphSingleLOOPs.isEmpty() && graphSingleLOOPs.stream().anyMatch(PiGraph.class::isInstance)) {
        // compute high
        for (Long i = 0L; i < totalLevelNumber; i++) {
          if (hierarchicalLevelOrdered.get(i).contains(fd.getContainingPiGraph())) {
            count = Math.min(count, i);
          }
        }

      }
    }

    return count + 1;
  }

}
