/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
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

package org.ietr.preesm.codegen.model;

// TODO: Auto-generated Javadoc
/**
 * Representation of a buffer in a prototype.
 *
 * @author jpiat
 * @author mpelcat
 */
public class CodeGenArgument extends CodeGenCallElement {

  /** The type. */
  private String type;

  /** The Constant INPUT. */
  public static final String INPUT = "INPUT";

  /** The Constant OUTPUT. */
  public static final String OUTPUT = "OUTPUT";

  /** The Constant INOUT. */
  public static final String INOUT = "INOUT";

  /** The direction. */
  private final String direction;

  /**
   * Instantiates a new code gen argument.
   *
   * @param name
   *          the name
   * @param direction
   *          the direction
   */
  public CodeGenArgument(final String name, final String direction) {
    super(name);
    this.direction = direction;
  }

  /**
   * Sets the type.
   *
   * @param type
   *          the new type
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return this.type;
  }

  /**
   * Gets the direction.
   *
   * @return the direction
   */
  public String getDirection() {
    return this.direction;
  }
}
