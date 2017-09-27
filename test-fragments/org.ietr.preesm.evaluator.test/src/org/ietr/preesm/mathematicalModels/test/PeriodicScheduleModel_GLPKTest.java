package org.ietr.preesm.mathematicalModels.test;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModel_GLPK;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.transformers.SDFTransformer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of PeriodicScheduleModel_GLPK class
 * 
 * @author hderoui
 *
 */
public class PeriodicScheduleModel_GLPKTest {

  @Test
  public void testNormalizedPeriodShouldBeComputed() {
    // generate a normalized SDF graph
    SDFGraph ABC = generateNormalizedSDFGraphABC3();
    // compute its normalized period K
    PeriodicScheduleModel_GLPK model = new PeriodicScheduleModel_GLPK();
    double k = model.computeNormalizedPeriod(ABC).doubleValue();
    // check the value of K
    Assert.assertEquals(1, k, 0);
  }

  /**
   * generates a normalized SDF graph
   * 
   * @return SDF graph
   */
  public SDFGraph generateNormalizedSDFGraphABC3() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]
    // Actors duration: A=1, B=1, C=1
    // Normalized rate of actors: A=2, B=3, C=1
    // normalization factor of edges: AB=1; BC=1; CA=0.5
    // normalized period K of the graph = 3

    // create SDF graph testABC3
    SDFGraph graph = new SDFGraph();
    graph.setName("testABC3");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, 3, 1., null, null);
    GraphStructureHelper.addActor(graph, "B", null, 2, 1., null, null);
    GraphStructureHelper.addActor(graph, "C", null, 6, 1., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "B", null, 2, 3, 6, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 3, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C", null, "A", null, 2, 4, 0, null);

    // normalize the graph
    SDFTransformer.normalize(graph);
    return graph;
  }
}
