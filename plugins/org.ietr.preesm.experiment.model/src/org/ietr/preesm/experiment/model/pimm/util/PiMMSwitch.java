/**
 */
package org.ietr.preesm.experiment.model.pimm.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.ietr.preesm.experiment.model.pimm.*;

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
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage
 * @generated
 */
public class PiMMSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static PiMMPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PiMMSwitch() {
		if (modelPackage == null) {
			modelPackage = PiMMPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @parameter ePackage the package in question.
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
			case PiMMPackage.PARAMETERIZABLE: {
				Parameterizable parameterizable = (Parameterizable)theEObject;
				T result = caseParameterizable(parameterizable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.ABSTRACT_VERTEX: {
				AbstractVertex abstractVertex = (AbstractVertex)theEObject;
				T result = caseAbstractVertex(abstractVertex);
				if (result == null) result = caseParameterizable(abstractVertex);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.ABSTRACT_ACTOR: {
				AbstractActor abstractActor = (AbstractActor)theEObject;
				T result = caseAbstractActor(abstractActor);
				if (result == null) result = caseAbstractVertex(abstractActor);
				if (result == null) result = caseParameterizable(abstractActor);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.PI_GRAPH: {
				PiGraph piGraph = (PiGraph)theEObject;
				T result = casePiGraph(piGraph);
				if (result == null) result = caseAbstractActor(piGraph);
				if (result == null) result = caseAbstractVertex(piGraph);
				if (result == null) result = caseParameterizable(piGraph);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.ACTOR: {
				Actor actor = (Actor)theEObject;
				T result = caseActor(actor);
				if (result == null) result = caseAbstractActor(actor);
				if (result == null) result = caseAbstractVertex(actor);
				if (result == null) result = caseParameterizable(actor);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.PORT: {
				Port port = (Port)theEObject;
				T result = casePort(port);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.DATA_INPUT_PORT: {
				DataInputPort dataInputPort = (DataInputPort)theEObject;
				T result = caseDataInputPort(dataInputPort);
				if (result == null) result = casePort(dataInputPort);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.DATA_OUTPUT_PORT: {
				DataOutputPort dataOutputPort = (DataOutputPort)theEObject;
				T result = caseDataOutputPort(dataOutputPort);
				if (result == null) result = casePort(dataOutputPort);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.CONFIG_INPUT_PORT: {
				ConfigInputPort configInputPort = (ConfigInputPort)theEObject;
				T result = caseConfigInputPort(configInputPort);
				if (result == null) result = casePort(configInputPort);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.CONFIG_OUTPUT_PORT: {
				ConfigOutputPort configOutputPort = (ConfigOutputPort)theEObject;
				T result = caseConfigOutputPort(configOutputPort);
				if (result == null) result = caseDataOutputPort(configOutputPort);
				if (result == null) result = caseISetter(configOutputPort);
				if (result == null) result = casePort(configOutputPort);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.FIFO: {
				Fifo fifo = (Fifo)theEObject;
				T result = caseFifo(fifo);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.INTERFACE_ACTOR: {
				InterfaceActor interfaceActor = (InterfaceActor)theEObject;
				T result = caseInterfaceActor(interfaceActor);
				if (result == null) result = caseAbstractActor(interfaceActor);
				if (result == null) result = caseAbstractVertex(interfaceActor);
				if (result == null) result = caseParameterizable(interfaceActor);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.DATA_INPUT_INTERFACE: {
				DataInputInterface dataInputInterface = (DataInputInterface)theEObject;
				T result = caseDataInputInterface(dataInputInterface);
				if (result == null) result = caseInterfaceActor(dataInputInterface);
				if (result == null) result = caseAbstractActor(dataInputInterface);
				if (result == null) result = caseAbstractVertex(dataInputInterface);
				if (result == null) result = caseParameterizable(dataInputInterface);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.DATA_OUTPUT_INTERFACE: {
				DataOutputInterface dataOutputInterface = (DataOutputInterface)theEObject;
				T result = caseDataOutputInterface(dataOutputInterface);
				if (result == null) result = caseInterfaceActor(dataOutputInterface);
				if (result == null) result = caseAbstractActor(dataOutputInterface);
				if (result == null) result = caseAbstractVertex(dataOutputInterface);
				if (result == null) result = caseParameterizable(dataOutputInterface);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.CONFIG_INPUT_INTERFACE: {
				ConfigInputInterface configInputInterface = (ConfigInputInterface)theEObject;
				T result = caseConfigInputInterface(configInputInterface);
				if (result == null) result = caseParameter(configInputInterface);
				if (result == null) result = caseAbstractVertex(configInputInterface);
				if (result == null) result = caseISetter(configInputInterface);
				if (result == null) result = caseParameterizable(configInputInterface);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.CONFIG_OUTPUT_INTERFACE: {
				ConfigOutputInterface configOutputInterface = (ConfigOutputInterface)theEObject;
				T result = caseConfigOutputInterface(configOutputInterface);
				if (result == null) result = caseInterfaceActor(configOutputInterface);
				if (result == null) result = caseAbstractActor(configOutputInterface);
				if (result == null) result = caseAbstractVertex(configOutputInterface);
				if (result == null) result = caseParameterizable(configOutputInterface);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.REFINEMENT: {
				Refinement refinement = (Refinement)theEObject;
				T result = caseRefinement(refinement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.PARAMETER: {
				Parameter parameter = (Parameter)theEObject;
				T result = caseParameter(parameter);
				if (result == null) result = caseAbstractVertex(parameter);
				if (result == null) result = caseISetter(parameter);
				if (result == null) result = caseParameterizable(parameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.DEPENDENCY: {
				Dependency dependency = (Dependency)theEObject;
				T result = caseDependency(dependency);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.ISETTER: {
				ISetter iSetter = (ISetter)theEObject;
				T result = caseISetter(iSetter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.DELAY: {
				Delay delay = (Delay)theEObject;
				T result = caseDelay(delay);
				if (result == null) result = caseParameterizable(delay);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case PiMMPackage.EXPRESSION: {
				Expression expression = (Expression)theEObject;
				T result = caseExpression(expression);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameterizable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameterizable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterizable(Parameterizable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Vertex</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Vertex</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractVertex(AbstractVertex object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Actor</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Actor</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractActor(AbstractActor object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Pi Graph</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Pi Graph</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePiGraph(PiGraph object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Actor</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Actor</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActor(Actor object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Port</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Port</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePort(Port object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Input Port</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Input Port</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataInputPort(DataInputPort object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Output Port</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Output Port</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataOutputPort(DataOutputPort object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Config Input Port</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Config Input Port</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConfigInputPort(ConfigInputPort object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Config Output Port</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Config Output Port</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConfigOutputPort(ConfigOutputPort object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Fifo</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Fifo</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFifo(Fifo object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Interface Actor</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Interface Actor</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInterfaceActor(InterfaceActor object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Input Interface</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Input Interface</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataInputInterface(DataInputInterface object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Output Interface</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Output Interface</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataOutputInterface(DataOutputInterface object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Config Output Interface</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Config Output Interface</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConfigOutputInterface(ConfigOutputInterface object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Refinement</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Refinement</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRefinement(Refinement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameter(Parameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Dependency</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dependency</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDependency(Dependency object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>ISetter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>ISetter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseISetter(ISetter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Delay</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Delay</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDelay(Delay object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Expression</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Expression</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExpression(Expression object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Config Input Interface</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Config Input Interface</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConfigInputInterface(ConfigInputInterface object) {
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

} //PiMMSwitch
