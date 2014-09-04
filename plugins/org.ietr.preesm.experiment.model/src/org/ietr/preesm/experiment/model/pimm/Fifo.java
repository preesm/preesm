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
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fifo</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getId <em>Id</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo()
 * @model
 * @generated
 */
public interface Fifo extends EObject, PiMMVisitable {
	/**
	 * Returns the value of the '<em><b>Source Port</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source Port</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source Port</em>' reference.
	 * @see #setSourcePort(DataOutputPort)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_SourcePort()
	 * @see org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo
	 * @model opposite="outgoingFifo" required="true"
	 * @generated
	 */
	DataOutputPort getSourcePort();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source Port</em>' reference.
	 * @see #getSourcePort()
	 * @generated
	 */
	void setSourcePort(DataOutputPort value);

	/**
	 * Returns the value of the '<em><b>Target Port</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo <em>Incoming Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target Port</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Port</em>' reference.
	 * @see #setTargetPort(DataInputPort)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_TargetPort()
	 * @see org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo
	 * @model opposite="incomingFifo" required="true"
	 * @generated
	 */
	DataInputPort getTargetPort();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Port</em>' reference.
	 * @see #getTargetPort()
	 * @generated
	 */
	void setTargetPort(DataInputPort value);

	/**
	 * Returns the value of the '<em><b>Delay</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Delay</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Delay</em>' containment reference.
	 * @see #setDelay(Delay)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_Delay()
	 * @model containment="true"
	 * @generated
	 */
	Delay getDelay();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Delay</em>' containment reference.
	 * @see #getDelay()
	 * @generated
	 */
	void setDelay(Delay value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #isSetId()
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_Id()
	 * @model unsettable="true" required="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	String getId();



	/**
	 * Returns whether the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getId <em>Id</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Id</em>' attribute is set.
	 * @see #getId()
	 * @generated
	 */
	boolean isSetId();

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The default value is <code>"char"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_Type()
	 * @model default="char" required="true"
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

} // Fifo
