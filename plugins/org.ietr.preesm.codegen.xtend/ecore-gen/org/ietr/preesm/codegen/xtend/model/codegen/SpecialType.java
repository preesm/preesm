/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.xtend.model.codegen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration ' <em><b>Special Type</b></em>', and utility methods for working with them. <!--
 * end-user-doc -->
 * 
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSpecialType()
 * @model
 * @generated
 */
public enum SpecialType implements Enumerator {
  /**
   * The '<em><b>FORK</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #FORK_VALUE
   * @generated
   * @ordered
   */
  FORK(0, "FORK", "FORK"),

  /**
   * The '<em><b>JOIN</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #JOIN_VALUE
   * @generated
   * @ordered
   */
  JOIN(1, "JOIN", "JOIN"),

  /**
   * The '<em><b>BROADCAST</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #BROADCAST_VALUE
   * @generated
   * @ordered
   */
  BROADCAST(2, "BROADCAST", "BROADCAST"),

  /**
   * The '<em><b>ROUND BUFFER</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #ROUND_BUFFER_VALUE
   * @generated
   * @ordered
   */
  ROUND_BUFFER(3, "ROUND_BUFFER", "ROUND_BUFFER");

  /**
   * The '<em><b>FORK</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>FORK</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #FORK
   * @model
   * @generated
   * @ordered
   */
  public static final int FORK_VALUE = 0;

  /**
   * The '<em><b>JOIN</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>JOIN</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #JOIN
   * @model
   * @generated
   * @ordered
   */
  public static final int JOIN_VALUE = 1;

  /**
   * The '<em><b>BROADCAST</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>BROADCAST</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #BROADCAST
   * @model
   * @generated
   * @ordered
   */
  public static final int BROADCAST_VALUE = 2;

  /**
   * The '<em><b>ROUND BUFFER</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>ROUND BUFFER</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #ROUND_BUFFER
   * @model
   * @generated
   * @ordered
   */
  public static final int ROUND_BUFFER_VALUE = 3;

  /**
   * An array of all the '<em><b>Special Type</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static final SpecialType[] VALUES_ARRAY = new SpecialType[] { FORK, JOIN, BROADCAST, ROUND_BUFFER, };

  /**
   * A public read-only list of all the '<em><b>Special Type</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final List<SpecialType> VALUES = Collections.unmodifiableList(Arrays.asList(SpecialType.VALUES_ARRAY));

  /**
   * Returns the '<em><b>Special Type</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static SpecialType get(final String literal) {
    for (final SpecialType result : SpecialType.VALUES_ARRAY) {
      if (result.toString().equals(literal)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Special Type</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static SpecialType getByName(final String name) {
    for (final SpecialType result : SpecialType.VALUES_ARRAY) {
      if (result.getName().equals(name)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Special Type</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static SpecialType get(final int value) {
    switch (value) {
      case FORK_VALUE:
        return FORK;
      case JOIN_VALUE:
        return JOIN;
      case BROADCAST_VALUE:
        return BROADCAST;
      case ROUND_BUFFER_VALUE:
        return ROUND_BUFFER;
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
  private SpecialType(final int value, final String name, final String literal) {
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

} // SpecialType
