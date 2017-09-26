package org.ietr.preesm.schedule.test;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.schedule.ASAPScheduler_SDF;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test of ASAPScheduler_SDF class
 * 
 * @author hderoui
 *
 */
public class ASAPScheduler_SDFTest {

  @Test
  public void testIterationDurationShouldBeComputed() {
    // generate a DAG
    SDFGraph dag = generateSDFGraphABC326();

    // schedule the DAG
    ASAPScheduler_SDF scheduler = new ASAPScheduler_SDF();
    double durationOf1Iteration = scheduler.schedule(dag, null);

    // check the value of the duration
    Assert.assertEquals(3.0, durationOf1Iteration, 0);

  }

  /**
   * generates a normalized SDF graph
   * 
   * @return SDF graph
   */
  public SDFGraph generateSDFGraphABC326() {
    // Actors: A B C
    // Edges: AB=(2,3); BC=(3,1); CA=(1,2)
    // RV[A=3, B=2, C=6]
    // Actors duration: A=1, B=1, C=1
    // Duration of the first iteration = 3

    // create SDF graph testABC3
    SDFGraph graph = new SDFGraph();
    graph.setName("testABC3");

    // add actors
    GraphStructureHelper.addActor(graph, "A", null, 3, 1., null, null);
    GraphStructureHelper.addActor(graph, "B", null, 2, 1., null, null);
    GraphStructureHelper.addActor(graph, "C", null, 6, 1., null, null);

    // add edges
    GraphStructureHelper.addEdge(graph, "A", null, "B", null, 2, 3, 6, null);
    GraphStructureHelper.addEdge(graph, "B", null, "C", null, 3, 1, 0, null);
    GraphStructureHelper.addEdge(graph, "C", null, "A", null, 2, 4, 0, null);

    return graph;
  }
}
