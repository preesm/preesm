package org.ietr.preesm.algorithm.transforms;

import java.util.ArrayList;
import java.util.HashMap;
//import java.awt.List;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * This class is used to perform the clusterization (loop IR builder and memory allocation). It is used to set the working memory of each non flattened
 * hierarchical actor. It builds up the loops IR for each clusterized actor. This class automates what is described in Shuvra's paper: "APGAN and RPMC:
 * Complementary Heuristics for Translation DSP Block Diagrams into Efficient Software Implementations" See section 2.1 Clustering of the paper.
 *
 * @author jhascoet
 */
public class HSDFBuildLoops {

  final Logger logger = WorkflowLogger.getLogger();

  private void p(final String s) {
    Logger logger = this.logger;
    logger.log(Level.INFO, "HSDFBuildLoops " + s);
  }

  private int getPGCD(int a, int b) {
    int r;
    while (b != 0) {
      r = a % b;
      a = b;
      b = r;
    }
    return a;
  }

  private List<SDFAbstractVertex> getPredessecors(final SDFAbstractVertex v) throws WorkflowException {
    final List<SDFAbstractVertex> l = new ArrayList<>();
    final List<SDFAbstractVertex> tmp = getInVertexs(v);
    boolean exit = false;
    do {
      tmp.removeAll(l); // avoid cycles deadlock
      l.addAll(tmp);
      final List<SDFAbstractVertex> tmp1 = new ArrayList<>();
      tmp1.addAll(tmp);
      if (tmp.isEmpty() == true) {
        exit = true;
      }
      tmp.clear();
      for (final SDFAbstractVertex e : tmp1) {
        tmp.addAll(getInVertexs(e));
      }
    } while (exit == false);
    /*
     * p("Predessecors of " + v.getName() + " are: "); for(SDFAbstractVertex e : l){ p(" - " + e.getName()); }
     */
    return l;
  }

