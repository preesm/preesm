package org.ietr.preesm.deadlock.test;

import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.deadlock.SDFConsistency;
import org.ietr.preesm.deadlock.SDFLiveness;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Unit test of SDFConsistency class
 * 
 * @author hderoui
 * 
 */
public class SDFLivenessTest {

  @Test
  public void testLivenessShouldReturnTrue() {
    // generate the SDF graph ABC326
    SDFGraph ABC = generateSDFGraphABC326();
    // check the liveness of the graph
    boolean live = SDFLiveness.evaluate(ABC);
    // check the RV value
    Assert.assertTrue(live);
  }

  @Test
  public void testLivenessShouldReturnFalse() {
    // generate the SDF graph ABC326
    SDFGraph ABC = generateSDFGraphABC326();
    // remove the initial delays
    for (SDFEdge e : ABC.edgeSet()) {
      e.setDelay(new SDFIntEdgePropertyType(0));
    }
    // check the liveness of the graph
    boolean live = SDFLiveness.evaluate(ABC);
    // check the RV value
    Assert.assertFalse(live);
  }

  /**
   * generate a SDF graph to test methods
   * 
   * @return SDF graph
   */
  public SDFGraph generateSDFGraphABC326() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]

    // create SDF graph testABC326
    SDFGraph graph = new SDFGraph();
    graph.setName("testABC326");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, null, 1., null, null);
    GraphStructureHelper.addActor(graph, "B", null, null, 1., null, null);
    GraphStructureHelper.addActor(graph, "C", null, null, 1., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "B", null, 2, 3, 3, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 3, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C", null, "A", null, 1, 2, 1, null);

    SDFConsistency.computeRV(graph);
    return graph;
  }

}
