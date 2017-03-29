/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2013)
 * Romina Racca <romina.racca@gmail.com> (2013)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage
 * @generated
 */
public interface PiMMFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PiMMFactory eINSTANCE = org.ietr.preesm.experiment.model.pimm.impl.PiMMFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Pi Graph</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Pi Graph</em>'.
	 * @generated
	 */
	PiGraph createPiGraph();

	/**
	 * Returns a new object of class '<em>Actor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Actor</em>'.
	 * @generated
	 */
	Actor createActor();

	/**
	 * Returns a new object of class '<em>Data Input Port</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Input Port</em>'.
	 * @generated
	 */
	DataInputPort createDataInputPort();

	/**
	 * Returns a new object of class '<em>Data Output Port</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Output Port</em>'.
	 * @generated
	 */
	DataOutputPort createDataOutputPort();

	/**
	 * Returns a new object of class '<em>Config Input Port</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Config Input Port</em>'.
	 * @generated
	 */
	ConfigInputPort createConfigInputPort();

	/**
	 * Returns a new object of class '<em>Config Output Port</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Config Output Port</em>'.
	 * @generated
	 */
	ConfigOutputPort createConfigOutputPort();

	/**
	 * Returns a new object of class '<em>Fifo</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Fifo</em>'.
	 * @generated
	 */
	Fifo createFifo();

	/**
	 * Returns a new object of class '<em>Interface Actor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interface Actor</em>'.
	 * @generated
	 */
	InterfaceActor createInterfaceActor();

	/**
	 * Returns a new object of class '<em>Data Input Interface</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Input Interface</em>'.
	 * @generated
	 */
	DataInputInterface createDataInputInterface();

	/**
	 * Returns a new object of class '<em>Data Output Interface</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Output Interface</em>'.
	 * @generated
	 */
	DataOutputInterface createDataOutputInterface();

	/**
	 * Returns a new object of class '<em>Config Output Interface</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Config Output Interface</em>'.
	 * @generated
	 */
	ConfigOutputInterface createConfigOutputInterface();

	/**
	 * Returns a new object of class '<em>Refinement</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Refinement</em>'.
	 * @generated
	 */
	Refinement createRefinement();

	/**
	 * Returns a new object of class '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter</em>'.
	 * @generated
	 */
	Parameter createParameter();

	/**
	 * Returns a new object of class '<em>Dependency</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Dependency</em>'.
	 * @generated
	 */
	Dependency createDependency();

	/**
	 * Returns a new object of class '<em>Delay</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Delay</em>'.
	 * @generated
	 */
	Delay createDelay();

	/**
	 * Returns a new object of class '<em>Expression</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Expression</em>'.
	 * @generated
	 */
	Expression createExpression();

	/**
	 * Returns a new object of class '<em>HRefinement</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>HRefinement</em>'.
	 * @generated
	 */
	HRefinement createHRefinement();

	/**
	 * Returns a new object of class '<em>Function Prototype</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Function Prototype</em>'.
	 * @generated
	 */
	FunctionPrototype createFunctionPrototype();

	/**
	 * Returns a new object of class '<em>Function Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Function Parameter</em>'.
	 * @generated
	 */
	FunctionParameter createFunctionParameter();

	/**
	 * Returns a new object of class '<em>Broadcast Actor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Broadcast Actor</em>'.
	 * @generated
	 */
	BroadcastActor createBroadcastActor();

	/**
	 * Returns a new object of class '<em>Join Actor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Join Actor</em>'.
	 * @generated
	 */
	JoinActor createJoinActor();

	/**
	 * Returns a new object of class '<em>Fork Actor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Fork Actor</em>'.
	 * @generated
	 */
	ForkActor createForkActor();

	/**
	 * Returns a new object of class '<em>Round Buffer Actor</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Round Buffer Actor</em>'.
	 * @generated
	 */
	RoundBufferActor createRoundBufferActor();

	/**
	 * Returns an instance of data type '<em>Direction</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	Direction createDirection(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Direction</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertDirection(Direction instanceValue);

	/**
	 * Returns an instance of data type '<em>Port Memory Annotation</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	PortMemoryAnnotation createPortMemoryAnnotation(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>Port Memory Annotation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertPortMemoryAnnotation(PortMemoryAnnotation instanceValue);

	/**
	 * Returns an instance of data type '<em>IPath</em>' corresponding the given literal.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal a literal of the data type.
	 * @return a new instance value of the data type.
	 * @generated
	 */
	IPath createIPath(String literal);

	/**
	 * Returns a literal representation of an instance of data type '<em>IPath</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param instanceValue an instance value of the data type.
	 * @return a literal representation of the instance value.
	 * @generated
	 */
	String convertIPath(IPath instanceValue);

	/**
	 * Returns a new object of class '<em>Config Input Interface</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Config Input Interface</em>'.
	 * @generated
	 */
	ConfigInputInterface createConfigInputInterface();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	PiMMPackage getPiMMPackage();

} //PiMMFactory
