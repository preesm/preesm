/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2014)
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
package org.ietr.preesm.codegen.xtend.model.codegen;

import org.eclipse.emf.ecore.EFactory;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model. <!-- end-user-doc -->
 * 
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage
 * @generated
 */
public interface CodegenFactory extends EFactory {
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  CodegenFactory eINSTANCE = org.ietr.preesm.codegen.xtend.model.codegen.impl.CodegenFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Block</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Block</em>'.
   * @generated
   */
  Block createBlock();

  /**
   * Returns a new object of class '<em>Buffer</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Buffer</em>'.
   * @generated
   */
  Buffer createBuffer();

  /**
   * Returns a new object of class '<em>Sub Buffer</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Sub Buffer</em>'.
   * @generated
   */
  SubBuffer createSubBuffer();

  /**
   * Returns a new object of class '<em>Constant</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Constant</em>'.
   * @generated
   */
  Constant createConstant();

  /**
   * Returns a new object of class '<em>Function Call</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Function Call</em>'.
   * @generated
   */
  FunctionCall createFunctionCall();

  /**
   * Returns a new object of class '<em>Communication</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Communication</em>'.
   * @generated
   */
  Communication createCommunication();

  /**
   * Returns a new object of class '<em>Core Block</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Core Block</em>'.
   * @generated
   */
  CoreBlock createCoreBlock();

  /**
   * Returns a new object of class '<em>Actor Block</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Actor Block</em>'.
   * @generated
   */
  ActorBlock createActorBlock();

  /**
   * Returns a new object of class '<em>Loop Block</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Loop Block</em>'.
   * @generated
   */
  LoopBlock createLoopBlock();

  /**
   * Returns a new object of class '<em>Actor Call</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Actor Call</em>'.
   * @generated
   */
  ActorCall createActorCall();

  /**
   * Returns a new object of class '<em>Call Block</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Call Block</em>'.
   * @generated
   */
  CallBlock createCallBlock();

  /**
   * Returns a new object of class '<em>Special Call</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Special Call</em>'.
   * @generated
   */
  SpecialCall createSpecialCall();

  /**
   * Returns a new object of class '<em>Fifo Call</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Fifo Call</em>'.
   * @generated
   */
  FifoCall createFifoCall();

  /**
   * Returns a new object of class '<em>Communication Node</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Communication Node</em>'.
   * @generated
   */
  CommunicationNode createCommunicationNode();

  /**
   * Returns a new object of class '<em>Semaphore</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Semaphore</em>'.
   * @generated
   */
  Semaphore createSemaphore();

  /**
   * Returns a new object of class '<em>Shared Memory Communication</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Shared Memory Communication</em>'.
   * @generated
   */
  SharedMemoryCommunication createSharedMemoryCommunication();

  /**
   * Returns a new object of class '<em>Constant String</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Constant String</em>'.
   * @generated
   */
  ConstantString createConstantString();

  /**
   * Returns a new object of class '<em>Null Buffer</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Null Buffer</em>'.
   * @generated
   */
  NullBuffer createNullBuffer();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  CodegenPackage getCodegenPackage();

} // CodegenFactory
