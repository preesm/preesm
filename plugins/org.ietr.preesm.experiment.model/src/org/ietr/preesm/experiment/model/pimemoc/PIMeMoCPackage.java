/**
 */
package org.ietr.preesm.experiment.model.pimemoc;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory
 * @model kind="package"
 * @generated
 */
public interface PIMeMoCPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "pimemoc";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.ietr.preesm/experiment/model/pimemoc";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.ietr.preesm.experiment.pimemoc";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PIMeMoCPackage eINSTANCE = org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl <em>Abstract Vertex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getAbstractVertex()
	 * @generated
	 */
	int ABSTRACT_VERTEX = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX__NAME = 0;

	/**
	 * The number of structural features of the '<em>Abstract Vertex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl <em>Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getGraph()
	 * @generated
	 */
	int GRAPH = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__NAME = ABSTRACT_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__VERTICES = ABSTRACT_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl <em>Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getActor()
	 * @generated
	 */
	int ACTOR = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__NAME = ABSTRACT_VERTEX__NAME;

	/**
	 * The number of structural features of the '<em>Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex <em>Abstract Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Vertex</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.AbstractVertex
	 * @generated
	 */
	EClass getAbstractVertex();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getName()
	 * @see #getAbstractVertex()
	 * @generated
	 */
	EAttribute getAbstractVertex_Name();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.Graph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Graph</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Graph
	 * @generated
	 */
	EClass getGraph();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimemoc.Graph#getVertices <em>Vertices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vertices</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Graph#getVertices()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Vertices();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.Actor <em>Actor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Actor</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Actor
	 * @generated
	 */
	EClass getActor();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PIMeMoCFactory getPIMeMoCFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl <em>Abstract Vertex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getAbstractVertex()
		 * @generated
		 */
		EClass ABSTRACT_VERTEX = eINSTANCE.getAbstractVertex();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_VERTEX__NAME = eINSTANCE.getAbstractVertex_Name();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl <em>Graph</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getGraph()
		 * @generated
		 */
		EClass GRAPH = eINSTANCE.getGraph();

		/**
		 * The meta object literal for the '<em><b>Vertices</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GRAPH__VERTICES = eINSTANCE.getGraph_Vertices();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl <em>Actor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getActor()
		 * @generated
		 */
		EClass ACTOR = eINSTANCE.getActor();

	}

} //PIMeMoCPackage
