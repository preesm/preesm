/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the model object ' <em><b>Fifo Call</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getOperation <em>Operation</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead <em>Fifo Head</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoTail <em>Fifo Tail</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getHeadBuffer <em>Head Buffer</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getBodyBuffer <em>Body Buffer</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall()
 * @model
 * @generated
 */
public interface FifoCall extends Call {
  /**
   * Returns the value of the '<em><b>Operation</b></em>' attribute. The literals are from the enumeration
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Operation</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Operation</em>' attribute.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
   * @see #setOperation(FifoOperation)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_Operation()
   * @model required="true"
   * @generated
   */
  FifoOperation getOperation();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getOperation <em>Operation</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Operation</em>' attribute.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation
   * @see #getOperation()
   * @generated
   */
  void setOperation(FifoOperation value);

  /**
   * Returns the value of the '<em><b>Fifo Head</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Fifo Head</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Fifo Head</em>' reference.
   * @see #setFifoHead(FifoCall)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_FifoHead()
   * @model transient="true"
   * @generated
   */
  FifoCall getFifoHead();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoHead <em>Fifo Head</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Fifo Head</em>' reference.
   * @see #getFifoHead()
   * @generated
   */
  void setFifoHead(FifoCall value);

  /**
   * Returns the value of the '<em><b>Fifo Tail</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Fifo Tail</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Fifo Tail</em>' reference.
   * @see #setFifoTail(FifoCall)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_FifoTail()
   * @model transient="true"
   * @generated
   */
  FifoCall getFifoTail();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getFifoTail <em>Fifo Tail</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Fifo Tail</em>' reference.
   * @see #getFifoTail()
   * @generated
   */
  void setFifoTail(FifoCall value);

  /**
   * Returns the value of the '<em><b>Head Buffer</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Storage Buffer</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Head Buffer</em>' reference.
   * @see #setHeadBuffer(Buffer)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_HeadBuffer()
   * @model
   * @generated
   */
  Buffer getHeadBuffer();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getHeadBuffer <em>Head Buffer</em>}' reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Head Buffer</em>' reference.
   * @see #getHeadBuffer()
   * @generated
   */
  void setHeadBuffer(Buffer value);

  /**
   * Returns the value of the '<em><b>Body Buffer</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Body Buffer</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Body Buffer</em>' reference.
   * @see #setBodyBuffer(Buffer)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getFifoCall_BodyBuffer()
   * @model
   * @generated
   */
  Buffer getBodyBuffer();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.FifoCall#getBodyBuffer <em>Body Buffer</em>}' reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Body Buffer</em>' reference.
   * @see #getBodyBuffer()
   * @generated
   */
  void setBodyBuffer(Buffer value);

} // FifoCall
