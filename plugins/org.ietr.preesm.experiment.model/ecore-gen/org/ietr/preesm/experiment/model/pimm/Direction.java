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
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Direction</b></em>', and utility methods for working with them. <!--
 * end-user-doc -->
 * 
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDirection()
 * @model
 * @generated
 */
public enum Direction implements Enumerator {
  /**
   * The '<em><b>IN</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #IN_VALUE
   * @generated
   * @ordered
   */
  IN(0, "IN", "IN"),

  /**
   * The '<em><b>OUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #OUT_VALUE
   * @generated
   * @ordered
   */
  OUT(1, "OUT", "OUT");

  /**
   * The '<em><b>IN</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>IN</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #IN
   * @model
   * @generated
   * @ordered
   */
  public static final int IN_VALUE = 0;

  /**
   * The '<em><b>OUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>OUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #OUT
   * @model
   * @generated
   * @ordered
   */
  public static final int OUT_VALUE = 1;

  /**
   * An array of all the '<em><b>Direction</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private static final Direction[] VALUES_ARRAY = new Direction[] { IN, OUT, };

  /**
   * A public read-only list of all the '<em><b>Direction</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final List<Direction> VALUES = Collections.unmodifiableList(Arrays.asList(Direction.VALUES_ARRAY));

  /**
   * Returns the '<em><b>Direction</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static Direction get(final String literal) {
    for (final Direction result : Direction.VALUES_ARRAY) {
      if (result.toString().equals(literal)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Direction</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static Direction getByName(final String name) {
    for (final Direction result : Direction.VALUES_ARRAY) {
      if (result.getName().equals(name)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Direction</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static Direction get(final int value) {
    switch (value) {
      case IN_VALUE:
        return IN;
      case OUT_VALUE:
        return OUT;
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
  private Direction(final int value, final String name, final String literal) {
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

} // Direction
