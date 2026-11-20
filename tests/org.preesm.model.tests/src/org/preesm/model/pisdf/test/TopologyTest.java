/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2023)
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
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
  void testDirectPredecessors() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "B"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "A")), preds);

    preds = helper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "A"));
    Assertions.assertEquals(Collections.emptyList(), preds);

    preds = helper.getDirectPredecessorsOf(VertexPath.lookup(createGraph, "D"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C")),
        preds);
  }

  @Test
  void testAllPredecessors() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "B"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "A")), preds);

    preds = helper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "A"));
    Assertions.assertEquals(Collections.emptyList(), preds);

    preds = helper.getAllPredecessorsOf(VertexPath.lookup(createGraph, "D"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C"),
        VertexPath.lookup(createGraph, "A")), preds);

  }

  @Test
  void testAllPredecessorsNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final AbstractActor actor = VertexPath.lookup(createGraph, "A");

    Assertions.assertThrows(StackOverflowError.class, () -> helper.getAllPredecessorsOf(actor));
  }

  @Test
  void testDirectSuccessors() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "B"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "D")), preds);

    preds = helper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "A"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C")),
        preds);

    preds = helper.getDirectSuccessorsOf(VertexPath.lookup(createGraph, "D"));
    Assertions.assertEquals(Collections.emptyList(), preds);
  }

  @Test
  void testAllSuccessors() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    List<AbstractActor> preds = helper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "B"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "D")), preds);

    preds = helper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "A"));
    Assertions.assertEquals(Arrays.asList(VertexPath.lookup(createGraph, "B"), VertexPath.lookup(createGraph, "C"),
        VertexPath.lookup(createGraph, "D")), preds);

    preds = helper.getAllSuccessorsOf(VertexPath.lookup(createGraph, "D"));
    Assertions.assertEquals(Collections.emptyList(), preds);
  }

  @Test
  void testAllSuccessorNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final AbstractActor actor = VertexPath.lookup(createGraph, "A");

    Assertions.assertThrows(StackOverflowError.class, () -> helper.getAllSuccessorsOf(actor));
  }

  @Test
  void testAllOutFifos() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    List<Fifo> outFifos;
    final Fifo fifo;

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    outFifos = helper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "B"));
    Assertions.assertEquals(1, outFifos.size());
    fifo = outFifos.get(0);
    Assertions.assertEquals(VertexPath.lookup(createGraph, "B"), fifo.getTarget());
    Assertions.assertEquals(VertexPath.lookup(createGraph, "A"), fifo.getSource());

    outFifos = helper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "D"));
    Assertions.assertEquals(4, outFifos.size());

    outFifos = helper.getPredecessorEdgesOf(VertexPath.lookup(createGraph, "A"));
    Assertions.assertEquals(0, outFifos.size());
  }

  @Test
  void testAllInFifos() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    List<Fifo> outFifos;
    final Fifo fifo;

    outFifos = new PiSDFTopologyHelper(createGraph).getSuccessorEdgesOf(VertexPath.lookup(createGraph, "B"));
    Assertions.assertEquals(1, outFifos.size());
    fifo = outFifos.get(0);
    Assertions.assertEquals(VertexPath.lookup(createGraph, "D"), fifo.getTarget());
    Assertions.assertEquals(VertexPath.lookup(createGraph, "B"), fifo.getSource());

  }

  @Test
  void testIsPredecessor() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final boolean successor = helper.isPredecessor(VertexPath.lookup(createGraph, "D"),
        VertexPath.lookup(createGraph, "B"));
    Assertions.assertFalse(successor);
    final boolean successor2 = helper.isPredecessor(VertexPath.lookup(createGraph, "B"),
        VertexPath.lookup(createGraph, "D"));
    Assertions.assertTrue(successor2);
  }

  @Test
  void testIsSuccessor() {
    final PiGraph createGraph = PiGraphGenerator.createDiamondGraph();
    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final boolean successor = helper.isSuccessor(VertexPath.lookup(createGraph, "D"),
        VertexPath.lookup(createGraph, "B"));
    Assertions.assertTrue(successor);
    final boolean successor2 = helper.isSuccessor(VertexPath.lookup(createGraph, "B"),
        VertexPath.lookup(createGraph, "D"));
    Assertions.assertFalse(successor2);
  }

  @Test
  void testAllOutFifosNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final AbstractActor actor = VertexPath.lookup(createGraph, "B");

    Assertions.assertThrows(StackOverflowError.class, () -> helper.getSuccessorEdgesOf(actor));
  }

  @Test
  void testAllInFifosNoDAG() {
    final PiGraph createGraph = createGraphNoDAG();

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(createGraph);
    final AbstractActor actor = VertexPath.lookup(createGraph, "B");

    Assertions.assertThrows(StackOverflowError.class, () -> helper.getPredecessorEdgesOf(actor));
  }

}
