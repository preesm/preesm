package org.preesm.model.slam.route;

import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.SlamMemoryRouteStep;
import org.preesm.model.slam.SlamMessageRouteStep;
import org.preesm.model.slam.SlamRoute;
import org.preesm.model.slam.SlamRouteStep;
import org.preesm.model.slam.util.SlamSwitch;

/**
 *
 * @author anmorvan
 *
 */
public class RouteCostEvaluator {

  private RouteCostEvaluator() {
    // forbid instantiation
  }

  /**
   * Evaluates the cost of a data transfer with size transferSize along the route.
   *
   * @param transferSize
   *          the transfer size
   * @return the long
   */
  public static final long evaluateTransferCost(final SlamRoute route, final long transferSize) {
    return new SlamRouteStepCostEvaluator(transferSize).doSwitch(route);
  }

  /**
   * Evaluates the cost of a data transfer with size transferSize. This cost can include overheads, involvements...
   *
   * @param transfersSize
   *          the transfers size
   * @return the transfer cost
   */
  public static final long getTransferCost(final SlamRouteStep routeStep, final long transfersSize) {
    return new SlamRouteStepCostEvaluator(transfersSize).doSwitch(routeStep);
  }

  /**
   * Returns the longest time a contention node needs to transfer the data before the RAM in the route steps.
   *
   * @param transfersSize
   *          the transfers size
   * @return the sender side worst transfer time
   */
  public static long getSenderSideWorstTransferTime(final SlamMemoryRouteStep memRouteStep, final long transfersSize) {
    long time = 0;

    for (final ComponentInstance node : memRouteStep.getSenderSideContentionNodes()) {
      time = Math.max(time, (long) (transfersSize / ((ComNode) node.getComponent()).getSpeed()));
    }
    return time;
  }

  /**
   * Returns the longest time a contention node needs to transfer the data after the RAM in the route steps.
   *
   * @param transfersSize
   *          the transfers size
   * @return the receiver side worst transfer time
   */
  public static long getReceiverSideWorstTransferTime(final SlamMemoryRouteStep memRouteStep,
      final long transfersSize) {
    long time = 0;

    for (final ComponentInstance node : memRouteStep.getReceiverSideContentionNodes()) {
      time = Math.max(time, (long) (transfersSize / ((ComNode) node.getComponent()).getSpeed()));
    }
    return time;
  }

  /**
   *
   * @author anmorvan
   *
   */
  private static class SlamRouteStepCostEvaluator extends SlamSwitch<Long> {

    private long transferSize;

    SlamRouteStepCostEvaluator(final long transferSize) {
      this.transferSize = transferSize;
    }

    @Override
    public Long caseSlamRoute(SlamRoute route) {
      long cost = 0;
      // Iterating the route and incrementing transfer cost
      for (final SlamRouteStep step : route.getRouteSteps()) {
        cost += doSwitch(step);
      }
      return cost;
    }

    @Override
    public Long caseSlamMessageRouteStep(SlamMessageRouteStep msg) {
      long time = 0;
      for (final ComponentInstance node : msg.getNodes()) {
        final Component def = node.getComponent();
        if (def instanceof ComNode) {
          final ComNode comNode = (ComNode) def;
          time = Math.max(time, (long) (transferSize / comNode.getSpeed()));
        }
      }

      // No zero transfer time is alloweds
      if (time <= 0) {
        time = 1;
      }
      return time;
    }

  }

}
