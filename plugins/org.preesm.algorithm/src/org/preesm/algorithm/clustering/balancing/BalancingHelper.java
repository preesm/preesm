package org.preesm.algorithm.clustering.balancing;

import java.util.List;
import java.util.Objects;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;

/**
 * Helper class for balancing weights of clusters only.
 *
 * @author rcazoulat
 */
public class BalancingHelper {
  private BalancingHelper() {
  }

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
