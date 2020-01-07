package org.preesm.model.pisdf.test;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;

/**
 * This class is used to test the PiSDFSubgraphBuilder class.
 * 
 * @author dgageot
 *
 */
public class PiSDFSubgraphBuilderTest {

  public PiGraph       topGraph;
  public PiGraph       subGraph;
  public AbstractActor actorA;
  public AbstractActor actorB;
  public AbstractActor actorC;
  public AbstractActor actorD;
  public Parameter     param;

  /**
   * Sets-up the test environnement
   */
  @Before
  public void setUp() {
    // Create a chained actors PiGraph and lookup for actors B and C
    this.topGraph = createChainedActorsPiGraph();
    this.actorA = (AbstractActor) topGraph.lookupAllVertex("A");
    this.actorB = (AbstractActor) topGraph.lookupAllVertex("B");
    this.actorC = (AbstractActor) topGraph.lookupAllVertex("C");
    this.actorD = (AbstractActor) topGraph.lookupAllVertex("D");
    this.param = topGraph.lookupParameterGivenGraph("useless", "topgraph");
    // Regroup the two reference in a list
    List<AbstractActor> subGraphActors = Arrays.asList(actorB, actorC);
    // Build a PiSDFSubgraphBuilder
    PiSDFSubgraphBuilder subgraphBuilder = new PiSDFSubgraphBuilder(topGraph, subGraphActors, "B_C");
    // Process the transformation
    subgraphBuilder.build();
    // Keep a reference to the builded subgraph
    this.subGraph = (PiGraph) topGraph.lookupAllVertex("B_C");
  }

  /**
   * Teardowns the test environnement
   */
  @After
  public void tearDown() {
    this.topGraph = null;
    this.actorA = null;
    this.actorB = null;
    this.actorC = null;
    this.actorD = null;
    this.param = null;
    this.subGraph = null;
  }

  @Test
  public void testExistenceOfSubGraph() {
    // Check if the subgraph has been found in top graph
    Assert.assertNotNull(subGraph);
  }

  @Test
  public void testNameOfSubGraph() {
    // Check if the subgraph is called "B_C"
    Assert.assertEquals(subGraph.getName(), "B_C");
  }

  @Test
  public void testActorsContainedInSubGraph() {
    // Check if the subgraph contains actors B and C
    Assert.assertTrue(subGraph.getActors().contains(actorB));
    Assert.assertTrue(subGraph.getActors().contains(actorC));
  }

  @Test
  public void testFifoContainedInSubGraph() {
    // Check if the fifo that link B to C is contained
    Fifo fifoBC = actorB.getDataOutputPorts().get(0).getFifo();
    Assert.assertEquals(fifoBC.getContainingPiGraph(), subGraph);
    // Check that the delay and its actor is contained in the subgraph
    Assert.assertEquals(fifoBC.getDelay().getContainingPiGraph(), subGraph);
    Assert.assertEquals(fifoBC.getDelay().getActor().getContainingPiGraph(), subGraph);
  }

  @Test
  public void testFifoOutsideOfSubGraph() {
    // Check that outside delay are still outside
    Fifo fifoAB = actorA.getDataOutputPorts().get(0).getFifo();
    Assert.assertEquals(fifoAB.getContainingPiGraph(), topGraph);
    // Check that the delay and its actor is contained in the top graph
    Assert.assertEquals(fifoAB.getDelay().getContainingPiGraph(), topGraph);
    Assert.assertEquals(fifoAB.getDelay().getActor().getContainingPiGraph(), topGraph);
  }

  private PiGraph createChainedActorsPiGraph() {
    // Create the top graph
    PiGraph topGraph = PiMMUserFactory.instance.createPiGraph();
    topGraph.setName("topgraph");
    topGraph.setUrl("topgraph");
    // Create 4 actors
    AbstractActor actorA = PiMMUserFactory.instance.createActor("A");
    AbstractActor actorB = PiMMUserFactory.instance.createActor("B");
    AbstractActor actorC = PiMMUserFactory.instance.createActor("C");
    AbstractActor actorD = PiMMUserFactory.instance.createActor("D");
    // Create a list for the 4 actors to easily add them to the top graph
    List<AbstractActor> actorsList = Arrays.asList(actorA, actorB, actorC, actorD);
    // Add actors to the top graph
    for (AbstractActor actor : actorsList) {
      topGraph.addActor(actor);
    }
    // Create data output and input ports
    DataOutputPort outputA = PiMMUserFactory.instance.createDataOutputPort("out");
    DataOutputPort outputB = PiMMUserFactory.instance.createDataOutputPort("out");
    DataOutputPort outputC = PiMMUserFactory.instance.createDataOutputPort("out");
    DataInputPort inputB = PiMMUserFactory.instance.createDataInputPort("in");
    DataInputPort inputC = PiMMUserFactory.instance.createDataInputPort("in");
    DataInputPort inputD = PiMMUserFactory.instance.createDataInputPort("in");
    // Attach them to actors
    actorA.getDataOutputPorts().add(outputA);
    actorB.getDataOutputPorts().add(outputB);
    actorC.getDataOutputPorts().add(outputC);
    actorB.getDataInputPorts().add(inputB);
    actorC.getDataInputPorts().add(inputC);
    actorD.getDataInputPorts().add(inputD);
    // Create fifos and form a chain such as A -> B -> C -> D
    Fifo fifoAB = PiMMUserFactory.instance.createFifo(outputA, inputB, "void");
    Fifo fifoBC = PiMMUserFactory.instance.createFifo(outputB, inputC, "void");
    Fifo fifoCD = PiMMUserFactory.instance.createFifo(outputC, inputD, "void");
    // Create a list for the 3 fifos to easily add them to the top graph
    List<Fifo> fifosList = Arrays.asList(fifoAB, fifoBC, fifoCD);
    // Add fifos to the top graph
    for (Fifo fifo : fifosList) {
      topGraph.addFifo(fifo);
    }
    // Setup data output and input ports rates
    // Repetition vector should be equal to [1, 8, 8, 2]'
    outputA.setExpression(16);
    inputB.setExpression(2);
    outputB.setExpression(2);
    inputC.setExpression(2);
    outputC.setExpression(2);
    inputD.setExpression(8);
    // Set delay to fifo BC
    Delay delayBC = PiMMUserFactory.instance.createDelay();
    @SuppressWarnings("unused")
    DelayActor delayActorBC = PiMMUserFactory.instance.createDelayActor(delayBC);
    delayBC.setExpression(2);
    fifoBC.setDelay(delayBC);
    topGraph.addDelay(delayBC);
    // Set delay to fifo AC
    Delay delayAC = PiMMUserFactory.instance.createDelay();
    @SuppressWarnings("unused")
    DelayActor delayActorAB = PiMMUserFactory.instance.createDelayActor(delayAC);
    delayAC.setExpression(16);
    fifoAB.setDelay(delayAC);
    topGraph.addDelay(delayAC);
    // Add a parameter to actor B
    Parameter parameter = PiMMUserFactory.instance.createParameter("useless", 2);
    ConfigInputPort configInputB = PiMMUserFactory.instance.createConfigInputPort();
    actorB.getConfigInputPorts().add(configInputB);
    Dependency dependency = PiMMUserFactory.instance.createDependency(parameter, configInputB);
    topGraph.addParameter(parameter);
    topGraph.addDependency(dependency);
    // Return top graph
    return topGraph;
  }

}
