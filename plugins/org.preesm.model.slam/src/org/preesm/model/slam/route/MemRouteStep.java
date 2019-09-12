/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2012)
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

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.ComponentInstance;

/**
 * Route step where the sender uses a shared RAM to send data.
 *
 * @author mpelcat
 */
public class MemRouteStep extends MessageRouteStep {

  private final ComponentInstance mem;

  /**
   * Index of the communication node connected to the shared ram. This is the index of the ComponentInstance in
   * {@link MessageRouteStep.nodes}
   */
  private int ramNodeIndex = -1;

  /**
   * Instantiates a new mem route step.
   *
   * @param sender
   *          the sender
   * @param nodes
   *          the nodes
   * @param receiver
   *          the receiver
   * @param mem
   *          the mem
   * @param ramNodeIndex
   *          the ram node index
   */
  MemRouteStep(final ComponentInstance sender, final List<ComponentInstance> nodes, final ComponentInstance receiver,
      final ComponentInstance mem, final int ramNodeIndex) {
    super(sender, nodes, receiver);
    this.mem = mem;
    this.ramNodeIndex = ramNodeIndex;
  }

  @Override
  public RouteStepType getType() {
    return RouteStepType.MEM_TYPE;
  }

  public ComponentInstance getMem() {
    return this.mem;
  }

  @Override
  public String toString() {
    String trace = super.toString();
    trace = trace.substring(0, trace.length() - 1);
    trace += "[" + this.mem + "]}";
    return trace;
  }

  /**
   * Gets the sender side contention nodes.
   *
   * @return the sender side contention nodes
   */
  public List<ComponentInstance> getSenderSideContentionNodes() {
    final List<ComponentInstance> contentionNodes = new ArrayList<>();
    for (int i = 0; i <= this.ramNodeIndex; i++) {
      final ComponentInstance node = this.nodes.get(i);
      if (!((ComNode) node.getComponent()).isParallel()) {
        contentionNodes.add(node);
      }
    }
    return contentionNodes;
  }

  /**
   * Gets the receiver side contention nodes.
   *
   * @return the receiver side contention nodes
   */
  public List<ComponentInstance> getReceiverSideContentionNodes() {
    final List<ComponentInstance> contentionNodes = new ArrayList<>();
    for (int i = this.ramNodeIndex; i < this.nodes.size(); i++) {
      final ComponentInstance node = this.nodes.get(i);
      if (!((ComNode) node.getComponent()).isParallel()) {
        contentionNodes.add(node);
      }
    }
    return contentionNodes;
  }
}
