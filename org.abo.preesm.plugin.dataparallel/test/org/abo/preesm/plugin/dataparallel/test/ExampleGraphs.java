package org.abo.preesm.plugin.dataparallel.test;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class that creates some of the example graphs to test this plugin and also tests if these graphs are schedulable
 * 
 * @author sudeep
 *
 */
public final class ExampleGraphs {
  @Test
  public void testSDFCreation() {
    SDFGraph sdf = acyclicTwoActors();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertFalse(isHierarchical(sdf));

    sdf = strictlyCyclic();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertFalse(isHierarchical(sdf));

    sdf = semanticallyAcyclicCycle();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertFalse(isHierarchical(sdf));

    sdf = twoActorSelfLoop();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertFalse(isHierarchical(sdf));

    sdf = twoActorLoop();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertFalse(isHierarchical(sdf));

    sdf = mixedNetwork1();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertFalse(isHierarchical(sdf));

    sdf = mixedNetwork2();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertFalse(isHierarchical(sdf));

    sdf = acyclicHierarchicalTwoActors();
    Assert.assertTrue(sdf.isSchedulable());
    Assert.assertTrue(isHierarchical(sdf));
  }

  protected boolean isHierarchical(SDFGraph sdf) {
    for (final SDFAbstractVertex vertex : sdf.vertexSet()) {
      if (vertex.getGraphDescription() != null && vertex.getGraphDescription() instanceof SDFGraph) {
        return true;
      }
    }
    return false;
  }

  protected static SDFGraph addEdge(SDFGraph sdf, SDFAbstractVertex source, String sinkPortName, SDFAbstractVertex target, String sourcePortName, int prod,
      int cons, int delay) {
    SDFInterfaceVertex sourcePort = new SDFSourceInterfaceVertex();
    sourcePort.setName(sourcePortName);
    target.addSource(sourcePort);

    SDFInterfaceVertex sinkPort = new SDFSinkInterfaceVertex();
    sinkPort.setName(sinkPortName);
    source.addSink(sinkPort);

    sdf.addEdge(source, sinkPort, target, sourcePort, new SDFIntEdgePropertyType(prod), new SDFIntEdgePropertyType(cons), new SDFIntEdgePropertyType(delay));
    return sdf;
  }

  /**
   * Create a network with an acylic and cyclic graph The graph is schedulable but not data-parallel The acyclic graph has 4 actors with following config Z(3)
   * -(7)-> (6)C(2) --> (3)D(3) --> (2)E The cyclic graph has 3 actors with following config (3)A(2) -(4)-> (3)B(3) --> (2)C(1) C(1) -(1)-> (1)C The actor C is
   * shared
   * 
   * @return SDF graph
   */
  public static SDFGraph mixedNetwork2() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex a = new SDFVertex();
    a.setName("a");
    sdf.addVertex(a);

    final SDFVertex b = new SDFVertex();
    b.setName("b");
    sdf.addVertex(b);

    final SDFVertex c = new SDFVertex();
    c.setName("c");
    sdf.addVertex(c);

    final SDFVertex d = new SDFVertex();
    d.setName("d");
    sdf.addVertex(d);

    final SDFVertex e = new SDFVertex();
    e.setName("e");
    sdf.addVertex(e);

    final SDFVertex z = new SDFVertex();
    z.setName("z");
    sdf.addVertex(z);

