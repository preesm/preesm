/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
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
package org.ietr.dftools.algorithm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for object using a property bean to store properties.
 *
 * @author jpiat
 */
public interface PropertySource {

  /**
   * Gives the object's property bean.
   *
   * @return The objects property bean
   */
  public PropertyBean getPropertyBean();

  /**
   * Copy the properties of the given PropertySource to this PropertySource.
   *
   * @param props
   *          The properties to be copied
   */
  public default void copyProperties(PropertySource props) {
    final List<String> keys = new ArrayList<>(props.getPropertyBean().keys());
    for (final String key : keys) {
      if (!key.equals(AbstractVertex.BASE_LITERAL)) {
        if (props.getPropertyBean().getValue(key) instanceof CloneableProperty) {
          this.getPropertyBean().setValue(key, props.getPropertyBean().<CloneableProperty<?>>getValue(key).copy());
        } else {
          this.getPropertyBean().setValue(key, props.getPropertyBean().getValue(key));
        }
      }
    }
  }

  /**
   * Gets the public properties.
   *
   * @return the public properties
   */
  public List<String> getPublicProperties();

  /**
   * Gets the factory for property.
   *
   * @param propertyName
   *          the property name
   * @return the factory for property
   */
  public PropertyFactory getFactoryForProperty(String propertyName);

  /**
   * Gets the property string value.
   *
   * @param propertyName
   *          the property name
   * @return the property string value
   */
  public default String getPropertyStringValue(String propertyName) {
    if (this.getPropertyBean().getValue(propertyName) != null) {
      return this.getPropertyBean().getValue(propertyName).toString();
    }
    return null;
  }

  /**
   * Sets the property value.
   *
   * @param propertyName
   *          the property name
   * @param value
   *          the value
   */
  public default void setPropertyValue(String propertyName, Object value) {
    this.getPropertyBean().setValue(propertyName, value);
  }
}
