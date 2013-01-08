/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
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
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#isConfigurationActor <em>Configuration Actor</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ActorImpl extends AbstractActorImpl implements Actor {
	/**
	 * The cached value of the '{@link #getRefinement() <em>Refinement</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getRefinement()
	 * @generated
	 * @ordered
	 */
	protected Refinement refinement;

	/**
	 * The default value of the '{@link #isConfigurationActor() <em>Configuration Actor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfigurationActor()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CONFIGURATION_ACTOR_EDEFAULT = false;

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
	 * <!-- begin-user-doc --> Check whether the {@link Actor} is a
	 * configuration {@link Actor}.<br>
	 * <br>
	 * An {@link Actor} is a configuration {@link Actor} if it sets a
	 * {@link Parameter} value through a {@link ConfigOutputPort}.
	 * 
	 * @return <code>true</code> if the {@link Actor} is a configuration
	 *         {@link Actor} <code>false</code> else.<!-- end-user-doc -->
	 * 
	 */
	public boolean isConfigurationActor() {
		boolean result = false;

		List<ConfigOutputPort> ports = this.getConfigOutputPorts();
		for (ConfigOutputPort port : ports) {
			// If the port has an outgoing dependency
			if (!port.getOutgoingDependencies().isEmpty()) {
				// Since its a port, only one dependency is allowed
				Dependency dependency = port.getOutgoingDependencies().get(0);
				Parameterizable parameterizable = (Parameterizable) dependency
						.getGetter().eContainer();

				// Should always be the case
				if (parameterizable instanceof Parameter) {
					result = true;
				} else {
					throw new RuntimeException(
							"Actor configuration output ports can"
									+ " only set the value of a Parameter. "
									+ parameterizable.eClass()
									+ " cannot be set directly.");
				}
			}
		}

		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetConfigurationActor() {
		// TODO: implement this method to return whether the 'Configuration Actor' attribute is set
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
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
			case PiMMPackage.ACTOR__CONFIGURATION_ACTOR:
				return isConfigurationActor();
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
			case PiMMPackage.ACTOR__CONFIGURATION_ACTOR:
				return isSetConfigurationActor();
		}
		return super.eIsSet(featureID);
	}

} // ActorImpl
