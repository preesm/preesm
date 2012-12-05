/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Refinement;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Actor</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getRefinement <em>Refinement</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ActorImpl extends AbstractVertexImpl implements Actor {
	/**
	 * The cached value of the '{@link #getRefinement() <em>Refinement</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getRefinement()
	 * @generated
	 * @ordered
	 */
	protected Refinement refinement;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	protected ActorImpl() {
		super();
		this.setRefinement(PiMMFactory.eINSTANCE.createRefinement());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.ACTOR;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Refinement getRefinement() {
		return refinement;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRefinement(Refinement newRefinement,
			NotificationChain msgs) {
		Refinement oldRefinement = refinement;
		refinement = newRefinement;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, oldRefinement, newRefinement);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefinement(Refinement newRefinement) {
		if (newRefinement != refinement) {
			NotificationChain msgs = null;
			if (refinement != null)
				msgs = ((InternalEObject)refinement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
			if (newRefinement != null)
				msgs = ((InternalEObject)newRefinement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
			msgs = basicSetRefinement(newRefinement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, newRefinement, newRefinement));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.ACTOR__REFINEMENT:
				return basicSetRefinement(null, msgs);
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
			case PiMMPackage.ACTOR__REFINEMENT:
				return getRefinement();
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
			case PiMMPackage.ACTOR__REFINEMENT:
				setRefinement((Refinement)newValue);
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
			case PiMMPackage.ACTOR__REFINEMENT:
				setRefinement((Refinement)null);
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
			case PiMMPackage.ACTOR__REFINEMENT:
				return refinement != null;
		}
		return super.eIsSet(featureID);
	}

} // ActorImpl
