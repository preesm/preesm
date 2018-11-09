/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2013)
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
package org.ietr.dftools.algorithm.model.parameters;

import org.ietr.dftools.algorithm.model.CloneableProperty;

/**
 * Class representing parameters that can be used to configure a graph ...
 *
 * @author jpiat
 *
 */
public class Parameter implements CloneableProperty<Parameter> {

  /** The name. */
  private String name;

  /** The value. */
  private Long value = null;

  /**
   * Builds a parameter with the given name.
   *
   * @param name
   *          The name of the parameter
   */
  public Parameter(final String name) {
    this.name = name;
  }

  /**
   * Gives this parameter name.
   *
   * @return The name of this parameter
   */
  public String getName() {
    return this.name;
  }

  /**
   * Set this parameter name.
   *
   * @param name
   *          The name to set for this parameter
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Gets the value.
   *
   * @return The value of the parameter
   * @throws NoIntegerValueException
   *           the no integer value exception
   */
  public Long getValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value
   *          The value of this parameter
   */
  public void setValue(final Long value) {
    this.value = value;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#clone()
   */
  @Override
  public Parameter copy() {
    final Parameter par = new Parameter(this.name);
    par.setValue(this.value);
    return par;
  }

}
