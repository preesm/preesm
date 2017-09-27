package org.ietr.preesm.throughput.transformers.test;

import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.transformers.SrSDFTransformer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of SrSDFTransformer class
 * 
 * @author hderoui
 *
 */
public class SrSDFTransformerTest {

  @Test
  public void testSrSDFGraphShouldBeTranformedToHSDFGraph() {

    // generate a srSDF graph
    SDFGraph srSDF = generateSrSDFGraphABC326();

    // convert the srSDF graph to an HSDF graph
    SDFGraph hsdf = SrSDFTransformer.convertToHSDF(srSDF);

    // verify that the consumption/production rate of all edges equal 1
    for (SDFEdge e : hsdf.edgeSet()) {
      int cons = e.getCons().intValue();
      int prod = e.getProd().intValue();

      Assert.assertEquals(1, cons);
      Assert.assertEquals(1, prod);

    }
  }

  @Test
  public void testSrSDFGraphShouldBeTranformedToDAG() {

    // generate a srSDF graph
    SDFGraph srSDF = generateSrSDFGraphABC326();

    // convert the srSDF graph to a DAG
    SDFGraph dag = SrSDFTransformer.convertToDAG(srSDF);

    // check if all the edges between Ai and Bi have been removed
    int nbEdge;
    nbEdge = dag.getVertex("A1").getSinks().size();
    Assert.assertEquals(0, nbEdge);

    nbEdge = dag.getVertex("A2").getSinks().size();
    Assert.assertEquals(0, nbEdge);

    nbEdge = dag.getVertex("A3").getSinks().size();
    Assert.assertEquals(0, nbEdge);

    // check if all the edges have zero delay
    for (SDFEdge e : dag.edgeSet()) {
      int delay = e.getDelay().intValue();
      Assert.assertEquals(0, delay);

    }
  }

  /**
   * generates a normalized SDF graph
   * 
   * @return SDF graph
   */
  public SDFGraph generateSrSDFGraphABC326() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]
    // Actors duration: A=1, B=1, C=1
    // Duration of the first iteration = 3

    // create srSDF graph testABC326
    SDFGraph graph = new SDFGraph();
    graph.setName("testABC326");

    // add actors
    GraphStructureHelper.addActor(graph, "A1", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "A2", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "A3", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "B1", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "B2", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C1", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C2", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C3", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C4", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C5", null, 1, 1., null, null);
    GraphStructureHelper.addActor(graph, "C6", null, 1, 1., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A1", null, "B1", null, 2, 2, 2, null);
    GraphStructureHelper.addEdge(graph, "A2", null, "B1", null, 1, 1, 1, null);
    GraphStructureHelper.addEdge(graph, "A2", null, "B2", null, 1, 1, 1, null);
    GraphStructureHelper.addEdge(graph, "A3", null, "B2", null, 2, 2, 2, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C4", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C5", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C6", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C1", null, "A1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C2", null, "A1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C3", null, "A2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C4", null, "A2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C5", null, "A3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C6", null, "A3", null, 1, 1, 0, null);

    return graph;
  }
}
