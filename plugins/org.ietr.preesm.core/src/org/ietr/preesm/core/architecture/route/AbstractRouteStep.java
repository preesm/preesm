/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2016)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.core.architecture.route;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// TODO: Auto-generated Javadoc
/**
 * Represents a single step in a route between two operators.
 *
 * @author mpelcat
 */
public abstract class AbstractRouteStep {

  /**
   * The sender of the route step. A route step is always directed.
   */
  private ComponentInstance sender;

  /** The receiver of the route step. */
  private ComponentInstance receiver;

  /**
   * Instantiates a new abstract route step.
   *
   * @param sender
   *          the sender
   * @param receiver
   *          the receiver
   */
  public AbstractRouteStep(final ComponentInstance sender, final ComponentInstance receiver) {
    super();
    this.sender = sender;
    this.receiver = receiver;
  }

  /**
   * Gets the receiver.
   *
   * @return the receiver
   */
  public ComponentInstance getReceiver() {
    return this.receiver;
  }

  /**
   * Gets the sender.
   *
   * @return the sender
   */
  public ComponentInstance getSender() {
    return this.sender;
  }

  /**
   * Sets the receiver.
   *
   * @param receiver
   *          the new receiver
   */
  public void setReceiver(final ComponentInstance receiver) {
    this.receiver = receiver;
  }

  /**
   * Sets the sender.
   *
   * @param sender
   *          the new sender
   */
  public void setSender(final ComponentInstance sender) {
    this.sender = sender;
  }

  /**
   * The route step type determines how the communication will be simulated.
   *
   * @return the type
   */
  public abstract String getType();

  /**
   * The id is given to code generation. It selects the communication functions to use
   *
   * @return the id
   */
  public abstract String getId();

  /**
   * The name of the step node is retrieved.
   *
   * @return the name
   */
  public abstract String getName();

  /**
   * Evaluates the cost of a data transfer with size transferSize. This cost can include overheads, involvements...
   *
   * @param transfersSize
   *          the transfers size
   * @return the transfer cost
   */
  public abstract long getTransferCost(long transfersSize);

  /**
   * Returns the longest time a contention node needs to transfer the data.
   *
   * @param transfersSize
   *          the transfers size
   * @return the worst transfer time
   */
  public abstract long getWorstTransferTime(long transfersSize);

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#clone()
   */
  @Override
  protected abstract Object clone() throws CloneNotSupportedException;

  /**
   * Appends the route step informations to a dom3 xml file.
   *
   * @param dom
   *          the dom
   * @param comFct
   *          the com fct
   */
  public void appendRouteStep(final Document dom, final Element comFct) {

    final Element routeStep = dom.createElement("routeStep");
    comFct.appendChild(routeStep);

    final Element sender = dom.createElement("sender");
    sender.setAttribute("name", getSender().getInstanceName());
    sender.setAttribute("def", getSender().getComponent().getVlnv().getName());
    routeStep.appendChild(sender);

    final Element receiver = dom.createElement("receiver");
    receiver.setAttribute("name", getReceiver().getInstanceName());
    receiver.setAttribute("def", getReceiver().getComponent().getVlnv().getName());
    routeStep.appendChild(receiver);

    if (getType() == DmaRouteStep.type) {
      routeStep.setAttribute("type", "dma");
      final DmaRouteStep dStep = (DmaRouteStep) this;
      routeStep.setAttribute("dmaDef", dStep.getDma().getVlnv().getName());

      for (final ComponentInstance node : dStep.getNodes()) {
        final Element eNode = dom.createElement("node");
        eNode.setAttribute("name", node.getInstanceName());
        eNode.setAttribute("def", node.getComponent().getVlnv().getName());
        routeStep.appendChild(eNode);
      }
    } else if (getType() == MessageRouteStep.type) {
      routeStep.setAttribute("type", "msg");
      final MessageRouteStep nStep = (MessageRouteStep) this;

      for (final ComponentInstance node : nStep.getNodes()) {
        final Element eNode = dom.createElement("node");
        eNode.setAttribute("name", node.getInstanceName());
        eNode.setAttribute("def", node.getComponent().getVlnv().getName());
        routeStep.appendChild(eNode);
      }
    } else if (getType() == MemRouteStep.type) {
      routeStep.setAttribute("type", "ram");
      final MemRouteStep rStep = (MemRouteStep) this;
      routeStep.setAttribute("ramDef", rStep.getMem().getVlnv().getName());
    }
  }
}
