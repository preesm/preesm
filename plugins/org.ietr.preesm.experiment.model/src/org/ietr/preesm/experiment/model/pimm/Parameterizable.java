/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameterizable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Parameterizable#getConfigInputPorts <em>Config Input Ports</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameterizable()
 * @model abstract="true"
 * @generated
 */
public interface Parameterizable extends EObject {
	/**
	 * Returns the value of the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Config Input Ports</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Config Input Ports</em>' containment reference list.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameterizable_ConfigInputPorts()
	 * @model containment="true"
	 * @generated
	 */
	EList<ConfigInputPort> getConfigInputPorts();

} // Parameterizable
