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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.ietr.dftools.algorithm.iterators.DAGIterator;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.attributes.Parameter;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 * The purpose of this class is to remove redundant synchronization created during the scheduling of an application. <br>
 * <br>
 * A synchronization is a communication supported by a communication node with the "zero-copy" properties. If several synchronization occur between a give pair
 * of core, come of them may be redundant, which means that they enforce a synchronization which is already enforced by a previous synchronization.
 * 
 * @author kdesnos
 *
 */
public class RedundantSynchronizationCleaner {
  public static final String TRUE      = "true";
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
  public static void cleanRedundantSynchronization(DirectedAcyclicGraph dag) {
    // Get the groups of synchronization.
    Map<ComponentInstance, LinkedList<LinkedList<TransferVertex>>> syncGroups = createSyncGroupsPerComponents(dag);

    // Build lookup map giving the components corresponding to each sync vertex
    Map<TransferVertex, ComponentInstance> lookupSyncComponent = new HashMap<>();
    // Build lookup map giving the group of sync to which each sync vertex belongs
    Map<TransferVertex, LinkedList<TransferVertex>> lookupSyncGroup = new HashMap<>();
    syncGroups.forEach((comp, list) -> {
      list.forEach(list2 -> {
        list2.forEach(vert -> {
          lookupSyncComponent.put(vert, comp);
          lookupSyncGroup.put(vert, list2);
        });
      });
    });

    // Get the list of components
    Set<ComponentInstance> components = syncGroups.keySet();

    // Scan the DAG to identify the precedence relationships enforced by each communication

    // Along the scan: For each Component, store the index of the last syncGroup encountered along the scan
    Map<ComponentInstance, SyncIndex> lastSyncedPerComp = new HashMap<>();
    // init the map
    for (ComponentInstance component : components) {
      lastSyncedPerComp.put(component, new SyncIndex(components));
    }

    // Along the scan: Index of sender registered to receiver vertex
    Map<ReceiveVertex, SyncIndex> registeredSenderSyncIndex = new HashMap<>();

    // Along the scan: register the receive vertex of synchronization to Be Removed
    Set<ReceiveVertex> toBeRemoved = new LinkedHashSet<>();

    final DAGIterator iterDAGVertices = new DAGIterator(dag); // Iterator on DAG vertices
    while (iterDAGVertices.hasNext()) {
      DAGVertex currentVertex = iterDAGVertices.next();
      // Get component
      final ComponentInstance component = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      // When the beginning of a sync group is reached
      if (currentVertex instanceof TransferVertex && isSynchronizationTransfer((TransferVertex) currentVertex)
          && lookupSyncGroup.get(currentVertex).getFirst().equals(currentVertex)) {
        // Increase self syncIndex
        lastSyncedPerComp.get(component).increment(component);
      }

      // When a send sync is encountered
      if (currentVertex instanceof SendVertex && isSynchronizationTransfer((TransferVertex) currentVertex)) {
        // Register the current syncIndexes to the receiver of this com.
        // there is only one outgoing edge for each sender vertex. Use it to retrieve the corresponding receivet
        ReceiveVertex receiver = (ReceiveVertex) currentVertex.outgoingEdges().iterator().next().getTarget();
        registeredSenderSyncIndex.put(receiver, lastSyncedPerComp.get(component).clone());
      }

      // When the end of a sync group is reached
      if (currentVertex instanceof TransferVertex && isSynchronizationTransfer((TransferVertex) currentVertex)
          && lookupSyncGroup.get(currentVertex).getLast().equals(currentVertex)) {

        // Remove redundant sync from the group
        SyncIndex coveredIdx = lastSyncedPerComp.get(component).clone();
        for (TransferVertex syncVertex : lookupSyncGroup.get(currentVertex)) {
          if (syncVertex instanceof ReceiveVertex) {
            // Is the receive vertex already covered (either by the currentSyncIndex of the core, OR by another receive of the group.
            if (registeredSenderSyncIndex.get(syncVertex).strictlySmallerOrEqual(coveredIdx)) {
              // If it is covered: it should be removed
              toBeRemoved.add((ReceiveVertex) syncVertex);
            } else {
              // It is not covered, keep it and update the list of covered idx
              coveredIdx.max(registeredSenderSyncIndex.get(syncVertex));
            }
          }
        }

        // Update the syncIndex of the component
        LinkedList<TransferVertex> syncGroup = lookupSyncGroup.get(currentVertex);
        // Sanity check: self-index cannot be greater than current group index
        if (coveredIdx.syncIndexPerComponent.get(component) > syncGroups.get(component).indexOf(syncGroup)) {
          throw new WorkflowException("Problem in communication order. There seems to be a deadlock.");
        }

        // Do the update
        lastSyncedPerComp.put(component, coveredIdx);
      }
    }

    debugList(syncGroups);
  }

