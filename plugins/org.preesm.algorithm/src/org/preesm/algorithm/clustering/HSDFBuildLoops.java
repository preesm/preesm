/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2017)
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
package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.preesm.algorithm.model.types.LongEdgePropertyType;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.SimulationInfo;
import org.preesm.model.slam.Design;

/**
 * This class is used to perform the clusterization (loop IR builder and memory allocation). It is used to set the
 * working memory of each non flattened hierarchical actor. It builds up the loops IR for each clusterized actor. This
 * class automates what is described in Shuvra's paper: "APGAN and RPMC: Complementary Heuristics for Translation DSP
 * Block Diagrams into Efficient Software Implementations" See section 2.1 Clustering of the paper.
 *
 * @author jhascoet
 */
public class HSDFBuildLoops {

  private final Logger logger = PreesmLogger.getLogger();

  private final SimulationInfo simulationInfo;

  /**
   * Build loops.
   *
   * @param scenario
   *          the scenario to pass
   * @param architecture
   *          the architecture to pass
   */
  public HSDFBuildLoops(final Scenario scenario, final Design architecture) {
    this.simulationInfo = scenario.getSimulationInfo();
  }

  private void p(final String s) {
    final String message = "HSDFBuildLoops " + s;
    this.logger.log(Level.INFO, message);
  }

  private List<SDFAbstractVertex> getPredessecors(final SDFAbstractVertex v) {
    final List<SDFAbstractVertex> l = new ArrayList<>();
    final List<SDFAbstractVertex> tmp = getInVertexs(v);
    boolean exit = false;
    do {
      // avoid cycles deadlock
      tmp.removeAll(l);
      l.addAll(tmp);
      final List<SDFAbstractVertex> tmp1 = new ArrayList<>(tmp);
      if (tmp.isEmpty()) {
        exit = true;
      }
      tmp.clear();
      for (final SDFAbstractVertex e : tmp1) {
        tmp.addAll(getInVertexs(e));
      }
    } while (!exit);
    return l;
  }

  private List<SDFAbstractVertex> getSuccessors(final SDFAbstractVertex v) {
    final List<SDFAbstractVertex> l = new ArrayList<>();
    final List<SDFAbstractVertex> tmp = getOutVertexs(v);
    boolean exit = false;
    do {
      tmp.removeAll(l); // avoid cycles deadlock
      l.addAll(tmp);
      final List<SDFAbstractVertex> tmp1 = new ArrayList<>(tmp);
      if (tmp.isEmpty()) {
        exit = true;
      }
      tmp.clear();
      for (final SDFAbstractVertex e : tmp1) {
        tmp.addAll(getOutVertexs(e));
      }
    } while (!exit);
    return l;
  }

  private boolean isMergeable(final SDFAbstractVertex a, final SDFAbstractVertex b) {
    final List<SDFAbstractVertex> predA = getPredessecors(a);
    final List<SDFAbstractVertex> predB = getPredessecors(b);
    final List<SDFAbstractVertex> succA = getSuccessors(a);
    final List<SDFAbstractVertex> succB = getSuccessors(b);
    predA.retainAll(succB);
    predB.retainAll(succA);
    return predA.isEmpty() && predB.isEmpty();
  }

  private List<SDFEdge> getInEdges(final SDFAbstractVertex v) {
    final List<SDFEdge> inEdge = new ArrayList<>();
    for (final SDFInterfaceVertex i : v.getSources()) {
      inEdge.add(v.getAssociatedEdge(i));
    }
    return inEdge;
  }

  private List<SDFEdge> getOutEdges(final SDFAbstractVertex v) {
    final List<SDFEdge> outEdge = new ArrayList<>();
    for (final SDFInterfaceVertex i : v.getSinks()) {
      outEdge.add(v.getAssociatedEdge(i));
    }
    return outEdge;
  }

  private List<SDFAbstractVertex> getInVertexs(final SDFAbstractVertex v) {
    final List<SDFAbstractVertex> inV = new ArrayList<>();
    final List<SDFEdge> inEdge = getInEdges(v);
    for (final SDFEdge i : inEdge) {
      final SDFAbstractVertex vv = i.getSource();
      if (vv instanceof SDFVertex) {
        if (vv == v) {
          throw new PreesmRuntimeException("HSDFBuildLoops Delays not supported when generating clustering");
        }
        inV.add(vv);
      }
    }
    return inV;
  }

