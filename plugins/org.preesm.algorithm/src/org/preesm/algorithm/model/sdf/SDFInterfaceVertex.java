/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.model.sdf;

import java.util.Objects;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.IInterface;
import org.preesm.algorithm.model.InterfaceDirection;

/**
 * Class used to represent the interfaces of a Hierarchical vertex.
 *
 * @author jpiat
 */
public abstract class SDFInterfaceVertex extends SDFAbstractVertex implements IInterface {

  /** Name of the property containing the direction. */
  public static final String PORT_DIRECTION = "port_direction";

  /** String representation of the type of data carried by this port. */
  private static final String DATA_TYPE = "data_type";

  /** Kind of node. */
  public static final String PORT = "port";

  static {
    AbstractVertex.public_properties.add(SDFInterfaceVertex.PORT_DIRECTION);
    AbstractVertex.public_properties.add(SDFInterfaceVertex.DATA_TYPE);
  }

  /**
   * Creates a new SDFInterfaceVertex with the default direction (SINK).
   */
  protected SDFInterfaceVertex(org.preesm.model.pisdf.AbstractVertex origVertex) {
    super(origVertex);
    setKind(SDFInterfaceVertex.PORT);
    setDirection(InterfaceDirection.OUTPUT);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex#clone()
   */
  @Override
  public abstract SDFInterfaceVertex copy();

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object e) {
    if (e instanceof final SDFInterfaceVertex sdfIfaceVertex) {
      return (sdfIfaceVertex.getName().equals(getName()) && sdfIfaceVertex.getDirection().equals(getDirection()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getDirection());
  }

  /**
   * Gives this interface direction.
   *
   * @return The direction of this interface
   */
  @Override
  public InterfaceDirection getDirection() {
    return getPropertyBean().getValue(SDFInterfaceVertex.PORT_DIRECTION);
  }

  /**
   * Set this interface direction.
   *
   * @param direction
   *          the new direction
   */
  @Override
  public void setDirection(final String direction) {
    getPropertyBean().setValue(SDFInterfaceVertex.PORT_DIRECTION, InterfaceDirection.fromString(direction));
  }

  /**
   * Set this interface direction.
   *
   * @param direction
   *          the new direction
   */
  @Override
  public void setDirection(final InterfaceDirection direction) {
    getPropertyBean().setValue(SDFInterfaceVertex.PORT_DIRECTION, direction);
  }

  /**
   * Sets the type of data on this interface.
   *
   * @param type
   *          the new data type
   */
  public void setDataType(final String type) {
    getPropertyBean().setValue(SDFInterfaceVertex.DATA_TYPE, type);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex#getNbRepeat()
   */
  @Override
  public long getNbRepeat() {
    return 1L;
  }

  /**
   * Gives the type of data on this interface.
   *
   * @return The string representation of the type of data on this interface
   */
  public String getDataType() {
    return getPropertyBean().getValue(SDFInterfaceVertex.DATA_TYPE);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractVertex#setPropertyValue(java.lang.String, java.lang.Object)
   */
  @Override
  public void setPropertyValue(final String propertyName, final Object value) {
    if (propertyName.equals(SDFInterfaceVertex.PORT_DIRECTION) && (value instanceof final String str)) {
      super.setPropertyValue(propertyName, InterfaceDirection.fromString(str));
    } else {
      super.setPropertyValue(propertyName, value);
    }

  }
}
