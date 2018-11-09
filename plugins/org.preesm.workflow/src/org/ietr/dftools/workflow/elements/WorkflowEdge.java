/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.workflow.elements;

/**
 * An edge between two workflow tasks.
 *
 * @author mpelcat
 */

public class WorkflowEdge {

  /** Name of the output port of the source that must correspond to the name in the corresponding task prototype. */
  private String sourcePort = null;

  /** Name of the input port of the target that must correspond to the name in the corresponding task prototype. */
  private String targetPort = null;

  /** Object carrying the data. */
  private Object data = null;

  /**
   * Gets the data.
   *
   * @return the data
   */
  public Object getData() {
    return this.data;
  }

  /**
   * Sets the data.
   *
   * @param data
   *          the new data
   */
  public void setData(final Object data) {
    this.data = data;
  }

  /**
   * Gets the source port.
   *
   * @return the source port
   */
  public String getSourcePort() {
    return this.sourcePort;
  }

  /**
   * Sets the source port.
   *
   * @param sourcePort
   *          the new source port
   */
  public void setSourcePort(final String sourcePort) {
    this.sourcePort = sourcePort;
  }

  /**
   * Gets the target port.
   *
   * @return the target port
   */
  public String getTargetPort() {
    return this.targetPort;
  }

  /**
   * Sets the target port.
   *
   * @param targetPort
   *          the new target port
   */
  public void setTargetPort(final String targetPort) {
    this.targetPort = targetPort;
  }

}
