/**
 */
package org.ietr.preesm.experiment.model.pimm;

import java.util.Set;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Graph</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Graph#getVertices <em>
 * Vertices</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Graph#getFifos <em>Fifos
 * </em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Graph#getParameters <em>
 * Parameters</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Graph#getDependencies <em>
 * Dependencies</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph()
 * @model
 * @generated
 */
public interface Graph extends AbstractActor {
	/**
	 * Returns the value of the '<em><b>Vertices</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimm.AbstractActor}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vertices</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Vertices</em>' containment reference list.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph_Vertices()
	 * @model containment="true"
	 * @generated
	 */
	EList<AbstractActor> getVertices();

	/**
	 * Returns the value of the '<em><b>Fifos</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimm.Fifo}. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Fifos</em>' containment reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Fifos</em>' containment reference list.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph_Fifos()
	 * @model containment="true"
	 * @generated
	 */
	EList<Fifo> getFifos();

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimm.Parameter}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameters</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parameters</em>' containment reference
	 *         list.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph_Parameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getParameters();

	/**
	 * Returns the value of the '<em><b>Dependencies</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimm.Dependency}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dependencies</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Dependencies</em>' containment reference
	 *         list.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph_Dependencies()
	 * @model containment="true"
	 * @generated
	 */
	EList<Dependency> getDependencies();

	/**
	 * <!-- begin-user-doc --> This method will add the {@link InterfaceActor}
	 * and create the corresponding {@link Port}. <!-- end-user-doc -->
	 * 
	 * @model
	 * @generated
	 */
	boolean addInterfaceActor(InterfaceActor interfaceVertex);

	/**
	 * <!-- begin-user-doc --> This method will remove the
	 * {@link InterfaceActor} and the corresponding {@link Port} from the
	 * {@link Graph}. <!-- end-user-doc -->
	 * 
	 * @model
	 * @generated
	 */
	boolean removeInterfaceActor(InterfaceActor interfaceVertex);

	/**
	 * Return the list of the names of all {@link Graph#getVertices()} of the Graph.
	 * 
	 * @return the list of names
	 */
	public Set<String> getVerticesNames();

	/**
	 * Return the list of the names of all {@link Graph#getParameters()} of the Graph.
	 * 
	 * @return the list of names
	 */
	public Set<String> getParametersNames();

	/**
	 * Return the {@link AbstractVertex} ( {@link AbstractActor} or
	 * {@link Parameter}) whose name is given as a parameter.
	 * 
	 * @param name
	 *            the desired vertex, or <code>null</code> if no vertex has the
	 *            requested name.
	 * @return
	 */
	public AbstractVertex getVertexNamed(String name);
} // Graph
