/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020 - 2022)
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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiGraphFiringBalancer;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;

/**
 * @author dgageot
 *
 */
public class PiGraphFiringBalancerTest {

  private PiGraph       topGraph;
  private PiGraph       subGraph;
  private AbstractActor actorA;
  private AbstractActor actorB;
  private AbstractActor actorC;
  private AbstractActor actorD;

  /**
   * Set-up the test environnement
   */
  @Before
  public void setUp() {
    // Create a test environment
    createTestEnvironment();
  }

  /**
   * Teardown the test environnement
   */
  @After
  public void tearDown() {
    this.topGraph = null;
    this.subGraph = null;
    this.actorA = null;
    this.actorB = null;
    this.actorC = null;
    this.actorD = null;
  }

  @Test
  public void testFactor2() {
    new PiGraphFiringBalancer(this.subGraph, 2).balance();
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);

    System.out.println("Taille de subGraph 2: " + brv.get(this.subGraph));
    System.out.println("Taille de actorB 2: " + brv.get(this.actorB));
    System.out.println("Taille de actorC 2: " + brv.get(this.actorC));
    assertEquals(Long.valueOf(128), brv.get(this.subGraph));
    assertEquals(Long.valueOf(2), brv.get(this.actorB));
    assertEquals(Long.valueOf(2), brv.get(this.actorC));
  }

  @Test
  public void testFactor4() {
    new PiGraphFiringBalancer(this.subGraph, 4).balance();
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);
    System.out.println("Taille de subGraph 4: " + brv.get(this.subGraph));
    System.out.println("Taille de actorB 4: " + brv.get(this.actorB));
    System.out.println("Taille de actorC 4: " + brv.get(this.actorC));
    assertEquals(Long.valueOf(64), brv.get(this.subGraph));
    assertEquals(Long.valueOf(4), brv.get(this.actorB));
    assertEquals(Long.valueOf(4), brv.get(this.actorC));
  }

  @Test
  public void testFactor8() {
    new PiGraphFiringBalancer(this.subGraph, 8).balance();
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);
    assertEquals(Long.valueOf(32), brv.get(this.subGraph));
    assertEquals(Long.valueOf(8), brv.get(this.actorB));
    assertEquals(Long.valueOf(8), brv.get(this.actorC));
  }

  @Test
  public void testFactor16() {
    new PiGraphFiringBalancer(this.subGraph, 16).balance();
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);
    assertEquals(Long.valueOf(16), brv.get(this.subGraph));
    assertEquals(Long.valueOf(16), brv.get(this.actorB));
    assertEquals(Long.valueOf(16), brv.get(this.actorC));
  }

  @Test
  public void testExceptionFactor() {
    try {
      new PiGraphFiringBalancer(this.subGraph, 15).balance();
      assertTrue(false);
    } catch (final PreesmRuntimeException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testExceptionGraph() {
    Assert.assertThrows(PreesmRuntimeException.class, () -> new PiGraphFiringBalancer(this.topGraph, 16));
    Assert.assertThrows(PreesmRuntimeException.class, () -> new PiGraphFiringBalancer(null, 16));
  }

  private void createTestEnvironment() {
    // Create the top graph
    this.topGraph = PiMMUserFactory.instance.createPiGraph();
    this.topGraph.setName("topgraph");
    this.topGraph.setUrl("topgraph");

    // Create actors
    this.actorA = PiMMUserFactory.instance.createActor("A");
    this.actorB = PiMMUserFactory.instance.createActor("B");
    this.actorC = PiMMUserFactory.instance.createActor("C");
    this.actorD = PiMMUserFactory.instance.createActor("D");

    // Create a list for the actors to easily add them to the top graph
    final List<AbstractActor> actorsList = Arrays.asList(this.actorA, this.actorB, this.actorC, this.actorD);

    // Add actors to the top graph
    actorsList.stream().forEach(x -> this.topGraph.addActor(x));

    // Create data output and input ports
    final DataOutputPort outputA = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputB = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputC = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataInputPort inputB = PiMMUserFactory.instance.createDataInputPort("in");
    final DataInputPort inputC = PiMMUserFactory.instance.createDataInputPort("in");
    final DataInputPort inputD = PiMMUserFactory.instance.createDataInputPort("in");

    // Attach them to actors
    this.actorA.getDataOutputPorts().add(outputA);
    this.actorB.getDataInputPorts().add(inputB);
    this.actorB.getDataOutputPorts().add(outputB);
    this.actorC.getDataInputPorts().add(inputC);
    this.actorC.getDataOutputPorts().add(outputC);
    this.actorD.getDataInputPorts().add(inputD);

    // Create fifos and form a chain such as A -> B -> C -> D
    final Fifo fifoAB = PiMMUserFactory.instance.createFifo(outputA, inputB, "void");
    final Fifo fifoBC = PiMMUserFactory.instance.createFifo(outputB, inputC, "void");
    final Fifo fifoCD = PiMMUserFactory.instance.createFifo(outputC, inputD, "void");

    // Create a list for the fifos to easily add them to the top graph
    final List<Fifo> fifosList = Arrays.asList(fifoAB, fifoBC, fifoCD);

    // Add fifos to the top graph
    fifosList.stream().forEach(x -> this.topGraph.addFifo(x));

    // Setup data output and input ports rates
    outputA.setExpression(256);
    inputB.setExpression(1);
    outputB.setExpression(1);
    inputC.setExpression(1);
    outputC.setExpression(1);
    inputD.setExpression(256);

    // Regroup under the same hierarchy actors B and C
    this.subGraph = new PiSDFSubgraphBuilder(this.topGraph, Arrays.asList(this.actorB, this.actorC), "subgraph_0")
        .build();

    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(this.topGraph);
  }

}
