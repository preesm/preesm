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
package org.ietr.preesm.codegen.model.codegen;

import org.eclipse.emf.common.util.EList;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A {@link Communication} is a {@link Call} used to represent the transmission of data between
 * processing elements. Each {@link Communication} {@link Call} has a {@link Direction} and a {@link Delimiter}.<!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getDirection <em>Direction</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getDelimiter <em>Delimiter</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getData <em>Data</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getSendStart <em>Send Start</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getSendEnd <em>Send End</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getReceiveStart <em>Receive Start</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getReceiveEnd <em>Receive End</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getId <em>Id</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getNodes <em>Nodes</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getReceiveRelease <em>Receive Release</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#getSendReserve <em>Send Reserve</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.Communication#isRedundant <em>Redundant</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication()
 * @model
 * @generated
 */
public interface Communication extends Call {
  /**
   * Returns the value of the '<em><b>Direction</b></em>' attribute. The literals are from the enumeration
   * {@link org.ietr.preesm.codegen.model.codegen.Direction}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Direction</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Direction</em>' attribute.
   * @see org.ietr.preesm.codegen.model.codegen.Direction
   * @see #setDirection(Direction)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_Direction()
   * @model required="true"
   * @generated
   */
  Direction getDirection();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getDirection
   * <em>Direction</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Direction</em>' attribute.
   * @see org.ietr.preesm.codegen.model.codegen.Direction
   * @see #getDirection()
   * @generated
   */
  void setDirection(Direction value);

  /**
   * Returns the value of the '<em><b>Delimiter</b></em>' attribute. The literals are from the enumeration
   * {@link org.ietr.preesm.codegen.model.codegen.Delimiter}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Delimiter</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Delimiter</em>' attribute.
   * @see org.ietr.preesm.codegen.model.codegen.Delimiter
   * @see #setDelimiter(Delimiter)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_Delimiter()
   * @model required="true"
   * @generated
   */
  Delimiter getDelimiter();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getDelimiter
   * <em>Delimiter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Delimiter</em>' attribute.
   * @see org.ietr.preesm.codegen.model.codegen.Delimiter
   * @see #getDelimiter()
   * @generated
   */
  void setDelimiter(Delimiter value);

  /**
   * Returns the value of the '<em><b>Data</b></em>' reference. <!-- begin-user-doc --> {@link Buffer} that is sent or
   * received by the current {@link Communication}. Calling this method is equivalent to calling
   * <code>thisCommunication.getParameters().iterator().next()</code> since the only "Parameter" of a communication is
   * the buffer it transfers. <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Data</em>' reference.
   * @see #setData(Buffer)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_Data()
   * @model required="true"
   * @generated
   */
  Buffer getData();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getData <em>Data</em>}'
   * reference. <!-- begin-user-doc -->Set the {@link #getData()} associated to the current {@link Communication}.
   * Calling this method will also modify the {@link Communication#getParameters() parameters}.<!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Data</em>' reference.
   * @see #getData()
   * @generated
   */
  void setData(Buffer value);

  /**
   * Returns the value of the '<em><b>Send Start</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Send Start</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Send Start</em>' reference.
   * @see #setSendStart(Communication)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_SendStart()
   * @model transient="true"
   * @generated
   */
  Communication getSendStart();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getSendStart <em>Send
   * Start</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Send Start</em>' reference.
   * @see #getSendStart()
   * @generated
   */
  void setSendStart(Communication value);

  /**
   * Returns the value of the '<em><b>Send End</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Send End</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Send End</em>' reference.
   * @see #setSendEnd(Communication)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_SendEnd()
   * @model transient="true"
   * @generated
   */
  Communication getSendEnd();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getSendEnd <em>Send
   * End</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Send End</em>' reference.
   * @see #getSendEnd()
   * @generated
   */
  void setSendEnd(Communication value);

  /**
   * Returns the value of the '<em><b>Receive Start</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Receive Start</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Receive Start</em>' reference.
   * @see #setReceiveStart(Communication)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_ReceiveStart()
   * @model transient="true"
   * @generated
   */
  Communication getReceiveStart();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getReceiveStart <em>Receive
   * Start</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Receive Start</em>' reference.
   * @see #getReceiveStart()
   * @generated
   */
  void setReceiveStart(Communication value);

  /**
   * Returns the value of the '<em><b>Receive End</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Receive End</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Receive End</em>' reference.
   * @see #setReceiveEnd(Communication)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_ReceiveEnd()
   * @model transient="true"
   * @generated
   */
  Communication getReceiveEnd();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getReceiveEnd <em>Receive
   * End</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Receive End</em>' reference.
   * @see #getReceiveEnd()
   * @generated
   */
  void setReceiveEnd(Communication value);

  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(int)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_Id()
   * @model required="true"
   * @generated
   */
  int getId();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getId <em>Id</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(int value);

  /**
   * Returns the value of the '<em><b>Nodes</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.codegen.model.codegen.CommunicationNode}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nodes</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Nodes</em>' containment reference list.
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_Nodes()
   * @model containment="true" required="true"
   * @generated
   */
  EList<CommunicationNode> getNodes();

  /**
   * Returns the value of the '<em><b>Receive Release</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Receive Release</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Receive Release</em>' reference.
   * @see #setReceiveRelease(Communication)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_ReceiveRelease()
   * @model transient="true"
   * @generated
   */
  Communication getReceiveRelease();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getReceiveRelease
   * <em>Receive Release</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Receive Release</em>' reference.
   * @see #getReceiveRelease()
   * @generated
   */
  void setReceiveRelease(Communication value);

  /**
   * Returns the value of the '<em><b>Send Reserve</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Send Reserve</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Send Reserve</em>' reference.
   * @see #setSendReserve(Communication)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_SendReserve()
   * @model transient="true"
   * @generated
   */
  Communication getSendReserve();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#getSendReserve <em>Send
   * Reserve</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Send Reserve</em>' reference.
   * @see #getSendReserve()
   * @generated
   */
  void setSendReserve(Communication value);

  /**
   * Returns the value of the '<em><b>Redundant</b></em>' attribute. The default value is <code>"false"</code>. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Redundant</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Redundant</em>' attribute.
   * @see #setRedundant(boolean)
   * @see org.ietr.preesm.codegen.model.codegen.CodegenPackage#getCommunication_Redundant()
   * @model default="false"
   * @generated
   */
  boolean isRedundant();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.model.codegen.Communication#isRedundant
   * <em>Redundant</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Redundant</em>' attribute.
   * @see #isRedundant()
   * @generated
   */
  void setRedundant(boolean value);

  /**
   * <!-- begin-user-doc --> This method browse the {@link #eContainer()} recursively of the {@link Communication} until
   * it finds a {@link CoreBlock}. <!-- end-user-doc -->
   * 
   * @model kind="operation" required="true"
   * @generated
   */
  CoreBlock getCoreContainer();

} // Communication
