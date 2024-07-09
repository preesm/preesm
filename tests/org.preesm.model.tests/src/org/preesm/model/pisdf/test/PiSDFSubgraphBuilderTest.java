/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2022 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020 - 2023)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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

  private PiGraph             topGraph;
  private PiGraph             subGraph;
  private AbstractActor       actorA;
  private AbstractActor       actorB;
  private AbstractActor       actorC;
  private AbstractActor       actorD;
  private Parameter           param;
  private static final String TOP_GRAPH_NAME = "topgraph";
  private static final String SUB_GRAPH_NAME = "B_C";

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
    this.param = topGraph.lookupParameterGivenGraph("useless", TOP_GRAPH_NAME);
    // Regroup the two reference in a list
    final List<AbstractActor> subGraphActors = Arrays.asList(actorB, actorC);
    // Build a PiSDFSubgraphBuilder
    final PiSDFSubgraphBuilder subgraphBuilder = new PiSDFSubgraphBuilder(topGraph, subGraphActors, SUB_GRAPH_NAME);
    // Process the transformation
    subgraphBuilder.build();
    // Keep a reference to the builded subgraph
    this.subGraph = (PiGraph) topGraph.lookupAllVertex(SUB_GRAPH_NAME);
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
    Assert.assertEquals(SUB_GRAPH_NAME, subGraph.getName());
  }

  @Test
  public void testRepetitionCountsOfSubGraph() {
    // Compute the repetition vector
    final Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(topGraph, BRVMethod.LCM);
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
    final Fifo fifoBC = actorB.getDataOutputPorts().get(0).getFifo();
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
    final Fifo fifoAB = actorA.getDataOutputPorts().get(0).getFifo();
    Assert.assertEquals(topGraph, fifoAB.getContainingPiGraph());
    // Check that the delay and its actor is contained in the top graph
    Assert.assertEquals(topGraph, fifoAB.getDelay().getContainingPiGraph());
    Assert.assertEquals(topGraph, fifoAB.getDelay().getActor().getContainingPiGraph());
    // 2. On fifo to actor D
    final Fifo fifoCD = actorD.getDataInputPorts().get(0).getFifo();
    Assert.assertEquals(topGraph, fifoCD.getContainingPiGraph());
    // Check that the delay and its actor is contained in the top graph
    Assert.assertEquals(topGraph, fifoCD.getDelay().getContainingPiGraph());
    Assert.assertEquals(topGraph, fifoCD.getDelay().getActor().getContainingPiGraph());
  }

  @Test
  public void testDataInputPortsOfSubGraph() {
    // Check the number of data input ports
    Assert.assertEquals(1, subGraph.getDataInputPorts().size());
    final DataInputPort dipSubGraph = subGraph.getDataInputPorts().get(0);
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
    final DataOutputPort dopSubGraph = subGraph.getDataOutputPorts().get(0);
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
    final ConfigInputPort cipSubGraph = subGraph.getConfigInputPorts().get(0);
    // Check port name
    Assert.assertTrue(cipSubGraph.getName().contains("cfg_0"));
    // Check if setter is param object
    Assert.assertEquals(param, cipSubGraph.getIncomingDependency().getSetter());
  }

  @Test
  public void testDataInputInterfaceOfSubGraph() {
    // Check the number of data input interfaces
    Assert.assertEquals(1, subGraph.getDataInputInterfaces().size());
    final DataInputInterface diiSubGraph = subGraph.getDataInputInterfaces().get(0);
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
    final DataOutputInterface doiSubGraph = subGraph.getDataOutputInterfaces().get(0);
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
    final ConfigInputInterface cfgSubGraph = subGraph.getConfigInputInterfaces().get(0);
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
    final PiGraph chainedActorGraph = PiMMUserFactory.instance.createPiGraph();
    chainedActorGraph.setName(TOP_GRAPH_NAME);
    chainedActorGraph.setUrl(TOP_GRAPH_NAME);
    // Create 4 actors
    final AbstractActor chainA = PiMMUserFactory.instance.createActor("A");
    final AbstractActor chainB = PiMMUserFactory.instance.createActor("B");
    final AbstractActor chainC = PiMMUserFactory.instance.createActor("C");
    final AbstractActor chainD = PiMMUserFactory.instance.createActor("D");
    // Create a list for the 4 actors to easily add them to the top graph
    final List<AbstractActor> actorsList = Arrays.asList(chainA, chainB, chainC, chainD);
    // Add actors to the top graph
    for (final AbstractActor actor : actorsList) {
      chainedActorGraph.addActor(actor);
    }
    // Create data output and input ports
    final DataOutputPort outputA = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputB = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputC = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataInputPort inputB = PiMMUserFactory.instance.createDataInputPort("in");
    final DataInputPort inputC = PiMMUserFactory.instance.createDataInputPort("in");
    final DataInputPort inputD = PiMMUserFactory.instance.createDataInputPort("in");
    // Attach them to actors
    chainA.getDataOutputPorts().add(outputA);
    chainB.getDataOutputPorts().add(outputB);
    chainC.getDataOutputPorts().add(outputC);
    chainB.getDataInputPorts().add(inputB);
    chainC.getDataInputPorts().add(inputC);
    chainD.getDataInputPorts().add(inputD);
    // Create fifos and form a chain such as A -> B -> C -> D
    // Setup data output and input ports rates
    // Repetition vector should be equal to [1, 8, 4, 2]'
    outputA.setExpression(16);
    inputB.setExpression(2);
    outputB.setExpression(2);
    inputC.setExpression(4);
    outputC.setExpression(4);
    inputD.setExpression(8);
    // Set delay to fifo AC
    final Delay delayAC = PiMMUserFactory.instance.createDelay();
    delayAC.setExpression(16);
    final Fifo fifoAB = PiMMUserFactory.instance.createFifo(outputA, inputB, "void");
    fifoAB.assignDelay(delayAC);
    chainedActorGraph.addDelay(delayAC);
    // Set delay to fifo BC
    final Delay delayBC = PiMMUserFactory.instance.createDelay();
    delayBC.setExpression(2);
    final Fifo fifoBC = PiMMUserFactory.instance.createFifo(outputB, inputC, "void");
    fifoBC.assignDelay(delayBC);
    chainedActorGraph.addDelay(delayBC);
    // Set delay to fifo CD
    final Delay delayCD = PiMMUserFactory.instance.createDelay();
    delayCD.setExpression(4);
    final Fifo fifoCD = PiMMUserFactory.instance.createFifo(outputC, inputD, "void");
    fifoCD.assignDelay(delayCD);
    chainedActorGraph.addDelay(delayCD);
    // Create a list for the 3 fifos to easily add them to the top graph
    final List<Fifo> fifosList = Arrays.asList(fifoAB, fifoBC, fifoCD);
    // Add fifos to the top graph
    for (final Fifo fifo : fifosList) {
      chainedActorGraph.addFifo(fifo);
    }
    // Add a parameter to actor B
    final Parameter parameter = PiMMUserFactory.instance.createParameter("useless", 2);
    final ConfigInputPort configInputB = PiMMUserFactory.instance.createConfigInputPort();
    chainB.getConfigInputPorts().add(configInputB);
    final Dependency dependency = PiMMUserFactory.instance.createDependency(parameter, configInputB);
    chainedActorGraph.addParameter(parameter);
    chainedActorGraph.addDependency(dependency);
    // Return top graph
    return chainedActorGraph;
  }

}
