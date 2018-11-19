/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.algorithm.mapper.model.property;

import org.preesm.algorithm.model.CloneableProperty;

/**
 * Properties of an edge set when converting dag to mapper dag.
 *
 * @author mpelcat
 */
public class EdgeInit implements CloneableProperty<EdgeInit> {

  /** The data size. */
  private long dataSize = 0;

  /**
   * Instantiates a new edge init.
   */
  public EdgeInit() {
    super();
  }

  /**
   * Instantiates a new edge init.
   *
   * @param dataSize
   *          the data size
   */
  public EdgeInit(final int dataSize) {
    super();
    this.dataSize = dataSize;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#clone()
   */
  @Override
  public EdgeInit copy() {
    final EdgeInit property = new EdgeInit();
    property.setDataSize(getDataSize());
    return property;
  }

  /**
   * Gets the data size.
   *
   * @return the data size
   */
  public long getDataSize() {
    return this.dataSize;
  }

  /**
   * Sets the data size.
   *
   * @param dataSize
   *          the new data size
   */
  public void setDataSize(final long dataSize) {
    this.dataSize = dataSize;
  }
}
