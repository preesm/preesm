package org.ietr.preesm.throughput;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.helpers.Stopwatch;
import org.ietr.preesm.throughput.parsers.Identifier;
import org.ietr.preesm.throughput.transformers.SDFTransformer;

/**
 * @author hderoui
 *
 */
public class HPeriodicSchedule {

  public Stopwatch timer;
  // private PreesmScenario preesmScenario;

  /**
   * @param inputGraph
   *          IBSDF graph
   * @return throughput of the graph
   */
  public double evaluate(SDFGraph inputGraph) {
    // this.preesmScenario = scenario;

    // add the name property for each edge of the graph
    for (SDFEdge e : inputGraph.edgeSet()) {
      e.setPropertyValue("edgeName", Identifier.generateEdgeId());
    }

    System.out.println("Computing the throughput of the graph using Hierarchical Periodic Schedule ...");
    this.timer = new Stopwatch();
    timer.start();

    // Step 1: define the execution duration of each hierarchical actor and add a self loop to it
    System.out.println("Step 1: define the execution duration of each hierarchical actor");
    for (SDFAbstractVertex actor : inputGraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        Double duration = this.setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
        // if (this.preesmScenario != null) {
        // this.preesmScenario.getTimingManager().setTiming(actor.getId(), "x86", duration.longValue());
        // }
        // add the self loop
        GraphStructureHelper.addEdge(inputGraph, actor.getName(), null, actor.getName(), null, 1, 1, 1, null);
      }
    }

    // Step 3: compute the throughput with the Periodic Schedule
    System.out.println("Step 4: compute the throughput using the Periodic Schedule");
    PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    double throughput = periodic.computeGraphThroughput(inputGraph, null, false);
    timer.stop();
    System.out.println("Throughput of the graph = " + throughput + " computed in " + timer.toString());

    return throughput;
  }

  /**
   * Computes the duration of a subgraph
   * 
   * @param subgraph
   *          subgraph of a hierarchical actor
   * @return the duration of the subgraph
   */
  public double setHierarchicalActorsDuration(SDFGraph subgraph) {

    // add the name property for each edge of the graph
    for (SDFEdge e : subgraph.edgeSet()) {
      e.setPropertyValue("edgeName", Identifier.generateEdgeId());
    }

    // recursive function
    for (SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        Double duration = this.setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
        // this.preesmScenario.getTimingManager().setTiming(actor.getId(), "x86", duration.longValue());
        // add the self loop
        GraphStructureHelper.addEdge(subgraph, actor.getName(), null, actor.getName(), null, 1, 1, 1, null);
      }
    }

    // compute the subgraph period using the periodic schedule
    // Step 4: compute the throughput with the Periodic Schedule
    System.out.println("Step 4: compute the throughput using the Periodic Schedule");
    // normalize the graph
    SDFGraph g = subgraph;
    // SDFGraph g = SDFTransformer.convertToSrSDF(subgraph);
    SDFTransformer.normalize(g);
    // compute its normalized period K
    PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    periodic.computeNormalizedPeriod(g, null);
    // compute the subgraph period
    return periodic.computeGraphPeriod(g);
  }

}
