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
 * cluster/subgraph} in its topg-raph, according to the number of available processing elements.
 *
 * @author rcazoulat
 */
public abstract class BalancingHeuristic extends Heuristic {

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
  public abstract List<PiGraph> balanceFirings(PiGraph topgraph, PiGraph cluster, long nPEs);

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
    String log;
    final List<String> portsName = actor.getAllDataPorts().stream().map(Port::getName).toList();
    final List<
        Long> newExprs = actor.getAllDataPorts().stream().map(dp -> dp.getExpression().evaluateAsLong()).toList();
    if (oldExprs == null) {
      log = "[Partitioning] >  Creating " + actor.getName() + " in " + containingGraph.getName() + " : ";
      for (int i = 0; i < portsName.size(); i++) {
        log += "\n       Port \"" + portsName.get(i) + "\", val = " + newExprs.get(i);
        log += defineProdConsLog(actor, portsName.get(i));
      }
    } else {
      final int nIter = Math.min(newExprs.size(), oldExprs.size());
      log = "[Partitioning] >  Modifying " + actor.getName() + " in " + containingGraph.getName() + " : ";
      for (int i = 0; i < nIter; i++) {
        log += "\n       Port \"" + portsName.get(i) + "\", old val = " + oldExprs.get(i) + ", new val = "
            + newExprs.get(i);
        log += defineProdConsLog(actor, portsName.get(i));

      }

      if (newExprs.size() < oldExprs.size()) {
        for (int i = nIter; i < oldExprs.size(); i++) {
          log += "\n       Unknown port removed. Old val = " + oldExprs.get(i);

        }
      } else {
        for (int i = nIter; i < newExprs.size(); i++) {
          log += "\n       Port \"" + portsName.get(i) + "\" added. Val = " + newExprs.get(i);
          log += defineProdConsLog(actor, portsName.get(i));

        }
      }
    }
    return log;
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
