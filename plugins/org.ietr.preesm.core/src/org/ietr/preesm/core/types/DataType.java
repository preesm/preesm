/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2010 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2010 - 2012)
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
package org.ietr.preesm.core.types;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representing a data type in code generation (exple: char, int...).
 *
 * @author mpelcat
 */
public class DataType {

  /** The type name. */
  private final String typeName;

  /** Size in base units (usually bytes). */
  private long size;

  /** The Constant defaultDataTypeSize. */
  public static final long defaultDataTypeSize = 1;

  /** The Constant nameToSize. */
  public static final Map<String, Long> nameToSize = new LinkedHashMap<>();

  /**
   * Instantiates a new data type.
   *
   * @param typeName
   *          the type name
   */
  public DataType(final String typeName) {
    super();
    this.typeName = typeName;
    if (DataType.nameToSize.get(typeName) == null) {
      this.size = DataType.defaultDataTypeSize;
    } else {
      this.size = DataType.nameToSize.get(typeName);
    }
  }

  /**
   * Instantiates a new data type.
   *
   * @param type
   *          the type
   */
  public DataType(final DataType type) {
    super();
    this.typeName = type.getTypeName();
    this.size = type.getSize();
  }

  /**
   * Instantiates a new data type.
   *
   * @param typeName
   *          the type name
   * @param size
   *          the size
   */
  public DataType(final String typeName, final long size) {
    super();
    this.typeName = typeName;
    this.size = size;
    DataType.nameToSize.put(typeName, size);
  }

  /**
   * Gets the type name.
   *
   * @return the type name
   */
  public String getTypeName() {
    return this.typeName;
  }

  /**
   * Gets the size.
   *
   * @return the size
   */
  public long getSize() {
    return this.size;
  }

  /**
   * Sets the size.
   *
   * @param size
   *          the new size
   */
  public void setSize(final long size) {
    this.size = size;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.typeName;
  }
}
