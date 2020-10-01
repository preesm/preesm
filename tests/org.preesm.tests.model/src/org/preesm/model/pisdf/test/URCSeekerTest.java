/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.URCSeeker;

/**
 * @author dgageot
 *
 */
public class URCSeekerTest {

  private PiGraph                   topGraph;
  private AbstractActor             actorA;
  private AbstractActor             actorB;
  private AbstractActor             actorC;
  private AbstractActor             actorD;
  private AbstractActor             actorE;
  private AbstractActor             actorF;
  private AbstractActor             actorG;
  private URCSeeker                 seeker;
  private List<List<AbstractActor>> seekerResults;

  /**
   * Set-up the test environnement
   */
  @Before
  public void setUp() {
    // Create a chained actors PiGraph
    createChainedActorsPiGraph();
    // Build the URC seeker
    this.seeker = new URCSeeker(this.topGraph);
    // Retrieve list of URC chain in the graph
    seekerResults = this.seeker.seek();
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
    this.actorE = null;
    this.actorF = null;
    this.actorG = null;
    this.seeker = null;
  }

  @Test
  public void testExpectToFoundTwoURCs() {
    assertEquals(this.seekerResults.size(), 2);
  }

  @Test
  public void testExpectToFoundChainBCD() {
    List<AbstractActor> expectedChain = Arrays.asList(this.actorB, this.actorC, this.actorD);
    assertTrue(this.seekerResults.contains(expectedChain));
  }

  @Test
  public void testExpectToFoundChainEFG() {
    List<AbstractActor> expectedChain = Arrays.asList(this.actorE, this.actorF, this.actorG);
    assertTrue(this.seekerResults.contains(expectedChain));
  }

  @Test
  public void testDoesntExpectToFoundChainDE() {
    List<AbstractActor> expectedChain = Arrays.asList(this.actorD, this.actorE);
    assertFalse(this.seekerResults.contains(expectedChain));
  }

  private void createChainedActorsPiGraph() {
    // Create the top graph
    this.topGraph = PiMMUserFactory.instance.createPiGraph();
    this.topGraph.setName("topgraph");
    this.topGraph.setUrl("topgraph");

    // Create actors
    this.actorA = PiMMUserFactory.instance.createActor("A");
    this.actorB = PiMMUserFactory.instance.createActor("B");
    this.actorC = PiMMUserFactory.instance.createActor("C");
    this.actorD = PiMMUserFactory.instance.createActor("D");
    this.actorE = PiMMUserFactory.instance.createActor("E");
    this.actorF = PiMMUserFactory.instance.createActor("F");
    this.actorG = PiMMUserFactory.instance.createActor("G");

    // Create a list for the actors to easily add them to the top graph
    List<AbstractActor> actorsList = Arrays.asList(this.actorC, this.actorD, this.actorB, this.actorE, this.actorF,
        this.actorA, this.actorG);

    // Add actors to the top graph
    actorsList.stream().forEach(x -> this.topGraph.addActor(x));

    // Create data output and input ports
    DataOutputPort outputA = PiMMUserFactory.instance.createDataOutputPort("out");
    DataOutputPort outputB1 = PiMMUserFactory.instance.createDataOutputPort("out1");
    DataOutputPort outputB2 = PiMMUserFactory.instance.createDataOutputPort("out2");
    DataOutputPort outputC = PiMMUserFactory.instance.createDataOutputPort("out");
    DataOutputPort outputD = PiMMUserFactory.instance.createDataOutputPort("out");
    DataOutputPort outputE = PiMMUserFactory.instance.createDataOutputPort("out");
    DataOutputPort outputF = PiMMUserFactory.instance.createDataOutputPort("out");
    DataInputPort inputB = PiMMUserFactory.instance.createDataInputPort("in");
    DataInputPort inputC1 = PiMMUserFactory.instance.createDataInputPort("in1");
    DataInputPort inputC2 = PiMMUserFactory.instance.createDataInputPort("in2");
    DataInputPort inputD = PiMMUserFactory.instance.createDataInputPort("in");
    DataInputPort inputE = PiMMUserFactory.instance.createDataInputPort("in");
    DataInputPort inputF = PiMMUserFactory.instance.createDataInputPort("in");
    DataInputPort inputG = PiMMUserFactory.instance.createDataInputPort("in");

    // Attach them to actors
    this.actorA.getDataOutputPorts().add(outputA);
    this.actorB.getDataOutputPorts().add(outputB1);
    this.actorB.getDataOutputPorts().add(outputB2);
    this.actorC.getDataOutputPorts().add(outputC);
    this.actorD.getDataOutputPorts().add(outputD);
    this.actorE.getDataOutputPorts().add(outputE);
    this.actorF.getDataOutputPorts().add(outputF);
    this.actorB.getDataInputPorts().add(inputB);
    this.actorC.getDataInputPorts().add(inputC1);
    this.actorC.getDataInputPorts().add(inputC2);
    this.actorD.getDataInputPorts().add(inputD);
    this.actorE.getDataInputPorts().add(inputE);
    this.actorF.getDataInputPorts().add(inputF);
    this.actorG.getDataInputPorts().add(inputG);

    // Create fifos and form a chain such as A -> B -> (x2) C -> D -> (delay) E -> F -> G
    Fifo fifoAB = PiMMUserFactory.instance.createFifo(outputA, inputB, "void");
    Fifo fifoBC1 = PiMMUserFactory.instance.createFifo(outputB1, inputC1, "void");
    Fifo fifoBC2 = PiMMUserFactory.instance.createFifo(outputB2, inputC2, "void");
    Fifo fifoCD = PiMMUserFactory.instance.createFifo(outputC, inputD, "void");
    Fifo fifoDE = PiMMUserFactory.instance.createFifo(outputD, inputE, "void");
    Fifo fifoEF = PiMMUserFactory.instance.createFifo(outputE, inputF, "void");
    Fifo fifoFG = PiMMUserFactory.instance.createFifo(outputF, inputG, "void");

    // Create a list for the fifos to easily add them to the top graph
    List<Fifo> fifosList = Arrays.asList(fifoAB, fifoBC1, fifoBC2, fifoCD, fifoDE, fifoEF, fifoFG);

    // Add fifos to the top graph
    fifosList.stream().forEach(x -> this.topGraph.addFifo(x));

    // Setup data output and input ports rates
    outputA.setExpression(16);
    inputB.setExpression(2);
    outputB1.setExpression(2);
    outputB2.setExpression(3);
    inputC1.setExpression(2);
    inputC2.setExpression(3);
    outputC.setExpression(3);
    inputD.setExpression(3);
    outputD.setExpression(1);
    inputE.setExpression(1);
    outputE.setExpression(7);
    inputF.setExpression(7);
    outputF.setExpression(5);
    inputG.setExpression(5);

    // Set delay to fifo DE
    Delay delayDE = PiMMUserFactory.instance.createDelay();
    delayDE.setExpression(1);
    fifoDE.setDelay(delayDE);
    this.topGraph.addDelay(delayDE);

    // Check consistency of the graph
    PiGraphConsistenceChecker.check(this.topGraph);
  }

}
