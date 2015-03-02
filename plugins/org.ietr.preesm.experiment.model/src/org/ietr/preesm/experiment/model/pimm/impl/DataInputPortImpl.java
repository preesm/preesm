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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Input Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataInputPortImpl#getIncomingFifo <em>Incoming Fifo</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataInputPortImpl extends DataPortImpl implements DataInputPort {
	/**
	 * The cached value of the '{@link #getIncomingFifo() <em>Incoming Fifo</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIncomingFifo()
	 * @generated
	 * @ordered
	 */
	protected Fifo incomingFifo;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 */
	protected DataInputPortImpl() {
		super();
		kind = PiIdentifiers.DATA_INPUT_PORT;
		
		this.setExpression(PiMMFactory.eINSTANCE.createExpression());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.DATA_INPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Fifo getIncomingFifo() {
		if (incomingFifo != null && incomingFifo.eIsProxy()) {
			InternalEObject oldIncomingFifo = (InternalEObject)incomingFifo;
			incomingFifo = (Fifo)eResolveProxy(oldIncomingFifo);
			if (incomingFifo != oldIncomingFifo) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, oldIncomingFifo, incomingFifo));
			}
		}
		return incomingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Fifo basicGetIncomingFifo() {
		return incomingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIncomingFifo(Fifo newIncomingFifo, NotificationChain msgs) {
		Fifo oldIncomingFifo = incomingFifo;
		incomingFifo = newIncomingFifo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, oldIncomingFifo, newIncomingFifo);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIncomingFifo(Fifo newIncomingFifo) {
		if (newIncomingFifo != incomingFifo) {
			NotificationChain msgs = null;
			if (incomingFifo != null)
				msgs = ((InternalEObject)incomingFifo).eInverseRemove(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
			if (newIncomingFifo != null)
				msgs = ((InternalEObject)newIncomingFifo).eInverseAdd(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
			msgs = basicSetIncomingFifo(newIncomingFifo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, newIncomingFifo, newIncomingFifo));
	}

/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
				if (incomingFifo != null)
					msgs = ((InternalEObject)incomingFifo).eInverseRemove(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
				return basicSetIncomingFifo((Fifo)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
				return basicSetIncomingFifo(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
				if (resolve) return getIncomingFifo();
				return basicGetIncomingFifo();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
				setIncomingFifo((Fifo)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
				setIncomingFifo((Fifo)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
				return incomingFifo != null;
		}
		return super.eIsSet(featureID);
	}

	@Override
	public void accept(PiMMVisitor v) {
		v.visitDataInputPort(this);
	}
	
	@Override
	public String getName() {
		String name = super.getName();
		if (name == null && this.eContainer instanceof DataOutputInterface) {
			name = ((DataOutputInterface) this.eContainer).getName();
		}
		return name;
	}

} //InputPortImpl
