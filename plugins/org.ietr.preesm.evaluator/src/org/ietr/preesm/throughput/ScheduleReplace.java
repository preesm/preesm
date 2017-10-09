package org.ietr.preesm.throughput;

import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.schedule.ASAPScheduler_SDF;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.helpers.Stopwatch;
import org.ietr.preesm.throughput.transformers.SDFTransformer;

/**
 * @author hderoui
 *
 */
public class ScheduleReplace {

  public Stopwatch       timer;
  private PreesmScenario preesmScenario;

  /**
   * @param inputGraph
   *          IBSDF graph
   * @param scenario
   *          contains actors duration
   * @return throughput of the graph
   */
  public double evaluate(SDFGraph inputGraph, PreesmScenario scenario) {
    this.preesmScenario = scenario;
    System.out.println("Computing the throughput of the graph using Schedule-Replace technique ...");
    this.timer = new Stopwatch();
    timer.start();

    // Step 1: define the execution duration of each hierarchical actor
    System.out.println("Step 1: define the execution duration of each hierarchical actor");
    for (SDFAbstractVertex actor : inputGraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        Double duration = this.setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
        if (preesmScenario != null) {
          this.preesmScenario.getTimingManager().setTiming(actor.getId(), "x86", duration.longValue());
        }
      }
    }

    // Step 2: convert the topGraph to a srSDF graph
    System.out.println("Step 2: convert the topGraph to a srSDF graph");
    SDFGraph srSDF = SDFTransformer.convertToSrSDF(inputGraph);

    // Step 3: add a self loop edge to each hierarchical actor
    System.out.println("Step 3: add a self loop edge to each hierarchical actor");
    for (SDFAbstractVertex actor : srSDF.vertexSet()) {
      SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      if (baseActor.getGraphDescription() != null) {
        GraphStructureHelper.addEdge(srSDF, actor.getName(), null, actor.getName(), null, 1, 1, 1, null);
      }
    }

    // Step 4: compute the throughput with the Periodic Schedule
    System.out.println("Step 4: compute the throughput using the Periodic Schedule");
    // normalize the graph
    SDFTransformer.normalize(srSDF);
    // compute its normalized period K
    PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);
    // compute its throughput as 1/K
    double throughput = 1 / k.doubleValue();
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
    // recursive function
    for (SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        Double duration = this.setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
        this.preesmScenario.getTimingManager().setTiming(actor.getId(), "x86", duration.longValue());
      }
    }

    // compute the subgraph duration using an ASAP schedule
    ASAPScheduler_SDF asap = new ASAPScheduler_SDF();
    return asap.schedule(subgraph, this.preesmScenario);
  }

}