  private List<SDFAbstractVertex> getOutVertexs(final SDFAbstractVertex v) {
    final List<SDFAbstractVertex> outV = new ArrayList<>();
    final List<SDFEdge> outEdge = getOutEdges(v);
    for (final SDFEdge i : outEdge) {
      final SDFAbstractVertex vv = i.getTarget();
      if (vv instanceof SDFVertex) {
        if (vv == v) {
          throw new PreesmRuntimeException("HSDFBuildLoops Delays not supported when generating clustering");
        }
        outV.add(vv);
      }
    }
    return outV;
  }

  private SDFGraph graph = null;

  private List<SDFAbstractVertex> getClusteringVertexes(final List<SDFAbstractVertex> vertexes) {
    if (vertexes.isEmpty()) {
      throw new PreesmRuntimeException("getClusteringVertexes failed vertexes is empty");
    }
    final List<SDFAbstractVertex> r = new ArrayList<>();
    if (vertexes.size() == 1) {
      r.add(vertexes.get(0));
      return r;
    }

    // get in and out vertexes
    final int first = vertexes.size() - 1;
    int nbVertex = vertexes.size();
    for (int i = first; i < nbVertex; i++) {
      final SDFAbstractVertex a = vertexes.get(i);
      final List<SDFAbstractVertex> inA = getInVertexs(a);
      final List<SDFAbstractVertex> outA = getOutVertexs(a);
      final List<SDFAbstractVertex> linkA = new ArrayList<>(inA);
      linkA.addAll(outA);

      // get first mergeable vertexes
      for (final SDFAbstractVertex v : linkA) {
        if (isMergeable(a, v)) {
          if (getInVertexs(a).contains(v)) {
            r.add(v);
            r.add(a);
          } else {
            r.add(a);
            r.add(v);
          }
          return r;
        }
      }

      if (i == (nbVertex - 1)) {
        i = -1;
        nbVertex = first;
      }
    }
    if (r.isEmpty()) {
      throw new PreesmRuntimeException("Failed getClusteringVertexes");
    }
    return r;
  }

  private int copyEdge(final SDFEdge dst, final SDFEdge src) {
    if ((dst == null) || (src == null)) {
      throw new PreesmRuntimeException("Failed copy Edge");
    }
    dst.setDataType(src.getDataType());

    dst.setDelay(src.getDelay());

    dst.setCons(src.getCons());
    dst.setProd(src.getProd());

    dst.setSourceLabel(src.getSourceLabel());
    dst.setTargetLabel(src.getTargetLabel());

    dst.setTargetInterface(src.getTargetInterface());
    dst.setSourceInterface(src.getSourceInterface());

    dst.copyProperties(src);
    return 0;
  }

  private SDFAbstractVertex generatePairedClusteredVertex(final long pgcm, final SDFAbstractVertex left,
      final SDFAbstractVertex right) {

    // execution order left ---> right

    // get in edge of vertex
    final List<SDFEdge> inEdgeRight = getInEdges(right);
    final List<SDFEdge> inEdgeLeft = getInEdges(left);
    final List<SDFEdge> inEdgeVertex = new ArrayList<>(inEdgeLeft);
    for (final SDFEdge e : inEdgeRight) { // all not from left in vertex (others are merged)
      if (e.getSource() != left) {
        inEdgeVertex.add(e);
        long cons;
        long rvRight;
        cons = e.getCons().longValue();
        rvRight = right.getNbRepeatAsLong();
        e.setCons(new LongEdgePropertyType((rvRight * cons) / pgcm));
      }
    }

    // get out edges of vertex
    final List<SDFEdge> outEdgeLeft = getOutEdges(left);
    final List<SDFEdge> outEdgeRight = getOutEdges(right);
    final List<SDFEdge> outEdgeVertex = new ArrayList<>(outEdgeRight);
    for (final SDFEdge e : outEdgeLeft) {
      if (e.getTarget() != right) {
        outEdgeVertex.add(e);
        long prod;
        long rvLeft;
        prod = e.getProd().longValue();
        rvLeft = left.getNbRepeatAsLong();
        e.setProd(new LongEdgePropertyType((rvLeft * prod) / pgcm));
      }
    }

    // get sources of vertex
    final List<SDFSourceInterfaceVertex> sourcesVertex = new ArrayList<>();
    for (final SDFEdge e : inEdgeVertex) {
      sourcesVertex.add(e.getTargetInterface());
    }

    // get sinks of vertex
    final List<SDFSinkInterfaceVertex> sinksVertex = new ArrayList<>();
    for (final SDFEdge e : outEdgeVertex) {
      sinksVertex.add(e.getSourceInterface());
    }

    // now we can build up vertex
    final SDFVertex vertex = new SDFVertex(null);
    vertex.setName(left.getName() + "_" + right.getName());
    vertex.setNbRepeat(pgcm);
    vertex.setSinks(sinksVertex);
    vertex.setSources(sourcesVertex);
    this.graph.addVertex(vertex);

    for (final SDFEdge e : inEdgeVertex) {
      final SDFEdge newEdge = this.graph.addEdge(e.getSource(), vertex);
      copyEdge(newEdge, e);
    }

    for (final SDFEdge e : outEdgeVertex) {
      final SDFEdge newEdge = this.graph.addEdge(vertex, e.getTarget());
      copyEdge(newEdge, e);
    }

    // remove edges and vertexes
    for (final SDFEdge e : inEdgeVertex) {
      this.graph.removeEdge(e);
    }
    for (final SDFEdge e : outEdgeVertex) {
      this.graph.removeEdge(e);
    }
    this.graph.removeVertex(left);
    this.graph.removeVertex(right);

    if (!this.graph.isSchedulable()) {
      throw new PreesmRuntimeException("Graph not schedulable while clustering procedure (possible bug)");
    }
    return vertex;
  }

