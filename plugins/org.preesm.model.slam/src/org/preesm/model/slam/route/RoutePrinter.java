package org.preesm.model.slam.route;

import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.SlamDMARouteStep;
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
public class RoutePrinter extends SlamSwitch<String> {

  public static final String print(final SlamRouteStep step) {
    return new RoutePrinter().doSwitch(step);
  }

  public static final String print(final SlamRoute route) {
    return new RoutePrinter().doSwitch(route);
  }

  @Override
  public String caseSlamRoute(final SlamRoute route) {
    final StringBuilder sb = new StringBuilder();
    for (final SlamRouteStep step : route.getRouteSteps()) {
      sb.append(doSwitch(step) + " ");
    }
    return sb.toString();
  }

  @Override
  public String caseSlamMessageRouteStep(final SlamMessageRouteStep msgStep) {
    final StringBuilder trace = new StringBuilder("{" + msgStep.getSender().toString() + " -> ");
    for (final ComponentInstance node : msgStep.getNodes()) {
      trace.append(node.getInstanceName() + " ");
    }
    trace.append("-> " + msgStep.getReceiver().toString() + "}");
    return trace.toString();
  }

  @Override
  public String caseSlamDMARouteStep(final SlamDMARouteStep dmaStep) {
    String trace = caseSlamMessageRouteStep(dmaStep);
    trace = trace.substring(0, trace.length() - 1);
    trace += "[" + dmaStep.getDma() + "]}";
    return trace;
  }

  @Override
  public String caseSlamMemoryRouteStep(final SlamMemoryRouteStep memStep) {
    String trace = caseSlamMessageRouteStep(memStep);
    trace = trace.substring(0, trace.length() - 1);
    trace += "[" + memStep.getMemory() + "]}";
    return trace;
  }

  @Override
  public String caseSlamRouteStep(final SlamRouteStep step) {
    throw new UnsupportedOperationException();
  }
}
