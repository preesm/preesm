package org.preesm.model.pisdf.test;

import org.junit.Assert;
import org.junit.Test;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;

public class SrDAGGenerationTest {

  @Test
  public void testDelaySetterInSubgraph() {

    // Creating inner actor
    final Actor actorA = PiMMUserFactory.instance.createActor("Setter");
    final DataOutputPort setterOut = PiMMUserFactory.instance.createDataOutputPort("Setter.out");
    setterOut.setExpression(2);
    actorA.getDataOutputPorts().add(setterOut);

    // Creating inner Data output interface
    final String interfaceName = "subgraphOut";
    final DataOutputInterface setterIfOut = PiMMUserFactory.instance.createDataOutputInterface(interfaceName);
    setterIfOut.getDataInputPorts().get(0).setExpression(2);

    // Creating inner fifo
    final Fifo innerFifo = PiMMUserFactory.instance.createFifo(setterOut, setterIfOut.getDataInputPorts().get(0),
        "char");

    // Creating subgraph
    final PiGraph subGraph = PiMMUserFactory.instance.createPiGraph();
    subGraph.setName("subGraph");
    subGraph.setUrl("subGraph");

    // Adding previous elements to subgraph
    subGraph.addActor(actorA);
    subGraph.addActor(setterIfOut);
    subGraph.addFifo(innerFifo);

    // Setting rate of data output port of subgraph
    // subgraph data port is automatically created when adding interface to graph
    final DataOutputPort subGraphOut = subGraph.getDataOutputPorts().stream()
        .filter(dip -> dip.getName().equals(interfaceName)).findFirst().get();
    subGraphOut.setExpression(2);

    // Creating loop actor
    final Actor loopActor = PiMMUserFactory.instance.createActor("loop");
    final DataInputPort loopIn = PiMMUserFactory.instance.createDataInputPort("loop.in");
    final DataOutputPort loopOut = PiMMUserFactory.instance.createDataOutputPort("loop.out");
    loopIn.setExpression(2);
    loopOut.setExpression(2);
    loopActor.getDataInputPorts().add(loopIn);
    loopActor.getDataOutputPorts().add(loopOut);

    // Creating getter actor
    final Actor getterActor = PiMMUserFactory.instance.createActor("Getter");
    final DataInputPort getterIn = PiMMUserFactory.instance.createDataInputPort("getter.in");
    getterIn.setExpression(2);
    getterActor.getDataInputPorts().add(getterIn);

    // Creating loop fifo
    final Fifo loopFifo = PiMMUserFactory.instance.createFifo(loopOut, loopIn, "char");

    // Creating/adding delay to loop fifo
    final Delay loopDelay = PiMMUserFactory.instance.createDelay();
    loopDelay.setExpression(2);
    loopFifo.assignDelay(loopDelay);

    final Fifo setterFifo = PiMMUserFactory.instance.createFifo(subGraphOut, loopDelay.getActor().getDataInputPort(),
        "char");
    final Fifo getterFifo = PiMMUserFactory.instance.createFifo(loopDelay.getActor().getDataOutputPort(), getterIn,
        "char");

    final PiGraph topGraph = PiMMUserFactory.instance.createPiGraph();
    topGraph.setName("topGraph");
    topGraph.setUrl("topGraph");

    topGraph.addActor(subGraph);
    topGraph.addActor(loopActor);
    topGraph.addActor(getterActor);

    topGraph.addFifo(setterFifo);
    topGraph.addFifo(getterFifo);
    topGraph.addFifo(loopFifo);

    topGraph.addDelay(loopDelay);

    PiSDFToSingleRate.compute(topGraph, BRVMethod.LCM);

    Assert.assertTrue(true);

  }
}
