/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017)
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
package fi.abo.preesm.dataparallel.operations.graph;

/*
 * (C) Copyright 2005-2017, by Christian Soltenborn and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

/**
 * Computes strongly connected components of a directed graph. The algorithm is implemented after "Cormen et al: Introduction to algorithms", Chapter 22.5. It
 * has a running time of O(V + E).
 *
 * <p>
 * Unlike {@link org.jgrapht.alg.ConnectivityInspector}, this class does not implement incremental inspection. The full algorithm is executed at the first call
 * of {@link KosarajuStrongConnectivityInspector#stronglyConnectedSets()} or {@link KosarajuStrongConnectivityInspector#isStronglyConnected()}.
 *
 * @param <V>
 *          the graph vertex type
 * @param <E>
 *          the graph edge type
 *
 * @author Christian Soltenborn
 * @author Christian Hammer
 * @since Feb 2, 2005
 */
public class KosarajuStrongConnectivityInspector<V, E> extends AbstractStrongConnectivityInspector<V, E> {
  // stores the vertices, ordered by their finishing time in first dfs
  private LinkedList<VertexData<V>> orderedVertices;

  // maps vertices to their VertexData object
  private Map<V, VertexData<V>> vertexToVertexData;

  /**
   * Constructor
   *
   * @param graph
   *          the input graph
   * @throws NullPointerException
   *           if the input graph is null
   */
  public KosarajuStrongConnectivityInspector(final DirectedGraph<V, E> graph) {
    super(graph);
  }

  @Override
  public List<Set<V>> stronglyConnectedSets() {
    if (this.stronglyConnectedSets == null) {
      this.orderedVertices = new LinkedList<>();
      this.stronglyConnectedSets = new Vector<>();

      // create VertexData objects for all vertices, store them
      createVertexData();

      // perform the first round of DFS, result is an ordering
      // of the vertices by decreasing finishing time
      for (final VertexData<V> data : this.vertexToVertexData.values()) {
        if (!data.isDiscovered()) {
          dfsVisit(this.graph, data, null, false);
        }
      }

      // 'create' inverse graph (i.e. every edge is reversed)
      final Graph<V, E> inverseGraph = new EdgeReversedGraph<>(this.graph);

      // get ready for next dfs round
      resetVertexData();

      // second dfs round: vertices are considered in decreasing
      // finishing time order; every tree found is a strongly
      // connected set
      for (final VertexData<V> data : this.orderedVertices) {
        if (!data.isDiscovered()) {
          // new strongly connected set
          final Set<V> set = new HashSet<>();
          this.stronglyConnectedSets.add(set);
          dfsVisit(inverseGraph, data, set, true);
        }
      }

      // clean up for garbage collection
      this.orderedVertices = null;
      this.vertexToVertexData = null;
    }

    return this.stronglyConnectedSets;
  }

  /*
   * Creates a VertexData object for every vertex in the graph and stores them in a HashMap.
   */
  private void createVertexData() {
    this.vertexToVertexData = new HashMap<>(this.graph.vertexSet().size());

    for (final V vertex : this.graph.vertexSet()) {
      this.vertexToVertexData.put(vertex, new VertexData2<>(vertex, false, false));
    }
  }

  /*
   * The subroutine of DFS. NOTE: the set is used to distinguish between 1st and 2nd round of DFS. set == null: finished vertices are stored (1st round). set !=
   * null: all vertices found will be saved in the set (2nd round)
   */
  private void dfsVisit(final Graph<V, E> visitedGraph, final VertexData<V> vertexData, final Set<V> vertices, final boolean isDirected) {
    final Deque<VertexData<V>> stack = new ArrayDeque<>();
    stack.add(vertexData);

    while (!stack.isEmpty()) {
      final VertexData<V> data = stack.removeLast();

      if (!data.isDiscovered()) {
        data.setDiscovered(true);

        if (vertices != null) {
          vertices.add(data.getVertex());
        }

        stack.add(new VertexData1<>(data, true, true));

        // follow all edges
        for (final E edge : ((DirectedGraph<V, E>) visitedGraph).outgoingEdgesOf(data.getVertex())) {
          final VertexData<V> targetData = this.vertexToVertexData.get(visitedGraph.getEdgeTarget(edge));

          if (!targetData.isDiscovered()) {
            // the "recursion"
            stack.add(targetData);
          }
        }
      } else if (data.isFinished()) {
        if (vertices == null) {
          this.orderedVertices.addFirst(data.getFinishedData());
        }
      }
    }
  }

  /*
   * Resets all VertexData objects.
   */
  private void resetVertexData() {
    for (final VertexData<V> data : this.vertexToVertexData.values()) {
      data.setDiscovered(false);
      data.setFinished(false);
    }
  }

  /**
   * Lightweight class storing some data for every vertex.
   *
   * @param <V>
   *          Vertex
   */
  private abstract static class VertexData<V> {
    private byte bitfield;

    private VertexData(final boolean discovered, final boolean finished) {
      this.bitfield = 0;
      setDiscovered(discovered);
      setFinished(finished);
    }

    private boolean isDiscovered() {
      return (this.bitfield & 1) == 1;
    }

    private boolean isFinished() {
      return (this.bitfield & 2) == 2;
    }

    private void setDiscovered(final boolean discovered) {
      if (discovered) {
        this.bitfield |= 1;
      } else {
        this.bitfield &= ~1;
      }
    }

    private void setFinished(final boolean finished) {
      if (finished) {
        this.bitfield |= 2;
      } else {
        this.bitfield &= ~2;
      }
    }

    abstract VertexData<V> getFinishedData();

    abstract V getVertex();
  }

  /**
   * Lightweight class storing some data for every vertex.
   *
   * @param <V>
   *          Vertex
   */
  private static final class VertexData1<V> extends VertexData<V> {
    private final VertexData<V> finishedData;

    private VertexData1(final VertexData<V> finishedData, final boolean discovered, final boolean finished) {
      super(discovered, finished);
      this.finishedData = finishedData;
    }

    @Override
    VertexData<V> getFinishedData() {
      return this.finishedData;
    }

    @Override
    V getVertex() {
      return null;
    }
  }

  /**
   * Lightweight class storing some data for every vertex.
   *
   * @param <V>
   *          Vertex
   */
  private static final class VertexData2<V> extends VertexData<V> {
    private final V vertex;

    private VertexData2(final V vertex, final boolean discovered, final boolean finished) {
      super(discovered, finished);
      this.vertex = vertex;
    }

    @Override
    VertexData<V> getFinishedData() {
      return null;
    }

    @Override
    V getVertex() {
      return this.vertex;
    }
  }
}

// End StrongConnectivityAlgorithm.java