  private String clustSchedString;

  private void recursivePrintClustSched(final AbstractClust seq) {
    if (seq instanceof final ClustVertex clustVertex) {
      this.clustSchedString += "(" + Long.toString(seq.getRepeat()) + "-" + clustVertex.getVertex().getName() + ")";
    } else if (seq instanceof final ClustSequence clustSequence) {
      this.clustSchedString += seq.getRepeat() + "(";
      for (final AbstractClust s : clustSequence.getSeq()) {
        recursivePrintClustSched(s);
      }
      this.clustSchedString += ")";
    } else {
      throw new PreesmRuntimeException("Error while printed clustering schedule");
    }
  }

  /**
   * Giving a clustered sequence, it prints the factorized from of the sequence of loops with associated actors.
   */
  private void printClusteringSchedule(final AbstractClust seq) {
    recursivePrintClustSched(seq);
    p(this.clustSchedString);
    this.clustSchedString = "";
  }

  private List<AbstractClust> getLoopClusterListV2 = null;

  /**
   * Clustering sequence getters. Initialized the sequence getters to retrieve loop sequence from a top level
   * AbstractClust.
   */
  public AbstractClust getLoopClustFirstV2(final AbstractClust a) {
    this.getLoopClusterListV2 = new ArrayList<>();
    this.getLoopClusterListV2.clear();
    return getLoopClustV2(a);
  }

  private AbstractClust recursiveGetLoopClustV2(final AbstractClust seq, final List<AbstractClust> getLoopClusterList) {
    if (seq instanceof ClustVertex) {
      if (!getLoopClusterList.contains(seq)) {
        getLoopClusterList.add(seq);
        return seq;
      }
    } else if (seq instanceof final ClustSequence clustSequence) {
      if (!getLoopClusterList.contains(clustSequence)) {
        getLoopClusterList.add(seq);
        return seq;
      }
      for (final AbstractClust s : clustSequence.getSeq()) {
        final AbstractClust ret = recursiveGetLoopClustV2(s, getLoopClusterList);
        if (ret != null) {
          return ret;
        }
      }
    }
    return null;
  }

  /**
   * Gets the next sequence to print. Used in the code generator when printing hierarchical actors.
   */
  public AbstractClust getLoopClustV2(final AbstractClust a) {
    return recursiveGetLoopClustV2(a, this.getLoopClusterListV2);
  }

