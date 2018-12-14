/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2012 - 2014)
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
package org.preesm.algorithm.mapper.timekeeper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.property.EdgeTiming;
import org.preesm.algorithm.mapper.model.property.VertexTiming;
import org.preesm.algorithm.model.IGraphVisitor;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.iterators.TopologicalDAGIterator;
import org.preesm.commons.exceptions.PreesmException;

/**
 * Visitor computing the TLevel of each actor firing.
 *
 * @author mpelcat
 */
public class TLevelVisitor implements IGraphVisitor<MapperDAG, MapperDAGVertex, MapperDAGEdge> {

  /** Vertices which TLevel needs to be recomputed. */
  private final Set<MapperDAGVertex> dirtyVertices;

  /**
   * Instantiates a new t level visitor.
   *
   * @param dirtyVertices
   *          the dirty vertices
   */
  public TLevelVisitor(final Set<MapperDAGVertex> dirtyVertices) {
    super();
    this.dirtyVertices = dirtyVertices;
  }

  /**
   * Visiting a graph in topological order to assign t-levels.
   *
   * @param dag
   *          the dag
   */
  @Override
  public void visit(final MapperDAG dag) {
    // Visiting a DAG consists in computing T Levels for all its vertices,
    // starting from vertices without predecessors
    final TopologicalDAGIterator iterator = new TopologicalDAGIterator(dag);

    try {
      // Recomputing all TLevels
      if (this.dirtyVertices.isEmpty()) {
        while (iterator.hasNext()) {
          final DAGVertex next = iterator.next();
          next.accept(this);
        }
      } else {
        boolean dirty = false;
        while (iterator.hasNext()) {
          final DAGVertex next = iterator.next();
          if (!dirty) {
            dirty |= this.dirtyVertices.contains(next);
          }
          if (dirty) {
            next.accept(this);
          }
        }
      }
    } catch (final NoSuchElementException e) {
      throw new PreesmException(e);
    }
  }

  /**
   * Visiting a vertex to assign t-levels. Prececessors are considered already visited. Successors are accepted
   *
   * @param dagVertex
   *          the dag vertex
   * @throws PreesmException
   *           the SDF 4 J exception
   */
  @Override
  public void visit(final MapperDAGVertex dagVertex) {
    long maxTLevel = -1;
    final VertexTiming timing = dagVertex.getTiming();

    // Synchronized vertices are taken into account to compute t-level
    final List<MapperDAGVertex> synchroVertices = timing.getVertices((MapperDAG) dagVertex.getBase());

    if (dagVertex.incomingEdges().isEmpty()) {
      timing.setTLevel(0L);
    } else {
      final Map<MapperDAGVertex, MapperDAGEdge> predecessors = new LinkedHashMap<>();

      for (final MapperDAGVertex v : synchroVertices) {
        final Map<MapperDAGVertex, MapperDAGEdge> preds = v.getPredecessors(false);
        predecessors.putAll(preds);
      }

      // From predecessors, computing the earliest time that the
      // vertex can start
      for (final Entry<MapperDAGVertex, MapperDAGEdge> entry : predecessors.entrySet()) {
        final MapperDAGVertex pred = entry.getKey();
        final VertexTiming predTiming = pred.getTiming();
        final EdgeTiming edgeTiming = predecessors.get(pred).getTiming();
        if (predTiming.hasTLevel() && predTiming.hasCost() && edgeTiming.hasCost()) {
          final long currentTLevel = predTiming.getTLevel() + predTiming.getCost() + edgeTiming.getCost();
          if (currentTLevel > maxTLevel) {
            maxTLevel = currentTLevel;
          }
        } else {
          timing.resetTLevel();
        }
      }

      if (maxTLevel >= 0) {
        timing.setTLevel(maxTLevel);
      }
    }
  }

}
