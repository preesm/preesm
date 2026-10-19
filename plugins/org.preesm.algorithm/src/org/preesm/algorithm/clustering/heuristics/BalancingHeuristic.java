package org.preesm.algorithm.clustering.heuristics;

import java.util.List;
import java.util.Objects;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;

/**
 * This abstract class is made to create {@link Heuristic heuristics} that will balance a {@link PiGraph
 * cluster/subgraph} in its topg-raph, according to the number of available processing elements. The implementation
 * classes are stored in the package clustering.balancing
 *
 *
 * @author rcazoulat
 */
public abstract class BalancingHeuristic implements Heuristic {

  public void balance(PiGraph topGraph, long nPEs) {

    /** Explore sub-graphs (cluster included) */
    for (final PiGraph child : topGraph.getChildrenGraphs()) {
      balance(child, nPEs);
    }

    /** Balance clusters */
    for (final PiGraph cluster : topGraph.getChildrenGraphs().stream().filter(g -> g.isCluster()).toList()) {
      balanceCluster(topGraph, cluster, nPEs);
    }
  }

  /**
   * This method will modify ports rates between the {@link AbstractActor actors} of the top-graph, and the graph ports'
   * cluster's rates in the top graph.
   *
   * @param topgraph
   *          The top-graph (parent graph)
   * @param cluster
   *          The cluster
   * @param nPEs
   *          The number of available processing elements
   * @return The list containing at least the modified cluster, and potential newly created clusters
   */
  protected abstract List<PiGraph> balanceCluster(PiGraph topgraph, PiGraph cluster, long nPEs);

  /**
   * Useful method to make logs if balancing is in verbose mode.
   *
   * @param actor
   *          Input actor
   * @param containingGraph
   *          parent graph
   * @param oldExprs
   *          old weights expressions. They will be compared to the new one, attached to the actors
   * @return The string log
   */
  public static String makeCompareLog(final AbstractActor actor, final PiGraph containingGraph,
      final List<Long> oldExprs) {
    final StringBuilder log = new StringBuilder();
    final List<String> portsName = actor.getAllDataPorts().stream().map(Port::getName).toList();
    final List<
        Long> newExprs = actor.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong()).toList();
    if (oldExprs == null) {
      log.append("[Partitioning] >  Creating " + actor.getName() + " in " + containingGraph.getName() + " : ");
      for (int i = 0; i < portsName.size(); i++) {
        log.append("\n       Port \"").append(portsName.get(i)).append("\", val = ").append(newExprs.get(i));
        log.append(defineProdConsLog(actor, portsName.get(i)));
      }
    } else {
      final int nIter = Math.min(newExprs.size(), oldExprs.size());
      log.append("[Partitioning] >  Modifying " + actor.getName() + " in " + containingGraph.getName() + " : ");
      for (int i = 0; i < nIter; i++) {
        log.append("\n       Port \"").append(portsName.get(i)).append("\", old val = ").append(oldExprs.get(i))
            .append(", new val = ").append(newExprs.get(i));
        log.append(defineProdConsLog(actor, portsName.get(i)));

      }

      if (newExprs.size() < oldExprs.size()) {
        for (int i = nIter; i < oldExprs.size(); i++) {
          log.append("\n       Unknown port removed. Old val = ").append(oldExprs.get(i));

        }
      } else {
        for (int i = nIter; i < newExprs.size(); i++) {
          log.append("\n       Port \"").append(portsName.get(i)).append("\" added. Val = ").append(newExprs.get(i));
          log.append(defineProdConsLog(actor, portsName.get(i)));

        }
      }
    }
    return log.toString();
  }

  /**
   * Pretty printer for production and consumption of a {@link fifo} attached to a specific {@link DataPort port} of an
   * {@link AbstractActor actor}.
   *
   * @param actor
   *          The actor
   * @param dpName
   *          the name of the port
   * @return the log of the production and consumption rate of the fifo attached to the port.
   */
  public static String defineProdConsLog(final AbstractActor actor, final String dpName) {
    String log = "";

    final DataPort dp = actor.getAllDataPorts().stream().filter(p -> Objects.equals(p.getName(), dpName)).toList()
        .getFirst();
    if (dp == null) {
      return log;
    }

    if (dp instanceof DataInputPort) {
      log += " <- out port \"" + dp.getFifo().getSourcePort().getName() + "\"of actor \""
          + dp.getFifo().getSource().getName() + "\"";
    } else {
      log += " -> in port \"" + dp.getFifo().getTargetPort().getName() + "\"of actor \""
          + dp.getFifo().getTarget().getName() + "\"";

    }
    return log;
  }
}
