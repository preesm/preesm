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
 * A representation of the model object '<em><b>Dependency</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Dependency#getSetter <em>Setter</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDependency()
 * @model
 * @generated
 */
public interface Dependency extends EObject, PiMMVisitable {
	/**
	 * Returns the value of the '<em><b>Setter</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.ISetter#getOutgoingDependencies <em>Outgoing Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Setter</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Setter</em>' reference.
	 * @see #setSetter(ISetter)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDependency_Setter()
	 * @see org.ietr.preesm.experiment.model.pimm.ISetter#getOutgoingDependencies
	 * @model opposite="outgoingDependencies" required="true"
	 * @generated
	 */
	ISetter getSetter();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getSetter <em>Setter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Setter</em>' reference.
	 * @see #getSetter()
	 * @generated
	 */
	void setSetter(ISetter value);

	/**
	 * Returns the value of the '<em><b>Getter</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Getter</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Getter</em>' reference.
	 * @see #setGetter(ConfigInputPort)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDependency_Getter()
	 * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency
	 * @model opposite="incomingDependency" required="true"
	 * @generated
	 */
	ConfigInputPort getGetter();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Getter</em>' reference.
	 * @see #getGetter()
	 * @generated
	 */
	void setGetter(ConfigInputPort value);

} // Dependency
