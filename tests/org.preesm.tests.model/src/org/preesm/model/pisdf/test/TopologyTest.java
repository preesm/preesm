package org.preesm.model.pisdf.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.VertexPath;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;

/**
 *
 * @author anmorvan
 *
 */
public class TopologyTest {

  private PiGraph createGraph() {
    final Actor actorA = PiMMUserFactory.instance.createActor("A");
    final DataOutputPort aOut1 = PiMMUserFactory.instance.createDataOutputPort("A.out1");
    final DataOutputPort aOut2 = PiMMUserFactory.instance.createDataOutputPort("A.out2");
    actorA.getDataOutputPorts().add(aOut1);
    actorA.getDataOutputPorts().add(aOut2);

    final Actor actorB = PiMMUserFactory.instance.createActor("B");
    final DataInputPort bIn = PiMMUserFactory.instance.createDataInputPort("B.in");
    final DataOutputPort bOut = PiMMUserFactory.instance.createDataOutputPort("B.out");
    actorB.getDataInputPorts().add(bIn);
    actorB.getDataOutputPorts().add(bOut);

    final Actor actorC = PiMMUserFactory.instance.createActor("C");
    final DataInputPort cIn = PiMMUserFactory.instance.createDataInputPort("C.in");
    final DataOutputPort cOut = PiMMUserFactory.instance.createDataOutputPort("C.out");
    actorC.getDataInputPorts().add(cIn);
    actorC.getDataOutputPorts().add(cOut);

    final Actor actorD = PiMMUserFactory.instance.createActor("D");
    final DataInputPort dIn1 = PiMMUserFactory.instance.createDataInputPort("D.in1");
    final DataInputPort dIn2 = PiMMUserFactory.instance.createDataInputPort("D.in2");
    actorD.getDataInputPorts().add(dIn1);
    actorD.getDataInputPorts().add(dIn2);

    final Fifo f1 = PiMMUserFactory.instance.createFifo(aOut1, bIn, "void");
    final Fifo f2 = PiMMUserFactory.instance.createFifo(aOut2, cIn, "void");
    final Fifo f3 = PiMMUserFactory.instance.createFifo(bOut, dIn1, "void");
    final Fifo f4 = PiMMUserFactory.instance.createFifo(cOut, dIn2, "void");

    final PiGraph graph = PiMMUserFactory.instance.createPiGraph();
    graph.addActor(actorA);
    graph.addActor(actorB);
    graph.addActor(actorC);
    graph.addActor(actorD);
    graph.addFifo(f1);
    graph.addFifo(f2);
    graph.addFifo(f3);
    graph.addFifo(f4);

    return graph;
  }

  private PiGraph createGraphNoDAG() {
    final PiGraph createGraph = createGraph();

    final AbstractActor actorA = VertexPath.lookup(createGraph, "A");
    final DataInputPort aIn = PiMMUserFactory.instance.createDataInputPort("A.in");
    actorA.getDataInputPorts().add(aIn);

    final AbstractActor actorD = VertexPath.lookup(createGraph, "D");
    final DataOutputPort dOut = PiMMUserFactory.instance.createDataOutputPort("D.out");
    actorD.getDataOutputPorts().add(dOut);

    final Fifo f5 = PiMMUserFactory.instance.createFifo(dOut, aIn, "void");

    createGraph.addFifo(f5);

    return createGraph;
  }

  @Test
  public void testDirectPredecessors() {
    final PiGraph createGraph = createGraph();

    List<AbstractActor> preds = PiSDFTopologyHelper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "A")), preds);

    preds = PiSDFTopologyHelper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Collections.emptyList(), preds);

    preds = PiSDFTopologyHelper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C")), preds);
  }

  @Test
  public void testAllPredecessors() {
    final PiGraph createGraph = createGraph();

    List<AbstractActor> preds = PiSDFTopologyHelper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "A")), preds);

    preds = PiSDFTopologyHelper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Collections.emptyList(), preds);

    preds = PiSDFTopologyHelper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C"),
        VertexPath.lookup(createGraph, "A")), preds);

  }

  @Test
  public void testAllPredecessorsNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      PiSDFTopologyHelper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "A"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

  @Test
  public void testDirectSuccessors() {
    final PiGraph createGraph = createGraph();

    List<AbstractActor> preds = PiSDFTopologyHelper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "D")), preds);

    preds = PiSDFTopologyHelper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C")), preds);

    preds = PiSDFTopologyHelper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Collections.emptyList(), preds);
  }

  @Test
  public void testAllSuccessors() {
    final PiGraph createGraph = createGraph();

    List<AbstractActor> preds = PiSDFTopologyHelper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "D")), preds);

    preds = PiSDFTopologyHelper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C"),
        VertexPath.lookup(createGraph, "D")), preds);

    preds = PiSDFTopologyHelper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Collections.emptyList(), preds);
  }

  @Test
  public void testAllSuccessorNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      PiSDFTopologyHelper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "A"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

  @Test
  public void testAllOutFifos() {
    final PiGraph createGraph = createGraph();
    List<Fifo> outFifos;
    final Fifo fifo;

    outFifos = PiSDFTopologyHelper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "B"));
    assertEquals(1, outFifos.size());
    fifo = outFifos.get(0);
    assertEquals(VertexPath.lookup(createGraph, "B"), fifo.getTarget());
    assertEquals(VertexPath.lookup(createGraph, "A"), fifo.getSource());

    outFifos = PiSDFTopologyHelper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "D"));
    assertEquals(4, outFifos.size());

    outFifos = PiSDFTopologyHelper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "A"));
    assertEquals(0, outFifos.size());
  }

  @Test
  public void testAllInFifos() {
    final PiGraph createGraph = createGraph();
    List<Fifo> outFifos;
    final Fifo fifo;

    outFifos = PiSDFTopologyHelper.getSuccessorEdgesOf(VertexPath.lookup(createGraph, "B"));
    assertEquals(1, outFifos.size());
    fifo = outFifos.get(0);
    assertEquals(VertexPath.lookup(createGraph, "D"), fifo.getTarget());
    assertEquals(VertexPath.lookup(createGraph, "B"), fifo.getSource());

    outFifos = PiSDFTopologyHelper.getSuccessorEdgesOf(VertexPath.lookup(createGraph, "A"));
    assertEquals(4, outFifos.size());

    outFifos = PiSDFTopologyHelper.getSuccessorEdgesOf(VertexPath.lookup(createGraph, "D"));
    assertEquals(0, outFifos.size());
  }

  @Test
  public void testAllOutFifosNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      PiSDFTopologyHelper.getSuccessorEdgesOf(VertexPath.lookup(createGraph, "B"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

  @Test
  public void testAllInFifosNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      PiSDFTopologyHelper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "B"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

}
