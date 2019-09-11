package org.preesm.model.slam.route;

import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;

/**
 *
 * @author anmorvan
 *
 */
public class RouteCostEvaluator {

  /**
   * Evaluates the cost of a data transfer with size transferSize along the route.
   *
   * @param transferSize
   *          the transfer size
   * @return the long
   */
  public static final long evaluateTransferCost(final Route route, final long transferSize) {
    long cost = 0;
    // Iterating the route and incrementing transfer cost
    for (final AbstractRouteStep step : route.getRouteSteps()) {
      cost += getTransferCost(step, transferSize);
    }
    return cost;
  }

  /**
   * Evaluates the cost of a data transfer with size transferSize. This cost can include overheads, involvements...
   *
   * @param transfersSize
   *          the transfers size
   * @return the transfer cost
   */
  public static final long getTransferCost(final AbstractRouteStep routeStep, final long transfersSize) {
    return new RouteStepCostEvaluator(transfersSize).doSwitch(routeStep);
  }

  /**
   *
   * TODO : make a Ecore switch
   *
   * @author anmorvan
   *
   */
  private static class RouteStepCostEvaluator {

    private long transferSize;

    RouteStepCostEvaluator(final long transferSize) {
      this.transferSize = transferSize;
    }

    public long doSwitch(Object o) {
      if (o instanceof MessageRouteStep) {
        return caseMessageRouteStep((MessageRouteStep) o);
      }
      return -1L;
    }

    public long caseMessageRouteStep(final MessageRouteStep msg) {
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
