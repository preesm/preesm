package org.ietr.preesm.throughput.transformers;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.parsers.GraphStructureHelper;

/**
 * @author hderoui
 *
 *         This class implements SDF conversions algorithms : SDF to srSDF, HSDF and DAG.
 */
public abstract class SDFTransformer {

  /**
   * Converts an SDF graph to an HSDF graph : SDF => HSDF
   * 
   * @param SDF
   *          graph
   * @return HSDF graph
   */
  public static SDFGraph convertToHSDF(SDFGraph SDF) {
    // create the SRSDF
    SDFGraph hsdf_graph = new SDFGraph();
    hsdf_graph.setName(SDF.getName() + "_HSDF");

    // create actors instances
    for (SDFAbstractVertex a : SDF.vertexSet()) {
      for (int i = 1; i <= a.getNbRepeatAsInteger(); i++) {
        // create an instance a_i of the actor a
        GraphStructureHelper.addActor(hsdf_graph, a.getName() + "_" + i, (SDFGraph) a.getGraphDescription(), 1, null, null, a);
        // SDFVertex a_i = new SDFVertex(hsdf_graph);
        // a_i.setId(a.getId() + "_" + i);
        // a_i.setName(a.getName() + "_" + i);
        // a_i.setNbRepeat(1);
        // a_i.setPropertyValue("baseActor", a);
        // hsdf_graph.addVertex(a_i);
        //
        // // if a is hierarchical add the subgraph to the instance
        // if (a.getGraphDescription() != null) {
        // a_i.setGraphDescription(a.getGraphDescription());
        // }

      }
    }

    // creates the edges
    for (SDFEdge e : SDF.edgeSet()) {
      for (int i = 1; i <= e.getSource().getNbRepeatAsInteger(); i++) {
        for (int k = 1; k <= e.getProd().intValue(); k += 1) {
          // compute the target actor instance id, and delay
          int j = ((e.getDelay().intValue() + ((i - 1) * e.getProd().intValue()) + k - 1) % (e.getCons().intValue() * e.getTarget().getNbRepeatAsInteger()))
              / e.getCons().intValue() + 1;
          int d = (int) Math
              .floor((e.getDelay().intValue() + ((i - 1) * e.getProd().intValue()) + k - 1) / (e.getCons().intValue() * e.getTarget().getNbRepeatAsInteger()));

          GraphStructureHelper.addEdge(hsdf_graph, e.getSource().getName() + "_" + i, "to_" + e.getTarget().getName() + "_" + j,
              e.getTarget().getName() + "_" + j, "from_" + e.getSource().getName() + "_" + i, 1, 1, d, e);

          // // get the source actor
          // SDFAbstractVertex srcActor = hsdf_graph.getVertex(e.getSource().getName() + "_" + i);
          // SDFInterfaceVertex srcPort = new SDFSinkInterfaceVertex();
          // srcPort.setId("to_" + e.getTarget().getName() + "_" + j);
          // srcPort.setName("to_" + e.getTarget().getName() + "_" + j);
          // srcActor.addInterface(srcPort);
          //
          // // get the target actor
          // SDFAbstractVertex tgtActor = hsdf_graph.getVertex(e.getTarget().getName() + "_" + j);
          // SDFInterfaceVertex tgtPort = new SDFSourceInterfaceVertex();
          // tgtPort.setId("from_" + e.getSource().getName() + "_" + i);
          // tgtPort.setName("from_" + e.getSource().getName() + "_" + i);
          // tgtActor.addInterface(tgtPort);
          //
          // // add the edge to the srSDF graph
          // SDFEdge edge = hsdf_graph.addEdge(srcActor, srcPort, tgtActor, tgtPort);
          // // edge.setPropertyValue("edgeName", "from_" + e.getSource().getName() + "_" + i + "_to_" + e.getTarget().getName() + "_" + j);
          // edge.setProd(new SDFIntEdgePropertyType(1));
          // edge.setCons(new SDFIntEdgePropertyType(1));
          // edge.setDelay(new SDFIntEdgePropertyType(d));
          // edge.setPropertyValue("baseEdge", e);

        }
      }
    }

    return hsdf_graph;
  }

