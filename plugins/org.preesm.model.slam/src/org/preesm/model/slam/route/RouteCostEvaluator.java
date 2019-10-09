/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
  public static final double evaluateTransferCost(final SlamRoute route, final long transferSize) {
    return new SlamRouteStepCostFactorEvaluator().doSwitch(route) * (double) transferSize;
  }

  /**
   * Evaluates the cost of a data transfer with size transferSize. This cost can include overheads, involvements...
   *
   * @param transferSize
   *          the transfers size
   * @return the transfer cost
   */
  public static final double getTransferCost(final SlamRouteStep routeStep, final long transferSize) {
    return new SlamRouteStepCostFactorEvaluator().doSwitch(routeStep) * (double) transferSize;
  }

  /**
   * Returns the longest time a contention node needs to transfer the data before the RAM in the route steps.
   *
   * @param transfersSize
   *          the transfers size
   * @return the sender side worst transfer time
   */
  public static double getSenderSideWorstTransferTime(final SlamMemoryRouteStep memRouteStep,
      final long transfersSize) {
    double time = 0;

    for (final ComponentInstance node : memRouteStep.getSenderSideContentionNodes()) {
      time = Math.max(time, ((double) transfersSize / ((ComNode) node.getComponent()).getSpeed()));
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
  public static double getReceiverSideWorstTransferTime(final SlamMemoryRouteStep memRouteStep,
      final long transfersSize) {
    double time = 0;

    for (final ComponentInstance node : memRouteStep.getReceiverSideContentionNodes()) {
      time = Math.max(time, ((double) transfersSize / ((ComNode) node.getComponent()).getSpeed()));
    }
    return time;
  }

  /**
   *
   * @author anmorvan
   *
   */
  private static class SlamRouteStepCostFactorEvaluator extends SlamSwitch<Double> {

    @Override
    public Double caseSlamRoute(SlamRoute route) {
      double cost = 0;
      // Iterating the route and incrementing transfer cost
      for (final SlamRouteStep step : route.getRouteSteps()) {
        cost += doSwitch(step);
      }
      return cost;
    }

    @Override
    public Double caseSlamMessageRouteStep(SlamMessageRouteStep msg) {
      double time = 0;
      for (final ComponentInstance node : msg.getNodes()) {
        final Component def = node.getComponent();
        if (def instanceof ComNode) {
          final ComNode comNode = (ComNode) def;
          time = Math.max(time, (1d / comNode.getSpeed()));
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
