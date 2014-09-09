/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fork Actor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ForkActorImpl extends ExecutableActorImpl implements ForkActor {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ForkActorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.FORK_ACTOR;
	}

	@Override
	public void accept(PiMMVisitor v) {
		v.visitForkActor(this);
	}
} //ForkActorImpl
