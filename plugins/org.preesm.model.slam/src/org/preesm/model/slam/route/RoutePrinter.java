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
