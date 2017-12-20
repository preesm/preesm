/**
 */
package org.ietr.preesm.experiment.model.pimm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Interface Kind</b></em>', and utility methods for working with them. <!--
 * end-user-doc -->
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getInterfaceKind()
 * @model
 * @generated
 */
public enum InterfaceKind implements Enumerator {
  /**
   * The '<em><b>DATA INPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #DATA_INPUT_VALUE
   * @generated
   * @ordered
   */
  DATA_INPUT(0, "DATA_INPUT", "src"),

  /**
   * The '<em><b>DATA OUTPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #DATA_OUTPUT_VALUE
   * @generated
   * @ordered
   */
  DATA_OUTPUT(1, "DATA_OUTPUT", "snk"),

  /**
   * The '<em><b>CFG OUTPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #CFG_OUTPUT_VALUE
   * @generated
   * @ordered
   */
  CFG_OUTPUT(2, "CFG_OUTPUT", "cfg_out_iface"),

  /**
   * The '<em><b>CFG INPUT</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #CFG_INPUT_VALUE
   * @generated
   * @ordered
   */
  CFG_INPUT(3, "CFG_INPUT", "cfg_in_iface");

  /**
   * The '<em><b>DATA INPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>DATA INPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #DATA_INPUT
   * @model literal="src"
   * @generated
   * @ordered
   */
  public static final int DATA_INPUT_VALUE = 0;

  /**
   * The '<em><b>DATA OUTPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>DATA OUTPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #DATA_OUTPUT
   * @model literal="snk"
   * @generated
   * @ordered
   */
  public static final int DATA_OUTPUT_VALUE = 1;

  /**
   * The '<em><b>CFG OUTPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CFG OUTPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #CFG_OUTPUT
   * @model literal="cfg_out_iface"
   * @generated
   * @ordered
   */
  public static final int CFG_OUTPUT_VALUE = 2;

  /**
   * The '<em><b>CFG INPUT</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>CFG INPUT</b></em>' literal object isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @see #CFG_INPUT
   * @model literal="cfg_in_iface"
   * @generated
   * @ordered
   */
  public static final int CFG_INPUT_VALUE = 3;

  /**
   * An array of all the '<em><b>Interface Kind</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static final InterfaceKind[] VALUES_ARRAY = new InterfaceKind[] { DATA_INPUT, DATA_OUTPUT, CFG_OUTPUT, CFG_INPUT, };

  /**
   * A public read-only list of all the '<em><b>Interface Kind</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static final List<InterfaceKind> VALUES = Collections.unmodifiableList(Arrays.asList(InterfaceKind.VALUES_ARRAY));

  /**
   * Returns the '<em><b>Interface Kind</b></em>' literal with the specified literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static InterfaceKind get(final String literal) {
    for (final InterfaceKind result : InterfaceKind.VALUES_ARRAY) {
      if (result.toString().equals(literal)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Interface Kind</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static InterfaceKind getByName(final String name) {
    for (final InterfaceKind result : InterfaceKind.VALUES_ARRAY) {
      if (result.getName().equals(name)) {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Interface Kind</b></em>' literal with the specified integer value. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static InterfaceKind get(final int value) {
    switch (value) {
      case DATA_INPUT_VALUE:
        return DATA_INPUT;
      case DATA_OUTPUT_VALUE:
        return DATA_OUTPUT;
      case CFG_OUTPUT_VALUE:
        return CFG_OUTPUT;
      case CFG_INPUT_VALUE:
        return CFG_INPUT;
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
  private InterfaceKind(final int value, final String name, final String literal) {
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

} // InterfaceKind
