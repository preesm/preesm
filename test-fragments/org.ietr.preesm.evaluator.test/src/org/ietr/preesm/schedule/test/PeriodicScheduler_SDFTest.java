package org.ietr.preesm.schedule.test;

import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.deadlock.SDFConsistency;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of PeriodicScheduler_SDF class
 * 
 * @author hderoui
 *
 */
public class PeriodicScheduler_SDFTest {

  @Test
  public void testPeriodicScheduleShouldBeComputed() {
    // generate a SDF graph
    SDFGraph sdf = generateSDFGraphABC326();

    // schedule the SDF graph
    PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    double throughput = periodic.schedule(sdf, null, true);

    // check the normalized rate of actors
    // Z=[t1=3, t2=2, t3=6, t4=6, t5=18]
    Assert.assertEquals(3, (double) sdf.getVertex("t1").getPropertyBean().getValue("normalizedRate"), 0);
    Assert.assertEquals(2, (double) sdf.getVertex("t2").getPropertyBean().getValue("normalizedRate"), 0);
    Assert.assertEquals(6, (double) sdf.getVertex("t3").getPropertyBean().getValue("normalizedRate"), 0);
    Assert.assertEquals(6, (double) sdf.getVertex("t4").getPropertyBean().getValue("normalizedRate"), 0);
    Assert.assertEquals(18, (double) sdf.getVertex("t5").getPropertyBean().getValue("normalizedRate"), 0);

    // check the value of the normalized period of the graph
    double k = ((Fraction) sdf.getPropertyBean().getValue("normalizedPeriod")).doubleValue();
    Assert.assertEquals(13, k, 0);

    // check the value of the throughput
    double throughputExpected = 1. / 234.;
    Assert.assertEquals(throughputExpected, throughput, 0);

    // check the execution period of actors
    // W(t1)=39, W(t2)=26, W(t3)=78, W(t4)=78, W(t5)=234
    Assert.assertEquals(39, (double) sdf.getVertex("t1").getPropertyBean().getValue("executionPeriod"), 0);
    Assert.assertEquals(26, (double) sdf.getVertex("t2").getPropertyBean().getValue("executionPeriod"), 0);
    Assert.assertEquals(78, (double) sdf.getVertex("t3").getPropertyBean().getValue("executionPeriod"), 0);
    Assert.assertEquals(78, (double) sdf.getVertex("t4").getPropertyBean().getValue("executionPeriod"), 0);
    Assert.assertEquals(234, (double) sdf.getVertex("t5").getPropertyBean().getValue("executionPeriod"), 0);

    // check the starting time of actors
    // s(t1)=0, s(t2)=0, s(t3)=54, s(t4)=56, s(t5)=222
    Assert.assertEquals(0, (double) sdf.getVertex("t1").getPropertyBean().getValue("firstExecutionStartDate"), 0);
    Assert.assertEquals(0, (double) sdf.getVertex("t2").getPropertyBean().getValue("firstExecutionStartDate"), 0);
    Assert.assertEquals(54, (double) sdf.getVertex("t3").getPropertyBean().getValue("firstExecutionStartDate"), 0);
    Assert.assertEquals(56, (double) sdf.getVertex("t4").getPropertyBean().getValue("firstExecutionStartDate"), 0);
    Assert.assertEquals(222, (double) sdf.getVertex("t5").getPropertyBean().getValue("firstExecutionStartDate"), 0);

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
    SDFGraph graph = new SDFGraph();
    graph.setName("testBenabid");

    // add actors
    GraphStructureHelper.addActor(graph, "t1", null, null, 2., null, null);
    GraphStructureHelper.addActor(graph, "t2", null, null, 2., null, null);
    GraphStructureHelper.addActor(graph, "t3", null, null, 2., null, null);
    GraphStructureHelper.addActor(graph, "t4", null, null, 10., null, null);
    GraphStructureHelper.addActor(graph, "t5", null, null, 12., null, null);

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
