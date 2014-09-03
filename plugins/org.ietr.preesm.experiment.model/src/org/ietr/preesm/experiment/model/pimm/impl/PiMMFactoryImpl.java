/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.ietr.preesm.experiment.model.pimm.*;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 * @generated
 */
public class PiMMFactoryImpl extends EFactoryImpl implements PiMMFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	public static PiMMFactory init() {
		try {
			PiMMFactory thePiMMFactory = (PiMMFactory)EPackage.Registry.INSTANCE.getEFactory(PiMMPackage.eNS_URI);
			if (thePiMMFactory != null) {
				return thePiMMFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new PiMMFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	public PiMMFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case PiMMPackage.PI_GRAPH: return createPiGraph();
			case PiMMPackage.ACTOR: return createActor();
			case PiMMPackage.DATA_INPUT_PORT: return createDataInputPort();
			case PiMMPackage.DATA_OUTPUT_PORT: return createDataOutputPort();
			case PiMMPackage.CONFIG_INPUT_PORT: return createConfigInputPort();
			case PiMMPackage.CONFIG_OUTPUT_PORT: return createConfigOutputPort();
			case PiMMPackage.FIFO: return createFifo();
			case PiMMPackage.INTERFACE_ACTOR: return createInterfaceActor();
			case PiMMPackage.DATA_INPUT_INTERFACE: return createDataInputInterface();
			case PiMMPackage.DATA_OUTPUT_INTERFACE: return createDataOutputInterface();
			case PiMMPackage.CONFIG_INPUT_INTERFACE: return createConfigInputInterface();
			case PiMMPackage.CONFIG_OUTPUT_INTERFACE: return createConfigOutputInterface();
			case PiMMPackage.REFINEMENT: return createRefinement();
			case PiMMPackage.PARAMETER: return createParameter();
			case PiMMPackage.DEPENDENCY: return createDependency();
			case PiMMPackage.DELAY: return createDelay();
			case PiMMPackage.EXPRESSION: return createExpression();
			case PiMMPackage.HREFINEMENT: return createHRefinement();
			case PiMMPackage.FUNCTION_PROTOTYPE: return createFunctionPrototype();
			case PiMMPackage.FUNCTION_PARAMETER: return createFunctionParameter();
			case PiMMPackage.BROADCAST_ACTOR: return createBroadcastActor();
			case PiMMPackage.JOIN_ACTOR: return createJoinActor();
			case PiMMPackage.FORK_ACTOR: return createForkActor();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case PiMMPackage.DIRECTION:
				return createDirectionFromString(eDataType, initialValue);
			case PiMMPackage.PORT_MEMORY_ANNOTATION:
				return createPortMemoryAnnotationFromString(eDataType, initialValue);
			case PiMMPackage.IPATH:
				return createIPathFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case PiMMPackage.DIRECTION:
				return convertDirectionToString(eDataType, instanceValue);
			case PiMMPackage.PORT_MEMORY_ANNOTATION:
				return convertPortMemoryAnnotationToString(eDataType, instanceValue);
			case PiMMPackage.IPATH:
				return convertIPathToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public PiGraph createPiGraph() {
		PiGraphImpl piGraph = new PiGraphImpl();
		return piGraph;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Actor createActor() {
		ActorImpl actor = new ActorImpl();
		return actor;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DataInputPort createDataInputPort() {
		DataInputPortImpl dataInputPort = new DataInputPortImpl();
		return dataInputPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DataOutputPort createDataOutputPort() {
		DataOutputPortImpl dataOutputPort = new DataOutputPortImpl();
		return dataOutputPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ConfigInputPort createConfigInputPort() {
		ConfigInputPortImpl configInputPort = new ConfigInputPortImpl();
		return configInputPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ConfigOutputPort createConfigOutputPort() {
		ConfigOutputPortImpl configOutputPort = new ConfigOutputPortImpl();
		return configOutputPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Fifo createFifo() {
		FifoImpl fifo = new FifoImpl();
		return fifo;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public InterfaceActor createInterfaceActor() {
		InterfaceActorImpl interfaceActor = new InterfaceActorImpl();
		return interfaceActor;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DataInputInterface createDataInputInterface() {
		DataInputInterfaceImpl dataInputInterface = new DataInputInterfaceImpl();
		return dataInputInterface;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DataOutputInterface createDataOutputInterface() {
		DataOutputInterfaceImpl dataOutputInterface = new DataOutputInterfaceImpl();
		return dataOutputInterface;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ConfigOutputInterface createConfigOutputInterface() {
		ConfigOutputInterfaceImpl configOutputInterface = new ConfigOutputInterfaceImpl();
		return configOutputInterface;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Refinement createRefinement() {
		RefinementImpl refinement = new RefinementImpl();
		return refinement;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Parameter createParameter() {
		ParameterImpl parameter = new ParameterImpl();
		// Set the expression to 1 to prevent from errors with division with
		// default expression value (0)
		parameter.getExpression().setString("1");
		return parameter;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Dependency createDependency() {
		DependencyImpl dependency = new DependencyImpl();
		return dependency;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Delay createDelay() {
		DelayImpl delay = new DelayImpl();
		return delay;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Expression createExpression() {
		ExpressionImpl expression = new ExpressionImpl();
		return expression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HRefinement createHRefinement() {
		HRefinementImpl hRefinement = new HRefinementImpl();
		return hRefinement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionPrototype createFunctionPrototype() {
		FunctionPrototypeImpl functionPrototype = new FunctionPrototypeImpl();
		return functionPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionParameter createFunctionParameter() {
		FunctionParameterImpl functionParameter = new FunctionParameterImpl();
		return functionParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BroadcastActor createBroadcastActor() {
		BroadcastActorImpl broadcastActor = new BroadcastActorImpl();
		return broadcastActor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public JoinActor createJoinActor() {
		JoinActorImpl joinActor = new JoinActorImpl();
		return joinActor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ForkActor createForkActor() {
		ForkActorImpl forkActor = new ForkActorImpl();
		return forkActor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Direction createDirection(String literal) {
		Direction result = Direction.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + PiMMPackage.Literals.DIRECTION.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Direction createDirectionFromString(EDataType eDataType, String initialValue) {
		return createDirection(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirection(Direction instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDirectionToString(EDataType eDataType, Object instanceValue) {
		return convertDirection((Direction)instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PortMemoryAnnotation createPortMemoryAnnotation(String literal) {
		PortMemoryAnnotation result = PortMemoryAnnotation.get(literal);
		if (result == null) throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + PiMMPackage.Literals.PORT_MEMORY_ANNOTATION.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PortMemoryAnnotation createPortMemoryAnnotationFromString(EDataType eDataType, String initialValue) {
		return createPortMemoryAnnotation(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPortMemoryAnnotation(PortMemoryAnnotation instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPortMemoryAnnotationToString(EDataType eDataType, Object instanceValue) {
		return convertPortMemoryAnnotation((PortMemoryAnnotation)instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPath createIPath(String literal) {
		return (IPath)super.createFromString(PiMMPackage.Literals.IPATH, literal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPath createIPathFromString(EDataType eDataType, String initialValue) {
		return createIPath(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIPath(IPath instanceValue) {
		return super.convertToString(PiMMPackage.Literals.IPATH, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIPathToString(EDataType eDataType, Object instanceValue) {
		return convertIPath((IPath)instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ConfigInputInterface createConfigInputInterface() {
		ConfigInputInterfaceImpl configInputInterface = new ConfigInputInterfaceImpl();
		return configInputInterface;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public PiMMPackage getPiMMPackage() {
		return (PiMMPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static PiMMPackage getPackage() {
		return PiMMPackage.eINSTANCE;
	}

} // PiMMFactoryImpl
