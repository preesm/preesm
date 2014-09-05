/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Round Buffer Actor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class RoundBufferActorImpl extends AbstractActorImpl implements RoundBufferActor {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RoundBufferActorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.ROUND_BUFFER_ACTOR;
	}
	
	@Override
	public void accept(PiMMVisitor v) {
		v.visitRoundBufferActor(this);
	}

} //RoundBufferActorImpl
