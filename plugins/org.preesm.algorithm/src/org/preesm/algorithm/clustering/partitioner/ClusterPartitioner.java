package org.preesm.algorithm.clustering.partitioner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
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

/**
 * This class provide an algorithm to cluster a PiSDF graph and balance actor firings of clustered actor between coarse
 * and fine-grained parallelism. Resulting clusters are marked as PiSDF cluster, they have to be schedule with the
 * Cluster Scheduler.
 * 
 * @author dgageot
 *
 */
public class ClusterPartitioner {

  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;
  /**
   * Number of PEs in compute clusters.
   */
  private final int      numberOfPEs;

  /**
   * Builds a ClusterPartitioner object.
   * 
   * @param graph
   *          Input graph.
   * @param scenario
   *          Workflow scenario.
   * @param numberOfPEs
   *          Number of processing elements in compute clusters.
   */
  public ClusterPartitioner(final PiGraph graph, final Scenario scenario, final int numberOfPEs) {
    this.graph = graph;
    this.scenario = scenario;
    this.numberOfPEs = numberOfPEs;
  }

  /**
   * @return Clustered PiGraph.
   */
  public PiGraph cluster() {

    // TODO: Look for actor groups other than URC chains.
    // Retrieve URC chains in input graph and verify that actors share component constraints.
    List<List<AbstractActor>> graphURCs = new URCSeeker(this.graph).seek();
    List<List<AbstractActor>> constrainedURCs = new LinkedList<>();
    for (List<AbstractActor> URC : graphURCs) {
      if (!ClusteringHelper.getListOfCommonComponent(URC, this.scenario).isEmpty()) {
        constrainedURCs.add(URC);
      }
    }

    // Cluster constrained URC chains.
    long index = 0;
    List<PiGraph> subGraphs = new LinkedList<>();
    for (List<AbstractActor> URC : graphURCs) {
      PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, URC, "urc_" + index++).build();
      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (ComponentInstance component : ClusteringHelper.getListOfCommonComponent(URC, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
      subGraphs.add(subGraph);
    }

    // Compute BRV and balance actor firings between coarse and fine-grained parallelism.
    Map<AbstractVertex, Long> brv = PiBRV.compute(this.graph, BRVMethod.LCM);
    for (final PiGraph subgraph : subGraphs) {
      long factor = MathFunctionsHelper.gcd(brv.get(subgraph), this.numberOfPEs);
      String message = String.format("%1$s: firings balanced by %3$d, leaving %2$d firings at coarse-grained.",
          subgraph.getName(), brv.get(subgraph) / factor, factor);
      PreesmLogger.getLogger().log(Level.INFO, message);
      new PiGraphFiringBalancer(subgraph, factor).balance();
    }

    return this.graph;
  }

}
