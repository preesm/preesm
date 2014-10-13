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
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Config Input Port</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl#getIncomingDependency <em>Incoming Dependency</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConfigInputPortImpl extends PortImpl implements ConfigInputPort {
	/**
	 * The cached value of the '{@link #getIncomingDependency() <em>Incoming Dependency</em>}' reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getIncomingDependency()
	 * @generated
	 * @ordered
	 */
	protected Dependency incomingDependency;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 */
	protected ConfigInputPortImpl() {
		super();
		kind = PiIdentifiers.CONFIGURATION_INPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.CONFIG_INPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Dependency getIncomingDependency() {
		if (incomingDependency != null && incomingDependency.eIsProxy()) {
			InternalEObject oldIncomingDependency = (InternalEObject)incomingDependency;
			incomingDependency = (Dependency)eResolveProxy(oldIncomingDependency);
			if (incomingDependency != oldIncomingDependency) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, oldIncomingDependency, incomingDependency));
			}
		}
		return incomingDependency;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Dependency basicGetIncomingDependency() {
		return incomingDependency;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIncomingDependency(
			Dependency newIncomingDependency, NotificationChain msgs) {
		Dependency oldIncomingDependency = incomingDependency;
		incomingDependency = newIncomingDependency;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, oldIncomingDependency, newIncomingDependency);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setIncomingDependency(Dependency newIncomingDependency) {
		if (newIncomingDependency != incomingDependency) {
			NotificationChain msgs = null;
			if (incomingDependency != null)
				msgs = ((InternalEObject)incomingDependency).eInverseRemove(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
			if (newIncomingDependency != null)
				msgs = ((InternalEObject)newIncomingDependency).eInverseAdd(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
			msgs = basicSetIncomingDependency(newIncomingDependency, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, newIncomingDependency, newIncomingDependency));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				if (incomingDependency != null)
					msgs = ((InternalEObject)incomingDependency).eInverseRemove(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
				return basicSetIncomingDependency((Dependency)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				return basicSetIncomingDependency(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				if (resolve) return getIncomingDependency();
				return basicGetIncomingDependency();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				setIncomingDependency((Dependency)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				setIncomingDependency((Dependency)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				return incomingDependency != null;
		}
		return super.eIsSet(featureID);
	}

	@Override
	public void accept(PiMMVisitor v) {
		v.visitConfigInputPort(this);
	}
	
	@Override
	public String getName() {
		if (this.name == null && this.incomingDependency.getSetter() instanceof Parameter)
			return ((Parameter) this.incomingDependency.getSetter()).getName();
		else return this.name;
	}

} // ConfigInputPortImpl
