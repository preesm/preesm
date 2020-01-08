package org.preesm.model.pisdf.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;

/**
 * This class is used to test the PiSDFSubgraphBuilder class.
 * 
 * @author dgageot
 *
 */
public class PiSDFSubgraphBuilderTest {

  private PiGraph       topGraph;
  private PiGraph       subGraph;
  private AbstractActor actorA;
  private AbstractActor actorB;
  private AbstractActor actorC;
  private AbstractActor actorD;
  private Parameter     param;
  private final String  topGraphName = new String("topgraph");
  private final String  subGraphName = new String("B_C");

  /**
   * Set-up the test environnement
   */
  @Before
  public void setUp() {
    // Create a chained actors PiGraph and lookup for actors B and C
    this.topGraph = createChainedActorsPiGraph();
    this.actorA = (AbstractActor) topGraph.lookupAllVertex("A");
    this.actorB = (AbstractActor) topGraph.lookupAllVertex("B");
    this.actorC = (AbstractActor) topGraph.lookupAllVertex("C");
    this.actorD = (AbstractActor) topGraph.lookupAllVertex("D");
    this.param = topGraph.lookupParameterGivenGraph("useless", topGraphName);
    // Regroup the two reference in a list
    List<AbstractActor> subGraphActors = Arrays.asList(actorB, actorC);
    // Build a PiSDFSubgraphBuilder
    PiSDFSubgraphBuilder subgraphBuilder = new PiSDFSubgraphBuilder(topGraph, subGraphActors, subGraphName);
    // Process the transformation
    subgraphBuilder.build();
    // Keep a reference to the builded subgraph
    this.subGraph = (PiGraph) topGraph.lookupAllVertex(subGraphName);
  }

