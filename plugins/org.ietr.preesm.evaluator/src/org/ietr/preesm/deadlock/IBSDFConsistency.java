package org.ietr.preesm.deadlock;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;

/**
 * @author hderoui
 *
 */
public abstract class IBSDFConsistency {

  /**
   * Compute the Repetition factor of all actors of the hierarchy
   * 
   * @param graph
   *          IBSDF graph
   * @return true if consistent
   */
  public static boolean computeRV(SDFGraph graph) {
    Stopwatch timer = new Stopwatch();
    timer.start();

    // step 1: compute the RV of the top graph
    if (!SDFConsistency.computeRV(graph)) {
      timer.stop();
      System.err.println("IBSDF RV computation : " + graph.getName() + " is not consistent !! evaluated in " + timer.toString());
      return false;
    } else {
      // step 2: compute the RV of each subgraph
      for (SDFAbstractVertex actor : graph.vertexSet()) {
        if (actor.getGraphDescription() != null) {
          if (!IBSDFConsistency.computeRV((SDFGraph) actor.getGraphDescription())) {
            timer.stop();
            return false;
          }
        }
      }
      timer.stop();
      System.out.println("IBSDF RV computation : " + graph.getName() + " is consistent !! evaluated in " + timer.toString());
      return true;
    }
  }
}
