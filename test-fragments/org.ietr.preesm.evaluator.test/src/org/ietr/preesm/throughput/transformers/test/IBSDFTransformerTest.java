package org.ietr.preesm.throughput.transformers.test;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.deadlock.IBSDFConsistency;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.transformers.IBSDFTransformer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of IBSDFTransformer class
 * 
 * @author hderoui
 *
 */
public class IBSDFTransformerTest {

  @Test
  public void testIBSDFGraphShouldBeTranformedToFlatSrSDFGraph() {

    // generate an IBSDF graph
    SDFGraph ibsdf = generateIBSDFGraph();

    // flatten the hierarchy with the execution rules
    SDFGraph flatSrSDF = IBSDFTransformer.convertToSrSDF(ibsdf, true);

    // check the number of actors and the number of edges
    // actors 53 = 2 + 3*(1 + 2 + 6 + 4 + 1 + 2) + 3
    // edges 176
    for (SDFAbstractVertex a : flatSrSDF.vertexSet()) {
      System.out.println("Actor " + a.getId());
    }

    for (SDFEdge e : flatSrSDF.edgeSet()) {
      System.out.println("Edge " + e.toString());
    }

    int nbActor = flatSrSDF.vertexSet().size();
    int nbEdges = flatSrSDF.edgeSet().size();

    Assert.assertEquals(53, nbActor);
    Assert.assertEquals(176, nbEdges);

  }

  @Test
  public void testIBSDFGraphShouldBeTranformedToRelaxedSrSDFGraph() {

    // generate an IBSDF graph
    SDFGraph ibsdf = generateIBSDFGraph();

    // flatten the hierarchy
    SDFGraph flatSrSDF = IBSDFTransformer.convertToSrSDF(ibsdf, false);

    // check the number of actors and the number of edges
    // actors 47 = 2 + 3*(1 + 2 + 6 + 4 + 1) + 3
    // edges 95
    int nbActor = flatSrSDF.vertexSet().size();
    int nbEdges = flatSrSDF.edgeSet().size();

    Assert.assertEquals(47, nbActor);
    Assert.assertEquals(95, nbEdges);

  }

  /**
   * generate an IBSDF graph to test methods
   * 
   * @return IBSDF graph
   */
  public SDFGraph generateIBSDFGraph() {
    // Actors: A B[DEF] C
    // actor B is a hierarchical actor described by the subgraph DEF
    // a is the input interface of the subgraph DEF, associated with the input edge coming from A
    // the input interface is linked to the sub-actor E
    // c is the output interface of the subgraph DEF, associated with the output edge going to C
    // the output interface is linked to the sub-actor F

    // Edges of the top graph ABC : AB=(3,2); BC=(1,1); CA=(2,3)
    // Edges of the subgraph DEF : aE=(2,1); EF=(2,3); FD=(1,2); DE=(3,1); Fc=(3,1)

    // RV(top-graph) = [A=2, B=3, C=3]
    // RV(sub-graph) = [D=2, E=6, F=4]
    // after computing the RV of the subgraph the consumption/production rate of the interfaces are multiplied by their RV, then RV of interfaces is set to 1
    // the resulted rates of edges : aE=(6,1); Fc=(3,12)

    // create the subgraph
    SDFGraph subgraph = new SDFGraph();
    subgraph.setName("subgraph");
    GraphStructureHelper.addActor(subgraph, "D", null, null, 1., null, null);
    GraphStructureHelper.addActor(subgraph, "E", null, null, 1., null, null);
    GraphStructureHelper.addActor(subgraph, "F", null, null, 1., null, null);
    GraphStructureHelper.addInputInterface(subgraph, "a", null, 0., null, null);
    GraphStructureHelper.addOutputInterface(subgraph, "c", null, 0., null, null);

    GraphStructureHelper.addEdge(subgraph, "a", null, "E", null, 2, 1, 0, null);
    GraphStructureHelper.addEdge(subgraph, "E", null, "F", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "D", null, 1, 2, 0, null);
    GraphStructureHelper.addEdge(subgraph, "D", null, "E", null, 3, 1, 3, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "c", null, 3, 1, 0, null);

    // create the top graph and add the subgraph to the hierarchical actor B
    SDFGraph topgraph = new SDFGraph();
    topgraph.setName("topgraph");
    GraphStructureHelper.addActor(topgraph, "A", null, null, 1., null, null);
    GraphStructureHelper.addActor(topgraph, "B", subgraph, null, null, null, null);
    GraphStructureHelper.addActor(topgraph, "C", null, null, 1., null, null);

    GraphStructureHelper.addEdge(topgraph, "A", null, "B", "a", 3, 2, 3, null);
    GraphStructureHelper.addEdge(topgraph, "B", "c", "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(topgraph, "C", null, "A", null, 2, 3, 3, null);

    IBSDFConsistency.computeRV(topgraph);
    return topgraph;
  }
}
