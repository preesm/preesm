package org.ietr.preesm.mapper.optimizer;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.ScheduledDAGIterator;

/**
 *
 */
public class ConsecutiveTransfersMap extends LinkedHashMap<ComponentInstance, ConsecutiveTransfersList> {

  private static final long serialVersionUID = -8987927834037340036L;

  static final ComponentInstance findComponent(final DAGVertex vertex) {
    return (ComponentInstance) vertex.getPropertyBean().getValue("Operator");
  }

  final ConsecutiveTransfersGroup findGroup(final DAGVertex vertex) {
    final ComponentInstance findComponent = ConsecutiveTransfersMap.findComponent(vertex);
    final ConsecutiveTransfersList consecutiveTransfersList = get(findComponent);
    for (final ConsecutiveTransfersGroup group : consecutiveTransfersList) {
      if (group.contains(vertex)) {
        return group;
      }
    }
    return null;
  }

  private ConsecutiveTransfersMap() {
    super();
  }

  /**
   *
   */
  public static final ConsecutiveTransfersMap initFrom(final DirectedAcyclicGraph dag) {
    final ConsecutiveTransfersMap res = new ConsecutiveTransfersMap();

    final ScheduledDAGIterator dagIterator = new ScheduledDAGIterator(dag);
    while (dagIterator.hasNext()) {
      final DAGVertex currentVertex = dagIterator.next();
      final ComponentInstance findComponent = ConsecutiveTransfersMap.findComponent(currentVertex);

      final ConsecutiveTransfersList transferList = res.getOrDefault(findComponent,
          new ConsecutiveTransfersList(res, findComponent));
      transferList.process(currentVertex);
      res.put(findComponent, transferList);
    }
    return res;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Entry<ComponentInstance, ConsecutiveTransfersList> e : entrySet()) {
      final ComponentInstance componentInstance = e.getKey();
      final ConsecutiveTransfersList transferGroupList = e.getValue();
      sb.append("  - " + componentInstance.getInstanceName() + ": " + transferGroupList.toString() + "\n");
    }
    return sb.toString();
  }
}
