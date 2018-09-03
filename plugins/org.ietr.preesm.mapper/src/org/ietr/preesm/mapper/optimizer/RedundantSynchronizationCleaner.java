/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2018)
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
package org.ietr.preesm.mapper.optimizer;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 * The purpose of this class is to remove redundant synchronization created during the scheduling of an application.
 * <br>
 * <br>
 * A synchronization is a communication supported by a communication node with the "zero-copy" properties. If several
 * synchronization occur between a given pair of core, some of them may be redundant. That means they enforce a
 * synchronization which is already enforced by a previous synchronization, and can therefore be safely removed.
 *
 * @author kdesnos
 *
 */
public class RedundantSynchronizationCleaner {
  public static final String ZERO_COPY = "zero-copy";

  /**
   * Private empty constructor to prevent use of this class.
   */
  private RedundantSynchronizationCleaner() {
    // forbid instantiation
  }

  /**
   * Analyzes the communications in the {@link DirectedAcyclicGraph} and remove redundant synchronizations.
   *
   * @param dag
   *          The {@link DirectedAcyclicGraph} whose synchronizations are optimized. The DAG is modified during call to
   *          the function.
   */
  public static void cleanRedundantSynchronization(final DirectedAcyclicGraph dag) {

    final Set<DAGVertex> toBeRemoved = RedundantSynchronizationCleaner.proMethod(dag);
    toBeRemoved.forEach(transferVertex -> transferVertex.setPropertyValue("Redundant", Boolean.valueOf(true)));
    WorkflowLogger.getLogger().log(Level.INFO, "removing " + toBeRemoved.size() + " syncs");
  }

  private static Set<DAGVertex> proMethod(final DirectedAcyclicGraph dag) {
    final Set<DAGVertex> toBeRemoved = new LinkedHashSet<>();

    final ConsecutiveTransfersMap syncGroups = ConsecutiveTransfersMap.initFrom(dag);
    final SyncGraph syncGraph = SyncGraph.buildEdges(syncGroups);
    final SyncGraph redundantEdges = syncGraph.computeRedundantEdges();

    final Set<DAGVertex> redundantSyncVertices = RedundantSynchronizationCleaner.redundantSyncVertices(redundantEdges);
    final Set<DAGVertex> duplicateSyncVertices = RedundantSynchronizationCleaner.duplicateSyncVertices(syncGraph);

    toBeRemoved.addAll(duplicateSyncVertices);
    toBeRemoved.addAll(redundantSyncVertices);
    return toBeRemoved;
  }

  private static Set<DAGVertex> duplicateSyncVertices(SyncGraph syncGraph) {
    final Set<DAGVertex> toBeRemoved = new LinkedHashSet<>();
    final Set<String> edgeSet = syncGraph.edgeSet();
    for (final String edge : edgeSet) {
      final ConsecutiveTransfersGroup sourceGroup = syncGraph.getEdgeSource(edge);
      final ConsecutiveTransfersGroup targetGroup = syncGraph.getEdgeTarget(edge);

      boolean firstKept = false;
      for (final TransferVertex sourceVertex : sourceGroup) {
        final DAGVertex target = sourceVertex.outgoingEdges().iterator().next().getTarget();
        if (targetGroup.contains(target)) {
          if (!firstKept) {
            firstKept = true;
          } else {
            toBeRemoved.add(sourceVertex);
            toBeRemoved.add(target);
          }
        }
      }
    }
    return toBeRemoved;
  }

  private static Set<DAGVertex> redundantSyncVertices(final SyncGraph redundantPaths) {
    final Set<DAGVertex> toBeRemoved = new LinkedHashSet<>();
    final Set<String> edgeSet = redundantPaths.edgeSet();
    for (final String edge : edgeSet) {
      final ConsecutiveTransfersGroup edgeSource = redundantPaths.getEdgeSource(edge);
      final ConsecutiveTransfersGroup edgeTarget = redundantPaths.getEdgeTarget(edge);

      for (final TransferVertex sourceVertex : edgeSource) {
        final DAGVertex target = sourceVertex.outgoingEdges().iterator().next().getTarget();
        if (edgeTarget.contains(target)) {
          toBeRemoved.add(sourceVertex);
          toBeRemoved.add(target);
        }
      }
    }
    return toBeRemoved;
  }

}
