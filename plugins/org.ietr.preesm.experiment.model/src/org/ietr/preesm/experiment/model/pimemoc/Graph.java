/**
 */
package org.ietr.preesm.experiment.model.pimemoc;

import java.util.Set;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Graph</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimemoc.Graph#getVertices <em>
 * Vertices</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimemoc.Graph#getFifos <em>Fifos
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getGraph()
 * @model
 * @generated
 */
public interface Graph extends AbstractVertex {
	/**
	 * Returns the value of the '<em><b>Vertices</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vertices</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Vertices</em>' containment reference list.
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getGraph_Vertices()
	 * @model containment="true"
	 * @generated
	 */
	EList<AbstractVertex> getVertices();

	/**
	 * Returns the value of the '<em><b>Fifos</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimemoc.Fifo}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fifos</em>' containment reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Fifos</em>' containment reference list.
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getGraph_Fifos()
	 * @model containment="true"
	 * @generated
	 */
	EList<Fifo> getFifos();

	/**
	 * Return the list of the names of all vertices of the Graph.
	 * 
	 * @return the list of names
	 */
	public Set<String> getVerticesNames();

	/**
	 * Return the vertex whose name is given as a parameter.
	 * 
	 * @param name
	 *            the desired vertex, or <code>null</code> if no vertex has the
	 *            requested name.
	 * @return
	 */
	public AbstractVertex getVertexNamed(String name);
} // Graph
