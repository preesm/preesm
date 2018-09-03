/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2012)
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
package org.ietr.preesm.core.architecture.route;

import java.util.List;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.component.Dma;

// TODO: Auto-generated Javadoc
/**
 * Route step where the sender uses a dma to send data in parallel with its processing.
 *
 * @author mpelcat
 */
public class DmaRouteStep extends MessageRouteStep {

  /** The dma. */
  private final Dma dma;

  /**
   * The route step type determines how the communication will be simulated.
   */
  public static final String type = "DmaRouteStep";

  /**
   * Instantiates a new dma route step.
   *
   * @param sender
   *          the sender
   * @param nodes
   *          the nodes
   * @param receiver
   *          the receiver
   * @param dma
   *          the dma
   */
  public DmaRouteStep(final ComponentInstance sender, final List<ComponentInstance> nodes,
      final ComponentInstance receiver, final Dma dma) {
    super(sender, nodes, receiver);
    this.dma = dma;
  }

  /**
   * The route step type determines how the communication will be simulated.
   *
   * @return the type
   */
  @Override
  public String getType() {
    return DmaRouteStep.type;
  }

  /**
   * Gets the dma.
   *
   * @return the dma
   */
  public Dma getDma() {
    return this.dma;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.core.architecture.route.MessageRouteStep#toString()
   */
  @Override
  public String toString() {
    String trace = super.toString();
    trace = trace.substring(0, trace.length() - 1);
    trace += "[" + this.dma + "]}";
    return trace;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.core.architecture.route.MessageRouteStep#clone()
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new DmaRouteStep(getSender(), getNodes(), getReceiver(), this.dma);
  }
}
