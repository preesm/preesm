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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.attributes.Parameter;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.mapper.AbstractMappingFromDAG;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.ietr.preesm.mapper.tools.TopologicalDAGIterator;

/**
 * The purpose of this class is to remove redundant synchronization created during the scheduling of an application. <br>
 * <br>
 * A synchronization is a communication supported by a communication node with the "zero-copy" properties. If several synchronization occur between a given pair
 * of core, some of them may be redundant. That means they enforce a synchronization which is already enforced by a previous synchronization, and can therefore
 * be safely removed.
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
  }

  /**
   * Analyzes the communications in the {@link DirectedAcyclicGraph} and remove redundant synchronizations.
   *
   * @param dag
   *          The {@link DirectedAcyclicGraph} whose synchronizations are optimized. The DAG is modified during call to the function.
   */
  public static void cleanRedundantSynchronization(final DirectedAcyclicGraph dag) {
    // Get the groups of synchronization.
    final Map<ComponentInstance, List<ConsecutiveTransferList>> syncGroups = RedundantSynchronizationCleaner.createSyncGroupsPerComponents(dag);

    // Build lookup map giving the components corresponding to each sync vertex
    final Map<TransferVertex, ComponentInstance> lookupSyncComponent = new LinkedHashMap<>();
    // Build lookup map giving the group of sync to which each sync vertex belongs
    final Map<TransferVertex, List<TransferVertex>> lookupSyncGroup = new LinkedHashMap<>();
    syncGroups.forEach((comp, list) -> list.forEach(list2 -> list2.forEach(vert -> {
      lookupSyncComponent.put(vert, comp);
      lookupSyncGroup.put(vert, list2);
    })));

    // Get the list of components
    final Set<ComponentInstance> components = syncGroups.keySet();

    // Scan the DAG to identify the precedence relationships enforced by each communication

    // Along the scan: For each Component, store the index of the last syncGroup encountered along the scan
    final Map<ComponentInstance, SyncIndex> lastSyncedPerComp = new LinkedHashMap<>();
    // init the map
    for (final ComponentInstance component : components) {
      lastSyncedPerComp.put(component, new SyncIndex(components));
    }

    // Along the scan: Index of sender registered to receiver vertex
    final Map<ReceiveVertex, SyncIndex> registeredSenderSyncIndex = new LinkedHashMap<>();

    // Along the scan: register the receive vertex of synchronization to Be Removed
    final Set<TransferVertex> toBeRemoved = new LinkedHashSet<>();

    final TopologicalDAGIterator iterDAGVertices = new TopologicalDAGIterator(dag); // Iterator on DAG vertices
    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();
      // Get component
      final ComponentInstance component = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      final List<TransferVertex> transfersForCurrentVertex = lookupSyncGroup.get(currentVertex);
      // When the beginning of a sync group is reached
      if ((currentVertex instanceof TransferVertex) && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex)
          && transfersForCurrentVertex.get(0).equals(currentVertex)) {
        // Increase self syncIndex
        lastSyncedPerComp.get(component).increment(component);
      }

      // When a send sync is encountered
      if ((currentVertex instanceof SendVertex) && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex)) {
        // Register the current syncIndexes to the receiver of this com.
        // there is only one outgoing edge for each sender vertex. Use it to retrieve the corresponding receivet
        final ReceiveVertex receiver = (ReceiveVertex) currentVertex.outgoingEdges().iterator().next().getTarget();
        registeredSenderSyncIndex.put(receiver, lastSyncedPerComp.get(component).clone());
      }

      // When the end of a sync group is reached
      if ((currentVertex instanceof TransferVertex) && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex)
          && transfersForCurrentVertex.get(transfersForCurrentVertex.size() - 1).equals(currentVertex)) {

        // Remove redundant sync from the group
        final SyncIndex coveredIdx = lastSyncedPerComp.get(component).clone();
        for (final TransferVertex syncVertex : transfersForCurrentVertex) {
          if (syncVertex instanceof ReceiveVertex) {
            // Is the receive vertex already covered (either by the currentSyncIndex of the core, OR by another receive of the group.
            if (registeredSenderSyncIndex.get(syncVertex).strictlySmallerOrEqual(coveredIdx)) {
              // If it is covered: it should be removed
              toBeRemoved.add(syncVertex);
              // Also add the corresponding SendVertex
              // there is only one incoming edge for each receive vertex. Use it to retrieve the corresponding sender
              toBeRemoved.add((TransferVertex) syncVertex.incomingEdges().iterator().next().getSource());
            } else {
              // It is not covered, keep it and update the list of covered idx
              coveredIdx.max(registeredSenderSyncIndex.get(syncVertex));
            }
          }
        }

        // Update the syncIndex of the component
        final List<TransferVertex> syncGroup = transfersForCurrentVertex;
        // Sanity check: self-index cannot be greater than current group index
        if (coveredIdx.syncIndexPerComponent.get(component) > syncGroups.get(component).indexOf(syncGroup)) {
          throw new WorkflowException("Problem in communication order. There seems to be a deadlock.");
        }

        // Do the update
        lastSyncedPerComp.put(component, coveredIdx);
      }
    }

    // Do the removal
    // dag.removeAllVertices(toBeRemoved);
    toBeRemoved.forEach(transferVertex -> transferVertex.setPropertyValue("Redundant", Boolean.valueOf(true)));

    RedundantSynchronizationCleaner.debugList(syncGroups);
  }

  /**
   * Create a {@link Map} that associates to each {@link ComponentInstance} a {@link List} of all its synchronization communication. Communications are stored
   * as a {@link List} of {@link List} where each nested {@link List} represents a group of consecutive synchronization primitive that is not "interrupted" by
   * any other computation.
   *
   * @param dag
   *          the scheduled {@link DirectedAcyclicGraph} from which communication are extracted
   * @return the described {@link Map}
   */
  private static Map<ComponentInstance, List<ConsecutiveTransferList>> createSyncGroupsPerComponents(final DirectedAcyclicGraph dag) {
    // This Map associates to each component of the architecture a List of all its communications.
    // Communications are stored as a List of list where each set represents a group of consecutive receive communication primitive that is not
    // "interrupted" by any other computation.
    final Map<ComponentInstance, List<ConsecutiveTransferList>> syncGroups = new LinkedHashMap<>();

    // Fill the syncGroups
    final TopologicalDAGIterator iterDAGVertices = new TopologicalDAGIterator(dag); // Iterator on DAG vertices
    // Store if the type of the last DAGVertex scheduled on each core (during the scan of the DAG) is sync or not (to identify groups)
    final Map<ComponentInstance, Boolean> lastVertexScheduled = new LinkedHashMap<>();
    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      // Get vertex type
      final boolean isCommunication = currentVertex instanceof TransferVertex;
      final boolean isSynchronization = isCommunication && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex);

      // Get component
      final ComponentInstance component = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      // If the currentVertex is a synchronization, store it in the comGroups
      if (isSynchronization) {
        // Get or create the appropriate sync group
        ConsecutiveTransferList syncGroup;
        if (!syncGroups.containsKey(component) || !lastVertexScheduled.get(component)) {
          // If the component still has no group OR if its last scheduled vertex is not a sync.
          // Create a new group
          syncGroup = new ConsecutiveTransferList();
        } else {
          // The component is associated with a group AND the last scheduled vertex was a communication.
          final List<ConsecutiveTransferList> componentSyncGroup = syncGroups.get(component);
          syncGroup = componentSyncGroup.get(componentSyncGroup.size() - 1);
        }
        syncGroup.add((TransferVertex) currentVertex);

        // Store the syncGroup (if needed) with the appropriate component.
        if (!syncGroups.containsKey(component)) {
          // Create first group of the component
          final List<ConsecutiveTransferList> componentList = new LinkedList<>();
          syncGroups.put(component, componentList);
          componentList.add(syncGroup);
        } else if (!lastVertexScheduled.get(component)) {
          // Add the new sync group to the component
          syncGroups.get(component).add(syncGroup);
        } // Else to both ifs, the vertex was directly added to an existing sync group in previous if statement.
      }

      // Save last vertex (replace previously saved type)
      lastVertexScheduled.put(component, isSynchronization);
    }
    return syncGroups;
  }

  /**
   * For debugging purposes only
   *
   * @param comGroups
   *          see previous function
   */
  private static void debugList(final Map<ComponentInstance, List<ConsecutiveTransferList>> comGroups) {
    for (final Entry<ComponentInstance, List<ConsecutiveTransferList>> e : comGroups.entrySet()) {
      final ComponentInstance componentInstance = e.getKey();
      final String componentName = componentInstance.getInstanceName();
      final StringBuilder sb = new StringBuilder(componentName + ": ");
      for (final List<TransferVertex> set : e.getValue()) {
        sb.append(set.size() + "-");
      }
      WorkflowLogger.getLogger().log(Level.INFO, "{0}", sb);
    }

  }

  /**
   * Check wether a given {@link TransferVertex} is supported only by communication steps going through zero-copy communication nodes in the architecture.
   *
   * @param currentVertex
   *          the {@link TransferVertex} whose communication steps parameters are being checked.
   * @return <code>true</code> if all communication steps are handled by zero copy communication nodes, <code>false</code> otherwise.
   */
  private static boolean isSynchronizationTransfer(final TransferVertex currentVertex) {
    boolean isSyncWait = true;
    // Check if all comm steps are zero copy.
    final List<ComponentInstance> communicationSteps = ((MessageRouteStep) currentVertex.getRouteStep()).getNodes();
    for (final ComponentInstance component : communicationSteps) {
      boolean isZeroCopy = false;
      for (final Parameter p : component.getParameters()) {
        isZeroCopy |= p.getKey().equals(RedundantSynchronizationCleaner.ZERO_COPY) && p.getValue().equals(AbstractMappingFromDAG.VALUE_TRUE);
      }
      isSyncWait &= isZeroCopy;
    }
    return isSyncWait;
  }
}
