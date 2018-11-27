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
package org.preesm.algorithm.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.preesm.algorithm.iterators.TopologicalDAGIterator;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.model.scenario.types.ImplementationPropertyNames;

/**
 *
 */
public class ScheduledDAGIterator implements Iterator<MapperDAGVertex> {

  private final List<MapperDAGVertex> vertexInSchedulingOrder;
  private int                         iterator;

  /**
   *
   */
  public ScheduledDAGIterator(final DirectedAcyclicGraph algo) {
    this.vertexInSchedulingOrder = new ArrayList<>();

    final TopologicalDAGIterator iter = new TopologicalDAGIterator(algo);
    // Fill a Map with Scheduling order and DAGvertices
    final Map<Integer, MapperDAGVertex> orderedDAGVertexMap = new TreeMap<>();
    while (iter.hasNext()) {
      final MapperDAGVertex vertex = (MapperDAGVertex) iter.next();
      final Integer order = (Integer) vertex.getPropertyBean()
          .getValue(ImplementationPropertyNames.Vertex_schedulingOrder);
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
  public MapperDAGVertex next() {
    return this.vertexInSchedulingOrder.get(this.iterator++);
  }

}
