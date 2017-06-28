package org.ietr.preesm.throughput.transformers;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;

/**
 * @author hderoui
 *
 *         This class implements srSDF conversions algorithms : srSDF to HSDF graph and DAG.
 */
public abstract class SrSDFTransformer {

  /**
   * Converts a srSDF graph to an HSDF graph
   * 
   * @param srSDF
   *          graph
   * @return HSDF graph
   */
  public static SDFGraph convertToHSDF(SDFGraph srSDF) {
    // clone the srSDF
    SDFGraph hsdf_graph = srSDF.clone();
    hsdf_graph.setName(srSDF.getName() + "_HSDF");

    // for each edge set cons=prod=1 and delay=delay/prod
    for (SDFEdge edge : hsdf_graph.edgeSet()) {
      int delay = edge.getDelay().intValue() / edge.getProd().intValue();
      edge.setProd(new SDFIntEdgePropertyType(1));
      edge.setCons(new SDFIntEdgePropertyType(1));
      edge.setDelay(new SDFIntEdgePropertyType(delay));
    }

    return hsdf_graph;
  }

  /**
   * Converts a srSDF graph to a DAG graph
   * 
   * @param srSDF
   *          graph
   * @return HSDF graph
   */
  public static SDFGraph convertToDAG(SDFGraph srSDF) {
    // clone the srSDF
    SDFGraph dag = srSDF.clone();
    dag.setName(srSDF.getName() + "_DAG");

    // save the list of edges
    ArrayList<SDFEdge> edgeList = new ArrayList<SDFEdge>(dag.edgeSet().size());
    for (SDFEdge edge : dag.edgeSet()) {
      edgeList.add(edge);
    }

    // remove edges with delays
    for (SDFEdge edge : edgeList) {
      if (edge.getDelay().intValue() != 0) {
        dag.removeEdge(edge);
      }
    }

    return dag;
  }

}
