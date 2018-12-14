/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2012 - 2013)
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
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.property.EdgeTiming;
import org.preesm.algorithm.mapper.model.property.VertexTiming;
import org.preesm.algorithm.mapper.tools.CustomTopologicalIterator;
import org.preesm.algorithm.model.IGraphVisitor;
import org.preesm.algorithm.model.dag.DAGVertex;

/**
 * Visitor computing the BLevel of each actor firing. T levels are considered to be valid.
 *
 * @author mpelcat
 */
public class BLevelVisitor implements IGraphVisitor<MapperDAG, MapperDAGVertex, MapperDAGEdge> {

  /**
   * Visiting a graph in topological order to assign b-levels.
   *
   * @param dag
   *          the dag
   */
  @Override
  public void visit(final MapperDAG dag) {
    // Visiting a DAG consists in computing T Levels for all its vertices,
    // starting from vertices without predecessors
    final CustomTopologicalIterator iterator = new CustomTopologicalIterator(dag, false);
    while (iterator.hasNext()) {
      final DAGVertex vertex = iterator.next();
      vertex.accept(this);
    }
  }

  /**
   * Visiting a vertex to assign b-levels. Successors are considered already visited
   *
   * @param dagVertex
   *          the dag vertex
   */
  @Override
  public void visit(final MapperDAGVertex dagVertex) {
    long maxBLevel = -1;
    final VertexTiming timing = dagVertex.getTiming();

    if (dagVertex.outgoingEdges().isEmpty()) {
      timing.setBLevel(timing.getCost());
      return;
    }

    // Synchronized vertices are taken into account to compute b-level
    final List<MapperDAGVertex> synchroVertices = timing.getVertices((MapperDAG) dagVertex.getBase());
    final Map<MapperDAGVertex, MapperDAGEdge> successors = new LinkedHashMap<>();

    for (final MapperDAGVertex v : synchroVertices) {
      final Map<MapperDAGVertex, MapperDAGEdge> succs = v.getSuccessors(false);
      successors.putAll(succs);
    }

    // From successors, computing the b-level

    for (final Entry<MapperDAGVertex, MapperDAGEdge> entry : successors.entrySet()) {
      final MapperDAGVertex succ = entry.getKey();
      final VertexTiming succTiming = succ.getTiming();
      final EdgeTiming edgeTiming = successors.get(succ).getTiming();
      if (succTiming.hasBLevel() && timing.hasCost() && edgeTiming.hasCost()) {
        final long currentBLevel = succTiming.getBLevel() + timing.getCost() + edgeTiming.getCost();
        if (currentBLevel > maxBLevel) {
          maxBLevel = currentBLevel;
        }
      } else {
        timing.resetBLevel();
      }
    }

    if (maxBLevel >= 0) {
      timing.setBLevel(maxBLevel);
    }
  }
}
