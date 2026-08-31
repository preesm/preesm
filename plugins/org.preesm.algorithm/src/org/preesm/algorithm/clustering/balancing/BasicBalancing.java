package org.preesm.algorithm.clustering.balancing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusterCreationTask;
import org.preesm.algorithm.clustering.heuristics.BalancingHeuristic;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This {@link BalancingHeuristic heuristic} is made to balance weights of a cluster, in the most simple way possible.
 * To keep this code simple, there are assumptions made when calling it. Firstly, the balancing has to work without
 * duplicating the cluster. Secondly, there will be no {@link SpecialActor special actors} added to unlock potential
 * memory reuse around and in the cluster.
 *
 * @author rcazoulat
 */
public class BasicBalancing extends BalancingHeuristic {

  /**
   * Basic repetition vector, one value for each vertex of the graph
   */
  Map<AbstractVertex, Long> brv;

  boolean verbose;

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);
    brv = PiBRV.compute(graph, BRVMethod.LCM);
    verbose = taskParameters == null || "true".equalsIgnoreCase(taskParameters.get(ClusterCreationTask.PARAM_VERBOSE));
  }

  @Override
  public List<PiGraph> balanceFirings(PiGraph topgraph, PiGraph cluster, long nPEs) {

    if (topgraph == null) {
      throw new PreesmRuntimeException("top graph is null");
    }

    if (cluster == null) {
      throw new PreesmRuntimeException("cluster1 is null");
    }

    // Logs helper
    final List<Long> clusterOldExprs = cluster.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong())
        .toList();
    String log;

    final List<PiGraph> clusters = new ArrayList<>();
    clusters.add(cluster);

    // Computing how much execution of the subgraph there is in the cluster
    // For example, if actor A is repeating 8 times and actor B 16 times, then clusterRepetition will be equal to 8.
    final long clusterRepetition = brv.get(cluster);

    // if nPEs is not a divisor of clusterRepetition, rest != 0
    final long rest = clusterRepetition % nPEs;
    if (rest != 0) {
      throw new PreesmRuntimeException(
          "cluster repetition is not divisible by nPEs. Consider using Complete Balancing Heuristic instead");
    }

    // clusterRepetition, without the rest. Used to compute scale and scale1

    // Number of time the cluster will be repeated in top graph, without rest
    final long scale = MathFunctionsHelper.gcd(clusterRepetition, nPEs);

    // Number of repetition of subgraph in cluster,
    final long ratio = clusterRepetition / scale;

    // Log
    if (verbose) {
      log = "[Partitioning] > rest = " + rest + ", scale = " + scale + ", repetition = " + clusterRepetition
          + ", ratio = " + ratio;
      PreesmLogger.getLogger().info(log);
    }

    // ------------------------------------------------------------------------------------------- //
    // Modifying the expressions of the ports of interfaces
    // ------------------------------------------------------------------------------------------- //
    for (final DataInterface dataInterface : cluster.getDataInterfaces()) {

      long expr;

      // Case if dataInterface is an input interface
      if (dataInterface instanceof DataInputInterface) {
        expr = dataInterface.getDataPort().getFifo().getTargetPort().getExpression().evaluateAsLong() * ratio;
      } else {
        expr = dataInterface.getDataPort().getFifo().getSourcePort().getExpression().evaluateAsLong() * ratio;
      }

      // Top graph & subgraph ports values modifications
      dataInterface.getGraphPort().setExpression(expr);
      dataInterface.getDataPort().setExpression(expr);
    }

    // Log -> track cluster1 modification
    if (verbose) {
      log = BalancingHelper.makeCompareLog(cluster, topgraph, clusterOldExprs);
      PreesmLogger.getLogger().info(log);
    }

    return clusters;

  }

}