  /**
   * Generate the clustered IR. It takes as input the hierarchical actor (graph) and returns the sequence of loops to
   * generate.
   */
  private AbstractClust generateClustering(final SDFGraph inGraph) {

    // deep clone of graph SDF
    this.graph = inGraph.copy();

    // copy vertexes
    final List<SDFAbstractVertex> vertexesCpy = copyVertices();

    final Map<SDFAbstractVertex, AbstractClust> clustMap = new LinkedHashMap<>();
    long pgcm = 0;
    long repLeft = 0;
    long repRight = 0;
    final int nbActor = vertexesCpy.size();
    SDFAbstractVertex lastClusteredVertex = vertexesCpy.get(0);
    for (int i = 0; i < (nbActor - 1); i++) {
      // get actor
      final List<SDFAbstractVertex> current = getClusteringVertexes(vertexesCpy);
      pgcm = MathFunctionsHelper.gcd(current.get(0).getNbRepeatAsLong(), current.get(1).getNbRepeatAsLong());
      repLeft = current.get(0).getNbRepeatAsLong() / pgcm;
      repRight = current.get(1).getNbRepeatAsLong() / pgcm;

      // clusterized at graph level the choosen actors (graph transfo)
      final SDFAbstractVertex clusteredVertex = generatePairedClusteredVertex(pgcm, current.get(0)/* left */,
          current.get(1)/* right */);
      vertexesCpy.removeAll(current);
      vertexesCpy.add(clusteredVertex);

      final AbstractClust seqLeft = clustMap.get(current.get(0)/* left */);
      final AbstractClust seqRight = clustMap.get(current.get(1)/* right */);

      // combinatory
      if ((seqLeft != null) && (seqRight != null)) {
        // existing sequences fusion here
        // create cluster vertex sequence
        final ClustSequence seq = new ClustSequence();
        seq.setSeq(new ArrayList<>());
        // set new repeat for seqRight
        seqRight.setRepeat(repRight);
        // set new repeat for seqLeft
        seqLeft.setRepeat(repLeft);
        // set sequence
        seq.setRepeat(pgcm);
        seq.getSeq().add(seqLeft);
        seq.getSeq().add(seqRight);
        clustMap.put(clusteredVertex, seq); // add hash map
        clustMap.remove(current.get(0));
        clustMap.remove(current.get(1));

      } else if (seqLeft != null) {
        // add clust vertex sequence with existing sequence on the right
        final ClustVertex vRight = new ClustVertex();
        vRight.setRepeat(repRight);
        vRight.setVertex(current.get(1));
        // set new repeat for seqLeft
        seqLeft.setRepeat(repLeft);
        // create cluster vertex sequence
        final ClustSequence seq = new ClustSequence();
        seq.setSeq(new ArrayList<>());
        // set sequence
        seq.setRepeat(pgcm);
        seq.getSeq().add(seqLeft);
        seq.getSeq().add(vRight);
        clustMap.put(clusteredVertex, seq); // add hash map
        clustMap.remove(current.get(0));

      } else if (seqRight != null) {
        // add clust vertex sequence with existing sequence on the left
        final ClustVertex vLeft = new ClustVertex();
        vLeft.setRepeat(repLeft);
        vLeft.setVertex(current.get(0));
        // set new repeat for seqRight
        seqRight.setRepeat(repRight);
        // create cluster vertex sequence
        final ClustSequence seq = new ClustSequence();
        seq.setSeq(new ArrayList<>());
        // set sequence
        seq.setRepeat(pgcm);
        seq.getSeq().add(vLeft);
        seq.getSeq().add(seqRight);
        clustMap.put(clusteredVertex, seq); // add hash map
        clustMap.remove(current.get(1));

      } else { // (seqLeft == null && seqRight == null)
        // new sequence (never visited)
        final ClustVertex vLeft = new ClustVertex();
        vLeft.setRepeat(repLeft);
        vLeft.setVertex(current.get(0));
        final ClustVertex vRight = new ClustVertex();
        vRight.setRepeat(repRight);
        vRight.setVertex(current.get(1));
        // create cluster vertex sequence
        final ClustSequence seq = new ClustSequence();
        seq.setSeq(new ArrayList<>());
        // set sequence
        seq.setRepeat(pgcm);
        seq.getSeq().add(vLeft);
        seq.getSeq().add(vRight);
        clustMap.put(clusteredVertex, seq); // add hash map
      }

      lastClusteredVertex = clusteredVertex;
    }
    p("HSDF LoopBuider Hierarchical actor: " + inGraph.getName());
    printClusteringSchedule(clustMap.get(lastClusteredVertex));
    return clustMap.get(lastClusteredVertex);
  }

  private List<SDFAbstractVertex> copyVertices() {
    final List<SDFAbstractVertex> vertexesCpy = new ArrayList<>();
    for (final SDFAbstractVertex v : this.graph.vertexSet()) {
      if (v instanceof SDFVertex) {
        vertexesCpy.add(v);
      }
      if (v instanceof SDFBroadcastVertex) {
        vertexesCpy.add(v);
      }
      if (v instanceof SDFRoundBufferVertex) {
        vertexesCpy.add(v);
      }
    }
    return vertexesCpy;
  }

  private List<SDFAbstractVertex> getHierarchicalActor(final SDFGraph graph) {
    final List<SDFAbstractVertex> l = new ArrayList<>();
    for (final SDFAbstractVertex v : graph.vertexSet()) {
      final Object refinement = v.getPropertyBean().getValue(AbstractVertex.REFINEMENT_LITERAL);
      // If the actor is hierarchical
      if (refinement instanceof AbstractGraph) {
        l.add(v);
      }
    }
    return l;
  }

