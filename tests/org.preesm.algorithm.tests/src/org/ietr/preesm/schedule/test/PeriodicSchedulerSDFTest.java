/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hamza Deroui [hamza.deroui@insa-rennes.fr] (2017)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

import org.apache.commons.lang3.math.Fraction;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.deadlock.SDFConsistency;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.schedule.sdf.PeriodicSchedulerSDF;
import org.preesm.algorithm.throughput.sdf.tools.GraphStructureHelper;

/**
 * Unit test of PeriodicScheduler_SDF class
 *
 * @author hderoui
 *
 */
public class PeriodicSchedulerSDFTest {

  private static final String NORMALIZED_RATE       = "normalizedRate";
  private static final String EXECUTION_PERIOD      = "executionPeriod";
  private static final String FIRST_EXEC_START_DATE = "firstExecutionStartDate";

  @Test
  public void testPeriodicScheduleShouldBeComputed() {
    // generate a SDF graph
    final SDFGraph sdf = generateSDFGraphABC326();

    // schedule the SDF graph
    final PeriodicSchedulerSDF periodic = new PeriodicSchedulerSDF();
    final double throughput = periodic.schedule(sdf, null, true);

    // check the normalized rate of actors
    // Z=[t1=3, t2=2, t3=6, t4=6, t5=18]
    Assert.assertEquals(3, (double) sdf.getVertex("t1").getPropertyBean().getValue(NORMALIZED_RATE), 0);
    Assert.assertEquals(2, (double) sdf.getVertex("t2").getPropertyBean().getValue(NORMALIZED_RATE), 0);
    Assert.assertEquals(6, (double) sdf.getVertex("t3").getPropertyBean().getValue(NORMALIZED_RATE), 0);
    Assert.assertEquals(6, (double) sdf.getVertex("t4").getPropertyBean().getValue(NORMALIZED_RATE), 0);
    Assert.assertEquals(18, (double) sdf.getVertex("t5").getPropertyBean().getValue(NORMALIZED_RATE), 0);

    // check the value of the normalized period of the graph
    final double k = ((Fraction) sdf.getPropertyBean().getValue("normalizedPeriod")).doubleValue();
    Assert.assertEquals(13, k, 0);

    // check the value of the throughput
    final double throughputExpected = 1. / 234.;
    Assert.assertEquals(throughputExpected, throughput, 0);

    // check the execution period of actors
    // W(t1)=39, W(t2)=26, W(t3)=78, W(t4)=78, W(t5)=234
    Assert.assertEquals(39, (double) sdf.getVertex("t1").getPropertyBean().getValue(EXECUTION_PERIOD), 0);
    Assert.assertEquals(26, (double) sdf.getVertex("t2").getPropertyBean().getValue(EXECUTION_PERIOD), 0);
    Assert.assertEquals(78, (double) sdf.getVertex("t3").getPropertyBean().getValue(EXECUTION_PERIOD), 0);
    Assert.assertEquals(78, (double) sdf.getVertex("t4").getPropertyBean().getValue(EXECUTION_PERIOD), 0);
    Assert.assertEquals(234, (double) sdf.getVertex("t5").getPropertyBean().getValue(EXECUTION_PERIOD), 0);

    // check the starting time of actors
    // s(t1)=0, s(t2)=0, s(t3)=54, s(t4)=56, s(t5)=222
    Assert.assertEquals(0, (double) sdf.getVertex("t1").getPropertyBean().getValue(FIRST_EXEC_START_DATE), 0);
    Assert.assertEquals(0, (double) sdf.getVertex("t2").getPropertyBean().getValue(FIRST_EXEC_START_DATE), 0);
    Assert.assertEquals(54, (double) sdf.getVertex("t3").getPropertyBean().getValue(FIRST_EXEC_START_DATE), 0);
    Assert.assertEquals(56, (double) sdf.getVertex("t4").getPropertyBean().getValue(FIRST_EXEC_START_DATE), 0);
    Assert.assertEquals(222, (double) sdf.getVertex("t5").getPropertyBean().getValue(FIRST_EXEC_START_DATE), 0);

  }

  /**
   * generates a normalized SDF graph
   *
   * @return SDF graph
   */
  public SDFGraph generateSDFGraphABC326() {
    // the graph example of Benabid et al. paper

    // Actors: t1 t2 t3 t4 t5

    // Edges: p1(t1,t3)=(1,2); p2(t2,t3)=(1,3); p3(t4,t1)=(2,1); p4(t3,t4)=(1,1);
    // p5(t4,t2)=(3,1); p6(t4,t5)=(1,3); p7(t5,t1)=(6,1); p8(t5,t2)=(9,1)

    // initial marking : M0(P1)=0; M0(P2)=0; M0(P3)=4; M0(P4)=0;
    // M0(P5)=6; M0(P6)=0; M0(P7)=6; M0(P8)=9

    // RV[t1=6, t2=9, t3=3, t4=3, t5=1]
    // Actors duration: t1=2, t2=2, t3=2, t4=10, t5=12

    // Normalized Rate: Z=[t1=3, t2=2, t3=6, t4=6, t5=18]
    // Normalized period of the graph (Average token flow time): K= 13
    // Execution period of actors: W(t1)=39, W(t2)=26, W(t3)=78, W(t4)=78, W(t5)=234
    // Throughput: Th = 1/(13*18) = 1/234
    // Starting time of actors: s(t1)=0, s(t2)=0, s(t3)=54, s(t4)=56, s(t5)=222

    // create SDF graph test Benabid
    final SDFGraph graph = new SDFGraph();
    graph.setName("testBenabid");

    // add actors
    GraphStructureHelper.addActor(graph, "t1", null, 0, 2., 0, null);
    GraphStructureHelper.addActor(graph, "t2", null, 0, 2., 0, null);
    GraphStructureHelper.addActor(graph, "t3", null, 0, 2., 0, null);
    GraphStructureHelper.addActor(graph, "t4", null, 0, 10., 0, null);
    GraphStructureHelper.addActor(graph, "t5", null, 0, 12., 0, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "t1", null, "t3", null, 1, 2, 0, null);
    GraphStructureHelper.addEdge(graph, "t2", null, "t3", null, 1, 3, 0, null);
    GraphStructureHelper.addEdge(graph, "t3", null, "t4", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "t4", null, "t1", null, 2, 1, 4, null);
    GraphStructureHelper.addEdge(graph, "t4", null, "t2", null, 3, 1, 6, null);
    GraphStructureHelper.addEdge(graph, "t4", null, "t5", null, 1, 3, 0, null);
    GraphStructureHelper.addEdge(graph, "t5", null, "t1", null, 6, 1, 6, null);
    GraphStructureHelper.addEdge(graph, "t5", null, "t2", null, 9, 1, 9, null);

    SDFConsistency.computeRV(graph);
    return graph;
  }
}
