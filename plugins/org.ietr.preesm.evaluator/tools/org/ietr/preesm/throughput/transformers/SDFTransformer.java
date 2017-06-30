package org.ietr.preesm.throughput.transformers;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.helpers.MathFunctionsHelper;

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
        GraphStructureHelper.addActor(hsdf_graph, a.getName() + "_" + i, (SDFGraph) a.getGraphDescription(), 1,
            (Double) a.getPropertyBean().getValue("duration"), null, a);

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

          // add the edge
          GraphStructureHelper.addEdge(hsdf_graph, e.getSource().getName() + "_" + i, null, e.getTarget().getName() + "_" + j, null, 1, 1, d, e);

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
        GraphStructureHelper.addActor(singleRate, a.getName() + "_" + i, (SDFGraph) a.getGraphDescription(), 1,
            (Double) a.getPropertyBean().getValue("duration"), null, a);
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

          // add the edge
          GraphStructureHelper.addEdge(singleRate, e.getSource().getName() + "_" + i, null, e.getTarget().getName() + "_" + j, null, m, m, d * m, e);

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

  /**
   * normalize an SDF graph for the liveness test with the sufficient condition and for periodic schedule computation.
   * 
   * @param SDF
   *          graph
   */
  public static void normalize(SDFGraph SDF) {
    double K_RV = 1;
    for (SDFAbstractVertex actor : SDF.vertexSet()) {
      K_RV = MathFunctionsHelper.lcm(K_RV, actor.getNbRepeatAsInteger());
    }

    for (SDFEdge edge : SDF.edgeSet()) {
      edge.getSource().setPropertyValue("normalizedRate", K_RV / edge.getSource().getNbRepeatAsInteger());
      edge.getTarget().setPropertyValue("normalizedRate", K_RV / edge.getTarget().getNbRepeatAsInteger());
      edge.setPropertyValue("normalizationFactor", (K_RV * edge.getCons().intValue()) / edge.getTarget().getNbRepeatAsInteger());
    }
  }

}
