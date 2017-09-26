package org.ietr.preesm.throughput.test;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.ClassicalMethod;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of ClassicalMethodTest class
 * 
 * @author hderoui
 *
 */
public class ClassicalMethodTest {

  @Test
  public void testThroughputShouldBeComputed() {

    // generate the IBSDF graph AB[DEF]C
    SDFGraph ibsdf = generateIBSDFGraph();

    // compute its throughput by classical method
    ClassicalMethod method = new ClassicalMethod();
    double th = method.evaluate(ibsdf, null);

    // check the throughput value
    Assert.assertEquals(1 / 7, th, 0);

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
    GraphStructureHelper.addActor(subgraph, "D", null, null, null, null, null);
    GraphStructureHelper.addActor(subgraph, "E", null, null, null, null, null);
    GraphStructureHelper.addActor(subgraph, "F", null, null, null, null, null);
    GraphStructureHelper.addInputInterface(subgraph, "a", null, null, null, null);
    GraphStructureHelper.addOutputInterface(subgraph, "c", null, null, null, null);

    GraphStructureHelper.addEdge(subgraph, "a", null, "E", null, 2, 1, 0, null);
    GraphStructureHelper.addEdge(subgraph, "E", null, "F", null, 2, 3, 0, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "D", null, 1, 2, 2, null);
    GraphStructureHelper.addEdge(subgraph, "D", null, "E", null, 3, 1, 3, null);
    GraphStructureHelper.addEdge(subgraph, "F", null, "c", null, 3, 1, 0, null);

    // create the top graph and add the subgraph to the hierarchical actor B
    SDFGraph topgraph = new SDFGraph();
    topgraph.setName("topgraph");
    GraphStructureHelper.addActor(topgraph, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(topgraph, "B", subgraph, null, null, null, null);
    GraphStructureHelper.addActor(topgraph, "C", null, null, null, null, null);

    GraphStructureHelper.addEdge(topgraph, "A", null, "B", "a", 3, 2, 3, null);
    GraphStructureHelper.addEdge(topgraph, "B", "c", "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(topgraph, "C", null, "A", null, 2, 3, 3, null);

    return topgraph;
  }
}
