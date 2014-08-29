/**
 */
package org.ietr.preesm.experiment.model.pimm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Port Memory Annotation</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPortMemoryAnnotation()
 * @model
 * @generated
 */
public enum PortMemoryAnnotation implements Enumerator {
	/**
	 * The '<em><b>READ ONLY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #READ_ONLY_VALUE
	 * @generated
	 * @ordered
	 */
	READ_ONLY(0, "READ_ONLY", "READ_ONLY"),

	/**
	 * The '<em><b>WRITE ONLY</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #WRITE_ONLY_VALUE
	 * @generated
	 * @ordered
	 */
	WRITE_ONLY(1, "WRITE_ONLY", "WRITE_ONLY"),

	/**
	 * The '<em><b>UNUSED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #UNUSED_VALUE
	 * @generated
	 * @ordered
	 */
	UNUSED(3, "UNUSED", "UNUSED");

	/**
	 * The '<em><b>READ ONLY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>READ ONLY</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #READ_ONLY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int READ_ONLY_VALUE = 0;

	/**
	 * The '<em><b>WRITE ONLY</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>WRITE ONLY</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #WRITE_ONLY
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int WRITE_ONLY_VALUE = 1;

	/**
	 * The '<em><b>UNUSED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>UNUSED</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #UNUSED
	 * @model
	 * @generated
	 * @ordered
	 */
	public static final int UNUSED_VALUE = 3;

	/**
	 * An array of all the '<em><b>Port Memory Annotation</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final PortMemoryAnnotation[] VALUES_ARRAY =
		new PortMemoryAnnotation[] {
			READ_ONLY,
			WRITE_ONLY,
			UNUSED,
		};

	/**
	 * A public read-only list of all the '<em><b>Port Memory Annotation</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<PortMemoryAnnotation> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Port Memory Annotation</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static PortMemoryAnnotation get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			PortMemoryAnnotation result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Port Memory Annotation</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static PortMemoryAnnotation getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			PortMemoryAnnotation result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Port Memory Annotation</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static PortMemoryAnnotation get(int value) {
		switch (value) {
			case READ_ONLY_VALUE: return READ_ONLY;
			case WRITE_ONLY_VALUE: return WRITE_ONLY;
			case UNUSED_VALUE: return UNUSED;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private PortMemoryAnnotation(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //PortMemoryAnnotation
