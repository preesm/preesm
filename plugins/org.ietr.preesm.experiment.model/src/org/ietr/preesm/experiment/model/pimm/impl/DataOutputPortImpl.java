/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
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
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Output Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataOutputPortImpl#getOutgoingFifo <em>Outgoing Fifo</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataOutputPortImpl extends DataPortImpl implements DataOutputPort {
	/**
	 * The cached value of the '{@link #getOutgoingFifo() <em>Outgoing Fifo</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutgoingFifo()
	 * @generated
	 * @ordered
	 */
	protected Fifo outgoingFifo;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 */
	protected DataOutputPortImpl() {
		super();
		kind = PiIdentifiers.DATA_OUTPUT_PORT;

		this.setExpression(PiMMFactory.eINSTANCE.createExpression());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.DATA_OUTPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Fifo getOutgoingFifo() {
		if (outgoingFifo != null && outgoingFifo.eIsProxy()) {
			InternalEObject oldOutgoingFifo = (InternalEObject)outgoingFifo;
			outgoingFifo = (Fifo)eResolveProxy(oldOutgoingFifo);
			if (outgoingFifo != oldOutgoingFifo) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, oldOutgoingFifo, outgoingFifo));
			}
		}
		return outgoingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Fifo basicGetOutgoingFifo() {
		return outgoingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOutgoingFifo(Fifo newOutgoingFifo, NotificationChain msgs) {
		Fifo oldOutgoingFifo = outgoingFifo;
		outgoingFifo = newOutgoingFifo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, oldOutgoingFifo, newOutgoingFifo);
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
	public void setOutgoingFifo(Fifo newOutgoingFifo) {
		if (newOutgoingFifo != outgoingFifo) {
			NotificationChain msgs = null;
			if (outgoingFifo != null)
				msgs = ((InternalEObject)outgoingFifo).eInverseRemove(this, PiMMPackage.FIFO__SOURCE_PORT, Fifo.class, msgs);
			if (newOutgoingFifo != null)
				msgs = ((InternalEObject)newOutgoingFifo).eInverseAdd(this, PiMMPackage.FIFO__SOURCE_PORT, Fifo.class, msgs);
			msgs = basicSetOutgoingFifo(newOutgoingFifo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, newOutgoingFifo, newOutgoingFifo));
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO:
				if (outgoingFifo != null)
					msgs = ((InternalEObject)outgoingFifo).eInverseRemove(this, PiMMPackage.FIFO__SOURCE_PORT, Fifo.class, msgs);
				return basicSetOutgoingFifo((Fifo)otherEnd, msgs);
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
			case PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO:
				return basicSetOutgoingFifo(null, msgs);
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
			case PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO:
				if (resolve) return getOutgoingFifo();
				return basicGetOutgoingFifo();
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
			case PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO:
				setOutgoingFifo((Fifo)newValue);
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
			case PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO:
				setOutgoingFifo((Fifo)null);
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
			case PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO:
				return outgoingFifo != null;
		}
		return super.eIsSet(featureID);
	}

	@Override
	public void accept(PiMMVisitor v) {
		v.visitDataOutputPort(this);
	}
	
	@Override
	public String getName() {
		String name = super.getName();
		if (name == null && this.eContainer instanceof DataInputInterface) {
			name = ((DataInputInterface) this.eContainer).getName();
		}
		return name;
	}

} //OutputPortImpl
