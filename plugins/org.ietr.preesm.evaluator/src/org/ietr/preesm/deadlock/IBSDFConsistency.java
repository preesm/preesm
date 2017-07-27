package org.ietr.preesm.deadlock;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;

/**
 * @author hderoui
 *
 */
public abstract class IBSDFConsistency {

  /**
   * @param graph
   *          IBSDF graph
   * @return true if consistent
   */
  public static boolean computeRV(SDFGraph graph) {
    // step 1: compute the RV of the top graph
    if (!SDFConsistency.computeRV(graph)) {
      System.err.println("IBSDF RV computation : " + graph.getName() + " is deadlock !!");
      return false;
    } else {
      // step 2: compute the RV of each subgraph
      for (SDFAbstractVertex actor : graph.vertexSet()) {
        if (actor.getGraphDescription() != null) {
          if (!IBSDFConsistency.computeRV((SDFGraph) actor.getGraphDescription())) {
            return false;
          }
        }
      }
      return true;
    }
  }
}