  /**
   * Converts an SDF graph to a srSDF graph : SDF => srSDF
   * 
   * @param SDF
   *          graph
   * @return srSDF graph
   */
  public static SDFGraph convertToSrSDF(SDFGraph SDF) {
    // create the SRSDF
    SDFGraph singleRate = new SDFGraph();
    singleRate.setName(SDF.getName() + "_srSDF");

    // create actors instances
    for (SDFAbstractVertex a : SDF.vertexSet()) {
      for (int i = 1; i <= a.getNbRepeatAsInteger(); i++) {
        // create an instance a_i of the actor a
        GraphStructureHelper.addActor(singleRate, a.getName() + "_" + i, (SDFGraph) a.getGraphDescription(), 1, null, null, a);

        // SDFVertex a_i = new SDFVertex(singleRate);
        // a_i.setId(a.getId() + "_" + i);
        // a_i.setName(a.getName() + "_" + i);
        // a_i.setNbRepeat(1);
        // a_i.setPropertyValue("baseActor", a);
        // singleRate.addVertex(a_i);
        //
        // // if a is hierarchical add the subgraph to the instance
        // if (a.getGraphDescription() != null) {
        // a_i.setGraphDescription(a.getGraphDescription());
        // }

      }
    }

    // creates the edges
    for (SDFEdge e : SDF.edgeSet()) {
      for (int i = 1; i <= e.getSource().getNbRepeatAsInteger(); i++) {
        for (int k = 1; k <= e.getProd().intValue(); k += 1) {
          // compute the target actor instance id, cons/prod rate, and delay
          int l = ((e.getDelay().intValue() + ((i - 1) * e.getProd().intValue()) + k - 1) % (e.getCons().intValue() * e.getTarget().getNbRepeatAsInteger()))
              % e.getCons().intValue() + 1;
          int j = (int) (((e.getDelay().intValue() + ((i - 1) * e.getProd().intValue()) + k - 1)
              % (e.getCons().intValue() * e.getTarget().getNbRepeatAsInteger())) / e.getCons().intValue()) + 1;
          int d = (int) Math
              .floor((e.getDelay().intValue() + ((i - 1) * e.getProd().intValue()) + k - 1) / (e.getCons().intValue() * e.getTarget().getNbRepeatAsInteger()));

          int ma = e.getProd().intValue() - (k - 1);
          int mb = e.getCons().intValue() - (l - 1);
          int m = Math.min(ma, mb);
          k += (m - 1);

          GraphStructureHelper.addEdge(singleRate, e.getSource().getName() + "_" + i, "to_" + e.getTarget().getName() + "_" + j,
              e.getTarget().getName() + "_" + j, "from_" + e.getSource().getName() + "_" + i, m, m, d * m, e);

          // // get the source actor
          // SDFAbstractVertex srcActor = singleRate.getVertex(e.getSource().getName() + "_" + i);
          // SDFInterfaceVertex srcPort = new SDFSinkInterfaceVertex();
          // srcPort.setId("to_" + e.getTarget().getName() + "_" + j);
          // srcPort.setName("to_" + e.getTarget().getName() + "_" + j);
          // srcActor.addInterface(srcPort);
          //
          // // get the target actor
          // SDFAbstractVertex tgtActor = singleRate.getVertex(e.getTarget().getName() + "_" + j);
          // SDFInterfaceVertex tgtPort = new SDFSourceInterfaceVertex();
          // tgtPort.setId("from_" + e.getSource().getName() + "_" + i);
          // tgtPort.setName("from_" + e.getSource().getName() + "_" + i);
          // tgtActor.addInterface(tgtPort);
          //
          // // add the edge to the srSDF graph
          // SDFEdge edge = singleRate.addEdge(srcActor, srcPort, tgtActor, tgtPort);
          // // edge.setPropertyValue("edgeName", "from_" + e.getSource().getName() + "_" + i + "_to_" + e.getTarget().getName() + "_" + j);
          // edge.setProd(new SDFIntEdgePropertyType(m));
          // edge.setCons(new SDFIntEdgePropertyType(m));
          // edge.setDelay(new SDFIntEdgePropertyType(d * m));
          // edge.setPropertyValue("baseEdge", e);

        }
      }
    }

    return singleRate;
  }

  /**
   * Converts an SDF graph to a reduced HSDF graph : SDF => srSDF => HSDF
   * 
   * @param SDF
   *          graph
   * @return HSDF graph with less number of edges
   */
  public static SDFGraph convertToReducedHSDF(SDFGraph SDF) {
    // convert the SDF graph to a srSDF graph first then convert the srSDF graph to an HSDF graph
    SDFGraph hsdf_graph = convertToSrSDF(SDF);
    hsdf_graph = SrSDFTransformer.convertToHSDF(hsdf_graph);

    return hsdf_graph;
  }

  /**
   * Converts an SDF graph to a DAG : SDF => srSDF => DAG
   * 
   * @param SDF
   *          graph
   * @return DAG
   */
  public static SDFGraph convertToDAG(SDFGraph SDF) {
    // convert the SDF graph to a srSDF graph first then convert the srSDF graph to a DAG
    SDFGraph dag = convertToSrSDF(SDF);
    dag = SrSDFTransformer.convertToDAG(dag);

    return dag;
  }

}
