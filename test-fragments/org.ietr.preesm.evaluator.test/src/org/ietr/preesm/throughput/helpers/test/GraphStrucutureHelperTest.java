package org.ietr.preesm.throughput.helpers.test;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of GraphStrucutureHelper class
 * 
 * @author hderoui
 *
 */
public class GraphStrucutureHelperTest {

  @Test
  public void testNewActorShouldBeAdded() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new actor to the SDF graph
    GraphStructureHelper.addActor(sdf, "newActor", null, 3, 7., null, null);

    // check the results
    Assert.assertEquals(1, sdf.vertexSet().size());
    Assert.assertNotNull(sdf.getVertex("newActor"));
    Assert.assertEquals(3, sdf.getVertex("newActor").getNbRepeat());
    Assert.assertEquals(7, (double) sdf.getVertex("newActor").getPropertyBean().getValue("duration"), 0);
  }

  @Test
  public void testNewEdgeShouldBeAdded() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "B", null, null, null, null, null);

    // Add the edge A to B
    GraphStructureHelper.addEdge(sdf, "A", null, "B", null, 2, 3, 5, null);

    // check the results
    Assert.assertEquals(1, sdf.edgeSet().size());
    SDFEdge e = sdf.edgeSet().iterator().next();
    Assert.assertEquals(3, e.getCons().intValue());
    Assert.assertEquals(2, e.getProd().intValue());
    Assert.assertEquals(5, e.getDelay().intValue());
  }

  @Test
  public void testNewInputInterfaceShouldBeAdded() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new InputInterface to the SDF graph
    GraphStructureHelper.addInputInterface(sdf, "in", 1, 1., null, null);

    // check the results
    Assert.assertEquals(1, sdf.vertexSet().size());
    Assert.assertNotNull(sdf.getVertex("in"));
    Assert.assertTrue(sdf.getVertex("in") instanceof SDFSourceInterfaceVertex);
  }

  @Test
  public void testNewOutputInterfaceShouldBeAdded() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add a new OutputInterface to the SDF graph
    GraphStructureHelper.addOutputInterface(sdf, "out", 1, 1., null, null);

    // check the results
    Assert.assertEquals(1, sdf.vertexSet().size());
    Assert.assertNotNull(sdf.getVertex("out"));
    Assert.assertTrue(sdf.getVertex("out") instanceof SDFSinkInterfaceVertex);
  }

  @Test
  public void testNewSourcePortShouldBeAdded() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A
    SDFAbstractVertex A = GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    Assert.assertEquals(0, A.getSinks().size());
    Assert.assertEquals(0, A.getSources().size());

    // Add a source port to actor A
    GraphStructureHelper.addSrcPort(A, "srcPort", 2);
    Assert.assertEquals(0, A.getSinks().size());
    Assert.assertEquals(1, A.getSources().size());

    // check the results
    int portRate = (Integer) A.getSources().iterator().next().getPropertyBean().getValue("port_rate");
    Assert.assertEquals(2, portRate);
  }

  @Test
  public void testNewSinkPortShouldBeAdded() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A
    SDFAbstractVertex A = GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    Assert.assertEquals(0, A.getSinks().size());
    Assert.assertEquals(0, A.getSources().size());

    // Add a sink port to actor A
    GraphStructureHelper.addSinkPort(A, "sinkPort", 3);
    Assert.assertEquals(1, A.getSinks().size());
    Assert.assertEquals(0, A.getSources().size());

    // check the results
    int portRate = (Integer) A.getSinks().iterator().next().getPropertyBean().getValue("port_rate");
    Assert.assertEquals(3, portRate);
  }

  @Test
  public void testTargetActorShouldBeReplaced() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "B", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "C", null, null, null, null, null);

    // Add the edge A to B
    SDFEdge e = GraphStructureHelper.addEdge(sdf, "A", null, "B", null, 2, 3, 5, null);

    SDFAbstractVertex sourceActor = e.getSource();
    SDFAbstractVertex targetActor = e.getTarget();

    Assert.assertEquals("A", sourceActor.getName());
    Assert.assertEquals("B", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("C").getSinks().size());

    // replace the target actor B by actor C
    e = GraphStructureHelper.replaceEdgeTargetActor(sdf, e, "C", null);

    sourceActor = e.getSource();
    targetActor = e.getTarget();

    Assert.assertEquals("A", sourceActor.getName());
    Assert.assertEquals("C", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("C").getSinks().size());

  }

  @Test
  public void testSourceActorShouldBeReplaced() {

    // create an empty SDF graph
    SDFGraph sdf = new SDFGraph();
    sdf.setName("test");

    // Add actor A and B
    GraphStructureHelper.addActor(sdf, "A", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "B", null, null, null, null, null);
    GraphStructureHelper.addActor(sdf, "C", null, null, null, null, null);

    // Add the edge A to B
    SDFEdge e = GraphStructureHelper.addEdge(sdf, "A", null, "B", null, 2, 3, 5, null);

    SDFAbstractVertex sourceActor = e.getSource();
    SDFAbstractVertex targetActor = e.getTarget();

    Assert.assertEquals("A", sourceActor.getName());
    Assert.assertEquals("B", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("C").getSinks().size());

    // replace the target actor B by actor C
    e = GraphStructureHelper.replaceEdgeSourceActor(sdf, e, "C", null);

    sourceActor = e.getSource();
    targetActor = e.getTarget();

    Assert.assertEquals("C", sourceActor.getName());
    Assert.assertEquals("B", targetActor.getName());

    Assert.assertEquals(0, sdf.getVertex("A").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("A").getSinks().size());

    Assert.assertEquals(1, sdf.getVertex("B").getSources().size());
    Assert.assertEquals(0, sdf.getVertex("B").getSinks().size());

    Assert.assertEquals(0, sdf.getVertex("C").getSources().size());
    Assert.assertEquals(1, sdf.getVertex("C").getSinks().size());

  }

}
