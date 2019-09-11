/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2016)
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
  protected AbstractRouteStep(final ComponentInstance sender, final ComponentInstance receiver) {
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
  public abstract RouteStepType getType();

}
