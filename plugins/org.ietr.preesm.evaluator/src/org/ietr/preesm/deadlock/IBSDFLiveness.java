package org.ietr.preesm.deadlock;

import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;

/**
 * @author hderoui
 *
 */
public abstract class IBSDFLiveness {

  /**
   * Test if each subgraph in the hierarchy is live including the top graph
   * 
   * @param ibsdf
   *          input graph
   * @return true if live, false if not.
   */
  public static boolean evaluate(SDFGraph ibsdf) {
    // step 1 check the liveness of the top graph
    boolean live = SDFLiveness.evaluate(ibsdf);

    // step 2 check the liveness of the subgraphs
    if (live) {
      // get the list of hierarchical actors
      Hashtable<String, SDFAbstractVertex> allHierarchicalActors = GraphStructureHelper.getAllHierarchicalActors(ibsdf);

      // check the liveness of the subgraph of each hierarchical actor in the list
      for (SDFAbstractVertex h : allHierarchicalActors.values()) {
        live = SDFLiveness.evaluate((SDFGraph) h.getGraphDescription());
        // if the subgraph is not live return false
        if (live == false) {
          return false;
        }
      }
    }

    return live;
  }
}
