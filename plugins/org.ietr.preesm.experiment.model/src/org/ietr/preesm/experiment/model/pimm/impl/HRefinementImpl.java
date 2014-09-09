/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>HRefinement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl#getLoopPrototype <em>Loop Prototype</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl#getInitPrototype <em>Init Prototype</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HRefinementImpl extends RefinementImpl implements HRefinement {
	/**
	 * The cached value of the '{@link #getLoopPrototype() <em>Loop Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLoopPrototype()
	 * @generated
	 * @ordered
	 */
	protected FunctionPrototype loopPrototype;
	/**
	 * The cached value of the '{@link #getInitPrototype() <em>Init Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitPrototype()
	 * @generated
	 * @ordered
	 */
	protected FunctionPrototype initPrototype;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HRefinementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.HREFINEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionPrototype getLoopPrototype() {
		if (loopPrototype != null && loopPrototype.eIsProxy()) {
			InternalEObject oldLoopPrototype = (InternalEObject)loopPrototype;
			loopPrototype = (FunctionPrototype)eResolveProxy(oldLoopPrototype);
			if (loopPrototype != oldLoopPrototype) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype, loopPrototype));
			}
		}
		return loopPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionPrototype basicGetLoopPrototype() {
		return loopPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLoopPrototype(FunctionPrototype newLoopPrototype) {
		FunctionPrototype oldLoopPrototype = loopPrototype;
		loopPrototype = newLoopPrototype;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype, loopPrototype));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionPrototype getInitPrototype() {
		if (initPrototype != null && initPrototype.eIsProxy()) {
			InternalEObject oldInitPrototype = (InternalEObject)initPrototype;
			initPrototype = (FunctionPrototype)eResolveProxy(oldInitPrototype);
			if (initPrototype != oldInitPrototype) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.HREFINEMENT__INIT_PROTOTYPE, oldInitPrototype, initPrototype));
			}
		}
		return initPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FunctionPrototype basicGetInitPrototype() {
		return initPrototype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInitPrototype(FunctionPrototype newInitPrototype) {
		FunctionPrototype oldInitPrototype = initPrototype;
		initPrototype = newInitPrototype;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.HREFINEMENT__INIT_PROTOTYPE, oldInitPrototype, initPrototype));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				if (resolve) return getLoopPrototype();
				return basicGetLoopPrototype();
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				if (resolve) return getInitPrototype();
				return basicGetInitPrototype();
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
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				setLoopPrototype((FunctionPrototype)newValue);
				return;
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				setInitPrototype((FunctionPrototype)newValue);
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
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				setLoopPrototype((FunctionPrototype)null);
				return;
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				setInitPrototype((FunctionPrototype)null);
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
			case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
				return loopPrototype != null;
			case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
				return initPrototype != null;
		}
		return super.eIsSet(featureID);
	}
	
	@Override
	public void accept(PiMMVisitor v) {
		v.visitHRefinement(this);
	}

} //HRefinementImpl