    sdf = addEdge(sdf, a, "output", b, "input", 2, 3, 4);
    sdf = addEdge(sdf, b, "output", c, "input", 3, 2, 0);
    sdf = addEdge(sdf, c, "output", a, "intput", 1, 1, 0);
    sdf = addEdge(sdf, c, "outputC", c, "inputC", 1, 1, 1);
    sdf = addEdge(sdf, z, "output", c, "inputZ", 3, 6, 7);
    sdf = addEdge(sdf, c, "outputD", d, "input", 2, 3, 0);
    sdf = addEdge(sdf, d, "output", e, "input", 3, 2, 0);
    return sdf;
  }

  /**
   * Create a network with an acylic and cyclic graph The acyclic graph has 4 actors with following config Z(3) -(7)-> (6)C(2) --> (3)D(3) --> (2)E The cyclic
   * graph has 3 actors with following config (3)A(2) -(4)-> (3)B(3) --> (2)C(1) The actor C is shared
   * 
   * @return SDF graph
   */
  public static SDFGraph mixedNetwork1() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex a = new SDFVertex();
    a.setName("a");
    sdf.addVertex(a);

    final SDFVertex b = new SDFVertex();
    b.setName("b");
    sdf.addVertex(b);

    final SDFVertex c = new SDFVertex();
    c.setName("c");
    sdf.addVertex(c);

    final SDFVertex d = new SDFVertex();
    d.setName("d");
    sdf.addVertex(d);

    final SDFVertex e = new SDFVertex();
    e.setName("e");
    sdf.addVertex(e);

    final SDFVertex z = new SDFVertex();
    z.setName("z");
    sdf.addVertex(z);

    sdf = addEdge(sdf, a, "output", b, "input", 2, 3, 4);
    sdf = addEdge(sdf, b, "output", c, "input", 3, 2, 3);
    sdf = addEdge(sdf, c, "output", a, "input", 1, 1, 0);
    sdf = addEdge(sdf, z, "output", c, "inputZ", 3, 6, 7);
    sdf = addEdge(sdf, c, "outputD", d, "input", 2, 3, 0);
    sdf = addEdge(sdf, d, "output", e, "input", 3, 2, 0);
    return sdf;
  }

  /**
   * Create strictly cyclic SDF with a loop A(3) --> (5)B(5) -(7)-> (3)A The actor is schedulable but not parallel
   * 
   * @return SDF Graph
   */
  public static SDFGraph twoActorLoop() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex a = new SDFVertex();
    a.setName("a");
    sdf.addVertex(a);

    final SDFVertex b = new SDFVertex();
    b.setName("b");
    sdf.addVertex(b);

    sdf = addEdge(sdf, a, "output", b, "input", 3, 5, 0);
    sdf = addEdge(sdf, b, "output", a, "input", 5, 3, 7);
    return sdf;
  }

  /**
   * Create Acyclic graph with two actors, one of which has a self loop
   * 
   * @return SDF graph
   */
  public static SDFGraph twoActorSelfLoop() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex a = new SDFVertex();
    a.setName("a");
    sdf.addVertex(a);

    final SDFVertex b = new SDFVertex();
    b.setName("b");
    sdf.addVertex(b);

    sdf = addEdge(sdf, a, "output", b, "input", 3, 5, 7);
    sdf = addEdge(sdf, a, "outputA", a, "inputA", 1, 1, 1);
    return sdf;
  }

  /**
   * Create strictly cyclic SDF graph containing 4 actors The delay tokens are arranged so that none of the actors fires all the instances
   * 
   * A(2) --> (3)B(3) --> (2)C(2) --> (3)D(3) -(6)-> (2)A
   * 
   * @return sdf graph
   */
  public static SDFGraph semanticallyAcyclicCycle() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex a = new SDFVertex();
    a.setName("a");
    sdf.addVertex(a);

    final SDFVertex b = new SDFVertex();
    b.setName("b");
    sdf.addVertex(b);

    final SDFVertex c = new SDFVertex();
    c.setName("c");
    sdf.addVertex(c);

    final SDFVertex d = new SDFVertex();
    d.setName("d");
    sdf.addVertex(d);

    sdf = addEdge(sdf, a, "output", b, "input", 2, 3, 0);
    sdf = addEdge(sdf, b, "output", c, "input", 3, 2, 6);
    sdf = addEdge(sdf, c, "output", d, "input", 2, 3, 4);
    sdf = addEdge(sdf, d, "output", a, "input", 3, 2, 0);
    return sdf;
  }

  /**
   * Create strictly cyclic SDF graph containing 4 actors The delay tokens are arranged so that none of the actors fires all the instances
   * 
   * A(2) -(2)-> (3)B(3) -(3)-> (2)C(2) -(1)-> (3)D(3) -(3)-> (2)A
   * 
   * @return sdf graph
   */
  public static SDFGraph strictlyCyclic() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex a = new SDFVertex();
    a.setName("a");
    sdf.addVertex(a);

    final SDFVertex b = new SDFVertex();
    b.setName("b");
    sdf.addVertex(b);

    final SDFVertex c = new SDFVertex();
    c.setName("c");
    sdf.addVertex(c);

    final SDFVertex d = new SDFVertex();
    d.setName("d");
    sdf.addVertex(d);

    sdf = addEdge(sdf, a, "output", b, "input", 2, 3, 2);
    sdf = addEdge(sdf, b, "output", c, "input", 3, 2, 3);
    sdf = addEdge(sdf, c, "output", d, "input", 2, 3, 1);
    sdf = addEdge(sdf, d, "output", a, "input", 3, 2, 3);
    return sdf;
  }

  /**
   * Create hierarchical graph containing two actors P where P contains A(3) -(7)-> (5)B
   * 
   * @return SDF graph
   */
  public static SDFGraph acyclicHierarchicalTwoActors() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex p = new SDFVertex();
    p.setName("p");
    p.setGraphDescription(acyclicTwoActors());
    sdf.addVertex(p);

    return sdf;
  }

  /**
   * Create Acyclic graph containing two actors A (3) -> (5) B
   * 
   * @return sdf graph
   */
  public static SDFGraph acyclicTwoActors() {
    SDFGraph sdf = new SDFGraph();

    final SDFVertex a = new SDFVertex();
    a.setName("a");
    sdf.addVertex(a);

    final SDFVertex b = new SDFVertex();
    b.setName("b");
    sdf.addVertex(b);

    sdf = addEdge(sdf, a, "output", b, "input", 3, 5, 6);
    return sdf;
  }
}
