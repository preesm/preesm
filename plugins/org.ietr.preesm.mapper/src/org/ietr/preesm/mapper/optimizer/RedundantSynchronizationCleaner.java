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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import org.jgrapht.alg.TransitiveReduction;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.StringComponentNameProvider;

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
  }

  private static final ComponentInstance findComponent(final DAGVertex vertex) {
    return (ComponentInstance) vertex.getPropertyBean().getValue("Operator");
  }

  /**
   * Analyzes the communications in the {@link DirectedAcyclicGraph} and remove redundant synchronizations.
   *
   * @param dag
   *          The {@link DirectedAcyclicGraph} whose synchronizations are optimized. The DAG is modified during call to
   *          the function.
   */
  public static void cleanRedundantSynchronization(final DirectedAcyclicGraph dag) {

    // final Set<TransferVertex> toBeRemoved = RedundantSynchronizationCleaner.karoldMethod(dag);
    final Set<DAGVertex> toBeRemoved = RedundantSynchronizationCleaner.proMethod(dag);
    toBeRemoved.forEach(transferVertex -> transferVertex.setPropertyValue("Redundant", Boolean.valueOf(true)));
    WorkflowLogger.getLogger().log(Level.INFO, "removing " + toBeRemoved.size() + " syncs");
  }

  private static Set<DAGVertex> proMethod(final DirectedAcyclicGraph dag) {
    final Set<DAGVertex> toBeRemoved = new LinkedHashSet<>();

    final ConsecutiveTransfersMap initFrom = ConsecutiveTransfersMap.initFrom(dag);
    System.out.println(initFrom);
    final SyncGraph redundantPaths = SyncGraph.buildUnecessaryEdges(initFrom);

    final Set<DAGVertex> redundantSyncVertices = RedundantSynchronizationCleaner.redundantSyncVertices(initFrom,
        redundantPaths);
    final Set<DAGVertex> duplicateSyncVertices = RedundantSynchronizationCleaner.duplicateSyncVertices(initFrom);

    toBeRemoved.addAll(duplicateSyncVertices);
    // toBeRemoved.addAll(redundantSyncVertices);
    dag.removeAllVertices(redundantSyncVertices);
    // dag.removeAllVertices(duplicateSyncVertices);
    return toBeRemoved;
  }

  private static Set<DAGVertex> duplicateSyncVertices(final ConsecutiveTransfersMap groupMap) {
    final Set<DAGVertex> res = new LinkedHashSet<>();
    for (final Entry<ComponentInstance, ConsecutiveTransfersList> e : groupMap.entrySet()) {
      final ConsecutiveTransfersList groupList = e.getValue();
      for (final ConsecutiveTransfersGroup sourceGroup : groupList) {
        final Set<ComponentInstance> targets = new LinkedHashSet<>();

        // for (int i = sourceGroup.size() - 1; i >= 0; i--) {
        // TransferVertex sourceVertex = sourceGroup.get(i);
        for (final TransferVertex sourceVertex : sourceGroup) {

          final DAGVertex target = sourceVertex.outgoingEdges().iterator().next().getTarget();
          final ComponentInstance findComponent = groupMap.findComponent(target);
          if (targets.contains(findComponent)) {
            res.add(sourceVertex);
            res.add(target);
          } else {
            targets.add(findComponent);
          }
        }
      }
    }
    return res;
  }

  private static Set<DAGVertex> redundantSyncVertices(final ConsecutiveTransfersMap initFrom,
      final SyncGraph redundantPaths) {
    final Set<DAGVertex> toBeRemoved = new LinkedHashSet<>();
    final Set<String> edgeSet = redundantPaths.edgeSet();
    for (final String edge : edgeSet) {
      final ConsecutiveTransfersGroup edgeSource = redundantPaths.getEdgeSource(edge);
      final ConsecutiveTransfersGroup edgeTarget = redundantPaths.getEdgeTarget(edge);

      final ComponentInstance sourceComponent = edgeSource.getComponent();
      final ComponentInstance targetComponent = edgeTarget.getComponent();

      for (final TransferVertex sourceVertex : edgeSource) {
        final DAGVertex target = sourceVertex.outgoingEdges().iterator().next().getTarget();
        final ComponentInstance findComponent = initFrom.findComponent(target);
        if (findComponent == targetComponent) {
          toBeRemoved.add(sourceVertex);
          toBeRemoved.add(target);
        }
      }

      for (final TransferVertex targetVertex : edgeTarget) {
        final DAGVertex source = targetVertex.incomingEdges().iterator().next().getSource();
        final ComponentInstance findComponent = initFrom.findComponent(source);
        if (findComponent == sourceComponent) {
          toBeRemoved.add(targetVertex);
          toBeRemoved.add(source);
        }
      }
    }
    return toBeRemoved;
  }

  /**
   *
   */
  static class RSCGraph extends org.jgrapht.graph.DirectedMultigraph<List<TransferVertex>, String> {

    public RSCGraph() {
      super(String.class);
    }

    @Override
    public RSCGraph clone() {
      return (RSCGraph) super.clone();
    }

    /**
     *
     */
    private static final long serialVersionUID = 8454469261232923042L;

  }

  /**
   *
   */
  static Set<TransferVertex> method2(final DirectedAcyclicGraph dag, final MapofListofListofTransfers syncGroups) {
    final Set<TransferVertex> res = new LinkedHashSet<>();

    final RSCGraph graph = new RSCGraph();
    // 1- add vertices (one per group, regardless the component)
    for (final ComponentInstance component : syncGroups.getComponents()) {
      for (final List<TransferVertex> group : syncGroups.get(component)) {
        graph.addVertex(group);
      }
    }
    // 2- add synchronization between all elements of the same component, wrt order:
    // there is a sync from one element to all its successors
    for (final ComponentInstance component : syncGroups.getComponents()) {
      final List<List<TransferVertex>> list = syncGroups.get(component);
      final int size = list.size();
      for (int i = 0; i < (size - 1); i++) {
        for (int j = i + 1; j < size; j++) {
          graph.addEdge(list.get(i), list.get(j), "seq" + component.getInstanceName() + "_" + i + "_" + j);
        }
      }
    }
    final List<String> seqSyncEdges = new ArrayList<>(graph.edgeSet());

    // 3- add syncs between all elements that actually share sync in the DAG
    for (final ComponentInstance component : syncGroups.getComponents()) {
      for (final List<TransferVertex> sourceGroup : syncGroups.get(component)) {
        for (final TransferVertex vertex : sourceGroup) {
          if (vertex instanceof SendVertex) {
            final DAGVertex receiver = vertex.outgoingEdges().iterator().next().getTarget();
            final List<TransferVertex> targetGroup = syncGroups.lookupSyncGroup(receiver);
            final String comName = "com_" + RedundantSynchronizationCleaner.getName(vertex, syncGroups) + "_"
                + RedundantSynchronizationCleaner.getName(receiver, syncGroups);
            System.out.println("adding " + comName);
            final boolean addEdge = graph.addEdge(sourceGroup, targetGroup, comName);
            System.out.println("   -> " + addEdge);
          }
        }
      }
    }
    final String print = RedundantSynchronizationCleaner.print(graph, syncGroups);
    System.out.println(print);

    final RSCGraph reducedGraph = graph.clone();
    TransitiveReduction.INSTANCE.reduce(graph);
    //
    final String print2 = RedundantSynchronizationCleaner.print(graph, syncGroups);
    System.out.println(print2);
    //
    graph.removeAllEdges(reducedGraph.edgeSet());
    graph.removeAllEdges(seqSyncEdges);
    final String print3 = RedundantSynchronizationCleaner.print(graph, syncGroups);
    System.out.println(print3);

    return res;
  }

  private static final String getName(final List<TransferVertex> group, final MapofListofListofTransfers syncGroups) {
    final TransferVertex firstVertex = group.get(0);
    final ComponentInstance findComponent = RedundantSynchronizationCleaner.findComponent(firstVertex);
    final String instanceName = findComponent.getInstanceName();
    final int indexOf = syncGroups.get(findComponent).indexOf(group);
    return instanceName + "_" + indexOf;
  }

  private static final String getName(final DAGVertex v, final MapofListofListofTransfers syncGroups) {
    final List<TransferVertex> lookupSyncGroup = syncGroups.lookupSyncGroup(v);
    return RedundantSynchronizationCleaner.getName(lookupSyncGroup, syncGroups);
  }

  /**
   *
   */
  private static class EdgeNameProvider extends StringComponentNameProvider<String> {
    @Override
    public String getName(final String component) {
      return component;
    }
  }

  /**
   *
   */
  private static class VertexNameProvider extends StringComponentNameProvider<List<TransferVertex>> {
    private final MapofListofListofTransfers syncGroups;

    public VertexNameProvider(final MapofListofListofTransfers syncGroups) {
      this.syncGroups = syncGroups;
    }

    @Override
    public String getName(final List<TransferVertex> group) {
      return RedundantSynchronizationCleaner.getName(group, this.syncGroups);
    }
  }

  private static String print(final RSCGraph graph, final MapofListofListofTransfers syncGroups) {
    final StringWriter stringWriter = new StringWriter();

    final DOTExporter<List<TransferVertex>, String> exporter = new DOTExporter<>(new VertexNameProvider(syncGroups),
        new VertexNameProvider(syncGroups), new EdgeNameProvider());
    exporter.exportGraph(graph, stringWriter);
    return stringWriter.toString();
  }

  /**
   * Analyzes the communications in the {@link DirectedAcyclicGraph} and remove redundant synchronizations.
   *
   * @param dag
   *          The {@link DirectedAcyclicGraph} whose synchronizations are optimized. The DAG is modified during call to
   *          the function.
   * @return
   */
  public static Set<TransferVertex> karoldMethod(final DirectedAcyclicGraph dag) {
    // Get the groups of synchronization.
    final MapofListofListofTransfers syncGroups = RedundantSynchronizationCleaner.createSyncGroupsPerComponents(dag);

    // Get the list of components
    final Set<ComponentInstance> components = syncGroups.getComponents();

    // Scan the DAG to identify the precedence relationships enforced by each communication

    // Along the scan: For each Component, store the index of the last syncGroup encountered along the scan
    final Map<ComponentInstance, SyncIndex> lastSyncedPerComp = new LinkedHashMap<>();
    // init the map
    for (final ComponentInstance component : components) {
      lastSyncedPerComp.put(component, new SyncIndex(components));
    }

    // Along the scan: Index of sender registered to receiver vertex
    final Map<ReceiveVertex, SyncIndex> registeredSenderSyncIndex = new LinkedHashMap<>();

    // Along the scan: register the transfer vertex (send AND receive) of synchronization to Be Removed
    final Set<TransferVertex> toBeRemoved = new LinkedHashSet<>();

    final TopologicalDAGIterator iterDAGVertices = new TopologicalDAGIterator(dag); // Iterator on DAG vertices
    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();
      // Get component
      final ComponentInstance component = RedundantSynchronizationCleaner.findComponent(currentVertex);

      final List<TransferVertex> groupForCurrentVertex = syncGroups.lookupSyncGroup(currentVertex);
      // When the beginning of a sync group is reached
      if ((currentVertex instanceof TransferVertex)
          && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex)
          && groupForCurrentVertex.get(0).equals(currentVertex)) {
        // Increase self syncIndex
        lastSyncedPerComp.get(component).increment(component);
      }

      // When a send sync is encountered
      if ((currentVertex instanceof SendVertex)
          && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex)) {
        // Register the current syncIndexes to the receiver of this com.
        // there is only one outgoing edge for each sender vertex. Use it to retrieve the corresponding receivet
        final ReceiveVertex receiver = (ReceiveVertex) currentVertex.outgoingEdges().iterator().next().getTarget();
        registeredSenderSyncIndex.put(receiver, new SyncIndex(lastSyncedPerComp.get(component)));
      }

      // When the end of a sync group is reached
      if ((currentVertex instanceof TransferVertex)
          && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex)
          && groupForCurrentVertex.get(groupForCurrentVertex.size() - 1).equals(currentVertex)) {

        // Remove redundant sync from the group
        final SyncIndex coveredIdx = new SyncIndex(lastSyncedPerComp.get(component));
        for (final TransferVertex syncVertex : groupForCurrentVertex) {
          if (syncVertex instanceof ReceiveVertex) {
            // Is the receive vertex already covered (either by the currentSyncIndex of the core, OR by another receive
            // of the group.
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
        final List<TransferVertex> syncGroup = groupForCurrentVertex;
        // Sanity check: self-index cannot be greater than current group index
        if (coveredIdx.getSyncIndexOfComponent(component) > syncGroups.get(component).indexOf(syncGroup)) {
          throw new WorkflowException("Problem in communication order. There seems to be a deadlock.");
        }

        // Do the update
        lastSyncedPerComp.put(component, coveredIdx);
      }
    }
    return toBeRemoved;
  }

  /**
   * Create a {@link Map} that associates to each {@link ComponentInstance} a {@link List} of all its synchronization
   * communication. Communications are stored as a {@link List} of {@link List} where each nested {@link List}
   * represents a group of consecutive synchronization primitive that is not "interrupted" by any other computation.
   *
   * @param dag
   *          the scheduled {@link DirectedAcyclicGraph} from which communication are extracted
   * @return the described {@link Map}
   */
  private static MapofListofListofTransfers createSyncGroupsPerComponents(final DirectedAcyclicGraph dag) {
    // This Map associates to each component of the architecture a List of all its communications.
    // Communications are stored as a List of list where each set represents a group of consecutive receive
    // communication primitive that is not
    // "interrupted" by any other computation.
    final MapofListofListofTransfers syncGroups = new MapofListofListofTransfers();

    // Fill the syncGroups
    final TopologicalDAGIterator iterDAGVertices = new TopologicalDAGIterator(dag); // Iterator on DAG vertices
    // Store if the type of the last DAGVertex scheduled on each core (during the scan of the DAG) is sync or not (to
    // identify groups)
    final Map<ComponentInstance, Boolean> lastVertexScheduledIsSyncPerComponent = new LinkedHashMap<>();
    while (iterDAGVertices.hasNext()) {
      final DAGVertex currentVertex = iterDAGVertices.next();
      // Get component
      final ComponentInstance component = RedundantSynchronizationCleaner.findComponent(currentVertex);

      // Get vertex type
      final boolean isCommunication = currentVertex instanceof TransferVertex;
      final boolean isSynchronization = isCommunication
          && RedundantSynchronizationCleaner.isSynchronizationTransfer((TransferVertex) currentVertex);

      // If the currentVertex is a synchronization, store it in the comGroups
      if (isSynchronization) {
        // syncGroups.getOrDefault(component, defaultValue)
        final boolean lastVertexOnComponentWasSync = lastVertexScheduledIsSyncPerComponent.getOrDefault(component,
            false);

        if (syncGroups.isEmpty(component) || !lastVertexOnComponentWasSync) {
          syncGroups.addNewGroup(component, (TransferVertex) currentVertex);
        } else {
          // The component is associated with a group AND the last scheduled vertex was a communication.
          syncGroups.addInGroup(component, (TransferVertex) currentVertex);
        }
      }

      // Save last vertex (replace previously saved type)
      lastVertexScheduledIsSyncPerComponent.put(component, isSynchronization);
    }
    RedundantSynchronizationCleaner.debugList(syncGroups);
    return syncGroups;
  }

  /**
   * For debugging purposes only
   *
   * @param comGroups
   *          see previous function
   */
  private static void debugList(final MapofListofListofTransfers comGroups) {
    WorkflowLogger.getLogger().log(Level.INFO, "Synchronization Consecutive Lists : \n" + comGroups.toString());
  }

  /**
   * Check wether a given {@link TransferVertex} is supported only by communication steps going through zero-copy
   * communication nodes in the architecture.
   *
   * @param currentVertex
   *          the {@link TransferVertex} whose communication steps parameters are being checked.
   * @return <code>true</code> if all communication steps are handled by zero copy communication nodes,
   *         <code>false</code> otherwise.
   */
  private static boolean isSynchronizationTransfer(final TransferVertex currentVertex) {
    boolean isSyncWait = true;
    // Check if all comm steps are zero copy.
    final List<ComponentInstance> communicationSteps = ((MessageRouteStep) currentVertex.getRouteStep()).getNodes();
    for (final ComponentInstance component : communicationSteps) {
      boolean isZeroCopy = false;
      for (final Parameter p : component.getParameters()) {
        isZeroCopy |= p.getKey().equals(RedundantSynchronizationCleaner.ZERO_COPY)
            && p.getValue().equals(AbstractMappingFromDAG.VALUE_TRUE);
      }
      isSyncWait &= isZeroCopy;
    }
    return isSyncWait;
  }
}
