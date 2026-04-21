package org.preesm.model.pisdf.test;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

public class PiGraphHierarchicalRecursivityTest {

  @Test
  public void testHierarchicalRecursivity1() {

    // Create recursive sub graph
    final PiGraph subGraph = PiMMUserFactory.instance.createPiGraph();
    subGraph.setName("subGraph");
    subGraph.setUrl("subGraph");

    // Create configurable input/output interfaces
    final DataInputInterface subGraphInput = PiMMUserFactory.instance.createDataInputInterface();
    final DataOutputInterface subGraphOutput = PiMMUserFactory.instance.createDataOutputInterface();

    // Add configurables to subGraph
    subGraph.addConfigurable(subGraphInput);
    subGraph.addConfigurable(subGraphOutput);

    // Create recursive Actor
    final AbstractActor recursiveActor = subGraph;

    subGraph.addActor(recursiveActor);

    final DataInputPort inputRec = PiMMUserFactory.instance.createDataInputPort("in");
    final DataOutputPort outputRec = PiMMUserFactory.instance.createDataOutputPort("out");

    recursiveActor.getDataInputPorts().add(inputRec);
    recursiveActor.getDataOutputPorts().add(outputRec);

    final Fifo fifoInPortInRec = PiMMUserFactory.instance.createFifo(subGraphInput.getDataPort(), inputRec, "void");
    final Fifo fifoOutRecOutPort = PiMMUserFactory.instance.createFifo(outputRec, subGraphOutput.getDataPort(), "void");
    final Fifo fifoInPortInGraph = PiMMUserFactory.instance.createFifo(subGraphInput.getDataPort(),
        subGraphInput.getGraphPort(), "void");
    final Fifo fifoOutGraphOutData = PiMMUserFactory.instance.createFifo(subGraphOutput.getGraphPort(),
        subGraphOutput.getDataPort(), "void");

    subGraph.addFifo(fifoInPortInRec);
    subGraph.addFifo(fifoOutRecOutPort);
    subGraph.addFifo(fifoInPortInGraph);
    subGraph.addFifo(fifoOutGraphOutData);

    inputRec.setExpression(1);
    outputRec.setExpression(1);
    subGraphInput.getDataPort().setExpression(1);
    subGraphInput.getGraphPort().setExpression(1);
    subGraphOutput.getDataPort().setExpression(1);
    subGraphOutput.getGraphPort().setExpression(1);

    // Create the top graph
    final PiGraph topGraph = PiMMUserFactory.instance.createPiGraph();
    topGraph.setName("topGraph");
    topGraph.setUrl("topGraph");

    // Create actors
    final AbstractActor actorA = PiMMUserFactory.instance.createActor("A");
    final AbstractActor actorB = subGraph;
    final AbstractActor actorC = PiMMUserFactory.instance.createActor("C");

    // Create a list for the actors to easily add them to the top graph
    final List<AbstractActor> actorsList = Arrays.asList(actorA, actorB, actorC);

    // Add actors to the top graph
    actorsList.stream().forEach(x -> topGraph.addActor(x));

    // Attach them to actors
    final DataOutputPort outputA = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputB = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataInputPort inputB = PiMMUserFactory.instance.createDataInputPort("in");
    final DataInputPort inputC = PiMMUserFactory.instance.createDataInputPort("in");

    actorA.getDataOutputPorts().add(outputA);
    actorB.getDataInputPorts().add(inputB);
    actorB.getDataOutputPorts().add(outputB);
    actorC.getDataInputPorts().add(inputC);

    // Create fifos and form a chain such as A -> B -> C -> D
    final Fifo fifoAB = PiMMUserFactory.instance.createFifo(outputA, inputB, "void");
    final Fifo fifoBC = PiMMUserFactory.instance.createFifo(outputB, inputC, "void");

    // Create a list for the fifos to easily add them to the top graph
    final List<Fifo> fifosList = Arrays.asList(fifoAB, fifoBC);

    // Add fifos to the top graph
    fifosList.stream().forEach(x -> topGraph.addFifo(x));

    // Setup data output and input ports rates
    outputA.setExpression(1);
    inputB.setExpression(1);
    outputB.setExpression(1);
    inputC.setExpression(1);

    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(topGraph);

  }
}
