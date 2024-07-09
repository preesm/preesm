/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2022) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hamza Deroui [hamza.deroui@insa-rennes.fr] (2017)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
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

import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.schedule.sdf.ALAPSchedulerDAG;
import org.preesm.algorithm.schedule.sdf.ASAPSchedulerDAG;
import org.preesm.algorithm.throughput.sdf.tools.GraphStructureHelper;

/**
 * Unit test of ALAPScheduler_DAG class
 *
 * @author hderoui
 *
 */
public class ALAPSchedulerDAGTest {

  @Test
  public void testIterationDurationShouldBeComputed() {
    // generate a DAG
    final SDFGraph dag = generateDAGOfGraphABC326();

    // schedule the DAG by an ASAP to get the throughput constraint
    final ASAPSchedulerDAG asap = new ASAPSchedulerDAG();
    final double ThConstraint = asap.schedule(dag);
    // check the throughput constraint
    Assert.assertEquals(12.0, ThConstraint, 0);
    // check the start date of each actor
    Assert.assertEquals(0.0, asap.getSimulator().getStartDate(dag.getVertex("A")), 0);
    Assert.assertEquals(0.0, asap.getSimulator().getStartDate(dag.getVertex("B")), 0);
    Assert.assertEquals(5.0, asap.getSimulator().getStartDate(dag.getVertex("C")), 0);

    asap.getSimulator().resetExecutionCounter();

    // ALAP schedule the DAG
    final ALAPSchedulerDAG alap = new ALAPSchedulerDAG();
    final double durationOf1Iteration = alap.schedule(dag, asap.getSimulator(), ThConstraint);

    // check the value of the duration
    Assert.assertEquals(12.0, durationOf1Iteration, 0);

    // check the start date of each actor
    Assert.assertEquals(0.0, alap.getSimulator().getStartDate(dag.getVertex("A")), 0);
    Assert.assertEquals(3.0, alap.getSimulator().getStartDate(dag.getVertex("B")), 0);
    Assert.assertEquals(5.0, alap.getSimulator().getStartDate(dag.getVertex("C")), 0);
  }

  /**
   * generates a normalized SDF graph
   *
   * @return SDF graph
   */
  public SDFGraph generateDAGOfGraphABC326() {
    // Actors: A B C
    // Edges: AC=(1,1); BC=(1,1);
    // RV[A=1, B=1, C=1]
    // Actors duration: A=5, B=2, C=7
    // Duration of the first iteration = 12

    // create DAG testABC
    final SDFGraph graph = new SDFGraph();
    graph.setName("testABC");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, 1L, 5., 0, null);
    GraphStructureHelper.addActor(graph, "B", null, 1L, 2., 0, null);
    GraphStructureHelper.addActor(graph, "C", null, 1L, 7., 0, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 1, 1, 0, null);

    return graph;
  }
}
