/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2009 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.algorithm.transforms;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ietr.dftools.algorithm.iterators.SDFIterator;
import org.ietr.dftools.algorithm.iterators.TopologicalDAGIterator;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;

// TODO: Auto-generated Javadoc
/**
 * Utility class created to gather all static methods used to remove fork/join nodes from a graph.
 *
 * @author kdesnos
 *
 */
public class ForkJoinRemover {

  /**
   * Remove the Fork and Join vertices from a SDFGraph.
   *
   * @param hsdf
   *          a single-rate {@link SDFGraph}
   */
  @SuppressWarnings("unchecked")
  public static void supprImplodeExplode(final SDFGraph hsdf) {
    /*
     * Declarations & initializations
     */
    SDFIterator iterSDFVertices;
    try {
      iterSDFVertices = new SDFIterator(hsdf);
    } catch (final InvalidExpressionException e) {
      e.printStackTrace();
      return;
    } catch (final RuntimeException e) {
      final Logger logger = WorkflowLogger.getLogger();
      logger.log(Level.SEVERE, "Explode/Implode vertices were not removed because:\n" + e.getMessage());
      return;
    }

    // Keep track of the initial number of edge to check if the right number
    // of edges were removed
    final int nbEdgeBefore = hsdf.edgeSet().size();

    // Count the number of edges added because of broadcasts and round
    // buffers
    int nbEdgeAdded = 0;

    // Be careful, DAGiterator does not seem to work well if dag is
    // modified throughout the iteration.
    // That's why we use first copy the ordered dag vertex set.
    final ArrayList<SDFAbstractVertex> sdfVertices = new ArrayList<>(hsdf.vertexSet().size());
    while (iterSDFVertices.hasNext()) {
      final SDFAbstractVertex vert = iterSDFVertices.next();
      sdfVertices.add(vert);
    }

    // Remove dag vertex of type implode explode
    // And identify source vertices (vertices without predecessors)
    final Set<SDFAbstractVertex> nonTaskVertices = new LinkedHashSet<>(); // Set
    // of
    // non-task vertices

    for (final SDFAbstractVertex vert : sdfVertices) {
      // boolean isTask = vert.getPropertyBean().getValue("vertexType")
      // .toString().equals("task");
      String vertKind = "";

      // Only task vertices have a kind
      if (true) {
        vertKind = vert.getKind();
      }

      // If the vertex is a task (an implode or explode vertex)
      if (vertKind.equals("fork") || vertKind.equals("join")) {

        if (vertKind.equals("fork") && (vert.getBase().incomingEdgesOf(vert).size() > 1)) {
          WorkflowLogger.getLogger().log(Level.SEVERE,
              "Skipped Fork vertex with multiple inputs (" + vert.getId() + ")");
          continue;
        }

        if ((vert.getBase().outgoingEdgesOf(vert).size() > 1) && (vert.getBase().incomingEdgesOf(vert).size() > 1)) {
          WorkflowLogger.getLogger().log(Level.SEVERE,
              "Skipped Fork/Join vertex with both multiple inputs and outputs (" + vert.getId() + ")");
          continue;
        }

        // Then link incoming/outgoing edges of the implode/explode
        // directly to the target/source of explosion.
        final Set<SDFEdge> outgoingEdges = vert.getBase().outgoingEdgesOf(vert);
        final Set<SDFEdge> incomingEdges = vert.getBase().incomingEdgesOf(vert);
        for (final SDFEdge incomingEdge : incomingEdges) {
          for (final SDFEdge outgoingEdge : outgoingEdges) {
            // One of this two nested loop will have only one
            // iteration. Indeed, an implode vertex only has 1
            // outgoing edge and a explode vertex only has 1
            // incoming edge.

            // // Check that the edge is linked to a task (we do
            // // not
            // // consider edges linked to send/receive)
            // if (incomingEdge.getSource().getPropertyBean()
            // .getValue("vertexType").toString()
            // .equals("task")
            // && outgoingEdge.getTarget().getPropertyBean()
            // .getValue("vertexType").toString()
            // .equals("task")) {

            // Select the edges whose properties must be
            // copied to the new edge
            SDFEdge edge;

            edge = (vertKind.equals("join")) ? incomingEdge : outgoingEdge;

            // Create the new edge that bypass the
            // explode/implode
            final SDFEdge newEdge = hsdf.addEdge(incomingEdge.getSource(), outgoingEdge.getTarget());

            newEdge.copyProperties(edge);
            newEdge.setSourceLabel(incomingEdge.getSourceLabel());
            newEdge.setTargetLabel(outgoingEdge.getTargetLabel());
            newEdge.setTargetInterface(outgoingEdge.getTargetInterface());
            newEdge.setSourceInterface(incomingEdge.getSourceInterface());

            // }
          }
        }
        nonTaskVertices.add(vert);

        // Remove the vertex from the graph
        hsdf.removeVertex(vert);
      } else if ((vert instanceof SDFBroadcastVertex) && !(vert instanceof SDFRoundBufferVertex)) {
        // WorkflowLogger.getLogger().log(Level.SEVERE, "BROADCAST");

        // If the broadcast follows an implosion duplicate the broadcast
        final Set<SDFEdge> incomingEdges = vert.getBase().incomingEdgesOf(vert);
        if (incomingEdges.size() > 1) {

          final Set<SDFEdge> outgoingEdges = vert.getBase().outgoingEdgesOf(vert);
          // Create a broadcast for each incoming edge instead of one
          // for
          // all of them
          for (final SDFEdge inEdge : incomingEdges) {
            // ignore the edge coming from implode vertex
            if (inEdge.getSource() instanceof SDFJoinVertex) {
              continue;
            }

            // Create new vertex
            final SDFBroadcastVertex newVert = (SDFBroadcastVertex) vert.clone();
            hsdf.addVertex(newVert);

            // Link it to its input
            final SDFEdge newInEdge = hsdf.addEdge(inEdge.getSource(), newVert);
            newInEdge.copyProperties(inEdge);
            newInEdge.setSourceLabel(inEdge.getSourceLabel());
            newInEdge.setTargetLabel(inEdge.getTargetLabel());
            newInEdge.setTargetInterface(inEdge.getTargetInterface());
            newInEdge.setSourceInterface(inEdge.getSourceInterface());

            // Link it to its outputs
            for (final SDFEdge outEdge : outgoingEdges) {
              // Create the new edge from broadcast to all
              // sink
              final SDFEdge newOutEdge = hsdf.addEdge(newVert, outEdge.getTarget());

              newOutEdge.copyProperties(inEdge);
              newOutEdge.setSourceLabel(outEdge.getSourceLabel());
              newOutEdge.setTargetLabel(outEdge.getTargetLabel());
              newOutEdge.setTargetInterface(outEdge.getTargetInterface());
              newOutEdge.setSourceInterface(outEdge.getSourceInterface());
            }
          }
          // Update the number of edge added
          // The -1 is because outgoing edges of broadcast
          // were already present once.
          nbEdgeAdded += (outgoingEdges.size()) * (incomingEdges.size() - 1);

          // Remove the vertex from the graph
          hsdf.removeVertex(vert);
        }
      } else if (vert instanceof SDFRoundBufferVertex) {
        // WorkflowLogger.getLogger().log(Level.SEVERE, "RoundBuffer");

        // Check if the roundbuffer precedes an explosion
        // It seems that in this case, no "explode" vertex was added :s
        final Set<SDFEdge> outgoingEdges = vert.getBase().outgoingEdgesOf(vert);
        if (outgoingEdges.size() > 1) {
          final Set<SDFEdge> incomingEdges = vert.getBase().incomingEdgesOf(vert);

          // Duplicate the roundbuffer for each outgoing edge
          for (final SDFEdge outEdge : outgoingEdges) {
            // Create new vertex
            final SDFRoundBufferVertex newVert = (SDFRoundBufferVertex) vert.clone();
            hsdf.addVertex(newVert);

            // link it to its output.
            final SDFEdge newOutEdge = hsdf.addEdge(newVert, outEdge.getTarget());

            newOutEdge.copyProperties(outEdge);
            newOutEdge.setSourceLabel(outEdge.getSourceLabel());
            newOutEdge.setTargetLabel(outEdge.getTargetLabel());
            newOutEdge.setTargetInterface(outEdge.getTargetInterface());
            newOutEdge.setSourceInterface(outEdge.getSourceInterface());

            // Link it to its inputs
            for (final SDFEdge inEdge : incomingEdges) {
              final SDFEdge newInEdge = hsdf.addEdge(inEdge.getSource(), newVert);
              newInEdge.copyProperties(outEdge);
              newInEdge.setSourceLabel(inEdge.getSourceLabel());
              newInEdge.setTargetLabel(inEdge.getTargetLabel());
              newInEdge.setTargetInterface(inEdge.getTargetInterface());
              newInEdge.setSourceInterface(inEdge.getSourceInterface());
            }
          }
          // Update the number of edge added
          nbEdgeAdded += (outgoingEdges.size() - 1) * (incomingEdges.size());

          // Remove the vertex from the graph
          hsdf.removeVertex(vert);
        }
      }
    }
    // hsdf.removeAllVertices(nonTaskVertices);
    if (nonTaskVertices.size() != ((nbEdgeBefore + nbEdgeAdded) - hsdf.edgeSet().size())) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "Expecting " + nonTaskVertices.size() + " edges removed but got "
          + ((nbEdgeBefore + nbEdgeAdded) - hsdf.edgeSet().size()) + " edges removed instead");
      WorkflowLogger.getLogger().log(Level.SEVERE,
          "Consider deactivating Implode/Explode suprression in HSDF workflow element parameters");
    } else {
      WorkflowLogger.getLogger().log(Level.INFO,
          "" + nonTaskVertices.size() + " implode/explode vertices removed (and as many edges)");
    }
  }

  /**
   * Remove the fork/join vertices from a {@link DirectedAcyclicGraph DAG}.
   *
   * @param dag
   *          the dag
   */
  public static void supprImplodeExplode(final DirectedAcyclicGraph dag) {
    // Remove Fork/join vertices

    final TopologicalDAGIterator iterDAG = new TopologicalDAGIterator(dag); // Iterator on DAG
    // vertices
    // Be careful, DAGiterator does not seem to work well if dag is
    // modified throughout the iteration.
    // That's why we use first copy the ordered dag vertex set.
    final ArrayList<DAGVertex> vertices = new ArrayList<>(dag.vertexSet().size());
    while (iterDAG.hasNext()) {
      final DAGVertex vert = iterDAG.next();
      vertices.add(vert);
    }

    final Set<DAGVertex> forkAndJoinVertices = new LinkedHashSet<>(); // Set
    // of
    // non-task
    // vertices

    for (final DAGVertex vert : vertices) {
      final boolean isTask = vert.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .equals(VertexType.TASK);
      String vertKind = "";

      // Only task vertices have a kind
      if (isTask) {
        vertKind = vert.getKind();
      }

      if (vertKind.equals(DAGForkVertex.DAG_FORK_VERTEX) || vertKind.equals(DAGJoinVertex.DAG_JOIN_VERTEX)) {
        // If the vertex is a task (an implode or explode vertex)

        // Link incoming/outgoing edges of the implode/explode
        // directly to the target/source of explosion.
        final Set<DAGEdge> outgoingEdges = vert.outgoingEdges();
        final Set<DAGEdge> incomingEdges = vert.incomingEdges();
        for (final DAGEdge incomingEdge : incomingEdges) {
          for (final DAGEdge outgoingEdge : outgoingEdges) {
            // One of this two nested loop will have only one
            // iteration. Indeed, an implode vertex only has 1
            // outgoing edge and a explode vertex only has 1
            // incoming edge.

            // Check that the edge is linked to a task (we do
            // not
            // consider edges linked to send/receive)
            if (incomingEdge.getSource().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
                .equals(VertexType.TASK)
                && outgoingEdge.getTarget().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
                    .equals(VertexType.TASK)) {

              // Select the edges whose properties must be
              // copied to the new edge
              final DAGEdge edge = (vert.getKind().equals(DAGJoinVertex.DAG_JOIN_VERTEX)) ? incomingEdge : outgoingEdge;

              // Create the new edge that bypass the
              // explode/implode
              final DAGEdge newEdge = dag.addEdge(incomingEdge.getSource(), outgoingEdge.getTarget());

              newEdge.copyProperties(edge);
              newEdge.setSourceLabel(incomingEdge.getSourceLabel());
              newEdge.setTargetLabel(outgoingEdge.getTargetLabel());
              newEdge.setPropertyValue("explodeName", vert.getName());

              // Add ExclusionObject to its implodeExplodeSet
              // implodeExplodeSet
              // .add(new MemoryExclusionVertex(newEdge));
            }
          }
        }
        // Fork/join
        forkAndJoinVertices.add(vert);
      }
    }
    // Actually remove the vertices
    dag.removeAllVertices(forkAndJoinVertices);
  }

}