  private long getNaiveWorkingMemAlloc(final SDFGraph resultGraph) {
    final List<SDFEdge> allocEdge = new ArrayList<>();
    // getting edges that are allocated by Karol
    final List<SDFEdge> edgeUpperGraph = new ArrayList<>();
    for (final SDFAbstractVertex v : resultGraph.vertexSet()) {
      if (v instanceof SDFInterfaceVertex) {
        edgeUpperGraph.addAll(resultGraph.incomingEdgesOf(v));
        edgeUpperGraph.addAll(resultGraph.outgoingEdgesOf(v));
      }
    }

    long bufSize = 0;
    for (final SDFAbstractVertex v : resultGraph.vertexSet()) {
      bufSize += getBufSize(allocEdge, edgeUpperGraph, v);
    }
    return bufSize;
  }

  private long getBufSize(final List<SDFEdge> allocEdge, final List<SDFEdge> edgeUpperGraph,
      final SDFAbstractVertex v) {
    final List<SDFEdge> edge = new ArrayList<>(getInEdges(v));
    edge.addAll(getOutEdges(v));
    long bufSize = 0;
    for (final SDFEdge e : edge) {
      if ((!allocEdge.contains(e)/* already visited */) && (!edgeUpperGraph.contains(e) /* allocation by Karol */)) {
        long mem = 0;
        if (v instanceof SDFVertex) {
          long nbRep = 0;

          try {
            nbRep = e.getTarget().getNbRepeat();
          } catch (final NumberFormatException ex) {
            throw new PreesmRuntimeException("Internal Memory allocation failed for actor " + v.getName(), ex);
          }
          mem = nbRep * e.getCons().longValue();
        } else if ((v instanceof SDFBroadcastVertex) || (v instanceof SDFRoundBufferVertex)) {
          mem += e.getCons().longValue() * v.getNbRepeatAsLong();
        } else {
          throw new PreesmRuntimeException(
              "Internal Memory allocation failed for actor " + v.getName() + " unsupported special actor");
        }

        final long sizeType = simulationInfo.getDataTypeSizeInBit(e.getDataType().toString());

        bufSize += mem * sizeType;
        allocEdge.add(e);
      }
    }
    return bufSize;
  }

  /**
   * This method is used in the hierarchical flattener workflow to set the internal_working memory of each actors inside
   * the hierarchical actors (graphs) The input memory and output memories of the hierarchical actor are set by the MEG.
   * Only the internal memory is allocated here (see "working_memory" in the propertyBean of the hierarchical actor).
   */
  public SDFGraph execute(final SDFGraph inputGraph) {
    final List<SDFAbstractVertex> list = getHierarchicalActor(inputGraph);

    /* allocate internal working buffer for the hierarchical actor */
    for (final SDFAbstractVertex g : list) {
      final SDFGraph graphClone = ((SDFGraph) g.getGraphDescription()).copy();
      final IbsdfFlattener flattener = new IbsdfFlattener(graphClone, 10);
      SDFGraph resultGraph = null;
      try {
        flattener.flattenGraph();
        resultGraph = flattener.getFlattenedGraph();
      } catch (final PreesmException e) {
        throw new PreesmRuntimeException(e.getMessage());
      }
      try {
        resultGraph.validateModel();
      } catch (final PreesmException e) {
        throw new PreesmRuntimeException("execute failed", e);
      } // compute repetition vectors
      try {
        if (!resultGraph.isSchedulable()) {
          throw new PreesmRuntimeException("HSDF Build Loops generate clustering: Graph not schedulable");
        }
      } catch (final PreesmException e) {
        throw new PreesmRuntimeException("execute failed", e);
      }

      final AbstractClust clust = generateClustering(resultGraph);
      if (clust == null) {
        throw new PreesmRuntimeException(
            "HSDF Build Loops generate clustering: Failed to cluster the hierarchical actor " + resultGraph.getName());
      }
      g.getGraphDescription().getPropertyBean().setValue(MapperDAG.CLUSTERED_VERTEX, clust);

      final long bufSize = getNaiveWorkingMemAlloc(resultGraph);

      g.getPropertyBean().setValue("working_memory", bufSize);
      p("Internal Working Memory Graph " + g.getName() + " has allocated " + bufSize + " bits");
    }
    return inputGraph;
  }
}
