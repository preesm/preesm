/**
 */
package org.ietr.preesm.experiment.model.pimemoc;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Graph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimemoc.Graph#getVertices <em>Vertices</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getGraph()
 * @model
 * @generated
 */
public interface Graph extends AbstractVertex {
	/**
	 * Returns the value of the '<em><b>Vertices</b></em>' containment reference list.
	 * The list contents are of type {@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vertices</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vertices</em>' containment reference list.
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getGraph_Vertices()
	 * @model containment="true"
	 * @generated
	 */
	EList<AbstractVertex> getVertices();

} // Graph
