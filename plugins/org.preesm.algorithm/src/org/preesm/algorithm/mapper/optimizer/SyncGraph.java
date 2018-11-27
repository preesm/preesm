/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.mapper.optimizer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.jgrapht.alg.TransitiveReduction;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.preesm.algorithm.mapper.AbstractMappingFromDAG;
import org.preesm.algorithm.mapper.model.special.SendVertex;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.attributes.Parameter;
import org.preesm.model.slam.route.MessageRouteStep;

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
      throw new PreesmException("Could not close string writer", e);
    }
    return writer.toString();
  }

}