  /**
   * Teardown the test environnement
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
    Assert.assertEquals(subGraph.getName(), subGraphName);
  }

  @Test
  public void testRepetitionCountsOfSubGraph() {
    // Compute the repetition vector
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(topGraph, BRVMethod.LCM);
    // Check if the subgraph is repeated 4 times
    Assert.assertEquals(4, repetitionVector.get(subGraph).longValue());
    // Check if the actor B is repeated 2 times
    Assert.assertEquals(2, repetitionVector.get(actorB).longValue());
    // Check if the actor C is repeated 1 times
    Assert.assertEquals(1, repetitionVector.get(actorC).longValue());
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
    Assert.assertEquals(subGraph, fifoBC.getContainingPiGraph());
    // Check that the delay and its actor is contained in the subgraph
    Assert.assertEquals(subGraph, fifoBC.getDelay().getContainingPiGraph());
    Assert.assertEquals(subGraph, fifoBC.getDelay().getActor().getContainingPiGraph());
    // Check that added fifo are also contained in the sugraph
    Assert.assertEquals(subGraph, actorB.getDataInputPorts().get(0).getFifo().getContainingPiGraph());
    Assert.assertEquals(subGraph, actorC.getDataOutputPorts().get(0).getFifo().getContainingPiGraph());
  }

  @Test
  public void testFifoOutsideOfSubGraph() {
    // Check that outside delays are outside
    // 1. On fifo from actor A
    Fifo fifoAB = actorA.getDataOutputPorts().get(0).getFifo();
    Assert.assertEquals(topGraph, fifoAB.getContainingPiGraph());
    // Check that the delay and its actor is contained in the top graph
    Assert.assertEquals(topGraph, fifoAB.getDelay().getContainingPiGraph());
    Assert.assertEquals(topGraph, fifoAB.getDelay().getActor().getContainingPiGraph());
    // 2. On fifo to actor D
    Fifo fifoCD = actorD.getDataInputPorts().get(0).getFifo();
    Assert.assertEquals(topGraph, fifoCD.getContainingPiGraph());
    // Check that the delay and its actor is contained in the top graph
    Assert.assertEquals(topGraph, fifoCD.getDelay().getContainingPiGraph());
    Assert.assertEquals(topGraph, fifoCD.getDelay().getActor().getContainingPiGraph());
  }

  @Test
  public void testDataInputPortsOfSubGraph() {
    // Check the number of data input ports
    Assert.assertEquals(1, subGraph.getDataInputPorts().size());
    DataInputPort dipSubGraph = subGraph.getDataInputPorts().get(0);
    // Check port name
    Assert.assertTrue(dipSubGraph.getName().contains("in_0"));
    // Check rate
    Assert.assertEquals(4, dipSubGraph.getExpression().evaluate());
    // Check if incoming fifo link actor A to actor B_C
    Assert.assertEquals(actorA, dipSubGraph.getIncomingFifo().getSource());
  }

  @Test
  public void testDataOutputPortsOfSubGraph() {
    // Check the number of data input ports
    Assert.assertEquals(1, subGraph.getDataOutputPorts().size());
    DataOutputPort dopSubGraph = subGraph.getDataOutputPorts().get(0);
    // Check port name
    Assert.assertTrue(dopSubGraph.getName().contains("out_0"));
    // Check rate
    Assert.assertEquals(4, dopSubGraph.getExpression().evaluate());
    // Check if outgoing fifo link actor B_C to actor D
    Assert.assertEquals(actorD, dopSubGraph.getOutgoingFifo().getTarget());
  }

  @Test
  public void testConfigInputPortsOfSubGraph() {
    // Check the number of data input ports
    Assert.assertEquals(1, subGraph.getConfigInputPorts().size());
    ConfigInputPort cipSubGraph = subGraph.getConfigInputPorts().get(0);
    // Check port name
    Assert.assertTrue(cipSubGraph.getName().contains("cfg_0"));
    // Check if setter is param object
    Assert.assertEquals(param, cipSubGraph.getIncomingDependency().getSetter());
  }

  @Test
  public void testDataInputInterfaceOfSubGraph() {
    // Check the number of data input interfaces
    Assert.assertEquals(1, subGraph.getDataInputInterfaces().size());
    DataInputInterface diiSubGraph = subGraph.getDataInputInterfaces().get(0);
    // Check interface name
    Assert.assertTrue(diiSubGraph.getName().contains("in_0"));
    // Check if graph port
    Assert.assertEquals(subGraph.getDataInputPorts().get(0), diiSubGraph.getGraphPort());
    // Check if actor B input fifo is linked to the data input interface
    Assert.assertEquals(actorB.getDataInputPorts().get(0).getFifo(), diiSubGraph.getDataPort().getFifo());
    // Check rate of data port of the interface
    Assert.assertEquals(4, diiSubGraph.getDataPort().getExpression().evaluate());
    // Check rate on actor B data input port
    Assert.assertEquals(2, actorB.getDataInputPorts().get(0).getExpression().evaluate());
  }

  @Test
  public void testDataOutputInterfaceOfSubGraph() {
    // Check the number of data output interfaces
    Assert.assertEquals(1, subGraph.getDataOutputInterfaces().size());
    DataOutputInterface doiSubGraph = subGraph.getDataOutputInterfaces().get(0);
    // Check interface name
    Assert.assertTrue(doiSubGraph.getName().contains("out_0"));
    // Check if graph port
    Assert.assertEquals(subGraph.getDataOutputPorts().get(0), doiSubGraph.getGraphPort());
    // Check if actor C input fifo is linked to the data output interface
    Assert.assertEquals(actorC.getDataOutputPorts().get(0).getFifo(), doiSubGraph.getDataPort().getFifo());
    // Check rate of data port of the interface
    Assert.assertEquals(4, doiSubGraph.getDataPort().getExpression().evaluate());
    // Check rate on actor C data output port
    Assert.assertEquals(4, actorC.getDataOutputPorts().get(0).getExpression().evaluate());
  }

  @Test
  public void testConfigInputInterfaceOfSubGraph() {
    // Check the number of data output interfaces
    Assert.assertEquals(1, subGraph.getConfigInputInterfaces().size());
    ConfigInputInterface cfgSubGraph = subGraph.getConfigInputInterfaces().get(0);
    // Check interface name
    Assert.assertTrue(cfgSubGraph.getName().contains("cfg_0"));
    // Check if graph port
    Assert.assertEquals(subGraph.getConfigInputPorts().get(0), cfgSubGraph.getGraphPort());
    // Check if actor B input dependency is linked to the config input interface
    Assert.assertEquals(actorB.getConfigInputPorts().get(0).getIncomingDependency(),
        cfgSubGraph.getOutgoingDependencies().get(0));
  }

  private PiGraph createChainedActorsPiGraph() {
    // Create the top graph
    PiGraph topGraph = PiMMUserFactory.instance.createPiGraph();
    topGraph.setName(topGraphName);
    topGraph.setUrl(topGraphName);
    // Create 4 actors
    AbstractActor _actorA = PiMMUserFactory.instance.createActor("A");
    AbstractActor _actorB = PiMMUserFactory.instance.createActor("B");
    AbstractActor _actorC = PiMMUserFactory.instance.createActor("C");
    AbstractActor _actorD = PiMMUserFactory.instance.createActor("D");
    // Create a list for the 4 actors to easily add them to the top graph
    List<AbstractActor> actorsList = Arrays.asList(_actorA, _actorB, _actorC, _actorD);
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
    _actorA.getDataOutputPorts().add(outputA);
    _actorB.getDataOutputPorts().add(outputB);
    _actorC.getDataOutputPorts().add(outputC);
    _actorB.getDataInputPorts().add(inputB);
    _actorC.getDataInputPorts().add(inputC);
    _actorD.getDataInputPorts().add(inputD);
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
    // Repetition vector should be equal to [1, 8, 4, 2]'
    outputA.setExpression(16);
    inputB.setExpression(2);
    outputB.setExpression(2);
    inputC.setExpression(4);
    outputC.setExpression(4);
    inputD.setExpression(8);
    // Set delay to fifo AC
    Delay delayAC = PiMMUserFactory.instance.createDelay();
    delayAC.setExpression(16);
    fifoAB.setDelay(delayAC);
    topGraph.addDelay(delayAC);
    // Set delay to fifo BC
    Delay delayBC = PiMMUserFactory.instance.createDelay();
    delayBC.setExpression(2);
    fifoBC.setDelay(delayBC);
    topGraph.addDelay(delayBC);
    // Set delay to fifo CD
    Delay delayCD = PiMMUserFactory.instance.createDelay();
    delayCD.setExpression(4);
    fifoCD.setDelay(delayCD);
    topGraph.addDelay(delayCD);
    // Add a parameter to actor B
    Parameter parameter = PiMMUserFactory.instance.createParameter("useless", 2);
    ConfigInputPort configInputB = PiMMUserFactory.instance.createConfigInputPort();
    _actorB.getConfigInputPorts().add(configInputB);
    Dependency dependency = PiMMUserFactory.instance.createDependency(parameter, configInputB);
    topGraph.addParameter(parameter);
    topGraph.addDependency(dependency);
    // Return top graph
    return topGraph;
  }

}
