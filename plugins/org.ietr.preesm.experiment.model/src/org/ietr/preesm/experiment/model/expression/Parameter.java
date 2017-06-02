/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.experiment.model.expression;

import org.nfunk.jep.Variable;

// TODO: Auto-generated Javadoc
/**
 * The Class Parameter.
 */
public class Parameter extends Variable implements Cloneable {

  /** The sdx index. */
  public int sdxIndex;

  /**
   * Instantiates a new parameter.
   *
   * @param name
   *          the name
   */
  public Parameter(final String name) {
    super(name);
    // TODO Auto-generated constructor stub
  }

  /**
   * Instantiates a new parameter.
   *
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public Parameter(final String name, final Object value) {
    super(name, value);
    // TODO Auto-generated constructor stub
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#clone()
   */
  @Override
  public Parameter clone() {
    final Parameter newParam = new Parameter(this.name);
    newParam.setValue(getValue());
    newParam.setSdxIndex(getSdxIndex());
    return newParam;
  }

  /**
   * Gets the sdx index.
   *
   * @return the sdx index
   */
  public int getSdxIndex() {
    return this.sdxIndex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.nfunk.jep.Variable#getValue()
   */
  @Override
  public Object getValue() {
    return super.getValue();
  }

  /**
   * Sets the sdx index.
   *
   * @param index
   *          the new sdx index
   */
  public void setSdxIndex(final int index) {
    this.sdxIndex = index;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.nfunk.jep.Variable#setValue(java.lang.Object)
   */
  @Override
  public boolean setValue(final Object value) {
    return super.setValue(value);
  }

}
