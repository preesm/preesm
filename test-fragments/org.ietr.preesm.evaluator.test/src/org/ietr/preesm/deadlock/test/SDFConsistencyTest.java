package org.ietr.preesm.deadlock.test;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.ietr.preesm.deadlock.SDFConsistency;
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
public class SDFConsistencyTest {

  @Test
  public void testRVShouldBeComputed() {
    // generate the SDF graph ABC326
    SDFGraph ABC = generateSDFGraphABC326();
    // compute the repetition vector (RV) of ABC326
    SDFConsistency.computeRV(ABC);
    // check the RV value
    Assert.assertEquals(3, ABC.getVertex("A").getNbRepeat());
    Assert.assertEquals(2, ABC.getVertex("B").getNbRepeat());
    Assert.assertEquals(6, ABC.getVertex("C").getNbRepeat());
  }

  @Test
  public void testConsistencyShouldBeEvaluated() {
    // generate the SDF graph ABC326 (consistent)
    SDFGraph ABC = generateSDFGraphABC326();
    // evaluate the consistency
    Boolean consistent = SDFConsistency.computeRV(ABC);
    Assert.assertTrue(consistent);

    // change the consumption rate of a random edge so that the graph becomes non consistent
    ABC.edgeSet().iterator().next().setCons(new SDFIntEdgePropertyType(10));
    // evaluate the consistency
    consistent = SDFConsistency.computeRV(ABC);
    Assert.assertFalse(consistent);

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
    GraphStructureHelper.addActor(graph, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(graph, "B", null, null, null, null, null);
    GraphStructureHelper.addActor(graph, "C", null, null, null, null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "B", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 3, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C", null, "A", null, 1, 2, 0, null);

    return graph;
  }

}
