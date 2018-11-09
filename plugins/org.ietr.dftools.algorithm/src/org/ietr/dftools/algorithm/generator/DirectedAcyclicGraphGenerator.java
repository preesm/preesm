/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.generator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;

/**
 * Generate a random SDF close to a Directed Acyclic graph. The generated Graph is acyclic and directed and have unitary
 * production and consumption on edge.
 *
 * @author pthebault
 *
 */

public class DirectedAcyclicGraphGenerator {
  // ~ Static fields/initializers
  // ---------------------------------------------

  /** Static field containing all the instances of this class. */

  private static final List<DirectedAcyclicGraphGenerator> adapters = new ArrayList<>();

  // graph

  // ~ Instance fields
  // --------------------------------------------------------

  /**
   * Creates a new RandomGraph.
   */
  public DirectedAcyclicGraphGenerator() {
    DirectedAcyclicGraphGenerator.adapters.add(this);
  }

  /**
   * Creates an acyclic random graph with a number of vertices fix by the parameter nbVertex. The number of sources and
   * sinks of each vertex and the production and consumption are randomly set. The number of minimum input vertices is
   * set to 1
   *
   * @param nbVertex
   *          is the number of vertices to create in the graph
   * @param minInDegree
   *          is the minimum sinks of each vertex
   * @param maxInDegree
   *          is the maximum sinks of each vertex
   * @param minOutDegree
   *          is the minimum sources of each vertex
   * @param maxOutDegree
   *          is the maximum sources of each vertex
   *
   *
   * @return the created graph
   *
   */
  public SDFGraph createAcyclicRandomGraph(final int nbVertex, final int minInDegree, final int maxInDegree,
      final int minOutDegree, final int maxOutDegree) {
    return createAcyclicRandomGraph(nbVertex, minInDegree, maxInDegree, minOutDegree, maxOutDegree, 1);
  }

  /**
   * Creates an acyclic random graph with a number of vertices fix by the parameter nbVertex. The number of sources and
   * sinks of each vertex and the production and consumption are randomly set.
   *
   * @param nbVertex
   *          is the number of vertices to create in the graph
   * @param minInDegree
   *          is the minimum sinks of each vertex
   * @param maxInDegree
   *          is the maximum sinks of each vertex
   * @param minOutDegree
   *          is the minimum sources of each vertex
   * @param maxOutDegree
   *          is the maximum sources of each vertex
   * @param nbSensors
   *          Exact number of input vertices in the graph
   *
   * @return the created graph
   *
   */

  public SDFGraph createAcyclicRandomGraph(final int nbVertex, final int minInDegree, final int maxInDegree,
      final int minOutDegree, final int maxOutDegree, final int nbSensors) {
    // Number of Vertex created on the
    int nbVertexgraph = 0;

    final int[] nbSinksVertex = new int[nbVertex];
    final int[] nbSourcesVertex = new int[nbVertex];
    final int[][] createdEdge = new int[nbVertex][nbVertex];
    int nbSinks = 0;
    int nbSources = 0;

    final SDFVertex[] arrayVertex = new SDFVertex[nbVertex];
    final List<SDFEdge> vecEdge = new ArrayList<>();

    // Create an SDF Graph
    final SDFGraph graph = new SDFGraph();

    // Create graph with nbVertex Vertex
    while (nbVertexgraph < nbVertex) {

      // Add a new vertex to the graph
      final SDFVertex vertex = new SDFVertex();
      vertex.setName("Vertex_" + (nbVertexgraph + 1));
      arrayVertex[nbVertexgraph] = vertex;
      graph.addVertex(arrayVertex[nbVertexgraph]);
      // Choose a random number of sinks for the new vertex
      int max = Math.min(maxOutDegree, nbVertex - nbVertexgraph - 1);
      int min = Math.min(max, minOutDegree);
      nbSourcesVertex[nbVertexgraph] = (max - min == 0) ? min : min + new SecureRandom().nextInt(max - min);

      // Choose a random number of sources for the new vertex
      max = Math.min(maxInDegree, nbVertexgraph);
      min = Math.min(max, minInDegree);
      nbSinksVertex[nbVertexgraph] = (max - min == 0) ? min : min + new SecureRandom().nextInt(max - min);

      nbSinks += nbSinksVertex[nbVertexgraph];
      nbSources += nbSourcesVertex[nbVertexgraph];
      // If Not the first
      if ((nbVertexgraph >= nbSensors) && (nbSinks != 0) && (nbSources != 0) && nbSinksVertex[nbVertexgraph] > 0) {
        int randout;
        do {
          randout = (new SecureRandom().nextInt(nbVertexgraph));
        } while (nbSourcesVertex[randout] == 0);
        final SDFEdge edge = graph.addEdgeWithInterfaces(arrayVertex[randout], arrayVertex[nbVertexgraph]);

        vecEdge.add(edge);
        // Set production and consumption

        vecEdge.get(vecEdge.size() - 1).setProd(new LongEdgePropertyType(1));

        vecEdge.get(vecEdge.size() - 1).setCons(new LongEdgePropertyType(1));
        createdEdge[randout][nbVertexgraph] = nbVertexgraph;
        nbSourcesVertex[randout]--;
        nbSinksVertex[nbVertexgraph]--;
        nbSinks--;
        nbSources--;
      }
      nbVertexgraph++;
    }

    // Create Edge

    int highestid = nbVertex - 1;
    int nbEdge = nbVertexgraph - 1;

    while ((nbSources != 0) && (nbSinks != 0)) {
      final int randout = new SecureRandom().nextInt(highestid);
      final int randin = randout + 1 + new SecureRandom().nextInt(nbVertex - randout - 1);
      if ((nbSinksVertex[randin] != 0) && (createdEdge[randout][randin] == 0) && (nbSourcesVertex[randout] != 0)) {
        createdEdge[randout][randin] = nbEdge + 1;
        final SDFEdge edge = graph.addEdgeWithInterfaces(arrayVertex[randout], arrayVertex[randin]);
        vecEdge.add(edge);
        // Set production and consumption

        vecEdge.get(vecEdge.size() - 1).setProd(new LongEdgePropertyType(1));
        vecEdge.get(vecEdge.size() - 1).setCons(new LongEdgePropertyType(1));

        nbSinksVertex[randin]--;
        nbSinks--;
        nbEdge++;
        nbSourcesVertex[randout]--;
        nbSources--;
      }

      if (createdEdge[highestid - 1][highestid] != 0) {
        while (nbSourcesVertex[highestid - 1] > 0) {
          nbSourcesVertex[highestid - 1]--;
          nbSources--;
        }
      }
      if (nbSourcesVertex[highestid - 1] == 0) {
        while (nbSinksVertex[highestid] > 0) {
          nbSinksVertex[highestid]--;
          nbSinks--;
        }
      }
      if (nbSinksVertex[highestid] == 0) {
        while (nbSourcesVertex[highestid - 1] > 0) {
          nbSourcesVertex[highestid - 1]--;
          nbSources--;
        }
        highestid--;
      }

    }

    return graph;
  }

}
