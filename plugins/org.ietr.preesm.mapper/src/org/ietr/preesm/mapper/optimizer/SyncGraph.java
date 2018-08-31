package org.ietr.preesm.mapper.optimizer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.jgrapht.alg.TransitiveReduction;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;

/**
 *
 */
public class SyncGraph extends org.jgrapht.graph.DirectedAcyclicGraph<ConsecutiveTransfersGroup, String> {

  private static final long serialVersionUID = -5902732205999536143L;

  private SyncGraph() {
    super(String.class);
  }

  /**
   *
   */
  public static final SyncGraph buildUnecessaryEdges(final ConsecutiveTransfersMap groupMap) {

    final SyncGraph graph = new SyncGraph();
    // 1- add vertices (one per group, regardless the component)
    for (final Entry<ComponentInstance, ConsecutiveTransfersList> e : groupMap.entrySet()) {
      final ConsecutiveTransfersList groupList = e.getValue();
      final ComponentInstance component = e.getKey();
      for (int i = 0; i < groupList.size(); i++) {
        final ConsecutiveTransfersGroup group = groupList.get(i);
        graph.addVertex(group);
        for (int j = 0; j < i; j++) {
          graph.addEdge(groupList.get(j), group, "seq" + component.getInstanceName() + "_" + j + "_" + i);
        }
      }
    }
    final List<String> seqSyncEdges = new ArrayList<>(graph.edgeSet());

    // 3- add syncs between all elements that actually share sync in the DAG
    for (final Entry<ComponentInstance, ConsecutiveTransfersList> e : groupMap.entrySet()) {
      final ConsecutiveTransfersList groupList = e.getValue();
      for (final ConsecutiveTransfersGroup sourceGroup : groupList) {
        for (final TransferVertex vertex : sourceGroup) {
          if (vertex instanceof SendVertex) {
            final DAGVertex receiver = vertex.outgoingEdges().iterator().next().getTarget();
            final ConsecutiveTransfersGroup targetGroup = groupMap.findGroup(receiver);
            final String comName = "com_" + SyncGraph.getName(sourceGroup) + "_" + SyncGraph.getName(targetGroup);
            System.out.println("adding " + comName);
            final boolean addEdge = graph.addEdge(sourceGroup, targetGroup, comName);
            System.out.println(" -> " + addEdge);
          }
        }
      }
    }

    // TODO compute transitive reduction + remove seq edges
    final SyncGraph reducedGraph = (SyncGraph) graph.clone();
    System.out.println("##################");
    System.out.println("## initial graph");
    System.out.println("##################");
    System.out.println(graph.toDotty());
    TransitiveReduction.INSTANCE.reduce(reducedGraph);
    System.out.println("##################");
    System.out.println("## transitive reduction");
    System.out.println("##################");
    System.out.println(reducedGraph.toDotty());
    //
    graph.removeAllEdges(reducedGraph.edgeSet());
    graph.removeAllEdges(seqSyncEdges);
    System.out.println("##################");
    System.out.println("## Syncs to remove");
    System.out.println("##################");
    System.out.println(graph.toDotty());

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
  class VertexNameProvider implements ComponentNameProvider<ConsecutiveTransfersGroup> {
    @Override
    public String getName(final ConsecutiveTransfersGroup group) {
      return SyncGraph.getName(group);
    }
  }

  /**
   *
   */
  class EdgeNameProvider implements ComponentNameProvider<String> {
    @Override
    public String getName(final String str) {
      return str;
    }
  }

  @Override
  public String toString() {
    return super.toString();
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return writer.toString();
  }

}
