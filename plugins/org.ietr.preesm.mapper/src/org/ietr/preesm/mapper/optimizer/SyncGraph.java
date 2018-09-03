package org.ietr.preesm.mapper.optimizer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.attributes.Parameter;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.mapper.AbstractMappingFromDAG;
import org.ietr.preesm.mapper.PreesmMapperException;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.jgrapht.alg.TransitiveReduction;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;

/**
 *
 */
public class SyncGraph extends org.jgrapht.graph.SimpleDirectedWeightedGraph<ConsecutiveTransfersGroup, String> {

  private static final long serialVersionUID = -5902732205999536143L;

  private SyncGraph() {
    super(String.class);
  }

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

  /**
   *
   */
  public static final SyncGraph buildEdges(final ConsecutiveTransfersMap groupMap) {
    final SyncGraph graph = new SyncGraph();
    buildSeqSyncEdges(groupMap, graph);
    buildDagSyncEdges(groupMap, graph);
    return graph;
  }

  private static void buildDagSyncEdges(final ConsecutiveTransfersMap groupMap, final SyncGraph graph) {
    for (final Entry<ComponentInstance, ConsecutiveTransfersList> e : groupMap.entrySet()) {
      final ConsecutiveTransfersList groupList = e.getValue();
      for (final ConsecutiveTransfersGroup sourceGroup : groupList) {
        for (final TransferVertex vertex : sourceGroup) {
          if (vertex instanceof SendVertex && isSynchronizationTransfer(vertex)) {
            final DAGVertex receiver = vertex.outgoingEdges().iterator().next().getTarget();
            final ConsecutiveTransfersGroup targetGroup = groupMap.findGroup(receiver);
            final String comName = "com_" + SyncGraph.getName(sourceGroup) + "_" + SyncGraph.getName(targetGroup);
            graph.addEdge(sourceGroup, targetGroup, comName);
            graph.setEdgeWeight(comName, +1);
          }
        }
      }
    }
  }

  private static void buildSeqSyncEdges(final ConsecutiveTransfersMap groupMap, final SyncGraph graph) {
    for (final Entry<ComponentInstance, ConsecutiveTransfersList> e : groupMap.entrySet()) {
      final ConsecutiveTransfersList groupList = e.getValue();
      final ComponentInstance component = e.getKey();
      for (int i = 0; i < groupList.size(); i++) {
        final ConsecutiveTransfersGroup group = groupList.get(i);
        graph.addVertex(group);
        for (int j = 0; j < i; j++) {
          final String comName = "seq" + component.getInstanceName() + "_" + j + "_" + i;
          graph.addEdge(groupList.get(j), group, comName);
          graph.setEdgeWeight(comName, -1);
        }
      }
    }
  }

  /**
   *
   */
  public final List<String> getSeqSyncEdges() {
    final Set<String> edgeSet = this.edgeSet();
    final List<String> seqSyncEdges = new ArrayList<>();
    edgeSet.forEach(s -> {
      if (this.getEdgeWeight(s) < 0) {
        seqSyncEdges.add(s);
      }
    });
    return seqSyncEdges;
  }

  /**
   *
   */
  public final SyncGraph computeRedundantEdges() {
    final SyncGraph reducedGraph = (SyncGraph) this.clone();
    TransitiveReduction.INSTANCE.reduce(reducedGraph);
    final SyncGraph graph = (SyncGraph) this.clone();
    graph.removeAllEdges(reducedGraph.edgeSet());
    graph.removeAllEdges(getSeqSyncEdges());
    return graph;
  }

  private static final String getName(final ConsecutiveTransfersGroup group) {
    final ComponentInstance findComponent = group.getComponent();
    final String instanceName = findComponent.getInstanceName();
    final int indexOf = group.getList().indexOf(group);
    return instanceName + "_" + indexOf;
  }

  /**
   *
   */
  private static class VertexNameProvider implements ComponentNameProvider<ConsecutiveTransfersGroup> {
    @Override
    public String getName(final ConsecutiveTransfersGroup group) {
      return SyncGraph.getName(group);
    }
  }

  /**
   *
   */
  private static class EdgeNameProvider implements ComponentNameProvider<String> {
    @Override
    public String getName(final String str) {
      return str;
    }
  }

  /**
   *
   */
  public String toDotty() {
    final DOTExporter<ConsecutiveTransfersGroup, String> exporter = new DOTExporter<>(new VertexNameProvider(),
        new VertexNameProvider(), new EdgeNameProvider());
    final StringWriter writer = new StringWriter();
    exporter.exportGraph(this, writer);
    try {
      writer.close();
    } catch (final IOException e) {
      throw new PreesmMapperException("Could not close string writer", e);
    }
    return writer.toString();
  }

}
