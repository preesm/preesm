/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Broadcast Actor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class BroadcastActorImpl extends AbstractActorImpl implements BroadcastActor {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BroadcastActorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.BROADCAST_ACTOR;
	}
	
	@Override
	public void accept(PiMMVisitor v) {
		v.visitBroadcastActor(this);
	}

} //BroadcastActorImpl
