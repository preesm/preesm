package org.preesm.model.slam.route;

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
    for (final AbstractRouteStep step : route) {
      cost += step.getTransferCost(transferSize);
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
    // TODO
    return 0;
  }

  /**
   * Returns the longest time a contention node needs to transfer the data.
   *
   * @param transfersSize
   *          the transfers size
   * @return the worst transfer time
   */
  public long getWorstTransferTime(long transfersSize) {
    // TODO
    return 0;
  }

}
