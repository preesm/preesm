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
package org.ietr.preesm.clustering.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.preesm.algorithm.clustering.ClusterCreator;
import org.preesm.algorithm.clustering.balancing.CompleteBalancing;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * @author dgageot
 *
 */
public class CompleteBalancingTest {

  private PiGraph        topGraph;
  private PiGraph        subGraph;
  private AbstractActor  actorA;
  private AbstractActor  actorB;
  private AbstractActor  actorC;
  private AbstractActor  actorD;
  private BroadcastActor brdActor;
  private AbstractActor  brdProducer;

  private final CompleteBalancing balancer = new CompleteBalancing();

  /**
   * Set-up the test environnement
   */
  @Before
  public void setUp() {
    // Create a test environment
    createTestEnvironment();
    balancer.initHeuristicParameters(topGraph, null, null, null);
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
    balancer.balanceFirings(topGraph, subGraph, 2);
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);
    assertEquals(Long.valueOf(2), brv.get(this.subGraph));
    assertEquals(Long.valueOf(128), brv.get(this.actorB));
    assertEquals(Long.valueOf(128), brv.get(this.actorC));
  }

  @Test
  public void testFactor4() {
    balancer.balanceFirings(topGraph, subGraph, 4);
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);
    assertEquals(Long.valueOf(4), brv.get(this.subGraph));
    assertEquals(Long.valueOf(64), brv.get(this.actorB));
    assertEquals(Long.valueOf(64), brv.get(this.actorC));
  }

  @Test
  public void testFactor8() {
    balancer.balanceFirings(topGraph, subGraph, 8);
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);
    assertEquals(Long.valueOf(8), brv.get(this.subGraph));
    assertEquals(Long.valueOf(32), brv.get(this.actorB));
    assertEquals(Long.valueOf(32), brv.get(this.actorC));
  }

  @Test
  public void testFactor16() {
    balancer.balanceFirings(topGraph, subGraph, 16);
    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);
    assertEquals(Long.valueOf(16), brv.get(this.subGraph));
    assertEquals(Long.valueOf(16), brv.get(this.actorB));
    assertEquals(Long.valueOf(16), brv.get(this.actorC));
  }

  @Test
  public void testFactor15() {
    final List<PiGraph> clusters = balancer.balanceFirings(topGraph, subGraph, 15);

    assertEquals(2, clusters.size());

    final Map<AbstractVertex, Long> brv = PiBRV.compute(topGraph, BRVMethod.LCM);

    assertEquals(Long.valueOf(14), brv.get(clusters.get(0)));
    assertEquals(Long.valueOf(1), brv.get(clusters.get(1)));

    assertEquals(Long.valueOf(17), brv.get(this.actorB));
    assertEquals(Long.valueOf(17), brv.get(this.actorC));

  }

  @Test
  public void testBrd() {
    final List<PiGraph> clusters = balancer.balanceFirings(topGraph, subGraph, 4);
    assertEquals(1, clusters.size());

    final PiGraph cluster = clusters.getFirst();

    assertEquals(1, brdActor.getDataInputPorts().getFirst().getExpression().evaluateAsLong());
    assertEquals(4, brdActor.getDataOutputPorts().getFirst().getExpression().evaluateAsLong());
    assertEquals(1, brdActor.getDataOutputPorts().getFirst().getOppositePort().getExpression().evaluateAsLong());

    final boolean isBrdPresentInCluster = cluster.getActors().stream().anyMatch(BroadcastActor.class::isInstance);
    assertTrue(isBrdPresentInCluster);

    final BroadcastActor brdInCluster = (BroadcastActor) cluster.getActors().stream()
        .filter(a -> a instanceof BroadcastActor).toList().getFirst();

    assertEquals(1, brdInCluster.getDataInputPorts().getFirst().getExpression().evaluateAsLong());
    assertEquals(256 / 4, brdInCluster.getDataOutputPorts().getFirst().getExpression().evaluateAsLong());
    assertEquals(1, brdInCluster.getDataOutputPorts().getFirst().getOppositePort().getExpression().evaluateAsLong());

  }

  @Test
  public void testExceptionGraph() {
    Assert.assertThrows(PreesmRuntimeException.class, () -> balancer.balanceFirings(null, subGraph, 15));
    Assert.assertThrows(PreesmRuntimeException.class, () -> balancer.balanceFirings(topGraph, null, 15));
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
    this.brdActor = PiMMUserFactory.instance.createBroadcastActor("brd");
    this.brdProducer = PiMMUserFactory.instance.createActor("prod");

    // Create a list for the actors to easily add them to the top graph
    final List<AbstractActor> actorsList = Arrays.asList(this.actorA, this.actorB, this.actorC, this.actorD,
        this.brdActor, this.brdProducer);

    // Add actors to the top graph
    actorsList.stream().forEach(x -> this.topGraph.addActor(x));

    // Create data output and input ports
    final DataOutputPort outputA = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputB = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputC = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputBrd = PiMMUserFactory.instance.createDataOutputPort("out");
    final DataOutputPort outputProd = PiMMUserFactory.instance.createDataOutputPort("out");

    final DataInputPort inputB1 = PiMMUserFactory.instance.createDataInputPort("in1");
    final DataInputPort inputB2 = PiMMUserFactory.instance.createDataInputPort("in2");
    final DataInputPort inputC = PiMMUserFactory.instance.createDataInputPort("in");
    final DataInputPort inputD = PiMMUserFactory.instance.createDataInputPort("in");
    final DataInputPort inputBrd = PiMMUserFactory.instance.createDataInputPort("in");

    // Attach them to actors
    this.actorA.getDataOutputPorts().add(outputA);
    this.actorB.getDataInputPorts().add(inputB1);
    this.actorB.getDataInputPorts().add(inputB2);
    this.actorB.getDataOutputPorts().add(outputB);
    this.actorC.getDataInputPorts().add(inputC);
    this.actorC.getDataOutputPorts().add(outputC);
    this.actorD.getDataInputPorts().add(inputD);
    this.brdActor.getDataInputPorts().add(inputBrd);
    this.brdActor.getDataOutputPorts().add(outputBrd);
    this.brdProducer.getDataOutputPorts().add(outputProd);

    // Create fifos and form a chain such as A -> B -> C -> D
    final Fifo fifoAB = PiMMUserFactory.instance.createFifo(outputA, inputB1, "void");
    final Fifo fifoBrdB = PiMMUserFactory.instance.createFifo(outputBrd, inputB2, "void");
    final Fifo fifoProdBrd = PiMMUserFactory.instance.createFifo(outputProd, inputBrd, "void");
    final Fifo fifoBC = PiMMUserFactory.instance.createFifo(outputB, inputC, "void");
    final Fifo fifoCD = PiMMUserFactory.instance.createFifo(outputC, inputD, "void");

    // Create a list for the fifos to easily add them to the top graph
    final List<Fifo> fifosList = Arrays.asList(fifoAB, fifoBC, fifoCD, fifoBrdB, fifoProdBrd);

    // Add fifos to the top graph
    fifosList.stream().forEach(x -> this.topGraph.addFifo(x));

    // Setup data output and input ports rates
    outputA.setExpression(256);
    inputB1.setExpression(1);
    inputB2.setExpression(1);
    outputB.setExpression(1);
    inputC.setExpression(1);
    outputC.setExpression(1);
    inputD.setExpression(256);
    inputBrd.setExpression(1);
    outputBrd.setExpression(256);
    outputProd.setExpression(1);

    // Regroup under the same hierarchy actors B and C
    final Set<AbstractActor> set = new HashSet<>();
    set.add(actorB);
    set.add(actorC);
    this.subGraph = ClusterCreator.create(topGraph, set, "subgraph_0");

    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(this.topGraph);
  }

}
