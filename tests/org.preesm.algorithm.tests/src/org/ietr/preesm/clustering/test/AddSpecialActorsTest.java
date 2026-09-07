package org.ietr.preesm.clustering.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.preesm.algorithm.clustering.synthesis.ClusterSynthesisHelper;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

public class AddSpecialActorsTest {

  PiGraph             topGraph   = null;
  PiGraph             cluster    = null;
  DataInputInterface  clusterIn  = null;
  DataOutputInterface clusterOut = null;

  Actor a1 = null;
  Actor a2 = null;
  Actor b  = null;
  Actor c  = null;

  @Before
  public void setUp() {
    // Create a test environment
    createTestEnvironment();
  }

  @After
  public void tearDown() {

    topGraph = null;
    cluster = null;
    clusterIn = null;
    clusterOut = null;
    a1 = null;
    b = null;
    c = null;
  }

  @Test
  public void testAddingActors() {
    ClusterSynthesisHelper.addSpecialActors(cluster);

    assertTrue(clusterIn.getDataPort().getFifo().getTarget() instanceof BroadcastActor);
    assertTrue(clusterOut.getDataPort().getFifo().getSource() instanceof RoundBufferActor);

    assertEquals(4L, clusterIn.getDataPort().getFifo().getTargetPort().getExpression().evaluateAsLong());
    assertEquals(4L, clusterOut.getDataPort().getFifo().getSourcePort().getExpression().evaluateAsLong());

    final BroadcastActor brd = (BroadcastActor) clusterIn.getDataPort().getFifo().getTarget();
    final RoundBufferActor rdb = (RoundBufferActor) clusterOut.getDataPort().getFifo().getSource();

    assertEquals(8L, brd.getDataOutputPorts().getFirst().getExpression().evaluateAsLong());
    assertEquals(8L, rdb.getDataInputPorts().getFirst().getExpression().evaluateAsLong());
  }

  @Test
  public void testNotAddingActors() {

    b.getDataInputPorts().getFirst().setExpression(4);
    b.getDataOutputPorts().getFirst().setExpression(4);

    ClusterSynthesisHelper.addSpecialActors(cluster);

    assertFalse(clusterIn.getDataPort().getFifo().getTarget() instanceof BroadcastActor);
    assertFalse(clusterOut.getDataPort().getFifo().getSource() instanceof RoundBufferActor);

  }

  private void createTestEnvironment() {

    topGraph = PiMMUserFactory.instance.createPiGraph();
    topGraph.setName("top");
    cluster = PiMMUserFactory.instance.createPiGraph();
    cluster.setName("cluster");

    a1 = PiMMUserFactory.instance.createActor();
    a1.setName("A");
    final DataOutputPort aOut = PiMMUserFactory.instance.createDataOutputPort();
    a1.getDataOutputPorts().add(aOut);

    b = PiMMUserFactory.instance.createActor();
    b.setName("B");
    final DataInputPort bIn = PiMMUserFactory.instance.createDataInputPort();
    final DataOutputPort bOut = PiMMUserFactory.instance.createDataOutputPort();
    b.getDataInputPorts().add(bIn);
    b.getDataOutputPorts().add(bOut);

    c = PiMMUserFactory.instance.createActor();
    c.setName("C");
    final DataInputPort cIn = PiMMUserFactory.instance.createDataInputPort();
    c.getDataInputPorts().add(cIn);

    clusterIn = PiMMUserFactory.instance.createDataInputInterface();

    clusterOut = PiMMUserFactory.instance.createDataOutputInterface();

    cluster.addActor(clusterIn);
    cluster.addActor(clusterOut);
    cluster.addActor(b);
    topGraph.addActor(a1);
    topGraph.addActor(cluster);
    topGraph.addActor(c);

    aOut.setExpression(8);
    clusterIn.getGraphPort().setExpression(4);
    clusterIn.getDataPort().setExpression(4);
    bIn.setExpression(8);
    bOut.setExpression(8);
    clusterOut.getGraphPort().setExpression(4);
    clusterOut.getDataPort().setExpression(4);
    cIn.setExpression(8);

    final Fifo a2Cluster = PiMMUserFactory.instance.createFifo(aOut, clusterIn.getGraphPort(), "char");
    final Fifo cluster2b = PiMMUserFactory.instance.createFifo(clusterIn.getDataPort(), bIn, "char");
    final Fifo b2Cluster = PiMMUserFactory.instance.createFifo(bOut, clusterOut.getDataPort(), "char");
    final Fifo cluster2c = PiMMUserFactory.instance.createFifo(clusterOut.getGraphPort(), cIn, "char");

    topGraph.addFifo(a2Cluster);
    topGraph.addFifo(cluster2c);
    cluster.addFifo(cluster2b);
    cluster.addFifo(b2Cluster);
  }
}
