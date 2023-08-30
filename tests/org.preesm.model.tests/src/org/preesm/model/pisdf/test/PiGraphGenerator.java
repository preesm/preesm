package org.preesm.model.pisdf.test;

import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

public class PiGraphGenerator {

  private PiGraphGenerator() {
    // forbid instantiation
  }

  public static PiGraph createChainGraph() {
    final Actor actorA = PiMMUserFactory.instance.createActor("A");
    final DataOutputPort aOut = PiMMUserFactory.instance.createDataOutputPort("A.out");
    aOut.setExpression(10);
    actorA.getDataOutputPorts().add(aOut);

    final Actor actorB = PiMMUserFactory.instance.createActor("B");
    final DataInputPort bIn = PiMMUserFactory.instance.createDataInputPort("B.in");
    bIn.setExpression(10);
    actorB.getDataInputPorts().add(bIn);

    final Fifo f1 = PiMMUserFactory.instance.createFifo(aOut, bIn, "int");

    final PiGraph graph = PiMMUserFactory.instance.createPiGraph();
    graph.addActor(actorA);
    graph.addActor(actorB);
    graph.addFifo(f1);

    return graph;
  }

  public static PiGraph createDiamondGraph() {
    final Actor actorA = PiMMUserFactory.instance.createActor("A");
    final DataOutputPort aOut1 = PiMMUserFactory.instance.createDataOutputPort("A.out1");
    final DataOutputPort aOut2 = PiMMUserFactory.instance.createDataOutputPort("A.out2");
    aOut1.setExpression(10);
    aOut2.setExpression(10);
    actorA.getDataOutputPorts().add(aOut1);
    actorA.getDataOutputPorts().add(aOut2);

    final Actor actorB = PiMMUserFactory.instance.createActor("B");
    final DataInputPort bIn = PiMMUserFactory.instance.createDataInputPort("B.in");
    final DataOutputPort bOut = PiMMUserFactory.instance.createDataOutputPort("B.out");
    bIn.setExpression(10);
    bOut.setExpression(10);
    actorB.getDataInputPorts().add(bIn);
    actorB.getDataOutputPorts().add(bOut);

    final Actor actorC = PiMMUserFactory.instance.createActor("C");
    final DataInputPort cIn = PiMMUserFactory.instance.createDataInputPort("C.in");
    final DataOutputPort cOut = PiMMUserFactory.instance.createDataOutputPort("C.out");
    cIn.setExpression(1);
    cOut.setExpression(1);
    actorC.getDataInputPorts().add(cIn);
    actorC.getDataOutputPorts().add(cOut);

    final Actor actorD = PiMMUserFactory.instance.createActor("D");
    final DataInputPort dIn1 = PiMMUserFactory.instance.createDataInputPort("D.in1");
    final DataInputPort dIn2 = PiMMUserFactory.instance.createDataInputPort("D.in2");
    dIn1.setExpression(100);
    dIn2.setExpression(100);
    actorD.getDataInputPorts().add(dIn1);
    actorD.getDataInputPorts().add(dIn2);

    final Fifo f1 = PiMMUserFactory.instance.createFifo(aOut1, bIn, "int");
    final Fifo f2 = PiMMUserFactory.instance.createFifo(aOut2, cIn, "int");
    final Fifo f3 = PiMMUserFactory.instance.createFifo(bOut, dIn1, "int");
    final Fifo f4 = PiMMUserFactory.instance.createFifo(cOut, dIn2, "int");

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
}
