/**
 */
package org.ietr.dftools.architecture.slam.component.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.ietr.dftools.architecture.slam.ParameterizedElement;
import org.ietr.dftools.architecture.slam.VLNVedElement;

import org.ietr.dftools.architecture.slam.component.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage
 * @generated
 */
public class ComponentAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ComponentPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ComponentPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentSwitch<Adapter> modelSwitch =
		new ComponentSwitch<Adapter>() {
			@Override
			public Adapter caseComponent(Component object) {
				return createComponentAdapter();
			}
			@Override
			public Adapter caseOperator(Operator object) {
				return createOperatorAdapter();
			}
			@Override
			public Adapter caseComNode(ComNode object) {
				return createComNodeAdapter();
			}
			@Override
			public Adapter caseEnabler(Enabler object) {
				return createEnablerAdapter();
			}
			@Override
			public Adapter caseDma(Dma object) {
				return createDmaAdapter();
			}
			@Override
			public Adapter caseMem(Mem object) {
				return createMemAdapter();
			}
			@Override
			public Adapter caseHierarchyPort(HierarchyPort object) {
				return createHierarchyPortAdapter();
			}
			@Override
			public Adapter caseComInterface(ComInterface object) {
				return createComInterfaceAdapter();
			}
			@Override
			public Adapter caseVLNVedElement(VLNVedElement object) {
				return createVLNVedElementAdapter();
			}
			@Override
			public Adapter caseParameterizedElement(ParameterizedElement object) {
				return createParameterizedElementAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.Component <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.Component
	 * @generated
	 */
	public Adapter createComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.Operator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.Operator
	 * @generated
	 */
	public Adapter createOperatorAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.ComNode <em>Com Node</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.ComNode
	 * @generated
	 */
	public Adapter createComNodeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.Enabler <em>Enabler</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.Enabler
	 * @generated
	 */
	public Adapter createEnablerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.Dma <em>Dma</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.Dma
	 * @generated
	 */
	public Adapter createDmaAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.Mem <em>Mem</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.Mem
	 * @generated
	 */
	public Adapter createMemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.HierarchyPort <em>Hierarchy Port</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.HierarchyPort
	 * @generated
	 */
	public Adapter createHierarchyPortAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.component.ComInterface <em>Com Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.component.ComInterface
	 * @generated
	 */
	public Adapter createComInterfaceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.VLNVedElement <em>VLN Ved Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.VLNVedElement
	 * @generated
	 */
	public Adapter createVLNVedElementAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.ietr.dftools.architecture.slam.ParameterizedElement <em>Parameterized Element</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.ietr.dftools.architecture.slam.ParameterizedElement
	 * @generated
	 */
	public Adapter createParameterizedElementAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //ComponentAdapterFactory