  private List<SDFAbstractVertex> getSuccessors(final SDFAbstractVertex v) throws WorkflowException {
    final List<SDFAbstractVertex> l = new ArrayList<>();
    final List<SDFAbstractVertex> tmp = getOutVertexs(v);
    boolean exit = false;
    do {
      tmp.removeAll(l); // avoid cycles deadlock
      l.addAll(tmp);
      final List<SDFAbstractVertex> tmp1 = new ArrayList<>();
      tmp1.addAll(tmp);
      if (tmp.isEmpty() == true) {
        exit = true;
      }
      tmp.clear();
      for (final SDFAbstractVertex e : tmp1) {
        tmp.addAll(getOutVertexs(e));
      }
    } while (exit == false);
    /*
     * p("Sucessors of " + v.getName() + " are: "); for(SDFAbstractVertex e : l){ p(" - " + e.getName()); }
     */
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
    // return true;
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

  private List<SDFAbstractVertex> getInVertexs(final SDFAbstractVertex v) throws WorkflowException {
    final List<SDFAbstractVertex> inV = new ArrayList<>();
    final List<SDFEdge> inEdge = getInEdges(v);
    for (final SDFEdge i : inEdge) {
      final SDFAbstractVertex vv = i.getSource();
      if (vv instanceof SDFVertex) {
        if (vv != v) {
          inV.add(vv);
          // p("getInVertexs " + vv.getName() + " -> " + v.getName());
        } else {
          logger.log(Level.SEVERE, "HSDFBuildLoops Delays not supported when generating clustering");
          throw new WorkflowException("HSDFBuildLoops Delays not supported when generating clustering");
        }
      }
    }
    return inV;
  }

  private List<SDFAbstractVertex> getOutVertexs(final SDFAbstractVertex v) throws WorkflowException {
    final List<SDFAbstractVertex> outV = new ArrayList<>();
    final List<SDFEdge> outEdge = getOutEdges(v);
    for (final SDFEdge i : outEdge) {
      final SDFAbstractVertex vv = i.getTarget();
      if (vv instanceof SDFVertex) {
        if (vv != v) {
          outV.add(vv);
          // p("getOutVertexs " + v.getName() + " -> " + vv.getName());
        } else {
          logger.log(Level.SEVERE, "HSDFBuildLoops Delays not supported when generating clustering");
          throw new WorkflowException("HSDFBuildLoops Delays not supported when generating clustering");
        }
      }
    }
    return outV;
  }

  private SDFGraph graph = null;

  private List<SDFAbstractVertex> getClusteringVertexes(final List<SDFAbstractVertex> vertexes) throws WorkflowException {
    if (vertexes.isEmpty() == true) {
      throw new WorkflowException("getClusteringVertexes failed vertexes is empty");
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
      final List<SDFAbstractVertex> linkA = new ArrayList<>();
      linkA.addAll(inA);
      linkA.addAll(outA);

      // get first mergeable vertexes
      for (final SDFAbstractVertex v : linkA) {
        if (isMergeable(a, v)) {
          if (getInVertexs(a).contains(v) == true) {
            r.add(v);
            r.add(a);
          } else {
            r.add(a);
            r.add(v);
          }
          // p("isMergeable " + r.get(0).getName() + " " + r.get(1).getName());
          return r;
        }
      }

      if (i == (nbVertex - 1)) {
        i = -1;
        nbVertex = first;
      }
    }
    if (r.isEmpty() == true) {
      p("get ClusteringVertexes Failed");
      throw new WorkflowException("Failed getClusteringVertexes");
    }
    return r;
  }

  private int copyEdge(final SDFEdge dst, final SDFEdge src) throws WorkflowException {
    if ((dst == null) || (src == null)) {
      p("copy Edge Failed");
      throw new WorkflowException("Failed copy Edge");
    }
    dst.setDataType(src.getDataType());

    dst.setDelay(src.getDelay());

    dst.setCons(src.getCons());
    dst.setProd(src.getProd());

    dst.setSourceLabel(src.getSourceLabel());
    dst.setTargetLabel(src.getTargetLabel());

    dst.setTargetInterface(src.getTargetInterface());
    dst.setSourceInterface(src.getSourceInterface());
    return 0;
  }

  private SDFAbstractVertex generatePairedClusteredVertex(final int pgcm, final SDFAbstractVertex left, final SDFAbstractVertex right)
      throws WorkflowException, SDF4JException {

    // execution order left ---> right

    // get in edge of vertex
    final List<SDFEdge> inEdgeRight = getInEdges(right);
    final List<SDFEdge> inEdgeLeft = getInEdges(left);
    final List<SDFEdge> inEdgeVertex = new ArrayList<>();
    inEdgeVertex.addAll(inEdgeLeft); // all in edge from left go in vertex
    for (final SDFEdge e : inEdgeRight) { // all not from left in vertex (others are merged)
      if (e.getSource() != left) {
        inEdgeVertex.add(e);
        int cons = 0;
        int rvRight = 0;
        try {
          cons = e.getCons().intValue();
          rvRight = right.getNbRepeatAsInteger();
        } catch (final InvalidExpressionException ex) {
          // TODO Auto-generated catch block
          ex.printStackTrace();
        }
        e.setCons(new SDFIntEdgePropertyType((rvRight * cons) / pgcm));
      }
    }

    // get out edges of vertex
    final List<SDFEdge> outEdgeLeft = getOutEdges(left);
    final List<SDFEdge> outEdgeRight = getOutEdges(right);
    final List<SDFEdge> outEdgeVertex = new ArrayList<>();
    outEdgeVertex.addAll(outEdgeRight); // all out edge of right go out of vertex
    for (final SDFEdge e : outEdgeLeft) {
      if (e.getTarget() != right) {
        outEdgeVertex.add(e);
        int prod = 0;
        int rvLeft = 0;
        try {
          prod = e.getProd().intValue();
          rvLeft = left.getNbRepeatAsInteger();
        } catch (final InvalidExpressionException ex) {
          // TODO Auto-generated catch block
          ex.printStackTrace();
        }
        e.setProd(new SDFIntEdgePropertyType((rvLeft * prod) / pgcm));
      }
    }

    // get sources of vertex
    final List<SDFInterfaceVertex> sourcesVertex = new ArrayList<>();
    for (final SDFEdge e : inEdgeVertex) {
      sourcesVertex.add(e.getTargetInterface());
    }

    // get sinks of vertex
    final List<SDFInterfaceVertex> sinksVertex = new ArrayList<>();
    for (final SDFEdge e : outEdgeVertex) {
      sinksVertex.add(e.getSourceInterface());
    }

    // now we can build up vertex
    final SDFVertex vertex = new SDFVertex();
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

    if (this.graph.isSchedulable() == true) {
      // p("graph ok isSchedulable");
    } else {
      throw new SDF4JException("Graph not schedulable while clustering procedure (possible bug)");
    }
    return vertex;
  }

  String clustSchedString = new String();

  private void recursivePrintClustSched(final AbstractClust seq) throws SDF4JException {
    if (seq instanceof ClustVertex) {
      this.clustSchedString += "(" + Integer.toString(seq.getRepeat()) + "-" + ((ClustVertex) seq).getVertex().getName() + ")";
    } else if (seq instanceof ClustSequence) {
      this.clustSchedString += seq.getRepeat() + "(";
      for (final AbstractClust s : ((ClustSequence) seq).getSeq()) {
        recursivePrintClustSched(s);
      }
      this.clustSchedString += ")";
    } else {
      throw new SDF4JException("Error while printed clustering schedule");
    }
  }

  /**
   * Giving a clustered sequence, it prints the factorized from of the sequence of loops with associated actors.
   */
  public void printClusteringSchedule(final AbstractClust seq) throws SDF4JException {
    recursivePrintClustSched(seq);
    p(this.clustSchedString);
    this.clustSchedString = "";
  }

  private AbstractClust recursiveGetLoopClust(final AbstractClust seq, final List<AbstractClust> getLoopClusterList) throws SDF4JException {
    if (seq instanceof ClustVertex) {
      if (getLoopClusterList.contains(seq) == false) {
        getLoopClusterList.add(seq);
        return seq;
      }
    } else if (seq instanceof ClustSequence) {
      if (getLoopClusterList.contains(seq) == false) {
        getLoopClusterList.add(seq);
      }
      for (final AbstractClust s : ((ClustSequence) seq).getSeq()) {
        recursiveGetLoopClust(s, getLoopClusterList);
      }
    } else {
      throw new SDF4JException("Error while printed clustering schedule");
    }
    return null;
  }

  /**
   * Used to print the clustered sequence.
   */
  public List<AbstractClust> getLoopClust(final AbstractClust a) throws SDF4JException {
    List<AbstractClust> getLoopClusterList = null;
    getLoopClusterList = new ArrayList<>();
    getLoopClusterList.clear();
    recursiveGetLoopClust(a, getLoopClusterList);
    if (getLoopClusterList.size() == 0) {
      throw new SDF4JException("Finished !! Error while getting clustered schedule");
    }
    return getLoopClusterList;
  }

  private List<AbstractClust> getLoopClusterListV2 = null;

  /**
   * Clustering sequence getters. Initialized the sequence getters to retrieve loop sequence from a top level AbstractClust.
   */
  public AbstractClust getLoopClustFirstV2(final AbstractClust a) throws SDF4JException {
    this.getLoopClusterListV2 = new ArrayList<>();
    this.getLoopClusterListV2.clear();
    return getLoopClustV2(a);
  }

  private AbstractClust recursiveGetLoopClustV2(final AbstractClust seq, final List<AbstractClust> getLoopClusterList) throws SDF4JException {
    if (seq instanceof ClustVertex) {
      if (getLoopClusterList.contains(seq) == false) {
        getLoopClusterList.add(seq);
        return seq;
      }
    } else if (seq instanceof ClustSequence) {
      if (getLoopClusterList.contains(seq) == false) {
        getLoopClusterList.add(seq);
        return seq;
      }
      for (final AbstractClust s : ((ClustSequence) seq).getSeq()) {
        final AbstractClust ret = recursiveGetLoopClustV2(s, getLoopClusterList);
        if (ret != null) {
          return ret;
        }
      }
    } else {
      // throw new SDF4JException("recursiveGetLoopClustV2 failed");
    }
    return null;
  }

  /**
   * Gets the next sequence to print. Used in the code generator when printing hierarchical actors.
   */
  public AbstractClust getLoopClustV2(final AbstractClust a) throws SDF4JException {
    final AbstractClust ret = recursiveGetLoopClustV2(a, this.getLoopClusterListV2);
    /*
     * if(ret == null){ throw new SDF4JException("getLoopClustV2 failed ret null"); }
     */
    return ret;
  }

  /**
   * Generate the clustered IR. It takes as input the hierarchical actor (graph) and returns the sequence of loops to generate.
   */
  public AbstractClust generateClustering(final SDFGraph inGraph) throws WorkflowException, SDF4JException, InvalidExpressionException {

    // deep clone of graph SDF
    this.graph = inGraph.clone();

    // copy vertexes
    final List<SDFAbstractVertex> vertexesCpy = new ArrayList<>();
    for (final SDFAbstractVertex v : this.graph.vertexSet()) {
      if (v instanceof SDFVertex) {
        vertexesCpy.add(v);
        // p("Clustering actor " + v.getName() + " RV " + v.getNbRepeatAsInteger());
      }
    }

    final HashMap<SDFAbstractVertex, AbstractClust> clustMap = new HashMap<>();
    int pgcm = 0;
    int repLeft = 0;
    int repRight = 0;
    final int nbActor = vertexesCpy.size();
    SDFAbstractVertex lastClusteredVertex = vertexesCpy.get(0);
    for (int i = 0; i < (nbActor - 1); i++) {
      // get actor
      final List<SDFAbstractVertex> current = getClusteringVertexes(vertexesCpy);
      try {
        pgcm = getPGCD(current.get(0).getNbRepeatAsInteger(), current.get(1).getNbRepeatAsInteger());
        repLeft = current.get(0).getNbRepeatAsInteger() / pgcm;
        repRight = current.get(1).getNbRepeatAsInteger() / pgcm;
      } catch (final InvalidExpressionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      // clusterized at graph level the choosen actors (graph transfo)
      final SDFAbstractVertex clusteredVertex = generatePairedClusteredVertex(pgcm, current.get(0)/* left */, current.get(1)/* right */);
      vertexesCpy.removeAll(current);
      vertexesCpy.add(clusteredVertex);

      final AbstractClust seqLeft = clustMap.get(current.get(0)/* left */);
      final AbstractClust seqRight = clustMap.get(current.get(1)/* right */);

      // combinatory
      if ((seqLeft != null) && (seqRight != null)) {
        // existing sequences fusion here
        // create cluster vertex sequence
        final ClustSequence seq = new ClustSequence();
        seq.setSeq(new ArrayList<AbstractClust>());
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

      } else if ((seqLeft != null) && (seqRight == null)) {
        // add clust vertex sequence with existing sequence on the right
        final ClustVertex vRight = new ClustVertex();
        vRight.setRepeat(repRight);
        vRight.setVertex(current.get(1));
        // set new repeat for seqLeft
        seqLeft.setRepeat(repLeft);
        // create cluster vertex sequence
        final ClustSequence seq = new ClustSequence();
        seq.setSeq(new ArrayList<AbstractClust>());
        // set sequence
        seq.setRepeat(pgcm);
        seq.getSeq().add(seqLeft);
        seq.getSeq().add(vRight);
        clustMap.put(clusteredVertex, seq); // add hash map
        clustMap.remove(current.get(0));

      } else if ((seqLeft == null) && (seqRight != null)) {
        // add clust vertex sequence with existing sequence on the left
        final ClustVertex vLeft = new ClustVertex();
        vLeft.setRepeat(repLeft);
        vLeft.setVertex(current.get(0));
        // set new repeat for seqRight
        seqRight.setRepeat(repRight);
        // create cluster vertex sequence
        final ClustSequence seq = new ClustSequence();
        seq.setSeq(new ArrayList<AbstractClust>());
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
        seq.setSeq(new ArrayList<AbstractClust>());
        // set sequence
        seq.setRepeat(pgcm);
        seq.getSeq().add(vLeft);
        seq.getSeq().add(vRight);
        clustMap.put(clusteredVertex, seq); // add hash map
      }

      // graph.addEdge(source, sourcePort, target, targetPort)
      /*
       * for(SDFEdge e : getInEdges(clusteredVertex)){ graph.addEdge(e.getSource(), e.getTarget()); } for(SDFEdge e : getOutEdges(clusteredVertex)){
       * graph.addEdge(e.getSource(), e.getTarget()); }
       */
      // p("clusteredVertex " + clusteredVertex.getName() + " left " + current.get(0).getName() + " right " + current.get(1).getName());
      lastClusteredVertex = clusteredVertex;
    }
    // p("generateClustering ok");
    // printClusteringSchedule(clustMap.get(lastClusteredVertex));
    return clustMap.get(lastClusteredVertex);
  }

  private List<SDFAbstractVertex> getHierarchicalActor(final SDFGraph graph) {
    final List<SDFAbstractVertex> l = new ArrayList<>();
    for (final SDFAbstractVertex v : graph.vertexSet()) {
      final Object refinement = v.getPropertyBean().getValue(AbstractVertex.REFINEMENT);
      // If the actor is hierarchical
      if (refinement instanceof AbstractGraph) {
        // p("Found hierarchical " + v.getName());
        l.add(v);
        // p("getHierarchicalActor " + v.getName());
      }
      /*
       * if( v instanceof SDFInterfaceVertex){ p("SDF Interface Vertex " + v.getName()); }else if( v instanceof SDFVertex){ p("SDF Vertex " + v.getName());
       * }else{ p("SDF Abs Vertex " + v.getName()); }
       */
    }
    return l;
  }

  /**
   * This method is used in the hierarchical flattener workflow to set the internal_working memory of each actors inside the hierarchical actors (graphs) The
   * input memory and output memories of the hierarchical actor are set by the MEG. Only the internal memory is allocated here (see "working_memory" in the
   * propertyBean of the hierarchical actor).
   */
  public SDFGraph execute(final SDFGraph inputGraph) throws WorkflowException {
    // p("Executing");
    final List<SDFAbstractVertex> list = getHierarchicalActor(inputGraph);
    // p("nbHierarchicalActor " + hierarchicalActorslist.size());
    // List <List <SDFAbstractVertex>> list = new ArrayList<List <SDFAbstractVertex>>();

    /* run through all hierarchical actors of graph inputGraph */
    /*
     * for(SDFAbstractVertex v : hierarchicalActorslist) { SDFGraph graph = (SDFGraph) v.getGraphDescription(); List <SDFAbstractVertex> sortedVertex =
     * sortVertex(graph.vertexSet(), v); list.add(sortedVertex); }
     */

    /* allocate internal working buffer for the hierarchical actor */
    for (final SDFAbstractVertex g : list) {
      final SDFGraph graph = ((SDFGraph) g.getGraphDescription()).clone();
      final IbsdfFlattener flattener = new IbsdfFlattener(graph, 10);
      SDFGraph resultGraph = null;
      final List<SDFEdge> allocEdge = new ArrayList<>();
      try {
        flattener.flattenGraph();
        resultGraph = flattener.getFlattenedGraph();
      } catch (final SDF4JException e) {
        throw (new WorkflowException(e.getMessage()));
      }
      try {
        resultGraph.validateModel(WorkflowLogger.getLogger());
      } catch (final SDF4JException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } // compute repetition vectors
      try {
        if (resultGraph.isSchedulable() == false) {
          throw (new WorkflowException("HSDF Build Loops generate clustering: Graph not schedulable"));
        }
      } catch (final SDF4JException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      final List<SDFEdge> edgeUpperGraph = new ArrayList<>();
      for (final SDFAbstractVertex v : resultGraph.vertexSet()) {
        if (v instanceof SDFInterfaceVertex) {
          // v.getAssociatedEdge(((SDFInterfaceVertex)v).getSinks())
          for (final SDFInterfaceVertex i : v.getSources()) {
            edgeUpperGraph.add(v.getAssociatedEdge(i));
          }
          for (final SDFInterfaceVertex i : v.getSinks()) {
            edgeUpperGraph.add(v.getAssociatedEdge(i));
          }
        }
      }

      int bufSize = 0;
      // int nbWorkingBufferAllocated = 0;
      for (final SDFAbstractVertex v : resultGraph.vertexSet()) {
        if (v instanceof SDFVertex) {
          final List<SDFEdge> edge = new ArrayList<>();
          edge.addAll(getInEdges(v));
          edge.addAll(getOutEdges(v));
          for (final SDFEdge e : edge) {
            if ((allocEdge.contains(e)/* already visited */ == false) && (edgeUpperGraph.contains(e) /* allocation by Karol */ == false)) {
              int nbRep = 0;
              try {
                nbRep = Integer.parseInt(e.getTarget().getNbRepeat().toString());
              } catch (final NumberFormatException | InvalidExpressionException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
              }
              int mem = 0;
              try {
                mem = nbRep * e.getCons().intValue();
              } catch (final InvalidExpressionException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
              }
              bufSize += mem;
              // nbWorkingBufferAllocated++;
              allocEdge.add(e);
            }
          }
        }
      }
      g.getPropertyBean().setValue("working_memory", new Integer(bufSize));
      // p("Internal working memory computation " + g.getName() + " number of allocation " + nbWorkingBufferAllocated + " byte allocated " + bufSize);
    }
    return inputGraph;
  }
}