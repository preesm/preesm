package org.preesm.algorithm.clustering.partionner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.util.PiGraphFiringBalancer;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.pisdf.util.URCSeeker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * This class provide heuristics to cluster input graph in order to balance coarse-grained and fine-grained parallelism.
 * 
 * @author dgageot
 *
 */
public class ClusterPartionner {

  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;
  /**
   * Workflow architecture.
   */
  private final Design   architecture;

  /**
   * Builds a ClusterPartionner object.
   * 
   * @param graph
   *          Input graph.
   * @param scenario
   *          Workflow scenario.
   * @param architecture
   *          Workflow architecture.
   */
  public ClusterPartionner(final PiGraph graph, final Scenario scenario, final Design architecture) {
    this.graph = graph;
    this.scenario = scenario;
    this.architecture = architecture;
  }

  /**
   * @return Resulting PiGraph.
   */
  public PiGraph cluster() {

    // Retrieve URC chains in input graph in which actors share the same component constraints.
    List<List<AbstractActor>> graphURCs = new URCSeeker(this.graph).seek();
    List<List<AbstractActor>> constrainedURCs = new LinkedList<>();
    for (List<AbstractActor> URC : graphURCs) {
      if (!ClusteringHelper.getListOfCommonComponent(URC, this.scenario).isEmpty()) {
        constrainedURCs.add(URC);
      }
    }

    // Cluster constrained URCs.
    long index = 0;
    List<PiGraph> subGraphs = new LinkedList<>();
    for (List<AbstractActor> URC : graphURCs) {
      PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, URC, "urc_" + index++).build();
      subGraph.setClusterValue(true);
      // Add constraint to the cluster
      for (ComponentInstance component : ClusteringHelper.getListOfCommonComponent(URC, scenario)) {
        scenario.getConstraints().addConstraint(component, subGraph);
      }
      subGraphs.add(subGraph);
    }

    // TODO Parse the architecture model to determine the max number of PE in a compute cluster.
    // 16 is for targeting the Kalray MPPA.
    final long maxPE = 16;

    // Compute BRV and balance firing between coarse-grained and fine-grained parallelism.
    Map<AbstractVertex, Long> brv = PiBRV.compute(this.graph, BRVMethod.LCM);
    for (final PiGraph subgraph : subGraphs) {
      double repetitionCount = brv.get(subgraph);
      long factor = 2;
      while ((Math.floor(repetitionCount / factor) == Math.ceil(repetitionCount / factor)) && (factor < maxPE)) {
        factor = factor * 2;
      }
      new PiGraphFiringBalancer(subgraph, factor).balance();
    }

    return this.graph;
  }

}
