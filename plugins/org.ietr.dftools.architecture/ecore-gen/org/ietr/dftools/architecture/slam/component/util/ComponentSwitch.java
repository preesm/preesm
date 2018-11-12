/**
 */
package org.ietr.dftools.architecture.slam.component.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.ietr.dftools.architecture.slam.ParameterizedElement;
import org.ietr.dftools.architecture.slam.VLNVedElement;

import org.ietr.dftools.architecture.slam.component.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage
 * @generated
 */
public class ComponentSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ComponentPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentSwitch() {
		if (modelPackage == null) {
			modelPackage = ComponentPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case ComponentPackage.COMPONENT: {
				Component component = (Component)theEObject;
				T result = caseComponent(component);
				if (result == null) result = caseVLNVedElement(component);
				if (result == null) result = caseParameterizedElement(component);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ComponentPackage.OPERATOR: {
				Operator operator = (Operator)theEObject;
				T result = caseOperator(operator);
				if (result == null) result = caseComponent(operator);
				if (result == null) result = caseVLNVedElement(operator);
				if (result == null) result = caseParameterizedElement(operator);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ComponentPackage.COM_NODE: {
				ComNode comNode = (ComNode)theEObject;
				T result = caseComNode(comNode);
				if (result == null) result = caseComponent(comNode);
				if (result == null) result = caseVLNVedElement(comNode);
				if (result == null) result = caseParameterizedElement(comNode);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ComponentPackage.ENABLER: {
				Enabler enabler = (Enabler)theEObject;
				T result = caseEnabler(enabler);
				if (result == null) result = caseComponent(enabler);
				if (result == null) result = caseVLNVedElement(enabler);
				if (result == null) result = caseParameterizedElement(enabler);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ComponentPackage.DMA: {
				Dma dma = (Dma)theEObject;
				T result = caseDma(dma);
				if (result == null) result = caseEnabler(dma);
				if (result == null) result = caseComponent(dma);
				if (result == null) result = caseVLNVedElement(dma);
				if (result == null) result = caseParameterizedElement(dma);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ComponentPackage.MEM: {
				Mem mem = (Mem)theEObject;
				T result = caseMem(mem);
				if (result == null) result = caseEnabler(mem);
				if (result == null) result = caseComponent(mem);
				if (result == null) result = caseVLNVedElement(mem);
				if (result == null) result = caseParameterizedElement(mem);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ComponentPackage.HIERARCHY_PORT: {
				HierarchyPort hierarchyPort = (HierarchyPort)theEObject;
				T result = caseHierarchyPort(hierarchyPort);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ComponentPackage.COM_INTERFACE: {
				ComInterface comInterface = (ComInterface)theEObject;
				T result = caseComInterface(comInterface);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Component</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Component</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComponent(Component object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Operator</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Operator</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOperator(Operator object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Com Node</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Com Node</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComNode(ComNode object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Enabler</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Enabler</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEnabler(Enabler object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Dma</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dma</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDma(Dma object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mem</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mem</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMem(Mem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Hierarchy Port</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Hierarchy Port</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseHierarchyPort(HierarchyPort object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Com Interface</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Com Interface</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComInterface(ComInterface object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>VLN Ved Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>VLN Ved Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVLNVedElement(VLNVedElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameterized Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameterized Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterizedElement(ParameterizedElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //ComponentSwitch
