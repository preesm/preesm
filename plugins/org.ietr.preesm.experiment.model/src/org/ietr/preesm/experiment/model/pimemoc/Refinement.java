/**
 */
package org.ietr.preesm.experiment.model.pimemoc;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Refinement</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimemoc.Refinement#getFileName
 * <em>File Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getRefinement()
 * @model
 * @generated
 */
public interface Refinement extends EObject {
	/**
	 * Returns the value of the '<em><b>File Name</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File Name</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>File Name</em>' attribute.
	 * @see #setFileName(String)
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getRefinement_FileName()
	 * @model
	 * @generated
	 */
	String getFileName();

	/**
	 * Return the URI of the file associated to the {@link Refinement}.
	 * 
	 * @return the URI of the file associated to the Refinement or
	 *         <code>null</code> if the file does not exists.
	 */
	URI getFileURI();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.experiment.model.pimemoc.Refinement#getFileName
	 * <em>File Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>File Name</em>' attribute.
	 * @see #getFileName()
	 * @generated
	 */
	void setFileName(String value);

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @model kind="operation"
	 * @generated
	 */
	AbstractVertex getAbstractVertex();

} // Refinement
