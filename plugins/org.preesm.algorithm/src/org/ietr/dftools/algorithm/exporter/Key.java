/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.ietr.dftools.algorithm.exporter;

import java.util.Objects;

/**
 * Class describing a GML key.
 *
 * @author jpiat
 */
public class Key {

  /** The apply to. */
  String applyTo;

  /** The desc. */
  Class<?> desc;

  /** The id. */
  String id;

  /** The name. */
  String name;

  /** The type. */
  String type;

  /**
   * Construct a new key with the given name, applyint to instanceof the class "applyTo" and with a value of the type
   * "type". The last parameter is a description of the key meaning
   *
   * @param name
   *          The key name
   * @param applyTo
   *          The type of element this key apply to (node, edge, port ...)
   * @param type
   *          The type of the value of this key (string, int, double)
   * @param desc
   *          The class name of the properties value (SDFEdgePorperty.class) ...
   */
  public Key(final String name, final String applyTo, final String type, final Class<?> desc) {
    this.name = name;
    this.type = type;
    this.applyTo = applyTo;
    this.desc = desc;
  }

  /**
   * Gives a string representation of the class this key applies to.
   *
   * @return Gives the Class name it applies to
   */
  public String getApplyTo() {
    return this.applyTo;
  }

  /**
   * Gives this key id.
   *
   * @return The id of the key
   */
  public String getId() {
    return this.id;
  }

  /**
   * Gives this key's name.
   *
   * @return The name of this key
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gives a String representation of the type of this key.
   *
   * @return The STring representation of this key type
   */
  public String getType() {
    return this.type;
  }

  /**
   * Gives the description of this key.
   *
   * @return The description of this key
   */
  public Class<?> getTypeClass() {
    return this.desc;
  }

  /**
   * Sets this key d.
   *
   * @param id
   *          the new id
   */
  public void setId(final String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object key) {
    if (key instanceof Key) {
      final Key kKey = (Key) key;
      // Since when it exists, the id is always equal to the name, we
      // remove
      // the equality test for this.id
      // @author Karol Desnos
      // @date 2012.10.17
      return kKey.applyTo.equals(this.applyTo) && kKey.name.equals(this.name) && kKey.type.equals(this.type);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.applyTo, this.desc, this.id, this.name, this.type);
  }
}
