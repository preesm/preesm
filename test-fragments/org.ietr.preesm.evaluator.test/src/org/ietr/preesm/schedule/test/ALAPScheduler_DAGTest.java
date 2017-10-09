package org.ietr.preesm.schedule.test;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.schedule.ALAPScheduler_DAG;
import org.ietr.preesm.schedule.ASAPScheduler_DAG;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of ALAPScheduler_DAG class
 * 
 * @author hderoui
 *
 */
public class ALAPScheduler_DAGTest {

  @Test
  public void testIterationDurationShouldBeComputed() {
    // generate a DAG
    SDFGraph dag = generateDAGOfGraphABC326();

    // schedule the DAG by an ASAP to get the throughput constraint
    ASAPScheduler_DAG asap = new ASAPScheduler_DAG();
    double ThConstraint = asap.schedule(dag);
    // check the throughput constraint
    Assert.assertEquals(12.0, ThConstraint, 0);
    // check the start date of each actor
    Assert.assertEquals(0.0, asap.simulator.getStartDate(dag.getVertex("A")), 0);
    Assert.assertEquals(0.0, asap.simulator.getStartDate(dag.getVertex("B")), 0);
    Assert.assertEquals(5.0, asap.simulator.getStartDate(dag.getVertex("C")), 0);

    asap.simulator.resetExecutionCounter();

    // ALAP schedule the DAG
    ALAPScheduler_DAG alap = new ALAPScheduler_DAG();
    double durationOf1Iteration = alap.schedule(dag, asap.simulator, ThConstraint);

    // check the value of the duration
    Assert.assertEquals(12.0, durationOf1Iteration, 0);

    // check the start date of each actor
    Assert.assertEquals(0.0, alap.simulator.getStartDate(dag.getVertex("A")), 0);
    Assert.assertEquals(3.0, alap.simulator.getStartDate(dag.getVertex("B")), 0);
    Assert.assertEquals(5.0, alap.simulator.getStartDate(dag.getVertex("C")), 0);
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
    SDFGraph graph = new SDFGraph();
    graph.setName("testABC");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, 1, 5., null, null);
    GraphStructureHelper.addActor(graph, "B", null, 1, 2., null, null);
    GraphStructureHelper.addActor(graph, "C", null, 1, 7., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "C", null, 1, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 1, 1, 0, null);

    return graph;
  }
}
