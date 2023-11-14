/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.model.pisdf.AbstractActor;
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

  private PiGraph createGraphNoDAG() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

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
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "A")), preds);

    preds = helper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Collections.emptyList(), preds);

    preds = helper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C")), preds);
  }

  @Test
  public void testAllPredecessors() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "A")), preds);

    preds = helper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Collections.emptyList(), preds);

    preds = helper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C"),
        VertexPath.lookup(createGraph, "A")), preds);

  }

  @Test
  public void testAllPredecessorsNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      new PiSDFTopologyHelper(createGraph).getAllPredecessorsOf(VertexPath.lookup(createGraph, "A"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

  @Test
  public void testDirectSuccessors() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "D")), preds);

    preds = helper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C")), preds);

    preds = helper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Collections.emptyList(), preds);
  }

  @Test
  public void testAllSuccessors() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "B"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "D")), preds);

    preds = helper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "A"));
    Assert.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C"),
        VertexPath.lookup(createGraph, "D")), preds);

    preds = helper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "D"));
    Assert.assertEquals(Collections.emptyList(), preds);
  }

  @Test
  public void testAllSuccessorNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      new PiSDFTopologyHelper(createGraph).getAllSuccessorsOf(VertexPath.lookup(createGraph, "A"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

  @Test
  public void testAllOutFifos() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    List<Fifo> outFifos;
    final Fifo fifo;

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    outFifos = helper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "B"));
    assertEquals(1, outFifos.size());
    fifo = outFifos.get(0);
    assertEquals(VertexPath.lookup(createGraph, "B"), fifo.getTarget());
    assertEquals(VertexPath.lookup(createGraph, "A"), fifo.getSource());

    outFifos = helper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "D"));
    assertEquals(4, outFifos.size());

    outFifos = helper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "A"));
    assertEquals(0, outFifos.size());
  }

  @Test
  public void testAllInFifos() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    List<Fifo> outFifos;
    final Fifo fifo;

    outFifos = new PiSDFTopologyHelper(createGraph).getSuccessorEdgesOf(VertexPath.lookup(createGraph, "B"));
    assertEquals(1, outFifos.size());
    fifo = outFifos.get(0);
    assertEquals(VertexPath.lookup(createGraph, "D"), fifo.getTarget());
    assertEquals(VertexPath.lookup(createGraph, "B"), fifo.getSource());

  }

  @Test
  public void testIsPredecessor() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final boolean successor = helper.isPredecessor(VertexPath.lookup(createGraph, "D"),
        VertexPath.lookup(createGraph, "B"));
    assertFalse(successor);
    final boolean successor2 = helper.isPredecessor(VertexPath.lookup(createGraph, "B"),
        VertexPath.lookup(createGraph, "D"));
    assertTrue(successor2);
  }

  @Test
  public void testIsSuccessor() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final boolean successor = helper.isSuccessor(VertexPath.lookup(createGraph, "D"),
        VertexPath.lookup(createGraph, "B"));
    assertTrue(successor);
    final boolean successor2 = helper.isSuccessor(VertexPath.lookup(createGraph, "B"),
        VertexPath.lookup(createGraph, "D"));
    assertFalse(successor2);
  }

  @Test
  public void testAllOutFifosNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      new PiSDFTopologyHelper(createGraph).getSuccessorEdgesOf(VertexPath.lookup(createGraph, "B"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

  @Test
  public void testAllInFifosNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    try {
      new PiSDFTopologyHelper(createGraph).getPredecessorEdgesOf(VertexPath.lookup(createGraph, "B"));
      Assert.fail();
    } catch (final StackOverflowError e) {
      // success
    }
  }

}
