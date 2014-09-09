/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Executable Actor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public abstract class ExecutableActorImpl extends AbstractActorImpl implements ExecutableActor {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ExecutableActorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.EXECUTABLE_ACTOR;
	}
	
	@Override
	public void accept(PiMMVisitor v) {
		v.visitExecutableActor(this);
	}

} //ExecutableActorImpl
