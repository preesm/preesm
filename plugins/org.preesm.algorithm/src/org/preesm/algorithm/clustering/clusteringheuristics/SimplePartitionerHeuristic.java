package org.preesm.algorithm.clustering.clusteringheuristics;

import java.util.Map;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.clustering.scape.EuclideTransfo;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class SimplePartitionerHeuristic extends PartitionerHeuristic {

  /**
   * Number of equivalent Processing Elements
   */
  long nPEs;

  /**
   * basic repetition vector, one value for each vertex of the graph
   */
  Map<AbstractVertex, Long> brv;

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {

    super.initHeuristicParameters(graph, scenario, arch, taskParameters);

    // Computing number of equivalent cores
    this.nPEs = EuclideTransfo.computeSingleNodeCoreEquivalent(scenario);

    // Computing the basic repetition vector of the graph
    this.brv = PiBRV.compute(graph, BRVMethod.LCM);
  }

  @Override
  public void balanceFirings(PiGraph topgraph, PiGraph cluster) {

    // Computing how much execution of the subgraph there is in the cluster
    // For example, if actor A is repeating 8 times and actor B 16 times, then clusterRepetition will be equal to 8.

    final long clusterRepetition = MathFunctionsHelper
        .gcd(cluster.getActors().stream().filter(a -> brv.get(a) != null).map(a -> brv.get(a)).toList());

    // Computing the number of execution of the subgraph there will be in the cluster, according to the number of PEs.
    // For example, if there is 8 execution of the subgraph and 8 PEs, scale will be equal to 2.
    final long scale = ClusteringHelper.computeScalingFactor(cluster, clusterRepetition, this.nPEs);

    // Modifying the expressions of the ports of interfaces
    for (final DataInterface iActor : cluster.getDataInterfaces()) {

      // Computing the expression (it will be the same for the input and output port of the interface actor

      final Long expr = iActor.getGraphPort().getExpression().evaluateAsLong() * clusterRepetition / scale;

      // top graph ports values modifications
      iActor.getGraphPort().setExpression(expr);

      // subgraph port values modifications
      iActor.getDataPort().setExpression(expr);
    }

  }

}
