package org.ietr.preesm.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.ietr.dftools.algorithm.iterators.TopologicalDAGIterator;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.preesm.core.types.ImplementationPropertyNames;

/**
 *
 */
public class ScheduledDAGIterator implements Iterator<DAGVertex> {

  private final List<DAGVertex> vertexInSchedulingOrder;
  private int                   iterator;

  /**
   *
   */
  public ScheduledDAGIterator(final DirectedAcyclicGraph algo) {
    this.vertexInSchedulingOrder = new ArrayList<>();

    final TopologicalDAGIterator iter = new TopologicalDAGIterator(algo);
    // Fill a Map with Scheduling order and DAGvertices
    final Map<Integer, DAGVertex> orderedDAGVertexMap = new TreeMap<>();
    while (iter.hasNext()) {
      final DAGVertex vertex = iter.next();
      final Integer order = vertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_schedulingOrder,
          Integer.class);
      orderedDAGVertexMap.put(order, vertex);
    }
    this.vertexInSchedulingOrder.addAll(orderedDAGVertexMap.values());
    this.iterator = 0;
  }

  @Override
  public boolean hasNext() {
    return (this.iterator < this.vertexInSchedulingOrder.size());
  }

  @Override
  public DAGVertex next() {
    return this.vertexInSchedulingOrder.get(this.iterator++);
  }

}
