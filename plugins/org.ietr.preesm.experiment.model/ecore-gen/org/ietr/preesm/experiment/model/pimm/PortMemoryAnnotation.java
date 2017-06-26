/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.experiment.model.pimm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Port Memory Annotation</b></em>', and utility methods for working with
 * them. <!-- end-user-doc -->
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPortMemoryAnnotation()
 * @model
 * @generated
 */
public enum PortMemoryAnnotation implements Enumerator {
  /**
   * The '<em><b>NONE</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #NONE_VALUE
   * @generated
   * @ordered
   */
  NONE(0, "NONE", "NONE"),
  /**
   * The '<em><b>READ ONLY</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #READ_ONLY_VALUE
   * @generated
   * @ordered
   */
  READ_ONLY(1, "READ_ONLY", "READ_ONLY"),

  /**
   * The '<em><b>WRITE ONLY</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #WRITE_ONLY_VALUE
   * @generated
   * @ordered
   */
  WRITE_ONLY(2, "WRITE_ONLY", "WRITE_ONLY"),

  /**
   * The '<em><b>UNUSED</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #UNUSED_VALUE
   * @generated
   * @ordered
   */
  UNUSED(3, "UNUSED", "UNUSED");

  /**
   * The '<em><b>NONE</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>NONE</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #NONE
   * @model
   * @generated
   * @ordered
   */
  public static final int NONE_VALUE = 0;

  /**
   * The '<em><b>READ ONLY</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>READ ONLY</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #READ_ONLY
   * @model
   * @generated
   * @ordered
   */
  public static final int READ_ONLY_VALUE = 1;

  /**
   * The '<em><b>WRITE ONLY</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>WRITE ONLY</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #WRITE_ONLY
   * @model
   * @generated
   * @ordered
   */
  public static final int WRITE_ONLY_VALUE = 2;

  /**
   * The '<em><b>UNUSED</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>UNUSED</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #UNUSED
   * @model
   * @generated
   * @ordered
   */
  public static final int UNUSED_VALUE = 3;

  /**
   * An array of all the '<em><b>Port Memory Annotation</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static final PortMemoryAnnotation[] VALUES_ARRAY = new PortMemoryAnnotation[] { NONE, READ_ONLY, WRITE_ONLY, UNUSED, };

  /**
   * A public read-only list of all the '<em><b>Port Memory Annotation</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static final List<PortMemoryAnnotation> VALUES = Collections.unmodifiableList(Arrays.asList(PortMemoryAnnotation.VALUES_ARRAY));

  /**
   * Returns the '<em><b>Port Memory Annotation</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PortMemoryAnnotation get(final String literal) {
    for (final PortMemoryAnnotation result : PortMemoryAnnotation.VALUES_ARRAY) {
      if (result.toString().equals(literal)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Port Memory Annotation</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PortMemoryAnnotation getByName(final String name) {
    for (final PortMemoryAnnotation result : PortMemoryAnnotation.VALUES_ARRAY) {
      if (result.getName().equals(name)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Port Memory Annotation</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PortMemoryAnnotation get(final int value) {
    switch (value) {
      case NONE_VALUE:
        return NONE;
      case READ_ONLY_VALUE:
        return READ_ONLY;
      case WRITE_ONLY_VALUE:
        return WRITE_ONLY;
      case UNUSED_VALUE:
        return UNUSED;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private PortMemoryAnnotation(final int value, final String name, final String literal) {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the value
   * @generated
   */
  @Override
  public int getValue() {
    return this.value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the name
   * @generated
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the literal
   * @generated
   */
  @Override
  public String getLiteral() {
    return this.literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String toString() {
    return this.literal;
  }

} // PortMemoryAnnotation
