/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.ietr.preesm.schedule.test;

import org.ietr.preesm.schedule.ASAPSchedulerDAG;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.model.sdf.SDFGraph;

/**
 * Unit test of ASAPScheduler_DAG class
 *
 * @author hderoui
 *
 */
public class ASAPSchedulerDAGTest {

  @Test
  public void testIterationDurationShouldBeComputed() {
    // generate a DAG
    final SDFGraph dag = generateDAGOfGraphABC326();

    // schedule the DAG
    final ASAPSchedulerDAG scheduler = new ASAPSchedulerDAG();
    final double durationOf1Iteration = scheduler.schedule(dag);

    // check the value of the duration
    Assert.assertEquals(3.0, durationOf1Iteration, 0);

    // check the start date of each actor
    Assert.assertEquals(0.0, scheduler.simulator.getStartDate(dag.getVertex("B1")), 0);
    Assert.assertEquals(0.0, scheduler.simulator.getStartDate(dag.getVertex("B2")), 0);
    Assert.assertEquals(1.0, scheduler.simulator.getStartDate(dag.getVertex("C1")), 0);
    Assert.assertEquals(1.0, scheduler.simulator.getStartDate(dag.getVertex("C2")), 0);
    Assert.assertEquals(1.0, scheduler.simulator.getStartDate(dag.getVertex("C3")), 0);
    Assert.assertEquals(1.0, scheduler.simulator.getStartDate(dag.getVertex("C4")), 0);
    Assert.assertEquals(1.0, scheduler.simulator.getStartDate(dag.getVertex("C5")), 0);
    Assert.assertEquals(1.0, scheduler.simulator.getStartDate(dag.getVertex("C6")), 0);
    Assert.assertEquals(2.0, scheduler.simulator.getStartDate(dag.getVertex("A1")), 0);
    Assert.assertEquals(2.0, scheduler.simulator.getStartDate(dag.getVertex("A2")), 0);
    Assert.assertEquals(2.0, scheduler.simulator.getStartDate(dag.getVertex("A3")), 0);

  }

  /**
   * generates a normalized SDF graph
   *
   * @return SDF graph
   */
  public SDFGraph generateDAGOfGraphABC326() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]
    // Actors duration: A=1, B=1, C=1
    // Duration of the first iteration = 3

    // create SDF graph testABC3
    final SDFGraph graph = new SDFGraph();
    graph.setName("testABC3");

    // add actors
    GraphStructureHelper.addActor(graph, "A1", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "A2", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "A3", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "B1", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "B2", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "C1", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "C2", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "C3", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "C4", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "C5", null, 1L, 1., 0, null);
    GraphStructureHelper.addActor(graph, "C6", null, 1L, 1., 0, null);

    // add edges

    // these edges are removed since we return a DAG not a srSDF
    // GraphStructureHelper.addEdge(graph, "A1", null, "B1", null, 1, 1, 1, null);
    // GraphStructureHelper.addEdge(graph, "A2", null, "B1", null, 1, 1, 1, null);
    // GraphStructureHelper.addEdge(graph, "A2", null, "B2", null, 1, 1, 1, null);
    // GraphStructureHelper.addEdge(graph, "A3", null, "B2", null, 1, 1, 1, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B1", null, "C3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C4", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C5", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B2", null, "C6", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C1", null, "A1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C2", null, "A1", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C3", null, "A2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C4", null, "A2", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C5", null, "A3", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C6", null, "A3", null, 1, 1, 0, null);

    return graph;
  }
}