  /**
   * Create a {@link Map} that associates to each {@link ComponentInstance} a {@link LinkedList} of all its synchronization communication. Communications are
   * stored as a {@link LinkedList} of {@link LinkedList} where each nested {@link LinkedList} represents a group of consecutive synchronization primitive that
   * is not "interrupted" by any other computation.
   * 
   * @param dag
   *          the scheduled {@link DirectedAcyclicGraph} from which communication are extracted
   * @return the described {@link Map}
   */
  private static Map<ComponentInstance, LinkedList<LinkedList<TransferVertex>>> createSyncGroupsPerComponents(DirectedAcyclicGraph dag) {
    // This Map associates to each component a LinkedList of all its communications.
    // Communications are stored as a LinkedList of list where each set represents a group of consecutive receive communication primitive that is not
    // "interrupted" by any other computation.
    Map<ComponentInstance, LinkedList<LinkedList<TransferVertex>>> syncGroups = new HashMap<>();

    // Fill the syncGroups
    final DAGIterator iterDAGVertices = new DAGIterator(dag); // Iterator on DAG vertices
    // Store if the type of the last DAGVertex scheduled on each core (during the scan of the DAG) is sync or not (to identify groups)
    final Map<ComponentInstance, Boolean> lastVertexScheduled = new HashMap<>();
    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();

      // Get vertex type
      boolean isCommunication = currentVertex instanceof TransferVertex;
      boolean isSynchronization = isCommunication && isSynchronizationTransfer((TransferVertex) currentVertex);

      // Get component
      final ComponentInstance component = (ComponentInstance) currentVertex.getPropertyBean().getValue("Operator");

      // If the currentVertex is a synchronization, store it in the comGroups
      if (isSynchronization) {
        // Get or create the appropriate sync group
        LinkedList<TransferVertex> syncGroup;
        if (!syncGroups.containsKey(component) || !lastVertexScheduled.get(component)) {
          // If the component still has no group OR if its last scheduled vertex is not a sync.
          // Create a new group
          syncGroup = new LinkedList<TransferVertex>();
        } else {
          // The component is associated with a group AND the last scheduled vertex was a communication.
          syncGroup = syncGroups.get(component).getLast();
        }
        syncGroup.add((TransferVertex) currentVertex);

        // Store the syncGroup (if needed) with the appropriate component.
        if (!syncGroups.containsKey(component)) {
          // Create first group of the component
          LinkedList<LinkedList<TransferVertex>> componentLinkedList = new LinkedList<>();
          syncGroups.put(component, componentLinkedList);
          componentLinkedList.add(syncGroup);
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
  private static void debugList(Map<ComponentInstance, LinkedList<LinkedList<TransferVertex>>> comGroups) {
    for (Entry<ComponentInstance, LinkedList<LinkedList<TransferVertex>>> e : comGroups.entrySet()) {
      System.out.print(e.getKey().getInstanceName() + ": ");
      for (LinkedList<TransferVertex> set : e.getValue()) {
        System.out.print(set.size() + "-");
      }
      System.out.println("");
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
    List<ComponentInstance> communicationSteps = ((MessageRouteStep) ((TransferVertex) currentVertex).getRouteStep()).getNodes();
    for (ComponentInstance component : communicationSteps) {
      boolean isZeroCopy = false;
      for (Parameter p : component.getParameters()) {
        isZeroCopy |= p.getKey().equals(ZERO_COPY) && p.getValue().equals(TRUE);
      }
      isSyncWait &= isZeroCopy;
    }
    return isSyncWait;
  }
}

/**
 * Utility class used to store per component index of the last sync group executed.
 * 
 * @author kdesnos
 *
 */
class SyncIndex {
  Map<ComponentInstance, Integer> syncIndexPerComponent;

  public SyncIndex(Set<ComponentInstance> components) {
    syncIndexPerComponent = new HashMap<>();
    for (ComponentInstance comp : components) {
      syncIndexPerComponent.put(comp, -1);
    }
  }

  public boolean strictlySmallerOrEqual(SyncIndex syncIndex) {
    boolean result = true;
    for (ComponentInstance comp : syncIndex.syncIndexPerComponent.keySet()) {
      result &= this.syncIndexPerComponent.get(comp) <= syncIndex.syncIndexPerComponent.get(comp);
    }
    return result;
  }

  public void increment(ComponentInstance component) {
    this.syncIndexPerComponent.put(component, this.syncIndexPerComponent.get(component) + 1);
  }

  @Override
  public String toString() {
    String result = "";
    List<ComponentInstance> components = new ArrayList<>(syncIndexPerComponent.keySet());
    components.sort((a, b) -> {
      return a.getInstanceName().compareTo(b.getInstanceName());
    });
    for (ComponentInstance c : components) {
      result += c.getInstanceName() + ": " + syncIndexPerComponent.get(c) + '\n';
    }

    return result;
  }

  public SyncIndex clone() {
    SyncIndex res = new SyncIndex(this.syncIndexPerComponent.keySet());
    this.syncIndexPerComponent.forEach((comp, index) -> {
      res.syncIndexPerComponent.put(comp, index);
    });
    return res;
  }

  /**
   * Update this with the max index of itself versus the one of the {@link SyncIndex} passed as a paramater for each {@link ComponentInstance}.
   * 
   * @param a
   *          the {@link SyncIndex} used to update the max value
   */
  public void max(SyncIndex a) {
    for (ComponentInstance c : a.syncIndexPerComponent.keySet()) {
      this.syncIndexPerComponent.put(c, Math.max(this.syncIndexPerComponent.get(c), a.syncIndexPerComponent.get(c)));
    }
  }
}
