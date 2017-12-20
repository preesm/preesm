/**
 */
package org.ietr.preesm.experiment.model.pimm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Port Kind</b></em>', and utility methods for working with them. <!--
 * end-user-doc -->
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPortKind()
 * @model
 * @generated
 */
public enum PortKind implements Enumerator {
  /**
   * The '<em><b>CFG INPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #CFG_INPUT_VALUE
   * @generated
   * @ordered
   */
  CFG_INPUT(0, "CFG_INPUT", "cfg_input"),

  /**
   * The '<em><b>DATA INPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #DATA_INPUT_VALUE
   * @generated
   * @ordered
   */
  DATA_INPUT(1, "DATA_INPUT", "input"),

  /**
   * The '<em><b>DATA OUTPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #DATA_OUTPUT_VALUE
   * @generated
   * @ordered
   */
  DATA_OUTPUT(2, "DATA_OUTPUT", "output"),

  /**
   * The '<em><b>CFG OUTPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #CFG_OUTPUT_VALUE
   * @generated
   * @ordered
   */
  CFG_OUTPUT(3, "CFG_OUTPUT", "cfg_output");

  /**
   * The '<em><b>CFG INPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CFG INPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #CFG_INPUT
   * @model literal="cfg_input"
   * @generated
   * @ordered
   */
  public static final int CFG_INPUT_VALUE = 0;

  /**
   * The '<em><b>DATA INPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>DATA INPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #DATA_INPUT
   * @model literal="input"
   * @generated
   * @ordered
   */
  public static final int DATA_INPUT_VALUE = 1;

  /**
   * The '<em><b>DATA OUTPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>DATA OUTPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #DATA_OUTPUT
   * @model literal="output"
   * @generated
   * @ordered
   */
  public static final int DATA_OUTPUT_VALUE = 2;

  /**
   * The '<em><b>CFG OUTPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CFG OUTPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #CFG_OUTPUT
   * @model literal="cfg_output"
   * @generated
   * @ordered
   */
  public static final int CFG_OUTPUT_VALUE = 3;

  /**
   * An array of all the '<em><b>Port Kind</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static final PortKind[] VALUES_ARRAY = new PortKind[] { CFG_INPUT, DATA_INPUT, DATA_OUTPUT, CFG_OUTPUT, };

  /**
   * A public read-only list of all the '<em><b>Port Kind</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static final List<PortKind> VALUES = Collections.unmodifiableList(Arrays.asList(PortKind.VALUES_ARRAY));

  /**
   * Returns the '<em><b>Port Kind</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PortKind get(final String literal) {
    for (final PortKind result : PortKind.VALUES_ARRAY) {
      if (result.toString().equals(literal)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Port Kind</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PortKind getByName(final String name) {
    for (final PortKind result : PortKind.VALUES_ARRAY) {
      if (result.getName().equals(name)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Port Kind</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static PortKind get(final int value) {
    switch (value) {
      case CFG_INPUT_VALUE:
        return CFG_INPUT;
      case DATA_INPUT_VALUE:
        return DATA_INPUT;
      case DATA_OUTPUT_VALUE:
        return DATA_OUTPUT;
      case CFG_OUTPUT_VALUE:
        return CFG_OUTPUT;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private PortKind(final int value, final String name, final String literal) {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int getValue() {
    return this.value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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

} // PortKind
